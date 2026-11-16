# Thuyết trình PTTK hướng đối tượng – Nhóm 4

[`PP_PTTKHT_TMP.pptx`](PP_PTTKHT_TMP.pptx) là bản trình bày hoàn chỉnh, gồm **15 slide** tỷ lệ 16:9. Mỗi slide có ghi chú cho người thuyết trình trong PowerPoint. Thời lượng gợi ý: 10–12 phút.

## Thứ tự trình bày

| Slide | Nội dung chính |
| --- | --- |
| 1–3 | Đề tài, khảo sát và use case theo bốn vai trò hiện có. |
| 4–6 | Đối tượng chính; mô hình khái niệm bán hàng và nhập kho. |
| 7–8 | Kiến trúc tầng và sơ đồ chi tiết đối chiếu mã nguồn. |
| 9–12 | Lập hóa đơn, luồng nghiệp vụ, điều chỉnh tồn và trạng thái hóa đơn. |
| 13–15 | Công cụ, bằng chứng kiểm thử và kết luận. |

Ba sơ đồ ảnh ở slide 5, 6 và 8 được lấy từ bản MAIN sau khi đối chiếu với các entity, service, repository và cấu hình bảo mật hiện tại. Tệp nguồn của các sơ đồ vẫn ở [`../so_do/`](../so_do/): `02_khai_niem_ban_hang`, `03_khai_niem_nhap_kho`, `05_kien_truc` (`.puml`, `.svg`, `.png`). Đây là **mô hình khái niệm và kiến trúc**; một số quan hệ nghiệp vụ trong sơ đồ được code lưu bằng mã, không ngụ ý mọi quan hệ đều có ánh xạ JPA hoặc khóa ngoại vật lý.

Slide 11–12 trình bày nghiệp vụ đã cài đặt và quy tắc trạng thái. Hai sơ đồ trạng thái bổ sung từ MAIN không được lặp lại vì TMP đã có phần này; sơ đồ đặt bàn trong MAIN còn mô tả chuyển trạng thái chặt hơn kiểm tra hiện có của `DatBanService`.

Số **23 kiểm thử Java trên H2** ở slide 14 dựa trên ba báo cáo Surefire gần nhất cho `DatabasePagesTests` (4), `MenuCrudIntegrationTests` (18) và `QuanLyNhhsApplicationTests` (1). Đây không phải bằng chứng mọi giao dịch trên Oracle đã chạy thành công. Dữ liệu, ngày chạy và môi trường cần được kiểm tra lại trước buổi bảo vệ.

## Trước khi trình chiếu

1. Mở TMP trong PowerPoint, kiểm tra hiển thị tiếng Việt, sơ đồ và ghi chú ở ba slide mới.
2. Điền thông tin giảng viên/lớp/mã sinh viên nếu cần trên bìa báo cáo nhóm.
3. Xuất PDF mới từ TMP sau khi chốt nội dung. Các file PDF trình chiếu cũ không còn là bản tham chiếu cho TMP.

`tao_powerpoint.py` và `cap_nhat_powerpoint.ps1` là script tạo bản 12 slide cũ; chạy chúng sẽ không tái tạo ba slide sơ đồ vừa ghép vào TMP.
