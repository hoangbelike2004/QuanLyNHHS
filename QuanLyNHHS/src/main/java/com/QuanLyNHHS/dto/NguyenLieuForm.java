package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;

public class NguyenLieuForm {
    @NotBlank(message = "Nhập tên nguyên liệu.")
    @Size(max = 100)
    private String tenNl;

    @NotBlank(message = "Nhập đơn vị tính.")
    @Size(max = 20)
    private String donVi;

    @NotNull
    @Pattern(regexp = "HOAT_DONG|NGUNG_SU_DUNG", message = "Trạng thái không hợp lệ.")
    private String trangThai = "HOAT_DONG";

    public String getTenNl() {
        return tenNl;
    }

    public void setTenNl(String tenNl) {
        this.tenNl = tenNl;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
