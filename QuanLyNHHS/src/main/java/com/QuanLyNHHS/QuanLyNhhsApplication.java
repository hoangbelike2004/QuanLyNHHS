package com.QuanLyNHHS;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Điểm khởi động Spring Boot, quét các thành phần ứng dụng và mở trình duyệt khi hệ thống sẵn sàng.
 */
@SpringBootApplication
public class QuanLyNhhsApplication {

    // serverPort: cổng HTTP lấy từ cấu hình; mặc định 8080.
    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Thiết lập khả năng dùng Desktop rồi khởi động Spring Boot từ các tham số dòng lệnh.
     * @param args tham số dòng lệnh truyền khi chạy chương trình
     */
    public static void main(String[] args) {
        // Tắt chế độ headless để hỗ trợ mở Desktop/trình duyệt trên Windows
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(QuanLyNhhsApplication.class, args);
    }

    /**
     * Sự kiện chạy ngay sau khi Spring Boot và Tomcat khởi động xong hoàn tất
     * Tự động mở trình duyệt web đến trang chủ hệ thống.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        // url: địa chỉ trang chủ sẽ mở sau khi ứng dụng khởi động.
        String url = "http://localhost:" + serverPort + "/";
        System.out.println("\n=======================================================");
        System.out.println("  Hệ thống đã khởi động xong!");
        System.out.println("  Đang tự động mở trình duyệt tại: " + url);
        System.out.println("=======================================================\n");

        try {
            // os: tên hệ điều hành dùng để chọn cách mở trình duyệt.
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Mở trình duyệt mặc định trên hệ điều hành Windows
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ Exception e) {
            System.err.println("Không thể tự động mở trình duyệt: " + e.getMessage());
        }
    }
}
