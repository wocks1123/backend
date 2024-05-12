package com.swygbro.trip.backend.global.jwt;

import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 24시간
    private static final long ACCESS_EXPIRE_TIME = 60 * 60 * 24 * 1000L;
    // 14일
    private static final long REFRESH_EXPIRE_TIME = 60 * 60 * 24 * 14 * 1000L;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto generateToken(String email) {
        String accessToken = createAccessToken(email);
        String refreshToken = createRefreshToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    public String createAccessToken(String email) {
        Date date = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + ACCESS_EXPIRE_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date date = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + REFRESH_EXPIRE_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (SecurityException | MalformedJwtException e) {
//            throw new InvalidJwtException("잘못된 JWT 토큰 입니다.");
        } catch (ExpiredJwtException e) {
//            throw new InvalidJwtException("만료된 JWT 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
//            throw new InvalidJwtException("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
//            throw new InvalidJwtException("JWT 토큰이 비어 있습니다.");
        } catch (Exception e) {
//            throw new InvalidJwtException("유효하지 않은 JWT 토큰 입니다.");
        }
        return null;
    }


}
