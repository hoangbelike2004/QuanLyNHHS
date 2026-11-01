
package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;

@Service
public class HoaDonService {

    private static final String CHUA_THANH_TOAN = "Chua thanh toan";
    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonRepository hoaDonRepository;
    private final CTHoaDonRepository ctHoaDonRepository;
    private final MonAnRepository monAnRepository;

    public HoaDonService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository,
            MonAnRepository monAnRepository) {

        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
        this.monAnRepository = monAnRepository;
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

    // Tạo hóa đơn và lưu chi tiết món ăn
    @Transactional
    public HoaDon taoHoaDon(
            HoaDon hoaDon,
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        if (hoaDon.getMaBan() == null || hoaDon.getMaNv() == null) {
            throw new IllegalArgumentException(
                    "Ma ban va ma nhan vien khong duoc de trong");
        }

        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        hoaDon.setNgayLap(LocalDate.now());
        hoaDon.setTrangThai(CHUA_THANH_TOAN);
        hoaDon.setTongTien(BigDecimal.ZERO);

        if (hoaDon.getGiamGia() == null) {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        kiemTraGiamGia(hoaDon.getGiamGia());

        HoaDon hoaDonMoi = hoaDonRepository.save(hoaDon);

        luuChiTietHoaDon(hoaDonMoi.getMaHd(), danhSachMon);

        return tinhLaiTongTien(hoaDonMoi.getMaHd());
    }

    // Cập nhật thông tin hóa đơn (giữ phương thức cũ)
    @Transactional
    public HoaDon capNhatHoaDon(HoaDon hoaDonMoi) {

        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        kiemTraChuaThanhToan(hoaDon);

        kiemTraThongTinHoaDon(hoaDonMoi);

        hoaDon.setMaBan(hoaDonMoi.getMaBan());
        hoaDon.setMaKh(hoaDonMoi.getMaKh());
        hoaDon.setMaNv(hoaDonMoi.getMaNv());

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

        kiemTraThongTinHoaDon(hoaDonMoi);

        // Kiểm tra danh sách món trước khi thay đổi dữ liệu
        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        // Cập nhật thông tin hóa đơn
        hoaDon.setMaBan(hoaDonMoi.getMaBan());
        hoaDon.setMaKh(hoaDonMoi.getMaKh());
        hoaDon.setMaNv(hoaDonMoi.getMaNv());

        if (hoaDonMoi.getGiamGia() != null) {
            kiemTraGiamGia(hoaDonMoi.getGiamGia());
            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        } else {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        hoaDonRepository.save(hoaDon);

        // Xóa chi tiết món ăn cũ
        ctHoaDonRepository.deleteById_MaHd(hoaDon.getMaHd());

        // Lưu lại danh sách món ăn mới
        luuChiTietHoaDon(hoaDon.getMaHd(), danhSachMon);

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
                    || soLuongMon.compareTo(BigDecimal.ZERO) <= 0) {

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
            Map<Long, BigDecimal> danhSachMon) {

        for (Map.Entry<Long, BigDecimal> entry
                : danhSachMon.entrySet()) {

            Long idMon = entry.getKey();
            BigDecimal soLuongMon = entry.getValue();

            MonAn monAn = monAnRepository.findById(idMon)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Khong tim thay mon an co ma: " + idMon));

            if (monAn.getDonGia() == null) {
                throw new IllegalArgumentException(
                        "Mon an chua co don gia: " + idMon);
            }

            BigDecimal donGia = monAn.getDonGia();
            BigDecimal thanhTien = donGia.multiply(soLuongMon);

            CTHoaDon chiTiet = new CTHoaDon();

            chiTiet.setId(
                    new CTHoaDon.CTHoaDonId(maHd, idMon));

            chiTiet.setSoLuong(soLuongMon);
            chiTiet.setDonGia(donGia);
            chiTiet.setThanhTien(thanhTien);

            ctHoaDonRepository.save(chiTiet);
        }
    }

    // Kiểm tra hóa đơn chưa thanh toán
    private void kiemTraChuaThanhToan(HoaDon hoaDon) {

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Khong the sua hoac xoa hoa don da thanh toan");
        }
    }

    // Kiểm tra thông tin hóa đơn
    private void kiemTraThongTinHoaDon(HoaDon hoaDon) {

        if (hoaDon.getMaBan() == null || hoaDon.getMaNv() == null) {
            throw new IllegalArgumentException(
                    "Ma ban va ma nhan vien khong duoc de trong");
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
