package com.QuanLyNHHS.dto;

import java.util.Locale;
import jakarta.validation.constraints.*;

/**
 * DTO nhận dữ liệu đăng ký tài khoản từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class DangKyForm {
    // hoTen: họ tên.
    @NotBlank(message = "Nhập họ tên.")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự.")
    private String hoTen;

    // tenDangNhap: tên đăng nhập của tài khoản.
    @NotBlank(message = "Nhập tên đăng nhập.")
    @Pattern(regexp = "[a-z0-9_]{4,30}", message = "Tên đăng nhập gồm 4–30 chữ cái không dấu, chữ số hoặc dấu gạch dưới.")
    private String tenDangNhap;

    // matKhau: mật khẩu người dùng nhập; chưa băm ở bước nhận form.
    @NotBlank(message = "Nhập mật khẩu.")
    @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự.")
    private String matKhau;

    // xacNhanMatKhau: mật khẩu nhập lại để đối chiếu trước khi đăng ký.
    @NotBlank(message = "Nhập lại mật khẩu.")
    @Size(max = 72, message = "Mật khẩu nhập lại quá dài.")
    private String xacNhanMatKhau;

    /**
     * Đọc họ tên; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHoTen() { return hoTen; }
    /**
     * Gán họ tên từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param hoTen họ tên
     */
    public void setHoTen(String hoTen) { this.hoTen = hoTen == null ? null : hoTen.strip(); }
    /**
     * Đọc tên đăng nhập của tài khoản; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenDangNhap() { return tenDangNhap; }
    /**
     * Gán tên đăng nhập của tài khoản từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối và chuyển chữ thường trước khi gán.
     * @param tenDangNhap tên đăng nhập của tài khoản
     */
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap == null ? null : tenDangNhap.strip().toLowerCase(Locale.ROOT);
    }
    /**
     * Đọc mật khẩu; ở DTO là chuỗi người dùng nhập, ở TaiKhoan là chuỗi đã băm BCrypt; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getMatKhau() { return matKhau; }
    /**
     * Gán mật khẩu; ở DTO là chuỗi người dùng nhập, ở TaiKhoan là chuỗi đã băm BCrypt từ tham số vào thuộc tính của đối tượng.
     * @param matKhau mật khẩu người dùng nhập; chưa băm ở bước nhận form
     */
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    /**
     * Đọc mật khẩu nhập lại để đối chiếu trước khi đăng ký; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getXacNhanMatKhau() { return xacNhanMatKhau; }
    /**
     * Gán mật khẩu nhập lại để đối chiếu trước khi đăng ký từ tham số vào thuộc tính của đối tượng.
     * @param xacNhanMatKhau mật khẩu nhập lại để đối chiếu trước khi đăng ký
     */
    public void setXacNhanMatKhau(String xacNhanMatKhau) { this.xacNhanMatKhau = xacNhanMatKhau; }
}
