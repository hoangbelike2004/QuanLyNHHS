package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity biểu diễn dữ liệu phiếu nhập; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "PHIEU_NHAP")
public class PhieuNhap {
    // maPn: mã phiếu nhập.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pn_seq")
    @SequenceGenerator(name = "pn_seq", sequenceName = "SEQ_PHIEU_NHAP", allocationSize = 1)
    @Column(name = "MA_PN")
    private Long maPn;

    // nhaCungCap: nhà cung cấp của phiếu nhập.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_NCC")
    private NhaCungCap nhaCungCap;

    // maNv: mã nhân viên dạng số trong database.
    @Column(name = "MA_NV", nullable = false)
    private Long maNv;

    // ngayNhap: ngày nhập hàng.
    @Column(name = "NGAY_NHAP", nullable = false)
    private LocalDate ngayNhap;

    // tongTien: tổng tiền của chứng từ.
    @Column(name = "TONG_TIEN", nullable = false, precision = 14, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    // phienBan: số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập.
    @Version
    @Column(name = "PHIEN_BAN", nullable = false)
    private Long phienBan;

    // chiTiet: danh sách các dòng chi tiết thuộc chứng từ hoặc biểu mẫu.
    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.maNl ASC")
    private List<CTPhieuNhap> chiTiet = new ArrayList<>();

    /**
     * Đọc mã phiếu nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaPn() {
        return maPn;
    }

    /**
     * Gán mã phiếu nhập từ tham số vào thuộc tính của đối tượng.
     * @param maPn mã phiếu nhập
     */
    public void setMaPn(Long maPn) {
        this.maPn = maPn;
    }

    /**
     * Đọc nhà cung cấp của phiếu nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    /**
     * Gán nhà cung cấp của phiếu nhập từ tham số vào thuộc tính của đối tượng.
     * @param nhaCungCap nhà cung cấp của phiếu nhập
     */
    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    /**
     * Đọc mã nhân viên dạng số trong database; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNv() {
        return maNv;
    }

    /**
     * Gán mã nhân viên dạng số trong database từ tham số vào thuộc tính của đối tượng.
     * @param maNv mã nhân viên dạng số trong database
     */
    public void setMaNv(Long maNv) {
        this.maNv = maNv;
    }

    /**
     * Đọc ngày nhập hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    /**
     * Gán ngày nhập hàng từ tham số vào thuộc tính của đối tượng.
     * @param ngayNhap ngày nhập hàng
     */
    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    /**
     * Đọc tổng tiền của chứng từ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getTongTien() {
        return tongTien;
    }

    /**
     * Gán tổng tiền của chứng từ từ tham số vào thuộc tính của đối tượng.
     * @param tongTien tổng tiền của chứng từ
     */
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Đọc số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getPhienBan() {
        return phienBan;
    }

    /**
     * Gán số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập từ tham số vào thuộc tính của đối tượng.
     * @param phienBan số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập
     */
    public void setPhienBan(Long phienBan) {
        this.phienBan = phienBan;
    }

    /**
     * Đọc dòng chi tiết hoặc danh sách chi tiết, tùy kiểu dữ liệu khai báo; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public List<CTPhieuNhap> getChiTiet() {
        return chiTiet;
    }

    /**
     * Gán dòng chi tiết hoặc danh sách chi tiết, tùy kiểu dữ liệu khai báo từ tham số vào thuộc tính của đối tượng.
     * @param chiTiet danh sách các dòng chi tiết thuộc chứng từ hoặc biểu mẫu
     */
    public void setChiTiet(List<CTPhieuNhap> chiTiet) {
        this.chiTiet = chiTiet;
    }
}
