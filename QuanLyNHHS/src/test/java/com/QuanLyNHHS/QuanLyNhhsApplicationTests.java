package com.QuanLyNHHS;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test cơ bản - kiểm tra Spring context khởi động được.
 * Exclude DataSource/JPA autoconfiguration vì không cần Oracle DB thật khi chạy test.
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class QuanLyNhhsApplicationTests {

    @Test
    void contextLoads() {
        // Test cơ bản: Spring context phải khởi động thành công
    }

}
