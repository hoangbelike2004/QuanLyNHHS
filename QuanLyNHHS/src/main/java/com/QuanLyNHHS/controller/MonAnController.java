package com.QuanLyNHHS.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.QuanLyNHHS.model.LoaiMon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.repository.LoaiMonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;

@Controller
@RequestMapping("/mon-an")
public class MonAnController {
    private final MonAnRepository monAnRepository;
    private final LoaiMonRepository loaiMonRepository;

    public MonAnController(MonAnRepository monAnRepository, LoaiMonRepository loaiMonRepository) {
        this.monAnRepository = monAnRepository;
        this.loaiMonRepository = loaiMonRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("dishes", monAnRepository.findAll(Sort.by("tenMon")));
        model.addAttribute("categories", loaiMonRepository.findAll(Sort.by("tenLoai")));
        model.addAttribute("activeMenu", "monan");
        return "monan/index";
    }

    @PostMapping("/loai/them")
    public String themLoai(@RequestParam String tenLoai, RedirectAttributes ra) {
        try {
            if (tenLoai.isBlank() || tenLoai.length() > 100)
                throw new IllegalArgumentException("Tên loại món không hợp lệ");
            loaiMonRepository.save(new LoaiMon(tenLoai.trim(), null, "Hoạt động"));
            ra.addFlashAttribute("successMessage", "Đã thêm loại món.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Không thể thêm loại món: " + e.getMessage());
        }
        return "redirect:/mon-an";
    }

    @PostMapping("/them")
    public String themMon(@RequestParam Long maLoai, @RequestParam String tenMon,
                          @RequestParam String donVi, @RequestParam BigDecimal donGia,
                          RedirectAttributes ra) {
        try {
            if (tenMon.isBlank() || tenMon.length() > 150 || donVi.length() > 20
                    || donGia.signum() < 0 || donGia.scale() > 2) {
                throw new IllegalArgumentException("Tên hoặc đơn giá món ăn không hợp lệ");
            }
            LoaiMon loai = loaiMonRepository.findById(maLoai)
                    .orElseThrow(() -> new IllegalArgumentException("Loại món không tồn tại"));
            if (!"Hoạt động".equals(loai.getTrangThai())) {
                throw new IllegalStateException("Loại món đang tạm dừng");
            }
            monAnRepository.save(new MonAn(loai, tenMon.trim(), donVi.trim(), donGia, "Đang phục vụ"));
            ra.addFlashAttribute("successMessage", "Đã thêm món ăn.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Không thể thêm món ăn: " + e.getMessage());
        }
        return "redirect:/mon-an";
    }
}
