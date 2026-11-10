# Phần thực đơn và nhân viên của Nguyễn Cao Hoàng

Ba module dùng luồng `Controller -> Service -> Repository -> Oracle`.
Controller nhận yêu cầu; DTO nhận và kiểm tra dữ liệu nhập; Service xử lý nghiệp vụ
trong giao dịch; Repository làm việc với database. Các form thêm/sửa dùng chung
giao diện hộp thoại, giữ lại dữ liệu và hiện lỗi theo từng trường khi không hợp lệ.

## Các file nên đọc

1. `src/main/java/com/QuanLyNHHS/dto/MonAnForm.java`, `LoaiMonForm.java`,
   `NhanVienForm.java`: các trường được phép nhập, giới hạn độ dài, giá/lương,
   trạng thái, email và ngày tháng. Khóa chính không lấy từ nội dung form.
2. `src/main/java/com/QuanLyNHHS/service/MonAnService.java`, `LoaiMonService.java`,
   `NhanVienService.java`: đọc dữ liệu, thêm/sửa, kiểm tra quan hệ trước khi xóa.
   `@Transactional` đảm bảo một thao tác ghi lỗi được hoàn tác.
3. Ba Controller tương ứng: mở danh sách/form, nhận POST, hiển thị lỗi hoặc
   chuyển về danh sách kèm thông báo thành công.
4. `src/main/resources/templates/monan/index.html`, `loaimon/index.html`,
   `nhanvien/index.html`: hiển thị dữ liệu và gửi form bằng Thymeleaf.
5. `src/main/resources/static/js/main.js`: tìm kiếm kết hợp bộ lọc,
   chuyển chế độ bảng/thẻ, đưa con trỏ vào trường lỗi.

## Đường dẫn

Thay `{module}` bằng `mon-an`, `loai-mon` hoặc `nhan-vien`:

| Phương thức | URL | Chức năng |
| --- | --- | --- |
| GET | `/{module}` | Danh sách và thống kê |
| GET | `/{module}/them` | Mở form thêm |
| POST | `/{module}/them` | Lưu mới |
| GET | `/{module}/sua/{id}` | Mở form có dữ liệu hiện tại |
| POST | `/{module}/sua/{id}` | Cập nhật đúng bản ghi theo URL |
| POST | `/{module}/xoa/{id}` | Xóa sau khi kiểm tra quan hệ |

## Quy tắc chính

- Món phải có loại món tồn tại, tên, đơn vị tính, giá và trạng thái hợp lệ.
- Giá/lương không âm, tối đa 10 chữ số nguyên và 2 chữ số thập phân,
  phù hợp `NUMBER(12,2)`. Lương và các thông tin nhân viên tùy chọn có thể để trống.
- Không xóa loại món còn món ăn; không xóa món đã có trong bất kỳ hóa đơn nào.
- Không xóa nhân viên đã lập hóa đơn hoặc phiếu nhập. Có thể đổi trạng thái thành
  `Đã nghỉ việc`. Chức vụ không làm thay đổi quyền của tài khoản đăng nhập.
- Sửa mã không tồn tại không tạo bản ghi mới. GET sửa mã đã mất trả HTTP 404.
- Form ghi dữ liệu dùng POST và CSRF của Spring Security. Theo lựa chọn của người
  dùng, giữ quy tắc đăng nhập hiện tại; mọi tài khoản đã đăng nhập có thể dùng CRUD.
- Cập nhật giá món không sửa đơn giá đã lưu trong chi tiết hóa đơn cũ.

## Kiểm tra

Chạy trong thư mục có `pom.xml`:

```powershell
mvn test
node --test src/test/js/menu-filters.test.cjs
```

Bộ test Java sử dụng profile `test`, H2 trong bộ nhớ, cấu hình riêng không tải
class mở trình duyệt. H2 chỉ là dependency cho test. Không thực thi SQL vào Oracle.
Các kiểm thử bao gồm CRUD qua HTTP/Thymeleaf, form sửa, kiểm tra dữ liệu,
chặn xóa dữ liệu liên quan, đăng nhập/BCrypt và CSRF. Test JavaScript kiểm tra
tìm kiếm kết hợp bộ lọc và chuyển chế độ hiển thị.

Khi chạy ứng dụng bình thường vẫn dùng cấu hình Oracle hiện có trong
`application.properties`. Chưa kiểm thử các thay đổi này trên Oracle thật.
