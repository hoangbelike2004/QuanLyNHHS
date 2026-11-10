
package com.QuanLyNHHS.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entity biểu diễn dữ liệu chi tiết hóa đơn; các annotation JPA ánh xạ thuộc tính sang bảng/cột trong database.
 */
@Entity
@Table(name = "CT_HOA_DON")
public class CTHoaDon {

    // id: khóa kép gồm mã hóa đơn và mã món.
    @EmbeddedId
    private CTHoaDonId id;

    // soLuong: số lượng của dòng chi tiết.
    @Column(name = "SO_LUONG", precision = 10, scale = 2)
    private BigDecimal soLuong;

    // donGia: đơn giá của một đơn vị.
    @Column(name = "DON_GIA", precision = 12, scale = 2)
    private BigDecimal donGia;

    // thanhTien: thành tiền của dòng chi tiết.
    @Column(name = "THANH_TIEN", precision = 14, scale = 2)
    private BigDecimal thanhTien;

    /**
     * Constructor rỗng để tạo CTHoaDon; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public CTHoaDon() {
    }

    /**
     * Đọc id; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public CTHoaDonId getId() {
        return id;
    }

    /**
     * Gán id từ tham số vào thuộc tính của đối tượng.
     * @param id khóa kép gồm mã hóa đơn và mã món
     */
    public void setId(CTHoaDonId id) {
        this.id = id;
    }

    /**
     * Đọc số lượng của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getSoLuong() {
        return soLuong;
    }

    /**
     * Gán số lượng của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param soLuong số lượng của dòng chi tiết
     */
    public void setSoLuong(BigDecimal soLuong) {
        this.soLuong = soLuong;
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
     * Đọc thành tiền của dòng chi tiết; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    /**
     * Gán thành tiền của dòng chi tiết từ tham số vào thuộc tính của đối tượng.
     * @param thanhTien thành tiền của dòng chi tiết
     */
    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    /**
     * Lấy mã hóa đơn từ khóa kép id; nếu id chưa có thì trả null.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaHd() {
        return id == null ? null : id.getMaHd();
    }

    /**
     * Lấy mã món từ khóa kép id; nếu id chưa có thì trả null.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaMon() {
        return id == null ? null : id.getMaMon();
    }

    /**
     * Khóa kép của chi tiết hóa đơn, gồm maHd và maMon; equals/hashCode so sánh theo cả hai thành phần.
     */
    @Embeddable
    public static class CTHoaDonId implements Serializable {

        // serialVersionUID: mã phiên bản phục vụ cơ chế tuần tự hóa của Java.
        private static final long serialVersionUID = 1L;

        // maHd: mã hóa đơn.
        @Column(name = "MA_HD")
        private Long maHd;

        // maMon: mã món ăn.
        @Column(name = "MA_MON")
        private Long maMon;

        /**
         * Constructor rỗng để tạo CTHoaDonId; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
         */
        public CTHoaDonId() {
        }

        /**
         * Tạo đối tượng CTHoaDonId và gán các giá trị ban đầu từ tham số.
         * @param maHd mã hóa đơn
         * @param maMon mã món ăn
         */
        public CTHoaDonId(Long maHd, Long maMon) {
            this.maHd = maHd;
            this.maMon = maMon;
        }

        /**
         * Đọc mã hóa đơn; không thay đổi dữ liệu.
         * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
         */
        public Long getMaHd() {
            return maHd;
        }

        /**
         * Gán mã hóa đơn từ tham số vào thuộc tính của đối tượng.
         * @param maHd mã hóa đơn
         */
        public void setMaHd(Long maHd) {
            this.maHd = maHd;
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
         * So sánh hai khóa kép theo các mã thành phần thay vì theo địa chỉ đối tượng; dùng khi JPA hoặc collection đối chiếu khóa.
         * @param obj đối tượng đem so sánh với khóa kép hiện tại
         * @return true nếu điều kiện so sánh/kiểm tra thỏa mãn, ngược lại false
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof /* other: khóa kép sau khi đã kiểm tra đúng kiểu dữ liệu. */ CTHoaDonId other)) {
                return false;
            }

            return Objects.equals(maHd, other.maHd)
                    && Objects.equals(maMon, other.maMon);
        }

        /**
         * Tạo mã băm từ các thành phần khóa kép, nhất quán với equals để dùng trong Map/Set và JPA.
         * @return kết quả kiểu {@code int} cho nơi gọi sử dụng
         */
        @Override
        public int hashCode() {
            return Objects.hash(maHd, maMon);
        }
    }
}
