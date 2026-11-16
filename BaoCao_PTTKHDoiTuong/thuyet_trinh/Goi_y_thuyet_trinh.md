# Gợi ý thuyết trình – Nhóm 4

12 slide, khoảng 8–10 phút. Lời dẫn cũng nằm trong Notes của PowerPoint.

## Slide 1. Hệ thống quản lý nhà hàng hải sản

Nhóm 4 trình bày hệ thống quản lý nhà hàng hải sản dưới góc nhìn phân tích và thiết kế hướng đối tượng.

## Slide 2. Khảo sát và yêu cầu

Phần khảo sát yêu cầu dựa trên tài liệu học phần, lược đồ dữ liệu và ứng dụng hiện có; chưa có khảo sát thực địa. Bốn nhu cầu chính là đặt bàn, bán hàng, nhập kho và báo cáo. Mục tiêu là quản lý tập trung, tính tiền và tồn kho đúng, đồng thời kiểm soát quyền.

## Slide 3. Use case tổng quát

Use case tổng quát có bốn vai trò. Admin quản trị chung; thu ngân bán hàng; quản lý kho nhập hàng; kế toán theo dõi số liệu.

## Slide 4. Các đối tượng chính

Hóa đơn và phiếu nhập đều có lớp chi tiết. Lớp chi tiết giữ số lượng và đơn giá của từng dòng.

## Slide 5. Kiến trúc hệ thống

Controller nhận yêu cầu, Service xử lý quy tắc, Repository đọc ghi dữ liệu, Oracle lưu trữ.

## Slide 6. Quy trình lập hóa đơn

Thu ngân chọn bàn và món. Hệ thống kiểm tra, lấy giá, tính tổng và lưu hóa đơn.

## Slide 7. Mô tả nghiệp vụ

Nghiệp vụ bắt đầu khi khách đặt bàn. Thu ngân ghi món và thu tiền; quản lý kho nhập nguyên liệu, còn kế toán theo dõi chứng từ và doanh thu. Admin quản trị tài khoản và các danh mục chung.

## Slide 8. Sửa phiếu nhập, cập nhật tồn kho

Khi sửa phiếu nhập, tồn kho thay đổi theo chênh lệch giữa lượng mới và lượng cũ.

## Slide 9. Trạng thái hóa đơn

Hóa đơn đi từ chưa thanh toán sang đã thanh toán. Thu ngân không giảm món đã xác nhận.

## Slide 10. Công cụ và dữ liệu

Dự án dùng Java 27 và Spring Boot 4.1.1. Thymeleaf và JavaScript hiển thị giao diện; Spring Data JPA và Oracle JDBC làm việc với Oracle. Spring Security và Validation kiểm tra quyền, dữ liệu nhập. Maven xây dựng dự án; JUnit và H2 dùng cho kiểm thử.

## Slide 11. Đã xử lý và kiểm thử

Đã xử lý quyền cho bốn vai trò, tính tiền và khóa sửa hóa đơn đã thu, điều chỉnh tồn khi sửa phiếu. Bộ Java có 23 bài đạt trên H2; bốn bài JavaScript về lọc giao diện đạt. Chưa chạy giao dịch trên Oracle thật; cần kiểm tra thao tác đồng thời.

## Slide 12. Kết luận

Hệ thống nối đặt bàn, bán hàng và nhập kho. Hướng tiếp theo là kiểm thử Oracle và các tình huống nhiều người cùng thao tác.
