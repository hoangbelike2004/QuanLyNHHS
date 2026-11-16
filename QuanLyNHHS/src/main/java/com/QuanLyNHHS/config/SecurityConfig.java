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
                        .requestMatchers("/tai-khoan", "/tai-khoan/**", "/nhan-vien", "/nhan-vien/**").hasRole("ADMIN")
                        .requestMatchers("/bao-cao", "/bao-cao/**").hasAnyRole("ADMIN", "KE_TOAN")
                        .requestMatchers(HttpMethod.POST, "/hoa-don/xoa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/hoa-don", "/hoa-don/chi-tiet/**").hasAnyRole("ADMIN", "KE_TOAN", "THU_NGAN")
                        .requestMatchers("/hoa-don/**").hasAnyRole("ADMIN", "THU_NGAN")
                        .requestMatchers("/phieu-nhap/them", "/phieu-nhap/*/sua", "/phieu-nhap/*/xoa")
                                .hasAnyRole("ADMIN", "QUAN_LY_KHO")
                        .requestMatchers(HttpMethod.GET, "/phieu-nhap", "/phieu-nhap/*").hasAnyRole("ADMIN", "QUAN_LY_KHO", "KE_TOAN")
                        .requestMatchers("/phieu-nhap/**").hasAnyRole("ADMIN", "QUAN_LY_KHO")
                        .requestMatchers("/kho", "/nguyen-lieu", "/nguyen-lieu/**", "/nha-cung-cap", "/nha-cung-cap/**")
                                .hasAnyRole("ADMIN", "QUAN_LY_KHO")
                        .requestMatchers(HttpMethod.GET, "/ban-an").hasAnyRole("ADMIN", "THU_NGAN")
                        .requestMatchers("/ban-an/**").hasRole("ADMIN")
                        .requestMatchers("/khach-hang", "/khach-hang/**").hasAnyRole("ADMIN", "THU_NGAN")
                        .requestMatchers(HttpMethod.POST, "/dat-ban/xoa/**").hasRole("ADMIN")
                        .requestMatchers("/dat-ban", "/dat-ban/**").hasAnyRole("ADMIN", "THU_NGAN")
                        .requestMatchers("/mon-an", "/mon-an/**", "/loai-mon", "/loai-mon/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/").hasAnyRole("ADMIN", "QUAN_LY_KHO", "KE_TOAN", "THU_NGAN")
                        .anyRequest().hasRole("ADMIN"))
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
