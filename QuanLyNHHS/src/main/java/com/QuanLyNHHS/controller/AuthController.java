package com.QuanLyNHHS.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Hiển thị đăng nhập bằng tài khoản được cấp; Spring Security tiếp nhận POST đăng nhập.
 */
@Controller
public class AuthController {
    /**
     * Mở trang đăng nhập hoặc chuyển người đã đăng nhập về trang chủ; POST đăng nhập do Spring Security xử lý.
     * @param authentication thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {
        return daDangNhap(authentication) ? "redirect:/" : "login";
    }

    /**
     * Kiểm tra request đã được xác thực và không thuộc người dùng ẩn danh.
     * @param authentication thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh
     * @return true nếu điều kiện so sánh/kiểm tra thỏa mãn, ngược lại false
     */
    private boolean daDangNhap(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
