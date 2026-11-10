
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
 * Truy cập dữ liệu hóa đơn bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code HoaDon}, kiểu khóa là {@code Long}.
 */
public interface HoaDonRepository
        extends JpaRepository<HoaDon, Long> {
    /**
     * Kiểm tra có chứng từ dùng mã nhân viên này hay không để bảo vệ hồ sơ khi xóa.
     * @param maNv mã nhân viên dạng số trong database
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsByMaNv(Long maNv);

    // Lấy hóa đơn theo trạng thái
    /**
     * Lấy các hóa đơn khớp trạng thái.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findByTrangThai(String trangThai);

    // Lấy hóa đơn theo mã bàn
    /**
     * Lấy các hóa đơn gắn với mã bàn ăn.
     * @param maBan mã bàn ăn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findByMaBan(Long maBan);

    // Lấy hóa đơn theo mã nhân viên
    /**
     * Lấy các hóa đơn do nhân viên có mã maNv lập.
     * @param maNv mã nhân viên dạng số trong database
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findByMaNv(Long maNv);

    // Lấy hóa đơn theo mã khách hàng
    /**
     * Lấy các hóa đơn gắn với mã khách hàng.
     * @param maKh mã khách hàng
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findByMaKh(Long maKh);

    // Tìm hóa đơn theo mã và trạng thái
    /**
     * Tìm một hóa đơn đồng thời khớp mã hóa đơn và trạng thái; không khớp thì Optional rỗng.
     * @param maHd mã hóa đơn
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    Optional<HoaDon> findByMaHdAndTrangThai(
            Long maHd, String trangThai);

    // Lấy hóa đơn trong khoảng ngày
    /**
     * Tìm hóa đơn có ngày lập trong khoảng ngày, bao gồm cả hai mốc theo toán tử BETWEEN.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findByNgayLapBetween(
            LocalDate tuNgay, LocalDate denNgay);

    // Tính doanh thu trong khoảng ngày
    /**
     * Kiểm tra khoảng ngày rồi cộng doanh thu của hóa đơn theo trạng thái; mốc kết thúc của truy vấn là cận trên không bao gồm.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
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
    /**
     * Đếm hóa đơn có trạng thái bằng giá trị truyền vào.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return kết quả kiểu {@code long} cho nơi gọi sử dụng
     */
    long countByTrangThai(String trangThai);

    // Lấy hóa đơn mới nhất
    /**
     * Lấy toàn bộ hóa đơn sắp theo ngày lập giảm dần.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<HoaDon> findAllByOrderByNgayLapDesc();
}
