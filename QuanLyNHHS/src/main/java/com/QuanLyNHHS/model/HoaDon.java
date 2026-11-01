
package com.QuanLyNHHS.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HOA_DON")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_HD")
    private Long maHd;

    @Column(name = "MA_BAN")
    private Long maBan;

    @Column(name = "MA_NV")
    private Long maNv;

    @Column(name = "MA_KH")
    private Long maKh;

    @Column(name = "NGAY_LAP")
    private LocalDate ngayLap;

    @Column(name = "GIAM_GIA", precision = 12, scale = 2)
    private BigDecimal giamGia;

    @Column(name = "TONG_TIEN", precision = 14, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    public HoaDon() {}

    public Long getMaHd() { return maHd; }
    public void setMaHd(Long maHd) { this.maHd = maHd; }

    public Long getMaBan() { return maBan; }
    public void setMaBan(Long maBan) { this.maBan = maBan; }

    public Long getMaNv() { return maNv; }
    public void setMaNv(Long maNv) { this.maNv = maNv; }

    public Long getMaKh() { return maKh; }
    public void setMaKh(Long maKh) { this.maKh = maKh; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    public BigDecimal getGiamGia() { return giamGia; }
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
