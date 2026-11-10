package com.QuanLyNHHS.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import com.QuanLyNHHS.dto.DangKyForm;
import com.QuanLyNHHS.service.TaiKhoanService;

/**
 * Hiển thị đăng nhập và xử lý đăng ký; Spring Security tiếp nhận POST đăng nhập.
 */
@Controller
public class AuthController {
    // service: thành phần TaiKhoanService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final TaiKhoanService service;
    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần TaiKhoanService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public AuthController(TaiKhoanService service) { this.service = service; }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("dangKyForm")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("hoTen", "tenDangNhap", "matKhau", "xacNhanMatKhau");
    }

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
     * Mở trang đăng ký với DTO rỗng; người đã đăng nhập được chuyển về trang chủ.
     * @param authentication thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/, register
     */
    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (daDangNhap(authentication)) return "redirect:/";
        model.addAttribute("dangKyForm", new DangKyForm());
        return "register";
    }

    /**
     * Nhận form đăng ký, kiểm tra lỗi, gọi service tạo tài khoản; nếu lỗi thì xóa mật khẩu trên DTO trước khi trả lại form.
     * @param form đối tượng DangKyForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param authentication thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/, redirect:/login?registered, register
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dangKyForm") DangKyForm form,
            BindingResult errors, Authentication authentication) {
        if (daDangNhap(authentication)) return "redirect:/";
        if (!errors.hasErrors()) {
            try {
                service.dangKy(form);
                return "redirect:/login?registered";
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ TaiKhoanService.DangKyException e) {
                errors.rejectValue(e.getField(), "registration", e.getMessage());
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ DataIntegrityViolationException e) {
                // Also handles simultaneous registration of the same normalized username.
                errors.rejectValue("tenDangNhap", "duplicate", "Không thể đăng ký tên này. Hãy chọn tên đăng nhập khác.");
            }
        }
        form.setMatKhau(null);
        form.setXacNhanMatKhau(null);
        return "register";
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
