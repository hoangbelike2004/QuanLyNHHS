package com.QuanLyNHHS.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.QuanLyNHHS.model.TaiKhoan;

/**
 * Truy cập dữ liệu tài khoản bằng Spring Data JPA. Kế thừa findAll, findById, save, saveAndFlush, delete và flush; entity là {@code TaiKhoan}, kiểu khóa là {@code Long}.
 */
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
    /**
     * Tìm tài khoản theo tên đăng nhập; trả Optional rỗng nếu không có tài khoản.
     * @param tenDangNhap tên đăng nhập của tài khoản
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
