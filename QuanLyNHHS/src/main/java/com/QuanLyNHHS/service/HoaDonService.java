
package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.DatBanRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;

@Service
public class HoaDonService {

    private static final String CHUA_THANH_TOAN = "Chua thanh toan";
    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonRepository hoaDonRepository;
    private final CTHoaDonRepository ctHoaDonRepository;
    private final MonAnRepository monAnRepository;
    private final DatBanRepository datBanRepository;
    private final BanAnRepository banAnRepository;

    public HoaDonService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository,
            MonAnRepository monAnRepository,
            DatBanRepository datBanRepository,
            BanAnRepository banAnRepository) {

        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
        this.monAnRepository = monAnRepository;
        this.datBanRepository = datBanRepository;
        this.banAnRepository = banAnRepository;
    }

    // Lấy tất cả hóa đơn
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonRepository.findAllByOrderByNgayLapDesc();
    }

    // Tìm hóa đơn theo trạng thái
    public List<HoaDon> timTheoTrangThai(String trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    // Tìm hóa đơn theo mã
    public HoaDon timTheoMa(Long maHd) {
        return hoaDonRepository.findById(maHd)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Khong tim thay hoa don: " + maHd));
    }

    // Lấy chi tiết hóa đơn
    public List<CTHoaDon> layChiTietHoaDon(Long maHd) {
        timTheoMa(maHd);
        return ctHoaDonRepository.findById_MaHd(maHd);
    }

    public List<DatBan> layDatBanCoTheGoiMon() {
        return datBanRepository.findByTrangThai("DA_XAC_NHAN").stream()
                .filter(db -> db.getMaNv() != null && db.getBanAn() != null
                        && db.getKhachHang() != null && !hoaDonRepository.existsByMaDat(db.getMaDat()))
                .toList();
    }

    // Tạo hóa đơn và lưu chi tiết món ăn
    @Transactional
    public HoaDon taoHoaDon(
            HoaDon hoaDon,
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        if (hoaDon.getMaDat() == null) {
            throw new IllegalArgumentException("Vui lòng chọn đặt bàn đã xác nhận");
        }
        DatBan datBan = datBanRepository.findForUpdate(hoaDon.getMaDat())
                .orElseThrow(() -> new IllegalArgumentException("Đặt bàn không tồn tại"));
        if (!"DA_XAC_NHAN".equals(datBan.getTrangThai()) || datBan.getMaNv() == null
                || datBan.getBanAn() == null || datBan.getKhachHang() == null) {
            throw new IllegalStateException("Đặt bàn chưa được xác nhận hợp lệ");
        }
        if (hoaDonRepository.existsByMaDat(datBan.getMaDat())) {
            throw new IllegalStateException("Đặt bàn đã có hóa đơn");
        }
        hoaDon.setMaBan(datBan.getBanAn().getMaBan());
        hoaDon.setMaNv(datBan.getMaNv());
        hoaDon.setMaKh(datBan.getKhachHang().getMaKh());
        hoaDon.setMaHd(null);

        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        hoaDon.setNgayLap(LocalDate.now());
        hoaDon.setTrangThai(CHUA_THANH_TOAN);
        hoaDon.setTongTien(BigDecimal.ZERO);

        if (hoaDon.getGiamGia() == null) {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        kiemTraGiamGia(hoaDon.getGiamGia());

        var ban = banAnRepository.findForUpdate(hoaDon.getMaBan())
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        if (!"Đã đặt".equals(ban.getTrangThai())) {
            throw new IllegalStateException("Bàn không còn ở trạng thái đã đặt");
        }
        ban.setTrangThai("Đang dùng");

        HoaDon hoaDonMoi = hoaDonRepository.save(hoaDon);

        luuChiTietHoaDon(hoaDonMoi.getMaHd(), danhSachMon, new LinkedHashMap<>());

        return tinhLaiTongTien(hoaDonMoi.getMaHd());
    }

    // Cập nhật thông tin hóa đơn (giữ phương thức cũ)
    @Transactional
    public HoaDon capNhatHoaDon(HoaDon hoaDonMoi) {

        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        kiemTraChuaThanhToan(hoaDon);

        capNhatNguoiPhuTrach(hoaDon);

        if (hoaDonMoi.getGiamGia() != null) {
            kiemTraGiamGia(hoaDonMoi.getGiamGia());
            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        }

        hoaDonRepository.save(hoaDon);

        return tinhLaiTongTien(hoaDon.getMaHd());
    }

    // Cập nhật hóa đơn và danh sách món ăn
    @Transactional
    public HoaDon capNhatHoaDon(
            HoaDon hoaDonMoi,
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        kiemTraChuaThanhToan(hoaDon);

        // Kiểm tra danh sách món trước khi thay đổi dữ liệu
        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        // Cập nhật thông tin hóa đơn
        capNhatNguoiPhuTrach(hoaDon);

        if (hoaDonMoi.getGiamGia() != null) {
            kiemTraGiamGia(hoaDonMoi.getGiamGia());
            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        } else {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        hoaDonRepository.save(hoaDon);

        Map<Long, CTHoaDon> monDaGoi = ctHoaDonRepository.findById_MaHd(hoaDon.getMaHd()).stream()
                .collect(Collectors.toMap(CTHoaDon::getMaMon, ct -> ct));
        luuChiTietHoaDon(hoaDon.getMaHd(), danhSachMon, monDaGoi);

        // Tính lại tổng tiền
        return tinhLaiTongTien(hoaDon.getMaHd());
    }

    // Xóa hóa đơn
    @Transactional
    public void xoaHoaDon(Long maHd) {

        HoaDon hoaDon = timTheoMa(maHd);

        kiemTraChuaThanhToan(hoaDon);

        ctHoaDonRepository.deleteById_MaHd(maHd);

        hoaDonRepository.delete(hoaDon);
        if (hoaDon.getMaDat() != null) {
            var ban = banAnRepository.findForUpdate(hoaDon.getMaBan())
                    .orElseThrow(() -> new IllegalStateException("Bàn của hóa đơn không tồn tại"));
            if (!"Đang dùng".equals(ban.getTrangThai())) {
                throw new IllegalStateException("Trạng thái bàn không khớp với hóa đơn");
            }
            ban.setTrangThai("Đã đặt");
        }
    }

    // Tính lại tổng tiền
    @Transactional
    public HoaDon tinhLaiTongTien(Long maHd) {

        HoaDon hoaDon = timTheoMa(maHd);

        BigDecimal tongChiTiet =
                ctHoaDonRepository.tinhTongTienTheoHoaDon(maHd);

        if (tongChiTiet == null) {
            tongChiTiet = BigDecimal.ZERO;
        }

        BigDecimal giamGia = hoaDon.getGiamGia() == null
                ? BigDecimal.ZERO
                : hoaDon.getGiamGia();

        BigDecimal tongSauGiam =
                tongChiTiet.subtract(giamGia);

        if (tongSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongSauGiam = BigDecimal.ZERO;
        }

        hoaDon.setTongTien(tongSauGiam);

        return hoaDonRepository.save(hoaDon);
    }

    // Thanh toán hóa đơn
    @Transactional
    public HoaDon thanhToan(Long maHd) {

        HoaDon hoaDon = timTheoMa(maHd);

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Hoa don da duoc thanh toan");
        }

        if (ctHoaDonRepository.countById_MaHd(maHd) == 0) {
            throw new IllegalStateException(
                    "Hoa don chua co mon an");
        }

        tinhLaiTongTien(maHd);

        hoaDon.setTrangThai(DA_THANH_TOAN);

        if (hoaDon.getMaDat() != null) {
            DatBan datBan = datBanRepository.findForUpdate(hoaDon.getMaDat())
                    .orElseThrow(() -> new IllegalStateException("Đặt bàn của hóa đơn không tồn tại"));
            if (!"DA_XAC_NHAN".equals(datBan.getTrangThai())) {
                throw new IllegalStateException("Đặt bàn không ở trạng thái đang phục vụ");
            }
            datBan.setTrangThai("HOAN_THANH");
            var ban = banAnRepository.findForUpdate(hoaDon.getMaBan())
                    .orElseThrow(() -> new IllegalStateException("Bàn của hóa đơn không tồn tại"));
            if (!"Đang dùng".equals(ban.getTrangThai())) {
                throw new IllegalStateException("Trạng thái bàn không khớp với hóa đơn");
            }
            ban.setTrangThai("Trống");
        }

        return hoaDonRepository.save(hoaDon);
    }

    // Tính doanh thu theo khoảng ngày
    public BigDecimal tinhDoanhThu(
            LocalDate tuNgay,
            LocalDate denNgay) {

        kiemTraKhoangNgay(tuNgay, denNgay);

        return hoaDonRepository.tinhDoanhThu(
                DA_THANH_TOAN,
                tuNgay,
                denNgay.plusDays(1));
    }

    // Kiểm tra và gộp danh sách món ăn
    private Map<Long, BigDecimal> kiemTraVaGopDanhSachMon(
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        if (maMon == null || soLuong == null
                || maMon.isEmpty()
                || maMon.size() != soLuong.size()) {

            throw new IllegalArgumentException(
                    "Hoa don phai co it nhat mot mon an hop le");
        }

        Map<Long, BigDecimal> danhSachMon =
                new LinkedHashMap<>();

        for (int i = 0; i < maMon.size(); i++) {

            Long idMon = maMon.get(i);
            BigDecimal soLuongMon = soLuong.get(i);

            if (idMon == null || soLuongMon == null
                    || soLuongMon.compareTo(BigDecimal.ZERO) <= 0
                    || soLuongMon.stripTrailingZeros().scale() > 0) {

                throw new IllegalArgumentException(
                        "Ma mon va so luong phai hop le");
            }

            danhSachMon.merge(
                    idMon,
                    soLuongMon,
                    BigDecimal::add);
        }

        return danhSachMon;
    }

    // Lưu chi tiết món ăn vào CT_HOA_DON
    private void luuChiTietHoaDon(
            Long maHd,
            Map<Long, BigDecimal> danhSachMon,
            Map<Long, CTHoaDon> monDaGoi) {

        for (Map.Entry<Long, BigDecimal> entry
                : danhSachMon.entrySet()) {

            Long idMon = entry.getKey();
            BigDecimal soLuongMon = entry.getValue();

            MonAn monAn = monAnRepository.findById(idMon)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Khong tim thay mon an co ma: " + idMon));

            CTHoaDon chiTiet = monDaGoi.remove(idMon);
            BigDecimal donGia = chiTiet == null ? monAn.getDonGia() : chiTiet.getDonGia();
            if (donGia == null) {
                throw new IllegalArgumentException(
                        "Mon an chua co don gia: " + idMon);
            }
            if (!"Đang phục vụ".equals(monAn.getTrangThai()) && chiTiet == null) {
                throw new IllegalStateException("Món ăn hiện không phục vụ: " + idMon);
            }

            BigDecimal thanhTien = donGia.multiply(soLuongMon);

            if (chiTiet == null) {
                chiTiet = new CTHoaDon();
                chiTiet.setId(new CTHoaDon.CTHoaDonId(maHd, idMon));
            }

            chiTiet.setSoLuong(soLuongMon);
            chiTiet.setDonGia(donGia);
            chiTiet.setThanhTien(thanhTien);

            ctHoaDonRepository.save(chiTiet);
        }
        monDaGoi.values().forEach(ctHoaDonRepository::delete);
    }

    // Kiểm tra hóa đơn chưa thanh toán
    private void kiemTraChuaThanhToan(HoaDon hoaDon) {

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Khong the sua hoac xoa hoa don da thanh toan");
        }
    }

    private void capNhatNguoiPhuTrach(HoaDon hoaDon) {
        if (hoaDon.getMaDat() != null) {
            DatBan datBan = datBanRepository.findForUpdate(hoaDon.getMaDat())
                    .orElseThrow(() -> new IllegalStateException("Đặt bàn không tồn tại"));
            if (!"DA_XAC_NHAN".equals(datBan.getTrangThai())) {
                throw new IllegalStateException("Đặt bàn không còn được phục vụ");
            }
            hoaDon.setMaBan(datBan.getBanAn().getMaBan());
            hoaDon.setMaKh(datBan.getKhachHang().getMaKh());
            hoaDon.setMaNv(datBan.getMaNv());
        }
    }

    // Kiểm tra giảm giá
    private void kiemTraGiamGia(BigDecimal giamGia) {

        if (giamGia == null
                || giamGia.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Giam gia khong duoc am");
        }
    }

    // Kiểm tra khoảng ngày
    private void kiemTraKhoangNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException(
                    "Ngay bat dau va ngay ket thuc khong duoc trong");
        }

        if (denNgay.isBefore(tuNgay)) {
            throw new IllegalArgumentException(
                    "Ngay ket thuc phai sau ngay bat dau");
        }
    }
}
