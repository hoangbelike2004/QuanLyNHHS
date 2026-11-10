package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.dto.MonAnForm;
import com.QuanLyNHHS.service.MonAnService;
import com.QuanLyNHHS.service.LoaiMonService;
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
 * Nhận yêu cầu HTTP của module món ăn, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/mon-an")
public class MonAnController {
    // service: thành phần MonAnService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final MonAnService service;
    // loaiMonService: thành phần LoaiMonService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final LoaiMonService loaiMonService;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần MonAnService xử lý nghiệp vụ trước khi đọc/ghi database
     * @param loaiMonService thành phần LoaiMonService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public MonAnController(MonAnService service, LoaiMonService loaiMonService) {
        this.service = service;
        this.loaiMonService = loaiMonService;
    }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("maLoai", "tenMon", "donVi", "donGia", "trangThai", "hinhAnh", "moTa");
    }

    /**
     * Nạp danh sách món ăn cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: monan/index
     */
    @GetMapping
    public String index(Model model) {
        // dishes: danh sách món ăn.
        var dishes = service.findAll();
        model.addAttribute("dishes", dishes);
        model.addAttribute("categories", loaiMonService.findAll());
        model.addAttribute("soldQuantities", service.tongSoLuongDaBan());
        model.addAttribute("totalDishes", dishes.size());
        model.addAttribute("servingDishes", dishes.stream().filter(/* d: món ăn hiện tại trong phép lọc danh sách. */ d -> "Đang phục vụ".equals(d.getTrangThai())).count());
        model.addAttribute("outOfStockDishes", dishes.stream().filter(/* d: món ăn hiện tại trong phép lọc danh sách. */ d -> "Tạm hết".equals(d.getTrangThai())).count());
        model.addAttribute("activeMenu", "monan");
        if (!model.containsAttribute("form")) model.addAttribute("form", new MonAnForm());
        return "monan/index";
    }

    /**
     * Tạo DTO rỗng và mở biểu mẫu thêm mới; chưa lưu dữ liệu vào database.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new MonAnForm());
        return showForm(null, model);
    }

    /**
     * Tìm bản ghi theo mã, đưa dữ liệu hiện tại vào form và mở giao diện sửa.
     * @param id khóa chính của món ăn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/sua/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("form", MonAnForm.from(service.getById(id)));
        return showForm(id, model);
    }

    /**
     * Nhận DTO món ăn, kiểm tra BindingResult rồi gọi service lưu; thành công chuyển hướng, có lỗi thì trả lại form và giữ dữ liệu nhập.
     * @param id khóa chính của món ăn (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng MonAnForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param redirect nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/mon-an
     */
    @PostMapping({"/them", "/sua/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") MonAnForm form, BindingResult errors,
            Model model, RedirectAttributes redirect) {
        if (!errors.hasErrors()) {
            try {
                service.save(id, form);
                redirect.addFlashAttribute("successMessage", "Đã lưu món ăn.");
                return "redirect:/mon-an";
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException | EntityNotFoundException e) {
                errors.reject("save", e.getMessage());
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ DataIntegrityViolationException e) {
                errors.reject("save", "Dữ liệu liên quan vừa thay đổi hoặc không hợp lệ. Hãy kiểm tra lại.");
            }
        }
        return showForm(id, model);
    }

    /**
     * Gọi service xóa món ăn và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param id khóa chính của món ăn
     * @param redirect nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/mon-an
     */
    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("successMessage", "Đã xóa món ăn.");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException | EntityNotFoundException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ DataIntegrityViolationException e) {
            redirect.addFlashAttribute("errorMessage", "Dữ liệu đang được sử dụng, không thể xóa.");
        }
        return "redirect:/mon-an";
    }

    /**
     * Bật hộp thoại thêm/sửa, đặt editingId và formAction rồi nạp lại dữ liệu danh sách để hiển thị cùng form.
     * @param id khóa chính của món ăn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    private String showForm(Long id, Model model) {
        model.addAttribute("showForm", true);
        model.addAttribute("editingId", id);
        model.addAttribute("formAction", id == null ? "/mon-an/them" : "/mon-an/sua/" + id);
        return index(model);
    }
}
