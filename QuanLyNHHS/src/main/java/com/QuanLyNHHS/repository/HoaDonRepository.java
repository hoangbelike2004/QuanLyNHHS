
package com.QuanLyNHHS.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.QuanLyNHHS.model.HoaDon;

/**
 * Truy cập dữ liệu hóa đơn bằng Spring Data JPA.
 */
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    /**
     * Kiểm tra có chứng từ dùng mã nhân viên này hay không.
     */
    boolean existsByMaNv(Long maNv);

    // Lấy hóa đơn theo trạng thái
    List<HoaDon> findByTrangThai(String trangThai);

    // Lấy hóa đơn theo mã bàn
    List<HoaDon> findByMaBan(Long maBan);

    /**
     * Kiểm tra bàn có hóa đơn ở trạng thái được chỉ định hay không.
     */
    boolean existsByMaBanAndTrangThai(Long maBan, String trangThai);

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

// Ưu tiên hóa đơn chưa thanh toán, sau đó sắp xếp theo ngày lập mới nhất
@Query("""
    SELECT h FROM HoaDon h
    ORDER BY
        CASE WHEN h.trangThai = 'Chua thanh toan' THEN 0 ELSE 1 END,
        h.ngayLap DESC
    """)
List<HoaDon> findAllSapXepTheoTrangThai();
}