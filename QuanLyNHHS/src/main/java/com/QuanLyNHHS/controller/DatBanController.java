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

/**
 * Nhận yêu cầu HTTP của module đặt bàn, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/dat-ban")
public class DatBanController {

    // datBanService: thành phần DatBanService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private DatBanService datBanService;
    // khachHangService: thành phần KhachHangService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private KhachHangService khachHangService;
    // banAnService: thành phần BanAnService xử lý nghiệp vụ trước khi đọc/ghi database.
    @Autowired private BanAnService banAnService;

    /**
     * Nạp danh sách đặt bàn cùng dữ liệu lọc/thống kê có trong hàm vào Model rồi trả template.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: dat-ban/list
     */
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

    /**
     * FIX BUG #7: Chỉ hiển thị bàn trống khi thêm mới, hiển thị tất cả khi sửa
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: dat-ban/form
     */
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("datBan", new DatBan());
        model.addAttribute("danhSachKhachHang", khachHangService.findAll());
        model.addAttribute("danhSachBan", banAnService.findBanTrong()); // FIX: chỉ bàn trống
        model.addAttribute("pageTitle", "Đặt bàn mới");
        model.addAttribute("isEdit", false);
        return "dat-ban/form";
    }

    /**
     * FIX BUG #4: Cập nhật trạng thái BanAn → "Đã đặt" khi tạo đặt bàn mới
     * @param maKh mã khách hàng
     * @param maBan mã bàn ăn
     * @param ngayDat ngày đặt bàn
     * @param soNguoi số khách trong lượt đặt bàn
     * @param tienCoc số tiền đặt cọc
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban, redirect:/dat-ban/them
     */
    @PostMapping("/them")
    public String processAdd(@RequestParam Long maKh,
                             @RequestParam Long maBan,
                             @RequestParam(required = false) String ngayDat,
                             @RequestParam Integer soNguoi,
                             @RequestParam(required = false) String tienCoc,
                             RedirectAttributes ra) {
        try {
            // FIX BUG #7: Kiểm tra bàn có trống không trước khi đặt
            // banOpt: Optional chứa bàn được chọn; cần kiểm tra rỗng trước khi lấy bàn.
            var banOpt = banAnService.findById(maBan);
            if (banOpt.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Bàn ăn không tồn tại!");
                return "redirect:/dat-ban";
            }
            // ban: bàn ăn đang được xét.
            var ban = banOpt.get();
            if (!"Trống".equals(ban.getTrangThai())) {
                ra.addFlashAttribute("errorMessage",
                    "Bàn \"" + ban.getTenBan() + "\" hiện đang ở trạng thái \"" + ban.getTrangThai() + "\", không thể đặt!");
                return "redirect:/dat-ban/them";
            }

            // datBan: thông tin đặt bàn đang xử lý.
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
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi đặt bàn: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    /**
     * Tìm bản ghi để điền sẵn form sửa; nếu không tồn tại thì chuyển về danh sách kèm thông báo.
     * @param maDat mã đặt bàn
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban, dat-ban/form
     */
    @GetMapping("/sua/{maDat}")
    public String showEditForm(@PathVariable Long maDat, Model model, RedirectAttributes ra) {
        // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
        Optional<DatBan> opt = datBanService.findById(maDat);
        if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy đặt bàn!"); return "redirect:/dat-ban"; }
        model.addAttribute("datBan", opt.get());
        model.addAttribute("danhSachKhachHang", khachHangService.findAll());
        model.addAttribute("danhSachBan", banAnService.findAll()); // Khi sửa: hiển thị tất cả bàn
        model.addAttribute("pageTitle", "Sửa Đặt bàn");
        model.addAttribute("isEdit", true);
        return "dat-ban/form";
    }

    /**
     * FIX BUG #5: Đồng bộ trạng thái BanAn khi thay đổi trangThai qua form sửa
     * @param maDat mã đặt bàn
     * @param maKh mã khách hàng
     * @param maBan mã bàn ăn
     * @param ngayDat ngày đặt bàn
     * @param soNguoi số khách trong lượt đặt bàn
     * @param tienCoc số tiền đặt cọc
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban
     */
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
            // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
            Optional<DatBan> opt = datBanService.findById(maDat);
            if (opt.isEmpty()) { ra.addFlashAttribute("errorMessage", "Không tìm thấy đặt bàn!"); return "redirect:/dat-ban"; }
            // datBan: thông tin đặt bàn đang xử lý.
            DatBan datBan = opt.get();

            // Lưu bàn cũ để xử lý đồng bộ
            // maBanCu: mã bàn trước khi thay đổi đặt bàn.
            Long maBanCu = (datBan.getBanAn() != null) ? datBan.getBanAn().getMaBan() : null;
            // trangThaiCu: trạng thái đặt bàn trước khi sửa.
            String trangThaiCu = datBan.getTrangThai();

            datBan.setSoNguoi(soNguoi);
            if (ngayDat != null && !ngayDat.isEmpty()) datBan.setNgayDat(LocalDate.parse(ngayDat));
            // FIX: Nếu tienCoc để trống → set null để xóa tiền cọc cũ
            datBan.setTienCoc((tienCoc != null && !tienCoc.isEmpty()) ? new BigDecimal(tienCoc) : null);

            // trangThaiMoi: trạng thái đặt bàn sau khi sửa.
            String trangThaiMoi = (trangThai != null && !trangThai.isEmpty()) ? trangThai : trangThaiCu;
            datBan.setTrangThai(trangThaiMoi);

            khachHangService.findById(maKh).ifPresent(datBan::setKhachHang);
            banAnService.findById(maBan).ifPresent(datBan::setBanAn);
            datBanService.save(datBan);

            // FIX BUG #5: Đồng bộ trạng thái bàn ăn theo trạng thái đặt bàn mới
            syncTrangThaiBan(maBanCu, maBan, trangThaiMoi);

            ra.addFlashAttribute("successMessage", "Cập nhật đặt bàn thành công!");
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    /**
     * Nhận yêu cầu chuyển đặt bàn sang da_xac_nhan và giữ bàn ở trạng thái đã đặt, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param maDat mã đặt bàn
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban
     */
    @PostMapping("/xac-nhan/{maDat}")
    public String xacNhan(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.xacNhan(maDat); ra.addFlashAttribute("successMessage", "Xác nhận đặt bàn thành công!"); }
        catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    /**
     * Gọi service hủy đặt bàn rồi chuyển về danh sách kèm thông báo kết quả.
     * @param maDat mã đặt bàn
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban
     */
    @PostMapping("/huy/{maDat}")
    public String huy(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.huyDatBan(maDat); ra.addFlashAttribute("successMessage", "Đã hủy đặt bàn thành công!"); }
        catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    /**
     * Nhận yêu cầu hoàn thành lượt đặt bàn và trả bàn liên quan về trống, gọi service và chuyển hướng kèm thông báo kết quả.
     * @param maDat mã đặt bàn
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban
     */
    @PostMapping("/hoan-thanh/{maDat}")
    public String hoanThanh(@PathVariable Long maDat, RedirectAttributes ra) {
        try { datBanService.hoanThanh(maDat); ra.addFlashAttribute("successMessage", "Đánh dấu hoàn thành thành công!"); }
        catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) { ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage()); }
        return "redirect:/dat-ban";
    }

    /**
     * FIX BUG #6: Thêm endpoint xóa đặt bàn
     * @param maDat mã đặt bàn
     * @param ra RedirectAttributes, giữ thông báo thành công/lỗi cho request sau khi chuyển hướng
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/dat-ban
     */
    @PostMapping("/xoa/{maDat}")
    public String delete(@PathVariable Long maDat, RedirectAttributes ra) {
        try {
            // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
            Optional<DatBan> opt = datBanService.findById(maDat);
            if (opt.isPresent()) {
                // db: bản ghi đặt bàn đang xử lý.
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
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa đặt bàn: " + e.getMessage());
        }
        return "redirect:/dat-ban";
    }

    /**
     * FIX BUG #5: Đồng bộ trạng thái bàn ăn sau khi cập nhật đặt bàn.
     * - Nếu đổi sang DA_HUY hoặc HOAN_THANH → bàn cũ về "Trống"
     * - Nếu đổi sang DA_XAC_NHAN hoặc CHO_XAC_NHAN → bàn mới thành "Đã đặt"
     * - Nếu đổi bàn (maBanCu != maBan) → bàn cũ về "Trống", bàn mới "Đã đặt"
     * @param maBanCu mã bàn trước khi thay đổi đặt bàn
     * @param maBanMoi mã bàn sau khi thay đổi đặt bàn
     * @param trangThaiMoi trạng thái đặt bàn sau khi sửa
     */
    private void syncTrangThaiBan(Long maBanCu, Long maBanMoi, String trangThaiMoi) {
        // daKetThuc: true khi đặt bàn đã hủy hoặc hoàn thành.
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
