package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.DatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository cho bảng DAT_BAN
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Repository
public interface DatBanRepository extends JpaRepository<DatBan, Long> {

    /** Lịch sử đặt bàn của một khách hàng */
    List<DatBan> findByKhachHang_MaKh(Long maKh);

    /** Đặt bàn theo bàn */
    List<DatBan> findByBanAn_MaBan(Long maBan);
    boolean existsByBanAn_MaBan(Long maBan);
    boolean existsByBanAn_MaBanAndTrangThaiIn(Long maBan, List<String> trangThai);

    /** Lọc theo trạng thái */
    List<DatBan> findByTrangThai(String trangThai);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DatBan d where d.maDat = :maDat")
    Optional<DatBan> findForUpdate(@Param("maDat") Long maDat);
}
