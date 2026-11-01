package com.QuanLyNHHS.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.service.BanAnService;

@Controller
@RequestMapping("/ban-an")
public class BanAnController {

    @Autowired private BanAnService banAnService;

    @GetMapping
    public String list(@RequestParam(required = false) String trangThai, Model model) {
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            model.addAttribute("danhSachBan", banAnService.findByTrangThai(trangThai));
            model.addAttribute("trangThaiFilter", trangThai);
        } else {
            model.addAttribute("danhSachBan", banAnService.findAll());
        }
        model.addAttribute("pageTitle", "Quản lý Bàn ăn");
        return "ban-an/list";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("banAn", new BanAn());
        model.addAttribute("pageTitle", "Thêm Bàn ăn");
        model.addAttribute("isEdit", false);
        return "ban-an/form";
    }

    @PostMapping("/them")
    public String processAdd(@ModelAttribute BanAn banAn, RedirectAttributes ra) {
        try {
            banAnService.save(banAn);
            ra.addFlashAttribute("successMessage", "Thêm bàn ăn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi thêm: " + e.getMessage());
        }
        return "redirect:/ban-an";
    }

    @GetMapping("/sua/{maBan}")
    public String showEditForm(@PathVariable Long maBan, Model model, RedirectAttributes ra) {
        Optional<BanAn> opt = banAnService.findById(maBan);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy bàn ăn!"); return "redirect:/ban-an"; }
        model.addAttribute("banAn", opt.get());
        model.addAttribute("pageTitle", "Sửa Bàn ăn");
        model.addAttribute("isEdit", true);
        return "ban-an/form";
    }

    @PostMapping("/sua/{maBan}")
    public String processEdit(@PathVariable Long maBan, @ModelAttribute BanAn banAn, RedirectAttributes ra) {
        try {
            banAn.setMaBan(maBan);
            banAnService.save(banAn);
            ra.addFlashAttribute("successMessage", "Cập nhật bàn ăn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/ban-an";
    }

    @PostMapping("/xoa/{maBan}")
    public String delete(@PathVariable Long maBan, RedirectAttributes ra) {
        try {
            banAnService.delete(maBan);
            ra.addFlashAttribute("successMessage", "Xóa bàn ăn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa bàn này (có thể đang có đặt bàn liên quan).");
        }
        return "redirect:/ban-an";
    }
}
