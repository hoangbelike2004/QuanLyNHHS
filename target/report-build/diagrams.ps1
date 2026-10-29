$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$outDir = $PSScriptRoot
$script:bmp = $null
$script:g = $null
function Canvas([int]$w,[int]$h) {
    $script:bmp = [Drawing.Bitmap]::new($w,$h)
    $script:g = [Drawing.Graphics]::FromImage($script:bmp)
    $g.Clear([Drawing.Color]::White)
    $g.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [Drawing.Text.TextRenderingHint]::AntiAliasGridFit
}
function Label($s,$x,$y,$w,$h,$size=23,$bold=$false,$color='#19354A') {
    $style = [Drawing.FontStyle]::Regular
    if ($bold) { $style = [Drawing.FontStyle]::Bold }
    $font=[Drawing.Font]::new('Arial',[single]$size,$style,[Drawing.GraphicsUnit]::Pixel)
    $brush=[Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml($color))
    $g.DrawString($s,$font,$brush,[Drawing.RectangleF]::new($x,$y,$w,$h))
    $font.Dispose(); $brush.Dispose()
}
function Box($x,$y,$w,$h,$title,$lines,$fill='#EAF2F8') {
    $brush=[Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml($fill))
    $pen=[Drawing.Pen]::new([Drawing.ColorTranslator]::FromHtml('#537184'),2)
    $g.FillRectangle($brush,$x,$y,$w,$h)
    $g.DrawRectangle($pen,$x,$y,$w,$h)
    Label $title ($x+15) ($y+10) ($w-24) 40 26 $true
    $g.DrawLine($pen,$x,($y+53),($x+$w),($y+53))
    Label $lines ($x+15) ($y+65) ($w-25) ($h-65) 22
    $pen.Dispose();$brush.Dispose()
}
function Arrow($points,$label='',$lx=0,$ly=0) {
    $pen=[Drawing.Pen]::new([Drawing.ColorTranslator]::FromHtml('#537184'),3)
    $pen.CustomEndCap=[Drawing.Drawing2D.AdjustableArrowCap]::new(5,6)
    $pts=@($points | ForEach-Object { [Drawing.PointF]::new($_[0],$_[1]) })
    $g.DrawLines($pen,[Drawing.PointF[]]$pts)
    if ($label) { Label $label $lx $ly 155 34 21 $true }
    $pen.Dispose()
}
function Save($name) { $bmp.Save((Join-Path $outDir $name),[Drawing.Imaging.ImageFormat]::Png);$g.Dispose();$bmp.Dispose() }

Canvas 1600 1540
Label 'MÔ HÌNH QUAN HỆ • 12 BẢNG' 30 15 1500 60 38 $true
Label 'Vẽ lại theo sơ đồ đề tài; mũi tên đi từ bảng cha sang bảng con (1 → N).' 30 75 1500 45 24
Box 30 155 430 230 'KHACH_HANG' "PK  MA_KH`nHO_TEN, SDT, EMAIL`nDIEM_TICH_LUY" '#FBEAED'
Box 590 155 420 230 'DAT_BAN' "PK  MA_DAT`nFK  MA_KH, MA_BAN`nNGAY_DAT, SO_NGUOI ..." '#E7F3FC'
Box 1140 155 430 230 'BAN_AN' "PK  MA_BAN`nTEN_BAN, SO_CHO`nKHU_VUC, TRANG_THAI" '#FBEAED'
Box 30 530 430 215 'LOAI_MON' "PK  MA_LOAI`nTEN_LOAI, MO_TA`nTRANG_THAI" '#E9F5E6'
Box 590 530 420 215 'HOA_DON' "PK  MA_HD`nFK  MA_BAN, MA_NV, MA_KH`nNGAY_LAP, TONG_TIEN ..." '#EFE9F8'
Box 1140 530 430 215 'NHAN_VIEN' "PK  MA_NV`nHO_TEN, CHUC_VU, LUONG`nSDT, NGAY_VAO_LAM ..." '#FBEAED'
Box 30 875 430 210 'MON_AN' "PK  MA_MON; FK  MA_LOAI`nTEN_MON, DON_VI`nDON_GIA, TRANG_THAI" '#E9F5E6'
Box 590 875 420 210 'CT_HOA_DON' "PK, FK  MA_HD, MA_MON`nSO_LUONG, DON_GIA`nTHANH_TIEN" '#EFE9F8'
Box 1140 875 430 210 'NGUYEN_LIEU' "PK  MA_NL`nTEN_NL, DON_VI`nSO_LUONG_TON, TRANG_THAI" '#FFF4D5'
Box 30 1220 430 210 'NHA_CUNG_CAP' "PK  MA_NCC`nTEN_NCC, SDT`nDIA_CHI, EMAIL" '#FFF4D5'
Box 590 1220 420 210 'PHIEU_NHAP' "PK  MA_PN`nFK  MA_NCC, MA_NV`nNGAY_NHAP, TONG_TIEN" '#E7F3FC'
Box 1140 1220 430 210 'CT_PHIEU_NHAP' "PK, FK  MA_PN, MA_NL`nSO_LUONG, DON_GIA`nTHANH_TIEN" '#E7F3FC'
Arrow @(@(460,235),@(590,235)) '1 : N' 485 195
Arrow @(@(1140,235),@(1010,235)) '1 : N' 1035 195
Arrow @(@(1355,385),@(1355,450),@(800,450),@(800,530)) '1 : N' 1045 414
Arrow @(@(245,385),@(245,465),@(650,465),@(650,530)) '1 : N' 405 427
Arrow @(@(1140,640),@(1010,640)) '1 : N' 1035 600
Arrow @(@(245,745),@(245,875)) '1 : N' 265 790
Arrow @(@(800,745),@(800,875)) '1 : N' 820 790
Arrow @(@(460,980),@(590,980)) '1 : N' 482 941
Arrow @(@(460,1325),@(590,1325)) '1 : N' 482 1286
Arrow @(@(1010,1325),@(1140,1325)) '1 : N' 1035 1286
Arrow @(@(1355,1085),@(1355,1220)) '1 : N' 1375 1140
Arrow @(@(1560,745),@(1583,745),@(1583,1160),@(800,1160),@(800,1220)) '1 : N (MA_NV)' 1030 1120
Label 'PK: khóa chính    FK: khóa ngoại    Các dấu ... chỉ thuộc tính đã liệt kê đầy đủ trong từ điển dữ liệu.' 30 1470 1540 40 23
Save 'erd.png'

Canvas 1600 1000
Label 'KIẾN TRÚC ỨNG DỤNG' 35 25 1500 60 38 $true
Box 65 145 430 205 'TRÌNH DUYỆT' "HTML / CSS / JavaScript`nThymeleaf tạo HTML`nLọc, tìm kiếm, modal" '#E7F3FC'
Box 610 145 900 205 'SPRING MVC • CONTROLLER' "HomeController, LoaiMonController`nMonAnController, NhanVienController`nHiện tại: danh sách mẫu trong bộ nhớ" '#E9F5E6'
Arrow @(@(495,240),@(610,240)) 'HTTP' 500 192
Box 610 480 900 190 'SERVICE → REPOSITORY → JPA' "Service: mới có file khung`n3 repository kế thừa JpaRepository`n3 entity: LoaiMon, MonAn, NhanVien" '#FFF4D5'
Arrow @(@(1050,350),@(1050,480)) 'Dự kiến' 1075 390
Box 610 795 900 160 'ORACLE DATABASE • NHHS_APP' "JDBC Thin / ojdbc17 • OracleDialect`nHibernate ddl-auto=update • Sequence cấp mã" '#EFE9F8'
Arrow @(@(1050,670),@(1050,795)) 'Ánh xạ' 1080 710
Label 'Luồng đọc danh sách đang đi từ Controller đến Thymeleaf. Luồng nghiệp vụ qua Service/Repository là hướng hoàn thiện.' 65 415 475 440 28
Save 'architecture.png'

Canvas 1600 1050
Label 'NHÓM TÁC NHÂN VÀ CHỨC NĂNG THEO THIẾT KẾ' 35 25 1530 60 36 $true
$actors=@('ADMIN','QUẢN LÝ','THU NGÂN','PHỤC VỤ','THỦ KHO','BÁO CÁO')
$func=@('Quản trị tài khoản, cấu hình','Nhân viên, loại món, món ăn','Lập hóa đơn, thanh toán','Xem bàn, đặt bàn, gọi món','Nhập hàng, nguyên liệu','Xem thống kê, tổng hợp')
for($i=0;$i -lt 6;$i++) {
    $y=130+$i*140
    Box 55 $y 350 110 $actors[$i] '' '#E7F3FC'
    Box 610 $y 900 110 $func[$i] '' '#F3F7F9'
    Arrow @(@(405,($y+55)),@(610,($y+55)))
}
Label 'Sơ đồ phân rã chức năng ở mức bài tập lớn; phân quyền web chưa được cài đặt.' 45 990 1510 45 25
Save 'actors.png'

Canvas 1600 920
Label 'QUY TRÌNH NGHIỆP VỤ BÁN HÀNG • THIẾT KẾ' 35 25 1530 60 36 $true
Box 70 170 430 170 '1. TIẾP NHẬN' "Chọn bàn / đặt bàn`nGhi nhận khách hàng" '#E7F3FC'
Box 590 170 430 170 '2. GỌI MÓN' "Tạo hóa đơn mở`nGhi chi tiết món, số lượng" '#E9F5E6'
Box 1110 170 430 170 '3. TÍNH TIỀN' "Σ số lượng × đơn giá`nÁp dụng giảm giá hợp lệ" '#FFF4D5'
Arrow @(@(500,250),@(590,250))
Arrow @(@(1020,250),@(1110,250))
Box 1110 580 430 180 '4. THANH TOÁN' "Xác nhận thanh toán`nĐóng hóa đơn, trả bàn" '#EFE9F8'
Box 590 580 430 180 '5. BÁO CÁO' "Chỉ cộng hóa đơn đã trả`nNhóm doanh thu theo ngày" '#FBEAED'
Arrow @(@(1325,340),@(1325,580))
Arrow @(@(1110,670),@(1020,670))
Label 'Quy tắc: không sửa chứng từ đã thanh toán. Chưa mô hình hóa tồn kho theo định lượng món ăn.' 70 830 1470 65 26
Save 'workflow.png'
Write-Output 'Created 4 report diagrams.'
