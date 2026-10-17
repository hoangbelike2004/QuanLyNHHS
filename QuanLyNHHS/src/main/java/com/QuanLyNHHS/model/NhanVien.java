package com.QuanLyNHHS.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Bảng NHAN_VIEN (Ảnh 1 & Ảnh 2)
 * PK: MA_NV NUMBER
 * HO_TEN VARCHAR2(100)
 * GIOI_TINH VARCHAR2(10)
 * SDT VARCHAR2(15)
 * CHUC_VU VARCHAR2(50)
 * LUONG NUMBER(12,2)
 * NGAY_VAO_LAM DATE
 */
@Entity
@Table(name = "NHAN_VIEN")
public class NhanVien {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nhan_vien")
    @SequenceGenerator(name = "seq_nhan_vien", sequenceName = "SEQ_NHAN_VIEN", allocationSize = 1)
    @Column(name = "MA_NV")
    private Long maNv;

    @Column(name = "HO_TEN", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "GIOI_TINH", length = 10)
    private String gioiTinh;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "CHUC_VU", length = 50)
    private String chucVu;

    @Column(name = "LUONG", precision = 12, scale = 2)
    private BigDecimal luong;

    @Column(name = "NGAY_VAO_LAM")
    private LocalDate ngayVaoLam;

    // Các thuộc tính phụ trợ hiển thị giao diện UI (không tạo cột trong Oracle DB)
    @Transient
    private String email;

    @Transient
    private String caLamViec;

    @Transient
    private String trangThai; // "Đang làm việc", "Nghỉ phép", "Đã nghỉ việc"

    public NhanVien() {
    }

    public NhanVien(String hoTen, String gioiTinh, String sdt, String chucVu, BigDecimal luong, LocalDate ngayVaoLam) {
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.luong = luong;
        this.ngayVaoLam = ngayVaoLam;
    }

    public NhanVien(Long maNv, String hoTen, String gioiTinh, String sdt, String chucVu, BigDecimal luong, LocalDate ngayVaoLam) {
        this.maNv = maNv;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.luong = luong;
        this.ngayVaoLam = ngayVaoLam;
    }

    // Constructor tiện ích cho dữ liệu mẫu controller
    public NhanVien(String maNVStr, String hoTen, String soDienThoai, String email, 
                    String chucVu, String caLamViec, String ngayVaoLamStr, double mucLuong, String trangThai) {
        try {
            this.maNv = Long.parseLong(maNVStr.replaceAll("\\D+", ""));
        } catch (Exception e) {
            this.maNv = null;
        }
        this.hoTen = hoTen;
        this.sdt = soDienThoai;
        this.email = email;
        this.chucVu = chucVu;
        this.caLamViec = caLamViec;
        this.gioiTinh = "Nam"; // Mặc định hiển thị mẫu
        try {
            this.ngayVaoLam = LocalDate.parse(ngayVaoLamStr, DATE_FORMATTER);
        } catch (Exception e) {
            this.ngayVaoLam = LocalDate.now();
        }
        this.luong = BigDecimal.valueOf(mucLuong);
        this.trangThai = trangThai;
    }

    public Long getMaNv() {
        return maNv;
    }

    public void setMaNv(Long maNv) {
        this.maNv = maNv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public BigDecimal getLuong() {
        return luong;
    }

    public void setLuong(BigDecimal luong) {
        this.luong = luong;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    // Helpers cho UI Thymeleaf
    public String getMaNV() {
        if (maNv == null) return "";
        return "NV" + String.format("%02d", maNv);
    }

    public void setMaNV(String maNV) {
        try {
            this.maNv = Long.parseLong(maNV.replaceAll("\\D+", ""));
        } catch (Exception ignored) {
        }
    }

    public String getSoDienThoai() {
        return sdt;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.sdt = soDienThoai;
    }

    public double getMucLuong() {
        return luong != null ? luong.doubleValue() : 0.0;
    }

    public void setMucLuong(double mucLuong) {
        this.luong = BigDecimal.valueOf(mucLuong);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
