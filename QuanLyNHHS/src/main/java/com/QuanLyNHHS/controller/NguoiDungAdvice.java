package com.QuanLyNHHS.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.QuanLyNHHS.service.TaiKhoanService;

/**
 * Bổ sung họ tên người dùng vào Model dùng chung cho các trang.
 */
@ControllerAdvice
public class NguoiDungAdvice {
    /**
     * Cung cấp thuộc tính tenNguoiDung dùng chung cho giao diện: ưu tiên họ tên từ principal, nếu không thì dùng tên đăng nhập.
     * @param authentication thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @ModelAttribute("tenNguoiDung")
    public String tenNguoiDung(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof /* user: thông tin người dùng lấy từ principal sau khi đăng nhập. */ TaiKhoanService.NguoiDung user) {
            return user.getHoTen();
        }
        return authentication == null ? "" : authentication.getName();
    }
}
