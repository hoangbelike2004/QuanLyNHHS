param([int[]]$Pages=@(1,3,13,18,25,26,35,40))
$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.Runtime.WindowsRuntime
$null=[Windows.Storage.StorageFile,Windows.Storage,ContentType=WindowsRuntime]
$null=[Windows.Data.Pdf.PdfDocument,Windows.Data.Pdf,ContentType=WindowsRuntime]
$null=[Windows.Storage.Streams.InMemoryRandomAccessStream,Windows.Storage.Streams,ContentType=WindowsRuntime]
$null=[Windows.Storage.Streams.DataReader,Windows.Storage.Streams,ContentType=WindowsRuntime]
$taskMethod=[System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.IsGenericMethodDefinition -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' } | Select-Object -First 1
$actionMethod=[System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and -not $_.IsGenericMethod -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncAction' } | Select-Object -First 1
function Await($operation,[type]$resultType) { $task=$taskMethod.MakeGenericMethod($resultType).Invoke($null,@($operation));$task.GetAwaiter().GetResult() }
function AwaitAction($operation) { $task=$actionMethod.Invoke($null,@($operation));$task.GetAwaiter().GetResult() }
$pdfPath=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../BTL.pdf'))
$file=Await ([Windows.Storage.StorageFile]::GetFileFromPathAsync($pdfPath)) ([Windows.Storage.StorageFile])
$pdf=Await ([Windows.Data.Pdf.PdfDocument]::LoadFromFileAsync($file)) ([Windows.Data.Pdf.PdfDocument])
foreach($pageNumber in $Pages) {
    if($pageNumber -gt $pdf.PageCount) { continue }
    $pdfPage=$pdf.GetPage([uint32]($pageNumber-1))
    $stream=New-Object Windows.Storage.Streams.InMemoryRandomAccessStream
    $opts=New-Object Windows.Data.Pdf.PdfPageRenderOptions
    $opts.DestinationWidth=1190
    AwaitAction ($pdfPage.RenderToStreamAsync($stream,$opts))
    $reader=New-Object Windows.Storage.Streams.DataReader ($stream.GetInputStreamAt(0))
    $null=Await ($reader.LoadAsync([uint32]$stream.Size)) ([uint32])
    $bytes=New-Object byte[] ([int]$stream.Size)
    $reader.ReadBytes($bytes)
    [IO.File]::WriteAllBytes((Join-Path $PSScriptRoot ('page-'+$pageNumber+'.png')),$bytes)
    $reader.Dispose();$stream.Dispose();$pdfPage.Dispose()
}
Write-Output ('Rendered '+$Pages.Count+' selected pages from '+$pdf.PageCount+' page PDF.')
