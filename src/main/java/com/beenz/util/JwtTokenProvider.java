package com.beenz.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

@Component
@Setter
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRE_MS = 60000;
    private final long REFRESH_TOKEN_EXPIRE_MS = 600000;
    private final String TOKEN_PREFIX = "Bearer ";

    public String createAccessToken(long id, Set<SimpleGrantedAuthority> authorities) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("authorities", authorities);
        return createToken(
                "accessToken",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_MS),
                claims);
    }

    public String createRefreshToken(long id, Set<SimpleGrantedAuthority> authorities) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("authorities", authorities);
        return createToken(
                "refreshToken",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_MS),
                claims);
    }

    private String createToken(String sub, Date issuedAt, Date expiration, Claims claims) {
        return Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(getSecretKeyForSigning())
                .compact();
    }

    public SecretKey getSecretKeyForSigning() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String getTokenPrefix() {
        return this.TOKEN_PREFIX;
    }
}
