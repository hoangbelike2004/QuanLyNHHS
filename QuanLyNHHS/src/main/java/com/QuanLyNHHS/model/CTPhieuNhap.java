package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entity biểu diễn dữ liệu chi tiết phiếu nhập; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "CT_PHIEU_NHAP")
public class CTPhieuNhap {
    // id: khóa kép gồm mã phiếu nhập và mã nguyên liệu.
    @EmbeddedId private CTPhieuNhapId id = new CTPhieuNhapId();

    // phieuNhap: phiếu nhập cha của dòng chi tiết.
    @MapsId("maPn")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_PN")
    private PhieuNhap phieuNhap;

    // nguyenLieu: nguyên liệu của dòng chi tiết.
    @MapsId("maNl")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_NL")
    private NguyenLieu nguyenLieu;

    // soLuong: số lượng của dòng chi tiết.
    @Column(name = "SO_LUONG", nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuong;

    // donGia: đơn giá của một đơn vị.
    @Column(name = "DON_GIA", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    // thanhTien: thành tiền của dòng chi tiết.
    @Column(name = "THANH_TIEN", nullable = false, precision = 14, scale = 2)
    private BigDecimal thanhTien;

    /**
     * Đọc id; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public CTPhieuNhapId getId() {
        return id;
    }

    /**
     * Gán id từ tham số vào thuộc tính của đối tượng.
     * @param id khóa kép gồm mã phiếu nhập và mã nguyên liệu
     */
    public void setId(CTPhieuNhapId id) {
        this.id = id;
    }

    /**
     * Đọc phiếu nhập cha của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    /**
     * Gán phiếu nhập cha của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param phieuNhap phiếu nhập cha của dòng chi tiết
     */
    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    /**
     * Đọc nguyên liệu của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public NguyenLieu getNguyenLieu() {
        return nguyenLieu;
    }

    /**
     * Gán nguyên liệu của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param nguyenLieu nguyên liệu của dòng chi tiết
     */
    public void setNguyenLieu(NguyenLieu nguyenLieu) {
        this.nguyenLieu = nguyenLieu;
    }

    /**
     * Đọc số lượng của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getSoLuong() {
        return soLuong;
    }

    /**
     * Gán số lượng của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param soLuong số lượng của dòng chi tiết
     */
    public void setSoLuong(BigDecimal soLuong) {
        this.soLuong = soLuong;
    }

    /**
     * Đọc đơn giá của một đơn vị; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getDonGia() {
        return donGia;
    }

    /**
     * Gán đơn giá của một đơn vị từ tham số vào thuộc tính của đối tượng.
     * @param donGia đơn giá của một đơn vị
     */
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    /**
     * Đọc thành tiền của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    /**
     * Gán thành tiền của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param thanhTien thành tiền của dòng chi tiết
     */
    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}
