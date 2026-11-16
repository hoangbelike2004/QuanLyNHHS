# Run after tao_powerpoint.py to apply the concise presentation content.
param(
    [string]$PresentationPath = (Join-Path $PSScriptRoot 'ThuyetTrinh_BTL_PTTKHDoiTuong_Nhom4.pptx')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression
$pUri = 'http://schemas.openxmlformats.org/presentationml/2006/main'
$aUri = 'http://schemas.openxmlformats.org/drawingml/2006/main'
$emu = 914400
$fullPath = [IO.Path]::GetFullPath($PresentationPath)
$temporaryPath = [IO.Path]::ChangeExtension($fullPath, '.updated.pptx')
[IO.File]::Copy($fullPath, $temporaryPath, $true)

function Read-ZipXml($archive, $entryName) {
    $entry = $archive.GetEntry($entryName)
    if ($null -eq $entry) { throw "Missing PPTX part: $entryName" }
    $reader = [IO.StreamReader]::new($entry.Open(), [Text.Encoding]::UTF8)
    try { return [xml]$reader.ReadToEnd() } finally { $reader.Dispose() }
}

function Write-ZipXml($archive, $entryName, [xml]$document) {
    $archive.GetEntry($entryName).Delete()
    $entry = $archive.CreateEntry($entryName, [IO.Compression.CompressionLevel]::Optimal)
    $stream = $entry.Open()
    $settings = [Xml.XmlWriterSettings]::new()
    $settings.Encoding = [Text.UTF8Encoding]::new($false)
    $settings.Indent = $false
    $writer = [Xml.XmlWriter]::Create($stream, $settings)
    try { $document.Save($writer) } finally { $writer.Dispose(); $stream.Dispose() }
}

function Namespace-Manager([xml]$document) {
    $manager = [Xml.XmlNamespaceManager]::new($document.NameTable)
    $manager.AddNamespace('p', $pUri)
    $manager.AddNamespace('a', $aUri)
    return ,$manager
}

function Shape-Text($shape, $manager) {
    return (($shape.SelectNodes('.//a:t', $manager) | ForEach-Object { $_.InnerText }) -join ' / ')
}

function Set-ShapeText($shape, $manager, [string]$value) {
    $nodes = @($shape.SelectNodes('.//a:t', $manager))
    if ($nodes.Count -eq 0) { throw "Shape has no text: $value" }
    $lines = $value.Split('|')
    $body = $shape.SelectSingleNode('./p:txBody', $manager)
    while ($nodes.Count -lt $lines.Count) {
        $paragraphs = @($body.SelectNodes('a:p', $manager))
        [void]$body.AppendChild($paragraphs[-1].CloneNode($true))
        $nodes = @($shape.SelectNodes('.//a:t', $manager))
    }
    for ($i = 0; $i -lt $nodes.Count; $i++) {
        $nodes[$i].InnerText = if ($i -lt $lines.Count) { $lines[$i].Trim() } else { '' }
    }
}

function Replace-Text([xml]$document, $manager, [string]$old, [string]$new) {
    foreach ($shape in $document.SelectNodes('//p:spTree/p:sp', $manager)) {
        if ((Shape-Text $shape $manager) -eq $old) {
            Set-ShapeText $shape $manager $new
            return
        }
    }
    foreach ($shape in $document.SelectNodes('//p:spTree/p:sp', $manager)) {
        if ((Shape-Text $shape $manager) -eq ($new.Replace('|', ' / '))) { return }
    }
    throw "Could not find slide text: $old"
}

function Set-Position($shape, $manager, [double]$x, [double]$y, [double]$w, [double]$h) {
    $transform = $shape.SelectSingleNode('./p:spPr/a:xfrm', $manager)
    if ($null -eq $transform) { throw 'Shape has no transform' }
    $off = $transform.SelectSingleNode('a:off', $manager)
    $ext = $transform.SelectSingleNode('a:ext', $manager)
    $off.SetAttribute('x', [string][int64][Math]::Round($x * $emu))
    $off.SetAttribute('y', [string][int64][Math]::Round($y * $emu))
    $ext.SetAttribute('cx', [string][int64][Math]::Round($w * $emu))
    $ext.SetAttribute('cy', [string][int64][Math]::Round($h * $emu))
}

function Clone-Shape([xml]$destination, $tree, $sourceShape, $manager,
                     [int]$id, [double]$x, [double]$y, [double]$w, [double]$h,
                     [string]$value = '') {
    $copy = $destination.ImportNode($sourceShape, $true)
    $identity = $copy.SelectSingleNode('./p:nvSpPr/p:cNvPr | ./p:nvCxnSpPr/p:cNvPr', $manager)
    $identity.SetAttribute('id', [string]$id)
    $identity.SetAttribute('name', "Overview $id")
    Set-Position $copy $manager $x $y $w $h
    if ($value) { Set-ShapeText $copy $manager $value }
    [void]$tree.AppendChild($copy)
}

function Add-Fade([xml]$document, $manager) {
    $root = $document.DocumentElement
    $old = $root.SelectSingleNode('p:transition', $manager)
    if ($null -ne $old) { [void]$root.RemoveChild($old) }
    $transition = $document.CreateElement('p', 'transition', $pUri)
    $transition.SetAttribute('spd', 'fast')
    $transition.SetAttribute('advClick', '1')
    [void]$transition.AppendChild($document.CreateElement('p', 'fade', $pUri))
    $anchor = $root.SelectSingleNode('p:clrMapOvr', $manager)
    if ($null -eq $anchor) { $anchor = $root.SelectSingleNode('p:cSld', $manager) }
    [void]$root.InsertAfter($transition, $anchor)
}

$notes = @(
    'Nhóm 4 trình bày hệ thống quản lý nhà hàng hải sản dưới góc nhìn phân tích và thiết kế hướng đối tượng.',
    'Phần khảo sát yêu cầu dựa trên tài liệu học phần, lược đồ dữ liệu và ứng dụng hiện có; chưa có khảo sát thực địa. Bốn nhu cầu chính là đặt bàn, bán hàng, nhập kho và báo cáo. Mục tiêu là quản lý tập trung, tính tiền và tồn kho đúng, đồng thời kiểm soát quyền.',
    'Use case tổng quát có bốn vai trò. Admin quản trị chung; thu ngân bán hàng; quản lý kho nhập hàng; kế toán theo dõi số liệu.',
    'Hóa đơn và phiếu nhập đều có lớp chi tiết. Lớp chi tiết giữ số lượng và đơn giá của từng dòng.',
    'Controller nhận yêu cầu, Service xử lý quy tắc, Repository đọc ghi dữ liệu, Oracle lưu trữ.',
    'Thu ngân chọn bàn và món. Hệ thống kiểm tra, lấy giá, tính tổng và lưu hóa đơn.',
    'Nghiệp vụ bắt đầu khi khách đặt bàn. Thu ngân ghi món và thu tiền; quản lý kho nhập nguyên liệu, còn kế toán theo dõi chứng từ và doanh thu. Admin quản trị tài khoản và các danh mục chung.',
    'Khi sửa phiếu nhập, tồn kho thay đổi theo chênh lệch giữa lượng mới và lượng cũ.',
    'Hóa đơn đi từ chưa thanh toán sang đã thanh toán. Thu ngân không giảm món đã xác nhận.',
    'Dự án dùng Java 27 và Spring Boot 4.1.1. Thymeleaf và JavaScript hiển thị giao diện; Spring Data JPA và Oracle JDBC làm việc với Oracle. Spring Security và Validation kiểm tra quyền, dữ liệu nhập. Maven xây dựng dự án; JUnit và H2 dùng cho kiểm thử.',
    'Đã xử lý quyền cho bốn vai trò, tính tiền và khóa sửa hóa đơn đã thu, điều chỉnh tồn khi sửa phiếu. Bộ Java có 23 bài đạt trên H2; bốn bài JavaScript về lọc giao diện đạt. Chưa chạy giao dịch trên Oracle thật; cần kiểm tra thao tác đồng thời.',
    'Hệ thống nối đặt bàn, bán hàng và nhập kho. Hướng tiếp theo là kiểm thử Oracle và các tình huống nhiều người cùng thao tác.'
)
$archive = [IO.Compression.ZipFile]::Open($temporaryPath, [IO.Compression.ZipArchiveMode]::Update)
try {
    $architecture = Read-ZipXml $archive 'ppt/slides/slide5.xml'
    $archNs = Namespace-Manager $architecture
    $archShapes = @($architecture.SelectNodes('//p:spTree/p:sp | //p:spTree/p:cxnSp', $archNs))

    for ($number = 1; $number -le 12; $number++) {
        $entryName = "ppt/slides/slide$number.xml"
        $document = Read-ZipXml $archive $entryName
        $ns = Namespace-Manager $document

        if ($number -eq 2) {
            $tree = $document.SelectSingleNode('//p:spTree', $ns)
            $shapes = @($tree.SelectNodes('p:sp | p:cxnSp', $ns))
            for ($i = 6; $i -lt $shapes.Count; $i++) { [void]$tree.RemoveChild($shapes[$i]) }
            Set-ShapeText $shapes[1] $ns 'Khảo sát và yêu cầu'
            Set-ShapeText $shapes[5] $ns 'Bối cảnh bài tập lớn; chưa khảo sát thực địa.'

            Clone-Shape $document $tree $archShapes[6] $ns 100 .80 1.55 11.9 .44 'Nguồn: tài liệu môn học, lược đồ dữ liệu, dự án hiện có'
            $cards = @(
                @(.70, 'Đặt bàn', 'Khách và bàn'),
                @(3.82, 'Bán hàng', 'Món và hóa đơn'),
                @(6.94, 'Nhập kho', 'Phiếu và tồn'),
                @(10.06, 'Báo cáo', 'Doanh thu')
            )
            $id = 101
            foreach ($card in $cards) {
                Clone-Shape $document $tree $archShapes[7] $ns $id $card[0] 2.35 2.57 2.18; $id++
                Clone-Shape $document $tree $archShapes[8] $ns $id ($card[0]+.25) 2.63 2.07 .60 $card[1]; $id++
                Clone-Shape $document $tree $archShapes[9] $ns $id ($card[0]+.25) 3.38 2.07 .64 $card[2]; $id++
            }
            foreach ($x in @(3.27, 6.39, 9.51)) {
                Clone-Shape $document $tree $archShapes[19] $ns $id $x 3.42 .52 0; $id++
            }
            Clone-Shape $document $tree $archShapes[22] $ns $id .85 5.35 11.8 .42 'Vấn đề: thông tin rời rạc, dễ lệch tiền và tồn'; $id++
            Clone-Shape $document $tree $archShapes[23] $ns $id .85 6.03 11.8 .42 'Yêu cầu: dữ liệu tập trung, số liệu đúng, quyền rõ ràng'
        }

        if ($number -eq 3) {
            $changes = [ordered]@{
                'Actor nghiệp vụ trong thiết kế' = 'Use case tổng quát'
                'Phục vụ' = 'Admin'
                'Thủ kho' = 'Quản lý kho'
                'Quản lý' = 'Kế toán'
                'Tiếp nhận đặt bàn' = 'Quản lý tài khoản'
                'Lập hóa đơn' = 'Đặt bàn'
                'Thanh toán' = 'Lập và thanh toán'
                'Lập, điều chỉnh phiếu nhập' = 'Quản lý phiếu nhập'
                'Quản lý thực đơn' = 'Xem chứng từ'
                'Quản lý cấp tài khoản; quyền web gồm 3 vai trò.' = 'Admin có thể truy cập mọi chức năng.'
            }
            foreach ($old in $changes.Keys) { Replace-Text $document $ns $old $changes[$old] }
        }
        if ($number -eq 4) { Replace-Text $document $ns 'Mô hình khái niệm: các đối tượng chính' 'Các đối tượng chính' }
        if ($number -eq 5) { Replace-Text $document $ns 'Kiến trúc và phân chia trách nhiệm' 'Kiến trúc hệ thống' }
        if ($number -eq 6) { Replace-Text $document $ns 'UC04 — Lập hóa đơn' 'Quy trình lập hóa đơn' }
        if ($number -eq 7) {
            $tree = $document.SelectSingleNode('//p:spTree', $ns)
            $shapes = @($tree.SelectNodes('p:sp | p:cxnSp', $ns))
            for ($i = 6; $i -lt $shapes.Count; $i++) { [void]$tree.RemoveChild($shapes[$i]) }
            Set-ShapeText $shapes[1] $ns 'Mô tả nghiệp vụ'
            Set-ShapeText $shapes[5] $ns 'Tóm tắt ba luồng công việc chính.'
            $business = @(
                @(.75, 'Đặt bàn', 'Nhận yêu cầu|Chọn bàn trống|Xác nhận hoặc hủy'),
                @(4.87, 'Bán hàng', 'Ghi món theo bàn|Tính tiền từ giá bán|Thu tiền, trả bàn'),
                @(8.99, 'Nhập kho', 'Nhập hàng theo phiếu|Cập nhật tồn kho|Sửa theo chênh lệch')
            )
            $id = 200
            foreach ($card in $business) {
                Clone-Shape $document $tree $archShapes[7] $ns $id $card[0] 1.90 3.58 3.92; $id++
                Clone-Shape $document $tree $archShapes[8] $ns $id ($card[0]+.23) 2.22 3.12 .58 $card[1]; $id++
                Clone-Shape $document $tree $archShapes[9] $ns $id ($card[0]+.23) 3.10 3.12 2.13 $card[2]; $id++
            }
            Clone-Shape $document $tree $archShapes[23] $ns $id .85 6.17 11.8 .42 'Kế toán xem chứng từ, doanh thu; Admin quản trị tài khoản và danh mục'
        }
        if ($number -eq 8) { Replace-Text $document $ns 'UC06 — Điều chỉnh phiếu nhập và tồn kho' 'Sửa phiếu nhập, cập nhật tồn kho' }
        if ($number -eq 9) {
            Replace-Text $document $ns 'Vòng đời hóa đơn' 'Trạng thái hóa đơn'
            Replace-Text $document $ns 'Sửa theo vai trò' 'Sửa khi còn mở'
            Replace-Text $document $ns 'Món đã xác nhận: nhân viên không được trừ.' 'Thu ngân không giảm món đã xác nhận.'
        }
        if ($number -eq 10) {
            $tree = $document.SelectSingleNode('//p:spTree', $ns)
            $shapes = @($tree.SelectNodes('p:sp | p:cxnSp', $ns))
            for ($i = 6; $i -lt $shapes.Count; $i++) { [void]$tree.RemoveChild($shapes[$i]) }
            Set-ShapeText $shapes[1] $ns 'Công cụ và dữ liệu'
            Set-ShapeText $shapes[5] $ns 'Đối chiếu pom.xml và cấu hình dự án.'
            $technology = @(
                @(.65, 1.74, 'Java + Spring Boot', 'Xử lý nghiệp vụ'),
                @(4.78, 1.74, 'Thymeleaf + JS', 'Giao diện web'),
                @(8.91, 1.74, 'Oracle + JDBC', 'Lưu dữ liệu chính'),
                @(.65, 3.93, 'Spring Data JPA', 'Kết nối và truy vấn'),
                @(4.78, 3.93, 'Spring Security', 'Đăng nhập, phân quyền'),
                @(8.91, 3.93, 'Maven + JUnit + H2', 'Xây dựng, kiểm thử')
            )
            $id = 300
            foreach ($card in $technology) {
                Clone-Shape $document $tree $archShapes[7] $ns $id $card[0] $card[1] 3.78 1.75; $id++
                Clone-Shape $document $tree $archShapes[8] $ns $id ($card[0]+.22) ($card[1]+.20) 3.34 .55 $card[2]; $id++
                Clone-Shape $document $tree $archShapes[9] $ns $id ($card[0]+.22) ($card[1]+.99) 3.34 .50 $card[3]; $id++
            }
            Clone-Shape $document $tree $archShapes[23] $ns $id .75 6.14 11.9 .42 'Dữ liệu: khách, bàn, món, hóa đơn, phiếu nhập, nguyên liệu'
        }
        if ($number -eq 11) {
            Replace-Text $document $ns 'Kết quả và giới hạn' 'Đã xử lý và kiểm thử'
            Replace-Text $document $ns 'Đã kiểm chứng' 'Đã xử lý'
            Replace-Text $document $ns 'Mô hình khớp / mã nguồn.' 'Phân quyền theo|bốn vai trò.'
            Replace-Text $document $ns '4/4 kiểm thử JavaScript đạt.' 'Tính tiền và khóa đơn đã thu.'
            Replace-Text $document $ns '23/23 kiểm thử Java đạt.' 'Sửa phiếu, cập nhật tồn.'
            Replace-Text $document $ns 'Giới hạn hiện tại' 'Đã kiểm thử'
            Replace-Text $document $ns 'Tài khoản chưa gắn / hồ sơ nhân viên.' '23 bài Java|trên H2 đạt.'
            Replace-Text $document $ns 'Cần kiểm thử xử lý / đồng thời hóa đơn.' '4 bài lọc giao diện|bằng JavaScript đạt.'
            Replace-Text $document $ns 'Chưa trừ nguyên liệu / khi bán món.' 'Đăng nhập, quyền,|thêm/sửa/xóa.'
            $tree = $document.SelectSingleNode('//p:spTree', $ns)
            $found = $false
            foreach ($shape in $tree.SelectNodes('p:sp', $ns)) {
                if ((Shape-Text $shape $ns) -eq 'Cần thử trên Oracle thật và khi nhiều người thao tác cùng lúc.') { $found = $true }
            }
            if (-not $found) { Clone-Shape $document $tree $archShapes[23] $ns 400 .80 6.43 11.9 .38 'Cần thử trên Oracle thật và khi nhiều người thao tác cùng lúc.' }
        }
        if ($number -eq 12) {
            Replace-Text $document $ns 'Kết luận và hướng hoàn thiện' 'Kết luận'
            Replace-Text $document $ns 'Nối kết đặt bàn, hóa đơn và nhập kho / trong một hệ thống quản lý.' 'Đặt bàn, bán hàng và nhập kho|trên cùng một hệ thống.'
            Replace-Text $document $ns 'Trách nhiệm nghiệp vụ tập trung tại Service.' 'Mỗi lớp có nhiệm vụ rõ.'
            Replace-Text $document $ns 'Kiểm thử tiếp các giao dịch trên Oracle.' 'Tiếp tục kiểm thử trên Oracle.'
        }
        Add-Fade $document $ns
        Write-ZipXml $archive $entryName $document

        $noteName = "ppt/notesSlides/notesSlide$number.xml"
        $noteDocument = Read-ZipXml $archive $noteName
        $noteNs = Namespace-Manager $noteDocument
        $body = $null
        foreach ($shape in $noteDocument.SelectNodes('//p:spTree/p:sp', $noteNs)) {
            $placeholder = $shape.SelectSingleNode('./p:nvSpPr/p:nvPr/p:ph', $noteNs)
            if ($null -ne $placeholder -and $placeholder.GetAttribute('type') -eq 'body') { $body = $shape; break }
        }
        if ($null -eq $body) { throw "Notes body missing on slide $number" }
        Set-ShapeText $body $noteNs $notes[$number - 1]
        Write-ZipXml $archive $noteName $noteDocument
    }
} finally { $archive.Dispose() }

# Reopen the resulting package before replacing the delivered presentation.
$check = [IO.Compression.ZipFile]::OpenRead($temporaryPath)
try {
    for ($number = 1; $number -le 12; $number++) {
        $document = Read-ZipXml $check "ppt/slides/slide$number.xml"
        $ns = Namespace-Manager $document
        if ($document.SelectNodes('//p:transition/p:fade', $ns).Count -ne 1) {
            throw "Fade transition missing on slide $number"
        }
    }
} finally { $check.Dispose() }
$backupPath = "$fullPath.backup"
$deliveredPath = $fullPath
try {
    [IO.File]::Replace($temporaryPath, $fullPath, $backupPath)
    [IO.File]::Delete($backupPath)
} catch [IO.IOException] {
    # PowerPoint may hold the original open. Keep the edited copy available.
    $deliveredPath = [IO.Path]::Combine([IO.Path]::GetDirectoryName($fullPath),
        [IO.Path]::GetFileNameWithoutExtension($fullPath) + '_cap_nhat.pptx')
    [IO.File]::Copy($temporaryPath, $deliveredPath, $true)
    [IO.File]::Delete($temporaryPath)
}

Write-Output "Updated 12 slides with survey, business process, tools, tests and fade transitions: $deliveredPath"
