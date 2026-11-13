# Gợi ý thuyết trình – Nhóm 4

12 slide, thời lượng gợi ý khoảng 8–10 phút. Lời dẫn cũng có trong phần Notes của PowerPoint.

## Slide 1. Hệ thống quản lý nhà hàng hải sản

Kính chào thầy/cô và các bạn. Nhóm 4 trình bày đề tài Hệ thống quản lý nhà hàng hải sản trong học phần Phân tích và thiết kế hướng đối tượng. Bài trình bày tập trung vào cách chuyển yêu cầu nghiệp vụ thành mô hình và thiết kế, minh họa bằng hóa đơn, thanh toán và nhập kho. Thời lượng gợi ý cho toàn bài khoảng 8–10 phút.

## Slide 2. Bài toán và mục tiêu

Nhà hàng có nhiều thông tin liên quan: khách đặt bàn, món được gọi, hóa đơn cần thu và nguyên liệu nhập. Nếu ghi rời rạc, việc đối chiếu tiền hoặc lượng nhập dễ sai. Mục tiêu là quản lý tập trung và giữ các giao dịch nhất quán. Phạm vi là một nhà hàng, chưa mở rộng sang cổng ngân hàng, xuất kho hay điều phối bếp.

## Slide 3. Ai sử dụng hệ thống?

Bốn vai trò nghiệp vụ là phục vụ, thu ngân, thủ kho và quản lý. Mỗi ca sử dụng thể hiện mục tiêu của vai trò: đặt bàn, lập hóa đơn, thanh toán, nhập kho hoặc xem số liệu. Khách hàng chưa trực tiếp thao tác ứng dụng nên không vẽ thành actor ở đây. Các nghiệp vụ cần đăng nhập; website hiện vẫn dùng ROLE_USER chung, chưa giới hạn từng chức năng theo bốn vai trò.

## Slide 4. Mô hình khái niệm: các đối tượng chính

Đây là mô hình khái niệm, tập trung vào ý nghĩa nghiệp vụ, chưa đưa khóa kỹ thuật hay framework vào. Hóa đơn gồm các dòng món; phiếu nhập gồm các dòng nguyên liệu. Cần lớp chi tiết vì quan hệ có lượng và đơn giá riêng. Một chứng từ hợp lệ có ít nhất một dòng, thể hiện bằng bội số 1..*. Khách và nhân viên cùng các quan hệ còn lại có trong mô hình đầy đủ của báo cáo.

## Slide 5. Kiến trúc và phân chia trách nhiệm

Ứng dụng tổ chức theo Controller, Service và Repository. Controller tiếp nhận yêu cầu; Service kiểm tra quy tắc và điều phối giao dịch; Repository đọc ghi dữ liệu qua các interface. Entity giữ dữ liệu ánh xạ xuống bảng. Cách chia này giúp thay quy tắc tiền hoặc tồn tại Service mà không phải sửa nhiều trang giao diện. Công nghệ hiện dùng là Spring Boot, Thymeleaf và Oracle.

## Slide 6. UC04 — Lập hóa đơn

Thu ngân chọn bàn và món, nhập lượng và giảm giá. Hệ thống kiểm tra đầu vào, gộp các dòng trùng món rồi lấy giá từ dữ liệu máy chủ. Thành tiền bằng lượng nhân giá; tổng phải thu bằng tổng dòng trừ số tiền giảm và không nhỏ hơn 0. Phần đầu và chi tiết nằm trong cùng giao dịch. Ở ví dụ, tổng món 720 nghìn, giảm 20 nghìn, còn 700 nghìn đồng.

## Slide 7. UC05 — Các lớp phối hợp khi thanh toán

Biểu đồ thể hiện sự phối hợp của bốn đối tượng. Thu ngân gửi yêu cầu tới Controller; Controller gọi thanhToan của Service. Service tải hóa đơn, kiểm tra chưa thanh toán và có món, tính lại tổng rồi lưu trạng thái. Sau đó kết quả quay về Controller và hiển thị cho người dùng. Nếu đã thanh toán hoặc không có món thì Service từ chối. Biểu đồ đã lược nhánh lỗi, các lần đọc lặp và chi tiết từng Repository để dễ trình bày.

## Slide 8. UC06 — Điều chỉnh phiếu nhập và tồn kho

Điểm quan trọng khi sửa phiếu là điều chỉnh theo chênh lệch. Tồn ban đầu 10 kg; nhập 5 kg thành 15 kg. Nếu sửa lượng trên phiếu từ 5 xuống 3 kg thì tồn là 15 cộng 3 trừ 5, bằng 13 kg. Service khóa dữ liệu, kiểm tra phiên bản form và ghi phiếu, chi tiết, tồn trong một giao dịch. Nhờ đó không cộng lại toàn bộ lượng mới hoặc ghi đè bằng form đã cũ.

## Slide 9. Vòng đời hóa đơn

Hóa đơn được tạo ở trạng thái chưa thanh toán. Khi còn mở, thu ngân có thể sửa hoặc xóa theo điều kiện nghiệp vụ. Khi đã có món và được tính lại tổng, thao tác thanh toán chuyển sang trạng thái đã thanh toán. Từ đây Service từ chối sửa, xóa và thanh toán lại. Đây là kiểm tra theo trạng thái; việc chống hai yêu cầu đồng thời vẫn cần bổ sung khóa hoặc kiểm soát phiên bản.

## Slide 10. DCD rút gọn: mỗi lớp có trách nhiệm rõ

DCD làm rõ phương thức nằm ở lớp nào. Controller nhận thao tác từ giao diện. HoaDonService chứa các phương thức tạo, thanh toán và tính lại tổng tiền. Repository cung cấp thao tác tìm và lưu, trong đó các phương thức cơ bản được kế thừa từ JpaRepository. Quyết định này thể hiện Controller, High Cohesion và SRP: các quy tắc tiền tập trung tại Service, không rải vào giao diện hoặc lớp lưu trữ.

## Slide 11. Kết quả kiểm chứng và giới hạn

Nhóm đã có bộ mô hình đối chiếu với mã nguồn. Trong lần lập báo cáo, 4 kiểm thử JavaScript về tìm kiếm và bộ lọc đã chạy đạt; chưa chạy lại bộ Java và Oracle. Vì vậy không dùng 4 kiểm thử này để kết luận giao dịch chứng từ đã được kiểm thử đầy đủ. Ba giới hạn cần nói rõ là ROLE_USER dùng chung, thanh toán chưa tự giải phóng bàn và bán món chưa tự trừ nguyên liệu. Các điểm này được đưa vào hướng hoàn thiện.

## Slide 12. Kết luận và hướng hoàn thiện

Đề tài nối kết yêu cầu đặt bàn, bán hàng và nhập kho thành các mô hình nhất quán. Bài học chính là xác định đúng đối tượng, phân chia trách nhiệm và bảo vệ tính toàn vẹn của giao dịch. Hướng tiếp theo là hoàn thiện phân quyền, xử lý xung đột và chạy đầy đủ các test Java/Oracle. Nhóm xin cảm ơn thầy/cô và các bạn đã lắng nghe.
