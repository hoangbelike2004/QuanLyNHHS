
package com.QuanLyNHHS.controller;

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
import com.QuanLyNHHS.service.HoaDonService;

@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {

    private static final String CHUA_THANH_TOAN = "Chua thanh toan";
    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonService hoaDonService;

    public HoaDonController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
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
        return "hoadon/form";
    }

    // Tạo hóa đơn
    @PostMapping("/them")
    public String taoHoaDon(
            @ModelAttribute("hoaDon") HoaDon hoaDon,
            RedirectAttributes redirectAttributes) {

        try {
            HoaDon hoaDonMoi = hoaDonService.taoHoaDon(hoaDon);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Tạo hóa đơn thành công: " + hoaDonMoi.getMaHd());

            return "redirect:/hoa-don/chi-tiet/"
                    + hoaDonMoi.getMaHd();

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don/them";
        }
    }

    // Hiển thị form sửa hóa đơn chưa thanh toán
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
                        "Không thể sửa hóa đơn đã thanh toán.");

                return "redirect:/hoa-don";
            }

            model.addAttribute("hoaDon", hoaDon);
            return "hoadon/form-sua";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());

            return "redirect:/hoa-don";
        }
    }

    // Lưu thông tin sửa hóa đơn
    @PostMapping("/sua/{maHd}")
    public String capNhatHoaDon(
            @PathVariable Long maHd,
            @ModelAttribute("hoaDon") HoaDon hoaDon,
            RedirectAttributes redirectAttributes) {

        try {
            hoaDon.setMaHd(maHd);

            hoaDonService.capNhatHoaDon(hoaDon);

            redirectAttributes.addFlashAttribute(
                    "thongBao",
                    "Cập nhật hóa đơn thành công.");

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
                    "thongBao", "Xóa hóa đơn thành công.");

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
                    "thongBao", "Thanh toán thành công.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "loi", e.getMessage());
        }

        return "redirect:/hoa-don/chi-tiet/" + maHd;
    }
}
