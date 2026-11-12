# Quản lý nhà hàng Hải Sản Hoàng Gia

Nhánh `DatNew` gồm quản lý kho, nhân viên, thực đơn, khách hàng, bàn ăn, đặt bàn và hóa đơn. Dùng Spring Boot 4.1.1, Java 27, Spring Data JPA và Thymeleaf.

## Chạy demo

Yêu cầu JDK 27. Maven Wrapper đã có sẵn:

```powershell
cd QuanLyNHHS
.\mvnw.cmd test
$env:APP_ADMIN_PASSWORD='mat_khau_quan_tri_cua_ban'
.\mvnw.cmd spring-boot:run
```

Mở `http://localhost:8080/` hoặc `/kho`. Tạo nhà cung cấp → tạo nguyên liệu → lập phiếu nhập. Các trang:

- `/nha-cung-cap`: thêm, xem, sửa, xóa nhà cung cấp.
- `/nguyen-lieu`: thêm, xem, sửa, ngừng sử dụng nguyên liệu và xem tồn kho.
- `/phieu-nhap`: danh sách, lập, xem chi tiết, sửa và xóa phiếu nhập nhiều dòng.

Profile mặc định `demo` dùng H2 trong bộ nhớ; **dữ liệu demo mất khi dừng ứng dụng**. Mở `/login`, đăng nhập tài khoản `admin` bằng `APP_ADMIN_PASSWORD`. Sau khi tạo nhân viên, khách hàng và bàn, tạo đặt bàn rồi chọn nhân viên phụ trách trong form xác nhận. Đặt bàn đã xác nhận có thể gọi món và lập hóa đơn; hóa đơn giữ mã nhân viên được chọn.

## Logic Java

- `PhieuNhapService` quản lý toàn bộ phiếu, chi tiết và tồn kho trong **một giao dịch**. Lỗi ở bất kỳ dòng nào sẽ rollback toàn bộ.
- Tạo phiếu: cộng số lượng nhập. Sửa phiếu: cộng `số lượng mới - số lượng cũ`, kể cả thêm/bỏ nguyên liệu. Xóa phiếu: trừ lại số lượng của phiếu; chặn nếu hàng đã được sử dụng khiến tồn không đủ.
- Tiền dùng `BigDecimal`; thành tiền mỗi dòng là `số lượng × đơn giá`, làm tròn hai chữ số `HALF_UP`; tổng phiếu bằng tổng các thành tiền đã làm tròn. Không nhận tổng tiền, thành tiền hoặc số lượng tồn do trình duyệt gửi lên.
- Số lượng phải dương, đơn giá không âm; tối đa hai chữ số thập phân. Kiểm tra giới hạn `NUMBER(10,2)`, `NUMBER(12,2)`, `NUMBER(14,2)`, ngày tương lai, mã không tồn tại, nguyên liệu trùng và phiếu rỗng/tối đa 100 dòng.
- Khóa dòng phiếu và nguyên liệu trong giao dịch; khóa nguyên liệu theo mã tăng dần để tránh tranh chấp. Làm mới số tồn sau khi lấy khóa để tránh dùng dữ liệu JPA đã tải trước đó.
- Cột bổ sung `PHIEU_NHAP.PHIEN_BAN` dùng `@Version` để chặn sửa/xóa từ trang cũ, kể cả chỉ sửa chi tiết mà tổng tiền không đổi.
- Nguyên liệu mới có tồn bằng 0. Form danh mục không sửa trực tiếp tồn kho. Nguyên liệu còn tồn hoặc đã có phiếu không được đổi đơn vị hoặc xóa; có thể chuyển ngừng sử dụng. Dòng cũ của nguyên liệu ngừng sử dụng được giữ/giảm/xóa nhưng không nhập thêm.
- Chặn xóa nhà cung cấp đang có phiếu. FK của DB là lớp bảo vệ cuối cùng khi có thao tác đồng thời.
- `NhanVienNhapKhoRepository` chỉ đọc `MA_NV`, `HO_TEN` của bảng dùng chung `NHAN_VIEN`; không định nghĩa lại Entity/CRUD nhân viên của thành viên khác.

## Oracle

Profile `oracle` dùng Hibernate `update` để bổ sung bảng/cột còn thiếu cho các module ứng dụng, gồm `DAT_BAN.MA_NV` và `HOA_DON.MA_DAT`. Sao lưu schema có dữ liệu trước khi nâng cấp. Các procedure, trigger và ràng buộc đặc thù của kho vẫn cần script SQL. Trong schema mới:

1. Tạo bảng `NHAN_VIEN` từ module của nhóm trước. Nếu chỉ chạy thử trong schema riêng chưa có bảng này, có thể chạy **tùy chọn** `QuanLyNHHS/database/oracle/00_employee_demo.sql`.
2. Trong SQL Developer (Run Script/F5) hoặc SQL*Plus, chạy `QuanLyNHHS/database/oracle/01_inventory.sql`. Script tạo bốn bảng, sequence, ràng buộc, index và gọi `02_routines.sql` cùng thư mục. Không chạy script tạo bảng trên schema đã có bốn bảng này.
3. Nếu bốn bảng đã có sẵn: đối chiếu các cột/ràng buộc với `01_inventory.sql`; thêm `PHIEN_BAN NUMBER(19) DEFAULT 0 NOT NULL` vào `PHIEU_NHAP`; tạo ba sequence `SEQ_NHA_CUNG_CAP`, `SEQ_NGUYEN_LIEU`, `SEQ_PHIEU_NHAP` với `INCREMENT BY 1`, `START WITH` lớn hơn mã lớn nhất tương ứng. Sau đó chạy riêng `02_routines.sql`. Giữ nguyên dữ liệu hiện hữu và không cài trigger cộng tồn tự động.
4. Cấu hình bằng biến môi trường, không ghi mật khẩu vào Git:

```powershell
$env:SPRING_PROFILES_ACTIVE='oracle'
$env:ORACLE_URL='jdbc:oracle:thin:@//localhost:1521/orclpdb'
$env:ORACLE_USERNAME='ten_schema'
$env:ORACLE_PASSWORD='mat_khau_cua_ban'
$env:APP_ADMIN_PASSWORD='mat_khau_quan_tri_cua_ban'
cd QuanLyNHHS
.\mvnw.cmd spring-boot:run
```

`NHAN_VIEN` phải nằm trong cùng schema hoặc truy cập được qua synonym/quyền phù hợp; `MA_NV` phải có khóa chính/unique để tạo FK.

### Procedure và Trigger

- `SP_CAP_NHAT_TON_KHO(ma_nl, chenh_lech)` cập nhật tồn nguyên tử, chặn tồn âm/vượt giới hạn và chênh lệch quá hai chữ số thập phân.
- `SP_TINH_TONG_PHIEU_NHAP(ma_pn)` khóa phiếu, tính lại tổng tiền từ chi tiết và tăng phiên bản.
- `TRG_CTPN_THANH_TIEN` kiểm tra số lượng/đơn giá và tính thành tiền khi INSERT/UPDATE trực tiếp trên Oracle.

**Java là nơi cập nhật tồn cho các yêu cầu web.** Các procedure dành cho thao tác SQL chủ động; không gọi cộng tồn thêm sau khi Java đã lưu phiếu. Trigger chỉ tính tiền, không cộng tồn, tránh tính hai lần. Nếu nhập/sửa/xóa trực tiếp bằng SQL, người thao tác phải khóa phiếu trước, khóa nguyên liệu theo thứ tự mã tăng dần, gọi procedure tồn với phần chênh lệch tương ứng, rồi tính lại tổng trong cùng giao dịch; tự `COMMIT` hoặc `ROLLBACK`. Procedure không tự commit.

`03_smoke_test.sql` kiểm tra procedure/trigger, cách làm tròn và chặn tồn âm trong schema Oracle riêng; rollback dữ liệu test sau khi chạy, nhưng sequence vẫn tăng. Script SQL này cần được chạy trên Oracle thực tế; kiểm thử H2 không thay thế kiểm thử PL/SQL.

## Kiểm tra

```powershell
cd QuanLyNHHS
.\mvnw.cmd clean verify
```

Kiểm thử tích hợp dùng database H2 thật và HTTP/Thymeleaf thật, bao gồm tính tiền/làm tròn, CRUD phiếu, đổi nguyên liệu, rollback khi không đủ tồn, dữ liệu sai, tràn số, phiếu cũ, nhập đồng thời, ràng buộc danh mục và chống ghi đè tồn/tổng tiền qua form. Các kiểm thử không đòi hỏi Oracle đang chạy.

Đã bổ sung kiểm thử luồng xác nhận bàn → gọi món → hóa đơn và đăng nhập cho các kiểm thử HTTP. Toàn bộ bộ kiểm thử hiện có 32 ca.

Giao diện dùng HTML/CSS/JavaScript cục bộ, không cần frontend build.

Kiểm tra giao diện: lint không có lỗi/cảnh báo; đã xem form nhập ở 1440px, 768px, 390px và thử thêm/xóa dòng trên trình duyệt. Bảng cuộn trong khung riêng trên điện thoại, trang không tràn ngang; không có lỗi console. Script đo giao diện tự động không khởi động được Chrome/Edge trong môi trường này, nên đã kiểm tra bằng trình duyệt tích hợp.
