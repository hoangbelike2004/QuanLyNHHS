package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.dto.NhanVienForm;
import com.QuanLyNHHS.service.NhanVienService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Nhận yêu cầu HTTP của module nhân viên, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {
    // service: thành phần NhanVienService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final NhanVienService service;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần NhanVienService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public NhanVienController(NhanVienService service) {
        this.service = service;
    }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("hoTen", "gioiTinh", "sdt", "email", "chucVu", "caLamViec", "luong", "ngayVaoLam", "trangThai");
    }

    /**
     * Nạp danh sách nhân viên cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: nhanvien/index
     */
    @GetMapping
    public String index(Model model) {
        // staffList: danh sách nhân viên.
        var staffList = service.findAll();
        model.addAttribute("staffList", staffList);
        model.addAttribute("totalStaff", staffList.size());
        model.addAttribute("activeStaff", staffList.stream().filter(/* s: nhân viên hiện tại trong phép lọc danh sách. */ s -> "Đang làm việc".equals(s.getTrangThai())).count());
        model.addAttribute("onLeaveStaff", staffList.stream().filter(/* s: nhân viên hiện tại trong phép lọc danh sách. */ s -> "Nghỉ phép".equals(s.getTrangThai())).count());
        model.addAttribute("activeMenu", "nhanvien");
        if (!model.containsAttribute("form")) model.addAttribute("form", new NhanVienForm());
        return "nhanvien/index";
    }

    /**
     * Tạo DTO rỗng và mở biểu mẫu thêm mới; chưa lưu dữ liệu vào database.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new NhanVienForm());
        return showForm(null, model);
    }

    /**
     * Tìm bản ghi theo mã, đưa dữ liệu hiện tại vào form và mở giao diện sửa.
     * @param id khóa chính của nhân viên
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/sua/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("form", NhanVienForm.from(service.getById(id)));
        return showForm(id, model);
    }

    /**
     * Nhận DTO nhân viên, kiểm tra BindingResult rồi gọi service lưu; thành công chuyển hướng, có lỗi thì trả lại form và giữ dữ liệu nhập.
     * @param id khóa chính của nhân viên (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng NhanVienForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param redirect nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nhan-vien
     */
    @PostMapping({"/them", "/sua/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") NhanVienForm form, BindingResult errors,
            Model model, RedirectAttributes redirect) {
        if (!errors.hasErrors()) {
            try {
                service.save(id, form);
                redirect.addFlashAttribute("successMessage", "Đã lưu nhân viên.");
                return "redirect:/nhan-vien";
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException | EntityNotFoundException e) {
                errors.reject("save", e.getMessage());
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ DataIntegrityViolationException e) {
                errors.reject("save", "Dữ liệu liên quan vừa thay đổi hoặc không hợp lệ. Hãy kiểm tra lại.");
            }
        }
        return showForm(id, model);
    }

    /**
     * Gọi service xóa nhân viên và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param id khóa chính của nhân viên
     * @param redirect nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/nhan-vien
     */
    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("successMessage", "Đã xóa nhân viên.");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException | EntityNotFoundException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ DataIntegrityViolationException e) {
            redirect.addFlashAttribute("errorMessage", "Dữ liệu đang được sử dụng, không thể xóa.");
        }
        return "redirect:/nhan-vien";
    }

    /**
     * Bật hộp thoại thêm/sửa, đặt editingId và formAction rồi nạp lại dữ liệu danh sách để hiển thị cùng form.
     * @param id khóa chính của nhân viên
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    private String showForm(Long id, Model model) {
        model.addAttribute("showForm", true);
        model.addAttribute("editingId", id);
        model.addAttribute("formAction", id == null ? "/nhan-vien/them" : "/nhan-vien/sua/" + id);
        return index(model);
    }
}
