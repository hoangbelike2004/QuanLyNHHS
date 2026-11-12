package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity mapping tới bảng KHACH_HANG trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "KHACH_HANG")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_KH")
    private Long maKh;

    @Column(name = "HO_TEN", length = 100)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "DIEM_TICH_LUY")
    private Integer diemTichLuy = 0;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<DatBan> danhSachDatBan;

    // ============ Constructors ============

    public KhachHang() {}

    public KhachHang(Long maKh, String hoTen, String sdt, String email, Integer diemTichLuy) {
        this.maKh = maKh;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    // ============ Getters & Setters ============

    public Long getMaKh() { return maKh; }
    public void setMaKh(Long maKh) { this.maKh = maKh; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0; }

    public List<DatBan> getDanhSachDatBan() { return danhSachDatBan; }
    public void setDanhSachDatBan(List<DatBan> danhSachDatBan) { this.danhSachDatBan = danhSachDatBan; }

    // ============ toString ============

    @Override
    public String toString() {
        return "KhachHang{maKh=" + maKh + ", hoTen='" + hoTen + "', sdt='" + sdt + "', email='" + email + "', diemTichLuy=" + diemTichLuy + '}';
    }
}
