
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
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.MonAnRepository;
import com.QuanLyNHHS.repository.NhanVienRepository;
import com.QuanLyNHHS.service.HoaDonService;

/**
 * Nhận yêu cầu HTTP của module hóa đơn, gọi xử lý
 * và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {

    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonService hoaDonService;
    private final MonAnRepository monAnRepository;
    private final BanAnRepository banAnRepository;
    private final NhanVienRepository nhanVienRepository;

    public HoaDonController(
            HoaDonService hoaDonService,
            MonAnRepository monAnRepository,
            BanAnRepository banAnRepository,
            NhanVienRepository nhanVienRepository) {

        this.hoaDonService = hoaDonService;
        this.monAnRepository = monAnRepository;
        this.banAnRepository = banAnRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    // Danh sách hóa đơn
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
    @GetMapping("/them")
    public String hienThiFormThem(Model model) {

        model.addAttribute("hoaDon", new HoaDon());

        model.addAttribute(
                "danhSachMonAn",
                monAnRepository.findAll(Sort.by("tenMon").ascending()));

        model.addAttribute(
                "danhSachBan",
                banAnRepository.findByTrangThai("Trống"));

        // Danh sách nhân viên có sẵn trong database
        model.addAttribute(
                "danhSachNhanVien",
                nhanVienRepository.findAll());

        return "hoadon/form";
    }

    // Tạo hóa đơn và lưu các món ăn
    @PostMapping("/them")
    public String taoHoaDon(
            @ModelAttribute("hoaDon") HoaDon hoaDon,
            @RequestParam(required = false) List<Long> maMon,
            @RequestParam(required = false) List<BigDecimal> soLuong,
            RedirectAttributes redirectAttributes) {

        try {
            HoaDon hoaDonMoi = hoaDonService.taoHoaDon(
                    hoaDon, maMon, soLuong);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Tao hoa don thanh cong: " + hoaDonMoi.getMaHd());

            return "redirect:/hoa-don/chi-tiet/"
                    + hoaDonMoi.getMaHd();

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don/them";
        }
    }

    // Hiển thị form sửa hóa đơn
    @GetMapping("/sua/{maHd}")
    public String hienThiFormSua(
            @PathVariable Long maHd,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            HoaDon hoaDon = hoaDonService.timTheoMa(maHd);

            if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
                redirectAttributes.addFlashAttribute(
                        "loi",
                        "Khong the sua hoa don da thanh toan.");

                return "redirect:/hoa-don";
            }

            model.addAttribute("hoaDon", hoaDon);

            model.addAttribute(
                    "danhSachMonAn",
                    monAnRepository.findAll(Sort.by("tenMon").ascending()));

            model.addAttribute(
                    "danhSachChiTiet",
                    hoaDonService.layChiTietHoaDon(maHd));

            return "hoadon/form-sua";

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don";
        }
    }

    // Cập nhật hóa đơn và toàn bộ danh sách món ăn
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

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don/sua/" + maHd;
        }
    }

    // Xóa hóa đơn
    @PostMapping("/xoa/{maHd}")
    public String xoaHoaDon(
            @PathVariable Long maHd,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDonService.xoaHoaDon(maHd);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Xoa hoa don thanh cong.");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());
        }

        return "redirect:/hoa-don";
    }

    // Thanh toán hóa đơn
    @PostMapping("/thanh-toan/{maHd}")
    public String thanhToan(
            @PathVariable Long maHd,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDonService.thanhToan(maHd);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Thanh toan thanh cong.");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());
        }

        return "redirect:/hoa-don/chi-tiet/" + maHd;
    }

    @PostMapping("/xac-nhan-mon/{maHd}")
    public String xacNhanMon(@PathVariable Long maHd, RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.xacNhanMon(maHd);
            redirectAttributes.addFlashAttribute("thongBao", "Da xac nhan mon an.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/hoa-don/chi-tiet/" + maHd;
    }
}
