package com.QuanLyNHHS.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Bảng NHAN_VIEN (Ảnh 1 & Ảnh 2)
 * PK: MA_NV NUMBER
 * HO_TEN VARCHAR2(100)
 * GIOI_TINH VARCHAR2(10)
 * SDT VARCHAR2(15)
 * CHUC_VU VARCHAR2(50)
 * LUONG NUMBER(12,2)
 * NGAY_VAO_LAM DATE
 */
@Entity
@Table(name = "NHAN_VIEN")
public class NhanVien {

    // maNv: mã nhân viên dạng số trong database.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nhan_vien")
    @SequenceGenerator(name = "seq_nhan_vien", sequenceName = "SEQ_NHAN_VIEN", allocationSize = 1)
    @Column(name = "MA_NV")
    private Long maNv;

    // hoTen: họ tên.
    @Column(name = "HO_TEN", length = 100, nullable = false)
    private String hoTen;

    // gioiTinh: giới tính.
    @Column(name = "GIOI_TINH", length = 10)
    private String gioiTinh;

    // sdt: số điện thoại.
    @Column(name = "SDT", length = 15)
    private String sdt;

    // chucVu: chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập.
    @Column(name = "CHUC_VU", length = 50)
    private String chucVu;

    // luong: mức lương dùng BigDecimal để lưu số tiền.
    @Column(name = "LUONG", precision = 12, scale = 2)
    private BigDecimal luong;

    // ngayVaoLam: ngày nhân viên vào làm.
    @Column(name = "NGAY_VAO_LAM")
    private LocalDate ngayVaoLam;

    // email: địa chỉ email.
    @Column(name = "EMAIL", length = 100)
    private String email;

    // caLamViec: ca làm việc.
    @Column(name = "CA_LAM_VIEC", length = 100)
    private String caLamViec;

    // trangThai: trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ.
    @Column(name = "TRANG_THAI", length = 30)
    private String trangThai; // "Đang làm việc", "Nghỉ phép", "Đã nghỉ việc"

    /**
     * Constructor rỗng để tạo NhanVien; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public NhanVien() {
    }

    /**
     * Tạo đối tượng NhanVien và gán các giá trị ban đầu từ tham số.
     * @param hoTen họ tên
     * @param gioiTinh giới tính
     * @param sdt số điện thoại
     * @param chucVu chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập
     * @param luong mức lương dùng BigDecimal để lưu số tiền
     * @param ngayVaoLam ngày nhân viên vào làm
     */
    public NhanVien(String hoTen, String gioiTinh, String sdt, String chucVu, BigDecimal luong, LocalDate ngayVaoLam) {
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.luong = luong;
        this.ngayVaoLam = ngayVaoLam;
    }

    /**
     * Tạo đối tượng NhanVien và gán các giá trị ban đầu từ tham số.
     * @param maNv mã nhân viên dạng số trong database
     * @param hoTen họ tên
     * @param gioiTinh giới tính
     * @param sdt số điện thoại
     * @param chucVu chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập
     * @param luong mức lương dùng BigDecimal để lưu số tiền
     * @param ngayVaoLam ngày nhân viên vào làm
     */
    public NhanVien(Long maNv, String hoTen, String gioiTinh, String sdt, String chucVu, BigDecimal luong, LocalDate ngayVaoLam) {
        this.maNv = maNv;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.luong = luong;
        this.ngayVaoLam = ngayVaoLam;
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
     * Đọc họ tên; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getHoTen() {
        return hoTen;
    }

    /**
     * Gán họ tên từ tham số vào thuộc tính của đối tượng.
     * @param hoTen họ tên
     */
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    /**
     * Đọc giới tính; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getGioiTinh() {
        return gioiTinh;
    }

    /**
     * Gán giới tính từ tham số vào thuộc tính của đối tượng.
     * @param gioiTinh giới tính
     */
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    /**
     * Đọc số điện thoại; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getSdt() {
        return sdt;
    }

    /**
     * Gán số điện thoại từ tham số vào thuộc tính của đối tượng.
     * @param sdt số điện thoại
     */
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    /**
     * Đọc chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getChucVu() {
        return chucVu;
    }

    /**
     * Gán chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập từ tham số vào thuộc tính của đối tượng.
     * @param chucVu chức vụ làm việc; không tự quyết định quyền của tài khoản đăng nhập
     */
    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    /**
     * Đọc mức lương dùng BigDecimal để lưu số tiền; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getLuong() {
        return luong;
    }

    /**
     * Gán mức lương dùng BigDecimal để lưu số tiền từ tham số vào thuộc tính của đối tượng.
     * @param luong mức lương dùng BigDecimal để lưu số tiền
     */
    public void setLuong(BigDecimal luong) {
        this.luong = luong;
    }

    /**
     * Đọc ngày nhân viên vào làm; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    /**
     * Gán ngày nhân viên vào làm từ tham số vào thuộc tính của đối tượng.
     * @param ngayVaoLam ngày nhân viên vào làm
     */
    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    // Helpers cho UI Thymeleaf
    /**
     * Tạo mã hiển thị từ maNv, ví dụ 1 thành NV01; nếu mã null thì trả chuỗi rỗng.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getMaNV() {
        if (maNv == null) return "";
        return "NV" + String.format("%02d", maNv);
    }

    /**
     * Lọc bỏ ký tự không phải chữ số từ mã hiển thị và gán số thu được vào maNv; nếu chuyển đổi lỗi thì giữ mã cũ.
     * @param maNV mã nhân viên dạng hiển thị, ví dụ NV01
     */
    public void setMaNV(String maNV) {
        try {
            this.maNv = Long.parseLong(maNV.replaceAll("\\D+", ""));
        } catch (/* ignored: ngoại lệ được bỏ qua trong hàm chuyển đổi mã nhân viên. */ Exception ignored) {
        }
    }

    /**
     * Đọc số điện thoại, dùng cùng dữ liệu với sdt; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getSoDienThoai() {
        return sdt;
    }

    /**
     * Gán số điện thoại, dùng cùng dữ liệu với sdt từ tham số vào thuộc tính của đối tượng.
     * @param soDienThoai số điện thoại, dùng cùng dữ liệu với sdt
     */
    public void setSoDienThoai(String soDienThoai) {
        this.sdt = soDienThoai;
    }

    /**
     * Đổi luong từ BigDecimal sang double cho giao diện cũ; trả 0 nếu luong null.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public double getMucLuong() {
        return luong != null ? luong.doubleValue() : 0.0;
    }

    /**
     * Đổi mức lương dạng double thành BigDecimal rồi gán vào thuộc tính luong.
     * @param mucLuong mức lương dạng double để tương thích giao diện cũ
     */
    public void setMucLuong(double mucLuong) {
        this.luong = BigDecimal.valueOf(mucLuong);
    }

    /**
     * Đọc địa chỉ email; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gán địa chỉ email từ tham số vào thuộc tính của đối tượng.
     * @param email địa chỉ email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Đọc ca làm việc; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public String getCaLamViec() {
        return caLamViec;
    }

    /**
     * Gán ca làm việc từ tham số vào thuộc tính của đối tượng.
     * @param caLamViec ca làm việc
     */
    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
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
