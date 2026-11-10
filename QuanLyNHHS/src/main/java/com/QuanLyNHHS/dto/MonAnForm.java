package com.QuanLyNHHS.dto;

import com.QuanLyNHHS.model.MonAn;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu món ăn từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class MonAnForm {
    // maLoai: mã loại món.
    @NotNull(message = "Chọn loại món.")
    @Positive(message = "Mã loại món không hợp lệ.")
    private Long maLoai;
    // tenMon: tên món ăn.
    @NotBlank(message = "Nhập tên món.")
    @Size(max = 150, message = "Tên món tối đa 150 ký tự.")
    private String tenMon;
    // donVi: đơn vị tính, ví dụ Kg, Con hoặc Đĩa.
    @NotBlank(message = "Nhập đơn vị tính.")
    @Size(max = 20, message = "Đơn vị tính tối đa 20 ký tự.")
    private String donVi;
    // donGia: đơn giá của một đơn vị.
    @NotNull(message = "Nhập đơn giá.")
    @DecimalMin(value = "0", message = "Đơn giá không được âm.")
    @Digits(integer = 10, fraction = 2, message = "Đơn giá tối đa 10 chữ số nguyên và 2 chữ số thập phân.")
    private BigDecimal donGia;
    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @NotBlank(message = "Chọn trạng thái.")
    @Pattern(regexp = "Đang phục vụ|Tạm hết", message = "Trạng thái món không hợp lệ.")
    private String trangThai = "Đang phục vụ";
    // hinhAnh: đường dẫn hình ảnh món ăn.
    @Size(max = 1000, message = "Đường dẫn ảnh tối đa 1000 ký tự.")
    @Pattern(regexp = "(?:https?://[^\\s]+)?", message = "Đường dẫn ảnh phải bắt đầu bằng http:// hoặc https://.")
    private String hinhAnh;
    // moTa: nội dung mô tả.
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự.")
    private String moTa;

    /**
     * Sao chép dữ liệu từ entity sang MonAnForm để điền sẵn biểu mẫu sửa; không cập nhật database.
     * @param entity entity MonAn lấy từ database hoặc tạo mới để lưu; khác với DTO chỉ mang dữ liệu của form
     * @return kết quả kiểu {@code MonAnForm} cho nơi gọi sử dụng
     */
    public static MonAnForm from(MonAn entity) {
        // form: đối tượng MonAnForm chứa dữ liệu nhập của biểu mẫu.
        MonAnForm form = new MonAnForm();
        form.setMaLoai(entity.getMaLoai());
        form.setTenMon(entity.getTenMon());
        form.setDonVi(entity.getDonVi());
        form.setDonGia(entity.getDonGia());
        form.setTrangThai(entity.getTrangThai());
        form.setHinhAnh(entity.getHinhAnh());
        form.setMoTa(entity.getMoTa());
        return form;
    }
    /**
     * Đọc mã loại món; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaLoai() { return maLoai; }
    /**
     * Gán mã loại món từ tham số vào thuộc tính của đối tượng.
     * @param value giá trị mã loại món nhận từ form
     */
    public void setMaLoai(Long value) { maLoai = value; }
    /**
     * Đọc tên món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenMon() { return tenMon; }
    /**
     * Gán tên món ăn từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị tên món ăn nhận từ form
     */
    public void setTenMon(String value) { tenMon = value == null ? null : value.strip(); }
    /**
     * Đọc đơn vị tính, ví dụ Kg, Con hoặc Đĩa; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getDonVi() { return donVi; }
    /**
     * Gán đơn vị tính, ví dụ Kg, Con hoặc Đĩa từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị đơn vị tính, ví dụ Kg, Con hoặc Đĩa nhận từ form
     */
    public void setDonVi(String value) { donVi = value == null ? null : value.strip(); }
    /**
     * Đọc đơn giá của một đơn vị; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getDonGia() { return donGia; }
    /**
     * Gán đơn giá của một đơn vị từ tham số vào thuộc tính của đối tượng.
     * @param value giá trị đơn giá của một đơn vị nhận từ form
     */
    public void setDonGia(BigDecimal value) { donGia = value; }
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
    /**
     * Đọc đường dẫn hình ảnh món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHinhAnh() { return hinhAnh; }
    /**
     * Gán đường dẫn hình ảnh món ăn từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị đường dẫn hình ảnh món ăn nhận từ form
     */
    public void setHinhAnh(String value) { hinhAnh = value == null ? null : value.strip(); }
    /**
     * Đọc nội dung mô tả; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getMoTa() { return moTa; }
    /**
     * Gán nội dung mô tả từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị nội dung mô tả nhận từ form
     */
    public void setMoTa(String value) { moTa = value == null ? null : value.strip(); }
}
