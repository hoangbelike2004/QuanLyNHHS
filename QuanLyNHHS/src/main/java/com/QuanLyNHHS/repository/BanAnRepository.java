package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.BanAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

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
}
