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
        // 添加认证过滤器到过滤器列表中
        filters.add(new AuthFilter(mat, config));
        // 添加请求日志过滤器
        filters.add(new RequestLoggingFilter(mat));
    }

    @Override
    public List<EssentialFilter> getFilters() {
        return filters;
    }

    /**
     * 内部类，实现具体的认证逻辑
     */
    public static class AuthFilter extends Filter {
        private final List<String> excludePaths;
        private final String filterPathStatus;

        public AuthFilter(Materializer mat, Config config) {
            super(mat);
            // 从配置文件中读取排除路径列表
            this.excludePaths = config.getStringList("auth.exclude.paths");
            this.filterPathStatus = config.getString("auth.exclude.filterPathStatus");
        }

        @Override
        public CompletionStage<Result> apply(
                Function<Http.RequestHeader, CompletionStage<Result>> next,
                Http.RequestHeader requestHeader) {
            //是否放开全部接口用于调试
            if (filterPathStatus.equals("true")) {
                return next.apply(requestHeader);
            }
            // 检查是否是排除路径
            String path = requestHeader.path();
            for (String excludePath : excludePaths) {
                if (path.equals(excludePath) ||
                        (excludePath.endsWith("/") && path.startsWith(excludePath)) ||
                        (excludePath.endsWith("/**") && path.startsWith(excludePath.substring(0, excludePath.length() - 2)))) {
                    return next.apply(requestHeader);
                }
            }
            // 检查会话中的用户信息
            String username = requestHeader.session().get("username").orElse(null);
            String loggedIn = requestHeader.session().get("loggedIn").orElse(null);
            if (username != null && "true".equals(loggedIn)) {
                // 用户已登录，允许访问
                return next.apply(requestHeader);
            } else {
                // 用户未登录，返回未授权
                return CompletableFuture.completedFuture(
                        play.mvc.Results.unauthorized(Json.toJson(Results.error("重新登陆!")))
                );
            }
        }
    }

    /**
     * 请求日志过滤器，记录所有请求信息
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
            // 记录请求开始
            logger.info("{} {} from {}",
                    requestHeader.method(),
                    requestHeader.uri(),
                    requestHeader.remoteAddress());
            // 继续处理请求
            return next.apply(requestHeader).thenApply(result -> {
                long duration = System.currentTimeMillis() - startTime;
                // 获取处理请求的控制器信息
                String controllerInfo = getControllerInfo(requestHeader);
                // 记录响应信息
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

    /**
     * 获取控制器信息，返回 "ControllerClass.actionMethod" 格式
     * @param requestHeader
     * @return
     */
    private static String getControllerInfo(Http.RequestHeader requestHeader) {
        try {
            // 获取请求属性中的路由定义（HandlerDef）
            if (requestHeader.attrs().containsKey(play.routing.Router.Attrs.HANDLER_DEF)) {
                play.routing.HandlerDef handlerDef = requestHeader.attrs().get(play.routing.Router.Attrs.HANDLER_DEF);
                String controller = handlerDef.controller();
                String actionMethod = handlerDef.method();
                return controller + "." + actionMethod;
            } else {
                // 如果没有 HandlerDef，尝试从路径推断或返回未知
                return "Unknown (No HandlerDef)";
            }
        } catch (Exception e) {
            // 日志可选：logger.error("Failed to get controller info", e);
            return "Unknown";
        }
    }

}
