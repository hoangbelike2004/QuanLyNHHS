package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.repository.BanAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ cho Bàn ăn
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Service
public class BanAnService {

    @Autowired
    private BanAnRepository banAnRepository;

    public List<BanAn> findAll() { return banAnRepository.findAll(); }

    public Optional<BanAn> findById(Long maBan) { return banAnRepository.findById(maBan); }

    public BanAn save(BanAn banAn) { return banAnRepository.save(banAn); }

    public void delete(Long maBan) { banAnRepository.deleteById(maBan); }

    public boolean existsById(Long maBan) { return banAnRepository.existsById(maBan); }

    public List<BanAn> findByTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return banAnRepository.findAll();
        return banAnRepository.findByTrangThai(trangThai.trim());
    }

    public List<BanAn> findByKhuVuc(String khuVuc) {
        if (khuVuc == null || khuVuc.trim().isEmpty()) return banAnRepository.findAll();
        return banAnRepository.findByKhuVuc(khuVuc.trim());
    }

    /** Danh sách bàn trống dùng cho form đặt bàn */
    public List<BanAn> findBanTrong() {
        return banAnRepository.findByTrangThai("Trống");
    }

    public void capNhatTrangThai(Long maBan, String trangThai) {
        Optional<BanAn> opt = banAnRepository.findById(maBan);
        opt.ifPresent(ban -> {
            ban.setTrangThai(trangThai);
            banAnRepository.save(ban);
        });
    }
}
