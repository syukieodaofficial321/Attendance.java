<#
Simple installer script:
- Copies provided EXE into Program Files\AttendanceApp
- Creates a desktop shortcut
Run with administrator privileges.
Usage: .\Install.ps1 -SourceExePath "C:\path\to\Attendance.exe"
#>
[CmdletBinding()]
param(
    [string]$SourceExePath = "./Attendance.exe",
    [string]$AppName = "Attendance",
    [string]$InstallDir = "$env:ProgramFiles\AttendanceApp",
    [string]$SourceDir = "./publish/Attendance"
)
function Ensure-Elevation {
    if (-not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
        Write-Output "Not running as administrator. Relaunching elevated..."
        $arg = "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
        if ($SourceExePath) { $arg += " -SourceExePath `"$SourceExePath`"" }
        Start-Process -FilePath powershell -ArgumentList $arg -Verb RunAs
        exit
    }
}

Ensure-Elevation

$SourceExeFull = (Resolve-Path -Path $SourceExePath -ErrorAction Stop).Path
if (-not (Test-Path $SourceExeFull)) { Write-Error "Source EXE not found: $SourceExePath"; exit 1 }

if (-not (Test-Path $InstallDir)) { New-Item -Path $InstallDir -ItemType Directory -Force | Out-Null }

$destExe = Join-Path $InstallDir (Split-Path $SourceExeFull -Leaf)
Copy-Item -Path $SourceExeFull -Destination $destExe -Force

# Copy application `app` and `runtime` (if provided)
try {
    $sourceDirFull = (Resolve-Path -Path $SourceDir -ErrorAction Stop).Path
    Write-Output "Copying application files from $sourceDirFull to $InstallDir..."
    # If the source contains `app` and `runtime`, mirror them into the install dir
    $appSource = Join-Path $sourceDirFull "app"
    $runtimeSource = Join-Path $sourceDirFull "runtime"
    if (Test-Path $appSource) { Copy-Item -Path (Join-Path $appSource '*') -Destination $InstallDir -Recurse -Force }
    if (Test-Path $runtimeSource) { Copy-Item -Path $runtimeSource -Destination (Join-Path $InstallDir 'runtime') -Recurse -Force }
} catch {
    Write-Output "SourceDir not found or copy failed: $_"
}

# Create Desktop shortcut
$desktop = [Environment]::GetFolderPath('Desktop')
$shortcutPath = Join-Path $desktop "$AppName.lnk"
$wsh = New-Object -ComObject WScript.Shell
$sc = $wsh.CreateShortcut($shortcutPath)
$sc.TargetPath = $destExe
$sc.WorkingDirectory = $InstallDir
$sc.WindowStyle = 1
$sc.IconLocation = "$destExe,0"
$sc.Save()

Write-Output "Installed $AppName to $InstallDir and created desktop shortcut."
Write-Output "To uninstall, remove $InstallDir and the desktop shortcut manually or use a custom uninstaller."