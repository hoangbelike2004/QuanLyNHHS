package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;

/**
 * DTO nhận dữ liệu nhà cung cấp từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class NhaCungCapForm {
    // tenNcc: tên nhà cung cấp.
    @NotBlank(message = "Nhập tên nhà cung cấp.")
    @Size(max = 150)
    private String tenNcc;

    // sdt: số điện thoại.
    @Pattern(
            regexp = "[0-9+() .-]{0,15}",
            message = "Số điện thoại không hợp lệ (tối đa 15 ký tự).")
    private String sdt;

    // diaChi: địa chỉ liên hệ.
    @Size(max = 200)
    private String diaChi;

    // email: địa chỉ email.
    @Email(message = "Email không hợp lệ.")
    @Size(max = 100)
    private String email;

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
