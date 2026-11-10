
package com.QuanLyNHHS.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.CTHoaDon.CTHoaDonId;

/**
 * Truy cập dữ liệu chi tiết hóa đơn bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code CTHoaDon}, kiểu khóa là {@code CTHoaDonId}.
 */
public interface CTHoaDonRepository
        extends JpaRepository<CTHoaDon, CTHoaDonId> {
    /**
     * Kiểm tra món đã xuất hiện trong chi tiết hóa đơn hay chưa trước khi xóa món.
     * @param maMon mã món ăn
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsById_MaMon(Long maMon);

    /**
     * Nhóm chi tiết của hóa đơn đã thanh toán theo mã món; mỗi Object[] gồm mã món ở [0] và tổng số lượng ở [1].
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    @Query("""
        SELECT c.id.maMon, SUM(c.soLuong)
        FROM CTHoaDon c, HoaDon h
        WHERE c.id.maHd = h.maHd AND h.trangThai = 'Da thanh toan'
        GROUP BY c.id.maMon
        """)
    List<Object[]> tongSoLuongDaBanTheoMon();

    // Lấy chi tiết hóa đơn theo mã hóa đơn
    /**
     * Lấy tất cả dòng chi tiết thuộc mã hóa đơn được truyền vào.
     * @param maHd mã hóa đơn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<CTHoaDon> findById_MaHd(Long maHd);

    // Lấy chi tiết theo mã món
    /**
     * Lấy các dòng chi tiết hóa đơn có chứa mã món được truyền vào.
     * @param maMon mã món ăn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<CTHoaDon> findById_MaMon(Long maMon);

    // Tính tổng tiền chi tiết của một hóa đơn
    /**
     * Cộng thanhTien của mọi dòng thuộc hóa đơn; COALESCE trả 0 khi không có dòng nào.
     * @param maHd mã hóa đơn
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    @Query("""
        SELECT COALESCE(SUM(c.thanhTien), 0)
        FROM CTHoaDon c
        WHERE c.id.maHd = :maHd
        """)
    BigDecimal tinhTongTienTheoHoaDon(
            @Param("maHd") Long maHd);

    // Thống kê món bán chạy - chỉ hóa đơn đã thanh toán
/**
 * Thống kê món theo trạng thái hóa đơn, sắp tổng lượng bán giảm dần; mỗi dòng gồm tên món, tổng số lượng, tổng thành tiền.
 * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
 * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
 */
@Query("""
    SELECT m.tenMon, SUM(c.soLuong), SUM(c.thanhTien)
    FROM CTHoaDon c, HoaDon h, MonAn m
    WHERE c.id.maHd = h.maHd
      AND c.id.maMon = m.maMon
      AND h.trangThai = :trangThai
    GROUP BY m.maMon, m.tenMon
    ORDER BY SUM(c.soLuong) DESC
    """)
List<Object[]> thongKeMonBanChay(
        @Param("trangThai") String trangThai);
    // Thống kê tổng tiền theo hóa đơn - chỉ hóa đơn đã thanh toán
    /**
     * Gom nhóm chi tiết theo hóa đơn; mỗi dòng gồm mã hóa đơn và tổng thành tiền chi tiết, chưa trừ giảm giá ở hóa đơn.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
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
    /**
     * Đếm số dòng chi tiết của hóa đơn để kiểm tra hóa đơn đã có món.
     * @param maHd mã hóa đơn
     * @return kết quả kiểu {@code long} cho nơi gọi sử dụng
     */
    long countById_MaHd(Long maHd);

    // Xóa các chi tiết theo mã hóa đơn
    /**
     * Xóa các dòng chi tiết thuộc hóa đơn; được gọi trong giao dịch khi sửa hoặc xóa hóa đơn.
     * @param maHd mã hóa đơn
     */
    void deleteById_MaHd(Long maHd);
}
