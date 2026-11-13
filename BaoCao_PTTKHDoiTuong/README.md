# Báo cáo BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4

Đề tài: **Hệ thống quản lý nhà hàng hải sản**.

- Trường Đại học Giao thông Vận tải, Khoa Công nghệ thông tin.
- Bản PDF có 48 trang cố định; bản Word chỉnh sửa được.
- Giảng viên, lớp và mã sinh viên để trống theo yêu cầu.
- Báo cáo cũ `BTL.docx` và `BTL.pdf` không bị thay đổi.

## Tệp chính

- `BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx`: bản để chỉnh sửa.
- `BaoCao_BTL_PTTKHDoiTuong_Nhom4.pdf`: bản để xem và in.
- `so_do/`: 14 sơ đồ, mỗi sơ đồ có `.puml` để sửa, `.svg` để phóng to, `.png` đã chèn vào báo cáo.
- `nguon/`: nội dung, script tạo tài liệu, log kiểm thử và kiểm tra chất lượng.

## Trước khi nộp

1. Bổ sung giảng viên, lớp, mã sinh viên và xác nhận phân công thực tế.
2. Nhóm đọc và thống nhất giả định, chính sách giá, các điểm thiết kế đề xuất.
3. Chạy test Java/Oracle và ghi kết quả thực tế cho TC01–TC20. Hiện các trường hợp này được ghi **chưa chạy**; chỉ 4 test JavaScript được chạy trong lần lập báo cáo.
4. Sau khi sửa Word, cập nhật trường tham chiếu trang (chọn toàn bộ, Update Field/F9; macOS có thể cần Fn+F9) và xuất PDF mới. Số trang Word có thể thay đổi tùy phiên bản Word và font máy; số trang PDF đã được kiểm tra.

## Tái tạo bằng Python (tùy chọn)

Tạo môi trường Python và cài `nguon/requirements.txt`. Script mặc định dùng font Times New Roman ở macOS; nếu chạy hệ điều hành khác, sửa `FONT_DIR` trong `tao_bao_cao.py` sang thư mục chứa font tương ứng.

```sh
python nguon/tao_bao_cao.py
```

Để dựng lại hình, cần Java và PlantUML JAR. Dùng biến môi trường `NHHS_REPORT_JAVA` và `NHHS_PLANTUML_JAR` trỏ đến hai tệp trên máy của bạn, rồi chạy:

```sh
python nguon/tao_so_do.py
python nguon/tao_bao_cao.py
python nguon/kiem_tra.py
```

Các `.puml` cũng có thể được sửa/xuất trực tiếp bằng công cụ PlantUML mà không cần chạy script. Không cần cài Java/PlantUML để mở Word, PDF, PNG hoặc SVG.

## Căn cứ và mức kiểm chứng

Nội dung dựa trên PDF hướng dẫn học phần, Excel phân công, ảnh sơ đồ do người dùng gửi và mã nguồn dự án ngày 13/11/2026. Tệp tài liệu ngoài dự án không được sao chép toàn bộ vào gói bàn giao.

Phần lớn mô tả cài đặt được đối chiếu mã nguồn. Chưa khởi động ứng dụng hoặc chạy test Oracle trong lần này. Kiểm tra JavaScript đạt 4/4; log tại `nguon/kiem_thu_js.txt`. Báo cáo tách rõ thiết kế nghiệp vụ, cài đặt hiện tại và đề xuất cải thiện.
