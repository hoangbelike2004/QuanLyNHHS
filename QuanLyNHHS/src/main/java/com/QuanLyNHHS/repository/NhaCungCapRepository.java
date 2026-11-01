package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.NhaCungCap;
import org.springframework.data.jpa.repository.*;


public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NhaCungCap n where n.maNcc = :id")
    java.util.Optional<NhaCungCap> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
