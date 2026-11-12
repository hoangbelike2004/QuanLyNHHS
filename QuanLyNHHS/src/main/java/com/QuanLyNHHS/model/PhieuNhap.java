package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PHIEU_NHAP")
public class PhieuNhap {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pn_seq")
    @SequenceGenerator(name = "pn_seq", sequenceName = "SEQ_PHIEU_NHAP", allocationSize = 1)
    @Column(name = "MA_PN")
    private Long maPn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_NCC")
    private NhaCungCap nhaCungCap;

    @Column(name = "MA_NV", nullable = false)
    private Long maNv;

    @Column(name = "NGAY_NHAP", nullable = false)
    private LocalDate ngayNhap;

    @Column(name = "TONG_TIEN", nullable = false, precision = 14, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Version
    @Column(name = "PHIEN_BAN", nullable = false)
    private Long phienBan;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.maNl ASC")
    private List<CTPhieuNhap> chiTiet = new ArrayList<>();

    public Long getMaPn() {
        return maPn;
    }

    public void setMaPn(Long maPn) {
        this.maPn = maPn;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public Long getMaNv() {
        return maNv;
    }

    public void setMaNv(Long maNv) {
        this.maNv = maNv;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public Long getPhienBan() {
        return phienBan;
    }

    public void setPhienBan(Long phienBan) {
        this.phienBan = phienBan;
    }

    public List<CTPhieuNhap> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<CTPhieuNhap> chiTiet) {
        this.chiTiet = chiTiet;
    }
}
