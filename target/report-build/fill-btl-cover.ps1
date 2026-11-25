$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$path = Join-Path $root 'BTL.docx'
$backup = Join-Path $PSScriptRoot ('backup-cover-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
[void](New-Item -ItemType Directory -Path $backup)
Copy-Item -LiteralPath $path -Destination (Join-Path $backup 'BTL.docx')
Copy-Item -LiteralPath (Join-Path $root 'BTL.pdf') -Destination (Join-Path $backup 'BTL.pdf')
$work = Join-Path $PSScriptRoot 'BTL-filled-cover.docx'
Copy-Item -LiteralPath $path -Destination $work -Force
$zip = [IO.Compression.ZipFile]::Open($work, [IO.Compression.ZipArchiveMode]::Update)
try {
    $reader = [IO.StreamReader]::new($zip.GetEntry('word/document.xml').Open())
    try { $original = $reader.ReadToEnd() } finally { $reader.Dispose() }
    $xml = [xml]::new()
    $xml.PreserveWhitespace = $true
    $xml.LoadXml($original)
    $ns = [Xml.XmlNamespaceManager]::new($xml.NameTable)
    $ns.AddNamespace('w', 'http://schemas.openxmlformats.org/wordprocessingml/2006/main')
    $body = $xml.SelectSingleNode('/w:document/w:body', $ns)
    $cover = $body.FirstChild
    if ($cover.LocalName -ne 'tbl') { throw 'Expected the cover table.' }
    $tail = (@($body.ChildNodes) | Select-Object -Skip 1 | ForEach-Object OuterXml) -join ''
    $changes = 0
    foreach ($node in $cover.SelectNodes('.//w:t', $ns)) {
        if ($node.InnerText -eq 'Môn học: ................................................') {
            $node.InnerText = 'Môn học: Công nghệ Java'
            $changes++
        }
        if ($node.InnerText -eq 'Giảng viên hướng dẫn: ........................................') {
            $node.InnerText = 'Giảng viên hướng dẫn: Đào Thị Lệ Thủy'
            $changes++
        }
    }
    $members = [ordered]@{
        'Hoàng Việt Đức' = '5260046'
        'Nguyễn Cao Hoàng' = '5260063'
        'Đỗ Minh Chiến' = '5260020'
        'Lý Tiến Đạt' = '5260038'
    }
    foreach ($name in $members.Keys) {
        $paragraphs = @($cover.SelectNodes('.//w:p', $ns) | Where-Object {
            (($_.SelectNodes('.//w:t', $ns) | ForEach-Object InnerText) -join '').StartsWith($name)
        })
        if ($paragraphs.Count -ne 1) { throw "Cannot identify member: $name" }
        $node = $paragraphs[0].SelectSingleNode('.//w:t[starts-with(text(),"MSSV:")]', $ns)
        if (!$node) { throw "Missing student number: $name" }
        $node.InnerText = 'MSSV: ' + $members[$name]
        $changes++
    }
    if ($changes -ne 6) { throw "Expected 6 changes, got $changes" }
    $newTail = (@($body.ChildNodes) | Select-Object -Skip 1 | ForEach-Object OuterXml) -join ''
    if ($tail -cne $newTail) { throw 'Content outside the cover changed.' }
    $zip.GetEntry('word/document.xml').Delete()
    $stream = $zip.CreateEntry('word/document.xml').Open()
    $settings = [Xml.XmlWriterSettings]::new()
    $settings.Encoding = [Text.UTF8Encoding]::new($false)
    $writer = [Xml.XmlWriter]::Create($stream, $settings)
    try { $xml.Save($writer) } finally { $writer.Dispose(); $stream.Dispose() }
    Write-Output 'Updated 6 cover fields; all content after the cover preserved.'
} finally { $zip.Dispose() }
Copy-Item -LiteralPath $work -Destination $path -Force
Write-Output "Backup: $backup"
