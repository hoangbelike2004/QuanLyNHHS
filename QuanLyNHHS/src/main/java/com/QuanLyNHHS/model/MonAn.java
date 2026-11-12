package com.QuanLyNHHS.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Bảng MON_AN (Ảnh 1 & Ảnh 2)
 * PK: MA_MON NUMBER
 * FK: MA_LOAI NUMBER (Liên kết đến LOAI_MON)
 * TEN_MON VARCHAR2(150)
 * DON_VI VARCHAR2(20)
 * DON_GIA NUMBER(12,2)
 * TRANG_THAI VARCHAR2(20)
 */
@Entity
@Table(name = "MON_AN")
public class MonAn {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mon_an")
    @SequenceGenerator(name = "seq_mon_an", sequenceName = "SEQ_MON_AN", allocationSize = 1)
    @Column(name = "MA_MON")
    private Long maMon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "MA_LOAI",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MON_AN_LOAI_MON"))
    private LoaiMon loaiMon;

    @Column(name = "TEN_MON", length = 150, nullable = false)
    private String tenMon;

    @Column(name = "DON_VI", length = 20)
    private String donVi;

    @Column(name = "DON_GIA", precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai; // "Đang phục vụ", "Tạm hết"

    // Các thuộc tính phụ trợ hiển thị giao diện UI (không tạo cột trong Oracle DB)
    @Transient
    private String hinhAnh;

    @Transient
    private String moTa;

    @Transient
    private Integer soLuongDaBan = 0;

    public MonAn() {
    }

    public MonAn(LoaiMon loaiMon, String tenMon, String donVi, BigDecimal donGia, String trangThai) {
        this.loaiMon = loaiMon;
        this.tenMon = tenMon;
        this.donVi = donVi;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    public MonAn(Long maMon, LoaiMon loaiMon, String tenMon, String donVi, BigDecimal donGia, String trangThai) {
        this.maMon = maMon;
        this.loaiMon = loaiMon;
        this.tenMon = tenMon;
        this.donVi = donVi;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    // Constructor tiện ích cho dữ liệu mẫu controller & UI
    public MonAn(String maMonStr, String tenMon, String maLoaiStr, String tenLoai, String donVi,
                 double donGiaDouble, String hinhAnh, String moTa, String trangThai, int soLuongDaBan) {
        try {
            this.maMon = Long.parseLong(maMonStr.replaceAll("\\D+", ""));
        } catch (Exception e) {
            this.maMon = null;
        }
        this.tenMon = tenMon;
        LoaiMon lm = new LoaiMon();
        try {
            lm.setMaLoai(Long.parseLong(maLoaiStr.replaceAll("\\D+", "")));
        } catch (Exception e) {
            lm.setMaLoai(null);
        }
        lm.setTenLoai(tenLoai);
        this.loaiMon = lm;
        this.donVi = donVi;
        this.donGia = BigDecimal.valueOf(donGiaDouble);
        this.hinhAnh = hinhAnh;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.soLuongDaBan = soLuongDaBan;
    }

    public Long getMaMon() {
        return maMon;
    }

    public void setMaMon(Long maMon) {
        this.maMon = maMon;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Helpers cho Thymeleaf UI
    public Long getMaLoai() {
        return loaiMon != null ? loaiMon.getMaLoai() : null;
    }

    public String getTenLoai() {
        return loaiMon != null ? loaiMon.getTenLoai() : null;
    }

    public String getDonViTinh() {
        return donVi;
    }

    public void setDonViTinh(String donViTinh) {
        this.donVi = donViTinh;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Integer getSoLuongDaBan() {
        return soLuongDaBan != null ? soLuongDaBan : 0;
    }

    public void setSoLuongDaBan(Integer soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }
}
