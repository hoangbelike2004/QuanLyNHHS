package com.QuanLyNHHS.model;

public class LoaiMon {
    private String maLoai;
    private String tenLoai;
    private String moTa;
    private int soLuongMon;
    private String trangThai; // "Hoạt động", "Tạm dừng"

    public LoaiMon() {
    }

    public LoaiMon(String maLoai, String tenLoai, String moTa, int soLuongMon, String trangThai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.soLuongMon = soLuongMon;
        this.trangThai = trangThai;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
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

    public int getSoLuongMon() {
        return soLuongMon;
    }

    public void setSoLuongMon(int soLuongMon) {
        this.soLuongMon = soLuongMon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
