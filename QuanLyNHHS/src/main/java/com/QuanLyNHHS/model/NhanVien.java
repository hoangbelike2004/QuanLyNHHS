package com.QuanLyNHHS.model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String chucVu; // "Quản lý", "Bếp trưởng", "Đầu bếp", "Thu ngân", "Phục vụ", "Lễ tân"
    private String caLamViec; // "Toàn thời gian", "Ca sáng (08:00 - 16:00)", "Ca tối (16:00 - 23:00)"
    private String ngayVaoLam;
    private double mucLuong;
    private String trangThai; // "Đang làm việc", "Nghỉ phép", "Đã nghỉ việc"

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String soDienThoai, String email, 
                   String chucVu, String caLamViec, String ngayVaoLam, double mucLuong, String trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.chucVu = chucVu;
        this.caLamViec = caLamViec;
        this.ngayVaoLam = ngayVaoLam;
        this.mucLuong = mucLuong;
        this.trangThai = trangThai;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }

    public String getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(String ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public double getMucLuong() {
        return mucLuong;
    }

    public void setMucLuong(double mucLuong) {
        this.mucLuong = mucLuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
