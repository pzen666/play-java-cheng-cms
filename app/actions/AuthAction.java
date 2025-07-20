package actions;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class AuthAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        // 从 Header 获取用户名
        String username = request.headers().get("X-User").orElse("anonymous");
        // 创建新的请求对象，并将 username 放入 attrs 中
        Http.Request newRequest = request.addAttr(AttrKey.USERNAME, username);
        // 继续调用下一个 action
        return delegate.call(newRequest);
    }
}