package com.QuanLyNHHS.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.DatBanRepository;


@Service
public class DatBanService {

    @Autowired
    private DatBanRepository datBanRepository;

    @Autowired
    private BanAnRepository banAnRepository;

    public List<DatBan> findAll() { return datBanRepository.findAll(); }

    public Optional<DatBan> findById(Long maDat) { return datBanRepository.findById(maDat); }

    public DatBan save(DatBan datBan) { return datBanRepository.save(datBan); }

    public void delete(Long maDat) { datBanRepository.deleteById(maDat); }

    public boolean existsById(Long maDat) { return datBanRepository.existsById(maDat); }

    public List<DatBan> findByTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return datBanRepository.findAll();
        return datBanRepository.findByTrangThai(trangThai.trim());
    }

    public List<DatBan> findByKhachHang(Long maKh) {
        return datBanRepository.findByKhachHang_MaKh(maKh);
    }

    public List<DatBan> findByBanAn(Long maBan) {
        return datBanRepository.findByBanAn_MaBan(maBan);
    }

    /** Xác nhận: DAT_BAN → DA_XAC_NHAN, BAN_AN → Đã đặt */
    @Transactional
    public void xacNhan(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(db -> {
            db.setTrangThai("DA_XAC_NHAN");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(ban -> {
                    ban.setTrangThai("Đã đặt");
                    banAnRepository.save(ban);
                });
            }
        });
    }

    /** Hủy: DAT_BAN → DA_HUY, BAN_AN → Trống */
    @Transactional
    public void huyDatBan(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(db -> {
            db.setTrangThai("DA_HUY");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(ban -> {
                    ban.setTrangThai("Trống");
                    banAnRepository.save(ban);
                });
            }
        });
    }

    /** Hoàn thành: DAT_BAN → HOAN_THANH, BAN_AN → Trống */
    @Transactional
    public void hoanThanh(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(db -> {
            db.setTrangThai("HOAN_THANH");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(ban -> {
                    ban.setTrangThai("Trống");
                    banAnRepository.save(ban);
                });
            }
        });
    }
}
