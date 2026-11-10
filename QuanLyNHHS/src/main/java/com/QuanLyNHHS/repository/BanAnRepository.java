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

    /**
     * Lọc bàn theo trạng thái
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<BanAn> findByTrangThai(String trangThai);

    /**
     * Lọc bàn theo khu vực
     * @param khuVuc khu vực bố trí bàn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    List<BanAn> findByKhuVuc(String khuVuc);
}
