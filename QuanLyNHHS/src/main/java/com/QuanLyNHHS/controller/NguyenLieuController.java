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

/**
 * Nhận yêu cầu HTTP của module nguyên liệu, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/nguyen-lieu")
public class NguyenLieuController {
    // service: thành phần NguyenLieuService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final NguyenLieuService service;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần NguyenLieuService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public NguyenLieuController(NguyenLieuService service) {
        this.service = service;
    }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("tenNl", "donVi", "trangThai");
    }

    /**
     * Nạp danh sách nguyên liệu cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nguyen-lieu-list
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", service.findAll());
        return "kho/nguyen-lieu-list";
    }

    /**
     * Tạo DTO rỗng và mở biểu mẫu thêm mới; chưa lưu dữ liệu vào database.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nguyen-lieu-form
     */
    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new NguyenLieuForm());
        model.addAttribute("id", null);
        return "kho/nguyen-lieu-form";
    }

    /**
     * Tìm bản ghi theo mã, đưa dữ liệu hiện tại vào form và mở giao diện sửa.
     * @param id khóa chính của nguyên liệu
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nguyen-lieu-form
     */
    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        // ingredient: nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn.
        NguyenLieu ingredient = service.findById(id);
        // form: đối tượng NguyenLieuForm chứa dữ liệu nhập của biểu mẫu.
        NguyenLieuForm form = new NguyenLieuForm();
        form.setTenNl(ingredient.getTenNl());
        form.setDonVi(ingredient.getDonVi());
        form.setTrangThai(ingredient.getTrangThai());
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        return "kho/nguyen-lieu-form";
    }

    /**
     * Nhận DTO nguyên liệu, kiểm tra BindingResult rồi gọi service lưu; thành công chuyển hướng, có lỗi thì trả lại form và giữ dữ liệu nhập.
     * @param id khóa chính của nguyên liệu (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng NguyenLieuForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nguyen-lieu, kho/nguyen-lieu-form
     */
    @PostMapping({"/them", "/{id}/sua"})
    public String save(
            @PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") NguyenLieuForm form,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                service.save(id, form);
                flash.addFlashAttribute("success", "Đã lưu nguyên liệu.");
                return "redirect:/nguyen-lieu";
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
                errors.reject("business", e.getMessage());
            }
        }
        model.addAttribute("id", id);
        return "kho/nguyen-lieu-form";
    }

    /**
     * Gọi service xóa nguyên liệu và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param id khóa chính của nguyên liệu
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nguyen-lieu
     */
    @PostMapping("/{id}/xoa")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.delete(id);
            flash.addFlashAttribute("success", "Đã xóa nguyên liệu.");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nguyen-lieu";
    }
}
