package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.typesafe.config.Config;

import java.util.Date;
import java.util.UUID;

public class JwtUtils {

    private static Algorithm algorithm;
    private static JWTVerifier verifier;
    private static long expiration = 1800000L; // 默认30分钟
    private static long refreshExpiration = 604800000L; // 默认7天
    private static String secret = "your-secret-key"; // 默认密钥

    // 静态初始化
    static {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).build();
    }

    public static void init(Config config) {
        secret = config.getString("jwt.secret");
        expiration = config.getLong("jwt.expiration");
        refreshExpiration = config.getLong("jwt.refresh.expiration");
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).build();
    }

    public static String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    public static String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withJWTId(UUID.randomUUID().toString()) // 添加JWT ID
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .sign(algorithm);
    }

    public static String parseToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return false;
        } catch (JWTVerificationException e) {
            // 如果是过期异常，则返回true
            return e.getMessage().contains("expired");
        }
    }

    public static boolean isRefreshTokenValid(String refreshToken) {
        try {
            DecodedJWT jwt = verifier.verify(refreshToken);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
