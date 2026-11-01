package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;


public class NhaCungCapForm {
    @NotBlank(message = "Nhập tên nhà cung cấp.")
    @Size(max = 150)
    private String tenNcc;

    @Pattern(regexp = "[0-9+() .-]{0,15}", message = "Số điện thoại không hợp lệ (tối đa 15 ký tự).")
    private String sdt;

    @Size(max = 200)
    private String diaChi;

    @Email(message = "Email không hợp lệ.")
    @Size(max = 100)
    private String email;

    public String getTenNcc() { return tenNcc; }
    public void setTenNcc(String tenNcc) { this.tenNcc = tenNcc; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
