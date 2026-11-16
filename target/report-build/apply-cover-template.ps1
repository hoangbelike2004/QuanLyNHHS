# Rebuild the complete cover using the frame and logo from the supplied template.
& (Join-Path $PSScriptRoot 'replace-real-covers.ps1')
& (Join-Path $PSScriptRoot 'fix-cover-visibility.ps1')
& (Join-Path $PSScriptRoot 'align-cover-layout.ps1')
& (Join-Path $PSScriptRoot 'finalize-report-pagination.ps1')
