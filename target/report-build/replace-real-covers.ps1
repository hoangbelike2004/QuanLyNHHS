$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'
$v='urn:schemas-microsoft-com:vml'
$r='http://schemas.openxmlformats.org/officeDocument/2006/relationships'
function ReadXml($z,$name){$s=$z.GetEntry($name).Open();$x=[xml]::new();try{$x.Load($s)}finally{$s.Dispose()};return $x}
function WriteXml($z,$name,$x){$e=$z.GetEntry($name);if($e){$e.Delete()};$e=$z.CreateEntry($name);$s=$e.Open();$settings=[Xml.XmlWriterSettings]::new();$settings.Encoding=[Text.UTF8Encoding]::new($false);$wr=[Xml.XmlWriter]::Create($s,$settings);try{$x.Save($wr)}finally{$wr.Dispose();$s.Dispose()}}
function Manager($x){$ns=[Xml.XmlNamespaceManager]::new($x.NameTable);$ns.AddNamespace('w',$w);$ns.AddNamespace('v',$v);$ns.AddNamespace('r',$r);return ,$ns}
function Para($x,$text,$size=14,$before=0,$after=0,$align='center'){
 $esc=[Security.SecurityElement]::Escape($text)
 $frag=$x.CreateDocumentFragment()
 $frag.InnerXml="<w:p xmlns:w='$w'><w:pPr><w:pStyle w:val='Normal'/><w:keepNext w:val='0'/><w:keepLines/><w:spacing w:before='$($before*20)' w:after='$($after*20)' w:line='280' w:lineRule='auto'/><w:ind w:left='0' w:right='0' w:firstLine='0'/><w:jc w:val='$align'/></w:pPr><w:r><w:rPr><w:rFonts w:ascii='Times New Roman' w:hAnsi='Times New Roman' w:eastAsia='Times New Roman'/><w:b/><w:color w:val='000000'/><w:sz w:val='$($size*2)'/><w:szCs w:val='$($size*2)'/></w:rPr><w:t xml:space='preserve'>$esc</w:t></w:r></w:p>"
 return $frag.FirstChild
}
$templatePath=Join-Path $PSScriptRoot 'mau-bia-2024.docx'
if(-not (Test-Path -LiteralPath $templatePath)){
 $word=New-Object -ComObject Word.Application
 try{
  $word.Visible=$false;$word.DisplayAlerts=0
  $document=$word.Documents.Open('C:\Users\hoang\Downloads\Huong dan lam do an TN_Ver2024.doc',$false,$true)
  $document.SaveAs2($templatePath,16);$document.Close(0)
 }finally{$word.Quit()}
}
$template=[IO.Compression.ZipFile]::OpenRead($templatePath)
try{
 $src=ReadXml $template 'word/document.xml';$ns=Manager $src
 $frame=$src.SelectSingleNode('//v:shape[@id="_x0000_s2085"]',$ns)
 $type=$src.SelectSingleNode('//v:shapetype[@id="_x0000_t202"]',$ns)
 $logo=$frame.SelectSingleNode('.//w:p[.//v:imagedata]',$ns)
 foreach($file in @('BTL.docx','BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx')){
  $path=Join-Path $root $file
  $work=Join-Path $PSScriptRoot ([IO.Path]::GetFileNameWithoutExtension($path)+'.real-cover.docx')
  Copy-Item -LiteralPath $path -Destination $work -Force
  $z=[IO.Compression.ZipFile]::Open($work,[IO.Compression.ZipArchiveMode]::Update)
  try{
   $x=ReadXml $z 'word/document.xml';$m=Manager $x;$body=$x.SelectSingleNode('/w:document/w:body',$m)
   $old=@($body.ChildNodes);$end=-1
   if($old[0].LocalName -eq 'tbl' -and $old[1].SelectSingleNode('./w:pPr/w:sectPr',$m)){$end=1}
   else{for($i=0;$i -lt $old.Count;$i++){if($old[$i].SelectSingleNode('.//w:br[@w:type="page"]',$m)){$end=$i;break}}}
   if($end -lt 0 -or $end -gt 20){throw 'Cannot identify cover boundary'}
   $originalTail=($old|Select-Object -Skip ($end+1)|ForEach-Object OuterXml)-join ''
   for($i=0;$i -le $end;$i++){[void]$body.RemoveChild($old[$i])}
   $anchor=Para $x '' 1
   $pr=$anchor.SelectSingleNode('./w:pPr',$m);$pr.SelectSingleNode('./w:spacing',$m).SetAttribute('line',$w,'20')|Out-Null
   $run=$x.CreateElement('w','r',$w);$pict=$x.CreateElement('w','pict',$w)
   [void]$pict.AppendChild($x.ImportNode($type,$true));$shape=$x.ImportNode($frame,$true)
   $shape.RemoveAttribute('gfxdata','urn:schemas-microsoft-com:office:office')
   $shape.SetAttribute('style','position:absolute;left:0;text-align:left;margin-left:70.85pt;margin-top:42.5pt;width:453.55pt;height:756pt;z-index:251658240;visibility:visible;mso-position-horizontal-relative:page;mso-position-vertical-relative:page')
   $wrap=$x.CreateElement('w10','wrap','urn:schemas-microsoft-com:office:word');$wrap.SetAttribute('type','none');[void]$shape.AppendChild($wrap)
   $tb=$shape.SelectSingleNode('./v:textbox',$m);$tb.SetAttribute('inset','12pt,10pt,12pt,8pt')
   $box=$tb.SelectSingleNode('./w:txbxContent',$m);$box.RemoveAll()
   [void]$box.AppendChild((Para $x 'TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI' 14 0 4))
   [void]$box.AppendChild((Para $x 'KHOA CÔNG NGHỆ THÔNG TIN' 14 0 3))
   [void]$box.AppendChild((Para $x '────────  ✧  ────────' 13 0 20))
   $lp=$x.ImportNode($logo,$true);$lp.SelectSingleNode('.//v:imagedata',$m).SetAttribute('id',$r,'rIdCoverTemplateLogo')|Out-Null
   $lpr=$lp.SelectSingleNode('./w:pPr',$m);$spacing=$x.CreateElement('w','spacing',$w);$spacing.SetAttribute('after',$w,'560')|Out-Null;[void]$lpr.AppendChild($spacing)
   [void]$box.AppendChild($lp)
   [void]$box.AppendChild((Para $x 'BÁO CÁO BÀI TẬP LỚN' 23 0 12))
   $course=if($file -eq 'BTL.docx'){'Môn học: ................................................'}else{'PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG'}
   [void]$box.AppendChild((Para $x $course 13 0 15))
   [void]$box.AppendChild((Para $x 'ĐỀ TÀI' 14 0 8))
   [void]$box.AppendChild((Para $x 'HỆ THỐNG QUẢN LÝ' 22 0 2))
   [void]$box.AppendChild((Para $x 'NHÀ HÀNG HẢI SẢN' 22 0 22))
   [void]$box.AppendChild((Para $x 'Giảng viên hướng dẫn: ........................................' 14 0 8 'left'))
   [void]$box.AppendChild((Para $x 'Sinh viên thực hiện: NHÓM 4' 14 0 6 'left'))
   foreach($name in @('Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt')){[void]$box.AppendChild((Para $x ('     '+$name+'       MSSV: ........................') 13 0 2 'left'))}
   [void]$box.AppendChild((Para $x 'Lớp: ........................    Khóa: ........................' 14 8 0 'left'))
   [void]$box.AppendChild((Para $x 'Hà Nội, năm 2026' 13 45 0))
   [void]$pict.AppendChild($shape);[void]$run.AppendChild($pict);[void]$anchor.AppendChild($run)
   [void]$body.PrependChild($anchor)
   $break=Para $x '' 1;$brRun=$x.CreateElement('w','r',$w);$br=$x.CreateElement('w','br',$w);$br.SetAttribute('type',$w,'page')|Out-Null;[void]$brRun.AppendChild($br);[void]$break.AppendChild($brRun);[void]$body.InsertAfter($break,$anchor)
   $tail=(@($body.ChildNodes)|Select-Object -Skip 2|ForEach-Object OuterXml)-join ''
   if($tail -cne $originalTail){throw 'Body changed outside cover'}
   $rels=ReadXml $z 'word/_rels/document.xml.rels'
   foreach($existing in @($rels.DocumentElement.ChildNodes|Where-Object {$_.GetAttribute('Id') -eq 'rIdCoverTemplateLogo'})){[void]$rels.DocumentElement.RemoveChild($existing)}
   $rel=$rels.CreateElement('Relationship',$rels.DocumentElement.NamespaceURI);$rel.SetAttribute('Id','rIdCoverTemplateLogo');$rel.SetAttribute('Type',"$r/image");$rel.SetAttribute('Target','media/cover-template-logo.jpeg');[void]$rels.DocumentElement.AppendChild($rel)
   $oldImage=$z.GetEntry('word/media/cover-template-logo.jpeg');if($oldImage){$oldImage.Delete()}
   $e=$z.CreateEntry('word/media/cover-template-logo.jpeg');$to=$e.Open();$from=$template.GetEntry('word/media/image1.jpeg').Open();$from.CopyTo($to);$from.Dispose();$to.Dispose()
   $ct=ReadXml $z '[Content_Types].xml';if(-not @($ct.DocumentElement.ChildNodes|Where-Object { $_.LocalName -eq 'Default' -and $_.GetAttribute('Extension') -eq 'jpeg'}).Count){$def=$ct.CreateElement('Default',$ct.DocumentElement.NamespaceURI);$def.SetAttribute('Extension','jpeg');$def.SetAttribute('ContentType','image/jpeg');[void]$ct.DocumentElement.AppendChild($def)}
   WriteXml $z '[Content_Types].xml' $ct;WriteXml $z 'word/document.xml' $x;WriteXml $z 'word/_rels/document.xml.rels' $rels
  }finally{$z.Dispose()}
  Copy-Item -LiteralPath $work -Destination $path -Force
  Write-Output "Replaced cover, body preserved: $file"
 }
}finally{$template.Dispose()}
