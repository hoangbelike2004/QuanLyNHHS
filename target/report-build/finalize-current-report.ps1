param([switch]$PreviewOnly)
$ErrorActionPreference='Stop'
$root=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$word=$null
$doc=$null
$results=@()
try {
 $word=New-Object -ComObject Word.Application
 $word.Visible=$false
 $word.DisplayAlerts=0
 foreach($name in @('BTL.docx','ON_TAP_BAO_VE_HOANG.docx')) {
  $doc=$word.Documents.Open((Join-Path $root $name),$false,$false,$false)
  $doc.Styles.Item(-86).Font.Name='Times New Roman'
  $doc.Styles.Item(-86).Font.Color=0
  $doc.Styles.Item(-87).Font.Name='Times New Roman'
  $doc.Styles.Item(-87).Font.Color=0
  $doc.Fields.Update() | Out-Null
  foreach($toc in $doc.TablesOfContents) { $toc.Update() }
  $doc.Content.Font.Name='Times New Roman'
  $doc.Content.Font.Color=0
  $doc.Repaginate()
  foreach($toc in $doc.TablesOfContents) { $toc.UpdatePageNumbers() }
  $doc.Content.Font.Name='Times New Roman'
  $doc.Content.Font.Color=0
  $doc.Save()
  $pdf=[IO.Path]::ChangeExtension((Join-Path $root $name),'.pdf')
  $doc.ExportAsFixedFormat($pdf,17)
  $results+=@{file=$name;pages=$doc.ComputeStatistics(2);words=$doc.ComputeStatistics(0);tables=$doc.Tables.Count;toc=$doc.TablesOfContents.Count;pdf=$pdf}
  $doc.Close(0)
  [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($doc)
  $doc=$null
 }
 $results | ConvertTo-Json -Depth 4 | Set-Content -Encoding UTF8 (Join-Path $PSScriptRoot 'current-word-layout.json')
 $results | ConvertTo-Json -Depth 4
}
finally {
 if($doc){$doc.Close(0);[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($doc)}
 if($word){$word.Quit();[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($word)}
 [GC]::Collect();[GC]::WaitForPendingFinalizers()
}
