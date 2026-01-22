package com.dental.clinic.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire; // 过期时间，毫秒

    private SecretKey getSigningKey() {
        // 使用指定的密钥生成 SecretKey
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 JWT Token
     * 
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 解析 JWT Token
     * 
     * @param token JWT Token
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            // Token 无效或解析失败
            return null;
        }
    }

    /**
     * 验证 JWT Token
     * 
     * @param token    JWT Token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 从 JWT Token 中获取用户名
     * 
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 判断 Token 是否过期
     * 
     * @param token JWT Token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从 JWT Token 中获取过期时间
     * 
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }
}