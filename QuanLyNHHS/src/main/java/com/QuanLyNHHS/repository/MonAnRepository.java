package com.QuanLyNHHS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import com.QuanLyNHHS.model.MonAn;

/**
 * Truy cập dữ liệu món ăn bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code MonAn}, kiểu khóa là {@code Long}.
 */
public interface MonAnRepository extends JpaRepository<MonAn, Long> {
    /**
     * Kiểm tra còn món ăn tham chiếu mã loại món hay không trước khi cho phép xóa loại.
     * @param maLoai mã loại món
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsByLoaiMon_MaLoai(Long maLoai);

    /**
     * Đếm món theo mã loại; kết quả truy vấn gồm mã loại ở [0] và số món ở [1].
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    @Query("SELECT m.loaiMon.maLoai, COUNT(m) FROM MonAn m GROUP BY m.loaiMon.maLoai")
    List<Object[]> demMonTheoLoai();
}
