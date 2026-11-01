package com.QuanLyNHHS.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.service.BanAnService;
import com.QuanLyNHHS.service.DatBanService;
import com.QuanLyNHHS.service.KhachHangService;

@Controller
@RequestMapping("/dat-ban")
public class DatBanController {

    @Autowired private DatBanService datBanService;
    @Autowired private KhachHangService khachHangService;
    @Autowired private BanAnService banAnService;

    @GetMapping
    public String list(@RequestParam(required = false) String trangThai, Model model) {
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            model.addAttribute("danhSachDatBan", datBanService.findByTrangThai(trangThai));
            model.addAttribute("trangThaiFilter", trangThai);
        } else {
            model.addAttribute("danhSachDatBan", datBanService.findAll());
        }
        model.addAttribute("pageTitle", "Quản lý Đặt bàn");
        return "dat-ban/list";
    }

    /** FIX BUG #7: Chỉ hiển thị bàn trống khi thêm mới, hiển thị tất cả khi sửa */
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("datBan", new DatBan());
        model.addAttribute("danhSachKhachHang", khachHangService.findAll());
        model.addAttribute("danhSachBan", banAnService.findBanTrong()); // FIX: chỉ bàn trống
        model.addAttribute("pageTitle", "Đặt bàn mới");
        model.addAttribute("isEdit", false);
        return "dat-ban/form";
    }

    /** FIX BUG #4: Cập nhật trạng thái BanAn → "Đã đặt" khi tạo đặt bàn mới */
    @PostMapping("/them")
    public String processAdd(@RequestParam Long maKh,
                             @RequestParam Long maBan,
                             @RequestParam(required = false) String ngayDat,
                             @RequestParam Integer soNguoi,
                             @RequestParam(required = false) String tienCoc,
                             RedirectAttributes ra) {
        try {
            // FIX BUG #7: Kiểm tra bàn có trống không trước khi đặt
            var banOpt = banAnService.findById(maBan);
            if (banOpt.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Bàn ăn không tồn tại!");
                return "redirect:/dat-ban";
            }
            var ban = banOpt.get();
            if (!"Trống".equals(ban.getTrangThai())) {
                ra.addFlashAttribute("errorMessage",
                    "Bàn \"" + ban.getTenBan() + "\" hiện đang ở trạng thái \"" + ban.getTrangThai() + "\", không thể đặt!");
                return "redirect:/dat-ban/them";
            }

            DatBan datBan = new DatBan();
            datBan.setTrangThai("CHO_XAC_NHAN");
            datBan.setSoNguoi(soNguoi);
            datBan.setNgayDat(ngayDat != null && !ngayDat.isEmpty() ? LocalDate.parse(ngayDat) : LocalDate.now());
            if (tienCoc != null && !tienCoc.isEmpty()) datBan.setTienCoc(new BigDecimal(tienCoc));

            khachHangService.findById(maKh).ifPresent(datBan::setKhachHang);
            datBan.setBanAn(ban);

            datBanService.save(datBan);

            // FIX BUG #4: Cập nhật trạng thái bàn → "Đã đặt"
            banAnService.capNhatTrangThai(maBan, "Đã đặt");

            ra.addFlashAttribute("successMessage", "Đặt bàn thành công! Bàn \"" + ban.getTenBan() + "\" đã được giữ.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi đặt bàn: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @GetMapping("/sua/{maDat}")
    public String showEditForm(@PathVariable Long maDat, Model model, RedirectAttributes ra) {
        Optional<DatBan> opt = datBanService.findById(maDat);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy đặt bàn!"); return "redirect:/dat-ban"; }
        model.addAttribute("datBan", opt.get());
        model.addAttribute("danhSachKhachHang", khachHangService.findAll());
        model.addAttribute("danhSachBan", banAnService.findAll()); // Khi sửa: hiển thị tất cả bàn
        model.addAttribute("pageTitle", "Sửa Đặt bàn");
        model.addAttribute("isEdit", true);
        return "dat-ban/form";
    }

    /** FIX BUG #5: Đồng bộ trạng thái BanAn khi thay đổi trangThai qua form sửa */
    @PostMapping("/sua/{maDat}")
    public String processEdit(@PathVariable Long maDat,
                              @RequestParam Long maKh,
                              @RequestParam Long maBan,
                              @RequestParam(required = false) String ngayDat,
                              @RequestParam Integer soNguoi,
                              @RequestParam(required = false) String tienCoc,
                              @RequestParam(required = false) String trangThai,
                              RedirectAttributes ra) {
        try {
            Optional<DatBan> opt = datBanService.findById(maDat);
            if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy đặt bàn!"); return "redirect:/dat-ban"; }
            DatBan datBan = opt.get();

            // Lưu bàn cũ để xử lý đồng bộ
            Long maBanCu = (datBan.getBanAn() != null) ? datBan.getBanAn().getMaBan() : null;
            String trangThaiCu = datBan.getTrangThai();

            datBan.setSoNguoi(soNguoi);
            if (ngayDat != null && !ngayDat.isEmpty()) datBan.setNgayDat(LocalDate.parse(ngayDat));
            // FIX: Nếu tienCoc để trống → set null để xóa tiền cọc cũ
            datBan.setTienCoc((tienCoc != null && !tienCoc.isEmpty()) ? new BigDecimal(tienCoc) : null);

            String trangThaiMoi = (trangThai != null && !trangThai.isEmpty()) ? trangThai : trangThaiCu;
            datBan.setTrangThai(trangThaiMoi);

            khachHangService.findById(maKh).ifPresent(datBan::setKhachHang);
            banAnService.findById(maBan).ifPresent(datBan::setBanAn);
            datBanService.save(datBan);

            // FIX BUG #5: Đồng bộ trạng thái bàn ăn theo trạng thái đặt bàn mới
            syncTrangThaiBan(maBanCu, maBan, trangThaiMoi);

            ra.addFlashAttribute("successMessage", "Cập nhật đặt bàn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    @PostMapping("/xac-nhan/{maDat}")
    public String xacNhan(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.xacNhan(maDat); ra.addFlashAttribute("successMessage", "Xác nhận đặt bàn thành công!"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    @PostMapping("/huy/{maDat}")
    public String huy(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.huyDatBan(maDat); ra.addFlashAttribute("successMessage", "Đã hủy đặt bàn thành công!"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    @PostMapping("/hoan-thanh/{maDat}")
    public String hoanThanh(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.hoanThanh(maDat); ra.addFlashAttribute("successMessage", "Đánh dấu hoàn thành thành công!"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    /** FIX BUG #6: Thêm endpoint xóa đặt bàn */
    @PostMapping("/xoa/{maDat}")
    public String delete(@PathVariable Long maDat, RedirectAttributes ra) {
        try {
            Optional<DatBan> opt = datBanService.findById(maDat);
            if (opt.isPresent()) {
                DatBan db = opt.get();
                // Nếu đặt bàn chưa bị hủy/hoàn thành → reset bàn về Trống
                if (db.getBanAn() != null &&
                    !"DA_HUY".equals(db.getTrangThai()) &&
                    !"HOAN_THANH".equals(db.getTrangThai())) {
                    banAnService.capNhatTrangThai(db.getBanAn().getMaBan(), "Trống");
                }
            }
            datBanService.delete(maDat);
            ra.addFlashAttribute("successMessage", "Đã xóa đặt bàn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa đặt bàn: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    /**
     * FIX BUG #5: Đồng bộ trạng thái bàn ăn sau khi cập nhật đặt bàn.
     * - Nếu đổi sang DA_HUY hoặc HOAN_THANH → bàn cũ về "Trống"
     * - Nếu đổi sang DA_XAC_NHAN hoặc CHO_XAC_NHAN → bàn mới thành "Đã đặt"
     * - Nếu đổi bàn (maBanCu != maBan) → bàn cũ về "Trống", bàn mới "Đã đặt"
     */
    private void syncTrangThaiBan(Long maBanCu, Long maBanMoi, String trangThaiMoi) {
        boolean daKetThuc = "DA_HUY".equals(trangThaiMoi) || "HOAN_THANH".equals(trangThaiMoi);

        if (daKetThuc) {
            // Đặt bàn kết thúc → trả bàn cũ về Trống
            if (maBanCu != null) banAnService.capNhatTrangThai(maBanCu, "Trống");
        } else {
            // Đặt bàn còn hoạt động
            if (maBanCu != null && !maBanCu.equals(maBanMoi)) {
                // Đổi sang bàn khác → bàn cũ về Trống, bàn mới thành Đã đặt
                banAnService.capNhatTrangThai(maBanCu, "Trống");
                banAnService.capNhatTrangThai(maBanMoi, "Đã đặt");
            } else if (maBanCu == null) {
                // Lần đầu gán bàn
                banAnService.capNhatTrangThai(maBanMoi, "Đã đặt");
            }
            // Cùng bàn, cùng trạng thái hoạt động → không cần thay đổi
        }
    }
}
