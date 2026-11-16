package com.QuanLyNHHS.config;

import jakarta.servlet.DispatcherType;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình đăng nhập, đăng xuất, quyền truy cập URL và bộ băm mật khẩu của toàn ứng dụng.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Cấu hình URL công khai, các URL cần đăng nhập, form đăng nhập và đăng xuất; CSRF giữ cấu hình mặc định của Spring Security.
     * @param http đối tượng cấu hình chuỗi bộ lọc bảo mật HTTP
     * @return kết quả kiểu {@code SecurityFilterChain} cho nơi gọi sử dụng
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(/* authorize: đối tượng cấu hình điều kiện truy cập các URL. */ authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/tai-khoan", "/tai-khoan/**").hasRole("QUAN_LY")
                        .requestMatchers(HttpMethod.POST, "/hoa-don/thanh-toan/**").hasAnyRole("THU_NGAN", "QUAN_LY")
                        .requestMatchers(HttpMethod.POST, "/hoa-don/xoa/**").hasRole("QUAN_LY")
                        .requestMatchers(HttpMethod.POST, "/dat-ban/xac-nhan/**").hasAnyRole("THU_NGAN", "QUAN_LY")
                        .requestMatchers("/bao-cao/**").hasAnyRole("THU_NGAN", "QUAN_LY")
                        .requestMatchers("/hoa-don/them", "/hoa-don/them/**").hasAnyRole("NHAN_VIEN", "THU_NGAN", "QUAN_LY")
                        .requestMatchers("/hoa-don/**").hasAnyRole("NHAN_VIEN", "THU_NGAN", "QUAN_LY")
                        .requestMatchers(HttpMethod.GET, "/").hasAnyRole("NHAN_VIEN", "THU_NGAN", "QUAN_LY")
                        .requestMatchers(HttpMethod.GET, "/dat-ban").hasAnyRole("THU_NGAN", "QUAN_LY")
                        .requestMatchers("/dat-ban/**").hasRole("QUAN_LY")
                        .requestMatchers("/**").hasRole("QUAN_LY"))
                .formLogin(/* form: đối tượng cấu hình form đăng nhập của Spring Security. */ form -> form
                        .loginPage("/login")
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(/* logout: đối tượng cấu hình đường dẫn và việc hủy phiên khi đăng xuất. */ logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .build();
    }

    /**
     * Tạo bean PasswordEncoder dùng BCrypt độ khó 12 và giới hạn độ dài mật khẩu khi đối chiếu.
     * @return kết quả kiểu {@code PasswordEncoder} cho nơi gọi sử dụng
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        // bcrypt: bộ băm BCrypt với độ khó được cấu hình là 12.
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
        return new PasswordEncoder() {
            /**
             * Băm mật khẩu chưa mã hóa bằng BCrypt để lưu vào database.
             * @param rawPassword mật khẩu chưa băm được người dùng cung cấp
             * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
             */
            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }

            /**
             * Đối chiếu mật khẩu người dùng nhập với chuỗi đã băm; trả false nếu null hoặc dài hơn 72 byte UTF-8.
             * @param rawPassword mật khẩu chưa băm được người dùng cung cấp
             * @param encodedPassword chuỗi mật khẩu đã băm lưu trong database
             * @return true nếu điều kiện so sánh/kiểm tra thỏa mãn, ngược lại false
             */
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword != null
                        && rawPassword.toString().getBytes(StandardCharsets.UTF_8).length <= 72
                        && bcrypt.matches(rawPassword, encodedPassword);
            }

            /**
             * Cho biết chuỗi BCrypt hiện tại có cần băm lại theo cấu hình độ khó mới hay không; hàm này không tự cập nhật database.
             * @param encodedPassword chuỗi mật khẩu đã băm lưu trong database
             * @return kết quả kiểm tra điều kiện được mô tả ở trên
             */
            @Override
            public boolean upgradeEncoding(String encodedPassword) {
                return bcrypt.upgradeEncoding(encodedPassword);
            }
        };
    }
}
