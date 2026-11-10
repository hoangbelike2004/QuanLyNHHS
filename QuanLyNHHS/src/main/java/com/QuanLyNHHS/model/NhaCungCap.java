package com.QuanLyNHHS.model;

import jakarta.persistence.*;

/**
 * Entity biểu diễn dữ liệu nhà cung cấp; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "NHA_CUNG_CAP")
public class NhaCungCap {
    // maNcc: mã nhà cung cấp.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ncc_seq")
    @SequenceGenerator(name = "ncc_seq", sequenceName = "SEQ_NHA_CUNG_CAP", allocationSize = 1)
    @Column(name = "MA_NCC")
    private Long maNcc;

    // tenNcc: tên nhà cung cấp.
    @Column(name = "TEN_NCC", nullable = false, length = 150)
    private String tenNcc;

    // sdt: số điện thoại.
    @Column(name = "SDT", length = 15)
    private String sdt;

    // diaChi: địa chỉ liên hệ.
    @Column(name = "DIA_CHI", length = 200)
    private String diaChi;

    // email: địa chỉ email.
    @Column(name = "EMAIL", length = 100)
    private String email;

    /**
     * Đọc mã nhà cung cấp; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNcc() {
        return maNcc;
    }

    /**
     * Gán mã nhà cung cấp từ tham số vào thuộc tính của đối tượng.
     * @param maNcc mã nhà cung cấp
     */
    public void setMaNcc(Long maNcc) {
        this.maNcc = maNcc;
    }

    /**
     * Đọc tên nhà cung cấp; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenNcc() {
        return tenNcc;
    }

    /**
     * Gán tên nhà cung cấp từ tham số vào thuộc tính của đối tượng.
     * @param tenNcc tên nhà cung cấp
     */
    public void setTenNcc(String tenNcc) {
        this.tenNcc = tenNcc;
    }

    /**
     * Đọc số điện thoại; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getSdt() {
        return sdt;
    }

    /**
     * Gán số điện thoại từ tham số vào thuộc tính của đối tượng.
     * @param sdt số điện thoại
     */
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    /**
     * Đọc địa chỉ liên hệ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getDiaChi() {
        return diaChi;
    }

    /**
     * Gán địa chỉ liên hệ từ tham số vào thuộc tính của đối tượng.
     * @param diaChi địa chỉ liên hệ
     */
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    /**
     * Đọc địa chỉ email; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gán địa chỉ email từ tham số vào thuộc tính của đối tượng.
     * @param email địa chỉ email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
