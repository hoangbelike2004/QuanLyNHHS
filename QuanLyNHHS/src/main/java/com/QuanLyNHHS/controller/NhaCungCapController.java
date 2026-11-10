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

/**
 * Nhận yêu cầu HTTP của module nhà cung cấp, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/nha-cung-cap")
public class NhaCungCapController {
    // service: thành phần NhaCungCapService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final NhaCungCapService service;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần NhaCungCapService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public NhaCungCapController(NhaCungCapService service) {
        this.service = service;
    }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("tenNcc", "sdt", "diaChi", "email");
    }

    /**
     * Nạp danh sách nhà cung cấp cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nha-cung-cap-list
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", service.findAll());
        return "kho/nha-cung-cap-list";
    }

    /**
     * Tạo DTO rỗng và mở biểu mẫu thêm mới; chưa lưu dữ liệu vào database.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nha-cung-cap-form
     */
    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new NhaCungCapForm());
        model.addAttribute("id", null);
        return "kho/nha-cung-cap-form";
    }

    /**
     * Tìm bản ghi theo mã, đưa dữ liệu hiện tại vào form và mở giao diện sửa.
     * @param id khóa chính của nhà cung cấp
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/nha-cung-cap-form
     */
    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        // supplier: nhà cung cấp được chọn hoặc đang chỉnh sửa.
        NhaCungCap supplier = service.findById(id);
        // form: đối tượng NhaCungCapForm chứa dữ liệu nhập của biểu mẫu.
        NhaCungCapForm form = new NhaCungCapForm();
        form.setTenNcc(supplier.getTenNcc());
        form.setSdt(supplier.getSdt());
        form.setDiaChi(supplier.getDiaChi());
        form.setEmail(supplier.getEmail());
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        return "kho/nha-cung-cap-form";
    }

    /**
     * Nhận DTO nhà cung cấp, kiểm tra BindingResult rồi gọi service lưu; thành công chuyển hướng, có lỗi thì trả lại form và giữ dữ liệu nhập.
     * @param id khóa chính của nhà cung cấp (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng NhaCungCapForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nha-cung-cap, kho/nha-cung-cap-form
     */
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
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
                errors.reject("business", e.getMessage());
            }
        }
        model.addAttribute("id", id);
        return "kho/nha-cung-cap-form";
    }

    /**
     * Gọi service xóa nhà cung cấp và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param id khóa chính của nhà cung cấp
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nha-cung-cap
     */
    @PostMapping("/{id}/xoa")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.delete(id);
            flash.addFlashAttribute("success", "Đã xóa nhà cung cấp.");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nha-cung-cap";
    }
}
