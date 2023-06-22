package com.sparta.springauth2.controller;

import com.sparta.springauth2.entity.User;
import com.sparta.springauth2.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProductController {

    @GetMapping("/products")
    // @AuthenticationPrincipal
    // Authentication의 Principal에 저장된 UserDetailsImpl을 가져올 수 있음
    // UserDetailsImpl에 저장된 인증된 사용자인 User 객체를 사용할 수 있음
    public String getProducts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 본인이 등록한 제품만 조회하는 기능의 API라 가정
        // Filter에서 인증 처리되어 넘어온 User 객체를 사용하면 API를 요청한 해당 사용자가 등록한 제품만 조회 가능
        System.out.println("ProductController.getProducts : 인증 완료");
        User user = userDetails.getUser();
        System.out.println("user.getUsername() = " + user.getUsername());

        return "redirect:/";
    }
}
