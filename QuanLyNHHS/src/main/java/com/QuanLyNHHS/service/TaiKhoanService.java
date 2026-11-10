package com.QuanLyNHHS.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.QuanLyNHHS.dto.DangKyForm;
import com.QuanLyNHHS.model.TaiKhoan;
import com.QuanLyNHHS.repository.TaiKhoanRepository;

/**
 * Xử lý nghiệp vụ tài khoản và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class TaiKhoanService implements UserDetailsService {
    // repository: thành phần TaiKhoanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final TaiKhoanRepository repository;
    // passwordEncoder: thành phần băm và đối chiếu mật khẩu, không giải mã mật khẩu.
    private final PasswordEncoder passwordEncoder;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần TaiKhoanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param passwordEncoder thành phần băm và đối chiếu mật khẩu, không giải mã mật khẩu
     */
    public TaiKhoanService(TaiKhoanRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
     * Kiểm tra mật khẩu nhập lại, giới hạn 72 byte và tên đăng nhập trùng; băm mật khẩu rồi lưu tài khoản trong một giao dịch.
     * @param form đối tượng DangKyForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     */
    @Transactional
    public void dangKy(@NotNull @Valid DangKyForm form) {
        if (!Objects.equals(form.getMatKhau(), form.getXacNhanMatKhau())) {
            throw new DangKyException("xacNhanMatKhau", "Mật khẩu nhập lại không khớp.");
        }
        if (form.getMatKhau().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new DangKyException("matKhau", "Mật khẩu tối đa 72 byte; hãy rút ngắn nếu dùng ký tự có dấu.");
        }
        if (repository.existsByTenDangNhap(form.getTenDangNhap())) {
            throw new DangKyException("tenDangNhap", "Tên đăng nhập đã được sử dụng.");
        }
        // account: entity tài khoản đọc từ database hoặc được tạo để lưu tên đăng nhập và mật khẩu đã băm.
        TaiKhoan account = new TaiKhoan();
        account.setHoTen(form.getHoTen());
        account.setTenDangNhap(form.getTenDangNhap());
        account.setMatKhau(passwordEncoder.encode(form.getMatKhau()));
        repository.saveAndFlush(account);
    }

    /**
     * Ngoại lệ nghiệp vụ đăng ký, mang tên trường và thông báo để hiển thị lỗi đúng vị trí.
     */
    public static class DangKyException extends IllegalArgumentException {
        // field: tên trường form cần gắn thông báo lỗi.
        private final String field;
        /**
         * Tạo lỗi đăng ký có tên trường và nội dung lỗi để controller gắn đúng vị trí trên form.
         * @param field tên trường form cần gắn thông báo lỗi
         * @param message nội dung thông báo lỗi hoặc thông báo hiển thị
         */
        public DangKyException(String field, String message) { super(message); this.field = field; }
        /**
         * Đọc tên trường form cần gắn thông báo lỗi để controller gắn lỗi đúng trường; không thay đổi dữ liệu.
         * @return giá trị thuộc tính được lấy hoặc giá trị chuyển đổi mô tả ở trên
         */
        public String getField() { return field; }
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
