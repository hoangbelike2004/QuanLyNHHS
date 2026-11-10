package com.QuanLyNHHS.support;

import com.QuanLyNHHS.config.SecurityConfig;
import com.QuanLyNHHS.controller.*;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.repository.MonAnRepository;
import com.QuanLyNHHS.service.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** Không tải class khởi động có tác dụng mở trình duyệt hoặc kết nối Oracle. */
@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = MonAn.class)
@EnableJpaRepositories(basePackageClasses = MonAnRepository.class)
@Import({MonAnService.class, LoaiMonService.class, NhanVienService.class,
        TaiKhoanService.class, SecurityConfig.class, AuthController.class,
        NguoiDungAdvice.class, MonAnController.class, LoaiMonController.class,
        NhanVienController.class, DanhMucExceptionHandler.class, HomeController.class})
public class MenuTestApplication {}
