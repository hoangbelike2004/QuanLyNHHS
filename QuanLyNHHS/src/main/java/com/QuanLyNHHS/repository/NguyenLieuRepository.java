package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.NguyenLieu;

import org.springframework.data.jpa.repository.*;

/**
 * Truy cập dữ liệu nguyên liệu bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code NguyenLieu}, kiểu khóa là {@code Long}.
 */
public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Long> {
    /**
     * Đọc bản ghi theo mã với khóa PESSIMISTIC_WRITE; dùng trong giao dịch ghi để các cập nhật đồng thời phải chờ nhau.
     * @param id khóa chính của nguyên liệu
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NguyenLieu n where n.maNl = :id")
    java.util.Optional<NguyenLieu> findForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);
}
