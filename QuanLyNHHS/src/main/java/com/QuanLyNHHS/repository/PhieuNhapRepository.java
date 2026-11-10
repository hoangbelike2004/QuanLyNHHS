package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.PhieuNhap;

import org.springframework.data.jpa.repository.*;

/**
 * Truy cập dữ liệu phiếu nhập bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code PhieuNhap}, kiểu khóa là {@code Long}.
 */
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Long> {
    /**
     * Kiểm tra có chứng từ dùng mã nhân viên này hay không để bảo vệ hồ sơ khi xóa.
     * @param maNv mã nhân viên dạng số trong database
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsByMaNv(Long maNv);
    /**
     * Kiểm tra nhà cung cấp đã có phiếu nhập hay chưa.
     * @param maNcc mã nhà cung cấp
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsByNhaCungCapMaNcc(Long maNcc);

    /**
     * Đọc bản ghi theo mã với khóa PESSIMISTIC_WRITE; dùng trong giao dịch ghi để các cập nhật đồng thời phải chờ nhau.
     * @param id khóa chính của phiếu nhập
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PhieuNhap p where p.maPn = :id")
    java.util.Optional<PhieuNhap> findForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);

    /**
     * Đọc phiếu cùng nhà cung cấp, chi tiết và nguyên liệu bằng EntityGraph để trang chi tiết/form sửa có đủ dữ liệu.
     * @param id khóa chính của phiếu nhập
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    @EntityGraph(attributePaths = {"nhaCungCap", "chiTiet", "chiTiet.nguyenLieu"})
    @Query("select p from PhieuNhap p where p.maPn = :id")
    java.util.Optional<PhieuNhap> findDetails(
            @org.springframework.data.repository.query.Param("id") Long id);

    /**
     * Lấy phiếu nhập theo ngày nhập rồi mã phiếu giảm dần, nạp kèm nhà cung cấp.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    @EntityGraph(attributePaths = "nhaCungCap")
    java.util.List<PhieuNhap> findAllByOrderByNgayNhapDescMaPnDesc();
}
