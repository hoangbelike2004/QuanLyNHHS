package com.QuanLyNHHS.dto;

import com.QuanLyNHHS.model.LoaiMon;
import jakarta.validation.constraints.*;

/** Chỉ nhận các trường người dùng được phép thay đổi, không nhận khóa chính. */
public class LoaiMonForm {
    // tenLoai: tên loại món.
    @NotBlank(message = "Nhập tên loại món.")
    @Size(max = 100, message = "Tên loại món tối đa 100 ký tự.")
    private String tenLoai;
    // moTa: nội dung mô tả.
    @Size(max = 255, message = "Mô tả tối đa 255 ký tự.")
    private String moTa;
    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @NotBlank(message = "Chọn trạng thái.")
    @Pattern(regexp = "Hoạt động|Tạm dừng", message = "Trạng thái loại món không hợp lệ.")
    private String trangThai = "Hoạt động";

    /**
     * Sao chép dữ liệu từ entity sang LoaiMonForm để điền sẵn biểu mẫu sửa; không cập nhật database.
     * @param entity entity LoaiMon lấy từ database hoặc tạo mới để lưu; khác với DTO chỉ mang dữ liệu của form
     * @return kết quả kiểu {@code LoaiMonForm} cho nơi gọi sử dụng
     */
    public static LoaiMonForm from(LoaiMon entity) {
        // form: đối tượng LoaiMonForm chứa dữ liệu nhập của biểu mẫu.
        LoaiMonForm form = new LoaiMonForm();
        form.setTenLoai(entity.getTenLoai());
        form.setMoTa(entity.getMoTa());
        form.setTrangThai(entity.getTrangThai());
        return form;
    }
    /**
     * Đọc tên loại món; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenLoai() { return tenLoai; }
    /**
     * Gán tên loại món từ tham số vào thuộc tính của đối tượng. Bỏ khoảng trắng đầu/cuối nếu đầu vào không null.
     * @param value giá trị tên loại món nhận từ form
     */
    public void setTenLoai(String value) { tenLoai = value == null ? null : value.strip(); }
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
