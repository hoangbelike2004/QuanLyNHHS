$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.Drawing
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$word=New-Object -ComObject Word.Application
$validation=@()
try{
 $word.Visible=$false;$word.DisplayAlerts=0
 foreach($file in @('BTL.docx','BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx')){
  $path=Join-Path $root $file
  $work=Join-Path $PSScriptRoot ([IO.Path]::GetFileNameWithoutExtension($path)+'.pagination.docx')
  Copy-Item -LiteralPath $path -Destination $work -Force
  $doc=$word.Documents.Open($work,$false,$false)
  try{
   $doc.Activate();$doc.ActiveWindow.View.Type=3
   $section=$doc.Sections.Item($doc.Sections.Count)
   $width=$section.PageSetup.PageWidth-$section.PageSetup.LeftMargin-$section.PageSetup.RightMargin
   # Align existing header labels using a right tab, preserving their text.
   foreach($header in $section.Headers){
    if($header.Exists -and $header.Range.Text.Trim().Length -gt 0){
     $range=$header.Range.Duplicate
     $text=$range.Text.Trim([char]13,[char]7,[char]32)
     $match=[regex]::Match($text,'^(.*?)\s+(NHÓM\s*4)\s*$')
     if($match.Success){$range.Text=$match.Groups[1].Value.Trim()+"`t"+$match.Groups[2].Value+"`r"}
     $format=$header.Range.ParagraphFormat
     $format.Alignment=0;$format.LeftIndent=0;$format.RightIndent=0;$format.FirstLineIndent=0
     $format.TabStops.ClearAll();[void]$format.TabStops.Add($width,2,0)
     $header.Range.Font.Name='Times New Roman';$header.Range.Font.Size=9
    }
   }
   $tocCount=$doc.TablesOfContents.Count
   # Native TOC regeneration fixes its links and builds current heading entries.
   foreach($toc in $doc.TablesOfContents){$toc.Update()}
   $referenceParagraphs=@{}
   foreach($field in $doc.Fields){
    if($field.Code.Text -match '^\s*PAGEREF\s+'){
     $par=$field.Result.Paragraphs.Item(1)
     if(-not $referenceParagraphs.ContainsKey([string]$par.Range.Start)){
      $fmt=$par.Range.ParagraphFormat
      $fmt.Alignment=0;$fmt.LeftIndent=0;$fmt.RightIndent=0;$fmt.FirstLineIndent=0
      $fmt.TabStops.ClearAll();[void]$fmt.TabStops.Add($width,2,1)
      $referenceParagraphs[[string]$par.Range.Start]=$true
     }
    }
   }
   # Inline illustrations and headings must not inherit the normal first-line indent.
   foreach($par in $doc.Paragraphs){
    if($par.Range.Start -lt $section.Range.Start){continue}
    if($par.Range.InlineShapes.Count -gt 0){
     $par.Format.Alignment=1;$par.Format.LeftIndent=0;$par.Format.RightIndent=0;$par.Format.FirstLineIndent=0
    }elseif($par.OutlineLevel -lt 10){
     $par.Format.Alignment=0;$par.Format.LeftIndent=0;$par.Format.RightIndent=0;$par.Format.FirstLineIndent=0
    }
   }
   $doc.Repaginate()
   # Update all fields together in Print Layout. Updating PAGEREF separately can
   # use Word's provisional pagination and incorrectly cache 1 or 2 throughout.
   $doc.Fields.Update()|Out-Null
   $doc.Repaginate()
   $refs=@()
   foreach($field in $doc.Fields){
    $match=[regex]::Match($field.Code.Text,'^\s*PAGEREF\s+"?([^\s"\\]+)')
    if($match.Success){
     $bookmark=$match.Groups[1].Value
     if(-not $doc.Bookmarks.Exists($bookmark)){throw "Missing target: $bookmark"}
     $expected=[int]$doc.Bookmarks.Item($bookmark).Range.Information(1)
     $actual=$field.Result.Text.Trim()
     if($actual -ne [string]$expected){$field.Result.Text=[string]$expected}
     $refs+=@{bookmark=$bookmark;page=$expected;result=$field.Result.Text.Trim()}
    }
   }
   if($refs.Count -eq 0){throw "No page references in $file"}
   if(@($refs|Where-Object {$_.result -ne [string]$_.page}).Count -gt 0){throw 'Page references do not match bookmarks'}
   $doc.Save()
   $pdf=[IO.Path]::ChangeExtension($work,'.pdf')
   $doc.ExportAsFixedFormat($pdf,17)
   $previewPages=if($file -eq 'BTL.docx'){@(1,2)}else{@(1,3,4)}
   foreach($n in $previewPages){
    $page=$doc.Range($doc.GoTo(1,1,$n).Start,$doc.GoTo(1,1,($n+1)).Start)
    $stream=[IO.MemoryStream]::new([byte[]]$page.EnhMetaFileBits)
    $metafile=[Drawing.Imaging.Metafile]::new($stream)
    $bitmap=[Drawing.Bitmap]::new(1190,1684);$graphics=[Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([Drawing.Color]::White);$graphics.DrawImage($metafile,0,0,1190,1684)
    $label=if($file -eq 'BTL.docx'){'BTL'}else{'PTTK'}
    $bitmap.Save((Join-Path $PSScriptRoot "$label-final-page-$n.png"),[Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose();$bitmap.Dispose();$metafile.Dispose();$stream.Dispose()
   }
   $validation+=@{file=$file;pages=$doc.ComputeStatistics(2);nativeToc=$tocCount;references=$refs}
   Write-Output "$file : $($refs.Count) page references verified, pages $((($refs|ForEach-Object page|Sort-Object -Unique) -join ','))"
  }finally{$doc.Close(0)}
  Copy-Item -LiteralPath $work -Destination $path -Force
  Copy-Item -LiteralPath ([IO.Path]::ChangeExtension($work,'.pdf')) -Destination ([IO.Path]::ChangeExtension($path,'.pdf')) -Force
 }
 $validation|ConvertTo-Json -Depth 6|Set-Content -Encoding UTF8 (Join-Path $PSScriptRoot 'report-pagination-validation.json')
}finally{$word.Quit()}
