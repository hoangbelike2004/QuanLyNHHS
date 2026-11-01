package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** Trang chủ */
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    /** Trang test hello */
    @GetMapping("/hello")
    public String helloPage(Model model) {
        model.addAttribute("message", "Hello World từ Spring Boot & Thymeleaf!");
        return "hello";
    }
}
