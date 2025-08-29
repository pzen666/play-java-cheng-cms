package controllers;

import entity.result.Results;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Session;
import play.mvc.Result;
import service.UserService;

import java.util.Map;

public class ApplicationController extends Controller {

    private final UserService userService;

    @Inject
    public ApplicationController(UserService userService) {
        this.userService = userService;
    }

    // 登录并设置 session
    public Result login(Http.Request request) {
        Map<String, String> map = Json.fromJson(request.body().asJson(), Map.class);
        Http.Session userSession = userService.loginUser(request, map);
        if (userSession == null) {
            return unauthorized(Json.toJson(Results.error("登录失败!")));
        } else {
            return ok(Json.toJson(Results.success(null))).withSession(userSession);
        }

    }

    // 获取当前用户信息
    public Result getUserInfo(Http.Request request) {
        Object userInfo = userService.getUserInfo(request.session());
        if (userInfo == null) {
            return unauthorized(Json.toJson(Results.error("未登录,请重新登录!")));
        } else {
            return ok(Json.toJson(Results.success(userInfo)));
        }
    }

    // 登出
    public Result logout(Http.Request request) {
        // 清除 session
        return ok("已登出").withSession(new Session());
    }

}

