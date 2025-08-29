# play-java-cheng-cms
基于Play Framework框架开发的CMS采集系统，采用GPL V3版本授权

sbt clean
sbt update
sbt compile


sbt clean update compile


-Xms512M
-Xmx2048M
-Xss1M
-XX:-CreateMinidumpOnCrash
-XX:-HeapDumpOnOutOfMemoryError

权限日志:

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SecurityService {
private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");

    public void loginUser(String username, String password) {
        if ("admin".equals(username) && "securepass".equals(password)) {
            logger.info(SECURITY_MARKER, "用户 {} 成功登录。", username);
        } else {
            logger.warn(SECURITY_MARKER, "用户 {} 登录失败。", username);
        }
    }
}