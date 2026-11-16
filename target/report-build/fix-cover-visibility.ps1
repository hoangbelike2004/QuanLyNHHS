$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'
function ReadXml($z,$name){$s=$z.GetEntry($name).Open();$x=[xml]::new();try{$x.Load($s)}finally{$s.Dispose()};return $x}
function WriteXml($z,$name,$x){$e=$z.GetEntry($name);$e.Delete();$s=$z.CreateEntry($name).Open();$settings=[Xml.XmlWriterSettings]::new();$settings.Encoding=[Text.UTF8Encoding]::new($false);$writer=[Xml.XmlWriter]::Create($s,$settings);try{$x.Save($writer)}finally{$writer.Dispose();$s.Dispose()}}
foreach($file in @('BTL.docx','BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx')){
 $path=Join-Path $root $file
 $work=Join-Path $PSScriptRoot ([IO.Path]::GetFileNameWithoutExtension($path)+'.visible.docx')
 Copy-Item -LiteralPath $path -Destination $work -Force
 $z=[IO.Compression.ZipFile]::Open($work,[IO.Compression.ZipArchiveMode]::Update)
 try{
  $x=ReadXml $z 'word/document.xml';$m=[Xml.XmlNamespaceManager]::new($x.NameTable);$m.AddNamespace('w',$w)
  $body=$x.SelectSingleNode('/w:document/w:body',$m)
  $cover=$body.SelectSingleNode('./w:p[1]//w:txbxContent',$m)
  if(-not $cover){throw "Expected the old floating cover in $file"}
  $old=@($body.ChildNodes)
  $coverParagraphs=@($cover.ChildNodes|ForEach-Object {$_.CloneNode($true)})
  $tail=($old|Select-Object -Skip 2|ForEach-Object OuterXml)-join ''
  [void]$body.RemoveChild($old[0]);[void]$body.RemoveChild($old[1])
  $frag=$x.CreateDocumentFragment()
  $frag.InnerXml=@"
<w:tbl xmlns:w="$w"><w:tblPr><w:tblW w:w="9071" w:type="dxa"/><w:jc w:val="center"/><w:tblBorders><w:top w:val="double" w:sz="12" w:color="000000"/><w:left w:val="double" w:sz="12" w:color="000000"/><w:bottom w:val="double" w:sz="12" w:color="000000"/><w:right w:val="double" w:sz="12" w:color="000000"/></w:tblBorders><w:tblLayout w:type="fixed"/><w:tblCellMar><w:top w:w="200" w:type="dxa"/><w:left w:w="240" w:type="dxa"/><w:bottom w:w="160" w:type="dxa"/><w:right w:w="240" w:type="dxa"/></w:tblCellMar></w:tblPr><w:tblGrid><w:gridCol w:w="9071"/></w:tblGrid><w:tr><w:trPr><w:cantSplit/><w:trHeight w:val="14400" w:hRule="atLeast"/></w:trPr><w:tc><w:tcPr><w:tcW w:w="9071" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr></w:tc></w:tr></w:tbl>
"@
  $table=$frag.FirstChild;$cell=$table.SelectSingleNode('./w:tr/w:tc',$m)
  foreach($p in $coverParagraphs){
   foreach($color in @($p.SelectNodes('.//w:color',$m))){$color.RemoveAll();$color.SetAttribute('val',$w,'000000')|Out-Null}
   [void]$cell.AppendChild($p)
  }
  [void]$body.PrependChild($table)
  $end=$x.CreateElement('w','p',$w)
  $pr=$x.CreateElement('w','pPr',$w)
  $frag=$x.CreateDocumentFragment();$frag.InnerXml="<w:spacing xmlns:w='$w' w:before='0' w:after='0' w:line='20' w:lineRule='exact'/>";[void]$pr.AppendChild($frag.FirstChild)
  $section=$body.SelectSingleNode('./w:sectPr',$m).CloneNode($true)
  foreach($node in @($section.SelectNodes('./w:headerReference|./w:footerReference|./w:type|./w:pgNumType|./w:titlePg',$m))){[void]$section.RemoveChild($node)}
  $type=$x.CreateElement('w','type',$w);$type.SetAttribute('val',$w,'nextPage')|Out-Null;[void]$section.PrependChild($type)
  $margin=$section.SelectSingleNode('./w:pgMar',$m)
  foreach($key in @('top','bottom')){$margin.SetAttribute($key,$w,'850')|Out-Null}
  foreach($key in @('left','right')){$margin.SetAttribute($key,$w,'1417')|Out-Null}
  [void]$pr.AppendChild($section);[void]$end.AppendChild($pr);[void]$body.InsertAfter($end,$table)
  $newTail=(@($body.ChildNodes)|Select-Object -Skip 2|ForEach-Object OuterXml)-join ''
  if($tail -cne $newTail){throw 'Content beyond cover was modified'}
  WriteXml $z 'word/document.xml' $x
 }finally{$z.Dispose()}
 Copy-Item -LiteralPath $work -Destination $path -Force
 Write-Output "Converted floating cover to normal Word content: $file"
}
