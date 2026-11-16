# Phân quyền tài khoản

Ứng dụng lưu vai trò trong `TAI_KHOAN.VAI_TRO` và kiểm tra quyền bằng Spring Security.

| Vai trò | Chức năng |
| --- | --- |
| `ADMIN` | Toàn bộ chức năng; quản lý tài khoản, nhân sự, thực đơn, bàn ăn; xóa hóa đơn và đặt bàn. |
| `QUAN_LY_KHO` | Quản lý nguyên liệu, nhà cung cấp, phiếu nhập và tồn kho. |
| `KE_TOAN` | Xem báo cáo doanh thu, hóa đơn và phiếu nhập; không sửa chứng từ. |
| `THU_NGAN` | Quản lý khách hàng và đặt bàn; xem bàn ăn; lập, sửa, xác nhận món và thanh toán hóa đơn. Không giảm hoặc xóa món đã xác nhận. |

Vai trò `NHAN_VIEN` đã được gộp vào `THU_NGAN`. Bảng `NHAN_VIEN` vẫn giữ để lưu hồ sơ và làm khóa tham chiếu cho hóa đơn, phiếu nhập. Tài khoản `QUAN_LY` cũ được xem là `ADMIN` khi đăng nhập.

Các bước chạy migration và nạp dữ liệu nằm trong [hướng dẫn cài đặt và dữ liệu Oracle](database/oracle/README.md). Ứng dụng vẫn nhận giá trị vai trò cũ khi đăng nhập cho đến khi hoàn tất chuyển đổi.
