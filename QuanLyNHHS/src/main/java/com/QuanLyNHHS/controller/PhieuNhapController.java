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

/**
 * Nhận yêu cầu HTTP của module phiếu nhập, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/phieu-nhap")
public class PhieuNhapController {
    // service: thành phần PhieuNhapService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final PhieuNhapService service;
    // suppliers: thành phần NhaCungCapService cung cấp dữ liệu cho danh sách lựa chọn trên form.
    private final NhaCungCapService suppliers;
    // materials: thành phần NguyenLieuService cung cấp dữ liệu cho danh sách lựa chọn trên form.
    private final NguyenLieuService materials;
    // employees: thành phần NhanVienNhapKhoRepository cung cấp dữ liệu cho danh sách lựa chọn trên form.
    private final NhanVienNhapKhoRepository employees;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param service thành phần PhieuNhapService xử lý nghiệp vụ trước khi đọc/ghi database
     * @param suppliers thành phần NhaCungCapService cung cấp dữ liệu cho danh sách lựa chọn trên form
     * @param materials thành phần NguyenLieuService cung cấp dữ liệu cho danh sách lựa chọn trên form
     * @param employees thành phần NhanVienNhapKhoRepository cung cấp dữ liệu cho danh sách lựa chọn trên form
     */
    public PhieuNhapController(
            PhieuNhapService service,
            NhaCungCapService suppliers,
            NguyenLieuService materials,
            NhanVienNhapKhoRepository employees) {
        this.service = service;
        this.suppliers = suppliers;
        this.materials = materials;
        this.employees = employees;
    }

    /**
     * Giới hạn các trường được phép gán từ request vào DTO, tránh nhận thêm thuộc tính ngoài dự kiến.
     * @param binder bộ gán dữ liệu request vào form; chỉ các trường được cho phép mới được nhận
     */
    @InitBinder("form")
    void bind(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(100);
        binder.setAllowedFields(
                "maNcc",
                "maNv",
                "ngayNhap",
                "phienBan",
                "chiTiet[*].maNl",
                "chiTiet[*].soLuong",
                "chiTiet[*].donGia");
    }

    /**
     * Nạp danh sách phiếu nhập cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/phieu-nhap-list
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", service.findAll());
        return "kho/phieu-nhap-list";
    }

    /**
     * Tạo DTO rỗng và mở biểu mẫu thêm mới; chưa lưu dữ liệu vào database.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/them")
    public String create(Model model) {
        model.addAttribute("form", new PhieuNhapForm());
        return formPage(null, model);
    }

    /**
     * Đọc phiếu nhập cùng các dòng chi tiết và đưa sang trang xem phiếu.
     * @param id khóa chính của phiếu nhập
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/phieu-nhap-detail
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("phieu", service.findById(id));
        return "kho/phieu-nhap-detail";
    }

    /**
     * Tìm bản ghi theo mã, đưa dữ liệu hiện tại vào form và mở giao diện sửa.
     * @param id khóa chính của phiếu nhập
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng do hàm hiển thị tương ứng cung cấp
     */
    @GetMapping("/{id}/sua")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.getForm(id));
        return formPage(id, model);
    }

    /**
     * Nhận DTO phiếu nhập, kiểm tra BindingResult rồi gọi service lưu; thành công chuyển hướng, có lỗi thì trả lại form và giữ dữ liệu nhập.
     * @param id khóa chính của phiếu nhập (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng PhieuNhapForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @param errors kết quả gán dữ liệu và kiểm tra form; chứa lỗi từng trường hoặc lỗi nghiệp vụ chung
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/phieu-nhap/
     */
    @PostMapping({"/them", "/{id}/sua"})
    public String save(
            @PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") PhieuNhapForm form,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                // receipt: phiếu nhập đang được đọc, tạo hoặc chỉnh sửa.
                PhieuNhap receipt = service.save(id, form);
                flash.addFlashAttribute("success", "Đã lưu phiếu nhập và cập nhật tồn kho.");
                return "redirect:/phieu-nhap/" + receipt.getMaPn();
            } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
                errors.reject("business", e.getMessage());
            }
        }
        return formPage(id, model);
    }

    /**
     * Gọi service xóa phiếu nhập và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param id khóa chính của phiếu nhập
     * @param phienBan số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập
     * @param flash RedirectAttributes, giữ thông báo cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/phieu-nhap
     */
    @PostMapping("/{id}/xoa")
    public String delete(
            @PathVariable Long id, @RequestParam Long phienBan, RedirectAttributes flash) {
        try {
            service.delete(id, phienBan);
            flash.addFlashAttribute("success", "Đã xóa phiếu nhập và trừ lại tồn kho.");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/phieu-nhap";
    }

    /**
     * Bổ sung ít nhất một dòng nhập nếu form trống và nạp nhà cung cấp, nguyên liệu, nhân viên cho các ô lựa chọn.
     * @param id khóa chính của phiếu nhập
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/phieu-nhap-form
     */
    private String formPage(Long id, Model model) {
        // form: đối tượng PhieuNhapForm chứa dữ liệu nhập của biểu mẫu.
        PhieuNhapForm form = (PhieuNhapForm) model.getAttribute("form");
        if (form.getChiTiet().isEmpty()) {
            form.getChiTiet().add(new DongNhapForm());
        }
        model.addAttribute("id", id);
        model.addAttribute("nhaCungCaps", suppliers.findAll());
        model.addAttribute("nguyenLieus", materials.findAll());
        model.addAttribute("nhanViens", employees.findAll());
        return "kho/phieu-nhap-form";
    }
}
