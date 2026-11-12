package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "NGUYEN_LIEU")
public class NguyenLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nl_seq")
    @SequenceGenerator(name = "nl_seq", sequenceName = "SEQ_NGUYEN_LIEU", allocationSize = 1)
    @Column(name = "MA_NL")
    private Long maNl;

    @Column(name = "TEN_NL", nullable = false, length = 100)
    private String tenNl;

    @Column(name = "DON_VI", nullable = false, length = 20)
    private String donVi;

    @Column(name = "SO_LUONG_TON", nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongTon = BigDecimal.ZERO;

    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    private String trangThai = "HOAT_DONG";

    public Long getMaNl() {
        return maNl;
    }

    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }

    public String getTenNl() {
        return tenNl;
    }

    public void setTenNl(String tenNl) {
        this.tenNl = tenNl;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(BigDecimal soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
