package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.result.Results;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.JwtUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class JwtAuthAction extends Action.Simple {

    private static final Logger.ALogger logger = Logger.of(JwtAuthAction.class);

    @Inject
    private JwtUtils jwtUtils;

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        String authHeader = request.header("Authorization").orElse(null);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = JwtUtils.parseToken(token);
                // ✅ 使用 attrs 添加用户名
                Http.Request newRequest = request.addAttr(AttrKey.USERNAME, username);
                return delegate.call(newRequest);
            } catch (Exception e) {
                return new CompletableFuture<>().thenApply(
                        v -> unauthorized(Json.toJson(Results.error("Invalid token")))
                );
            }
        } else {
            return new CompletableFuture<>().thenApply(
                    v -> unauthorized(Json.toJson(Results.error("Authorization header missing or invalid format")))
            );
        }
    }
}


