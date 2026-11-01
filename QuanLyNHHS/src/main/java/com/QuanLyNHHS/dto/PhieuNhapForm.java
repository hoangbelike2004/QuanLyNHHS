package com.QuanLyNHHS.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;


public class PhieuNhapForm {
    @NotNull(message = "Chọn nhà cung cấp.")
    @Positive
    private Long maNcc;

    @NotNull(message = "Chọn nhân viên nhập kho.")
    @Positive
    private Long maNv;

    @NotNull(message = "Chọn ngày nhập.")
    @PastOrPresent(message = "Ngày nhập không được ở tương lai.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhap = LocalDate.now();


    private Long phienBan;

    @NotNull
    @Size(min = 1, max = 100, message = "Phiếu phải có từ 1 đến 100 dòng.")
    private List<@NotNull @Valid DongNhapForm> chiTiet = new ArrayList<>();

    public Long getMaNcc() { return maNcc; }
    public void setMaNcc(Long maNcc) { this.maNcc = maNcc; }

    public Long getMaNv() { return maNv; }
    public void setMaNv(Long maNv) { this.maNv = maNv; }

    public LocalDate getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }

    public Long getPhienBan() { return phienBan; }
    public void setPhienBan(Long phienBan) { this.phienBan = phienBan; }

    public List<DongNhapForm> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongNhapForm> chiTiet) { this.chiTiet = chiTiet; }
}
