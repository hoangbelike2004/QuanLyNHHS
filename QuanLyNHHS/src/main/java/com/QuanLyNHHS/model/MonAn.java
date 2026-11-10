package com.QuanLyNHHS.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Bảng MON_AN (Ảnh 1 & Ảnh 2)
 * PK: MA_MON NUMBER
 * FK: MA_LOAI NUMBER (Liên kết đến LOAI_MON)
 * TEN_MON VARCHAR2(150)
 * DON_VI VARCHAR2(20)
 * DON_GIA NUMBER(12,2)
 * TRANG_THAI VARCHAR2(20)
 */
@Entity
@Table(name = "MON_AN")
public class MonAn {

    // maMon: mã món ăn.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mon_an")
    @SequenceGenerator(name = "seq_mon_an", sequenceName = "SEQ_MON_AN", allocationSize = 1)
    @Column(name = "MA_MON")
    private Long maMon;

    // loaiMon: loại món được gắn với món ăn.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "MA_LOAI",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MON_AN_LOAI_MON"))
    private LoaiMon loaiMon;

    // tenMon: tên món ăn.
    @Column(name = "TEN_MON", length = 150, nullable = false)
    private String tenMon;

    // donVi: đơn vị tính, ví dụ Kg, Con hoặc Đĩa.
    @Column(name = "DON_VI", length = 20)
    private String donVi;

    // donGia: đơn giá của một đơn vị.
    @Column(name = "DON_GIA", precision = 12, scale = 2)
    private BigDecimal donGia;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai; // "Đang phục vụ", "Tạm hết"

    // Thông tin bổ sung của món ăn
    // hinhAnh: đường dẫn hình ảnh món ăn.
    @Column(name = "HINH_ANH", length = 1000)
    private String hinhAnh;

    // moTa: nội dung mô tả.
    @Column(name = "MO_TA", length = 1000)
    private String moTa;

    /**
     * Constructor rỗng để tạo MonAn; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public MonAn() {
    }

    /**
     * Tạo đối tượng MonAn và gán các giá trị ban đầu từ tham số.
     * @param loaiMon loại món được gắn với món ăn
     * @param tenMon tên món ăn
     * @param donVi đơn vị tính, ví dụ Kg, Con hoặc Đĩa
     * @param donGia đơn giá của một đơn vị
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public MonAn(LoaiMon loaiMon, String tenMon, String donVi, BigDecimal donGia, String trangThai) {
        this.loaiMon = loaiMon;
        this.tenMon = tenMon;
        this.donVi = donVi;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    /**
     * Tạo đối tượng MonAn và gán các giá trị ban đầu từ tham số.
     * @param maMon mã món ăn
     * @param loaiMon loại món được gắn với món ăn
     * @param tenMon tên món ăn
     * @param donVi đơn vị tính, ví dụ Kg, Con hoặc Đĩa
     * @param donGia đơn giá của một đơn vị
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public MonAn(Long maMon, LoaiMon loaiMon, String tenMon, String donVi, BigDecimal donGia, String trangThai) {
        this.maMon = maMon;
        this.loaiMon = loaiMon;
        this.tenMon = tenMon;
        this.donVi = donVi;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    /**
     * Đọc mã món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaMon() {
        return maMon;
    }

    /**
     * Gán mã món ăn từ tham số vào thuộc tính của đối tượng.
     * @param maMon mã món ăn
     */
    public void setMaMon(Long maMon) {
        this.maMon = maMon;
    }

    /**
     * Đọc loại món được gắn với món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    /**
     * Gán loại món được gắn với món ăn từ tham số vào thuộc tính của đối tượng.
     * @param loaiMon loại món được gắn với món ăn
     */
    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    /**
     * Đọc tên món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenMon() {
        return tenMon;
    }

    /**
     * Gán tên món ăn từ tham số vào thuộc tính của đối tượng.
     * @param tenMon tên món ăn
     */
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
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
     * Đọc đơn giá của một đơn vị; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getDonGia() {
        return donGia;
    }

    /**
     * Gán đơn giá của một đơn vị từ tham số vào thuộc tính của đối tượng.
     * @param donGia đơn giá của một đơn vị
     */
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
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

    // Helpers cho Thymeleaf UI
    /**
     * Lấy mã của đối tượng loaiMon liên quan; không có loại thì trả null, không phải cột mã độc lập trong entity món.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaLoai() {
        return loaiMon != null ? loaiMon.getMaLoai() : null;
    }

    /**
     * Lấy tên từ đối tượng loaiMon để giao diện truy cập trực tiếp; loaiMon null thì trả null.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenLoai() {
        return loaiMon != null ? loaiMon.getTenLoai() : null;
    }

    /**
     * Trả thuộc tính donVi dưới tên donViTinh để tương thích biểu thức Thymeleaf.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getDonViTinh() {
        return donVi;
    }

    /**
     * Gán tham số donViTinh vào thuộc tính donVi; đây là tên gọi thay thế, không tạo thêm cột trong database.
     * @param donViTinh tên gọi khác của thuộc tính donVi để giao diện truy cập
     */
    public void setDonViTinh(String donViTinh) {
        this.donVi = donViTinh;
    }

    /**
     * Đọc đường dẫn hình ảnh món ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHinhAnh() {
        return hinhAnh;
    }

    /**
     * Gán đường dẫn hình ảnh món ăn từ tham số vào thuộc tính của đối tượng.
     * @param hinhAnh đường dẫn hình ảnh món ăn
     */
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
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

}
