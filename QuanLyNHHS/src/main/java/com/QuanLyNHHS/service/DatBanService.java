package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.DatBanRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.KhachHangRepository;
import com.QuanLyNHHS.repository.NhanVienRepository;

@Service
public class DatBanService {
    private final DatBanRepository datBanRepository;
    private final BanAnRepository banAnRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final HoaDonRepository hoaDonRepository;

    public DatBanService(DatBanRepository datBanRepository, BanAnRepository banAnRepository,
                         KhachHangRepository khachHangRepository, NhanVienRepository nhanVienRepository,
                         HoaDonRepository hoaDonRepository) {
        this.datBanRepository = datBanRepository;
        this.banAnRepository = banAnRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    public List<DatBan> findAll() { return datBanRepository.findAll(); }
    public Optional<DatBan> findById(Long maDat) { return datBanRepository.findById(maDat); }
    public List<DatBan> findByTrangThai(String trangThai) {
        return trangThai == null || trangThai.isBlank() ? findAll() : datBanRepository.findByTrangThai(trangThai.trim());
    }
    public List<DatBan> findByKhachHang(Long maKh) { return datBanRepository.findByKhachHang_MaKh(maKh); }
    public List<DatBan> findByBanAn(Long maBan) { return datBanRepository.findByBanAn_MaBan(maBan); }

    @Transactional
    public DatBan tao(Long maKh, Long maBan, LocalDate ngayDat, Integer soNguoi, BigDecimal tienCoc) {
        kiemTraNgay(ngayDat);
        BanAn ban = layBanTrong(maBan);
        kiemTraThongTin(ban, soNguoi, tienCoc);
        DatBan datBan = new DatBan();
        datBan.setKhachHang(khachHangRepository.findById(maKh)
                .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại")));
        datBan.setBanAn(ban);
        datBan.setNgayDat(ngayDat);
        datBan.setSoNguoi(soNguoi);
        datBan.setTienCoc(tienCoc);
        datBan.setTrangThai("CHO_XAC_NHAN");
        ban.setTrangThai("Đã đặt");
        return datBanRepository.save(datBan);
    }

    @Transactional
    public void capNhat(Long maDat, Long maKh, Long maBan, LocalDate ngayDat,
                        Integer soNguoi, BigDecimal tienCoc) {
        kiemTraNgay(ngayDat);
        DatBan datBan = datBanRepository.findForUpdate(maDat)
                .orElseThrow(() -> new IllegalArgumentException("Đặt bàn không tồn tại"));
        if (!"CHO_XAC_NHAN".equals(datBan.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể sửa đặt bàn đang chờ xác nhận");
        }
        if (datBan.getBanAn() == null) {
            throw new IllegalStateException("Đặt bàn không còn bàn ăn hợp lệ");
        }
        BanAn banCu = banAnRepository.findForUpdate(datBan.getBanAn().getMaBan())
                .orElseThrow(() -> new IllegalArgumentException("Bàn cũ không tồn tại"));
        BanAn banMoi = banCu.getMaBan().equals(maBan) ? banCu : layBanTrong(maBan);
        kiemTraThongTin(banMoi, soNguoi, tienCoc);
        datBan.setKhachHang(khachHangRepository.findById(maKh)
                .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại")));
        datBan.setBanAn(banMoi);
        datBan.setNgayDat(ngayDat);
        datBan.setSoNguoi(soNguoi);
        datBan.setTienCoc(tienCoc);
        if (banMoi != banCu) {
            banCu.setTrangThai("Trống");
            banMoi.setTrangThai("Đã đặt");
        }
    }

    @Transactional
    public void xacNhan(Long maDat, Long maNv) {
        DatBan db = datBanRepository.findForUpdate(maDat)
                .orElseThrow(() -> new IllegalArgumentException("Đặt bàn không tồn tại"));
        if (!"CHO_XAC_NHAN".equals(db.getTrangThai())) {
            throw new IllegalStateException("Đặt bàn không còn chờ xác nhận");
        }
        if (maNv == null || !nhanVienRepository.existsById(maNv)) {
            throw new IllegalArgumentException("Nhân viên xác nhận không tồn tại");
        }
        if (db.getBanAn() == null || !banAnRepository.findForUpdate(db.getBanAn().getMaBan())
                .map(b -> "Đã đặt".equals(b.getTrangThai())).orElse(false)) {
            throw new IllegalStateException("Bàn đặt không còn hợp lệ");
        }
        db.setMaNv(maNv);
        db.setTrangThai("DA_XAC_NHAN");
    }

    @Transactional
    public void huyDatBan(Long maDat) {
        DatBan db = datBanRepository.findForUpdate(maDat)
                .orElseThrow(() -> new IllegalArgumentException("Đặt bàn không tồn tại"));
        if (!"CHO_XAC_NHAN".equals(db.getTrangThai()) && !"DA_XAC_NHAN".equals(db.getTrangThai())) {
            throw new IllegalStateException("Đặt bàn không thể hủy");
        }
        if (hoaDonRepository.findByMaDat(maDat).isPresent()) {
            throw new IllegalStateException("Bàn đã gọi món; hãy xử lý hóa đơn trước khi hủy");
        }
        if (db.getBanAn() == null) {
            throw new IllegalStateException("Đặt bàn không còn bàn ăn hợp lệ");
        }
        BanAn ban = banAnRepository.findForUpdate(db.getBanAn().getMaBan())
                .orElseThrow(() -> new IllegalStateException("Bàn đặt không tồn tại"));
        if (!"Đã đặt".equals(ban.getTrangThai())) {
            throw new IllegalStateException("Trạng thái bàn không khớp với đặt bàn");
        }
        db.setTrangThai("DA_HUY");
        ban.setTrangThai("Trống");
    }

    @Transactional
    public void delete(Long maDat) {
        DatBan db = datBanRepository.findById(maDat)
                .orElseThrow(() -> new IllegalArgumentException("Đặt bàn không tồn tại"));
        if (!"DA_HUY".equals(db.getTrangThai()) || hoaDonRepository.findByMaDat(maDat).isPresent()) {
            throw new IllegalStateException("Chỉ có thể xóa đặt bàn đã hủy và không có hóa đơn");
        }
        datBanRepository.delete(db);
    }

    private BanAn layBanTrong(Long maBan) {
        BanAn ban = banAnRepository.findForUpdate(maBan)
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        if (!"Trống".equals(ban.getTrangThai())) {
            throw new IllegalStateException("Bàn " + ban.getTenBan() + " không còn trống");
        }
        return ban;
    }

    private void kiemTraThongTin(BanAn ban, Integer soNguoi, BigDecimal tienCoc) {
        if (soNguoi == null || soNguoi < 1 || ban.getSoCho() != null && soNguoi > ban.getSoCho()) {
            throw new IllegalArgumentException("Số người vượt quá sức chứa của bàn hoặc không hợp lệ");
        }
        if (tienCoc != null && (tienCoc.signum() < 0 || tienCoc.scale() > 2)) {
            throw new IllegalArgumentException("Tiền cọc không hợp lệ");
        }
    }

    private void kiemTraNgay(LocalDate ngayDat) {
        if (ngayDat == null || ngayDat.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày đặt không được ở quá khứ");
        }
    }
}
