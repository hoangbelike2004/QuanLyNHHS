from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / 'so_do'
OUT.mkdir(exist_ok=True)
COMMON = '''@startuml
!pragma layout smetana
skinparam defaultFontName Arial
skinparam defaultFontSize 16
skinparam dpi 180
skinparam backgroundColor white
skinparam shadowing false
skinparam roundcorner 8
skinparam ArrowColor #37536B
skinparam classBackgroundColor #F0F6FA
skinparam classBorderColor #37536B
skinparam usecaseBackgroundColor #F0F6FA
skinparam packageBackgroundColor #FFFFFF
skinparam sequenceParticipantBackgroundColor #EDF4F8
skinparam sequenceLifeLineBorderColor #78909C
skinparam sequenceMessageAlign center
skinparam padding 5
hide circle
'''
diagrams = {}
diagrams['01_use_case'] = r'''left to right direction
actor "Nhân viên nội bộ" as NV
actor "Phục vụ" as PV
actor "Thu ngân" as TN
actor "Thủ kho" as TK
actor "Quản lý" as QL
PV --|> NV
TN --|> NV
TK --|> NV
QL --|> NV
rectangle "HỆ THỐNG QUẢN LÝ NHÀ HÀNG HẢI SẢN" {
 usecase "UC01\nĐăng nhập / đăng xuất" as U1
 usecase "UC02\nQuản lý thực đơn" as U2
 usecase "UC03\nTiếp nhận, xử lý đặt bàn" as U3
 usecase "UC04\nLập, điều chỉnh hóa đơn" as U4
 usecase "UC05\nGhi nhận thanh toán" as U5
 usecase "UC06\nLập, điều chỉnh phiếu nhập" as U6
 usecase "UC07\nXem báo cáo doanh thu" as U7
 usecase "UC08\nQuản lý hồ sơ nhân viên" as U8
 usecase "UC09\nQuản lý danh mục kho" as U9
 usecase "UC10\nQuản lý khách hàng, bàn ăn" as U10
}
NV -- U1
PV -- U3
PV -- U10
TN -- U4
TN -- U5
QL -- U2
QL -- U7
QL -- U8
TK -- U6
TK -- U9
note bottom of U1
Vai trò theo nghiệp vụ.
Code hiện tại dùng ROLE_USER chung;
chưa hạn chế URL theo từng vai trò.
end note
'''
diagrams['02_khai_niem_ban_hang'] = r'''left to right direction
class "Khách hàng" as KH {
 họ tên
 điện thoại
 điểm tích lũy
}
class "Lượt đặt bàn" as DB {
 ngày đặt
 số người
 tiền cọc
 trạng thái
}
class "Bàn ăn" as BA {
 tên bàn
 số chỗ
 khu vực
 trạng thái
}
class "Nhân viên" as NV {
 họ tên
 chức vụ
}
class "Hóa đơn" as HD {
 ngày lập
 giảm giá
 tổng tiền
 trạng thái
}
class "Dòng hóa đơn" as CT {
 số lượng
 đơn giá bán
 thành tiền
}
class "Món ăn" as MA {
 tên món
 đơn vị bán
 giá hiện tại
 trạng thái
}
class "Loại món" as LM {
 tên loại
 mô tả
}
KH "1" -- "0..*" DB : thực hiện
BA "1" -- "0..*" DB : được đặt
KH "0..1" -- "0..*" HD : nhận
BA "1" -- "0..*" HD : phục vụ tại
NV "1" -- "0..*" HD : lập
HD "1" *-- "1..*" CT : gồm
MA "1" -- "0..*" CT : được bán
LM "1" -- "0..*" MA : phân loại
note bottom of CT
Đơn giá tại lúc ghi chứng từ
khác giá hiện tại của món.
end note
'''
diagrams['03_khai_niem_nhap_kho'] = r'''left to right direction
class "Nhà cung cấp" as NCC {
 tên nhà cung cấp
 điện thoại
 địa chỉ
}
class "Nhân viên" as NV {
 họ tên
 chức vụ
}
class "Phiếu nhập" as PN {
 ngày nhập
 tổng tiền
}
class "Dòng phiếu nhập" as CT {
 số lượng nhập
 đơn giá nhập
 thành tiền
}
class "Nguyên liệu" as NL {
 tên nguyên liệu
 đơn vị
 số lượng tồn
 trạng thái
}
NCC "1" -- "0..*" PN : cung cấp
NV "1" -- "0..*" PN : lập
PN "1" *-- "1..*" CT : gồm
NL "1" -- "0..*" CT : được nhập
note bottom of PN
Mỗi phiếu thuộc một nhà cung cấp.
Mỗi nguyên liệu chỉ xuất hiện một lần/phiếu.
Sửa hoặc xóa phiếu phải điều chỉnh lại tồn.
end note
'''
diagrams['04_ssd_lap_hoa_don'] = r'''actor "Thu ngân" as A
participant ":Hệ thống" as S
A -> S : yêuCầuLậpHóaĐơn()
S --> A : danh sách bàn, nhân viên, khách, món
A -> S : lậpHóaĐơn(bàn, nhân viên, khách?,\ndanh sách món và lượng, giảm giá)
alt dữ liệu hợp lệ
 S --> A : mã hóa đơn, chi tiết, tổng tiền,\ntrạng thái chưa thanh toán
else dữ liệu không hợp lệ
 S --> A : thông báo lỗi; không tạo chứng từ dở dang
end
note over A,S
SSD chỉ mô tả sự kiện tại ranh giới hệ thống.
Giá bán và tổng tiền do hệ thống xác định.
end note
'''
diagrams['05_kien_truc'] = r'''top to bottom direction
rectangle "Trình duyệt\nHTML / CSS / JavaScript" as B
package "Ứng dụng Spring Boot" {
 rectangle "Spring Security\nĐăng nhập, phiên, CSRF" as SEC
 rectangle "Controller\nHTTP, Model, BindingResult" as C
 rectangle "DTO / Form\nDữ liệu được phép nhập" as D
 rectangle "Service\nQuy tắc nghiệp vụ, giao dịch" as S
 rectangle "Repository interface\nJPA hoặc JDBC ở module kho" as R
 rectangle "Entity / Model\nDữ liệu và ánh xạ lưu trữ" as E
 rectangle "Thymeleaf\nTạo HTML phía máy chủ" as V
}
database "Oracle Database\nDữ liệu bền vững" as DB
B --> SEC : HTTP
SEC --> C : đã xác thực
C --> D : nhận / kiểm tra
C --> S : gọi nghiệp vụ
S --> R : đọc / ghi
S --> E : tạo / cập nhật
R --> E : ánh xạ
R --> DB : SQL / JDBC
C --> V : Model + template
V --> B : HTML
'''
diagrams['06_sequence_lap_hoa_don'] = r'''autonumber
actor "Thu ngân" as A
boundary "HoaDon\nController" as C
control "HoaDon\nService" as S
participant "HoaDon\nRepository" as H
participant "MonAn\nRepository" as M
participant "CTHoaDon\nRepository" as D
A -> C : POST /hoa-don/them
C -> S : taoHoaDon(hd, ids, qty)
activate S
S -> S : kiểm tra đầu vào;\ngộp món trùng
alt không hợp lệ
 S --> C : IllegalArgumentException
 C --> A : trả form / thông báo lỗi
else hợp lệ
 group @Transactional
 S -> H : save(hd chưa thanh toán)
 H --> S : hd có maHd
 loop mỗi món sau gộp
  S -> M : findById(maMon)
  M --> S : món có đơn giá
  S -> S : tạo CTHoaDon; lượng × giá
  S -> D : save(chiTiet)
 end
 S -> S : tinhLaiTongTien(maHd)
 S -> D : tinhTongTienTheoHoaDon(maHd)
 D --> S : tổng chi tiết
 S -> H : save(hd với tổng sau giảm)
 end
 S --> C : HoaDon
 C --> A : chuyển hướng chi tiết
end
deactivate S
note over S,D
Lỗi phát sinh trong giao dịch làm rollback.
Không cập nhật trạng thái bàn trong luồng này.
end note
'''
diagrams['07_sequence_thanh_toan'] = r'''autonumber
actor "Thu ngân" as A
boundary "HoaDon\nController" as C
control "HoaDon\nService" as S
participant "HoaDon\nRepository" as H
participant "CTHoaDon\nRepository" as D
A -> C : POST thanh toán (maHd)
C -> S : thanhToan(maHd)
group @Transactional
 S -> H : findById(maHd)
 H --> S : hóa đơn
 alt đã thanh toán
  S --> C : IllegalStateException
 else chưa thanh toán
  S -> D : countById_MaHd(maHd)
  D --> S : số dòng
  alt số dòng = 0
   S --> C : IllegalStateException
  else có món
   S -> S : tinhLaiTongTien(maHd)
   S -> D : tinhTongTienTheoHoaDon(maHd)
   D --> S : tổng chi tiết
   S -> H : save(hd với tổng tiền mới)
   S -> S : đặt Da thanh toan
   S -> H : save(hd)
   S --> C : HoaDon
  end
 end
end
C --> A : kết quả / thông báo
note over S,H
Chưa gọi BanAnService để giải phóng bàn.
Kiểm tra trạng thái hiện chưa khóa chống
hai yêu cầu thanh toán đồng thời.
end note
'''
diagrams['08_sequence_sua_phieu_nhap'] = r'''autonumber
actor "Thủ kho" as A
boundary "PhieuNhap\nController" as C
control "PhieuNhap\nService" as S
participant "Repository liên quan" as R
participant "EntityManager" as EM
A -> C : POST sửa phiếu (id)
C -> S : save(id, form có phienBan)
group @Transactional
 S -> R : findForUpdate(id)
 R --> S : phiếu và chi tiết cũ
 S -> S : checkVersion(phiếu, form.phienBan)
 alt form cũ / dữ liệu sai
  S --> C : ngoại lệ; rollback
 else dữ liệu hợp lệ
  S -> EM : lock(phiếu, FORCE_INCREMENT)
  S -> R : khóa NCC, kiểm tra nhân viên
  S -> S : kiểm tra dòng; tính tổng
  loop nguyên liệu theo mã tăng dần
   S -> R : findForUpdate(maNl)
   S -> EM : refresh(nl, PESSIMISTIC_WRITE)
   S -> S : adjustStock(nl, mới - cũ)
  end
  S -> S : cập nhật phiếu và dòng
  S -> R : saveAndFlush(phiếu)
  R --> S : Phiếu nhập
  S --> C : Phiếu nhập
 end
end
C --> A : chuyển hướng / báo lỗi
note over S,R
R là vai trò rút gọn cho các repository:
PhieuNhap, NhaCungCap, NguyenLieu,
NhanVienNhapKho (JDBC).
end note
'''
diagrams['09_dcd_hoa_don'] = r'''top to bottom direction
class HoaDonController <<boundary>> {
 +taoHoaDon(...): String
 +capNhatHoaDon(...): String
 +thanhToan(...): String
}
class HoaDonService <<control>> {
 +taoHoaDon(...): HoaDon
 +capNhatHoaDon(...): HoaDon
 +thanhToan(maHd): HoaDon
 +tinhLaiTongTien(maHd): HoaDon
 -kiemTraVaGopDanhSachMon(...)
 -luuChiTietHoaDon(...)
}
class HoaDon <<entity>> {
 -maHd: Long
 -maBan, maNv, maKh: Long
 -giamGia: BigDecimal
 -tongTien: BigDecimal
 -trangThai: String
 +setTrangThai(value): void
}
class CTHoaDon <<entity>> {
 -id: CTHoaDonId
 -soLuong: BigDecimal
 -donGia: BigDecimal
 -thanhTien: BigDecimal
 +setId(id): void
}
class CTHoaDonId <<value object>> {
 -maHd: Long
 -maMon: Long
 +equals(obj): boolean
 +hashCode(): int
}
interface HoaDonRepository {
 +findById(maHd): Optional
 +save(hd): HoaDon
}
interface CTHoaDonRepository {
 +save(ct): CTHoaDon
 +countById_MaHd(maHd): long
 +tinhTongTienTheoHoaDon(...)
}
interface MonAnRepository {
 +findById(maMon): Optional
}
HoaDonController --> HoaDonService
HoaDonService --> HoaDonRepository
HoaDonService --> CTHoaDonRepository
HoaDonService --> MonAnRepository
HoaDonRepository ..> HoaDon
CTHoaDonRepository ..> CTHoaDon
CTHoaDon *-- CTHoaDonId
note bottom of HoaDon
Quan hệ bàn, khách, nhân viên lưu bằng Long.
Không có collection chi tiết trong entity HoaDon.
Các thao tác repository cơ bản kế thừa JpaRepository.
end note
'''
diagrams['10_dcd_kho'] = r'''top to bottom direction
class PhieuNhapController <<boundary>> {
 +save(id, form, ...): String
 +delete(id, phienBan, ...): String
}
class PhieuNhapForm <<DTO>> {
 -maNcc: Long
 -maNv: Long
 -ngayNhap: LocalDate
 -phienBan: Long
 -chiTiet: List
}
class PhieuNhapService <<control>> {
 +save(id, form): PhieuNhap
 +delete(id, phienBan): void
 -checkVersion(...)
 -lockMaterials(ids): Map
 -adjustStock(...)
}
class PhieuNhap <<entity>> {
 -maPn: Long
 -ngayNhap: LocalDate
 -tongTien: BigDecimal
 -phienBan: Long {version}
 -maNv: Long
 -chiTiet: List
}
class CTPhieuNhap <<entity>> {
 -id: CTPhieuNhapId
 -soLuong: BigDecimal
 -donGia: BigDecimal
 -thanhTien: BigDecimal
}
class NguyenLieu <<entity>> {
 -maNl: Long
 -soLuongTon: BigDecimal
 -trangThai: String
}
class NhaCungCap <<entity>>
interface PhieuNhapRepository {
 +findForUpdate(id): Optional
 +saveAndFlush(...): PhieuNhap
}
interface NguyenLieuRepository {
 +findForUpdate(id): Optional
}
PhieuNhapController --> PhieuNhapService
PhieuNhapController ..> PhieuNhapForm
PhieuNhapService ..> PhieuNhapForm
PhieuNhapService --> PhieuNhapRepository
PhieuNhapService --> NguyenLieuRepository
PhieuNhap "1" *-- "1..*" CTPhieuNhap
CTPhieuNhap "0..*" --> "1" NguyenLieu
PhieuNhap "0..*" --> "1" NhaCungCap
PhieuNhapService ..> PhieuNhap
PhieuNhapRepository -[hidden]down-> NguyenLieuRepository
note bottom of NguyenLieu
Service còn phụ thuộc:
NhaCungCapRepository,
NhanVienNhapKhoRepository,
EntityManager.
Lược getter/setter thường dùng.
end note
'''
diagrams['11_trang_thai_hoa_don'] = r'''hide empty description
state "Chưa thanh toán\nChua thanh toan" as C
state "Đã thanh toán\nDa thanh toan" as D
state "Đã xóa\n(không còn bản ghi)" as X
[*] --> C : tạo hợp lệ
C --> C : sửa thông tin / món hợp lệ\n/ tính lại tổng
C --> D : thanhToan() [có chi tiết]\n/ tính lại tổng, ghi trạng thái
C --> X : xoaHoaDon()\n/ xóa chi tiết rồi xóa hóa đơn
X --> [*]
note right of D
Không cho sửa, xóa hoặc
thanh toán lại bằng service.
Không có hoàn tiền trong phạm vi.
end note
'''
diagrams['12_trang_thai_dat_ban'] = r'''hide empty description
state "Chờ xác nhận\nCHO_XAC_NHAN" as C
state "Đã xác nhận\nDA_XAC_NHAN" as X
state "Đã hủy\nDA_HUY" as H
state "Hoàn thành\nHOAN_THANH" as T
[*] --> C : tiếp nhận [bàn trống]\n/ giữ bàn
C --> X : xác nhận [chưa kết thúc]
C --> H : hủy\n/ trả bàn trống
X --> H : hủy\n/ trả bàn trống
X --> T : hoàn thành phục vụ\n/ trả bàn trống
H --> [*]
T --> [*]
note bottom of X
MÔ HÌNH ĐỀ XUẤT CHẶT CHẼ HƠN CODE.
Service hiện ghi trạng thái trực tiếp;
chưa kiểm tra đầy đủ trạng thái nguồn.
end note
'''
diagrams['13_erd_ban_hang'] = r'''hide methods
left to right direction
entity KHACH_HANG {
 * MA_KH : NUMBER <<PK>>
 --
 HO_TEN : VARCHAR2(100)
 SDT : VARCHAR2(15)
 EMAIL : VARCHAR2(100)
 DIEM_TICH_LUY : NUMBER(10,0)
}
entity DAT_BAN {
 * MA_DAT : NUMBER <<PK>>
 --
 MA_KH : NUMBER <<FK>>
 MA_BAN : NUMBER <<FK>>
 NGAY_DAT : DATE
 SO_NGUOI : NUMBER(3)
 TIEN_COC : NUMBER(12,2)
 TRANG_THAI : VARCHAR2(20)
}
entity BAN_AN {
 * MA_BAN : NUMBER <<PK>>
 --
 TEN_BAN : VARCHAR2(50)
 SO_CHO : NUMBER(3)
 KHU_VUC : VARCHAR2(50)
 TRANG_THAI : VARCHAR2(20)
}
entity HOA_DON {
 * MA_HD : NUMBER <<PK>>
 --
 MA_BAN : NUMBER <<FK logic>>
 MA_NV : NUMBER <<FK logic>>
 MA_KH : NUMBER <<FK logic, nullable>>
 NGAY_LAP : DATE
 GIAM_GIA : NUMBER(12,2)
 TONG_TIEN : NUMBER(14,2)
 TRANG_THAI : VARCHAR2(20)
}
entity CT_HOA_DON {
 * MA_HD : NUMBER <<PK, FK logic>>
 * MA_MON : NUMBER <<PK, FK logic>>
 --
 SO_LUONG : NUMBER(10,2)
 DON_GIA : NUMBER(12,2)
 THANH_TIEN : NUMBER(14,2)
}
entity MON_AN {
 * MA_MON : NUMBER <<PK>>
 --
 MA_LOAI : NUMBER <<FK>>
 TEN_MON : VARCHAR2(150)
 DON_VI : VARCHAR2(20)
 DON_GIA : NUMBER(12,2)
 TRANG_THAI : VARCHAR2(20)
 HINH_ANH : VARCHAR2(1000)
 MO_TA : VARCHAR2(1000)
}
entity LOAI_MON {
 * MA_LOAI : NUMBER <<PK>>
 --
 TEN_LOAI : VARCHAR2(100)
 MO_TA : VARCHAR2(255)
 TRANG_THAI : VARCHAR2(20)
}
entity NHAN_VIEN {
 * MA_NV : NUMBER <<PK>>
 --
 HO_TEN : VARCHAR2(100)
 CHUC_VU : VARCHAR2(50)
 LUONG : NUMBER(12,2)
 ... thuộc tính hồ sơ
}
KHACH_HANG ||--o{ DAT_BAN
BAN_AN ||--o{ DAT_BAN
KHACH_HANG |o..o{ HOA_DON
BAN_AN ||..o{ HOA_DON
NHAN_VIEN ||..o{ HOA_DON
HOA_DON ||..o{ CT_HOA_DON
MON_AN ||..o{ CT_HOA_DON
LOAI_MON ||--o{ MON_AN
'''
diagrams['14_erd_kho_tai_khoan'] = r'''hide methods
left to right direction
entity NHA_CUNG_CAP {
 * MA_NCC : NUMBER <<PK>>
 --
 TEN_NCC : VARCHAR2(150)
 SDT : VARCHAR2(15)
 DIA_CHI : VARCHAR2(200)
 EMAIL : VARCHAR2(100)
}
entity PHIEU_NHAP {
 * MA_PN : NUMBER <<PK>>
 --
 MA_NCC : NUMBER <<FK>>
 MA_NV : NUMBER <<FK logic>>
 NGAY_NHAP : DATE
 TONG_TIEN : NUMBER(14,2)
 PHIEN_BAN : NUMBER
}
entity CT_PHIEU_NHAP {
 * MA_PN : NUMBER <<PK, FK>>
 * MA_NL : NUMBER <<PK, FK>>
 --
 SO_LUONG : NUMBER(10,2)
 DON_GIA : NUMBER(12,2)
 THANH_TIEN : NUMBER(14,2)
}
entity NGUYEN_LIEU {
 * MA_NL : NUMBER <<PK>>
 --
 TEN_NL : VARCHAR2(100)
 DON_VI : VARCHAR2(20)
 SO_LUONG_TON : NUMBER(10,2)
 TRANG_THAI : VARCHAR2(20)
}
entity NHAN_VIEN {
 * MA_NV : NUMBER <<PK>>
 --
 HO_TEN : VARCHAR2(100)
 GIOI_TINH : VARCHAR2(10)
 SDT : VARCHAR2(15)
 CHUC_VU : VARCHAR2(50)
 LUONG : NUMBER(12,2)
 NGAY_VAO_LAM : DATE
 EMAIL : VARCHAR2(100)
 CA_LAM_VIEC : VARCHAR2(100)
 TRANG_THAI : VARCHAR2(30)
}
entity TAI_KHOAN {
 * MA_TK : NUMBER <<PK>>
 --
 HO_TEN : VARCHAR2(100)
 TEN_DANG_NHAP : VARCHAR2(30) <<UQ>>
 MAT_KHAU : VARCHAR2(100)
}
NHA_CUNG_CAP ||--o{ PHIEU_NHAP
NHAN_VIEN ||..o{ PHIEU_NHAP
PHIEU_NHAP ||--o{ CT_PHIEU_NHAP
NGUYEN_LIEU ||--o{ CT_PHIEU_NHAP
note bottom of TAI_KHOAN
Bảng bổ sung ngoài 12 bảng nghiệp vụ.
Chưa liên kết với NHAN_VIEN;
chưa lưu vai trò riêng.
end note
'''

if __name__ == '__main__':
    for name, body in diagrams.items():
        (OUT / (name + '.puml')).write_text(COMMON + body + '\n@enduml\n', encoding='utf-8')
    java = '/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java'
    import os
    java = os.environ.get('NHHS_REPORT_JAVA', java)
    jar = os.environ.get('NHHS_PLANTUML_JAR', '/private/tmp/nhhs-plantuml.jar')
    for fmt in ['-tsvg', '-tpng']:
        subprocess.run([java, '-Djava.awt.headless=true', '-jar', jar, '-charset', 'UTF-8', fmt, str(OUT / '*.puml')], check=True)
    print('Đã dựng', len(diagrams), 'sơ đồ, mỗi sơ đồ có PUML, SVG, PNG.')
