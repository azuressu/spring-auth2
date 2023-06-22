package com.sparta.springauth2.controller;

import com.sparta.springauth2.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // @AuthenticationPrincipal 사용해서 메인 페이지 사용자 이름 반영하기
        model.addAttribute("username", userDetails.getUser().getUsername());
        return "index";
    }

}
