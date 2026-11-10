package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.CTPhieuNhap;

import org.springframework.data.jpa.repository.*;

/**
 * Truy cập dữ liệu chi tiết phiếu nhập bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code CTPhieuNhap}, kiểu khóa là {@code com.QuanLyNHHS.model.CTPhieuNhapId}.
 */
public interface CTPhieuNhapRepository
        extends JpaRepository<CTPhieuNhap, com.QuanLyNHHS.model.CTPhieuNhapId> {
    /**
     * Kiểm tra nguyên liệu đã xuất hiện trong chi tiết phiếu nhập hay chưa.
     * @param maNl mã nguyên liệu
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    boolean existsByNguyenLieuMaNl(Long maNl);
}
