package com.QuanLyNHHS.dto;

import jakarta.validation.constraints.*;

/**
 * DTO nhận dữ liệu nguyên liệu từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class NguyenLieuForm {
    // tenNl: tên nguyên liệu.
    @NotBlank(message = "Nhập tên nguyên liệu.")
    @Size(max = 100)
    private String tenNl;

    // donVi: đơn vị tính, ví dụ Kg, Con hoặc Đĩa.
    @NotBlank(message = "Nhập đơn vị tính.")
    @Size(max = 20)
    private String donVi;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @NotNull
    @Pattern(regexp = "HOAT_DONG|NGUNG_SU_DUNG", message = "Trạng thái không hợp lệ.")
    private String trangThai = "HOAT_DONG";

    /**
     * Đọc tên nguyên liệu; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenNl() {
        return tenNl;
    }

    /**
     * Gán tên nguyên liệu từ tham số vào thuộc tính của đối tượng.
     * @param tenNl tên nguyên liệu
     */
    public void setTenNl(String tenNl) {
        this.tenNl = tenNl;
    }

    /**
     * Đọc đơn vị tính, ví dụ Kg, Con hoặc Đĩa; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getDonVi() {
        return donVi;
    }

    /**
     * Gán đơn vị tính, ví dụ Kg, Con hoặc Đĩa từ tham số vào thuộc tính của đối tượng.
     * @param donVi đơn vị tính, ví dụ Kg, Con hoặc Đĩa
     */
    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    /**
     * Đọc trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTrangThai() {
        return trangThai;
    }

    /**
     * Gán trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ từ tham số vào thuộc tính của đối tượng.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
