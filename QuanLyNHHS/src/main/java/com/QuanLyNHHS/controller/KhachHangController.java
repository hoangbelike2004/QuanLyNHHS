package com.QuanLyNHHS.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.service.DatBanService;
import com.QuanLyNHHS.service.KhachHangService;

@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    @Autowired private KhachHangService khachHangService;
    @Autowired private DatBanService datBanService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("danhSachKH", khachHangService.search(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("danhSachKH", khachHangService.findAll());
        }
        model.addAttribute("pageTitle", "Quản lý Khách hàng");
        return "khach-hang/list";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        model.addAttribute("pageTitle", "Thêm Khách hàng");
        model.addAttribute("isEdit", false);
        return "khach-hang/form";
    }

    @PostMapping("/them")
    public String processAdd(@ModelAttribute KhachHang khachHang, RedirectAttributes ra) {
        try {
            khachHangService.save(khachHang);
            ra.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi thêm: " + e.getMessage());
        }
        return "redirect:/khach-hang";
    }

    @GetMapping("/sua/{maKh}")
    public String showEditForm(@PathVariable Long maKh, Model model, RedirectAttributes ra) {
        Optional<KhachHang> opt = khachHangService.findById(maKh);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!"); return "redirect:/khach-hang"; }
        model.addAttribute("khachHang", opt.get());
        model.addAttribute("pageTitle", "Sửa Khách hàng");
        model.addAttribute("isEdit", true);
        return "khach-hang/form";
    }

    @PostMapping("/sua/{maKh}")
    public String processEdit(@PathVariable Long maKh, @ModelAttribute KhachHang khachHang, RedirectAttributes ra) {
        try {
            khachHang.setMaKh(maKh);
            khachHangService.save(khachHang);
            ra.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/khach-hang";
    }

    @PostMapping("/xoa/{maKh}")
    public String delete(@PathVariable Long maKh, RedirectAttributes ra) {
        try {
            khachHangService.delete(maKh);
            ra.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa (có thể đang có đặt bàn liên quan).");
        }
        return "redirect:/khach-hang";
    }

    @GetMapping("/{maKh}/lich-su-dat-ban")
    public String lichSuDatBan(@PathVariable Long maKh, Model model, RedirectAttributes ra) {
        Optional<KhachHang> opt = khachHangService.findById(maKh);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!"); return "redirect:/khach-hang"; }
        model.addAttribute("khachHang", opt.get());
        model.addAttribute("danhSachDatBan", datBanService.findByKhachHang(maKh));
        model.addAttribute("pageTitle", "Lịch sử Đặt bàn - " + opt.get().getHoTen());
        return "khach-hang/lich-su-dat-ban";
    }
}
