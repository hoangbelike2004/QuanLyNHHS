package com.QuanLyNHHS.service;

import java.util.List;
import java.util.Locale;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.QuanLyNHHS.model.TaiKhoan;
import com.QuanLyNHHS.repository.TaiKhoanRepository;

/**
 * Xử lý nghiệp vụ tài khoản và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
@Transactional(readOnly = true)
public class TaiKhoanService implements UserDetailsService {
    // repository: thành phần TaiKhoanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final TaiKhoanRepository repository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần TaiKhoanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public TaiKhoanService(TaiKhoanRepository repository) {
        this.repository = repository;
    }

    /**
     * Chuẩn hóa tên đăng nhập, tìm tài khoản và chuyển thành UserDetails cho Spring Security; không tìm thấy thì ném UsernameNotFoundException.
     * @param username tên đăng nhập Spring Security chuyển đến để tìm tài khoản
     * @return kết quả kiểu {@code UserDetails} cho nơi gọi sử dụng
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        // normalized: tên đăng nhập đã bỏ khoảng trắng đầu/cuối và chuyển chữ thường.
        String normalized = username == null ? "" : username.strip().toLowerCase(Locale.ROOT);
        return repository.findByTenDangNhap(normalized)
                .map(NguoiDung::new)
                .orElseThrow(() -> new UsernameNotFoundException("Thông tin đăng nhập không đúng."));
    }

    /**
     * Principal của Spring Security, kế thừa User và giữ thêm họ tên; hiện mọi tài khoản nhận ROLE_USER.
     */
    public static final class NguoiDung extends User {
        // serialVersionUID: mã phiên bản phục vụ cơ chế tuần tự hóa của Java.
        private static final long serialVersionUID = 1L;
        // hoTen: họ tên.
        private final String hoTen;
        /**
         * Chuyển entity tài khoản thành principal Spring Security, cấp ROLE_USER và giữ họ tên để hiển thị.
         * @param account entity tài khoản đọc từ database hoặc được tạo để lưu tên đăng nhập và mật khẩu đã băm
         */
        public NguoiDung(TaiKhoan account) {
            super(account.getTenDangNhap(), account.getMatKhau(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
            this.hoTen = account.getHoTen();
        }
        /**
         * Đọc họ tên; không thay đổi dữ liệu.
         * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
         */
        public String getHoTen() { return hoTen; }
    }
}
