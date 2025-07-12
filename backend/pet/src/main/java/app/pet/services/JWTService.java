package app.pet.services;

import app.pet.models.User;
import app.pet.security.JWTConfig;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    private final JWTConfig jwtConfig;
    private final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24;
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 4;

    private JwtParser parser;
    private SecretKey signingKey;

    public JWTService(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }


    public JwtParser getParser() {
        return this.parser;
    }

    @PostConstruct
    public void init() {
        this.signingKey = jwtConfig.getSecretKey();
        this.parser = Jwts.parserBuilder()
                          .setSigningKey(signingKey)
                          .setAllowedClockSkewSeconds(30)
                          .build();
    }

    public String generateAccessToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                   .setSubject(user.getId().toString())
                   .claim("username", user.getUsername())
                   .claim("role", user.getRole().name())
                   .setIssuedAt(new Date(now))
                   .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRATION))
                   .signWith(signingKey, SignatureAlgorithm.HS256)
                   .compact();
    }

    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                   .setSubject(user.getId().toString())
                   .setIssuedAt(new Date(now))
                   .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRATION))
                   .signWith(signingKey, SignatureAlgorithm.HS256)
                   .compact();
    }

    public String extractUserId(String token) throws JwtException {
        Jws<Claims> claims = parser.parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    public boolean isTokenValid(String token, String expectedUserId) {
        try {
            String sub = extractUserId(token);
            return sub.equals(expectedUserId);
        } catch (ExpiredJwtException eje) {
            return false;
        } catch (JwtException e) {
            return false;
        }
    }
}
