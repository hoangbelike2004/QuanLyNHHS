package com.QuanLyNHHS.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Khóa kép của chi tiết phiếu nhập, gồm maPn và maNl; dùng với @EmbeddedId và @MapsId.
 */
@Embeddable
public class CTPhieuNhapId implements Serializable {
    // maPn: mã phiếu nhập.
    @Column(name = "MA_PN")
    private Long maPn;

    // maNl: mã nguyên liệu.
    @Column(name = "MA_NL")
    private Long maNl;

    /**
     * Constructor rỗng để tạo CTPhieuNhapId; JPA hoặc cơ chế gán form có thể khởi tạo đối tượng trước khi điền thuộc tính.
     */
    public CTPhieuNhapId() {}

    /**
     * Tạo đối tượng CTPhieuNhapId và gán các giá trị ban đầu từ tham số.
     * @param maPn mã phiếu nhập
     * @param maNl mã nguyên liệu
     */
    public CTPhieuNhapId(Long maPn, Long maNl) {
        this.maPn = maPn;
        this.maNl = maNl;
    }

    /**
     * So sánh hai khóa kép theo các mã thành phần thay vì theo địa chỉ đối tượng; dùng khi JPA hoặc collection đối chiếu khóa.
     * @param o đối tượng đem so sánh với khóa kép hiện tại
     * @return true nếu điều kiện so sánh/kiểm tra thỏa mãn, ngược lại false
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof /* other: khóa kép sau khi đã kiểm tra đúng kiểu dữ liệu. */ CTPhieuNhapId other
                && Objects.equals(maPn, other.maPn)
                && Objects.equals(maNl, other.maNl);
    }

    /**
     * Tạo mã băm từ các thành phần khóa kép, nhất quán với equals để dùng trong Map/Set và JPA.
     * @return kết quả kiểu {@code int} cho nơi gọi sử dụng
     */
    @Override
    public int hashCode() {
        return Objects.hash(maPn, maNl);
    }

    /**
     * Đọc mã phiếu nhập; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaPn() {
        return maPn;
    }

    /**
     * Gán mã phiếu nhập từ tham số vào thuộc tính của đối tượng.
     * @param maPn mã phiếu nhập
     */
    public void setMaPn(Long maPn) {
        this.maPn = maPn;
    }

    /**
     * Đọc mã nguyên liệu; không thay đổi dữ liệu.
     * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
     */
    public Long getMaNl() {
        return maNl;
    }

    /**
     * Gán mã nguyên liệu từ tham số vào thuộc tính của đối tượng.
     * @param maNl mã nguyên liệu
     */
    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }
}
