package utils;

// utils/SessionUtils.java
import play.mvc.Http;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class SessionUtils {

    /**
     * 创建带过期时间的 session
     */
    public static void createSession(Http.Session session, String username) {
        session.data().put("username", username);
        session.data().put("loginTime", String.valueOf(System.currentTimeMillis()));
        // 可以添加角色等其他信息
        session.data().put("role", "user");
    }

    /**
     * 验证 session 是否有效
     */
    public static boolean isSessionValid(Http.Session session) {
        String loginTimeStr = session.data().get("loginTime");
        if (loginTimeStr == null) {
            return false;
        }

        try {
            long loginTime = Long.parseLong(loginTimeStr);
            // 假设 session 有效期为 30 分钟
            long sessionExpiry = 30 * 60 * 1000; // 30分钟
            return (System.currentTimeMillis() - loginTime) < sessionExpiry;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取当前用户名
     */
    public static Optional<String> getCurrentUser(Http.Session session) {
        return Optional.ofNullable(session.data().get("username"));
    }
}

