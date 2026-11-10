package com.QuanLyNHHS.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO nhận dữ liệu phiếu nhập từ form; annotation validation quy định dữ liệu hợp lệ trước khi gọi service.
 */
public class PhieuNhapForm {
    // maNcc: mã nhà cung cấp.
    @NotNull(message = "Chọn nhà cung cấp.")
    @Positive
    private Long maNcc;

    // maNv: mã nhân viên dạng số trong database.
    @NotNull(message = "Chọn nhân viên nhập kho.")
    @Positive
    private Long maNv;

    // ngayNhap: ngày nhập hàng.
    @NotNull(message = "Chọn ngày nhập.")
    @PastOrPresent(message = "Ngày nhập không được ở tương lai.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhap = LocalDate.now();

    // phienBan: số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập.
    private Long phienBan;

    // chiTiet: danh sách các dòng chi tiết thuộc chứng từ hoặc biểu mẫu.
    @NotNull
    @Size(min = 1, max = 100, message = "Phiếu phải có từ 1 đến 100 dòng.")
    private List<@NotNull @Valid DongNhapForm> chiTiet = new ArrayList<>();

    /**
     * Đọc mã nhà cung cấp; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNcc() {
        return maNcc;
    }

    /**
     * Gán mã nhà cung cấp từ tham số vào thuộc tính của đối tượng.
     * @param maNcc mã nhà cung cấp
     */
    public void setMaNcc(Long maNcc) {
        this.maNcc = maNcc;
    }

    /**
     * Đọc mã nhân viên dạng số trong database; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNv() {
        return maNv;
    }

    /**
     * Gán mã nhân viên dạng số trong database từ tham số vào thuộc tính của đối tượng.
     * @param maNv mã nhân viên dạng số trong database
     */
    public void setMaNv(Long maNv) {
        this.maNv = maNv;
    }

    /**
     * Đọc ngày nhập hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    /**
     * Gán ngày nhập hàng từ tham số vào thuộc tính của đối tượng.
     * @param ngayNhap ngày nhập hàng
     */
    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    /**
     * Đọc số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getPhienBan() {
        return phienBan;
    }

    /**
     * Gán số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập từ tham số vào thuộc tính của đối tượng.
     * @param phienBan số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập
     */
    public void setPhienBan(Long phienBan) {
        this.phienBan = phienBan;
    }

    /**
     * Đọc dòng chi tiết hoặc danh sách chi tiết, tùy kiểu dữ liệu khai báo; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public List<DongNhapForm> getChiTiet() {
        return chiTiet;
    }

    /**
     * Gán dòng chi tiết hoặc danh sách chi tiết, tùy kiểu dữ liệu khai báo từ tham số vào thuộc tính của đối tượng.
     * @param chiTiet danh sách các dòng chi tiết thuộc chứng từ hoặc biểu mẫu
     */
    public void setChiTiet(List<DongNhapForm> chiTiet) {
        this.chiTiet = chiTiet;
    }
}
