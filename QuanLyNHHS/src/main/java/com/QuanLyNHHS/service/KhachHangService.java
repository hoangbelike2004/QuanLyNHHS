package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ cho Khách hàng
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Service
public class KhachHangService {

    // khachHangRepository: thành phần KhachHangRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired
    private KhachHangRepository khachHangRepository;

    /**
     * Lấy toàn bộ khách hàng.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    /**
     * Tìm khách hàng theo khóa chính; trả Optional rỗng nếu không có bản ghi.
     * @param maKh mã khách hàng
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    public Optional<KhachHang> findById(Long maKh) {
        return khachHangRepository.findById(maKh);
    }

    /**
     * Lưu khách hàng; nếu điểm tích lũy null thì mặc định 0.
     * @param kh đối tượng khách hàng đang xử lý
     * @return kết quả kiểu {@code KhachHang} cho nơi gọi sử dụng
     */
    public KhachHang save(KhachHang kh) {
        if (kh.getDiemTichLuy() == null) kh.setDiemTichLuy(0);
        return khachHangRepository.save(kh);
    }

    /**
     * Xóa khách hàng theo mã qua repository.
     * @param maKh mã khách hàng
     */
    public void delete(Long maKh) {
        khachHangRepository.deleteById(maKh);
    }

    /**
     * Tìm kiếm khách hàng theo họ tên
     * @param keyword từ khóa tìm kiếm
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<KhachHang> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return khachHangRepository.findAll();
        return khachHangRepository.findByHoTenContainingIgnoreCase(keyword.trim());
    }

    /**
     * Kiểm tra có khách hàng mang mã đã cho trong database hay không.
     * @param maKh mã khách hàng
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    public boolean existsById(Long maKh) {
        return khachHangRepository.existsById(maKh);
    }
}
