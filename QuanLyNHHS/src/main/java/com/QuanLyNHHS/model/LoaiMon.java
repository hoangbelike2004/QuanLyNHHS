package com.QuanLyNHHS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Bảng LOAI_MON (Ảnh 1 & Ảnh 2)
 * PK: MA_LOAI NUMBER
 * TEN_LOAI VARCHAR2(100)
 * MO_TA VARCHAR2(255)
 * TRANG_THAI VARCHAR2(20)
 */
@Entity
@Table(name = "LOAI_MON")
public class LoaiMon {

    // maLoai: mã loại món.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_loai_mon")
    @SequenceGenerator(name = "seq_loai_mon", sequenceName = "SEQ_LOAI_MON", allocationSize = 1)
    @Column(name = "MA_LOAI")
    private Long maLoai;

    // tenLoai: tên loại món.
    @Column(name = "TEN_LOAI", length = 100, nullable = false)
    private String tenLoai;

    // moTa: nội dung mô tả.
    @Column(name = "MO_TA", length = 255)
    private String moTa;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai; // "Hoạt động", "Tạm dừng"

    /**
     * Constructor rỗng để tạo LoaiMon; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public LoaiMon() {
    }

    /**
     * Tạo đối tượng LoaiMon và gán các giá trị ban đầu từ tham số.
     * @param maLoai mã loại món
     * @param tenLoai tên loại món
     * @param moTa nội dung mô tả
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public LoaiMon(Long maLoai, String tenLoai, String moTa, String trangThai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    /**
     * Tạo đối tượng LoaiMon và gán các giá trị ban đầu từ tham số.
     * @param tenLoai tên loại món
     * @param moTa nội dung mô tả
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public LoaiMon(String tenLoai, String moTa, String trangThai) {
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    /**
     * Đọc mã loại món; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaLoai() {
        return maLoai;
    }

    /**
     * Gán mã loại món từ tham số vào thuộc tính của đối tượng.
     * @param maLoai mã loại món
     */
    public void setMaLoai(Long maLoai) {
        this.maLoai = maLoai;
    }

    /**
     * Đọc tên loại món; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenLoai() {
        return tenLoai;
    }

    /**
     * Gán tên loại món từ tham số vào thuộc tính của đối tượng.
     * @param tenLoai tên loại món
     */
    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    /**
     * Đọc nội dung mô tả; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getMoTa() {
        return moTa;
    }

    /**
     * Gán nội dung mô tả từ tham số vào thuộc tính của đối tượng.
     * @param moTa nội dung mô tả
     */
    public void setMoTa(String moTa) {
        this.moTa = moTa;
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
