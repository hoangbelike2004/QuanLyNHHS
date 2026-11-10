package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.NhaCungCap;

import org.springframework.data.jpa.repository.*;

/**
 * Truy cập dữ liệu nhà cung cấp bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code NhaCungCap}, kiểu khóa là {@code Long}.
 */
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {
    /**
     * Đọc bản ghi theo mã với khóa PESSIMISTIC_WRITE; dùng trong giao dịch ghi để các cập nhật đồng thời phải chờ nhau.
     * @param id khóa chính của nhà cung cấp
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NhaCungCap n where n.maNcc = :id")
    java.util.Optional<NhaCungCap> findForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);
}
