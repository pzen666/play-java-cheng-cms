// controllers/SessionController.java
package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionController extends Controller {

    // 获取 session JWT token 并设置 session
    public Result getSessionToken() {
        // 使用 withSession 方法设置 session
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("user", "user");
        userInfo.put("loggedIn", "true");
        return ok("Session 已设置，检查响应中的 Set-Cookie 头")
                .withSession(
                        userInfo
                );
    }

    // 读取 session 数据 (通过 Request 对象)
    public Result readSessionData(Http.Request request) {
        // 从 request.session() 获取数据
        Optional<String> username = request.session().get("user");
        Optional<String> loggedIn = request.session().get("loggedIn");

        if (username.isPresent() && "true".equals(loggedIn.orElse("false"))) {
            return ok("用户已登录: " + username.get());
        } else {
            return unauthorized("用户未登录");
        }
    }

    // 另一种读取 session 数据的方式
    public Result readSessionDataAlt(Http.Request request) {
        // 使用 Java 8 的函数式方法
        return request.session().get("user")
                .filter(user -> "true".equals(request.session().get("loggedIn").orElse("false")))
                .map(user -> ok("用户已登录: " + user))
                .orElse(unauthorized("用户未登录"));
    }

    // 清除 session
    public Result clearSession() {
        return ok("Session 已清除").withSession(new Session()); // withSession() 不传参数会创建空 session
    }
}
