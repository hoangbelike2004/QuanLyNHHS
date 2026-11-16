# Lịch sử tích hợp các module

Ghi lại nguồn nhánh, thay đổi và giới hạn tại thời điểm tích hợp. Để cài đặt, chạy ứng dụng và nạp dữ liệu hiện tại, xem [hướng dẫn Oracle](database/oracle/README.md).

## Tích hợp khách hàng, bàn ăn và đặt bàn vào Hoang

Nguồn: `DucNew`, commit `5eedd79fccc0195fc908e6b992f54b00032e1740`.
Phạm vi: phần Khách hàng, Bàn ăn và Đặt bàn của Hoàng Việt Đức.

### Chức năng và đường dẫn

- `/khach-hang`: danh sách, tìm theo tên, thêm, sửa, xóa khách hàng và điểm tích lũy.
- `/khach-hang/{maKh}/lich-su-dat-ban`: lịch sử đặt bàn của từng khách.
- `/ban-an`: thêm, sửa, xóa bàn ăn, lọc theo trạng thái.
- `/dat-ban`: tạo, sửa, lọc, xác nhận, hủy, hoàn thành và xóa đặt bàn.

Trình tự dùng: tạo khách hàng và bàn trạng thái `Trống`, lập đặt bàn, xác nhận,
sau đó hủy hoặc hoàn thành khi kết thúc. Các danh sách lấy dữ liệu từ database.

### Phạm vi thay đổi

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

### Nghiệp vụ được giữ nguyên

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

### Database

Ba entity ánh xạ `KHACH_HANG`, `BAN_AN`, `DAT_BAN`; dùng khóa `Long` sinh bằng
`GenerationType.IDENTITY` theo code Đức. Nếu các bảng Oracle đã có sẵn, cần đối
chiếu cơ chế sinh `MA_KH`, `MA_BAN`, `MA_DAT`; sơ đồ chỉ ghi `NUMBER` chưa xác định
cột identity hay sequence/trigger. Không thay đổi cơ chế sinh mã trong lần tích hợp.

`DAT_BAN` tham chiếu khách hàng và bàn bằng `@ManyToOne` với
`@NotFound(IGNORE)` của nguồn; liên kết không tìm thấy được đọc thành null.
Không thay thế cơ chế này hoặc tự thêm quan hệ với hóa đơn.

Không chạy script SQL, không thay đổi cấu hình hoặc dữ liệu Oracle trong lần này.

### Kiểm tra

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

---

## Tích hợp hóa đơn và báo cáo vào nhánh Hoang

Nguồn: nhánh `ChienNew`, commit `15cfa50c27ff31ee5d5f655dbdf026d906fc4d7c`.
Phạm vi theo bảng phân công: phần Bán hàng, Hóa đơn và Báo cáo của Đỗ Minh Chiến.

### Phần được bổ sung

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

### Sử dụng

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

### Nghiệp vụ được giữ nguyên từ ChienNew

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

### Kiểm tra

Trước khi dọn các file test theo yêu cầu, phần tích hợp đã biên dịch thành công
và vượt qua 21 kiểm thử nghiệp vụ/controller/render Thymeleaf, bao gồm các trang
cũ của Hoang. Ba file test bổ sung đã được xóa; file
`QuanLyNhhsApplicationTests.java` có sẵn của Hoang được giữ nguyên.

Các kiểm thử đã chạy dùng repository/service giả lập và không khởi động ứng dụng.
Chưa kiểm thử CRUD, ánh xạ JPA và truy vấn trên Oracle thực tế. Không chạy
`contextLoads` có sẵn vì nó khởi tạo ứng dụng với datasource thật và sự kiện mở
trình duyệt của Hoang. Không thay đổi dữ liệu hoặc thực thi script SQL trên Oracle.

---

## Tích hợp kho và nhập hàng vào Hoang

Nguồn: `DatNew`, commit `02ac4006426c029304c06b45cee199802ccfc9c6`.
Phạm vi: phần Nhà cung cấp, Nguyên liệu, Phiếu nhập và Tồn kho của Lý Tiến Đạt.

### Sử dụng

- `/kho` chuyển đến danh sách phiếu nhập.
- `/nha-cung-cap`: thêm, sửa, xóa nhà cung cấp.
- `/nguyen-lieu`: quản lý nguyên liệu, đơn vị tính, trạng thái và xem tồn kho.
- `/phieu-nhap`: lập phiếu nhiều dòng, xem chi tiết, sửa và xóa phiếu.

Tạo nhà cung cấp và nguyên liệu trước, sau đó lập phiếu nhập. Danh sách nhân viên
đọc trực tiếp từ bảng Oracle `NHAN_VIEN`; phải có nhân viên thực trong database.
Các nhân viên mẫu hiển thị ở trang nhân viên hiện tại của Hoang không tự được lưu
vào database. Không thêm nhân viên demo khi tích hợp.

### Giữ nguyên các phần hiện có

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

### Nghiệp vụ từ DatNew

- Tồn nguyên liệu mới bằng 0; form nguyên liệu không cho ghi đè tồn kho.
- Tạo phiếu cộng tồn; sửa phiếu điều chỉnh chênh lệch; xóa phiếu trừ lại lượng nhập.
  Thao tác bị chặn nếu gây tồn âm hoặc vượt giới hạn số. Giao dịch lỗi rollback toàn bộ.
- Tiền dùng `BigDecimal`, làm tròn từng dòng 2 chữ số với `HALF_UP` rồi cộng tổng.
- Phiếu có từ 1 đến 100 dòng; không lặp nguyên liệu; số lượng dương, đơn giá không
  âm, tối đa 2 chữ số thập phân; không nhập ngày tương lai hoặc mã không tồn tại.
- Khóa dữ liệu khi cập nhật và dùng phiên bản để chặn ghi đè từ form đã cũ.
- Không đổi đơn vị/xóa nguyên liệu còn tồn hoặc đã có phiếu. Nguyên liệu ngừng sử
  dụng không được nhập tăng thêm. Nhà cung cấp đã có phiếu không được xóa.

### Đối chiếu Oracle trước khi chạy với schema có sẵn

Ứng dụng giữ cấu hình `ddl-auto=update` của Hoang. Không tự chạy các script SQL
đã bổ sung. Mapping mới dùng 4 bảng `NHA_CUNG_CAP`, `NGUYEN_LIEU`, `PHIEU_NHAP`,
`CT_PHIEU_NHAP` và 3 sequence `SEQ_NHA_CUNG_CAP`, `SEQ_NGUYEN_LIEU`, `SEQ_PHIEU_NHAP`
với bước tăng 1. Nếu bảng đã có dữ liệu, sequence phải cấp mã lớn hơn mã lớn nhất
hiện tại; không tạo lại bảng hoặc reset sequence về 1.

`PHIEU_NHAP.PHIEN_BAN` là cột bổ sung ngoài sơ đồ ban đầu, phục vụ `@Version`.
Lệnh nâng cấp schema và các bước kiểm tra dữ liệu cũ nằm trong
[hướng dẫn cài đặt và dữ liệu Oracle](database/oracle/README.md).

`database/oracle/01_inventory.sql` từ DatNew dùng cho schema chưa có 4 bảng kho,
có tham chiếu `NHAN_VIEN` đã tồn tại; không chạy trên schema đã có các bảng này.
`02_routines.sql` chứa 2 procedure và trigger tính thành tiền. Java đã cập nhật
tồn kho cho các thao tác web; không cài thêm trigger cộng tồn hoặc gọi procedure
cộng tồn lần nữa sau khi Java lưu phiếu. Các procedure không tự commit.

### Kết quả kiểm tra

- Biên dịch Maven thành công với Java 27.
- 21 kiểm tra tích hợp từ DatNew chạy thành công với H2 trong bộ nhớ, trên code
  và giao diện đã ghép; gồm CRUD HTTP, tồn kho, làm tròn, rollback, phiên bản,
  nhập đồng thời, kiểm tra dữ liệu và ràng buộc danh mục.
- Kiểm tra bổ sung các trang `/`, `/mon-an`, `/loai-mon`, `/nhan-vien`, `/hoa-don`,
  `/hoa-don/them`, `/bao-cao` hiển thị thành công và có liên kết đến kho.
- Kiểm tra tạm nằm trong `target`, đã dọn sau khi chạy. `src/test` không thêm file.
  Cấu hình kiểm tra không tải class khởi động của Hoang để tránh tự mở trình duyệt.
- Chưa kiểm thử trên Oracle thực tế; không thực thi SQL hoặc thay đổi dữ liệu Oracle.

---
