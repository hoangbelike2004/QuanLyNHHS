package com.QuanLyNHHS.dto;

import com.QuanLyNHHS.model.NhanVien;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO nhận dữ liệu nhân viên từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class NhanVienForm {
    // hoTen: họ tên.
    @NotBlank(message = "Nhập họ tên nhân viên.")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự.")
    private String hoTen;
    // gioiTinh: giới tính.
    @Pattern(regexp = "Nam|Nữ|Khác|", message = "Giới tính không hợp lệ.")
    private String gioiTinh;
    // sdt: số điện thoại.
    @Size(max = 15, message = "Số điện thoại tối đa 15 ký tự.")
    @Pattern(regexp = "[+0-9 ()-]*", message = "Số điện thoại không hợp lệ.")
    private String sdt;
    // email: địa chỉ email.
    @Email(message = "Email không hợp lệ.")
    @Size(max = 100, message = "Email tối đa 100 ký tự.")
    private String email;
    // chucVu: chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập.
    @NotBlank(message = "Nhập chức vụ.")
    @Size(max = 50, message = "Chức vụ tối đa 50 ký tự.")
    private String chucVu;
    // caLamViec: ca làm việc.
    @Size(max = 100, message = "Ca làm việc tối đa 100 ký tự.")
    private String caLamViec;
    // luong: mức lương dùng BigDecimal để lưu số tiền.
    @DecimalMin(value = "0", message = "Lương không được âm.")
    @Digits(integer = 10, fraction = 2, message = "Lương tối đa 10 chữ số nguyên và 2 chữ số thập phân.")
    private BigDecimal luong;
    // ngayVaoLam: ngày nhân viên vào làm.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayVaoLam;
    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @NotBlank(message = "Chọn trạng thái.")
    @Pattern(regexp = "Đang làm việc|Nghỉ phép|Đã nghỉ việc", message = "Trạng thái nhân viên không hợp lệ.")
    private String trangThai = "Đang làm việc";

    /**
     * Sao chép dữ liệu từ entity sang NhanVienForm để điền sẵn biểu mẫu sửa; không cập nhật database.
     * @param entity entity NhanVien lấy từ database hoặc tạo mới để lưu; khác với DTO chỉ mang dữ liệu của form
     * @return kết quả kiểu {@code NhanVienForm} cho nơi gọi sử dụng
     */
    public static NhanVienForm from(NhanVien entity) {
        // form: đối tượng NhanVienForm chứa dữ liệu nhập của biểu mẫu.
        NhanVienForm form = new NhanVienForm();
        form.setHoTen(entity.getHoTen());
        form.setGioiTinh(entity.getGioiTinh());
        form.setSdt(entity.getSdt());
        form.setEmail(entity.getEmail());
        form.setChucVu(entity.getChucVu());
        form.setCaLamViec(entity.getCaLamViec());
        form.setLuong(entity.getLuong());
        form.setNgayVaoLam(entity.getNgayVaoLam());
        form.setTrangThai(entity.getTrangThai());
        return form;
    }
    /**
     * Đọc họ tên; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHoTen() { return hoTen; }
    /**
     * Gán họ tên từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị họ tên nhận từ form
     */
    public void setHoTen(String value) { hoTen = value == null ? null : value.strip(); }
    /**
     * Đọc giới tính; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getGioiTinh() { return gioiTinh; }
    /**
     * Gán giới tính từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị giới tính nhận từ form
     */
    public void setGioiTinh(String value) { gioiTinh = value == null ? null : value.strip(); }
    /**
     * Đọc số điện thoại; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getSdt() { return sdt; }
    /**
     * Gán số điện thoại từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị số điện thoại nhận từ form
     */
    public void setSdt(String value) { sdt = value == null ? null : value.strip(); }
    /**
     * Đọc địa chỉ email; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getEmail() { return email; }
    /**
     * Gán địa chỉ email từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị địa chỉ email nhận từ form
     */
    public void setEmail(String value) { email = value == null ? null : value.strip(); }
    /**
     * Đọc chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getChucVu() { return chucVu; }
    /**
     * Gán chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập nhận từ form
     */
    public void setChucVu(String value) { chucVu = value == null ? null : value.strip(); }
    /**
     * Đọc ca làm việc; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getCaLamViec() { return caLamViec; }
    /**
     * Gán ca làm việc từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị ca làm việc nhận từ form
     */
    public void setCaLamViec(String value) { caLamViec = value == null ? null : value.strip(); }
    /**
     * Đọc mức lương dùng BigDecimal để lưu số tiền; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getLuong() { return luong; }
    /**
     * Gán mức lương dùng BigDecimal để lưu số tiền từ tham số vào thuộc tính của đối tượng.
     * @param value giá trị mức lương dùng BigDecimal để lưu số tiền nhận từ form
     */
    public void setLuong(BigDecimal value) { luong = value; }
    /**
     * Đọc ngày nhân viên vào làm; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    /**
     * Gán ngày nhân viên vào làm từ tham số vào thuộc tính của đối tượng.
     * @param value giá trị ngày nhân viên vào làm nhận từ form
     */
    public void setNgayVaoLam(LocalDate value) { ngayVaoLam = value; }
    /**
     * Đọc trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTrangThai() { return trangThai; }
    /**
     * Gán trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ nhận từ form
     */
    public void setTrangThai(String value) { trangThai = value == null ? null : value.strip(); }
}
