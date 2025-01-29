package com.example.sehomallapi.config.security;

import com.example.sehomallapi.config.redis.RedisUtil;
import com.example.sehomallapi.repository.users.refreshToken.RefreshToken;
import com.example.sehomallapi.repository.users.refreshToken.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisUtil redisUtil;
    private final UserDetailsService userDetailsService;

    @Value("${JWT_SECRET}")
    private String keySource;

    private String key;

    @PostConstruct
    public void setUp(){
        key = Base64.getEncoder().encodeToString(keySource.getBytes());
    }

    public boolean validateToken(String token){
        try{
            if(redisUtil.hasKeyBlackList(token)){
                log.warn("로그아웃된 access 토큰입니다.");
                return false;
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(key).parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        }catch (Exception e){
            log.warn(e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token){
        if(!validateToken(token)) return false;
        // DB에 저장한 토큰 비교
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByRefreshToken(token);
        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    public Authentication getAuthentication(String token){
        String email = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createAccessToken(String email){
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setSubject(email)
                .setExpiration(new Date(now.getTime() + 1000L * 30 * 60))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))
                .signWith(SignatureAlgorithm.HS256, key) //HS256알고리즘으로 key를 암호화 해줄것이다.
                .compact();
    }

    public String getEmail(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    public void setAccessTokenCookies(HttpServletResponse response, String accessToken){
        ResponseCookie myCookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .maxAge(30 * 60)
                .sameSite("None")
                .secure(false)
                .build();

        response.addHeader("Set-Cookie", myCookie.toString());
    }

    public void setRefreshTokenCookies(HttpServletResponse response, String refreshToken){
        ResponseCookie myCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .sameSite("None")
                .secure(false)
                .build();

        response.addHeader("Set-Cookie", myCookie.toString());
    }

    public void deleteAccessAndRefreshTokenCookies(HttpServletResponse response){
        ResponseCookie myCookie1 = ResponseCookie.from("accessToken", null)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .secure(false)
                .build();

        ResponseCookie myCookie2 = ResponseCookie.from("refreshToken", null)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .secure(false)
                .build();

        response.addHeader("Set-Cookie", myCookie1.toString());
        response.addHeader("Set-Cookie", myCookie2.toString());
    }

    public String getAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
