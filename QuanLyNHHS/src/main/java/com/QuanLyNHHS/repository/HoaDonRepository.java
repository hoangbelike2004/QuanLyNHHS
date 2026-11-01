
package com.QuanLyNHHS.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.QuanLyNHHS.model.HoaDon;

public interface HoaDonRepository
        extends JpaRepository<HoaDon, Long> {

    // Lấy hóa đơn theo trạng thái
    List<HoaDon> findByTrangThai(String trangThai);

    // Lấy hóa đơn theo mã bàn
    List<HoaDon> findByMaBan(Long maBan);

    // Lấy hóa đơn theo mã nhân viên
    List<HoaDon> findByMaNv(Long maNv);

    // Lấy hóa đơn theo mã khách hàng
    List<HoaDon> findByMaKh(Long maKh);

    // Tìm hóa đơn theo mã và trạng thái
    Optional<HoaDon> findByMaHdAndTrangThai(
            Long maHd, String trangThai);

    // Lấy hóa đơn trong khoảng ngày
    List<HoaDon> findByNgayLapBetween(
            LocalDate tuNgay, LocalDate denNgay);

    // Tính doanh thu trong khoảng ngày
    @Query("""
        SELECT COALESCE(SUM(h.tongTien), 0)
        FROM HoaDon h
        WHERE h.trangThai = :trangThai
          AND h.ngayLap >= :tuNgay
          AND h.ngayLap < :denNgay
        """)
    BigDecimal tinhDoanhThu(
            @Param("trangThai") String trangThai,
            @Param("tuNgay") LocalDate tuNgay,
            @Param("denNgay") LocalDate denNgay);

    // Đếm số hóa đơn theo trạng thái
    long countByTrangThai(String trangThai);

    // Lấy hóa đơn mới nhất
    List<HoaDon> findAllByOrderByNgayLapDesc();
}
