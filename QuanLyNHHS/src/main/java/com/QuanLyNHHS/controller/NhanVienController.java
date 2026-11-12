package com.QuanLyNHHS.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.QuanLyNHHS.model.NhanVien;
import com.QuanLyNHHS.repository.NhanVienRepository;

@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {
    private final NhanVienRepository nhanVienRepository;

    public NhanVienController(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public String index(Model model) {
        var staffList = nhanVienRepository.findAll();
        model.addAttribute("staffList", staffList);
        model.addAttribute("totalStaff", staffList.size());
        model.addAttribute("activeMenu", "nhanvien");
        return "nhanvien/index";
    }

    @PostMapping("/them")
    public String them(@RequestParam String hoTen, @RequestParam String sdt,
                       @RequestParam String chucVu, @RequestParam BigDecimal luong,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayVaoLam,
                       RedirectAttributes ra) {
        try {
            if (hoTen.isBlank() || hoTen.length() > 100 || sdt.length() > 15
                    || chucVu.isBlank() || chucVu.length() > 50
                    || luong.signum() < 0 || luong.scale() > 2 || ngayVaoLam == null) {
                throw new IllegalArgumentException("Thông tin nhân viên không hợp lệ");
            }
            nhanVienRepository.save(new NhanVien(hoTen.trim(), null, sdt.trim(),
                    chucVu.trim(), luong, ngayVaoLam));
            ra.addFlashAttribute("successMessage", "Đã thêm nhân viên.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Không thể thêm nhân viên: " + e.getMessage());
        }
        return "redirect:/nhan-vien";
    }
}
