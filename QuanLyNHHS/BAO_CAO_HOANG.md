# Tài liệu ôn báo cáo – Nguyễn Cao Hoàng

Tài liệu được đối chiếu từ `PhanCong_Nhom4_QuanLyNhaHangHaiSan.xlsx` và mã nguồn trong workspace ngày 10/11/2026. Đây là phần việc được giao cho Hoàng và cách phần đó đang được triển khai; không phải kết luận rằng mọi dòng code liên quan đều do một mình Hoàng viết. Các ô tiến độ trong Excel vẫn ghi “Chưa bắt đầu”, nên không dùng chúng để kết luận trạng thái mã hiện tại.

## 1. Hoàng phụ trách những gì?

Theo sheet **Tổng quan**, dòng Nguyễn Cao Hoàng, phạm vi là **Thực đơn & Nhân viên & Security**, nhánh dự kiến `feature/menu-security`.

| Phần việc | Nội dung cần trình bày | Bảng dữ liệu chính |
| --- | --- | --- |
| Loại món | Xem danh sách, thêm, sửa, xóa, tìm kiếm, đếm món theo loại | `LOAI_MON` |
| Món ăn | Quản lý thông tin món, loại món, giá, đơn vị, ảnh, mô tả, trạng thái; tìm kiếm và lọc | `MON_AN` |
| Nhân viên | Quản lý hồ sơ, chức vụ, ca làm, lương, ngày vào làm và trạng thái | `NHAN_VIEN` |
| Đăng nhập và bảo mật | Giao diện đăng nhập, Spring Security, kiểm soát truy cập; mã hiện tại có thêm đăng ký tài khoản | Mã hiện tại dùng thêm `TAI_KHOAN` |
| Oracle/SQL | Ánh xạ bảng, khóa chính/khóa ngoại, CRUD, truy vấn món, script role và privilege | Ba bảng được phân công và các bảng liên quan |

Phần khách hàng/bàn ăn/đặt bàn được giao cho Đức; bán hàng/hóa đơn/báo cáo được giao cho Chiến; kho/nhập hàng được giao cho Đạt. Hoàng có dùng dữ liệu các phần đó để kiểm tra ràng buộc hoặc hiển thị số lượng bán, nhưng đó là các điểm tích hợp.

## 2. Các chức năng trong mã hiện tại

### Loại món – `/loai-mon`

- Hiển thị loại món gồm mã, tên, mô tả và trạng thái `Hoạt động`/`Tạm dừng`.
- Thêm mới, mở form sửa với dữ liệu cũ, cập nhật và xóa.
- Tìm kiếm trên giao diện; hiển thị tổng loại và số món của từng loại.
- Tên không được trống, tối đa 100 ký tự; mô tả tối đa 255 ký tự.
- **Chặn xóa loại đang có món ăn**. Cần chuyển món sang loại khác trước.

File chính: [LoaiMonService.java](src/main/java/com/QuanLyNHHS/service/LoaiMonService.java), [LoaiMonController.java](src/main/java/com/QuanLyNHHS/controller/LoaiMonController.java), [LoaiMonForm.java](src/main/java/com/QuanLyNHHS/dto/LoaiMonForm.java), [giao diện loại món](src/main/resources/templates/loaimon/index.html).

### Món ăn – `/mon-an`

- Quản lý tên món, loại món, đơn vị tính, đơn giá, trạng thái, đường dẫn ảnh và mô tả.
- Có trạng thái `Đang phục vụ` và `Tạm hết`; thống kê tổng món và số món theo hai trạng thái này.
- Tìm kiếm kết hợp lọc theo loại món, chuyển cách xem dạng thẻ hoặc bảng.
- Loại món được chọn phải tồn tại trong database.
- Giá bắt buộc, không âm, tối đa 10 chữ số nguyên và 2 chữ số thập phân. Đường dẫn ảnh nếu nhập phải bắt đầu bằng HTTP hoặc HTTPS. Đây là nhập URL ảnh, không phải chức năng tải file ảnh lên.
- Số lượng đã bán lấy từ chi tiết hóa đơn có trạng thái `Da thanh toan`, cộng theo mã món.
- **Chặn xóa món đã có trong bất kỳ hóa đơn nào**, kể cả hóa đơn chưa thanh toán. Có thể chuyển món sang `Tạm hết`.
- Sửa giá món chỉ cập nhật món đó; hàm này không sửa đơn giá đã lưu trong chi tiết hóa đơn cũ.

File chính: [MonAnService.java](src/main/java/com/QuanLyNHHS/service/MonAnService.java), [MonAnController.java](src/main/java/com/QuanLyNHHS/controller/MonAnController.java), [MonAnForm.java](src/main/java/com/QuanLyNHHS/dto/MonAnForm.java), [giao diện món ăn](src/main/resources/templates/monan/index.html).

### Nhân viên – `/nhan-vien`

- Quản lý họ tên, giới tính, số điện thoại, email, chức vụ, ca làm, lương, ngày vào làm và trạng thái.
- Có trạng thái `Đang làm việc`, `Nghỉ phép`, `Đã nghỉ việc`.
- Hiển thị tổng nhân viên, số đang làm việc và số nghỉ phép; tìm kiếm kết hợp lọc theo chức vụ.
- Họ tên và chức vụ bắt buộc; kiểm tra định dạng email, số điện thoại và lương không âm nếu có nhập. Lương có thể để trống.
- **Chặn xóa nhân viên đã lập hóa đơn hoặc phiếu nhập**, nhằm giữ thông tin người lập chứng từ. Khi nghỉ việc có thể đổi trạng thái.
- Đây là quản lý thông tin lương trong hồ sơ; mã phần này chưa phải một hệ thống chấm công hoặc tính lương tự động.

File chính: [NhanVienService.java](src/main/java/com/QuanLyNHHS/service/NhanVienService.java), [NhanVienController.java](src/main/java/com/QuanLyNHHS/controller/NhanVienController.java), [NhanVienForm.java](src/main/java/com/QuanLyNHHS/dto/NhanVienForm.java), [giao diện nhân viên](src/main/resources/templates/nhanvien/index.html).

### Đăng ký, đăng nhập, đăng xuất

- `/register`: nhận thông tin đăng ký, kiểm tra tên đăng nhập trùng và xác nhận mật khẩu, băm mật khẩu bằng BCrypt rồi lưu `TAI_KHOAN`.
- `/login`: Spring Security xác thực tài khoản từ database. Thành công chuyển về `/`; sai thông tin chuyển về `/login?error`.
- Đăng xuất hủy phiên đăng nhập và xóa cookie `JSESSIONID`.
- Trang đăng nhập, đăng ký và tài nguyên CSS/JS/ảnh được truy cập công khai; các URL còn lại yêu cầu đăng nhập.
- Các form ghi dữ liệu dùng POST và cơ chế CSRF của Spring Security.

File chính: [SecurityConfig.java](src/main/java/com/QuanLyNHHS/config/SecurityConfig.java), [TaiKhoanService.java](src/main/java/com/QuanLyNHHS/service/TaiKhoanService.java), [AuthController.java](src/main/java/com/QuanLyNHHS/controller/AuthController.java), [TaiKhoan.java](src/main/java/com/QuanLyNHHS/model/TaiKhoan.java).

**Giới hạn cần nói đúng:** Bảng phân công yêu cầu phân quyền URL theo role. Trong mã hiện tại, tài khoản đều được cấp `ROLE_USER`, và cấu hình dùng `anyRequest().authenticated()`. Chưa có quy tắc chia quyền CRUD theo vai trò quản lý/thu ngân/phục vụ. Trường `chucVu` của nhân viên không tự cấp quyền cho tài khoản; `TAI_KHOAN` hiện cũng không có quan hệ ánh xạ với `NHAN_VIEN`.

Script [oracle_setup_user.sql](src/main/resources/oracle_setup_user.sql) có khai báo các role Oracle như `ROLE_ADMIN`, `ROLE_QUAN_LY`, `ROLE_PHUC_VU`, `ROLE_BAO_CAO` và cấp quyền trên bảng. Quyền của user kết nối Oracle và quyền của người đăng nhập website là hai lớp khác nhau. Có script không chứng minh script đã chạy trên database, cũng không tự tạo phân quyền URL cho website.

## 3. Kiến trúc: mỗi tầng làm gì?

Luồng đọc dữ liệu:

```text
Trình duyệt gửi GET
  → Controller nhận URL
  → Service xử lý yêu cầu
  → Repository truy vấn database Oracle qua JPA/Hibernate
  → Controller đưa dữ liệu vào Model
  → Thymeleaf dựng HTML trả về trình duyệt
```

Luồng ghi dữ liệu:

```text
Form HTML gửi POST
  → Spring Security kiểm tra đăng nhập và CSRF
  → Controller nhận DTO và kiểm tra @Valid/BindingResult
  → Service kiểm tra nghiệp vụ, thêm/sửa/xóa trong giao dịch
  → Repository ghi database
  → Thành công: chuyển hướng về danh sách kèm thông báo
  → Dữ liệu không hợp lệ: mở lại form và hiển thị lỗi
```

| Thành phần | Cách giải thích khi báo cáo |
| --- | --- |
| Entity/Model | Đại diện bản ghi, ánh xạ class và thuộc tính Java vào bảng/cột Oracle |
| DTO/Form | Chứa các trường được nhập và quy tắc kiểm tra dữ liệu; tách dữ liệu form khỏi entity |
| Controller | Nhận request, gọi service, chọn giao diện và đưa thông báo cho người dùng |
| Service | Xử lý quy tắc nghiệp vụ, kiểm tra quan hệ và quản lý giao dịch |
| Repository | Cung cấp thao tác đọc/ghi qua `JpaRepository`, bổ sung truy vấn khi cần |
| Thymeleaf | Đưa dữ liệu từ server vào HTML bằng `th:each`, `th:text`, `th:field`… |
| JavaScript | Tìm kiếm/lọc dữ liệu đã tải trên trang, chuyển bảng/thẻ và hỗ trợ hộp thoại |

Tìm kiếm trong ba màn hình hiện tại chủ yếu lọc các dòng/thẻ đã tải bằng JavaScript, không phải mỗi lần gõ đều gửi một truy vấn tìm kiếm mới đến Oracle.

## 4. Giải thích file LoaiMonService.java đang mở

Đây là file thích hợp để học trước vì đủ ngắn và thể hiện gần hết luồng CRUD.

| Thành phần | Ý nghĩa |
| --- | --- |
| `@Service` | Đăng ký lớp nghiệp vụ để Spring quản lý |
| Constructor nhận hai repository | Spring truyền đối tượng truy cập loại món và món ăn vào service |
| `@Validated`, `@Valid`, `@NotNull` | Kiểm tra dữ liệu tham số khi service được gọi qua bean Spring |
| `@Transactional(readOnly = true)` | Các hàm mặc định được khai báo là giao dịch đọc |
| `@Transactional` trên `save`/`delete` | Hai thao tác ghi có giao dịch riêng; lỗi runtime có thể làm rollback |
| `findAll()` | Lấy toàn bộ loại món |
| `getById(id)` | Lấy loại theo mã, không có thì ném `EntityNotFoundException` |
| `demMonTheoLoai()` | Nhận kết quả đếm món theo loại, đổi thành `Map<mã loại, số món>` |
| `save(id, form)` | `id == null` thì thêm mới; có mã thì lấy bản ghi cũ và cập nhật |
| `delete(id)` | Kiểm tra loại còn món không, đủ điều kiện mới xóa |

Ví dụ thêm loại “Món hấp”:

1. Truy cập `/loai-mon/them`: controller mở form rỗng.
2. Nhập tên, mô tả, trạng thái rồi gửi POST `/loai-mon/them`.
3. `LoaiMonForm` kiểm tra dữ liệu; controller chỉ gọi service nếu không có lỗi.
4. `service.save(null, form)` tạo `LoaiMon` mới, sao chép các trường từ form.
5. `repository.saveAndFlush(entity)` gửi thay đổi xuống database; mã mới lấy từ sequence `SEQ_LOAI_MON`.
6. Giao dịch hoàn tất, controller chuyển về `/loai-mon` và hiện thông báo thành công.

Khi sửa, ví dụ `/loai-mon/sua/5`, mã 5 lấy từ URL. Service gọi `getById(5)` để sửa đúng bản ghi; nếu mã không tồn tại thì không tự tạo bản ghi mới. GET mở form sửa mã không tồn tại được trả thành trang HTTP 404.

`flush` gửi thay đổi đang chờ xuống database để sớm phát hiện lỗi ràng buộc; **flush không đồng nghĩa commit**. Commit là hoàn tất giao dịch.

## 5. Database và các điểm tích hợp cần nhớ

- `LOAI_MON` có khóa chính `MA_LOAI`; `MON_AN` có khóa chính `MA_MON` và khóa ngoại `MA_LOAI`. Một loại có thể có nhiều món; `MonAn` dùng `@ManyToOne` để tham chiếu `LoaiMon`.
- `NHAN_VIEN` có khóa chính `MA_NV`; hóa đơn và phiếu nhập dùng mã nhân viên để xác định người lập.
- Các entity loại món, món ăn, nhân viên sinh mã bằng sequence tương ứng `SEQ_LOAI_MON`, `SEQ_MON_AN`, `SEQ_NHAN_VIEN`.
- Giá và lương dùng `BigDecimal`, phù hợp dữ liệu tiền và kiểu số thập phân của database.
- `MonAnRepository.demMonTheoLoai()` dùng `COUNT` và `GROUP BY` để đếm món theo loại.
- `CTHoaDonRepository.tongSoLuongDaBanTheoMon()` nối chi tiết với hóa đơn, lọc hóa đơn đã thanh toán rồi `SUM` số lượng theo món. Đây là dữ liệu phục vụ trang món ăn; module báo cáo doanh thu được giao cho Chiến.
- Chặn xóa ở service giúp báo lỗi dễ hiểu; ràng buộc database vẫn là lớp bảo vệ khi dữ liệu liên quan thay đổi đồng thời.

## 6. Bài nói mẫu khoảng 2–3 phút

> Em là Nguyễn Cao Hoàng, phụ trách phần thực đơn, nhân viên và đăng nhập bảo mật trong dự án quản lý nhà hàng hải sản. Ba bảng chính thuộc phần em là LOAI_MON, MON_AN và NHAN_VIEN. Trong mã hiện tại, phần tài khoản đăng nhập sử dụng thêm bảng TAI_KHOAN.
>
> Về thực đơn, hệ thống cho phép xem, thêm, sửa, xóa loại món và món ăn. Mỗi món thuộc một loại, có đơn vị tính, đơn giá và trạng thái đang phục vụ hoặc tạm hết. Giao diện hỗ trợ tìm kiếm, lọc món theo loại và chuyển giữa dạng thẻ với dạng bảng. Số lượng món đã bán được tổng hợp từ các hóa đơn đã thanh toán.
>
> Về nhân viên, hệ thống quản lý hồ sơ gồm họ tên, số điện thoại, email, chức vụ, ca làm, lương và trạng thái làm việc. Các form kiểm tra dữ liệu trước khi lưu, chẳng hạn tên không được để trống, email đúng định dạng và giá hoặc lương không âm.
>
> Phần em triển khai theo các tầng Controller, Service, Repository và Entity. Controller nhận yêu cầu, DTO kiểm tra dữ liệu nhập, Service xử lý nghiệp vụ, Repository làm việc với Oracle qua JPA, còn Thymeleaf hiển thị dữ liệu lên giao diện.
>
> Một quy tắc cần thiết là bảo vệ dữ liệu liên quan: không xóa loại đang có món, không xóa món đã nằm trong hóa đơn, và không xóa nhân viên đã lập hóa đơn hoặc phiếu nhập. Những trường hợp này hệ thống báo lý do để người dùng xử lý phù hợp.
>
> Về bảo mật, hệ thống dùng Spring Security để đăng nhập, đăng xuất, bảo vệ các trang nghiệp vụ, băm mật khẩu bằng BCrypt và kiểm tra CSRF cho form ghi dữ liệu. Phân quyền chi tiết theo vai trò trên website hiện chưa được cấu hình; mã đang yêu cầu đăng nhập chung. Script role và privilege Oracle là phần quyền truy cập ở tầng cơ sở dữ liệu.

Đọc hiểu rồi diễn đạt bằng lời của mình. Khi báo cáo kết quả kiểm thử, chỉ nói những tình huống mình đã thực sự chạy và kiểm tra.

## 7. Những câu dễ được hỏi

**1. CRUD là gì?** Tạo mới, đọc, cập nhật, xóa; ví dụ thêm món, xem danh sách, sửa giá và xóa món chưa được sử dụng.

**2. Vì sao dùng Service thay vì viết hết trong Controller?** Tách nghiệp vụ khỏi HTTP và giao diện, để dễ tái sử dụng, quản lý giao dịch và kiểm thử.

**3. Vì sao cần DTO khi đã có Entity?** DTO giới hạn dữ liệu được nhận từ form và đặt validation. Các controller còn dùng `setAllowedFields`; khóa chính không được lấy tùy ý từ nội dung form để quyết định bản ghi cần sửa.

**4. Repository ít code thì CRUD nằm ở đâu?** Kế thừa `JpaRepository`; Spring Data cung cấp triển khai cho các hàm như `findAll`, `findById`, `saveAndFlush`, `delete`.

**5. Vì sao không cho xóa món đã có hóa đơn?** Cần giữ quan hệ và lịch sử chứng từ. Có thể đổi trạng thái món thành tạm hết thay vì xóa.

**6. Một loại có nhiều món biểu diễn thế nào?** `MON_AN.MA_LOAI` tham chiếu `LOAI_MON.MA_LOAI`; phía entity `MonAn` dùng `@ManyToOne`.

**7. `@Transactional` dùng làm gì?** Gom thao tác trong một giao dịch, đảm bảo thay đổi được commit khi thành công hoặc rollback khi gặp lỗi phù hợp.

**8. Vì sao tiền dùng `BigDecimal`?** Biểu diễn và tính số thập phân chính xác theo cách phù hợp hơn với tiền so với kiểu dấu phẩy động như `double`.

**9. Mật khẩu lưu thế nào?** Lưu chuỗi băm BCrypt; khi đăng nhập dùng `PasswordEncoder` đối chiếu, không giải mã để lấy lại mật khẩu gốc.

**10. Đăng nhập và phân quyền khác nhau thế nào?** Đăng nhập xác định danh tính; phân quyền xác định người đó được làm gì. Website hiện yêu cầu đăng nhập chung, chưa tách quyền chức năng theo vai trò.

**11. Đổi chức vụ nhân viên thành quản lý có được thêm quyền không?** Không. Chức vụ hiện là dữ liệu hồ sơ; quyền tài khoản được xử lý riêng.

**12. Đã kiểm thử những gì?** Repository có test cho CRUD, validation, form sửa, chặn xóa liên quan, mã không tồn tại, đăng nhập/BCrypt và CSRF; test Java dùng H2. Trong lần rà soát để viết tài liệu này chưa chạy lại test hoặc ứng dụng Oracle. Không dùng sự tồn tại của test để khẳng định tất cả đang chạy đạt trên Oracle thật.

## 8. Kịch bản demo nên luyện

1. Chưa đăng nhập, mở một trang nghiệp vụ để xem ứng dụng yêu cầu đăng nhập; sau đó đăng nhập bằng tài khoản đã chuẩn bị.
2. Vào loại món, thêm một loại mới để demo. Mở sửa và kiểm tra form đã có dữ liệu cũ.
3. Vào món ăn, thêm món thuộc loại vừa tạo; thử tìm kiếm, lọc loại, chuyển bảng/thẻ và sửa giá.
4. Thử xóa loại đang chứa món: kiểm tra thông báo chặn xóa. Nếu món demo chưa có hóa đơn, xóa món trước rồi xóa loại.
5. Vào nhân viên, thêm hồ sơ demo, sửa trạng thái hoặc ca làm; xóa hồ sơ demo chưa gắn chứng từ.
6. Với dữ liệu chuẩn bị sẵn, minh họa chặn xóa món đã có hóa đơn hoặc nhân viên đã lập chứng từ.
7. Đăng xuất và kiểm tra lại việc truy cập trang nghiệp vụ.

Dùng dữ liệu demo riêng và thử trước buổi báo cáo. Để minh họa lỗi validation trên server, có thể chuẩn bị test sẵn: trình duyệt có thể chặn ngay một số dữ liệu sai trước khi form được gửi.

## 9. Thứ tự đọc nếu thời gian ít

1. Đọc mục 1, 2 và bài nói mẫu để nắm phạm vi công việc.
2. Đọc [LoaiMonService.java](src/main/java/com/QuanLyNHHS/service/LoaiMonService.java), sau đó [LoaiMonController.java](src/main/java/com/QuanLyNHHS/controller/LoaiMonController.java) và [LoaiMonForm.java](src/main/java/com/QuanLyNHHS/dto/LoaiMonForm.java) để hiểu trọn một luồng.
3. Đọc `save` và `delete` trong [MonAnService.java](src/main/java/com/QuanLyNHHS/service/MonAnService.java), [NhanVienService.java](src/main/java/com/QuanLyNHHS/service/NhanVienService.java) để nhớ các khác biệt nghiệp vụ.
4. Đọc [SecurityConfig.java](src/main/java/com/QuanLyNHHS/config/SecurityConfig.java) và [TaiKhoanService.java](src/main/java/com/QuanLyNHHS/service/TaiKhoanService.java), nhất là giới hạn phân quyền.
5. Mở ba màn hình, tự demo rồi trả lời các câu hỏi ở mục 7 bằng lời của mình.

Tài liệu bổ sung có sẵn: [HOANG_CRUD.md](HOANG_CRUD.md) mô tả endpoint và quy tắc CRUD; [GIAI_THICH_JAVA.md](GIAI_THICH_JAVA.md) giải thích cú pháp và vai trò các tầng Java.
