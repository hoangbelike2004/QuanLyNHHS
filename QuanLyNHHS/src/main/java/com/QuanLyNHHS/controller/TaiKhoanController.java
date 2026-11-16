package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.service.TaiKhoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tai-khoan")
public class TaiKhoanController {
    private final TaiKhoanService taiKhoanService;

    public TaiKhoanController(TaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSachTaiKhoan", taiKhoanService.layDanhSach());
        return "taikhoan/index";
    }

    @PostMapping
    public String taoTaiKhoan(@RequestParam String hoTen,
            @RequestParam String tenDangNhap,
            @RequestParam String matKhau,
            @RequestParam String vaiTro,
            RedirectAttributes redirectAttributes) {
        try {
            taiKhoanService.taoTaiKhoan(hoTen, tenDangNhap, matKhau, vaiTro);
            redirectAttributes.addFlashAttribute("thongBao", "Đã tạo tài khoản " + tenDangNhap.strip().toLowerCase(java.util.Locale.ROOT) + ".");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/tai-khoan";
    }
}
