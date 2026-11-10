package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping tới bảng DAT_BAN trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "DAT_BAN")
public class DatBan {

    // maDat: mã đặt bàn.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DAT")
    private Long maDat;

    // khachHang: khách hàng gắn với đặt bàn hoặc đang chỉnh sửa.
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MA_KH", referencedColumnName = "MA_KH")
    private KhachHang khachHang;

    // banAn: đối tượng bàn ăn liên quan.
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MA_BAN", referencedColumnName = "MA_BAN")
    private BanAn banAn;

    // ngayDat: ngày đặt bàn.
    @Column(name = "NGAY_DAT")
    private LocalDate ngayDat;

    // soNguoi: số khách trong lượt đặt bàn.
    @Column(name = "SO_NGUOI")
    private Integer soNguoi;

    // tienCoc: số tiền đặt cọc.
    @Column(name = "TIEN_COC", precision = 12, scale = 2)
    private BigDecimal tienCoc;

    /** Trạng thái: CHO_XAC_NHAN / DA_XAC_NHAN / DA_HUY / HOAN_THANH */
    @Column(name = "TRANG_THAI", length = 20)
    private String trangThai;

    // ============ Constructors ============

    /**
     * Constructor rỗng để tạo DatBan; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public DatBan() {}

    /**
     * Tạo đối tượng DatBan và gán các giá trị ban đầu từ tham số.
     * @param maDat mã đặt bàn
     * @param khachHang khách hàng gắn với đặt bàn hoặc đang chỉnh sửa
     * @param banAn đối tượng bàn ăn liên quan
     * @param ngayDat ngày đặt bàn
     * @param soNguoi số khách trong lượt đặt bàn
     * @param tienCoc số tiền đặt cọc
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public DatBan(Long maDat, KhachHang khachHang, BanAn banAn,
                  LocalDate ngayDat, Integer soNguoi, BigDecimal tienCoc, String trangThai) {
        this.maDat = maDat;
        this.khachHang = khachHang;
        this.banAn = banAn;
        this.ngayDat = ngayDat;
        this.soNguoi = soNguoi;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
    }

    // ============ Getters & Setters ============

    /**
     * Đọc mã đặt bàn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaDat() { return maDat; }
    /**
     * Gán mã đặt bàn từ tham số vào thuộc tính của đối tượng.
     * @param maDat mã đặt bàn
     */
    public void setMaDat(Long maDat) { this.maDat = maDat; }

    /**
     * Đọc khách hàng gắn với đặt bàn hoặc đang chỉnh sửa; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public KhachHang getKhachHang() { return khachHang; }
    /**
     * Gán khách hàng gắn với đặt bàn hoặc đang chỉnh sửa từ tham số vào thuộc tính của đối tượng.
     * @param khachHang khách hàng gắn với đặt bàn hoặc đang chỉnh sửa
     */
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    /**
     * Đọc đối tượng bàn ăn liên quan; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BanAn getBanAn() { return banAn; }
    /**
     * Gán đối tượng bàn ăn liên quan từ tham số vào thuộc tính của đối tượng.
     * @param banAn đối tượng bàn ăn liên quan
     */
    public void setBanAn(BanAn banAn) { this.banAn = banAn; }

    /**
     * Đọc ngày đặt bàn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayDat() { return ngayDat; }
    /**
     * Gán ngày đặt bàn từ tham số vào thuộc tính của đối tượng.
     * @param ngayDat ngày đặt bàn
     */
    public void setNgayDat(LocalDate ngayDat) { this.ngayDat = ngayDat; }

    /**
     * Đọc số khách trong lượt đặt bàn; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Integer getSoNguoi() { return soNguoi; }
    /**
     * Gán số khách trong lượt đặt bàn từ tham số vào thuộc tính của đối tượng.
     * @param soNguoi số khách trong lượt đặt bàn
     */
    public void setSoNguoi(Integer soNguoi) { this.soNguoi = soNguoi; }

    /**
     * Đọc số tiền đặt cọc; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getTienCoc() { return tienCoc; }
    /**
     * Gán số tiền đặt cọc từ tham số vào thuộc tính của đối tượng.
     * @param tienCoc số tiền đặt cọc
     */
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }

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

    // ============ toString ============

    /**
     * Tạo chuỗi mô tả các thuộc tính hiện tại để theo dõi/log; không ghi dữ liệu vào database.
     * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
     */
    @Override
    public String toString() {
        return "DatBan{maDat=" + maDat +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKh() : "null") +
                ", banAn=" + (banAn != null ? banAn.getMaBan() : "null") +
                ", ngayDat=" + ngayDat +
                ", soNguoi=" + soNguoi +
                ", tienCoc=" + tienCoc +
                ", trangThai='" + trangThai + "'}";
    }
}
