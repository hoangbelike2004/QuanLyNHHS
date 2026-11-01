package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.DatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    /** Lọc theo trạng thái */
    List<DatBan> findByTrangThai(String trangThai);
}
