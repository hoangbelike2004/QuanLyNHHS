
package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.CTHoaDonRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HoaDonService {

    private static final String CHUA_THANH_TOAN = "Chua thanh toan";
    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonRepository hoaDonRepository;
    private final CTHoaDonRepository ctHoaDonRepository;

    public HoaDonService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
    }

    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonRepository.findAllByOrderByNgayLapDesc();
    }

    public List<HoaDon> timTheoTrangThai(String trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    public HoaDon timTheoMa(Long maHd) {
        return hoaDonRepository.findById(maHd)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Khong tim thay hoa don: " + maHd));
    }

    public List<CTHoaDon> layChiTietHoaDon(Long maHd) {
        timTheoMa(maHd);
        return ctHoaDonRepository.findById_MaHd(maHd);
    }

    @Transactional
    public HoaDon taoHoaDon(HoaDon hoaDon) {
        if (hoaDon.getMaBan() == null || hoaDon.getMaNv() == null) {
            throw new IllegalArgumentException(
                    "Ma ban va ma nhan vien khong duoc de trong");
        }

        hoaDon.setNgayLap(LocalDate.now());
        hoaDon.setTrangThai(CHUA_THANH_TOAN);
        hoaDon.setTongTien(BigDecimal.ZERO);

        if (hoaDon.getGiamGia() == null) {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        if (hoaDon.getGiamGia().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Giam gia khong duoc am");
        }

        return hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public HoaDon capNhatHoaDon(HoaDon hoaDonMoi) {
        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Khong the sua hoa don da thanh toan");
        }

        hoaDon.setMaBan(hoaDonMoi.getMaBan());
        hoaDon.setMaKh(hoaDonMoi.getMaKh());
        hoaDon.setMaNv(hoaDonMoi.getMaNv());

        if (hoaDonMoi.getGiamGia() != null) {
            if (hoaDonMoi.getGiamGia()
                    .compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Giam gia khong duoc am");
            }

            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        }

        return hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public void xoaHoaDon(Long maHd) {
        HoaDon hoaDon = timTheoMa(maHd);

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Khong the xoa hoa don da thanh toan");
        }

        ctHoaDonRepository.deleteById_MaHd(maHd);
        hoaDonRepository.delete(hoaDon);
    }

    @Transactional
    public HoaDon tinhLaiTongTien(Long maHd) {
        HoaDon hoaDon = timTheoMa(maHd);

        BigDecimal tongChiTiet =
                ctHoaDonRepository.tinhTongTienTheoHoaDon(maHd);

        if (tongChiTiet == null) {
            tongChiTiet = BigDecimal.ZERO;
        }

        BigDecimal giamGia = hoaDon.getGiamGia() == null
                ? BigDecimal.ZERO : hoaDon.getGiamGia();

        BigDecimal tongSauGiam =
                tongChiTiet.subtract(giamGia);

        if (tongSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongSauGiam = BigDecimal.ZERO;
        }

        hoaDon.setTongTien(tongSauGiam);
        return hoaDonRepository.save(hoaDon);
    }

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

    public BigDecimal tinhDoanhThu(
            LocalDate tuNgay,
            LocalDate denNgay) {

        kiemTraKhoangNgay(tuNgay, denNgay);

        return hoaDonRepository.tinhDoanhThu(
                DA_THANH_TOAN,
                tuNgay,
                denNgay.plusDays(1));
    }

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
