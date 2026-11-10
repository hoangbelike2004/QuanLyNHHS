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

/**
 * Nhận yêu cầu HTTP của module khách hàng, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    // khachHangService: thành phần KhachHangService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private KhachHangService khachHangService;
    // datBanService: thành phần DatBanService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private DatBanService datBanService;

    /**
     * Nạp danh sách khách hàng cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param keyword từ khóa tìm kiếm
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: khach-hang/list
     */
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

    /**
     * Chuẩn bị đối tượng mới cùng các danh sách lựa chọn rồi mở biểu mẫu thêm.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: khach-hang/form
     */
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        model.addAttribute("pageTitle", "Thêm Khách hàng");
        model.addAttribute("isEdit", false);
        return "khach-hang/form";
    }

    /**
     * Nhận dữ liệu thêm mới, gọi service lưu và đặt thông báo kết quả trước khi chuyển hướng.
     * @param khachHang khách hàng gắn với đặt bàn hoặc đang chỉnh sửa
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/khach-hang
     */
    @PostMapping("/them")
    public String processAdd(@ModelAttribute KhachHang khachHang, RedirectAttributes ra) {
        try {
            khachHangService.save(khachHang);
            ra.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi thêm: " + e.getMessage());
        }
        return "redirect:/khach-hang";
    }

    /**
     * Tìm bản ghi để điền sẵn form sửa; nếu không tồn tại thì chuyển về danh sách kèm thông báo.
     * @param maKh mã khách hàng
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/khach-hang, khach-hang/form
     */
    @GetMapping("/sua/{maKh}")
    public String showEditForm(@PathVariable Long maKh, Model model, RedirectAttributes ra) {
        // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
        Optional<KhachHang> opt = khachHangService.findById(maKh);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!"); return "redirect:/khach-hang"; }
        model.addAttribute("khachHang", opt.get());
        model.addAttribute("pageTitle", "Sửa Khách hàng");
        model.addAttribute("isEdit", true);
        return "khach-hang/form";
    }

    /**
     * Nhận dữ liệu sửa, dùng mã lấy từ URL để xác định bản ghi rồi gọi service cập nhật.
     * @param maKh mã khách hàng
     * @param khachHang khách hàng gắn với đặt bàn hoặc đang chỉnh sửa
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/khach-hang
     */
    @PostMapping("/sua/{maKh}")
    public String processEdit(@PathVariable Long maKh, @ModelAttribute KhachHang khachHang, RedirectAttributes ra) {
        try {
            khachHang.setMaKh(maKh);
            khachHangService.save(khachHang);
            ra.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/khach-hang";
    }

    /**
     * Gọi service xóa khách hàng và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param maKh mã khách hàng
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/khach-hang
     */
    @PostMapping("/xoa/{maKh}")
    public String delete(@PathVariable Long maKh, RedirectAttributes ra) {
        try {
            khachHangService.delete(maKh);
            ra.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa (có thể đang có đặt bàn liên quan).");
        }
        return "redirect:/khach-hang";
    }

    /**
     * Tìm khách hàng rồi hiển thị các lượt đặt bàn của khách đó; không tìm thấy thì chuyển hướng kèm thông báo.
     * @param maKh mã khách hàng
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/khach-hang, khach-hang/lich-su-dat-ban
     */
    @GetMapping("/{maKh}/lich-su-dat-ban")
    public String lichSuDatBan(@PathVariable Long maKh, Model model, RedirectAttributes ra) {
        // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
        Optional<KhachHang> opt = khachHangService.findById(maKh);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!"); return "redirect:/khach-hang"; }
        model.addAttribute("khachHang", opt.get());
        model.addAttribute("danhSachDatBan", datBanService.findByKhachHang(maKh));
        model.addAttribute("pageTitle", "Lịch sử Đặt bàn - " + opt.get().getHoTen());
        return "khach-hang/lich-su-dat-ban";
    }
}
