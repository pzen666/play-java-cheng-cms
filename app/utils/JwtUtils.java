package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.typesafe.config.Config;

import java.util.Date;

public class JwtUtils {

    private static Algorithm algorithm;
    private static JWTVerifier verifier;
    private static long expiration;

    public static void init(Config config) {
        String secret = config.getString("jwt.secret");
        expiration = config.getLong("jwt.expiration");
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).build();
    }

    public static String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    public static String parseToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
