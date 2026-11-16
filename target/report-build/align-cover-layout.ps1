$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.Drawing
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$word=New-Object -ComObject Word.Application
try{
 $word.Visible=$false;$word.DisplayAlerts=0
 foreach($file in @('BTL.docx','BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx')){
  $path=Join-Path $root $file
  $work=Join-Path $PSScriptRoot ([IO.Path]::GetFileNameWithoutExtension($path)+'.aligned.docx')
  Copy-Item -LiteralPath $path -Destination $work -Force
  Write-Output "Opening local work copy: $file"
  $doc=$word.Documents.Open($work,$false,$false)
  try{
   $table=$doc.Tables.Item(1)
   $table.LeftPadding=18;$table.RightPadding=18
   $table.Range.Font.Color=0
   $members=@('Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt')
   $paragraphCount=$table.Range.Paragraphs.Count
   for($index=1;$index -le $paragraphCount;$index++){
    $paragraph=$table.Range.Paragraphs.Item($index)
    $range=$paragraph.Range
    $text=$range.Text.Trim([char]13,[char]7,[char]32)
    $fmt=$range.ParagraphFormat
    $fmt.LeftIndent=0;$fmt.RightIndent=0;$fmt.FirstLineIndent=0
    $fmt.TabStops.ClearAll()
    $fmt.Alignment=1
    if($text -match '^Giảng viên hướng dẫn:|^Sinh viên thực hiện:'){
     $fmt.Alignment=0;$fmt.LeftIndent=18
    }
    foreach($name in $members){
     if($text.StartsWith($name)){
      $originalValue=$text.Substring($name.Length).Trim()
      $range.Text=$name+"`t"+$originalValue+"`r"
      $range.Font.Size=13
      $fmt=$range.ParagraphFormat
      $fmt.Alignment=0;$fmt.LeftIndent=36;$fmt.FirstLineIndent=0;$fmt.RightIndent=0
      $fmt.SpaceBefore=0;$fmt.SpaceAfter=2
      $fmt.TabStops.ClearAll();[void]$fmt.TabStops.Add(205,0,0)
     }
    }
    if($text.StartsWith('Lớp:')){
     $parts=$text -split '\s+Khóa:'
     if($parts.Count -eq 2){$range.Text=$parts[0].Trim()+"`tKhóa: "+$parts[1].Trim()+"`r"}
     $fmt=$range.ParagraphFormat;$fmt.Alignment=0;$fmt.LeftIndent=18;$fmt.FirstLineIndent=0;$fmt.RightIndent=0
     $fmt.SpaceBefore=8;$fmt.SpaceAfter=0
     $fmt.TabStops.ClearAll();[void]$fmt.TabStops.Add(205,0,0)
    }
    if($range.InlineShapes.Count -gt 0){
     $fmt.LeftIndent=0;$fmt.FirstLineIndent=0;$fmt.RightIndent=0;$fmt.Alignment=1
     $fmt.LineSpacingRule=0
     foreach($image in $range.InlineShapes){
      $image.PictureFormat.TransparencyColor=16777215
      $image.PictureFormat.TransparentBackground=-1
      $image.LockAspectRatio=-1;$image.Width=126
     }
    }
   }
   $doc.Repaginate()
   if($table.Range.Information(3) -ne 1){throw "Cover overflow: $file"}
   Write-Output "Saving cover: $file"
   $doc.Save()
   $doc.ExportAsFixedFormat([IO.Path]::ChangeExtension($path,'.pdf'),17)
   $page=$doc.Range(0,$doc.GoTo(1,1,2).Start)
   $ms=[IO.MemoryStream]::new([byte[]]$page.EnhMetaFileBits)
   $mf=[Drawing.Imaging.Metafile]::new($ms)
   $bmp=[Drawing.Bitmap]::new(1190,1684);$graphics=[Drawing.Graphics]::FromImage($bmp)
   $graphics.Clear([Drawing.Color]::White);$graphics.DrawImage($mf,0,0,1190,1684)
   $preview=if($file -eq 'BTL.docx'){'bia-BTL-moi.png'}else{'bia-PTTK-moi.png'}
   $bmp.Save((Join-Path $PSScriptRoot $preview),[Drawing.Imaging.ImageFormat]::Png)
   $graphics.Dispose();$bmp.Dispose();$mf.Dispose();$ms.Dispose()
   Write-Output "Aligned and exported: $file"
  }finally{$doc.Close(0)}
  Copy-Item -LiteralPath $work -Destination $path -Force
 }
}finally{$word.Quit()}
