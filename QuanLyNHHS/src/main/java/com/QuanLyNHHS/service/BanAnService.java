package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.DatBanRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý nghiệp vụ cho Bàn ăn
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Service
public class BanAnService {

    @Autowired
    private BanAnRepository banAnRepository;
    @Autowired private DatBanRepository datBanRepository;
    @Autowired private HoaDonRepository hoaDonRepository;

    public List<BanAn> findAll() { return banAnRepository.findAll(); }

    public Optional<BanAn> findById(Long maBan) { return banAnRepository.findById(maBan); }

    @Transactional
    public BanAn save(BanAn banAn) {
        if (banAn.getMaBan() == null) return banAnRepository.save(banAn);
        BanAn hienTai = banAnRepository.findForUpdate(banAn.getMaBan())
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        if ((!Objects.equals(hienTai.getTrangThai(), banAn.getTrangThai())
                || !Objects.equals(hienTai.getSoCho(), banAn.getSoCho()))
                && datBanRepository.existsByBanAn_MaBanAndTrangThaiIn(
                        banAn.getMaBan(), List.of("CHO_XAC_NHAN", "DA_XAC_NHAN"))) {
            throw new IllegalStateException("Bàn đang được đặt; không thể đổi trạng thái hoặc số chỗ");
        }
        hienTai.setTenBan(banAn.getTenBan());
        hienTai.setSoCho(banAn.getSoCho());
        hienTai.setKhuVuc(banAn.getKhuVuc());
        hienTai.setTrangThai(banAn.getTrangThai());
        return hienTai;
    }

    @Transactional
    public void delete(Long maBan) {
        if (datBanRepository.existsByBanAn_MaBan(maBan) || hoaDonRepository.existsByMaBan(maBan)) {
            throw new IllegalStateException("Bàn đã có đặt bàn hoặc hóa đơn");
        }
        banAnRepository.deleteById(maBan);
    }

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

}
