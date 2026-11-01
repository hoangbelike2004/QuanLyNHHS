package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity mapping tới bảng CT_PHIEU_NHAP (chi tiết phiếu nhập nguyên liệu)
 * TODO: Cần implement đầy đủ khi phát triển module Phiếu nhập
 */
@Entity
@Table(name = "CT_PHIEU_NHAP")
public class CTPhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_CT")
    private Long maCt;

    @Column(name = "SO_LUONG")
    private Integer soLuong;

    @Column(name = "DON_GIA", precision = 12, scale = 2)
    private BigDecimal donGia;

    // ============ Constructors ============

    public CTPhieuNhap() {}

    // ============ Getters & Setters ============

    public Long getMaCt() { return maCt; }
    public void setMaCt(Long maCt) { this.maCt = maCt; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
}
