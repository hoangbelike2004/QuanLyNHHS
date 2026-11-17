# Báo cáo BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4

Đề tài: **Hệ thống quản lý nhà hàng hải sản**.

- Trường Đại học Giao thông Vận tải, Khoa Công nghệ thông tin.
- Bản PDF hiện tại có 49 trang, gồm phần cập nhật ngày 16/11/2026; bản Word chỉnh sửa được.
- Giảng viên, lớp và mã sinh viên để trống theo yêu cầu.
- Mục lục của cả `../BTL.docx` và báo cáo này đã được sửa ngày 17/11/2026, đồng bộ với các bản PDF tương ứng.

## Sửa mục lục ngày 17/11/2026

- BTL: 23 mục, thống nhất font chữ, cấp chương/mục, dấu chấm dẫn và cột số trang.
- PTTK HĐT: 49 mục riêng, bổ sung các mục trước đây bị gộp và Phụ lục C; đồng thời chỉnh 14 dòng danh mục hình.
- Sau phản hồi về Word hiện số `0` hoặc “Không tìm thấy nội dung”, bản PTTK HĐT đã đổi 63 dòng tra cứu sang **số trang dạng văn bản và liên kết bookmark**. Không còn trường `TOC` hoặc `PAGEREF` để Word tự tính lại những số này khi mở tệp. Chú thích hình có bookmark riêng.
- Đã lược hoặc viết gọn 17 ghi chú biên soạn dư thừa, gồm ghi chú “Cột đáp ứng…” và hướng dẫn cập nhật trường. Các giới hạn kiểm thử được giữ tại mục 4.5; các giải thích về nghiệp vụ và mô hình UML vẫn được giữ. Chi tiết thay đổi: `nguon/ra_soat_bao_cao.json`.
- 14 sơ đồ và các bảng vẫn được giữ đầy đủ. Phụ lục C bắt đầu ở trang mới.
- PDF đã được đồng bộ trực tiếp và kiểm tra 63 số trang/liên kết. Chưa mở và phân trang lại bằng Microsoft Word trên máy này; các số trang văn bản không tự cập nhật khi đổi bố cục.
- Bản trước khi sửa lưu tại `../target/report-build/toc-fix/backup/`. Kết quả kiểm tra: `nguon/kiem_tra_muc_luc.json`.

Để kiểm tra bản đã sửa, chạy từ thư mục gốc dự án, trong môi trường có các thư viện ở `nguon/requirements.txt`:

```sh
python BaoCao_PTTKHDoiTuong/nguon/kiem_tra_muc_luc.py
```

`nguon/sua_muc_luc.py` thuộc lần sửa đầu, dùng bản PDF sao lưu làm căn cứ; không chạy công cụ đó trên bản PTTK HĐT đã hoàn thiện. `nguon/hoan_thien_bao_cao.py` làm sạch ghi chú và tạo các dòng tra cứu có số trang dạng văn bản. Sau khi sửa nội dung làm đổi phân trang, xuất PDF mới rồi đối chiếu/cập nhật số trang; **F9 không tự cập nhật các số trang văn bản này**. Có thể đồng bộ bằng:

```sh
python BaoCao_PTTKHDoiTuong/nguon/hoan_thien_bao_cao.py duong_dan_den_ban_PDF_moi.pdf
```

Sau đó xuất lại PDF để hiển thị số trang mới vừa ghi vào Word.

## Tệp chính

- `BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx`: bản để chỉnh sửa.
- `BaoCao_BTL_PTTKHDoiTuong_Nhom4.pdf`: bản để xem và in.
- `so_do/`: 14 sơ đồ, mỗi sơ đồ có `.puml` để sửa, `.svg` để phóng to, `.png` đã chèn vào báo cáo.
- `nguon/`: nội dung, script tạo tài liệu, log kiểm thử và kiểm tra chất lượng.

## Trước khi nộp

1. Bổ sung giảng viên, lớp, mã sinh viên và xác nhận phân công thực tế.
2. Nhóm đọc và thống nhất giả định, chính sách giá, các điểm thiết kế đề xuất.
3. Chạy test Java/Oracle và ghi kết quả thực tế cho TC01–TC20. Hiện các trường hợp này được ghi **chưa chạy**; chỉ 4 test JavaScript được chạy trong lần lập báo cáo.
4. Sau khi sửa Word, xuất PDF rồi đối chiếu và cập nhật số trang trong mục lục/danh mục hình theo quy trình ở trên. Số trang có thể thay đổi tùy trình soạn thảo và font máy.

## Tái tạo bằng Python (tùy chọn)

Các script `tao_bao_cao.py`, `noi_dung.py` và `kiem_tra.py` thuộc bản nguồn 48 trang ban đầu. Chạy tái tạo sẽ ghi đè tài liệu hiện tại và không giữ các chỉnh sửa đã bổ sung trực tiếp trong Word, bao gồm phần cập nhật ngày 16/11/2026. Khi chỉnh sửa bản hiện tại, dùng Word và quy trình cập nhật mục lục ở trên.

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
