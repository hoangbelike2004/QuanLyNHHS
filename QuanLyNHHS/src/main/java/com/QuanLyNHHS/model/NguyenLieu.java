package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entity biểu diễn dữ liệu nguyên liệu; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "NGUYEN_LIEU")
public class NguyenLieu {
    // maNl: mã nguyên liệu.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nl_seq")
    @SequenceGenerator(name = "nl_seq", sequenceName = "SEQ_NGUYEN_LIEU", allocationSize = 1)
    @Column(name = "MA_NL")
    private Long maNl;

    // tenNl: tên nguyên liệu.
    @Column(name = "TEN_NL", nullable = false, length = 100)
    private String tenNl;

    // donVi: đơn vị tính, ví dụ Kg, Con hoặc Đĩa.
    @Column(name = "DON_VI", nullable = false, length = 20)
    private String donVi;

    // soLuongTon: số lượng nguyên liệu hiện còn trong kho.
    @Column(name = "SO_LUONG_TON", nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongTon = BigDecimal.ZERO;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    private String trangThai = "HOAT_DONG";

    /**
     * Đọc mã nguyên liệu; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNl() {
        return maNl;
    }

    /**
     * Gán mã nguyên liệu từ tham số vào thuộc tính của đối tượng.
     * @param maNl mã nguyên liệu
     */
    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }

    /**
     * Đọc tên nguyên liệu; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenNl() {
        return tenNl;
    }

    /**
     * Gán tên nguyên liệu từ tham số vào thuộc tính của đối tượng.
     * @param tenNl tên nguyên liệu
     */
    public void setTenNl(String tenNl) {
        this.tenNl = tenNl;
    }

    /**
     * Đọc đơn vị tính, ví dụ Kg, Con hoặc Đĩa; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getDonVi() {
        return donVi;
    }

    /**
     * Gán đơn vị tính, ví dụ Kg, Con hoặc Đĩa từ tham số vào thuộc tính của đối tượng.
     * @param donVi đơn vị tính, ví dụ Kg, Con hoặc Đĩa
     */
    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    /**
     * Đọc số lượng nguyên liệu hiện còn trong kho; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getSoLuongTon() {
        return soLuongTon;
    }

    /**
     * Gán số lượng nguyên liệu hiện còn trong kho từ tham số vào thuộc tính của đối tượng.
     * @param soLuongTon số lượng nguyên liệu hiện còn trong kho
     */
    public void setSoLuongTon(BigDecimal soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    /**
     * Đọc trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTrangThai() {
        return trangThai;
    }

    /**
     * Gán trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ từ tham số vào thuộc tính của đối tượng.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
