# TRANG BÌA



---

# MỤC LỤC



---

# CHƯƠNG 1. KHẢO SÁT BÀI TOÁN VÀ PHÂN TÍCH YÊU CẦU

1.1. Mô tả bài toán

Nhà hàng hải sản cần quản lý đồng thời thực đơn, bàn ăn, khách đặt trước, nhân viên phục vụ, hóa đơn bán hàng và nguyên liệu nhập từ nhà cung cấp. Các thông tin này có liên quan trực tiếp với nhau: mỗi món thuộc một loại món, mỗi hóa đơn gắn với bàn và nhân viên, còn mỗi phiếu nhập gồm nhiều nguyên liệu. Nếu ghi chép rời rạc, nhà hàng khó tra cứu thông tin, đối chiếu chứng từ và tổng hợp hoạt động.

Bài toán đặt ra là xây dựng một ứng dụng quản lý tập trung, có giao diện tiếng Việt, hỗ trợ tra cứu danh mục và tổ chức dữ liệu theo mô hình quan hệ. Phần mềm cần giúp người quản lý theo dõi thông tin nhất quán, giảm việc nhập lặp và tạo cơ sở để mở rộng các nghiệp vụ bán hàng, đặt bàn và nhập kho.

1.2. Phạm vi khảo sát và mục tiêu

Đề tài khảo sát mô hình nghiệp vụ của một nhà hàng hải sản ở quy mô bài tập lớn. Nội dung khảo sát được xây dựng từ yêu cầu đề tài và đối chiếu với dự án QuanLyNHHS; chưa có số liệu khảo sát thực địa hoặc biên bản phỏng vấn tại một nhà hàng cụ thể.

Mục tiêu phân tích là xác định đối tượng quản lý, chức năng và dữ liệu vào/ra. Mục tiêu thiết kế là xây dựng lược đồ 12 bảng, mô tả thuộc tính, khóa chính, khóa ngoại và các quy tắc dữ liệu. Mục tiêu xây dựng chương trình là tổ chức ứng dụng Java Spring Boot, khai báo ánh xạ Oracle và minh họa các giao diện quản lý cơ bản.

Phiên bản hiện có tập trung vào trang chủ, loại món, món ăn và nhân viên. Ba nhóm danh mục đã có entity và repository, nhưng trang danh sách vẫn lấy dữ liệu mẫu từ controller. Các nghiệp vụ đặt bàn, hóa đơn và nhập kho được trình bày ở mức yêu cầu và thiết kế để phát triển tiếp.

Phạm vi hệ thống là một nhà hàng. Nội dung báo cáo phân biệt kết quả đã có trong mã nguồn với nghiệp vụ dự kiến; không suy ra rằng một chức năng đã hoàn thành chỉ vì có tên lớp hoặc nút thao tác.

---

# 1.3. Đối tượng quản lý và người sử dụng

Bảng 1.1. Các đối tượng quản lý
Nhóm | Đối tượng và thông tin cần quản lý
Khách hàng và bàn ăn | Khách hàng: họ tên, liên hệ, điểm tích lũy. Bàn ăn: tên bàn, sức chứa, khu vực và trạng thái. Đặt bàn: khách, bàn, thời điểm đến, số người, tiền cọc.
Thực đơn | Loại món: tên và mô tả nhóm món. Món ăn: tên, loại, đơn vị bán, đơn giá và trạng thái phục vụ.
Nhân sự | Nhân viên: họ tên, giới tính, số điện thoại, chức vụ, lương và ngày vào làm.
Bán hàng | Hóa đơn: bàn, nhân viên, khách hàng, thời điểm lập, giảm giá, tổng tiền và trạng thái. Chi tiết hóa đơn: món, số lượng và giá tại thời điểm bán.
Nhập hàng và nguyên liệu | Nhà cung cấp: tên, địa chỉ, liên hệ. Nguyên liệu: tên, đơn vị và tồn. Phiếu nhập và chi tiết phiếu nhập: người lập, nhà cung cấp, thời gian, lượng và giá nhập.

1.3.1. Nhóm người sử dụng theo yêu cầu

Bảng 1.2. Vai trò nghiệp vụ dự kiến
Người sử dụng | Nhiệm vụ
Quản lý | Quản lý thực đơn, nhân viên và danh mục; theo dõi tình hình kinh doanh.
Nhân viên phục vụ | Tra cứu món, tiếp nhận đặt bàn và ghi nhận yêu cầu gọi món.
Thu ngân | Lập, kiểm tra và thanh toán hóa đơn.
Thủ kho | Theo dõi nhà cung cấp, nguyên liệu và phiếu nhập.

Đây là các vai trò nghiệp vụ để phân tích yêu cầu. Mã nguồn hiện chưa có chức năng đăng nhập và phân quyền web hoàn chỉnh. Khách hàng là đối tượng được quản lý, chưa có giao diện tài khoản để tự đặt bàn trên website.

---

# 1.4. Yêu cầu chức năng

Bảng 1.3. Chức năng cần có và phạm vi hiện tại
Mã | Yêu cầu | Hiện trạng mã nguồn
CN01 | Quản lý loại món: xem danh sách, tên, mô tả và trạng thái; thêm, sửa, xóa khi hợp lệ. | Có trang danh sách mẫu và biểu mẫu; chưa có thao tác ghi Oracle.
CN02 | Quản lý món ăn: xem giá, đơn vị, loại và trạng thái; tra cứu, lọc, thêm, sửa, xóa. | Có 8 món mẫu, tìm kiếm, lọc loại và đổi cách hiển thị; thao tác ghi đang mô phỏng.
CN03 | Quản lý hồ sơ nhân viên; tìm kiếm và lọc theo chức vụ. | Có 8 hồ sơ mẫu và mã xử lý tìm kiếm, lọc.
CN04 | Quản lý khách hàng, bàn ăn và thông tin đặt bàn. | Mức thiết kế; chưa có luồng xử lý hoàn chỉnh.
CN05 | Lập hóa đơn, ghi món, tính thành tiền, giảm giá và thanh toán. | Mức thiết kế.
CN06 | Quản lý nhà cung cấp, nguyên liệu, lập phiếu nhập và tăng tồn. | Mức thiết kế.
CN07 | Tổng hợp doanh thu từ các hóa đơn đã thanh toán. | Mức thiết kế.

1.4.1. Quy trình nghiệp vụ chính

Quy trình phục vụ dự kiến: tiếp nhận khách hoặc đặt bàn → bố trí bàn phù hợp → mở hóa đơn → ghi món và số lượng → tính tiền → xác nhận thanh toán. Với khách vãng lai, hóa đơn có thể không gắn hồ sơ khách hàng. Giá bán cần được lưu trên chi tiết để giữ nguyên thông tin hóa đơn khi thực đơn thay đổi.

Quy trình nhập hàng dự kiến: chọn nhà cung cấp và người lập → nhập nguyên liệu, số lượng, đơn giá → tính tổng phiếu → lưu chứng từ và tăng tồn trong cùng giao dịch. Khi có lỗi, các thay đổi liên quan phải được hoàn tác để tránh tồn kho và chứng từ không khớp.

---

# 1.5. Dữ liệu vào, dữ liệu ra và yêu cầu xử lý

Bảng 1.4. Dữ liệu vào/ra theo nghiệp vụ
Nghiệp vụ | Dữ liệu vào | Dữ liệu ra
Loại món, món ăn | Tên, loại, mô tả, đơn vị, giá, trạng thái; từ khóa tra cứu. | Danh sách loại/món, thông tin giá và trạng thái; kết quả tìm kiếm.
Nhân viên | Thông tin hồ sơ, chức vụ, lương, ngày vào làm; tiêu chí lọc. | Danh sách và hồ sơ nhân viên theo điều kiện.
Đặt bàn (thiết kế) | Khách hàng, bàn, ngày giờ đến, số khách, tiền cọc. | Phiếu đặt bàn và trạng thái đặt.
Bán hàng (thiết kế) | Bàn, người lập, khách tùy chọn, món, số lượng, giá bán, số tiền giảm. | Chi tiết hóa đơn, tổng phải trả, trạng thái thanh toán.
Nhập kho (thiết kế) | Nhà cung cấp, người lập, nguyên liệu, số lượng và giá nhập. | Phiếu nhập, tổng giá trị và tồn sau nhập.
Doanh thu (thiết kế) | Khoảng thời gian và hóa đơn đã thanh toán. | Doanh thu tổng hợp theo kỳ tra cứu.

1.5.1. Yêu cầu hợp lệ dữ liệu

Tên danh mục và các trường bắt buộc không được bỏ trống. Số lượng giao dịch phải lớn hơn 0; giá, tiền cọc và tồn kho không âm. Mã loại món, bàn, nhân viên, nhà cung cấp và nguyên liệu được tham chiếu phải tồn tại. Số người đặt cần phù hợp sức chứa bàn; số tiền giảm không vượt tổng giá trị hóa đơn.

1.5.2. Yêu cầu về giao diện và tổ chức chương trình

Giao diện dùng tiếng Việt, thống nhất thanh điều hướng, tên cột và cách định dạng tiền. Khi hoàn thiện thao tác ghi, hệ thống cần thông báo lỗi rõ ràng và chỉ báo thành công sau khi lưu được dữ liệu. Mã nguồn được chia theo trách nhiệm để thuận tiện bảo trì; cấu hình kết nối được tách khỏi xử lý nghiệp vụ.

Các yêu cầu trên là tiêu chí xây dựng và hoàn thiện hệ thống. Dữ liệu đầu ra trên các trang hiện tại chủ yếu là dữ liệu mẫu; chưa có kết quả thực nghiệm xác nhận nghiệp vụ thanh toán, nhập kho hoặc báo cáo doanh thu.

---

# CHƯƠNG 2. THIẾT KẾ CƠ SỞ DỮ LIỆU VÀ XÂY DỰNG CHƯƠNG TRÌNH

2.1. Lược đồ cơ sở dữ liệu

Hình 2.1. Lược đồ quan hệ của 12 bảng trong thiết kế.

Cơ sở dữ liệu gồm các nhóm khách hàng – bàn ăn – đặt bàn; loại món – món ăn; nhân viên – hóa đơn – chi tiết hóa đơn; nhà cung cấp – phiếu nhập – chi tiết phiếu nhập – nguyên liệu. PK là khóa chính; FK là khóa ngoại. Mũi tên trên hình biểu diễn quan hệ từ bảng cha đến bảng con.

Lược đồ thể hiện thiết kế toàn hệ thống. Mã nguồn hiện có ánh xạ JPA cho LOAI_MON, MON_AN và NHAN_VIEN; chín bảng còn lại chưa có entity hoàn chỉnh.

---

# 2.2. Khóa chính, khóa ngoại và các quan hệ

Bảng 2.1. Các quan hệ theo khóa ngoại
Bảng được tham chiếu | Bảng chứa khóa ngoại | Cột khóa ngoại
KHACH_HANG | DAT_BAN | MA_KH
BAN_AN | DAT_BAN | MA_BAN
BAN_AN | HOA_DON | MA_BAN
NHAN_VIEN | HOA_DON | MA_NV
KHACH_HANG | HOA_DON | MA_KH (cho phép NULL)
LOAI_MON | MON_AN | MA_LOAI
HOA_DON | CT_HOA_DON | MA_HD
MON_AN | CT_HOA_DON | MA_MON
NHA_CUNG_CAP | PHIEU_NHAP | MA_NCC
NHAN_VIEN | PHIEU_NHAP | MA_NV
PHIEU_NHAP | CT_PHIEU_NHAP | MA_PN
NGUYEN_LIEU | CT_PHIEU_NHAP | MA_NL

2.2.1. Cách xác định khóa

Mười bảng danh mục và phần đầu chứng từ dùng khóa chính đơn là mã định danh. CT_HOA_DON dùng khóa ghép (MA_HD, MA_MON), bảo đảm một món chỉ có một dòng trong một hóa đơn. CT_PHIEU_NHAP dùng khóa ghép (MA_PN, MA_NL), bảo đảm một nguyên liệu chỉ có một dòng trong một phiếu nhập. Khi phát sinh thêm cùng món hoặc nguyên liệu trong cùng chứng từ, cần cộng dồn số lượng theo quy tắc đã chọn.

2.2.2. Ý nghĩa các quan hệ

Một loại món có thể có nhiều món ăn. Một khách hàng có thể có nhiều lần đặt bàn hoặc nhiều hóa đơn; một bàn và một nhân viên có thể gắn với nhiều hóa đơn theo thời gian. Mỗi hóa đơn có nhiều dòng món và mỗi phiếu nhập có nhiều dòng nguyên liệu. Khóa ngoại buộc bản ghi con tham chiếu bản ghi cha hợp lệ.

Việc tách danh mục khỏi chứng từ giảm lặp thông tin. Đơn giá giao dịch được lưu tại bảng chi tiết để bảo toàn lịch sử; các cột tổng tiền là dữ liệu tính toán cần được đồng bộ với chi tiết. Khi xóa danh mục đã được chứng từ sử dụng, nên từ chối xóa hoặc chuyển trạng thái ngừng sử dụng để giữ lịch sử.

---

# 2.3. Mô tả các bảng dữ liệu

Các bảng dưới đây mô tả cấu trúc logic bằng kiểu dữ liệu Oracle. Các ràng buộc nghiệp vụ nêu trong diễn giải cần được cài bằng CHECK, khóa ngoại hoặc tầng xử lý; mô tả thiết kế không đồng nghĩa toàn bộ ràng buộc đã tồn tại trong cơ sở dữ liệu.

2.3.1. Khách hàng và đặt bàn

KHACH_HANG – Lưu thông tin khách hàng và điểm tích lũy.

Bảng 2.2. Cấu trúc KHACH_HANG
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_KH | NUMBER | PK; mã khách hàng
HO_TEN | VARCHAR2(100) | Họ tên khách hàng
SDT | VARCHAR2(15) | Số điện thoại
EMAIL | VARCHAR2(100) | Địa chỉ email
DIEM_TICH_LUY | NUMBER(10,0) | Điểm tích lũy, không âm

Khóa chính: MA_KH. Không bắt buộc tạo hồ sơ cho khách vãng lai; khi đó MA_KH trên hóa đơn có thể để trống.

Mức triển khai: Thiết kế; chưa có entity hoàn chỉnh.

DAT_BAN – Ghi nhận yêu cầu đặt bàn của khách hàng.

Bảng 2.3. Cấu trúc DAT_BAN
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_DAT | NUMBER | PK; mã đặt bàn
MA_KH | NUMBER | FK → KHACH_HANG.MA_KH
MA_BAN | NUMBER | FK → BAN_AN.MA_BAN
NGAY_DAT | DATE | Ngày và giờ khách dự kiến đến
SO_NGUOI | NUMBER(3) | Số khách, lớn hơn 0
TIEN_COC | NUMBER(12,2) | Tiền đặt cọc, không âm
TRANG_THAI | VARCHAR2(20) | Trạng thái đặt bàn

Khóa chính: MA_DAT. SO_NGUOI không vượt sức chứa bàn được chọn. Chưa có thời điểm kết thúc nên mô hình chưa đủ để tự kiểm tra trùng khoảng thời gian.

Mức triển khai: Thiết kế; controller/service còn trống.

---

# 2.3.2. Bàn ăn và hóa đơn

BAN_AN – Lưu bàn phục vụ và trạng thái sử dụng.

Bảng 2.4. Cấu trúc BAN_AN
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_BAN | NUMBER | PK; mã bàn
TEN_BAN | VARCHAR2(50) | Tên hoặc số hiệu bàn
SO_CHO | NUMBER(3) | Sức chứa, lớn hơn 0
KHU_VUC | VARCHAR2(50) | Khu vực bố trí
TRANG_THAI | VARCHAR2(20) | Trống / đặt trước / đang dùng

Khóa chính: MA_BAN. Bàn có thể phát sinh nhiều hóa đơn theo thời gian; tại một thời điểm chỉ có một hóa đơn đang phục vụ theo quy ước nghiệp vụ.

Mức triển khai: Thiết kế; chưa có entity hoàn chỉnh.

HOA_DON – Lưu chứng từ bán hàng và thông tin thanh toán.

Bảng 2.5. Cấu trúc HOA_DON
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_HD | NUMBER | PK; mã hóa đơn
MA_BAN | NUMBER | FK → BAN_AN.MA_BAN
MA_NV | NUMBER | FK → NHAN_VIEN.MA_NV
MA_KH | NUMBER | FK → KHACH_HANG.MA_KH; có thể NULL
NGAY_LAP | DATE | Ngày giờ lập hóa đơn
GIAM_GIA | NUMBER(12,2) | Số tiền giảm trực tiếp
TONG_TIEN | NUMBER(14,2) | Số tiền sau giảm giá
TRANG_THAI | VARCHAR2(20) | MO / DA_THANH_TOAN / HUY

Khóa chính: MA_HD. GIAM_GIA được thống nhất là số tiền, không phải phần trăm. Tiền cọc của đặt bàn chưa được liên kết trực tiếp với hóa đơn trong 12 bảng.

Mức triển khai: Thiết kế; chưa có nghiệp vụ hóa đơn.

---

# 2.3.3. Loại món và món ăn

LOAI_MON – Phân nhóm các món ăn trong thực đơn.

Bảng 2.6. Cấu trúc LOAI_MON
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_LOAI | NUMBER | PK; SEQ_LOAI_MON cấp mã trong entity
TEN_LOAI | VARCHAR2(100) | Tên loại; entity quy định NOT NULL
MO_TA | VARCHAR2(255) | Mô tả loại món
TRANG_THAI | VARCHAR2(20) | Hoạt động / tạm dừng

Khóa chính: MA_LOAI. soLuongMon là @Transient; không phải cột lưu trong Oracle và chưa được tính từ truy vấn cơ sở dữ liệu.

Mức triển khai: Đã có entity, repository và trang danh sách dùng dữ liệu mẫu.

MON_AN – Lưu thông tin món ăn, nhóm món và giá bán.

Bảng 2.7. Cấu trúc MON_AN
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_MON | NUMBER | PK; SEQ_MON_AN cấp mã trong entity
MA_LOAI | NUMBER | FK → LOAI_MON.MA_LOAI; NOT NULL
TEN_MON | VARCHAR2(150) | Tên món; entity quy định NOT NULL
DON_VI | VARCHAR2(20) | Đơn vị bán: kg, đĩa, con…
DON_GIA | NUMBER(12,2) | Giá bán; Java dùng BigDecimal
TRANG_THAI | VARCHAR2(20) | Đang phục vụ / tạm hết

Khóa chính: MA_MON. hinhAnh, moTa, soLuongDaBan đều @Transient. Không thể xem các giá trị này là dữ liệu đã được lưu xuống Oracle.

Mức triển khai: Đã có entity, repository và giao diện danh sách mẫu.

---

# 2.3.4. Chi tiết hóa đơn và nhân viên

CT_HOA_DON – Lưu từng món và giá bán tại thời điểm lập hóa đơn.

Bảng 2.8. Cấu trúc CT_HOA_DON
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_HD | NUMBER | PK thành phần; FK → HOA_DON.MA_HD
MA_MON | NUMBER | PK thành phần; FK → MON_AN.MA_MON
SO_LUONG | NUMBER(10,2) | Số lượng bán, lớn hơn 0
DON_GIA | NUMBER(12,2) | Giá tại thời điểm bán, không âm
THANH_TIEN | NUMBER(14,2) | SO_LUONG × DON_GIA

Khóa chính: (MA_HD, MA_MON). Một món chỉ xuất hiện một dòng trong một hóa đơn. Gọi thêm cùng món thì cộng dồn số lượng; không tách nhiều mức giá trong cùng hóa đơn.

Mức triển khai: Thiết kế; file model/repository còn khung.

NHAN_VIEN – Lưu hồ sơ nhân viên tham gia hoạt động nhà hàng.

Bảng 2.9. Cấu trúc NHAN_VIEN
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_NV | NUMBER | PK; SEQ_NHAN_VIEN cấp mã trong entity
HO_TEN | VARCHAR2(100) | Họ tên; entity quy định NOT NULL
GIOI_TINH | VARCHAR2(10) | Giới tính
SDT | VARCHAR2(15) | Số điện thoại
CHUC_VU | VARCHAR2(50) | Chức vụ công việc
LUONG | NUMBER(12,2) | Mức lương; Java dùng BigDecimal
NGAY_VAO_LAM | DATE | Ngày vào làm; Java dùng LocalDate

Khóa chính: MA_NV. email, caLamViec, trangThai là @Transient. CHUC_VU là dữ liệu nghiệp vụ, chưa phải vai trò đăng nhập hay Oracle role.

Mức triển khai: Đã có entity, repository và danh sách nhân viên mẫu.

---

# 2.3.5. Nhà cung cấp và phiếu nhập

NHA_CUNG_CAP – Lưu các đơn vị cung cấp hải sản và nguyên liệu.

Bảng 2.10. Cấu trúc NHA_CUNG_CAP
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_NCC | NUMBER | PK; mã nhà cung cấp
TEN_NCC | VARCHAR2(150) | Tên nhà cung cấp
SDT | VARCHAR2(15) | Số điện thoại liên hệ
DIA_CHI | VARCHAR2(200) | Địa chỉ
EMAIL | VARCHAR2(100) | Email liên hệ

Khóa chính: MA_NCC. Một nhà cung cấp có nhiều phiếu nhập. Thông tin nhà cung cấp được quản lý tập trung, không lặp lại ở mọi phiếu nhập.

Mức triển khai: Thiết kế; chưa có entity hoàn chỉnh.

PHIEU_NHAP – Lưu phần đầu chứng từ nhập nguyên liệu.

Bảng 2.11. Cấu trúc PHIEU_NHAP
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_PN | NUMBER | PK; mã phiếu nhập
MA_NCC | NUMBER | FK → NHA_CUNG_CAP.MA_NCC
MA_NV | NUMBER | FK → NHAN_VIEN.MA_NV
NGAY_NHAP | DATE | Ngày giờ nhập hàng
TONG_TIEN | NUMBER(14,2) | Tổng giá trị chi tiết phiếu

Khóa chính: MA_PN. Mô hình chưa có cột trạng thái duyệt phiếu; trong phạm vi bài tập có thể quy ước phiếu đã lưu là phiếu được ghi nhận nhập.

Mức triển khai: Thiết kế; các lớp chức năng đang để trống.

---

# 2.3.6. Chi tiết phiếu nhập và nguyên liệu

CT_PHIEU_NHAP – Lưu lượng và giá nhập của từng nguyên liệu.

Bảng 2.12. Cấu trúc CT_PHIEU_NHAP
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_PN | NUMBER | PK thành phần; FK → PHIEU_NHAP.MA_PN
MA_NL | NUMBER | PK thành phần; FK → NGUYEN_LIEU.MA_NL
SO_LUONG | NUMBER(10,2) | Số lượng nhập, lớn hơn 0
DON_GIA | NUMBER(12,2) | Đơn giá nhập, không âm
THANH_TIEN | NUMBER(14,2) | SO_LUONG × DON_GIA

Khóa chính: (MA_PN, MA_NL). Mỗi nguyên liệu xuất hiện một dòng trong một phiếu; không mô hình hóa nhiều lô có nhiều đơn giá trong cùng phiếu.

Mức triển khai: Thiết kế; CTPhieuNhap mới là lớp rỗng.

NGUYEN_LIEU – Lưu danh mục nguyên liệu và số lượng tồn.

Bảng 2.13. Cấu trúc NGUYEN_LIEU
Cột | Kiểu Oracle | Ý nghĩa / khóa
MA_NL | NUMBER | PK; mã nguyên liệu
TEN_NL | VARCHAR2(100) | Tên nguyên liệu
DON_VI | VARCHAR2(20) | Đơn vị tồn: kg, lít, con…
SO_LUONG_TON | NUMBER(10,2) | Số lượng tồn, không âm
TRANG_THAI | VARCHAR2(20) | Trạng thái sử dụng

Khóa chính: MA_NL. Chưa có bảng định lượng MON_AN–NGUYEN_LIEU, phiếu xuất hoặc kiểm kê; chưa đủ dữ liệu tự trừ tồn khi bán món hay tính lợi nhuận chính xác.

Mức triển khai: Thiết kế; chưa có entity hoàn chỉnh.

---

# 2.4. Ràng buộc dữ liệu và quy tắc tính toán

2.4.1. Ràng buộc toàn vẹn

Khóa chính không được trống và không được trùng. Khóa ngoại phải tham chiếu mã đã tồn tại, trừ MA_KH trên hóa đơn được phép trống khi phục vụ khách vãng lai. Với ba entity đã có, TEN_LOAI, TEN_MON và HO_TEN được khai báo nullable=false; MA_LOAI của món ăn cũng bắt buộc. Các kiểm tra không âm và kiểm tra liên bảng cần bổ sung khi cài đặt nghiệp vụ.

Bảng 2.14. Quy tắc tính giá trị chứng từ theo thiết kế
Giá trị | Cách tính / điều kiện
Thành tiền một dòng | THANH_TIEN = SO_LUONG × DON_GIA. Số lượng > 0, đơn giá ≥ 0.
Tổng hóa đơn | TONG_TIEN = tổng thành tiền các dòng − GIAM_GIA. GIAM_GIA là số tiền giảm trực tiếp và không vượt tổng trước giảm.
Tổng phiếu nhập | TONG_TIEN = tổng thành tiền của các dòng chi tiết phiếu nhập.
Tồn sau nhập | Tồn mới = tồn trước + số lượng nhập; cập nhật cùng giao dịch với việc lưu phiếu.
Doanh thu kỳ | Cộng tổng tiền các hóa đơn đã thanh toán trong kỳ; loại hóa đơn đang mở hoặc đã hủy.

2.4.2. Ví dụ tính hóa đơn

Một hóa đơn có 2 đĩa món giá 240.000 đồng và 1 đĩa món giá 290.000 đồng. Tổng trước giảm là 2 × 240.000 + 1 × 290.000 = 770.000 đồng. Nếu giảm trực tiếp 50.000 đồng, tổng phải trả là 720.000 đồng. Đây là ví dụ giải thích thiết kế, chưa phải hóa đơn được chương trình xử lý thực tế.

2.4.3. Giới hạn của mô hình hiện tại

DAT_BAN mới lưu thời điểm đến, chưa có thời điểm kết thúc nên chưa đủ dữ liệu kiểm tra trùng khoảng đặt. Tiền cọc chưa liên kết trực tiếp với hóa đơn để tự khấu trừ. Mô hình chưa có định lượng món – nguyên liệu, phiếu xuất hoặc kiểm kê nên chưa tự trừ tồn khi bán và chưa đủ cơ sở tính giá vốn, lợi nhuận chính xác.

---

# 2.5. Cấu trúc project và công nghệ sử dụng

QuanLyNHHS/
  pom.xml
  src/main/java/com/QuanLyNHHS/
    QuanLyNhhsApplication.java
    controller/
    model/
    repository/
    service/
    config/
  src/main/resources/
    application.properties
    oracle_setup_user.sql
    templates/
      index.html
      loaimon/index.html
      monan/index.html
      nhanvien/index.html
      layout/sidebar.html, navbar.html
    static/css/style.css
    static/js/main.js

Bảng 2.15. Trách nhiệm các thành phần
Thành phần | Vai trò trong chương trình
controller | Nhận yêu cầu HTTP, chuẩn bị Model và trả tên trang Thymeleaf; hiện các trang danh mục dùng danh sách mẫu.
model | Ánh xạ bảng, cột và quan hệ JPA; có ba entity hoàn chỉnh là LoaiMon, MonAn, NhanVien.
repository | Khai báo ba interface JpaRepository; chưa được gọi trong luồng danh sách hiện tại.
service, config | Đã tạo các file khung; Service và SecurityConfig chưa có xử lý hoàn chỉnh.
templates, static | Giao diện HTML/Thymeleaf, CSS và JavaScript tương tác.

Theo pom.xml, dự án khai báo Java 27, Spring Boot 4.1.1, Spring MVC, Thymeleaf, Spring Data JPA và Oracle JDBC. Maven quản lý thư viện và đóng gói. Các phiên bản được ghi theo cấu hình dự án, chưa phải kết luận đã kiểm thử tương thích hoặc khởi chạy thành công.

---

# 2.6. Kết nối cơ sở dữ liệu và luồng xử lý

Hình 2.2. Các tầng của ứng dụng và mức độ triển khai.

2.6.1. Cấu hình Oracle

application.properties cho phép thiết lập DB_URL, DB_USERNAME và DB_PASSWORD qua biến môi trường. Đích kết nối mặc định là Oracle PDB FREEPDB1 trên localhost:1521; tài khoản mặc định là NHHS_APP. Báo cáo không chép mật khẩu mặc định. Script oracle_setup_user.sql hỗ trợ chuẩn bị tài khoản và role ở mức cơ sở dữ liệu.

Hibernate được cấu hình ddl-auto=update để tạo hoặc cập nhật cấu trúc tương ứng với các entity đã khai báo. Vì hiện chỉ có ba entity hoàn chỉnh, không thể xem cấu hình này là bằng chứng đã tạo đầy đủ lược đồ 12 bảng. Quyền Oracle cũng không thay thế chức năng phân quyền người dùng web.

2.6.2. Luồng xử lý hiện có

Trình duyệt gửi GET /mon-an → MonAnController tạo danh sách món và loại món mẫu → đưa dữ liệu vào Model → trả monan/index → Thymeleaf sinh HTML → JavaScript xử lý tìm kiếm, lọc và đổi kiểu hiển thị trên dữ liệu của trang. Luồng hiện tại chưa đọc danh sách từ Oracle qua repository.

Khi hoàn thiện, Controller sẽ gọi Service để kiểm tra nghiệp vụ, Service sử dụng Repository để truy cập Oracle thông qua JPA/Hibernate. Những thao tác liên quan nhiều bảng như lưu hóa đơn và phiếu nhập cần được đặt trong một giao dịch.

---

# 2.7. Giao diện chương trình

Bảng 2.16. Các trang đã có controller và template
Đường dẫn | Nội dung giao diện
/ | Trang chủ; dùng bố cục điều hướng chung và liên kết các nhóm chức năng.
/loai-mon | Danh sách 6 loại món mẫu, mô tả, số món và trạng thái.
/mon-an | Danh sách 8 món mẫu: 7 đang phục vụ, 1 tạm hết; có kiểu thẻ và kiểu bảng.
/nhan-vien | Danh sách 8 nhân viên mẫu; hỗ trợ tra cứu và lọc theo chức vụ.

2.7.1. Bố cục chung

Các trang dùng thanh điều hướng bên trái và thanh thông tin phía trên thông qua fragment sidebar và navbar. Vùng nội dung chính đặt tiêu đề, thẻ thống kê, thanh tìm kiếm hoặc bộ lọc, danh sách dữ liệu và nút mở biểu mẫu. Các mục đặt bàn, hóa đơn, kho trong menu đang ở trạng thái phát triển.

2.7.2. Trang loại món và món ăn

Trang loại món trình bày thông tin nhóm món, mô tả và trạng thái. Số lượng món hiển thị là dữ liệu mẫu, không được tính từ truy vấn Oracle. Trang món ăn có nút lọc loại, ô tìm theo tên/mã, nút đổi dạng thẻ hoặc bảng; thông tin món gồm ảnh, tên, loại, đơn vị bán, giá và trạng thái.

Ảnh, mô tả và số lượng đã bán trên giao diện món ăn là thuộc tính phụ trợ @Transient. Các giá trị này không được lưu thành cột trong entity hiện tại. Khi chuyển sang dữ liệu thật, cần bổ sung lưu trữ hoặc nguồn tính toán nếu muốn giữ chúng.

2.7.3. Trang nhân viên và biểu mẫu

Trang nhân viên trình bày hồ sơ, chức vụ, ngày vào làm, lương và trạng thái mẫu. Các trang có hộp thoại thêm/sửa/xóa và thông báo phản hồi. Tuy nhiên, mã xử lý biểu mẫu hiện chặn gửi bằng preventDefault(), đóng hộp thoại và hiện thông báo; chưa tạo, cập nhật hoặc xóa bản ghi trong Oracle.

---

# 2.8. Các chức năng chính và mức độ thực hiện

Bảng 2.17. Đối chiếu chức năng với mã nguồn
Chức năng | Cách thực hiện hiện tại
Xem danh sách | Controller tạo ArrayList mẫu; Model chuyển dữ liệu sang template để hiển thị.
Thống kê trên danh mục | Tính từ danh sách mẫu hoặc lấy số liệu mẫu có sẵn; chưa phải thống kê giao dịch thực tế.
Tìm kiếm món ăn | JavaScript so khớp từ khóa với tên/mã hoặc nội dung dòng bảng rồi ẩn/hiện phần tử.
Lọc món theo loại | Đọc data-category để so sánh loại được chọn; áp dụng trên các món đã tải.
Đổi cách xem món | Bật/tắt vùng lưới thẻ và vùng bảng, giữ cùng nguồn dữ liệu hiển thị.
Tra cứu nhân viên | Có mã JavaScript tìm theo nội dung dòng và lọc chức vụ.
Thêm, sửa, xóa | Giao diện mô phỏng; chưa có endpoint ghi dữ liệu và tầng Service xử lý.
Đặt bàn, hóa đơn, nhập kho | Mới có thiết kế dữ liệu và file khung; chưa có nghiệp vụ hoàn chỉnh.

2.8.1. Lưu ý về kết quả hiển thị

Dữ liệu do controller khởi tạo lại khi tải trang không thể thay thế dữ liệu lưu bền vững. Thông báo thành công trên trình duyệt chưa phản ánh kết quả ghi cơ sở dữ liệu. Ngoài ra, tìm kiếm và lọc loại món hiện xử lý ẩn/hiện riêng; cần hợp nhất điều kiện khi muốn dùng đồng thời nhiều tiêu chí.

2.8.2. Cơ sở đánh giá

Mức độ thực hiện được xác định từ các file controller, entity, repository, template và main.js. Báo cáo chưa có biên bản chạy ứng dụng, kiểm thử trình duyệt hoặc xác nhận kết nối Oracle cho phiên bản hiện tại; vì vậy kết quả được giới hạn ở nội dung triển khai tìm thấy trong dự án.

---

# 2.9. Một số đoạn mã tiêu biểu

2.9.1. Ánh xạ entity và khóa chính

@Entity
@Table(name = "LOAI_MON")
public class LoaiMon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_loai_mon")
    @SequenceGenerator(name = "seq_loai_mon",
        sequenceName = "SEQ_LOAI_MON", allocationSize = 1)
    @Column(name = "MA_LOAI")
    private Long maLoai;

    @Column(name = "TEN_LOAI", length = 100, nullable = false)
    private String tenLoai;
    // Luoc bot cac thuoc tinh, constructor va getter/setter.
}

Đoạn trích từ LoaiMon.java cho thấy @Entity và @Table ánh xạ lớp đến bảng; @Id đánh dấu khóa chính. Sequence SEQ_LOAI_MON cấp mã theo cấu hình JPA. Tên loại là trường bắt buộc, tối đa 100 ký tự theo annotation.

2.9.2. Ánh xạ khóa ngoại và kiểu tiền

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "MA_LOAI", nullable = false,
    foreignKey = @ForeignKey(name = "FK_MON_AN_LOAI_MON"))
private LoaiMon loaiMon;

@Column(name = "DON_GIA", precision = 12, scale = 2)
private BigDecimal donGia;

Đoạn trích từ MonAn.java ánh xạ nhiều món đến một loại qua MA_LOAI. Đơn giá dùng BigDecimal và cột có precision=12, scale=2. Các trường đánh dấu @Transient chỉ phục vụ hiển thị, không được JPA lưu xuống bảng.

---

# 2.9.3. Repository, Controller và template

public interface MonAnRepository
        extends JpaRepository<MonAn, Long> {
}

MonAnRepository.java khai báo khả năng truy cập entity MonAn với khóa kiểu Long. Tuy nhiên, controller đang dùng dữ liệu mẫu, chưa gọi các phương thức truy cập dữ liệu của interface này.

Đoạn trích dưới đây lấy từ phương thức index() của MonAnController.java; các dòng tính thống kê và thêm thuộc tính khác đã được lược bớt để tập trung vào luồng dữ liệu:

@GetMapping
public String index(Model model) {
    List<MonAn> dishes = getSampleDishes();
    List<LoaiMon> categories = getSampleCategories();
    // Luoc bot cac dong thong ke va thuoc tinh Model khac.
    model.addAttribute("dishes", dishes);
    model.addAttribute("categories", categories);
    model.addAttribute("activeMenu", "monan");
    return "monan/index";
}

Controller tiếp nhận yêu cầu tại /mon-an, tạo hai danh sách mẫu và chuyển sang template qua Model. Giá trị trả về là tên template, không phải một lệnh truy vấn Oracle.

Trong templates/monan/index.html, phần tử sau được lặp cho từng món và mang mã loại để JavaScript lọc:

<div class="dish-card" th:each="dish : ${dishes}"
     th:data-category="${dish.maLoai}">
    <!-- Noi dung the mon an duoc luoc bot. -->
</div>

th:each tạo phần tử từ danh sách dishes. th:data-category đưa mã loại vào HTML. Cách tổ chức này tách dữ liệu chuẩn bị ở Controller khỏi phần trình bày, đồng thời cho phép JavaScript xử lý các phần tử đã được kết xuất.

---

# KẾT LUẬN

Kết quả thực hiện

Đề tài đã xác định được bài toán quản lý nhà hàng hải sản, các đối tượng quản lý, nhóm người sử dụng, yêu cầu chức năng và dữ liệu vào/ra. Thiết kế cơ sở dữ liệu gồm 12 bảng, có mô tả thuộc tính, khóa chính, khóa ngoại và các quy tắc tính toán cơ bản cho hóa đơn, phiếu nhập.

Về chương trình, dự án đã có cấu trúc ứng dụng Java Spring Boot, cấu hình Oracle, ba entity và ba repository cho loại món, món ăn, nhân viên. Các controller và template đã tổ chức trang chủ cùng các trang danh mục; JavaScript có mã tìm kiếm, lọc và đổi cách hiển thị món ăn. Đây là nền tảng ban đầu để tiếp tục kết nối giao diện với dữ liệu lưu trữ.

Hạn chế

Các trang danh sách còn dùng dữ liệu mẫu, biểu mẫu thêm/sửa/xóa mới mô phỏng tương tác. Service, đăng nhập và phân quyền web chưa hoàn chỉnh; đặt bàn, hóa đơn, nhập kho và báo cáo doanh thu chưa có luồng nghiệp vụ đầy đủ. Chưa có kết quả thực nghiệm xác nhận toàn bộ hệ thống làm việc với Oracle.

Thiết kế còn thiếu thời gian kết thúc đặt bàn, quan hệ khấu trừ tiền cọc và định lượng món – nguyên liệu. Một số thông tin giao diện là @Transient nên không được lưu vào cơ sở dữ liệu. Vì vậy sản phẩm hiện tại chưa đáp ứng đầy đủ yêu cầu vận hành thực tế của nhà hàng.

Hướng phát triển

Trước hết, cần hoàn thiện Service và các endpoint ghi dữ liệu, thay danh sách mẫu bằng truy vấn repository, kiểm tra dữ liệu nhập và xử lý lỗi lưu. Tiếp theo, bổ sung các entity còn thiếu, ràng buộc cơ sở dữ liệu và giao dịch cho hóa đơn, phiếu nhập; kiểm tra dữ liệu thực tế trên Oracle để xác nhận tính nhất quán.

Các bước tiếp theo gồm xây dựng xác thực và phân quyền web, bổ sung chống trùng lịch đặt bàn, liên kết tiền cọc với hóa đơn và quản lý định lượng nguyên liệu. Sau khi nghiệp vụ ổn định, có thể triển khai thống kê doanh thu, phân trang, xuất báo cáo và sao lưu dữ liệu. Việc kiểm thử cần gắn với từng chức năng hoàn thiện trước khi mở rộng phạm vi.