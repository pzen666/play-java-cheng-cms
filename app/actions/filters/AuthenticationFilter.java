package actions.filters;

import play.http.HttpFilters;
import play.mvc.EssentialFilter;
import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.Arrays;
import org.apache.pekko.stream.Materializer;
import java.util.concurrent.CompletableFuture;

public class AuthenticationFilter implements HttpFilters {

    private final List<EssentialFilter> filters = new ArrayList<>();

    @Inject
    public AuthenticationFilter(Materializer mat) {
        // 添加认证过滤器到过滤器列表中
        filters.add(new AuthFilter(mat));
    }

    @Override
    public List<EssentialFilter> getFilters() {
        return filters;
    }

    /**
     * 内部类，实现具体的认证逻辑
     */
    public static class AuthFilter extends Filter {
        private final List<String> excludePaths = Arrays.asList(
//                "/", //放开所有
                "/index",
                "/login",
                "/simpleLogin",
                "/assets/",
                "/user/getUser",
                "/health"
        );

        public AuthFilter(Materializer mat) {
            super(mat);
        }

        @Override
        public CompletionStage<Result> apply(
                Function<Http.RequestHeader, CompletionStage<Result>> next,
                Http.RequestHeader requestHeader) {

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
                        play.mvc.Results.unauthorized("需要登录后操作")
                );
            }
        }
    }
}
