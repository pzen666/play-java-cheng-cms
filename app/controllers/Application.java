package controllers;

import play.mvc.*;
import play.libs.Json;
import play.mvc.Http.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Application extends Controller {

    // 登录并设置 session
    public Result login(Http.Request request) {
        // 验证用户凭据后
        String username = "exampleUser";

        // 设置 session 数据
        Session session = request.session();
        session = session.adding("username", username);
        session = session.adding("loggedIn", "true");
        session = session.adding("role", "user");

        // 返回结果并设置session
        return ok("登录成功").withSession(session);
    }

    // 获取当前用户信息
    public Result getUserInfo(Http.Request request) {
        // 获取 session 数据
        Optional<String> username = request.session().get("username");
        Optional<String> role = request.session().get("role");

        if (username.isPresent()) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", username.get());
            userInfo.put("role", role.orElse("user"));
            return ok(Json.toJson(userInfo));
        } else {
            return unauthorized("未登录");
        }
    }

    // 登出
    public Result logout() {
        // 清除 session
        return ok("已登出").withSession(new Session());
    }

    // 更简单的登录方法
    public Result simpleLogin() {
        String username = "exampleUser";
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("role", "user");
        // 直接使用withSession方法
        return ok("登录成功")
                .withSession(
                        userInfo
                );
    }

}

