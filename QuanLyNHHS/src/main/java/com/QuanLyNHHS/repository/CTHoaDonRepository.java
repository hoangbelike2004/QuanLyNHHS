package com.QuanLyNHHS.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.CTHoaDon.CTHoaDonId;

public interface CTHoaDonRepository
        extends JpaRepository<CTHoaDon, CTHoaDonId> {

    // Lấy chi tiết hóa đơn theo mã hóa đơn
    List<CTHoaDon> findById_MaHd(Long maHd);

    // Lấy chi tiết theo mã món
    List<CTHoaDon> findById_MaMon(Long maMon);

    // Tính tổng tiền chi tiết của một hóa đơn
    @Query("""
        SELECT COALESCE(SUM(c.thanhTien), 0)
        FROM CTHoaDon c
        WHERE c.id.maHd = :maHd
        """)
    BigDecimal tinhTongTienTheoHoaDon(
            @Param("maHd") Long maHd);

    // Thống kê món bán chạy - chỉ hóa đơn đã thanh toán
    @Query("""
        SELECT c.id.maMon, SUM(c.soLuong), SUM(c.thanhTien)
        FROM CTHoaDon c, HoaDon h
        WHERE c.id.maHd = h.maHd
          AND h.trangThai = :trangThai
        GROUP BY c.id.maMon
        ORDER BY SUM(c.soLuong) DESC
        """)
    List<Object[]> thongKeMonBanChay(
            @Param("trangThai") String trangThai);

    // Thống kê tổng tiền theo hóa đơn - chỉ hóa đơn đã thanh toán
    @Query("""
        SELECT c.id.maHd, SUM(c.thanhTien)
        FROM CTHoaDon c, HoaDon h
        WHERE c.id.maHd = h.maHd
          AND h.trangThai = :trangThai
        GROUP BY c.id.maHd
        ORDER BY c.id.maHd DESC
        """)
    List<Object[]> thongKeTongTienTheoHoaDon(
            @Param("trangThai") String trangThai);

    // Đếm số chi tiết của hóa đơn
    long countById_MaHd(Long maHd);

    // Xóa các chi tiết theo mã hóa đơn
    void deleteById_MaHd(Long maHd);
}