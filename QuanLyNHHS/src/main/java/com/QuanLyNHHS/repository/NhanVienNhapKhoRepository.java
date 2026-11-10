package com.QuanLyNHHS.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// Read the shared employee table without taking ownership of the NhanVien module.
/**
 * Đọc bảng nhân viên dùng chung bằng JdbcTemplate; không quản lý CRUD hồ sơ nhân viên.
 */
@Repository
public class NhanVienNhapKhoRepository {
    /**
     * Record chỉ chứa mã và họ tên nhân viên để làm lựa chọn khi lập phiếu nhập.
     * Java tự sinh constructor, các hàm maNv(), hoTen(), equals(), hashCode() và toString().
     */
    public record NhanVienNhapKho(/* maNv: mã nhân viên dạng số trong database. */ Long maNv, /* hoTen: họ tên. */ String hoTen) {}

    // jdbc: JdbcTemplate dùng để thực thi SQL có tham số trên bảng nhân viên.
    private final JdbcTemplate jdbc;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param jdbc JdbcTemplate dùng để thực thi SQL có tham số trên bảng nhân viên
     */
    public NhanVienNhapKhoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Đọc mã và họ tên nhân viên bằng JDBC, sắp theo họ tên rồi mã để chọn người nhập kho.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<NhanVienNhapKho> findAll() {
        return jdbc.query(
                "SELECT MA_NV, HO_TEN FROM NHAN_VIEN ORDER BY HO_TEN, MA_NV",
                (/* rs: ResultSet đang trỏ vào một dòng kết quả SQL. */ rs, /* row: chỉ số dòng của RowMapper; tham số này không được dùng trong biểu thức ánh xạ. */ row) -> new NhanVienNhapKho(rs.getLong("MA_NV"), rs.getString("HO_TEN")));
    }

    /**
     * Kiểm tra có nhân viên nhập kho mang mã đã cho trong database hay không.
     * @param id mã nhân viên cần kiểm tra trong bảng NHAN_VIEN
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    public boolean existsById(Long id) {
        return id != null
                && jdbc.queryForObject(
                                "SELECT COUNT(*) FROM NHAN_VIEN WHERE MA_NV = ?", Long.class, id)
                        > 0;
    }
}
