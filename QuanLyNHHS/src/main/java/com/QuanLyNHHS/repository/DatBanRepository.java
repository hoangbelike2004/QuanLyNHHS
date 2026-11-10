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

    /**
     * Lịch sử đặt bàn của một khách hàng
     * @param maKh mã khách hàng
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<DatBan> findByKhachHang_MaKh(Long maKh);

    /**
     * Đặt bàn theo bàn
     * @param maBan mã bàn ăn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<DatBan> findByBanAn_MaBan(Long maBan);

    /**
     * Lọc theo trạng thái
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<DatBan> findByTrangThai(String trangThai);
}
