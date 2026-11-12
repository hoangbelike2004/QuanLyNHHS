package com.QuanLyNHHS.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.repository.NhanVienRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import java.util.stream.Collectors;
import com.QuanLyNHHS.service.BanAnService;
import com.QuanLyNHHS.service.DatBanService;
import com.QuanLyNHHS.service.KhachHangService;

@Controller
@RequestMapping("/dat-ban")
public class DatBanController {
    private final DatBanService datBanService;
    private final KhachHangService khachHangService;
    private final BanAnService banAnService;
    private final NhanVienRepository nhanVienRepository;
    private final HoaDonRepository hoaDonRepository;

    public DatBanController(DatBanService datBanService, KhachHangService khachHangService,
                            BanAnService banAnService, NhanVienRepository nhanVienRepository,
                            HoaDonRepository hoaDonRepository) {
        this.datBanService = datBanService;
        this.khachHangService = khachHangService;
        this.banAnService = banAnService;
        this.nhanVienRepository = nhanVienRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String trangThai, Model model) {
        model.addAttribute("danhSachDatBan", datBanService.findByTrangThai(trangThai));
        model.addAttribute("trangThaiFilter", trangThai);
        model.addAttribute("danhSachNhanVien", nhanVienRepository.findAll());
        model.addAttribute("hoaDonTheoDatBan", hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getMaDat() != null)
                .collect(Collectors.toMap(hd -> hd.getMaDat(), hd -> hd, (a, b) -> a)));
        model.addAttribute("pageTitle", "Quản lý Đặt bàn");
        return "dat-ban/list";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        form(model, new DatBan(), false);
        return "dat-ban/form";
    }

    @PostMapping("/them")
    public String processAdd(@RequestParam Long maKh, @RequestParam Long maBan,
                             @RequestParam String ngayDat, @RequestParam Integer soNguoi,
                             @RequestParam(required = false) String tienCoc, RedirectAttributes ra) {
        try {
            datBanService.tao(maKh, maBan, LocalDate.parse(ngayDat), soNguoi, tienCoc(tienCoc));
            ra.addFlashAttribute("successMessage", "Đặt bàn thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi đặt bàn: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @GetMapping("/sua/{maDat}")
    public String showEditForm(@PathVariable Long maDat, Model model, RedirectAttributes ra) {
        DatBan db = datBanService.findById(maDat).orElse(null);
        if (db == null || !"CHO_XAC_NHAN".equals(db.getTrangThai())) {
            ra.addFlashAttribute("errorMessage", "Chỉ có thể sửa đặt bàn đang chờ xác nhận.");
            return "redirect:/dat-ban";
        }
        form(model, db, true);
        return "dat-ban/form";
    }

    @PostMapping("/sua/{maDat}")
    public String processEdit(@PathVariable Long maDat, @RequestParam Long maKh,
                              @RequestParam Long maBan, @RequestParam String ngayDat,
                              @RequestParam Integer soNguoi, @RequestParam(required = false) String tienCoc,
                              RedirectAttributes ra) {
        try {
            datBanService.capNhat(maDat, maKh, maBan, LocalDate.parse(ngayDat), soNguoi, tienCoc(tienCoc));
            ra.addFlashAttribute("successMessage", "Cập nhật đặt bàn thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @PostMapping("/xac-nhan/{maDat}")
    public String xacNhan(@PathVariable Long maDat, @RequestParam Long maNv, RedirectAttributes ra) {
        try {
            datBanService.xacNhan(maDat, maNv);
            ra.addFlashAttribute("successMessage", "Xác nhận đặt bàn thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @PostMapping("/huy/{maDat}")
    public String huy(@PathVariable Long maDat, RedirectAttributes ra) {
        try {
            datBanService.huyDatBan(maDat);
            ra.addFlashAttribute("successMessage", "Đã hủy đặt bàn.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @PostMapping("/xoa/{maDat}")
    public String delete(@PathVariable Long maDat, RedirectAttributes ra) {
        try {
            datBanService.delete(maDat);
            ra.addFlashAttribute("successMessage", "Đã xóa đặt bàn.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    private void form(Model model, DatBan db, boolean isEdit) {
        model.addAttribute("datBan", db);
        model.addAttribute("danhSachKhachHang", khachHangService.findAll());
        model.addAttribute("danhSachBan", banAnService.findBanTrong());
        if (isEdit && db.getBanAn() != null) {
            model.addAttribute("danhSachBan", banAnService.findAll().stream()
                    .filter(b -> "Trống".equals(b.getTrangThai()) || b.getMaBan().equals(db.getBanAn().getMaBan()))
                    .toList());
        }
        model.addAttribute("pageTitle", isEdit ? "Sửa đặt bàn" : "Đặt bàn mới");
        model.addAttribute("isEdit", isEdit);
    }

    private BigDecimal tienCoc(String value) {
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }
}
