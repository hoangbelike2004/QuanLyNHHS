package com.QuanLyNHHS;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.QuanLyNHHS.support.MenuTestApplication;

/**
 * Bộ kiểm thử khởi tạo application context bằng cấu hình kiểm thử.
 */
@SpringBootTest(classes = MenuTestApplication.class)
@ActiveProfiles("test")
class QuanLyNhhsApplicationTests {

	/**
	 * Kiểm tra Spring tạo được application context với cấu hình kiểm thử H2.
	 */
	@Test
	void contextLoads() {
	}

}
