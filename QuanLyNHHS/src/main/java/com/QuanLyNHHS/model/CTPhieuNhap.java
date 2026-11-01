package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CT_PHIEU_NHAP")
public class CTPhieuNhap {
    @EmbeddedId
    private CTPhieuNhapId id = new CTPhieuNhapId();

    @MapsId("maPn")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_PN")
    private PhieuNhap phieuNhap;

    @MapsId("maNl")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_NL")
    private NguyenLieu nguyenLieu;

    @Column(name = "SO_LUONG", nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuong;

    @Column(name = "DON_GIA", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "THANH_TIEN", nullable = false, precision = 14, scale = 2)
    private BigDecimal thanhTien;

    public CTPhieuNhapId getId() { return id; }
    public void setId(CTPhieuNhapId id) { this.id = id; }

    public PhieuNhap getPhieuNhap() { return phieuNhap; }
    public void setPhieuNhap(PhieuNhap phieuNhap) { this.phieuNhap = phieuNhap; }

    public NguyenLieu getNguyenLieu() { return nguyenLieu; }
    public void setNguyenLieu(NguyenLieu nguyenLieu) { this.nguyenLieu = nguyenLieu; }

    public BigDecimal getSoLuong() { return soLuong; }
    public void setSoLuong(BigDecimal soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
