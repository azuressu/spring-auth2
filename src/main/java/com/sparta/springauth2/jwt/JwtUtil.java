package com.sparta.springauth2.jwt;

import com.sparta.springauth2.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // Header Key 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 key
    public static final String AUTHORIZATION_KEY = "Auth";
    // Token 식별자
    // Bearer : JWT 혹은 OAuth에 대한 토큰을 사용한다는 표시
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료 시간
    public final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode한 SecretKey (application.properties에 작성한 값 가져오기 = jwt.secret.key)
    private String secretKey;
    private Key key;
    // 암호화 알고리즘은 HS256 알고리즘 사용
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    // 애플리케이션 동작 동안 프로젝트의 상태나 동작 정보를 시간순으로 기록하는 것
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        // 가져온 secretKey로 암호화
        // Encode된 secret Key를 decode해서 사용
        // Key는 Decode된 secret key를 담는 객체
        // @PostConstruct는 딱 한 번만 받아오면 되는 값을 사용할 때마다 요청을 새로 호출하는 실수를 방지하기 위한 어노테이션
        // JwtUtil 클래스의 생성자 호출 이후에 실행되어 key 필드에 값을 주입해줌
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)    // 사용자의 식별값(ID)
                        .claim(AUTHORIZATION_KEY, role)   // 사용자 권한(key-value 형식으로 key 값을 통해 확인 가능- 여기서는 Auth)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))   // 토큰 만료 시간(ms 기준)
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // secretKey 값을 담고 있는 key 암호화 알고리즘 값을 넣어줌 (key와 암호화 알고리즘을 사용해 JWT 암호화)
                        .compact();
    }

    // JWT COOKIE에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            token = URLEncoder.encode(token, "UTF-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name - Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    
    // 받아온 Cookie의 Value인 JWT 토큰 substring
    public String substringToken(String tokenValue) {
        // hasText를 사용해 공백, null을 확인하고 startsWith을 사용하여 토큰의 시작값이 Bearer이 맞는지 확인
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            // 맞다면 순수 JWT를 반환하기 위해서 substring을 사용해 Bearer을 잘라냄
            return tokenValue.substring(7); // 띄어쓰기 까지 해서 7번째부터 가져옴
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Jwts.parserBuilder()를 사용해 JWT 파싱 가능
            // JWT가 위변조 되지 않았는지 secretKey(key) 값을 넣어 확인
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
    
    // JWT 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        // JWT의 구조 중 Payload 부분에는 토큰에 담긴 정보가 들어있음
        // 여기에 담긴 정보의 한 조각을 claim이라고 부르고, 이는 key-value 한 쌍으로 구성됨. 토큰에는 여러 개의 클레임들을 넣을 수 있음
        // Jwts.parserBuilder()와 secretKey를 사용해 JWT의 Claims를 가져와 담겨 있는 사용자의 정보를 사용함
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody();
    }




}
