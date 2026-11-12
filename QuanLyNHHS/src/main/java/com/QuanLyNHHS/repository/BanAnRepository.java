package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.BanAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository cho bảng BAN_AN
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Repository
public interface BanAnRepository extends JpaRepository<BanAn, Long> {

    /** Lọc bàn theo trạng thái */
    List<BanAn> findByTrangThai(String trangThai);

    /** Lọc bàn theo khu vực */
    List<BanAn> findByKhuVuc(String khuVuc);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BanAn b where b.maBan = :maBan")
    Optional<BanAn> findForUpdate(@Param("maBan") Long maBan);
}
