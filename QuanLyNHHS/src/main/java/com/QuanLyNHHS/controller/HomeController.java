package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Điều hướng đến trang chủ và trang chào mẫu.
 */
@Controller
public class HomeController {

    /**
     * Đánh dấu menu dashboard đang chọn và trả template trang chủ.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: index
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        return "index";
    }

    /**
     * Đặt thông báo mẫu vào Model và trả template hello.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: hello
     */
    @GetMapping("/hello")
    public String helloPage(Model model) {
        // Truyền một biến tên là "message" sang cho file HTML
        model.addAttribute("message", "Hello World từ Spring Boot & Thymeleaf!");

        // Trả về tên file HTML (không cần đuôi .html) nằm trong thư mục templates
        return "hello";
    }
}
