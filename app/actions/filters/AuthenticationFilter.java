package actions.filters;

import com.typesafe.config.Config;
import entity.result.Results;
import jakarta.inject.Inject;
import org.apache.pekko.stream.Materializer;
import play.Logger;
import play.http.HttpFilters;
import play.libs.Json;
import play.mvc.EssentialFilter;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class AuthenticationFilter implements HttpFilters {

    private static final play.Logger.ALogger logger = Logger.of(AuthenticationFilter.class);
    private final List<EssentialFilter> filters = new ArrayList<>();

    @Inject
    public AuthenticationFilter(Materializer mat, Config config) {
        // 添加 CORS 过滤器（第一个执行）
        filters.add(new CORSFilter(mat));
        // 添加认证过滤器
        filters.add(new AuthFilter(mat, config));
        // 添加请求日志过滤器
        filters.add(new RequestLoggingFilter(mat));
    }

    @Override
    public List<EssentialFilter> getFilters() {
        return filters;
    }

    /**
     * 自定义 CORS 过滤器 - 直接添加 CORS 响应头
     */
    public static class CORSFilter extends Filter {
        public CORSFilter(Materializer mat) {
            super(mat);
        }

        @Override
        public CompletionStage<Result> apply(
                Function<Http.RequestHeader, CompletionStage<Result>> next,
                Http.RequestHeader requestHeader) {

            // 处理 OPTIONS 预检请求
            if ("OPTIONS".equalsIgnoreCase(requestHeader.method())) {
                logger.debug("✓ CORS OPTIONS 预检请求: {}", requestHeader.uri());

                Result result = play.mvc.Results.ok()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH")
                        .withHeader("Access-Control-Allow-Headers", "*")
                        .withHeader("Access-Control-Max-Age", "3600")
                        .withHeader("Access-Control-Allow-Credentials", "true");

                return CompletableFuture.completedFuture(result);
            }

            // 处理普通请求 - 添加 CORS 响应头
            return next.apply(requestHeader).thenApply(result -> {
                return result
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH")
                        .withHeader("Access-Control-Allow-Headers", "*")
                        .withHeader("Access-Control-Max-Age", "3600")
                        .withHeader("Access-Control-Allow-Credentials", "true");
            });
        }
    }

    /**
     * 内部类，实现具体的认证逻辑
     */
    public static class AuthFilter extends Filter {
        private final List<String> excludePaths;
        private final String filterPathStatus;

        public AuthFilter(Materializer mat, Config config) {
            super(mat);
            this.excludePaths = config.getStringList("auth.exclude.paths");
            this.filterPathStatus = config.getString("auth.exclude.filterPathStatus");
        }

        @Override
        public CompletionStage<Result> apply(
                Function<Http.RequestHeader, CompletionStage<Result>> next,
                Http.RequestHeader requestHeader) {

            // 1. 放行 OPTIONS 预检请求（应该已经被 CORSFilter 处理）
            if ("OPTIONS".equalsIgnoreCase(requestHeader.method())) {
                return next.apply(requestHeader);
            }

            // 2. 是否放开全部接口用于调试
            if ("true".equals(filterPathStatus)) {
                logger.debug("✓ 调试模式 - 放行所有请求: {}", requestHeader.uri());
                return next.apply(requestHeader);
            }

            // 3. 检查是否是排除路径
            String path = requestHeader.path();
            for (String excludePath : excludePaths) {
                if (path.equals(excludePath) ||
                        (excludePath.endsWith("/") && path.startsWith(excludePath)) ||
                        (excludePath.endsWith("/**") && path.startsWith(excludePath.substring(0, excludePath.length() - 2)))) {
                    logger.debug("✓ 排除路径 - 放行: {}", path);
                    return next.apply(requestHeader);
                }
            }

            // 4. 检查会话中的用户信息
            String username = requestHeader.session().get("username").orElse(null);
            String loggedIn = requestHeader.session().get("loggedIn").orElse(null);

            if (username != null && "true".equals(loggedIn)) {
                logger.debug("✓ 认证通过 - 用户: {}", username);
                return next.apply(requestHeader);
            } else {
                logger.warn("✗ 认证失败 - 未登录: {}", path);
                return CompletableFuture.completedFuture(
                        play.mvc.Results.unauthorized(Json.toJson(Results.error("重新登陆!")))
                );
            }
        }
    }

    /**
     * 请求日志过滤器
     */
    public static class RequestLoggingFilter extends Filter {
        public RequestLoggingFilter(Materializer mat) {
            super(mat);
        }

        @Override
        public CompletionStage<Result> apply(
                Function<Http.RequestHeader, CompletionStage<Result>> next,
                Http.RequestHeader requestHeader) {
            long startTime = System.currentTimeMillis();
            logger.info("{} {} from {}",
                    requestHeader.method(),
                    requestHeader.uri(),
                    requestHeader.remoteAddress());

            return next.apply(requestHeader).thenApply(result -> {
                long duration = System.currentTimeMillis() - startTime;
                String controllerInfo = getControllerInfo(requestHeader);
                logger.info("METHOD={} PATH={} STATUS={} DURATION={}ms IP={} CONTROLLER={}",
                        requestHeader.method(),
                        requestHeader.uri(),
                        result.status(),
                        duration,
                        requestHeader.remoteAddress(),
                        controllerInfo
                );
                return result;
            });
        }
    }

    private static String getControllerInfo(Http.RequestHeader requestHeader) {
        try {
            if (requestHeader.attrs().containsKey(play.routing.Router.Attrs.HANDLER_DEF)) {
                play.routing.HandlerDef handlerDef = requestHeader.attrs().get(play.routing.Router.Attrs.HANDLER_DEF);
                return handlerDef.controller() + "." + handlerDef.method();
            } else {
                return "Unknown (No HandlerDef)";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }
}