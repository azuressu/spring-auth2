package com.sparta.springauth2.auth;

import com.sparta.springauth2.entity.UserRoleEnum;
import com.sparta.springauth2.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {

    // 범용적으로 사용될 상수이기 때문에 static & final & 대문자
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // JwtUtil 클래스 객체 생성
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        addCookie("Robbie Auth", res);

        return "createCookie";
    }

    // 쿠키 읽기
    @GetMapping("/get-cookie")
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) { // Cookie의 Name 정보를 전달해 주면 해당 정보를 토대로 Cookie의 Value를 가져옴
        System.out.println("value = " + value);

        return "getCookie : " + value;
    }

    // 세션 생성
    @GetMapping("/create-session")
    public String createSession(HttpServletRequest req) { // HttpServletRequest으로 세션을 생성 및 반환 가능
        // 세션이 존재할 경우 세션을 반환하고, 없으면 새로운 세션을 생성한 후에 반환
        HttpSession session = req.getSession(true); // 세션이 존재할 경우 세션 반환, 없으면 새로 생성

        // 세션에 저장될 정보 Name-Value를 추가
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie-Auth");
        // 반환된 세션은 브라우저 Cookie 저장소에 JSESSIONID라는 Name으로 Value에 저장됨
        return "createSession";
    }

    // 세션 읽기
    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false); // 세션이 존재할 경우 세션 반환, 없으면 null 밯놘
        // 가져온 세션에 저장된 Value를 Name을 사용하여 가져옴
        String value = (String) session.getAttribute(AUTHORIZATION_HEADER); // Name을 사용해 세션에 저장된 Value 가져옴
        System.out.println("value = " + value);

        return "getSession : "+value;
    }

    public static void addCookie(String cookieValue, HttpServletResponse res) { // 응답 메시지를 생성하는 역할
        try {
            // Cookie Value에는 공백이 불가능해서 encoding 진행
            cookieValue = URLEncoder.encode(cookieValue, "UTF-8").replaceAll("\\+", "%20");
            // Name-Value
            // new Cookie - Cookie에 저장될 Name과 Value를 생성자로 받는 Cookie 객체를 생성
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
            // path와 만료 시간을 지정
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);

            // Response 객체에 Cookie 추가 - HttpServletResponse 객체에 생성한 Cookie 객체를 추가하여 브라우저로 반환
            // 브라우저의 Cookie 저장소에 저장됨
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/create-jwt")
    public String createJwt(HttpServletResponse res) {
        // JWT 생성
        String token = jwtUtil.createToken("Robbie", UserRoleEnum.USER);

        // JWT 쿠키 저장
        jwtUtil.addJwtToCookie(token, res);

        return "createJwt : " + token;
    }

    @GetMapping("/get-jwt")
    public String getJwt(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
        // JWT 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // JWT 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        // 토큰에서 사용자 정보 가져오기 (Claims 타입)
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // 사용자 username
        String username = info.getSubject();
        System.out.println("username = " + username);
        // 사용자 권한
        String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
        System.out.println("authority = " + authority);

        return "getJwt : " + username + ", " + authority;

    }


}
