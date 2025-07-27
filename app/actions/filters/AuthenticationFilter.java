package actions.filters;

import com.typesafe.config.Config;
import entity.result.Results;
import org.apache.pekko.stream.Materializer;
import play.http.HttpFilters;
import play.libs.Json;
import play.mvc.EssentialFilter;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class AuthenticationFilter implements HttpFilters {

    private final List<EssentialFilter> filters = new ArrayList<>();

    @Inject
    public AuthenticationFilter(Materializer mat, Config config) {
        // 添加认证过滤器到过滤器列表中
        filters.add(new AuthFilter(mat, config));
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
        private final  String filterPathStatus;

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
            if (filterPathStatus.equals("true")){
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
    
}
