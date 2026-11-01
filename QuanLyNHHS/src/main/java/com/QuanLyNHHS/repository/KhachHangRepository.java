package com.QuanLyNHHS.repository;

import com.QuanLyNHHS.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository cho bảng KHACH_HANG
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    /** Tìm kiếm theo họ tên (không phân biệt hoa thường) */
    List<KhachHang> findByHoTenContainingIgnoreCase(String hoTen);

    /** Tìm theo số điện thoại */
    List<KhachHang> findBySdt(String sdt);
}
