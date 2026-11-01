package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.NguyenLieu;
import org.springframework.data.jpa.repository.*;


public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NguyenLieu n where n.maNl = :id")
    java.util.Optional<NguyenLieu> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
