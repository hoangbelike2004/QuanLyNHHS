package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.CTPhieuNhap;

import org.springframework.data.jpa.repository.*;

public interface CTPhieuNhapRepository
        extends JpaRepository<CTPhieuNhap, com.QuanLyNHHS.model.CTPhieuNhapId> {
    boolean existsByNguyenLieuMaNl(Long maNl);
}
