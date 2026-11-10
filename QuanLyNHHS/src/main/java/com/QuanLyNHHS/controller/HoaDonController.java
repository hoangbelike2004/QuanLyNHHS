
package com.QuanLyNHHS.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.repository.MonAnRepository;
import com.QuanLyNHHS.service.HoaDonService;

/**
 * Nhận yêu cầu HTTP của module hóa đơn, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {

    // DA_THANH_TOAN: giá trị trạng thái Da thanh toan dùng khi thanh toán và thống kê.
    private static final String DA_THANH_TOAN = "Da thanh toan";

    // hoaDonService: thành phần HoaDonService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final HoaDonService hoaDonService;
    // monAnRepository: thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final MonAnRepository monAnRepository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param hoaDonService thành phần HoaDonService xử lý nghiệp vụ trước khi đọc/ghi database
     * @param monAnRepository thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public HoaDonController(
            HoaDonService hoaDonService,
            MonAnRepository monAnRepository) {

        this.hoaDonService = hoaDonService;
        this.monAnRepository = monAnRepository;
    }

    // Danh sách hóa đơn
    /**
     * Nạp danh sách hóa đơn cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: hoadon/danhsach
     */
    @GetMapping
    public String danhSach(
            @RequestParam(required = false) String trangThai,
            Model model) {

        if (trangThai == null || trangThai.isBlank()) {
            model.addAttribute(
                    "danhSachHoaDon",
                    hoaDonService.layTatCaHoaDon());
        } else {
            model.addAttribute(
                    "danhSachHoaDon",
                    hoaDonService.timTheoTrangThai(trangThai));
        }

        model.addAttribute("trangThai", trangThai);

        return "hoadon/danhsach";
    }

    // Xem chi tiết hóa đơn
    /**
     * Đọc thông tin hóa đơn cùng các món đã gọi và đưa sang trang chi tiết.
     * @param maHd mã hóa đơn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: hoadon/chi-tiet
     */
    @GetMapping("/chi-tiet/{maHd}")
    public String chiTiet(
            @PathVariable Long maHd,
            Model model) {

        model.addAttribute(
                "hoaDon",
                hoaDonService.timTheoMa(maHd));

        model.addAttribute(
                "danhSachChiTiet",
                hoaDonService.layChiTietHoaDon(maHd));

        return "hoadon/chi-tiet";
    }

    // Hiển thị form tạo hóa đơn
    /**
     * Chuẩn bị hóa đơn mới và danh sách món sắp theo tên để mở trang tạo hóa đơn.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: hoadon/form
     */
    @GetMapping("/them")
    public String hienThiFormThem(Model model) {

        model.addAttribute("hoaDon", new HoaDon());

        model.addAttribute(
                "danhSachMonAn",
                monAnRepository.findAll(Sort.by("tenMon").ascending()));

        return "hoadon/form";
    }

    // Tạo hóa đơn và lưu các món ăn
    /**
     * Nhận yêu cầu tạo hóa đơn chưa thanh toán, gộp các dòng trùng món, lưu chi tiết với đơn giá hiện tại rồi tính tổng tiền, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param hoaDon hóa đơn đang xử lý
     * @param maMon danh sách mã món từ form; phần tử thứ i đi cùng soLuong[i]
     * @param soLuong danh sách số lượng từ form; phần tử thứ i đi cùng maMon[i]
     * @param redirectAttributes nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/hoa-don/chi-tiet/, redirect:/hoa-don/them
     */
    @PostMapping("/them")
    public String taoHoaDon(
            @ModelAttribute("hoaDon") HoaDon hoaDon,
            @RequestParam(required = false) List<Long> maMon,
            @RequestParam(required = false) List<BigDecimal> soLuong,
            RedirectAttributes redirectAttributes) {

        try {
            // hoaDonMoi: hóa đơn mới tạo hoặc dữ liệu hóa đơn mới nhận từ form.
            HoaDon hoaDonMoi = hoaDonService.taoHoaDon(
                    hoaDon, maMon, soLuong);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Tao hoa don thanh cong: " + hoaDonMoi.getMaHd());

            return "redirect:/hoa-don/chi-tiet/"
                    + hoaDonMoi.getMaHd();

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don/them";
        }
    }

    // Hiển thị form sửa hóa đơn
    /**
     * Chỉ mở form sửa khi hóa đơn tồn tại và chưa thanh toán; nạp món có thể chọn và các dòng đã gọi.
     * @param maHd mã hóa đơn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param redirectAttributes nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/hoa-don, hoadon/form-sua
     */
    @GetMapping("/sua/{maHd}")
    public String hienThiFormSua(
            @PathVariable Long maHd,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // hoaDon: hóa đơn đang xử lý.
            HoaDon hoaDon = hoaDonService.timTheoMa(maHd);

            if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
                redirectAttributes.addFlashAttribute(
                        "loi",
                        "Khong the sua hoa don da thanh toan.");

                return "redirect:/hoa-don";
            }

            model.addAttribute("hoaDon", hoaDon);

            // Danh sách món ăn để chọn thêm hoặc thay đổi
            model.addAttribute(
                    "danhSachMonAn",
                    monAnRepository.findAll(Sort.by("tenMon").ascending()));

            // Các món đã có trong hóa đơn
            model.addAttribute(
                    "danhSachChiTiet",
                    hoaDonService.layChiTietHoaDon(maHd));

            return "hoadon/form-sua";

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don";
        }
    }

    // Cập nhật hóa đơn và toàn bộ danh sách món ăn
    /**
     * Nhận yêu cầu chỉ cập nhật hóa đơn chưa thanh toán; kiểm tra thông tin, xử lý giảm giá và tính lại tổng tiền, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param maHd mã hóa đơn
     * @param hoaDon hóa đơn đang xử lý
     * @param maMon danh sách mã món từ form; phần tử thứ i đi cùng soLuong[i]
     * @param soLuong danh sách số lượng từ form; phần tử thứ i đi cùng maMon[i]
     * @param redirectAttributes nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/hoa-don/chi-tiet/, redirect:/hoa-don/sua/
     */
    @PostMapping("/sua/{maHd}")
    public String capNhatHoaDon(
            @PathVariable Long maHd,
            @ModelAttribute("hoaDon") HoaDon hoaDon,
            @RequestParam(required = false) List<Long> maMon,
            @RequestParam(required = false) List<BigDecimal> soLuong,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDon.setMaHd(maHd);

            hoaDonService.capNhatHoaDon(
                    hoaDon, maMon, soLuong);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Cap nhat hoa don thanh cong.");

            return "redirect:/hoa-don/chi-tiet/" + maHd;

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don/sua/" + maHd;
        }
    }

    // Xóa hóa đơn
    /**
     * Nhận yêu cầu chỉ xóa hóa đơn chưa thanh toán; xóa chi tiết trước rồi xóa hóa đơn, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param maHd mã hóa đơn
     * @param redirectAttributes nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/hoa-don
     */
    @PostMapping("/xoa/{maHd}")
    public String xoaHoaDon(
            @PathVariable Long maHd,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDonService.xoaHoaDon(maHd);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Xoa hoa don thanh cong.");

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());
        }

        return "redirect:/hoa-don";
    }

    // Thanh toán hóa đơn
    /**
     * Nhận yêu cầu từ chối hóa đơn đã thanh toán hoặc chưa có món; tính lại tổng tiền rồi đổi trạng thái sang da thanh toan, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param maHd mã hóa đơn
     * @param redirectAttributes nơi đặt thông báo dùng sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/hoa-don/chi-tiet/
     */
    @PostMapping("/thanh-toan/{maHd}")
    public String thanhToan(
            @PathVariable Long maHd,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDonService.thanhToan(maHd);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Thanh toan thanh cong.");

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());
        }

        return "redirect:/hoa-don/chi-tiet/" + maHd;
    }
}
