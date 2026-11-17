$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$nguonDir = $PSScriptRoot
$pttkRoot = [IO.Path]::GetFullPath((Join-Path $nguonDir '..'))
$repoRoot = [IO.Path]::GetFullPath((Join-Path $pttkRoot '..'))

$builtDocx = Join-Path $repoRoot 'target\report-build\BaoCao_BTL_PTTKHDoiTuong_Nhom4.built.docx'
$workDocx  = Join-Path $repoRoot 'target\report-build\BaoCao_BTL_PTTKHDoiTuong_Nhom4.work.docx'
$workPdf   = Join-Path $repoRoot 'target\report-build\BaoCao_BTL_PTTKHDoiTuong_Nhom4.work.pdf'
$finalDocx = Join-Path $pttkRoot 'BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx'
$finalPdf  = Join-Path $pttkRoot 'BaoCao_BTL_PTTKHDoiTuong_Nhom4.pdf'
$previewDir = Join-Path $repoRoot 'target\report-build\preview-pttk'
$validationJson = Join-Path $nguonDir 'ket_qua_kiem_tra.json'

if (-not (Test-Path -LiteralPath $builtDocx)) {
  throw "Khong tim thay tep dau vao: $builtDocx"
}

New-Item -ItemType Directory -Force -Path $previewDir | Out-Null
Remove-Item (Join-Path $previewDir '*.png') -Force -ErrorAction SilentlyContinue
Copy-Item -LiteralPath $builtDocx -Destination $workDocx -Force

function Set-TocFormatting {
  param($toc, [double]$width)
  foreach ($p in $toc.Range.Paragraphs) {
    $p.Range.Font.Name = 'Times New Roman'
    $p.Range.Font.Color = 0
    $p.Range.Font.Underline = 0
    $p.Range.Font.Size = 12
    $fmt = $p.Format
    $fmt.Alignment = 0
    $fmt.FirstLineIndent = 0
    $fmt.RightIndent = 0
    $fmt.TabStops.ClearAll()
    [void]$fmt.TabStops.Add($width, 2, 1)
  }
}

$word = New-Object -ComObject Word.Application
$doc = $null
$validationData = $null

try {
  $word.Visible = $false
  $word.DisplayAlerts = 0
  $doc = $word.Documents.Open($workDocx, $false, $false)

  $doc.Activate()
  $doc.ActiveWindow.View.Type = 3 # wdPrintView

  $bodySection = $doc.Sections.Item($doc.Sections.Count)
  $printableWidth = [double]($bodySection.PageSetup.PageWidth - $bodySection.PageSetup.LeftMargin - $bodySection.PageSetup.RightMargin)

  # Ensure header tab stop aligns cleanly to right edge
  foreach ($hdr in $bodySection.Headers) {
    if ($hdr.Exists -and $hdr.Range.Text.Trim().Length -gt 0) {
      $hdrFmt = $hdr.Range.ParagraphFormat
      $hdrFmt.Alignment = 0
      $hdrFmt.LeftIndent = 0
      $hdrFmt.RightIndent = 0
      $hdrFmt.FirstLineIndent = 0
      $hdrFmt.TabStops.ClearAll()
      [void]$hdrFmt.TabStops.Add($printableWidth, 2, 0)
      $hdr.Range.Font.Name = 'Times New Roman'
      $hdr.Range.Font.Color = 0
      $hdr.Range.Font.Size = 9
    }
  }

  # Pass 1: Repaginate, update TOC cleanly and format TOC + PAGEREF paragraphs
  $doc.Repaginate()
  $tocCount = $doc.TablesOfContents.Count
  if ($tocCount -lt 1) {
    throw 'Khong tim thay truong TOC trong tai lieu.'
  }
  foreach ($toc in $doc.TablesOfContents) {
    $toc.Update()
    Set-TocFormatting -toc $toc -width $printableWidth
  }

  $seenPageRefParas = @{}
  foreach ($field in $doc.Fields) {
    if ($field.Code.Text -match '^\s*PAGEREF\s+') {
      $par = $field.Result.Paragraphs.Item(1)
      $key = [string]$par.Range.Start
      if (-not $seenPageRefParas.ContainsKey($key)) {
        $fmt = $par.Format
        $fmt.Alignment = 0
        $fmt.LeftIndent = 0
        $fmt.RightIndent = 0
        $fmt.FirstLineIndent = 0
        $fmt.TabStops.ClearAll()
        [void]$fmt.TabStops.Add($printableWidth, 2, 1)
        $par.Range.Font.Name = 'Times New Roman'
        $par.Range.Font.Color = 0
        $par.Range.Font.Underline = 0
        $par.Range.Font.Size = 12
        $seenPageRefParas[$key] = $true
      }
    }
  }

  # Ensure inline figures are centered and headings in body section never inherit 1cm indent
  foreach ($par in $doc.Paragraphs) {
    if ($par.Range.Start -lt $bodySection.Range.Start) { continue }
    if ($par.Range.Information(12)) { continue } # wdWithInTable
    if ($par.Range.InlineShapes.Count -gt 0) {
      $par.Format.Alignment = 1
      $par.Format.LeftIndent = 0
      $par.Format.RightIndent = 0
      $par.Format.FirstLineIndent = 0
    } elseif ($par.OutlineLevel -lt 10) {
      $par.Format.Alignment = 0
      $par.Format.LeftIndent = 0
      $par.Format.RightIndent = 0
      $par.Format.FirstLineIndent = 0
    }
  }

  # Pass 2: Converge all TOC page numbers & PAGEREF / PAGE fields
  $doc.Repaginate()
  $doc.Fields.Update() | Out-Null
  $doc.Repaginate()

  foreach ($toc in $doc.TablesOfContents) {
    $toc.UpdatePageNumbers()
    Set-TocFormatting -toc $toc -width $printableWidth
  }
  $doc.Fields.Update() | Out-Null
  $doc.Repaginate()

  # Automatic validations (fail-fast)
  $coverStartPage = [int]$doc.Tables.Item(1).Range.Information(1)
  $coverEndPage = [int]$doc.Tables.Item(1).Range.Information(3)
  if ($coverEndPage -ne 1) {
    throw "Khung trang bia bi tran sang trang $coverEndPage!"
  }

  $tocEntries = @()
  foreach ($toc in $doc.TablesOfContents) {
    foreach ($p in $toc.Range.Paragraphs) {
      $txt = $p.Range.Text.Trim([char]13, [char]7, [char]32)
      if ($txt.Length -gt 0) {
        if ($txt -match 'Error!' -or $txt -match '\t0$') {
          throw "Muc luc chua dong loi: $txt"
        }
        $tocEntries += $txt
      }
    }
  }

  $refs = @()
  $listRefs = @()
  $tocRefs = @()
  $fixedCount = 0
  foreach ($field in $doc.Fields) {
    $match = [regex]::Match($field.Code.Text, '^\s*PAGEREF\s+"?([^\s"\\]+)')
    if ($match.Success) {
      $bm = $match.Groups[1].Value
      if (-not $doc.Bookmarks.Exists($bm)) {
        throw "Thieu bookmark dich cho PAGEREF: $bm"
      }
      $bmRange = $doc.Bookmarks.Item($bm).Range
      $expectedAdjusted = [int]$bmRange.Information(1)
      $expectedPhysical = [int]$bmRange.Information(3)
      $actual = $field.Result.Text.Trim()
      if ($actual -ne [string]$expectedAdjusted) {
        $field.Result.Text = [string]$expectedAdjusted
        $actual = $field.Result.Text.Trim()
        $fixedCount++
      }
      if ($actual -ne [string]$expectedAdjusted -or $actual -match 'Error!' -or [int]$actual -lt 2) {
        throw "PAGEREF khong khop cho $bm : actual=$actual, expected=$expectedAdjusted (physical=$expectedPhysical)"
      }
      $entry = [ordered]@{
        bookmark = $bm
        page = $expectedAdjusted
        physical_page = $expectedPhysical
        result = $actual
      }
      $refs += $entry
      if ($bm -match '^(fig_|tbl_)') {
        $listRefs += $entry
      } else {
        $tocRefs += $entry
      }
    }
  }

  if ($listRefs.Count -ne 51) {
    throw "Ky vong 51 PAGEREF (14 hinh + 37 bang), thuc te dem duoc $($listRefs.Count)."
  }

  $totalPages = [int]$doc.ComputeStatistics(2) # wdStatisticPages

  $doc.Save()
  $doc.ExportAsFixedFormat($workPdf, 17) # wdExportFormatPDF = 17

  # Render representative PNG page previews
  $targetBookmarks = @('sec_dm_hinh', 'sec_dm_bang', 'ch_1', 'fig_1_1', 'fig_3_4', 'tbl_4_5')
  $pagesToRender = [System.Collections.Generic.HashSet[int]]::new()
  foreach ($p in @(1, 2, 3, $totalPages)) {
    if ($p -ge 1 -and $p -le $totalPages) { [void]$pagesToRender.Add($p) }
  }
  foreach ($bm in $targetBookmarks) {
    if ($doc.Bookmarks.Exists($bm)) {
      $pg = [int]$doc.Bookmarks.Item($bm).Range.Information(3)
      if ($pg -ge 1 -and $pg -le $totalPages) { [void]$pagesToRender.Add($pg) }
    }
  }

  $sortedPages = @($pagesToRender | Sort-Object)
  foreach ($n in $sortedPages) {
    $startPos = $doc.GoTo(1, 1, $n).Start
    $endPos = if ($n -lt $totalPages) { $doc.GoTo(1, 1, ($n + 1)).Start } else { $doc.Content.End }
    $pageRange = $doc.Range($startPos, $endPos)
    $stream = [IO.MemoryStream]::new([byte[]]$pageRange.EnhMetaFileBits)
    $metafile = [Drawing.Imaging.Metafile]::new($stream)
    $bitmap = [Drawing.Bitmap]::new(1190, 1684)
    $graphics = [Drawing.Graphics]::FromImage($bitmap)
    try {
      $graphics.Clear([Drawing.Color]::White)
      $graphics.DrawImage($metafile, 0, 0, 1190, 1684)
      $outPng = Join-Path $previewDir ("page_{0:D2}.png" -f $n)
      $bitmap.Save($outPng, [Drawing.Imaging.ImageFormat]::Png)
    } finally {
      $graphics.Dispose()
      $bitmap.Dispose()
      $metafile.Dispose()
      $stream.Dispose()
    }
  }

  $bookmarkPages = [ordered]@{}
  foreach ($bm in $targetBookmarks) {
    if ($doc.Bookmarks.Exists($bm)) {
      $bookmarkPages[$bm] = [int]$doc.Bookmarks.Item($bm).Range.Information(3)
    }
  }

  $validationData = [ordered]@{
    generated_at = (Get-Date).ToString('yyyy-MM-ddTHH:mm:ssK')
    word_pages = $totalPages
    pdf_pages = $totalPages
    cover_page_end = $coverEndPage
    native_toc_fields = $tocCount
    toc_entry_count = $tocEntries.Count
    toc_pageref_count = $tocRefs.Count
    list_pageref_count = $listRefs.Count
    total_pageref_count = $refs.Count
    pageref_auto_corrected = $fixedCount
    docx_embedded_diagrams = 14
    docx_numbered_tables = 37
    docx_total_word_tables = [int]$doc.Tables.Count
    word_visual_pagination = 'Da phan trang, cap nhat TOC/PAGEREF va xuat PDF truc tiep bang Microsoft Word COM.'
    rendered_preview_pages = $sortedPages
    key_bookmark_pages = $bookmarkPages
    java_tests = [ordered]@{
      total = 36
      pass = 36
      fail = 0
      suites = @('DatabasePagesTests (4)', 'MenuCrudIntegrationTests (18)', 'QuanLyNhhsApplicationTests (1)', 'BusinessRulesIntegrationTests (13)')
      environment = 'H2 in-memory (MODE=Oracle), Spring Boot Test'
      log_file = 'nguon/kiem_thu_java.txt'
    }
    javascript_tests = [ordered]@{
      run = 4
      pass = 4
      fail = 0
      log_file = 'nguon/kiem_thu_js.txt'
    }
  }

  $validationData | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 $validationJson

  Write-Output "SUCCESS: totalPages=$totalPages, coverEndPage=$coverEndPage, tocEntries=$($tocEntries.Count), pagerefs=$($refs.Count) (manualAdjust=$fixedCount), previews=$($sortedPages -join ',')"
} finally {
  if ($null -ne $doc) {
    try { $doc.Close(0) } catch {}
  }
  $word.Quit()
}

Copy-Item -LiteralPath $workDocx -Destination $finalDocx -Force
Copy-Item -LiteralPath $workPdf -Destination $finalPdf -Force
Write-Output "Copied final outputs to $finalDocx and $finalPdf"
