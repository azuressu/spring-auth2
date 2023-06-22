package com.sparta.springauth2.controller;

import com.sparta.springauth2.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProductController {

    @GetMapping("/products")
    public String getProducts(HttpServletRequest req) {
        // 사용자 본인이 등록한 제품만 조회하는 기능의 API라 가정
        // Filter에서 인증 처리되어 넘어온 User 객체를 사용하면 API를 요청한 해당 사용자가 등록한 제품만 조회 가능
        System.out.println("ProductController.getProducts : 인증 완료");
        User user = (User) req.getAttribute("user");
        System.out.println("user.getUsername() = " + user.getUsername());

        return "redirect:/";
    }
}
