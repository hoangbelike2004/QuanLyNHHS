
package com.QuanLyNHHS.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity biểu diễn dữ liệu hóa đơn; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "HOA_DON")
public class HoaDon {

    // maHd: mã hóa đơn.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_HD")
    private Long maHd;

    // maBan: mã bàn ăn.
    @Column(name = "MA_BAN")
    private Long maBan;

    // maNv: mã nhân viên dạng số trong database.
    @Column(name = "MA_NV")
    private Long maNv;

    // maKh: mã khách hàng.
    @Column(name = "MA_KH")
    private Long maKh;

    // ngayLap: ngày lập hóa đơn.
    @Column(name = "NGAY_LAP")
    private LocalDate ngayLap;

    // giamGia: số tiền giảm trực tiếp, không phải phần trăm.
    @Column(name = "GIAM_GIA", precision = 12, scale = 2)
    private BigDecimal giamGia;

    // tongTien: tổng tiền của chứng từ.
    @Column(name = "TONG_TIEN", precision = 14, scale = 2)
    private BigDecimal tongTien;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    /**
     * Constructor rỗng để tạo HoaDon; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public HoaDon() {}

    /**
     * Đọc mã hóa đơn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaHd() { return maHd; }
    /**
     * Gán mã hóa đơn từ tham số vào thuộc tính của đối tượng.
     * @param maHd mã hóa đơn
     */
    public void setMaHd(Long maHd) { this.maHd = maHd; }

    /**
     * Đọc mã bàn ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaBan() { return maBan; }
    /**
     * Gán mã bàn ăn từ tham số vào thuộc tính của đối tượng.
     * @param maBan mã bàn ăn
     */
    public void setMaBan(Long maBan) { this.maBan = maBan; }

    /**
     * Đọc mã nhân viên dạng số trong database; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNv() { return maNv; }
    /**
     * Gán mã nhân viên dạng số trong database từ tham số vào thuộc tính của đối tượng.
     * @param maNv mã nhân viên dạng số trong database
     */
    public void setMaNv(Long maNv) { this.maNv = maNv; }

    /**
     * Đọc mã khách hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaKh() { return maKh; }
    /**
     * Gán mã khách hàng từ tham số vào thuộc tính của đối tượng.
     * @param maKh mã khách hàng
     */
    public void setMaKh(Long maKh) { this.maKh = maKh; }

    /**
     * Đọc ngày lập hóa đơn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayLap() { return ngayLap; }
    /**
     * Gán ngày lập hóa đơn từ tham số vào thuộc tính của đối tượng.
     * @param ngayLap ngày lập hóa đơn
     */
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    /**
     * Đọc số tiền giảm trực tiếp, không phải phần trăm; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getGiamGia() { return giamGia; }
    /**
     * Gán số tiền giảm trực tiếp, không phải phần trăm từ tham số vào thuộc tính của đối tượng.
     * @param giamGia số tiền giảm trực tiếp, không phải phần trăm
     */
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }

    /**
     * Đọc tổng tiền của chứng từ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getTongTien() { return tongTien; }
    /**
     * Gán tổng tiền của chứng từ từ tham số vào thuộc tính của đối tượng.
     * @param tongTien tổng tiền của chứng từ
     */
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    /**
     * Đọc trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTrangThai() { return trangThai; }
    /**
     * Gán trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ từ tham số vào thuộc tính của đối tượng.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
