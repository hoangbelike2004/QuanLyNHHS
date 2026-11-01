# Tích hợp hóa đơn và báo cáo vào nhánh Hoang

Nguồn: nhánh `ChienNew`, commit `15cfa50c27ff31ee5d5f655dbdf026d906fc4d7c`.
Phạm vi theo bảng phân công: phần Bán hàng, Hóa đơn và Báo cáo của Đỗ Minh Chiến.

## Phần được bổ sung

- Điền 8 file Java trước đó đang trống: `HoaDonController`, `BaoCaoController`,
  `HoaDon`, `CTHoaDon`, `HoaDonRepository`, `CTHoaDonRepository`,
  `HoaDonService`, `BaoCaoService`.
- Giữ nguyên nghiệp vụ trong service, model và repository của ChienNew.
- Trong controller mới, thay `findAllByOrderByTenMonAsc()` bằng
  `findAll(Sort.by("tenMon").ascending())` để giữ nguyên cách sắp xếp mà không
  cần sửa `MonAnRepository` đang có của Hoang.
- Thêm 5 trang Thymeleaf và `static/css/finance.css`, sử dụng màu sắc và bố cục
  hiện tại. CSS mới chỉ được tải ở các trang hóa đơn, báo cáo.
- Trong giao diện cũ, chỉ bật liên kết hóa đơn và thêm liên kết báo cáo ở
  `templates/layout/sidebar.html`. Các trang món ăn, loại món, nhân viên,
  trang chủ, navbar, CSS và JavaScript cũ được giữ nguyên.
- Thêm nút xóa hóa đơn chưa thanh toán, gọi endpoint đã có của ChienNew.

Tất cả các file Java đã có nội dung trên Hoang trước khi tích hợp đều được giữ
nguyên, bao gồm class khởi động ứng dụng. Không sửa `pom.xml`, cấu hình Oracle,
SecurityConfig, file Excel hoặc các module của Đức và Đạt.

## Sử dụng

- `/hoa-don`: danh sách, lọc theo trạng thái, xem, sửa, xóa và thanh toán.
- `/hoa-don/them`: nhập mã bàn, mã nhân viên, mã khách nếu có, giảm giá và chọn món.
- `/bao-cao`: doanh thu, số hóa đơn đã thanh toán, món bán chạy và tổng tiền món
  theo từng hóa đơn.

`HoaDon` ánh xạ `HOA_DON`, khóa `MA_HD` kiểu `Long`, sinh bằng `IDENTITY` theo
code nguồn của Chiến. `CTHoaDon` ánh xạ `CT_HOA_DON` với khóa kép
`(MA_HD, MA_MON)`; số lượng và tiền dùng `BigDecimal`, tên cột theo sơ đồ được cung cấp.
Nếu database đã tồn tại, cần đối chiếu cách sinh `MA_HD` thực tế; sơ đồ ảnh chỉ
ghi kiểu NUMBER, không xác định identity, sequence hoặc trigger.

Món ăn để lập hóa đơn được lấy từ bảng `MON_AN` trong Oracle. Các món mẫu đang
hiển thị trong controller món ăn của Hoang không tự được lưu vào database.
Các mã bàn, nhân viên, khách hàng phải tương ứng dữ liệu thực nếu database có
ràng buộc khóa ngoại. Module mới chưa thêm các quan hệ ORM hoặc ràng buộc khóa
ngoại thay cho module của các thành viên khác.

## Nghiệp vụ được giữ nguyên từ ChienNew

- Giảm giá là số tiền VNĐ, không phải phần trăm; tổng thanh toán tối thiểu là 0.
- Trạng thái lưu là `Chua thanh toan` và `Da thanh toan`.
- Không sửa/xóa hóa đơn đã thanh toán và không thanh toán lại.
- Backend gộp các dòng cùng mã món; giao diện yêu cầu sửa số lượng trên dòng đã có.
- Khi sửa danh sách món, chi tiết cũ được thay thế và giá được lấy lại từ `MON_AN`.
- Chỉ doanh thu trong khoảng đã chọn chịu bộ lọc ngày. Số hóa đơn, món bán chạy
  và bảng tổng tiền theo hóa đơn thống kê toàn bộ hóa đơn đã thanh toán.
- Tổng tiền chi tiết theo hóa đơn và doanh thu món là số trước giảm giá;
  doanh thu từ hóa đơn là số sau giảm giá.
- Thanh toán hiện chỉ cập nhật trạng thái hóa đơn. Chưa có cập nhật trạng thái
  bàn hoặc tích hợp ngân hàng/cổng thanh toán; phần bàn ăn hiện chưa được triển khai.

## Kiểm tra

Trước khi dọn các file test theo yêu cầu, phần tích hợp đã biên dịch thành công
và vượt qua 21 kiểm thử nghiệp vụ/controller/render Thymeleaf, bao gồm các trang
cũ của Hoang. Ba file test bổ sung đã được xóa; file
`QuanLyNhhsApplicationTests.java` có sẵn của Hoang được giữ nguyên.

Các kiểm thử đã chạy dùng repository/service giả lập và không khởi động ứng dụng.
Chưa kiểm thử CRUD, ánh xạ JPA và truy vấn trên Oracle thực tế. Không chạy
`contextLoads` có sẵn vì nó khởi tạo ứng dụng với datasource thật và sự kiện mở
trình duyệt của Hoang. Không thay đổi dữ liệu hoặc thực thi script SQL trên Oracle.
