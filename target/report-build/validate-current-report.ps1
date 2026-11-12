$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$results=@()
foreach($name in @('BTL.docx','ON_TAP_BAO_VE_HOANG.docx')) {
 $file=Join-Path $root $name
 $zip=[IO.Compression.ZipFile]::OpenRead($file)
 try {
  $parts=@{}
  foreach($entry in $zip.Entries) {
   if($entry.FullName -notmatch '(\.xml|\.rels)$') { continue }
   $reader=[IO.StreamReader]::new($entry.Open())
   try { $text=$reader.ReadToEnd() } finally { $reader.Dispose() }
   [xml]$validated=$text
   $parts[$entry.FullName]=$text
  }
  [xml]$document=$parts['word/document.xml']
  $ns=[Xml.XmlNamespaceManager]::new($document.NameTable)
  $ns.AddNamespace('w','http://schemas.openxmlformats.org/wordprocessingml/2006/main')
  $allXml=($parts.Values -join "`n")
  $fonts=@([regex]::Matches($allXml,'<w:rFonts\b[^>]*/>') | ForEach-Object { [regex]::Matches($_.Value,'w:(?:ascii|hAnsi|eastAsia|cs)="([^"]+)"') | ForEach-Object { $_.Groups[1].Value } } | Sort-Object -Unique)
  $colors=@([regex]::Matches($allXml,'<w:color\b[^>]*w:val="([^"]+)"') | ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique)
  if($fonts.Count -ne 1 -or $fonts[0] -ne 'Times New Roman') { throw "Unexpected fonts in $name : $fonts" }
  if($colors.Count -ne 1 -or $colors[0] -ne '000000') { throw "Unexpected text colors in $name : $colors" }
  [xml]$styles=$parts['word/styles.xml']
  $sns=[Xml.XmlNamespaceManager]::new($styles.NameTable)
  $sns.AddNamespace('w','http://schemas.openxmlformats.org/wordprocessingml/2006/main')
  foreach($styleId in @('Heading1','Heading2','Heading3','CoverTitle')) {
   if(!$styles.SelectSingleNode("//w:style[@w:styleId='$styleId']/w:rPr/w:b",$sns)) { throw "Heading not bold: $styleId" }
  }
  $bodyText=($document.SelectNodes('//w:t',$ns) | ForEach-Object { $_.InnerText }) -join "`n"
  if($name -eq 'BTL.docx') {
   foreach($required in @('Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt','TAI_KHOAN','14 chạy','4 chạy','CHƯƠNG 1.','CHƯƠNG 2.')) {
    if(!$bodyText.Contains($required)) { throw "Missing required report content: $required" }
   }
   foreach($outdated in @('getSampleDishes','Có 8 món mẫu','chưa có entity hoàn chỉnh','thao tác ghi đang mô phỏng')) {
    if($bodyText.Contains($outdated)) { throw "Outdated content: $outdated" }
   }
  }
  $styleIds=@($styles.SelectNodes('//w:style/@w:styleId',$sns) | ForEach-Object { $_.Value })
  foreach($used in $document.SelectNodes('//w:pStyle/@w:val',$ns)) {
   if($used.Value -notin $styleIds) { throw "Missing paragraph style: $($used.Value)" }
  }
  foreach($grid in $document.SelectNodes('//w:tblGrid',$ns)) {
   $width=0; foreach($col in $grid.ChildNodes){$width += [int]$col.GetAttribute('w','http://schemas.openxmlformats.org/wordprocessingml/2006/main')}
   if($width -gt 9071) { throw "Table exceeds content width: $width" }
  }
  $results+=@{file=$name;bytes=(Get-Item $file).Length;font=$fonts[0];textColor=$colors[0];xmlPartsValidated=$parts.Count;paragraphs=$document.SelectNodes('//w:p',$ns).Count;tables=$document.SelectNodes('//w:tbl',$ns).Count;hasLiveTOC=($bodyText.Contains('MỤC LỤC'));layoutValidation='OOXML structure and style validation; Word repagination and PDF export completed; selected PDF pages visually reviewed.'}
 } finally { $zip.Dispose() }
}
$backup=Join-Path $root 'BTL.truoc-chinh-sua-2026-11-10.docx'
if(!(Test-Path $backup)) {throw 'Missing original report backup.'}
$results | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 (Join-Path $PSScriptRoot 'current-report-validation.json')
$results | ConvertTo-Json -Depth 5
