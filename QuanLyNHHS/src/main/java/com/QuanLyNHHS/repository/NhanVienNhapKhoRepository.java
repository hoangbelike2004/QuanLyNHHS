package com.QuanLyNHHS.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// Read the shared employee table without taking ownership of the NhanVien module.
@Repository
public class NhanVienNhapKhoRepository {
    public record NhanVienNhapKho(Long maNv, String hoTen) {}

    private final JdbcTemplate jdbc;

    public NhanVienNhapKhoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<NhanVienNhapKho> findAll() {
        return jdbc.query(
                "SELECT MA_NV, HO_TEN FROM NHAN_VIEN ORDER BY HO_TEN, MA_NV",
                (rs, row) -> new NhanVienNhapKho(rs.getLong("MA_NV"), rs.getString("HO_TEN")));
    }

    public boolean existsById(Long id) {
        return id != null
                && jdbc.queryForObject(
                                "SELECT COUNT(*) FROM NHAN_VIEN WHERE MA_NV = ?", Long.class, id)
                        > 0;
    }
}
