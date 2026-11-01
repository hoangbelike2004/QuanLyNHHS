package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.dto.NguyenLieuForm;
import com.QuanLyNHHS.model.NguyenLieu;
import com.QuanLyNHHS.service.NguyenLieuService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nguyen-lieu")
public class NguyenLieuController {
    private final NguyenLieuService service;
    public NguyenLieuController(NguyenLieuService service) { this.service = service; }

    @InitBinder("form")
    void bind(WebDataBinder binder) { binder.setAllowedFields("tenNl", "donVi", "trangThai"); }

    @GetMapping
    public String list(Model model) { model.addAttribute("rows", service.findAll()); return "kho/nguyen-lieu-list"; }

    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new NguyenLieuForm());
        model.addAttribute("id", null);
        return "kho/nguyen-lieu-form";
    }

    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        NguyenLieu n = service.findById(id);
        NguyenLieuForm f = new NguyenLieuForm();
        f.setTenNl(n.getTenNl());
        f.setDonVi(n.getDonVi());
        f.setTrangThai(n.getTrangThai());
        model.addAttribute("form", f);
        model.addAttribute("id", id);
        return "kho/nguyen-lieu-form";
    }

    @PostMapping({"/them", "/{id}/sua"})
    public String save(@PathVariable(required = false) Long id, @Valid @ModelAttribute("form") NguyenLieuForm form,
            BindingResult errors, Model model, RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                service.save(id, form);
                flash.addFlashAttribute("success", "Đã lưu nguyên liệu.");
                return "redirect:/nguyen-lieu";
            } catch (IllegalArgumentException e) { errors.reject("business", e.getMessage()); }
        }
        model.addAttribute("id", id);
        return "kho/nguyen-lieu-form";
    }

    @PostMapping("/{id}/xoa")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try { service.delete(id); flash.addFlashAttribute("success", "Đã xóa nguyên liệu."); }
        catch (IllegalArgumentException e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/nguyen-lieu";
    }
}
