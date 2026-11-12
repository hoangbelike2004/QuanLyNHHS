# Thêm dữ liệu vào Oracle

## Những gì đã được dọn

Ba trang `/nhan-vien`, `/mon-an`, `/loai-mon` đọc dữ liệu từ Oracle thay cho danh sách mẫu trong controller. Khi bảng rỗng, trang hiển thị thông báo chưa có dữ liệu. Số món theo loại được đếm từ `MON_AN`; số lượng đã bán được cộng từ chi tiết hóa đơn có trạng thái `Da thanh toan`.

Email, ca làm, trạng thái nhân viên và ảnh, mô tả món ăn đã được ánh xạ thành cột thật. Bản ghi cũ có thể để trống các cột mới; hệ thống không tự gán thông tin mẫu cho bản ghi cũ.

Các giá trị điền sẵn trong modal đã được bỏ. Các biểu mẫu thêm/sửa/xóa ở **ba trang này chưa có endpoint lưu dữ liệu**; chúng hiển thị thông báo chưa hỗ trợ thay cho thông báo thành công giả. Dùng SQL bên dưới để thêm dữ liệu. Các module khác giữ nguyên cách lưu hiện có.

Không có lệnh xóa dữ liệu Oracle trong thay đổi này. Script INSERT chỉ chạy khi bạn tự mở và thực thi; ứng dụng không tự nạp script.

## 1. Kiểm tra cấu hình kết nối

Trong `src/main/resources/application.properties`, mặc định:

| Thuộc tính | Giá trị |
| --- | --- |
| Host | `localhost` |
| Port | `1521` |
| Service name | `FREEPDB1` |
| Username/schema | `NHHS_APP` |
| Password | Theo `DB_PASSWORD`; nếu chưa đặt thì dùng giá trị mặc định trong file cấu hình |

Nếu dùng Oracle XE, kiểm tra service của máy bạn, thường là `XEPDB1`. Các biến `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` ghi đè giá trị mặc định. Kết nối SQL Developer phải dùng cùng service và schema với ứng dụng.

Nếu **chưa có schema**, dùng tài khoản quản trị trong đúng PDB để chạy một lần, thay mật khẩu bên dưới bằng mật khẩu bạn chọn:

```sql
-- Chỉ chạy CREATE USER khi NHHS_APP chưa tồn tại.
CREATE USER NHHS_APP IDENTIFIED BY "ThayMatKhauCuaBan";
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO NHHS_APP;
ALTER USER NHHS_APP QUOTA UNLIMITED ON USERS;
```

Đặt `DB_PASSWORD` tương ứng khi chạy ứng dụng. Không cần chạy toàn bộ `oracle_setup_user.sql` để INSERT: file đó còn tạo role và các tài khoản ví dụ khác.

## 2. Cho ứng dụng tạo/cập nhật bảng

Chạy dự án từ IDE hoặc, tại thư mục chứa `pom.xml`:

```powershell
mvn spring-boot:run
```

Cấu hình hiện tại là `spring.jpa.hibernate.ddl-auto=update`. Sau khi ứng dụng khởi động thành công, Hibernate tạo các bảng/sequence còn thiếu và bổ sung cột mới:

- `NHAN_VIEN`: `EMAIL`, `CA_LAM_VIEC`, `TRANG_THAI`.
- `MON_AN`: `HINH_ANH`, `MO_TA`.

Nếu bạn đã đổi `ddl-auto` thành `none` hoặc `validate`, cần bổ sung các cột này thủ công trước khi chạy:

```sql
-- Chỉ chạy từng lệnh nếu cột chưa tồn tại.
ALTER TABLE NHAN_VIEN ADD EMAIL VARCHAR2(100 CHAR);
ALTER TABLE NHAN_VIEN ADD CA_LAM_VIEC VARCHAR2(100 CHAR);
ALTER TABLE NHAN_VIEN ADD TRANG_THAI VARCHAR2(30 CHAR);
ALTER TABLE MON_AN ADD HINH_ANH VARCHAR2(1000 CHAR);
ALTER TABLE MON_AN ADD MO_TA VARCHAR2(1000 CHAR);
```

Kiểm tra trong SQL Developer:

```sql
SELECT USER FROM DUAL;
SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME;
SELECT SEQUENCE_NAME FROM USER_SEQUENCES ORDER BY SEQUENCE_NAME;
SELECT COLUMN_NAME, DATA_TYPE, CHAR_LENGTH
FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'NHAN_VIEN';
```

Không chạy `01_inventory.sql` trên schema đã được Hibernate tạo bảng kho vì sẽ trùng tên bảng/sequence. `02_routines.sql` chỉ cần nếu muốn cài các procedure/trigger quản trị kho có sẵn; script INSERT mới không phụ thuộc các procedure đó. Nếu tạo schema kho thủ công bằng `01_inventory.sql`, script này tự gọi `02_routines.sql`; schema cần có bảng `NHAN_VIEN` trước và quyền tạo procedure/trigger tương ứng.

## 3. Chạy bộ INSERT có đủ quan hệ

1. Trong SQL Developer, kết nối bằng schema của ứng dụng, thường là `NHHS_APP`.
2. Mở worksheet mới, không có giao dịch chưa lưu, tắt Auto Commit để script quản lý giao dịch.
3. Mở file UTF-8 `03_insert_data.sql` trong thư mục này. Đổi tên, số điện thoại, giá, ngày… thành dữ liệu muốn sử dụng.
4. Chọn **Run Script (F5)** để chạy cả file. File dùng khối PL/SQL nên cần chạy cả `DECLARE … END;` và dấu `/` ở dòng riêng.
5. Kiểm tra thông báo và kết quả SELECT cuối file, rồi tải lại các trang của ứng dụng. Thay đổi SQL đã COMMIT sẽ được đọc ở lần tải trang tiếp theo.

Script thêm nhân viên, loại món, món ăn, bàn, khách hàng, đặt bàn, nhà cung cấp, nguyên liệu, phiếu nhập/chi tiết và hóa đơn/chi tiết. Không tạo tài khoản đăng nhập. Hệ thống không hỗ trợ tự đăng ký; quản trị viên cấp tài khoản trong bảng `TAI_KHOAN`. Tên đăng nhập lưu bằng chữ thường, không có khoảng trắng đầu/cuối; cột `MAT_KHAU` phải chứa mật khẩu đã băm BCrypt (độ khó 12), không lưu mật khẩu dạng rõ. Hiện chưa có màn hình quản trị cấp tài khoản trong ứng dụng.

**Mỗi lần chạy thành công sẽ thêm một bộ dữ liệu nữa.** Chỉ chạy một lần nếu không muốn lặp dữ liệu. Nếu một INSERT lỗi, khối PL/SQL ROLLBACK toàn bộ bộ dữ liệu của lần chạy đó. Sequence/identity vẫn có thể nhảy số sau ROLLBACK; đây không phải lỗi.

## 4. Tự INSERT từng bảng

Ví dụ thêm riêng một nhân viên sau khi bảng đã có các cột mới:

```sql
INSERT INTO NHAN_VIEN
    (MA_NV, HO_TEN, GIOI_TINH, SDT, CHUC_VU, LUONG,
     NGAY_VAO_LAM, EMAIL, CA_LAM_VIEC, TRANG_THAI)
VALUES
    (SEQ_NHAN_VIEN.NEXTVAL, 'Nguyễn Văn Nam', 'Nam', '0909000001',
     'Nhân viên phục vụ', 8000000, DATE '2026-11-09',
     'nam@example.com', 'Ca tối (15:00 - 23:00)', 'Đang làm việc');

COMMIT;
SELECT * FROM NHAN_VIEN ORDER BY MA_NV;
```

Thêm loại món trước, lấy mã được sinh ra rồi dùng mã đó để thêm món ăn. Ví dụ sau dùng biến PL/SQL để giữ đúng quan hệ mà không giả định mã loại luôn là `1`:

```sql
SET DEFINE OFF
DECLARE
    V_MA_LOAI LOAI_MON.MA_LOAI%TYPE;
BEGIN
    INSERT INTO LOAI_MON (MA_LOAI, TEN_LOAI, MO_TA, TRANG_THAI)
    VALUES (SEQ_LOAI_MON.NEXTVAL, 'Món hấp', 'Hải sản hấp', 'Hoạt động')
    RETURNING MA_LOAI INTO V_MA_LOAI;

    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI)
    VALUES (SEQ_MON_AN.NEXTVAL, V_MA_LOAI, 'Nghêu hấp sả', 'Đĩa', 120000, 'Đang phục vụ');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
COMMIT;
```

| Bảng | Cách sinh mã |
| --- | --- |
| `NHAN_VIEN` | `SEQ_NHAN_VIEN.NEXTVAL` |
| `LOAI_MON` | `SEQ_LOAI_MON.NEXTVAL` |
| `MON_AN` | `SEQ_MON_AN.NEXTVAL` |
| `NHA_CUNG_CAP` | `SEQ_NHA_CUNG_CAP.NEXTVAL` |
| `NGUYEN_LIEU` | `SEQ_NGUYEN_LIEU.NEXTVAL` |
| `PHIEU_NHAP` | `SEQ_PHIEU_NHAP.NEXTVAL` |
| `BAN_AN`, `KHACH_HANG`, `DAT_BAN`, `HOA_DON` | Identity: không ghi cột mã trong INSERT |
| `CT_HOA_DON`, `CT_PHIEU_NHAP` | Khóa ghép: dùng mã bản ghi cha đã tồn tại |

Đây là cách sinh mã theo các entity hiện tại. Nếu schema cũ được tạo theo thiết kế khác, kiểm tra `USER_TAB_IDENTITY_COLS` và `USER_SEQUENCES` trước khi chạy script.

Thứ tự quan hệ: loại món → món ăn; bàn + khách → đặt bàn; nhân viên + nhà cung cấp → phiếu nhập → chi tiết với nguyên liệu; bàn + nhân viên + khách → hóa đơn → chi tiết với món ăn.

## 5. Giá trị và lỗi thường gặp

- Mã là số (`NUMBER`). Không INSERT chuỗi `NV01`, `LM01` hoặc `MA01` vào cột mã.
- Dùng `DATE 'YYYY-MM-DD'` để tránh phụ thuộc định dạng ngày của Oracle. `SYSDATE` theo đồng hồ máy chủ Oracle; nếu muốn ngày cụ thể, thay bằng DATE literal.
- Giá và lương ghi `8000000`, không ghi dấu phân cách hàng nghìn hoặc chữ `đ`.
- Trạng thái nhân viên: `Đang làm việc`, `Nghỉ phép`, `Đã nghỉ việc`; loại món: `Hoạt động`, `Tạm dừng`; món: `Đang phục vụ`, `Tạm hết`.
- Trạng thái nguyên liệu dùng `HOAT_DONG`, `NGUNG_SU_DUNG`; đặt bàn dùng `CHO_XAC_NHAN`, `DA_XAC_NHAN`, `DA_HUY`, `HOAN_THANH`.
- Hóa đơn dùng đúng chuỗi **không dấu** `Chua thanh toan` hoặc `Da thanh toan` theo service hiện tại. Báo cáo chỉ tính hóa đơn đã thanh toán.
- `ORA-00942`: kiểm tra đúng schema/service và đã chạy ứng dụng tạo bảng. `ORA-00904`: kiểm tra các cột mới trong bước 2. `ORA-02289`: thiếu sequence hoặc đang kết nối sai schema.
- `ORA-02291`: mã cha chưa tồn tại; dùng đúng thứ tự INSERT. `ORA-00001`: trùng khóa; nếu từng gán mã bằng tay, kiểm tra và đồng bộ sequence với dữ liệu hiện có khi ứng dụng đã dừng, không tự chuyển sang mã cố định khác.
- Tiếng Việt bị lỗi: mở script bằng UTF-8 và kiểm tra bộ ký tự database hỗ trợ tiếng Việt. `SET DEFINE OFF` tránh SQL Developer hỏi biến khi dữ liệu có ký tự `&`.
- INSERT phiếu nhập trực tiếp phải cập nhật `THANH_TIEN`, `TONG_TIEN`, `PHIEN_BAN` và tồn kho nhất quán. Bộ INSERT đã làm việc này; khi tạo phiếu trên giao diện, service tự cộng tồn nên không cộng thêm bằng SQL.
