package com.QuanLyNHHS.model;

import jakarta.persistence.*;

/**
 * Entity biểu diễn dữ liệu tài khoản; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "TAI_KHOAN", uniqueConstraints = @UniqueConstraint(
        name = "UK_TAI_KHOAN_TEN_DANG_NHAP", columnNames = "TEN_DANG_NHAP"))
public class TaiKhoan {
    // maTk: mã tài khoản.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tai_khoan")
    @SequenceGenerator(name = "seq_tai_khoan", sequenceName = "SEQ_TAI_KHOAN", allocationSize = 1)
    @Column(name = "MA_TK")
    private Long maTk;

    // hoTen: họ tên.
    @Column(name = "HO_TEN", nullable = false, length = 100)
    private String hoTen;

    // tenDangNhap: tên đăng nhập của tài khoản.
    @Column(name = "TEN_DANG_NHAP", nullable = false, length = 30)
    private String tenDangNhap;

    // matKhau: chuỗi mật khẩu đã băm BCrypt để lưu trong database.
    @Column(name = "MAT_KHAU", nullable = false, length = 100)
    private String matKhau;

    /**
     * Đọc mã tài khoản; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaTk() { return maTk; }
    /**
     * Đọc họ tên; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHoTen() { return hoTen; }
    /**
     * Gán họ tên từ tham số vào thuộc tính của đối tượng.
     * @param hoTen họ tên
     */
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    /**
     * Đọc tên đăng nhập của tài khoản; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenDangNhap() { return tenDangNhap; }
    /**
     * Gán tên đăng nhập của tài khoản từ tham số vào thuộc tính của đối tượng.
     * @param tenDangNhap tên đăng nhập của tài khoản
     */
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    /**
     * Đọc mật khẩu; ở DTO là chuỗi người dùng nhập, ở TaiKhoan là chuỗi đã băm BCrypt; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getMatKhau() { return matKhau; }
    /**
     * Gán mật khẩu; ở DTO là chuỗi người dùng nhập, ở TaiKhoan là chuỗi đã băm BCrypt từ tham số vào thuộc tính của đối tượng.
     * @param matKhau chuỗi mật khẩu đã băm BCrypt để lưu trong database
     */
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
}
