package actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entity.result.Results;
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
        // 对于API路由，跳过CSRF检查
        if (request.path().startsWith("/user/") ||
                request.path().startsWith("/api/")) {
            // 直接处理JWT认证
            return handleJwtAuth(request);
        }
        // 其他路由按原逻辑处理
        return handleJwtAuth(request);
    }

    private CompletionStage<Result> handleJwtAuth(Http.Request request) {
        // 排除不需要认证的接口
        if (request.path().equals("/login") ||
                request.path().equals("/refresh") ||
                request.path().equals("/")) {
            return delegate.call(request);
        }

        // JWT认证逻辑
        String authHeader = request.header("Authorization").orElse(null);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = JwtUtils.parseToken(token);
                Http.Request newRequest = request.addAttr(AttrKey.USERNAME, username);
                return delegate.call(newRequest);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("expired")) {
                    return CompletableFuture.completedFuture(
                            unauthorized(Json.toJson(Results.error(401,"Token已过期，请刷新Token"))));
                } else {
                    return CompletableFuture.completedFuture(
                            unauthorized(Json.toJson(Results.error(401,"无效的Token"))));
                }
            }
        } else {
            return CompletableFuture.completedFuture(
                    unauthorized(Json.toJson(Results.error(401,"请登录后操作"))));
        }
    }

}
