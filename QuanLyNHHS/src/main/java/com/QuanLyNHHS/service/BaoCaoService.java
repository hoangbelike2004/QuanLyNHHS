
package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;

@Service
public class BaoCaoService {

    private static final String DA_THANH_TOAN = "Da thanh toan";

    private final HoaDonRepository hoaDonRepository;
    private final CTHoaDonRepository ctHoaDonRepository;

    public BaoCaoService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
    }

    public BigDecimal doanhThuTheoNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        kiemTraNgay(tuNgay, denNgay);

        BigDecimal doanhThu = hoaDonRepository.tinhDoanhThu(
                DA_THANH_TOAN,
                tuNgay,
                denNgay.plusDays(1));

        return doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }

    public BigDecimal doanhThuHomNay() {
        LocalDate homNay = LocalDate.now();
        return doanhThuTheoNgay(homNay, homNay);
    }

    public BigDecimal doanhThuThangNay() {
        LocalDate homNay = LocalDate.now();
        LocalDate dauThang = homNay.withDayOfMonth(1);

        return doanhThuTheoNgay(dauThang, homNay);
    }

    public long soHoaDonDaThanhToan() {
        return hoaDonRepository.countByTrangThai(DA_THANH_TOAN);
    }

    public List<Object[]> thongKeMonBanChay() {
        return ctHoaDonRepository.thongKeMonBanChay(DA_THANH_TOAN);
    }

    public List<Object[]> thongKeTongTienTheoHoaDon() {
        return ctHoaDonRepository.thongKeTongTienTheoHoaDon(DA_THANH_TOAN);
    }

    private void kiemTraNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException(
                    "Vui long chon ngay bat dau va ngay ket thuc");
        }

        if (denNgay.isBefore(tuNgay)) {
            throw new IllegalArgumentException(
                    "Ngay ket thuc khong duoc truoc ngay bat dau");
        }
    }
}
