$ErrorActionPreference = 'Stop'
$reportPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../BTL.docx'))
$pdfPath = [IO.Path]::ChangeExtension($reportPath,'.pdf')
$word = $null
$reportDoc = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $reportDoc = $word.Documents.Open($reportPath,$false,$false,$false)
    $reportDoc.Repaginate()
    $reportDoc.Fields.Update() | Out-Null
    foreach($toc in $reportDoc.TablesOfContents) { $toc.Update() }
    $reportDoc.Repaginate()
    foreach($toc in $reportDoc.TablesOfContents) { $toc.UpdatePageNumbers() }
    $reportDoc.Save()
    $reportDoc.ExportAsFixedFormat($pdfPath,17)
    $pageCount=$reportDoc.ComputeStatistics(2)
    $wordCount=$reportDoc.ComputeStatistics(0)
    $pageStarts=@()
    for($i=1;$i -le $pageCount;$i++) {
        $pageRange=$reportDoc.GoTo(1,1,$i)
        $paragraphText=$pageRange.Paragraphs.Item(1).Range.Text.Trim()
        $pageStarts+=@{page=$i;firstParagraph=$paragraphText}
    }
    @{pages=$pageCount;words=$wordCount;tables=$reportDoc.Tables.Count;figures=$reportDoc.InlineShapes.Count;toc=$reportDoc.TablesOfContents.Count;pageStarts=$pageStarts} |
        ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 (Join-Path $PSScriptRoot 'word-validation.json')
    Write-Output ('Word layout: '+$pageCount+' pages; '+$wordCount+' words; '+$reportDoc.InlineShapes.Count+' figures.')
    Write-Output ('PDF: '+$pdfPath)
}
finally {
    if($reportDoc) { $reportDoc.Close(0);[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($reportDoc) }
    if($word) { $word.Quit();[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($word) }
    [GC]::Collect();[GC]::WaitForPendingFinalizers()
}
