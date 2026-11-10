package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity mapping tới bảng KHACH_HANG trong Oracle DB
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Entity
@Table(name = "KHACH_HANG")
public class KhachHang {

    // maKh: mã khách hàng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_KH")
    private Long maKh;

    // hoTen: họ tên.
    @Column(name = "HO_TEN", length = 100)
    private String hoTen;

    // sdt: số điện thoại.
    @Column(name = "SDT", length = 15)
    private String sdt;

    // email: địa chỉ email.
    @Column(name = "EMAIL", length = 100)
    private String email;

    // diemTichLuy: điểm tích lũy của khách hàng.
    @Column(name = "DIEM_TICH_LUY")
    private Integer diemTichLuy = 0;

    // danhSachDatBan: các lượt đặt bàn liên quan đến bàn hoặc khách hàng.
    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<DatBan> danhSachDatBan;

    // ============ Constructors ============

    /**
     * Constructor rỗng để tạo KhachHang; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public KhachHang() {}

    /**
     * Tạo đối tượng KhachHang và gán các giá trị ban đầu từ tham số.
     * @param maKh mã khách hàng
     * @param hoTen họ tên
     * @param sdt số điện thoại
     * @param email địa chỉ email
     * @param diemTichLuy điểm tích lũy của khách hàng
     */
    public KhachHang(Long maKh, String hoTen, String sdt, String email, Integer diemTichLuy) {
        this.maKh = maKh;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    // ============ Getters & Setters ============

    /**
     * Đọc mã khách hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaKh() { return maKh; }
    /**
     * Gán mã khách hàng từ tham số vào thuộc tính của đối tượng.
     * @param maKh mã khách hàng
     */
    public void setMaKh(Long maKh) { this.maKh = maKh; }

    /**
     * Đọc họ tên; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHoTen() { return hoTen; }
    /**
     * Gán họ tên từ tham số vào thuộc tính của đối tượng.
     * @param hoTen họ tên
     */
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    /**
     * Đọc số điện thoại; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getSdt() { return sdt; }
    /**
     * Gán số điện thoại từ tham số vào thuộc tính của đối tượng.
     * @param sdt số điện thoại
     */
    public void setSdt(String sdt) { this.sdt = sdt; }

    /**
     * Đọc địa chỉ email; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getEmail() { return email; }
    /**
     * Gán địa chỉ email từ tham số vào thuộc tính của đối tượng.
     * @param email địa chỉ email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Đọc điểm tích lũy của khách hàng; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Integer getDiemTichLuy() { return diemTichLuy; }
    /**
     * Gán điểm tích lũy của khách hàng từ tham số vào thuộc tính của đối tượng. Nếu tham số null thì dùng 0.
     * @param diemTichLuy điểm tích lũy của khách hàng
     */
    public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0; }

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
        return "KhachHang{maKh=" + maKh + ", hoTen='" + hoTen + "', sdt='" + sdt + "', email='" + email + "', diemTichLuy=" + diemTichLuy + '}';
    }
}
