package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.dto.NhaCungCapForm;
import com.QuanLyNHHS.model.NhaCungCap;
import com.QuanLyNHHS.service.NhaCungCapService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nha-cung-cap")
public class NhaCungCapController {
    private final NhaCungCapService service;

    public NhaCungCapController(NhaCungCapService service) {
        this.service = service;
    }

    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("tenNcc", "sdt", "diaChi", "email");
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", service.findAll());
        return "kho/nha-cung-cap-list";
    }

    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new NhaCungCapForm());
        model.addAttribute("id", null);
        return "kho/nha-cung-cap-form";
    }

    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        NhaCungCap supplier = service.findById(id);
        NhaCungCapForm form = new NhaCungCapForm();
        form.setTenNcc(supplier.getTenNcc());
        form.setSdt(supplier.getSdt());
        form.setDiaChi(supplier.getDiaChi());
        form.setEmail(supplier.getEmail());
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        return "kho/nha-cung-cap-form";
    }

    @PostMapping({"/them", "/{id}/sua"})
    public String save(
            @PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") NhaCungCapForm form,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                service.save(id, form);
                flash.addFlashAttribute("success", "Đã lưu nhà cung cấp.");
                return "redirect:/nha-cung-cap";
            } catch (IllegalArgumentException e) {
                errors.reject("business", e.getMessage());
            }
        }
        model.addAttribute("id", id);
        return "kho/nha-cung-cap-form";
    }

    @PostMapping("/{id}/xoa")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.delete(id);
            flash.addFlashAttribute("success", "Đã xóa nhà cung cấp.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nha-cung-cap";
    }
}
