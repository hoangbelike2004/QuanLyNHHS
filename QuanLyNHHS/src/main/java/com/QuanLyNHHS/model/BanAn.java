package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity mapping tới bảng BAN_AN trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "BAN_AN")
public class BanAn {

    // maBan: mã bàn ăn.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_BAN")
    private Long maBan;

    // tenBan: tên bàn ăn.
    @Column(name = "TEN_BAN", length = 50)
    private String tenBan;

    // soCho: số chỗ ngồi của bàn.
    @Column(name = "SO_CHO")
    private Integer soCho;

    /** Khu vực: Tầng 1, Tầng 2, Sân vườn, Phòng VIP ... */
    @Column(name = "KHU_VUC", length = 50)
    private String khuVuc;

    /** Trạng thái: Trống / Đã đặt / Đang dùng */
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    // danhSachDatBan: các lượt đặt bàn liên quan đến bàn hoặc khách hàng.
    @OneToMany(mappedBy = "banAn", fetch = FetchType.LAZY)
    private List<DatBan> danhSachDatBan;

    // ============ Constructors ============

    /**
     * Constructor rỗng để tạo BanAn; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public BanAn() {}

    /**
     * Tạo đối tượng BanAn và gán các giá trị ban đầu từ tham số.
     * @param maBan mã bàn ăn
     * @param tenBan tên bàn ăn
     * @param soCho số chỗ ngồi của bàn
     * @param khuVuc khu vực bố trí bàn
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public BanAn(Long maBan, String tenBan, Integer soCho, String khuVuc, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.soCho = soCho;
        this.khuVuc = khuVuc;
        this.trangThai = trangThai;
    }

    // ============ Getters & Setters ============

    /**
     * Đọc mã bàn ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaBan() { return maBan; }
    /**
     * Gán mã bàn ăn từ tham số vào thuộc tính của đối tượng.
     * @param maBan mã bàn ăn
     */
    public void setMaBan(Long maBan) { this.maBan = maBan; }

    /**
     * Đọc tên bàn ăn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTenBan() { return tenBan; }
    /**
     * Gán tên bàn ăn từ tham số vào thuộc tính của đối tượng.
     * @param tenBan tên bàn ăn
     */
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }

    /**
     * Đọc số chỗ ngồi của bàn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Integer getSoCho() { return soCho; }
    /**
     * Gán số chỗ ngồi của bàn từ tham số vào thuộc tính của đối tượng.
     * @param soCho số chỗ ngồi của bàn
     */
    public void setSoCho(Integer soCho) { this.soCho = soCho; }

    /**
     * Đọc khu vực bố trí bàn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getKhuVuc() { return khuVuc; }
    /**
     * Gán khu vực bố trí bàn từ tham số vào thuộc tính của đối tượng.
     * @param khuVuc khu vực bố trí bàn
     */
    public void setKhuVuc(String khuVuc) { this.khuVuc = khuVuc; }

    /**
     * Đọc trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getTrangThai() { return trangThai; }
    /**
     * Gán trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ từ tham số vào thuộc tính của đối tượng.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    /**
     * Đọc các lượt đặt bàn liên quan đến bàn hoặc khách hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public List<DatBan> getDanhSachDatBan() { return danhSachDatBan; }
    /**
     * Gán các lượt đặt bàn liên quan đến bàn hoặc khách hàng từ tham số vào thuộc tính của đối tượng.
     * @param danhSachDatBan các lượt đặt bàn liên quan đến bàn hoặc khách hàng
     */
    public void setDanhSachDatBan(List<DatBan> danhSachDatBan) { this.danhSachDatBan = danhSachDatBan; }

    // ============ toString ============

    /**
     * Tạo chuỗi mô tả các thuộc tính hiện tại để theo dõi/log; không ghi dữ liệu vào database.
     * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
     */
    @Override
    public String toString() {
        return "BanAn{maBan=" + maBan + ", tenBan='" + tenBan + "', soCho=" + soCho + ", khuVuc='" + khuVuc + "', trangThai='" + trangThai + "'}";
    }
}
