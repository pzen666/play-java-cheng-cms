package controllers;

import entity.result.Results;
import io.ebean.DB;
import models.User;
import actions.AttrKey;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.JwtUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AuthController extends Controller {

    /**
     * 用户登录并返回JWT token
     *
     * @param request
     * @return
     */
//    public Result login(Http.Request request) {
//        User loginRequest = Json.fromJson(request.body().asJson(), User.class);
//        // 查找用户
//        User user = DB.find(User.class).where()
//                .eq("name", loginRequest.name).eq("password", loginRequest.password)
//                .findOne();
//        // 验证用户是否存在以及密码是否正确
//        if (user == null || !user.password.equals(loginRequest.password)) {
//            return unauthorized(Json.toJson(Results.error("用户名或密码错误")));
//        }
//        // 生成JWT token
//        String token = JwtUtils.generateToken(user.name);
//        // 返回token
//        return ok(Json.toJson(Results.success(token)));
//    }

    public CompletionStage<Result> login(Http.Request request) {
        JsonNode json = request.body().asJson();
        String username = json.get("username").asText();
        String password = json.get("password").asText();

        // 这里应该有实际的用户验证逻辑
        // 暂时假设用户验证通过
        if ("admin".equals(username) && "admin".equals(password)) {
            String accessToken = JwtUtils.generateToken(username);
            String refreshToken = JwtUtils.generateRefreshToken(username);

            ObjectNode tokenNode = Json.newObject();
            tokenNode.put("access_token", accessToken);
            tokenNode.put("refresh_token", refreshToken);
            tokenNode.put("token_type", "Bearer");
            tokenNode.put("expires_in", 1800); // 30分钟

            return CompletableFuture.completedFuture(ok(Json.toJson(tokenNode)));
        } else {
            ObjectNode errorNode = Json.newObject();
            errorNode.put("error", "invalid_credentials");
            errorNode.put("message", "用户名或密码错误");
            return CompletableFuture.completedFuture(unauthorized(errorNode));
        }
    }

    public CompletionStage<Result> refresh(Http.Request request) {
        String authHeader = request.header("Authorization").orElse(null);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            if (JwtUtils.isRefreshTokenValid(refreshToken)) {
                try {
                    String username = JwtUtils.parseToken(refreshToken);
                    String newAccessToken = JwtUtils.generateToken(username);
                    String newRefreshToken = JwtUtils.generateRefreshToken(username);

                    ObjectNode tokenNode = Json.newObject();
                    tokenNode.put("access_token", newAccessToken);
                    tokenNode.put("refresh_token", newRefreshToken);
                    tokenNode.put("token_type", "Bearer");
                    tokenNode.put("expires_in", 1800); // 30分钟

                    return CompletableFuture.completedFuture(ok(Json.toJson(tokenNode)));
                } catch (Exception e) {
                    return CompletableFuture.completedFuture(unauthorized("Invalid refresh token"));
                }
            } else {
                return CompletableFuture.completedFuture(unauthorized("Invalid refresh token"));
            }
        } else {
            return CompletableFuture.completedFuture(unauthorized("Authorization header missing or invalid format"));
        }
    }


    public CompletionStage<Result> secure(Http.Request request) {
        String username = request.attrs().get(AttrKey.USERNAME);
        ObjectNode result = Json.newObject();
        result.put("message", "Secure page");
        result.put("username", username);
        return CompletableFuture.completedFuture(ok(result));
    }

    public CompletionStage<Result> currentUser(Http.Request request) {
        String username = request.attrs().get(AttrKey.USERNAME);
        ObjectNode result = Json.newObject();
        result.put("username", username);
        return CompletableFuture.completedFuture(ok(result));
    }
    public CompletionStage<Result> logout(Http.Request request) {
        return CompletableFuture.completedFuture(ok("Logout successful"));
    }

}