# Tích hợp kho và nhập hàng vào Hoang

Nguồn: `DatNew`, commit `02ac4006426c029304c06b45cee199802ccfc9c6`.
Phạm vi: phần Nhà cung cấp, Nguyên liệu, Phiếu nhập và Tồn kho của Lý Tiến Đạt.

## Sử dụng

- `/kho` chuyển đến danh sách phiếu nhập.
- `/nha-cung-cap`: thêm, sửa, xóa nhà cung cấp.
- `/nguyen-lieu`: quản lý nguyên liệu, đơn vị tính, trạng thái và xem tồn kho.
- `/phieu-nhap`: lập phiếu nhiều dòng, xem chi tiết, sửa và xóa phiếu.

Tạo nhà cung cấp và nguyên liệu trước, sau đó lập phiếu nhập. Danh sách nhân viên
đọc trực tiếp từ bảng Oracle `NHAN_VIEN`; phải có nhân viên thực trong database.
Các nhân viên mẫu hiển thị ở trang nhân viên hiện tại của Hoang không tự được lưu
vào database. Không thêm nhân viên demo khi tích hợp.

## Giữ nguyên các phần hiện có

- Giữ nguyên các file Java đã triển khai của Hoang và phần hóa đơn, báo cáo từ Chiến.
- Các file trống của module kho được điền bằng code DatNew. `CTPhieuNhap.java`
  trước đó chỉ có khai báo class rỗng; nay được điền entity chi tiết phiếu nhập.
- Giữ nguyên service, model, DTO, repository và các controller nghiệp vụ của DatNew.
  Riêng controller điều hướng kho chỉ nhận `/kho`, nhường `/` cho trang chủ Hoang.
- Giữ nguyên `application.properties`, class khởi động, cấu hình bảo mật và các
  dependency cũ. Chỉ thêm `spring-boot-starter-validation` cho form của module mới.
- Không mang cấu hình demo H2, dữ liệu mẫu, test hoặc cấu hình Oracle riêng từ DatNew
  vào ứng dụng. Các trang mới dùng sidebar, navbar và màu sắc Hoang; CSS kho chỉ
  áp dụng bên trong `.inventory-module`. Sidebar được bật liên kết Kho & Nguyên Liệu.

## Nghiệp vụ từ DatNew

- Tồn nguyên liệu mới bằng 0; form nguyên liệu không cho ghi đè tồn kho.
- Tạo phiếu cộng tồn; sửa phiếu điều chỉnh chênh lệch; xóa phiếu trừ lại lượng nhập.
  Thao tác bị chặn nếu gây tồn âm hoặc vượt giới hạn số. Giao dịch lỗi rollback toàn bộ.
- Tiền dùng `BigDecimal`, làm tròn từng dòng 2 chữ số với `HALF_UP` rồi cộng tổng.
- Phiếu có từ 1 đến 100 dòng; không lặp nguyên liệu; số lượng dương, đơn giá không
  âm, tối đa 2 chữ số thập phân; không nhập ngày tương lai hoặc mã không tồn tại.
- Khóa dữ liệu khi cập nhật và dùng phiên bản để chặn ghi đè từ form đã cũ.
- Không đổi đơn vị/xóa nguyên liệu còn tồn hoặc đã có phiếu. Nguyên liệu ngừng sử
  dụng không được nhập tăng thêm. Nhà cung cấp đã có phiếu không được xóa.

## Đối chiếu Oracle trước khi chạy với schema có sẵn

Ứng dụng giữ cấu hình `ddl-auto=update` của Hoang. Không tự chạy các script SQL
đã bổ sung. Mapping mới dùng 4 bảng `NHA_CUNG_CAP`, `NGUYEN_LIEU`, `PHIEU_NHAP`,
`CT_PHIEU_NHAP` và 3 sequence `SEQ_NHA_CUNG_CAP`, `SEQ_NGUYEN_LIEU`, `SEQ_PHIEU_NHAP`
với bước tăng 1. Nếu bảng đã có dữ liệu, sequence phải cấp mã lớn hơn mã lớn nhất
hiện tại; không tạo lại bảng hoặc reset sequence về 1.

`PHIEU_NHAP.PHIEN_BAN` là cột bổ sung ngoài sơ đồ ban đầu, phục vụ `@Version`.
Nếu bảng đã tồn tại nhưng chưa có cột này, cần bổ sung trước khi khởi động ứng dụng
để các dòng cũ có giá trị khởi đầu 0. Chỉ chạy câu sau khi đã xác nhận cột chưa có:

```sql
ALTER TABLE PHIEU_NHAP ADD (PHIEN_BAN NUMBER(19) DEFAULT 0 NOT NULL);
```

Nếu cột đã có nhưng chứa NULL, cần đối chiếu và bổ sung giá trị khởi đầu trước khi
đặt ràng buộc NOT NULL. Không đổi phiên bản của các phiếu đang được sử dụng.

`database/oracle/01_inventory.sql` từ DatNew dùng cho schema chưa có 4 bảng kho,
có tham chiếu `NHAN_VIEN` đã tồn tại; không chạy trên schema đã có các bảng này.
`02_routines.sql` chứa 2 procedure và trigger tính thành tiền. Java đã cập nhật
tồn kho cho các thao tác web; không cài thêm trigger cộng tồn hoặc gọi procedure
cộng tồn lần nữa sau khi Java lưu phiếu. Các procedure không tự commit.

## Kết quả kiểm tra

- Biên dịch Maven thành công với Java 27.
- 21 kiểm tra tích hợp từ DatNew chạy thành công với H2 trong bộ nhớ, trên code
  và giao diện đã ghép; gồm CRUD HTTP, tồn kho, làm tròn, rollback, phiên bản,
  nhập đồng thời, kiểm tra dữ liệu và ràng buộc danh mục.
- Kiểm tra bổ sung các trang `/`, `/mon-an`, `/loai-mon`, `/nhan-vien`, `/hoa-don`,
  `/hoa-don/them`, `/bao-cao` hiển thị thành công và có liên kết đến kho.
- Kiểm tra tạm nằm trong `target`, đã dọn sau khi chạy. `src/test` không thêm file.
  Cấu hình kiểm tra không tải class khởi động của Hoang để tránh tự mở trình duyệt.
- Chưa kiểm thử trên Oracle thực tế; không thực thi SQL hoặc thay đổi dữ liệu Oracle.
