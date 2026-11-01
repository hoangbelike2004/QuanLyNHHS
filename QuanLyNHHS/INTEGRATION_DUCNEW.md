# Tích hợp khách hàng, bàn ăn và đặt bàn vào Hoang

Nguồn: `DucNew`, commit `5eedd79fccc0195fc908e6b992f54b00032e1740`.
Phạm vi: phần Khách hàng, Bàn ăn và Đặt bàn của Hoàng Việt Đức.

## Chức năng và đường dẫn

- `/khach-hang`: danh sách, tìm theo tên, thêm, sửa, xóa khách hàng và điểm tích lũy.
- `/khach-hang/{maKh}/lich-su-dat-ban`: lịch sử đặt bàn của từng khách.
- `/ban-an`: thêm, sửa, xóa bàn ăn, lọc theo trạng thái.
- `/dat-ban`: tạo, sửa, lọc, xác nhận, hủy, hoàn thành và xóa đặt bàn.

Trình tự dùng: tạo khách hàng và bàn trạng thái `Trống`, lập đặt bàn, xác nhận,
sau đó hủy hoặc hoàn thành khi kết thúc. Các danh sách lấy dữ liệu từ database.

## Phạm vi thay đổi

- Điền 12 file Java đang trống: controller, model, repository, service của
  `KhachHang`, `BanAn`, `DatBan`. Cả 12 file giữ nguyên nội dung nghiệp vụ của Đức.
- Thêm 7 trang Thymeleaf, `reservation.css` và `reservation.js`; các trang mới
  sử dụng sidebar, navbar và màu sắc của Hoang. CSS giới hạn trong
  `.reservation-module`, không tải Bootstrap toàn cục.
- Bật liên kết Khách hàng, Bàn ăn, Đặt bàn trên sidebar. Giữ nguyên các liên kết cũ.
- Giữ nguyên mọi file Java đã triển khai của Hoang, module hóa đơn/báo cáo và kho,
  `pom.xml`, `application.properties`, class khởi động, trang chủ, navbar và CSS/JS cũ.
- Không chép `CTPhieuNhap` từ DucNew: bản đó dùng `MA_CT` và không tương thích với
  khóa kép của phần kho đã tích hợp từ DatNew.
- Không mang trang chủ, cấu hình database, trang lỗi dùng chung hoặc test của
  DucNew vào mã nguồn ứng dụng.

## Nghiệp vụ được giữ nguyên

- Bàn có trạng thái `Trống`, `Đã đặt`, `Đang dùng`.
- Đặt bàn có trạng thái `CHO_XAC_NHAN`, `DA_XAC_NHAN`, `DA_HUY`, `HOAN_THANH`.
- Tạo đặt bàn chỉ cho chọn bàn đang trống, sau khi lưu chuyển bàn sang `Đã đặt`.
- Xác nhận giữ bàn `Đã đặt`; hủy/hoàn thành trả bàn về `Trống`.
- Sửa có thể đổi khách, bàn, ngày, số người, tiền cọc và trạng thái. Tiền cọc
  để trống khi sửa sẽ được xóa. Đổi bàn đang hoạt động trả bàn cũ về `Trống`
  và đặt bàn mới thành `Đã đặt` theo logic nguồn.
- Controller cho xóa đặt bàn và trả bàn về trống khi đặt bàn còn hoạt động;
  giao diện chỉ hiện nút xóa với đặt bàn đã hủy/hoàn thành.
- Điểm tích lũy mặc định là 0; tìm tên khách không phân biệt hoa thường.

Giữ nguyên giới hạn hiện tại của code nguồn: kiểm tra bàn trống dựa vào trạng thái
hiện tại, chưa kiểm tra lịch trùng theo ngày/giờ hoặc số khách so với số chỗ.
Các bước lưu đặt bàn và đổi trạng thái bàn khi tạo/sửa/xóa chưa nằm chung một
giao dịch; không bổ sung kiểm soát đặt bàn đồng thời hoặc thay đổi quy tắc nghiệp vụ.
Thanh toán hóa đơn vẫn giữ logic của Chiến, chưa tự liên kết với trạng thái bàn,
tiền cọc hoặc điểm tích lũy.

## Database

Ba entity ánh xạ `KHACH_HANG`, `BAN_AN`, `DAT_BAN`; dùng khóa `Long` sinh bằng
`GenerationType.IDENTITY` theo code Đức. Nếu các bảng Oracle đã có sẵn, cần đối
chiếu cơ chế sinh `MA_KH`, `MA_BAN`, `MA_DAT`; sơ đồ chỉ ghi `NUMBER` chưa xác định
cột identity hay sequence/trigger. Không thay đổi cơ chế sinh mã trong lần tích hợp.

`DAT_BAN` tham chiếu khách hàng và bàn bằng `@ManyToOne` với
`@NotFound(IGNORE)` của nguồn; liên kết không tìm thấy được đọc thành null.
Không thay thế cơ chế này hoặc tự thêm quan hệ với hóa đơn.

Không chạy script SQL, không thay đổi cấu hình hoặc dữ liệu Oracle trong lần này.

## Kiểm tra

- Maven biên dịch thành công với Java 27.
- 53 test service có sẵn của Đức chạy thành công với Mockito.
- 8 kiểm tra tích hợp chạy thành công bằng H2 trong bộ nhớ và HTTP/Thymeleaf thật:
  CRUD khách hàng/bàn, tìm kiếm/lọc, tạo/sửa/xóa đặt bàn, xác nhận/hủy/hoàn thành,
  đồng bộ trạng thái bàn theo các luồng trên và xem lịch sử.
- Kiểm tra các trang cũ của Hoang, hóa đơn, báo cáo và kho vẫn hiển thị thành công
  cùng các liên kết mới. Không khởi động class mở trình duyệt của Hoang khi kiểm tra.
- Tổng 61 kiểm tra đạt. Các file kiểm tra tạm ở `target` đã được dọn; `src/test`
  giữ nguyên, không thêm test hoặc dependency H2 vào ứng dụng.
- Chưa kiểm thử trên Oracle thật hoặc kiểm tra hiển thị bằng trình duyệt thật.
