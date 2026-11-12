package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity mapping tới bảng BAN_AN trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "BAN_AN")
public class BanAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_BAN")
    private Long maBan;

    @Column(name = "TEN_BAN", length = 50)
    private String tenBan;

    @Column(name = "SO_CHO")
    private Integer soCho;

    /** Khu vực: Tầng 1, Tầng 2, Sân vườn, Phòng VIP ... */
    @Column(name = "KHU_VUC", length = 50)
    private String khuVuc;

    /** Trạng thái: Trống / Đã đặt / Đang dùng */
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    @OneToMany(mappedBy = "banAn", fetch = FetchType.LAZY)
    private List<DatBan> danhSachDatBan;

    // ============ Constructors ============

    public BanAn() {}

    public BanAn(Long maBan, String tenBan, Integer soCho, String khuVuc, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.soCho = soCho;
        this.khuVuc = khuVuc;
        this.trangThai = trangThai;
    }

    // ============ Getters & Setters ============

    public Long getMaBan() { return maBan; }
    public void setMaBan(Long maBan) { this.maBan = maBan; }

    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }

    public Integer getSoCho() { return soCho; }
    public void setSoCho(Integer soCho) { this.soCho = soCho; }

    public String getKhuVuc() { return khuVuc; }
    public void setKhuVuc(String khuVuc) { this.khuVuc = khuVuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public List<DatBan> getDanhSachDatBan() { return danhSachDatBan; }
    public void setDanhSachDatBan(List<DatBan> danhSachDatBan) { this.danhSachDatBan = danhSachDatBan; }

    // ============ toString ============

    @Override
    public String toString() {
        return "BanAn{maBan=" + maBan + ", tenBan='" + tenBan + "', soCho=" + soCho + ", khuVuc='" + khuVuc + "', trangThai='" + trangThai + "'}";
    }
}
