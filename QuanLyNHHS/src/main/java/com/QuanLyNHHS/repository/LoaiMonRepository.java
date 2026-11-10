package com.QuanLyNHHS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.QuanLyNHHS.model.LoaiMon;

/**
 * Truy cập dữ liệu loại món bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code LoaiMon}, kiểu khóa là {@code Long}.
 */
public interface LoaiMonRepository extends JpaRepository<LoaiMon, Long> {
}
