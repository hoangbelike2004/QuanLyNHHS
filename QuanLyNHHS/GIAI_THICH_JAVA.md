# Cách đọc các class Java trong dự án

Các file Java trong `src/main/java` và `src/test/java` đã được chú thích bằng
tiếng Việt. Phía trên mỗi class là vai trò của class; phía trên từng hàm là chức
năng, tham số (`@param`) và kết quả (`@return`, nếu có). Biến thành viên và biến
cục bộ được giải thích ngay tại nơi khai báo. Có thể rê chuột lên tên hàm trong
IDE để đọc Javadoc.

## Vai trò các tầng

| Vị trí | Vai trò |
| --- | --- |
| `controller` | Nhận URL và dữ liệu request, gọi service, trả template hoặc chuyển hướng |
| `service` | Xử lý quy tắc nghiệp vụ, tính toán, kiểm tra quan hệ và giao dịch |
| `repository` | Truy vấn và lưu dữ liệu bằng JPA hoặc JDBC |
| `model` | Entity ánh xạ dữ liệu Java với bảng/cột database; chứa cả các lớp khóa kép |
| `dto` | Dữ liệu nhận từ biểu mẫu; annotation validation kiểm tra dữ liệu nhập |
| `config/SecurityConfig` | Đăng nhập, đăng xuất, URL được truy cập và bộ băm mật khẩu |
| `QuanLyNhhsApplication` | Khởi động ứng dụng và mở trình duyệt sau khi sẵn sàng |
| `src/test/java` | Kiểm tra hành vi bằng dữ liệu giả hoặc database H2 trong bộ nhớ |

## Các biến thường gặp

| Tên | Cách sử dụng |
| --- | --- |
| `id`, `maMon`, `maLoai`… | Xác định bản ghi cần đọc/sửa/xóa. Riêng các hàm `save(id, form)` dùng `id == null` để thêm mới |
| `form` | DTO nhận dữ liệu từ form; trong SecurityConfig, lambda `form` là đối tượng cấu hình đăng nhập |
| `entity` | Đối tượng ánh xạ database, được lấy ra hoặc tạo mới để thao tác |
| `repository` | Thành phần cung cấp các thao tác đọc/ghi database |
| `model` | Chứa các thuộc tính gửi sang Thymeleaf, không phải entity trong package model |
| `errors` | BindingResult chứa lỗi chuyển kiểu, validation hoặc lỗi nghiệp vụ |
| `redirect`, `flash`, `ra` | RedirectAttributes giữ thông báo cho request sau chuyển hướng |
| `this.tenBien` | Thuộc tính của đối tượng hiện tại, phân biệt với tham số trùng tên |
| `row`, `entry`, `d`, `s` | Phần tử đang được xử lý trong lambda/vòng lặp; ý nghĩa cụ thể được ghi ngay trong hàm |

Ví dụ `model.addAttribute("dishes", dishes)`: biến Java `dishes` chứa danh sách
món, còn chuỗi `"dishes"` là tên thuộc tính để HTML đọc bằng `${dishes}`.
`activeMenu` xác định menu đang chọn; `formAction` là URL nhận POST;
`editingId` là mã đang sửa; `showForm` quyết định hiển thị hộp thoại.

## Ví dụ đọc LoaiMonService.save

1. `id` là mã loại cần sửa; null nghĩa là thêm.
2. `form` chứa tên, mô tả, trạng thái đã được kiểm tra.
3. `entity` được tạo mới hoặc đọc bằng `getById(id)`.
4. Các setter sao chép dữ liệu từ DTO vào entity.
5. `saveAndFlush(entity)` gửi thay đổi xuống database. Commit diễn ra khi giao
   dịch hoàn tất, không đồng nghĩa với việc gọi flush là tự commit.
6. Hàm trả entity đã lưu để nơi gọi dùng tiếp.

## Các kiểu và cú pháp cần nhớ

- `Long`: kiểu mã dạng số, có thể null. `long` không nhận null.
- `BigDecimal`: tiền hoặc lượng có phần thập phân; các phép cộng/trừ/nhân tạo
  kết quả mới nên phải nhận kết quả vào biến.
- `LocalDate`: ngày, không bao gồm giờ.
- `List<T>`: danh sách phần tử kiểu T.
- `Optional<T>`: kết quả có thể không tìm thấy; kiểm tra trước khi gọi `get()`.
- `Map<K, V>`: tra giá trị V theo khóa K. Ví dụ `Map<Long, Long>` đếm món:
  khóa là mã loại, giá trị là số món; `Map<Long, BigDecimal>` lượng bán dùng mã món.
- `Object[]`: một dòng truy vấn gồm nhiều cột. Chú thích ở repository/service
  nêu rõ cột nào nằm tại `[0]`, `[1]` hoặc `[2]`.
- `var`: Java suy luận kiểu từ biểu thức bên phải, không phải biến không có kiểu.
- `private final`: tham chiếu được gán khi khởi tạo và không gán lại; đối tượng
  mà nó trỏ tới vẫn có thể thay đổi trạng thái.
- `static final`: hằng dùng chung của class, như trạng thái hóa đơn hoặc giới hạn tiền.
- `@Transactional`: xác định giao dịch; các thao tác ghi phối hợp trong cùng
  giao dịch có thể được rollback khi gặp lỗi runtime.
- `@Valid` và `@Validated`: kích hoạt kiểm tra các ràng buộc theo cách class
  được Spring/controller gọi; getter/setter thông thường không tự lưu database.

Interface repository trống vẫn có các hàm kế thừa từ `JpaRepository`, như
`findAll()`, `findById(id)`, `existsById(id)`, `count()`, `save(entity)`,
`saveAndFlush(entity)`, `delete(entity)`, `deleteById(id)` và `flush()`.
Spring Data tạo phần triển khai; không cần viết lại các hàm đó trong mỗi interface.

Luồng CRUD của phần Hoàng được mô tả thêm trong [HOANG_CRUD.md](HOANG_CRUD.md).
