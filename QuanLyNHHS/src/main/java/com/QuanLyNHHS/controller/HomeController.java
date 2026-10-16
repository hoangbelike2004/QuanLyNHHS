package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activeMenu", "home");
        model.addAttribute("totalDishes", 28);
        model.addAttribute("totalStaff", 8);
        model.addAttribute("totalCategories", 6);
        model.addAttribute("todayRevenue", "48.650.000 đ");
        return "index";
    }

    @GetMapping("/hello")
    public String helloPage(Model model) {
        model.addAttribute("message", "Hệ thống Quản lý Nhà hàng Hải sản (QuanLyNHHS) đã sẵn sàng!");
        return "hello";
    }
}