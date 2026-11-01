package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping tới bảng DAT_BAN trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "DAT_BAN")
public class DatBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DAT")
    private Long maDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MA_KH", referencedColumnName = "MA_KH")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MA_BAN", referencedColumnName = "MA_BAN")
    private BanAn banAn;

    @Column(name = "NGAY_DAT")
    private LocalDate ngayDat;

    @Column(name = "SO_NGUOI")
    private Integer soNguoi;

    @Column(name = "TIEN_COC", precision = 12, scale = 2)
    private BigDecimal tienCoc;

    /** Trạng thái: CHO_XAC_NHAN / DA_XAC_NHAN / DA_HUY / HOAN_THANH */
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    // ============ Constructors ============

    public DatBan() {}

    public DatBan(Long maDat, KhachHang khachHang, BanAn banAn,
                  LocalDate ngayDat, Integer soNguoi, BigDecimal tienCoc, String trangThai) {
        this.maDat = maDat;
        this.khachHang = khachHang;
        this.banAn = banAn;
        this.ngayDat = ngayDat;
        this.soNguoi = soNguoi;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
    }

    // ============ Getters & Setters ============

    public Long getMaDat() { return maDat; }
    public void setMaDat(Long maDat) { this.maDat = maDat; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public BanAn getBanAn() { return banAn; }
    public void setBanAn(BanAn banAn) { this.banAn = banAn; }

    public LocalDate getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDate ngayDat) { this.ngayDat = ngayDat; }

    public Integer getSoNguoi() { return soNguoi; }
    public void setSoNguoi(Integer soNguoi) { this.soNguoi = soNguoi; }

    public BigDecimal getTienCoc() { return tienCoc; }
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // ============ toString ============

    @Override
    public String toString() {
        return "DatBan{maDat=" + maDat +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKh() : "null") +
                ", banAn=" + (banAn != null ? banAn.getMaBan() : "null") +
                ", ngayDat=" + ngayDat +
                ", soNguoi=" + soNguoi +
                ", tienCoc=" + tienCoc +
                ", trangThai='" + trangThai + "'}";
    }
}
