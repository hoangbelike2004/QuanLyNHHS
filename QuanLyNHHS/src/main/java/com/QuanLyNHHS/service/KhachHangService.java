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

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    public Optional<KhachHang> findById(Long maKh) {
        return khachHangRepository.findById(maKh);
    }

    public KhachHang save(KhachHang kh) {
        if (kh.getDiemTichLuy() == null) kh.setDiemTichLuy(0);
        return khachHangRepository.save(kh);
    }

    public void delete(Long maKh) {
        khachHangRepository.deleteById(maKh);
    }

    /** Tìm kiếm khách hàng theo họ tên */
    public List<KhachHang> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return khachHangRepository.findAll();
        return khachHangRepository.findByHoTenContainingIgnoreCase(keyword.trim());
    }

    public boolean existsById(Long maKh) {
        return khachHangRepository.existsById(maKh);
    }
}
