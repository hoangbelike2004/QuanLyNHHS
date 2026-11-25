$ErrorActionPreference = 'Stop'
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$word = $null
$doc = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $doc = $word.Documents.Open((Join-Path $root 'BTL.docx'), $false, $true, $false)
    $doc.Repaginate()
    $cover = $doc.Tables.Item(1).Range
    if ($cover.Information(3) -ne 1) { throw 'Cover extends beyond the first page.' }
    $doc.ExportAsFixedFormat((Join-Path $PSScriptRoot 'BTL-filled-cover.pdf'), 17)
    Write-Output ('PDF exported; cover fits page 1; total pages: ' + $doc.ComputeStatistics(2))
} finally {
    if ($doc) { $doc.Close(0); [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($doc) }
    if ($word) { $word.Quit(); [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($word) }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'BTL-filled-cover.pdf') -Destination (Join-Path $root 'BTL.pdf') -Force
