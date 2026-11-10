package com.QuanLyNHHS.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.service.BanAnService;

/**
 * Nhận yêu cầu HTTP của module bàn ăn, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/ban-an")
public class BanAnController {

    // banAnService: thành phần BanAnService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private BanAnService banAnService;

    /**
     * Nạp danh sách bàn ăn cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: ban-an/list
     */
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

    /**
     * Chuẩn bị đối tượng mới cùng các danh sách lựa chọn rồi mở biểu mẫu thêm.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: ban-an/form
     */
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("banAn", new BanAn());
        model.addAttribute("pageTitle", "Thêm Bàn ăn");
        model.addAttribute("isEdit", false);
        return "ban-an/form";
    }

    /**
     * Nhận dữ liệu thêm mới, gọi service lưu và đặt thông báo kết quả trước khi chuyển hướng.
     * @param banAn đối tượng bàn ăn liên quan
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/ban-an
     */
    @PostMapping("/them")
    public String processAdd(@ModelAttribute BanAn banAn, RedirectAttributes ra) {
        try {
            banAnService.save(banAn);
            ra.addFlashAttribute("successMessage", "Thêm bàn ăn thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi thêm: " + e.getMessage());
        }
        return "redirect:/ban-an";
    }

    /**
     * Tìm bản ghi để điền sẵn form sửa; nếu không tồn tại thì chuyển về danh sách kèm thông báo.
     * @param maBan mã bàn ăn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/ban-an, ban-an/form
     */
    @GetMapping("/sua/{maBan}")
    public String showEditForm(@PathVariable Long maBan, Model model, RedirectAttributes ra) {
        // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
        Optional<BanAn> opt = banAnService.findById(maBan);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy bàn ăn!"); return "redirect:/ban-an"; }
        model.addAttribute("banAn", opt.get());
        model.addAttribute("pageTitle", "Sửa Bàn ăn");
        model.addAttribute("isEdit", true);
        return "ban-an/form";
    }

    /**
     * Nhận dữ liệu sửa, dùng mã lấy từ URL để xác định bản ghi rồi gọi service cập nhật.
     * @param maBan mã bàn ăn
     * @param banAn đối tượng bàn ăn liên quan
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/ban-an
     */
    @PostMapping("/sua/{maBan}")
    public String processEdit(@PathVariable Long maBan, @ModelAttribute BanAn banAn, RedirectAttributes ra) {
        try {
            banAn.setMaBan(maBan);
            banAnService.save(banAn);
            ra.addFlashAttribute("successMessage", "Cập nhật bàn ăn thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/ban-an";
    }

    /**
     * Gọi service xóa bàn ăn và chuyển hướng kèm thông báo thành công hoặc lỗi; chỉ nhận yêu cầu POST.
     * @param maBan mã bàn ăn
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/ban-an
     */
    @PostMapping("/xoa/{maBan}")
    public String delete(@PathVariable Long maBan, RedirectAttributes ra) {
        try {
            banAnService.delete(maBan);
            ra.addFlashAttribute("successMessage", "Xóa bàn ăn thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa bàn này (có thể đang có đặt bàn liên quan).");
        }
        return "redirect:/ban-an";
    }
}
