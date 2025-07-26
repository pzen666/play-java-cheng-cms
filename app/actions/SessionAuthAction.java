package actions;

// actions/SessionAuthAction.java
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static play.mvc.Results.unauthorized;

public class SessionAuthAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        String username = request.session().get("username").toString();
        String loggedIn = request.session().get("loggedIn").toString();

        if (username != null && "true".equals(loggedIn)) {
            // 用户已登录，继续处理请求
            return delegate.call(request);
        } else {
            // 用户未登录，返回未授权
            return CompletableFuture.completedFuture(
                    unauthorized("需要登录后操作")
            );
        }
    }
}

