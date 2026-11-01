package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.PhieuNhap;

import org.springframework.data.jpa.repository.*;

public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Long> {
    boolean existsByNhaCungCapMaNcc(Long maNcc);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PhieuNhap p where p.maPn = :id")
    java.util.Optional<PhieuNhap> findForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);

    @EntityGraph(attributePaths = {"nhaCungCap", "chiTiet", "chiTiet.nguyenLieu"})
    @Query("select p from PhieuNhap p where p.maPn = :id")
    java.util.Optional<PhieuNhap> findDetails(
            @org.springframework.data.repository.query.Param("id") Long id);

    @EntityGraph(attributePaths = "nhaCungCap")
    java.util.List<PhieuNhap> findAllByOrderByNgayNhapDescMaPnDesc();
}
