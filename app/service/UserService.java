package service;

import models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserService {

    public Object getUserInfo(Http.Session session) {
        // 获取 session 数据
        Optional<String> username = session.get("username");
        Optional<String> role = session.get("role");
        if (username.isPresent()) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", username.get());
            userInfo.put("role", role.orElse("user"));
            return userInfo;
        } else {
            return null;
        }
    }

    public Http.Session loginUser(Http.Request request, Map<String, String> map) {
        // 验证用户凭据后
        String username = map.getOrDefault("username", "");
        String password = map.getOrDefault("password", "");
        User u = User.find.query().where().eq("username", username).findOne();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (u != null && encoder.matches(password, u.getPassword())) {
            Http.Session session = request.session();
            // 设置 session 数据
            session = session.adding("username", username);
            session = session.adding("loggedIn", "true");
            session = session.adding("role", "user");
            return session;
        } else {
            return null;
        }
    }

}
