package com.QuanLyNHHS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Bảng LOAI_MON (Ảnh 1 & Ảnh 2)
 * PK: MA_LOAI NUMBER
 * TEN_LOAI VARCHAR2(100)
 * MO_TA VARCHAR2(255)
 * TRANG_THAI VARCHAR2(20)
 */
@Entity
@Table(name = "LOAI_MON")
public class LoaiMon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_loai_mon")
    @SequenceGenerator(name = "seq_loai_mon", sequenceName = "SEQ_LOAI_MON", allocationSize = 1)
    @Column(name = "MA_LOAI")
    private Long maLoai;

    @Column(name = "TEN_LOAI", length = 100, nullable = false)
    private String tenLoai;

    @Column(name = "MO_TA", length = 255)
    private String moTa;

    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai; // "Hoạt động", "Tạm dừng"

    // Thuộc tính phụ trợ phục vụ thống kê hiển thị giao diện UI (không tạo cột trong Oracle DB)
    @Transient
    private Integer soLuongMon = 0;

    public LoaiMon() {
    }

    public LoaiMon(Long maLoai, String tenLoai, String moTa, String trangThai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public LoaiMon(String tenLoai, String moTa, String trangThai) {
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    // Constructor tiện ích cho dữ liệu mẫu controller
    public LoaiMon(String maLoaiStr, String tenLoai, String moTa, Integer soLuongMon, String trangThai) {
        try {
            this.maLoai = Long.parseLong(maLoaiStr.replaceAll("\\D+", ""));
        } catch (Exception e) {
            this.maLoai = null;
        }
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.soLuongMon = soLuongMon;
        this.trangThai = trangThai;
    }

    public Long getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Long maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getSoLuongMon() {
        return soLuongMon != null ? soLuongMon : 0;
    }

    public void setSoLuongMon(Integer soLuongMon) {
        this.soLuongMon = soLuongMon;
    }
}
