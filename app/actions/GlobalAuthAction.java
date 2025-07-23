package actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.JwtUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GlobalAuthAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        // 排除登录接口
        if (request.path().equals("/login")) {
            return delegate.call(request);
        }

        // 排除刷新token接口
        if (request.path().equals("/refresh")) {
            return delegate.call(request);
        }
        if (request.path().equals("/")) {
            return delegate.call(request);
        }

        // 检查Authorization header
        String authHeader = request.header("Authorization").orElse(null);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = JwtUtils.parseToken(token);
                // 将用户名添加到请求属性中
                Http.Request newRequest = request.addAttr(AttrKey.USERNAME, username);
                return delegate.call(newRequest);
            } catch (Exception e) {
                // 检查是否是token过期异常
                if (e.getMessage() != null && e.getMessage().contains("expired")) {
                    ObjectNode errorNode = Json.newObject();
                    errorNode.put("error", "token_expired");
                    errorNode.put("message", "Token已过期，请刷新Token");
                    return CompletableFuture.completedFuture(unauthorized(errorNode));
                } else {
                    ObjectNode errorNode = Json.newObject();
                    errorNode.put("error", "invalid_token");
                    errorNode.put("message", "无效的Token");
                    return CompletableFuture.completedFuture(unauthorized(errorNode));
                }
            }
        } else {
            ObjectNode errorNode = Json.newObject();
            errorNode.put("error", "missing_token");
            errorNode.put("message", "缺少Authorization header或格式不正确");
            return CompletableFuture.completedFuture(unauthorized(errorNode));
        }
    }
}
