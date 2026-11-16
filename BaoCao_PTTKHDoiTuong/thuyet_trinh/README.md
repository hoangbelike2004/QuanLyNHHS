# Thuyết trình BTL Nhóm 4

- **ThuyetTrinh_BTL_PTTKHDoiTuong_Nhom4_cap_nhat.pptx**: bản PowerPoint mới nhất, 12 slide, tỷ lệ 16:9. File gốc đang mở trong PowerPoint nên Windows chưa cho ghi đè.
- **ThuyetTrinh_BTL_PTTKHDoiTuong_Nhom4.pptx**: bản trước khi thêm khảo sát, mô tả nghiệp vụ, công cụ và kiểm thử.
- **ThuyetTrinh_BTL_PTTKHDoiTuong_Nhom4.pdf**: bản xem trước cũ; nội dung mới nhất nằm trong PowerPoint.
- **Goi_y_thuyet_trinh.md**: lời dẫn ngắn, cũng đã nhúng vào Notes của từng slide; thời lượng gợi ý 8–10 phút.

Thiết kế nền sáng, chữ Arial, màu xanh trầm. Mỗi slide dùng hiệu ứng chuyển trang Fade nhanh, không có animation hoặc âm thanh. Chữ, hình và sơ đồ trong PowerPoint đều chỉnh sửa được. Slide 2 trình bày nguồn khảo sát yêu cầu và bốn nhóm nghiệp vụ; slide 3 là use case tổng quát; slide 7 mô tả quy trình công việc; slide 10 nêu công cụ, thư viện và dữ liệu; slide 11 tách việc đã xử lý với kết quả kiểm thử.

Đã kiểm tra cấu trúc PPTX mới, 12 slide, Notes, hiệu ứng Fade và vị trí các thành phần. Máy hiện tại không xuất PDF trực tiếp từ PowerPoint, nên PDF và ảnh trong `xem_truoc` chưa phản ánh lần chỉnh sửa này.

Nguồn chính: báo cáo Nhóm 4, hướng dẫn học phần, Excel phân công và mã nguồn QuanLyNHHS. Các giới hạn sản phẩm và phạm vi kiểm thử giữ đúng như báo cáo.

Tệp `tao_powerpoint.py` tạo bố cục gốc; chạy `cap_nhat_powerpoint.ps1` sau đó để áp dụng nội dung trình bày và hiệu ứng chuyển trang. Nếu bản gốc đang mở, script xuất bản mới với hậu tố `_cap_nhat`. Script Python cần `python-pptx==1.0.2`, `reportlab==4.5.1`, `pymupdf==1.26.5` và đường dẫn font Arial phù hợp với máy chạy.
