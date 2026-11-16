package com.QuanLyNHHS.service;

import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> VAI_TRO_HOP_LE = Set.of("ADMIN", "QUAN_LY_KHO", "KE_TOAN", "THU_NGAN");

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần TaiKhoanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public TaiKhoanService(TaiKhoanRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TaiKhoan> layDanhSach() {
        return repository.findAll(org.springframework.data.domain.Sort.by("maTk").ascending());
    }

    @Transactional
    public TaiKhoan taoTaiKhoan(String hoTen, String tenDangNhap, String matKhau, String vaiTro) {
        String name = hoTen == null ? "" : hoTen.strip();
        String username = tenDangNhap == null ? "" : tenDangNhap.strip().toLowerCase(Locale.ROOT);
        if (name.isBlank() || name.length() > 100) {
            throw new IllegalArgumentException("Họ tên phải có từ 1 đến 100 ký tự.");
        }
        if (!username.matches("[a-z0-9_]{3,30}")) {
            throw new IllegalArgumentException("Tên đăng nhập phải có 3-30 ký tự chữ thường, số hoặc dấu gạch dưới.");
        }
        if (matKhau == null || matKhau.length() < 8
                || matKhau.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự và không quá 72 byte UTF-8.");
        }
        if (!VAI_TRO_HOP_LE.contains(vaiTro)) {
            throw new IllegalArgumentException("Vai trò không hợp lệ.");
        }
        if (repository.findByTenDangNhap(username).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        TaiKhoan account = new TaiKhoan();
        account.setHoTen(name);
        account.setTenDangNhap(username);
        account.setMatKhau(passwordEncoder.encode(matKhau));
        account.setVaiTro(vaiTro);
        try {
            return repository.saveAndFlush(account);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.", e);
        }
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
     * Principal của Spring Security, kế thừa User và giữ thêm họ tên cùng vai trò ứng dụng.
     */
    public static final class NguoiDung extends User {
        // serialVersionUID: mã phiên bản phục vụ cơ chế tuần tự hóa của Java.
        private static final long serialVersionUID = 1L;
        // hoTen: họ tên.
        private final String hoTen;
        /**
         * Chuyển entity tài khoản thành principal Spring Security, cấp vai trò và giữ họ tên để hiển thị.
         * @param account entity tài khoản đọc từ database hoặc được tạo để lưu tên đăng nhập và mật khẩu đã băm
         */
        public NguoiDung(TaiKhoan account) {
            super(account.getTenDangNhap(), account.getMatKhau(), List.of(new SimpleGrantedAuthority("ROLE_" +
                    chuanHoaVaiTro(account.getVaiTro()))));
            this.hoTen = account.getHoTen();
        }
        private static String chuanHoaVaiTro(String vaiTro) {
            if (vaiTro == null || "QUAN_LY".equals(vaiTro)) return "ADMIN";
            if ("NHAN_VIEN".equals(vaiTro)) return "THU_NGAN";
            return vaiTro;
        }
        /**
         * Đọc họ tên; không thay đổi dữ liệu.
         * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
         */
        public String getHoTen() { return hoTen; }
    }
}
