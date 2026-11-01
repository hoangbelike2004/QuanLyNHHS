package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class DongNhapForm {
    @NotNull(message = "Chọn nguyên liệu.")
    @Positive
    private Long maNl;

    @NotNull(message = "Nhập số lượng.")
    @DecimalMin(value = "0.01", message = "Số lượng phải lớn hơn 0.")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal soLuong;

    @NotNull(message = "Nhập đơn giá.")
    @DecimalMin(value = "0", message = "Đơn giá không được âm.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal donGia;

    public Long getMaNl() {
        return maNl;
    }

    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }

    public BigDecimal getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(BigDecimal soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
}
