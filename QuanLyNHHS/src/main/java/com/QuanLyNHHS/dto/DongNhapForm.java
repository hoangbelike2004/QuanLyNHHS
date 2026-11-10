package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu dòng nhập nguyên liệu từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class DongNhapForm {
    // maNl: mã nguyên liệu.
    @NotNull(message = "Chọn nguyên liệu.")
    @Positive
    private Long maNl;

    // soLuong: số lượng của dòng chi tiết.
    @NotNull(message = "Nhập số lượng.")
    @DecimalMin(value = "0.01", message = "Số lượng phải lớn hơn 0.")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal soLuong;

    // donGia: đơn giá của một đơn vị.
    @NotNull(message = "Nhập đơn giá.")
    @DecimalMin(value = "0", message = "Đơn giá không được âm.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal donGia;

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
}
