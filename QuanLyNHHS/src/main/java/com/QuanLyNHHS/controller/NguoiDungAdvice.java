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
    @ModelAttribute("laAdmin")
    public boolean laAdmin(Authentication authentication) {
        return coQuyen(authentication, "ROLE_ADMIN");
    }

    @ModelAttribute("laQuanLyKho")
    public boolean laQuanLyKho(Authentication authentication) {
        return coQuyen(authentication, "ROLE_QUAN_LY_KHO") || laAdmin(authentication);
    }

    @ModelAttribute("laKeToan")
    public boolean laKeToan(Authentication authentication) {
        return coQuyen(authentication, "ROLE_KE_TOAN") || laAdmin(authentication);
    }

    @ModelAttribute("laThuNgan")
    public boolean laThuNgan(Authentication authentication) {
        return coQuyen(authentication, "ROLE_THU_NGAN") || laAdmin(authentication);
    }

    private boolean coQuyen(Authentication authentication, String quyen) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> quyen.equals(authority.getAuthority()));
    }
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
