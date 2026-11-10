package com.QuanLyNHHS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.QuanLyNHHS.model.NhanVien;

/**
 * Truy cập dữ liệu nhân viên bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code NhanVien}, kiểu khóa là {@code Long}.
 */
public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
}
