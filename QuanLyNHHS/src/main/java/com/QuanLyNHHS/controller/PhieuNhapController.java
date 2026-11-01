package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.PhieuNhap;
import com.QuanLyNHHS.repository.NhanVienNhapKhoRepository;
import com.QuanLyNHHS.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/phieu-nhap")
public class PhieuNhapController {
    private final PhieuNhapService service;
    private final NhaCungCapService suppliers;
    private final NguyenLieuService materials;
    private final NhanVienNhapKhoRepository employees;
    public PhieuNhapController(PhieuNhapService service, NhaCungCapService suppliers,
            NguyenLieuService materials, NhanVienNhapKhoRepository employees) {
        this.service = service; this.suppliers = suppliers; this.materials = materials; this.employees = employees;
    }

    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(100);
        binder.setAllowedFields("maNcc", "maNv", "ngayNhap", "phienBan",
            "chiTiet[*].maNl", "chiTiet[*].soLuong", "chiTiet[*].donGia");
    }

    @GetMapping
    public String list(Model model) { model.addAttribute("rows", service.findAll()); return "kho/phieu-nhap-list"; }

    @GetMapping("/them")
    public String create(Model model) {
        PhieuNhapForm f = new PhieuNhapForm();
        f.getChiTiet().add(new DongNhapForm());
        model.addAttribute("form", f);
        return formPage(null, model);
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("phieu", service.findById(id));
        return "kho/phieu-nhap-detail";
    }

    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.getForm(id));
        return formPage(id, model);
    }

    @PostMapping({"/them", "/{id}/sua"})
    public String save(@PathVariable(required = false) Long id, @Valid @ModelAttribute("form") PhieuNhapForm form,
            BindingResult errors, Model model, RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                PhieuNhap p = service.save(id, form);
                flash.addFlashAttribute("success", "Đã lưu phiếu nhập và cập nhật tồn kho.");
                return "redirect:/phieu-nhap/" + p.getMaPn();
            } catch (IllegalArgumentException e) { errors.reject("business", e.getMessage()); }
        }
        return formPage(id, model);
    }

    @PostMapping("/{id}/xoa")
    public String delete(@PathVariable Long id, @RequestParam Long phienBan, RedirectAttributes flash) {
        try { service.delete(id, phienBan); flash.addFlashAttribute("success", "Đã xóa phiếu nhập và trừ lại tồn kho."); }
        catch (IllegalArgumentException e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/phieu-nhap";
    }

    private String formPage(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("nhaCungCaps", suppliers.findAll());
        model.addAttribute("nguyenLieus", materials.findAll());
        model.addAttribute("nhanViens", employees.findAll());
        return "kho/phieu-nhap-form";
    }
}
