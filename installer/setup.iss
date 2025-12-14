; Inno Setup script for building a proper Windows installer
[Setup]
AppName=Attendance
AppVersion=1.0.1
DefaultDirName={pf}\Attendance
DefaultGroupName=Attendance
OutputBaseFilename=AttendanceSetup
Compression=lzma
SolidCompression=yes

[Files]
; Install Attendance.exe to main app directory
Source: "C:\Programing Java\ambut uyy\publish\Attendance\Attendance.exe"; DestDir: "{app}"; Flags: ignoreversion
; Install app subfolder contents
Source: "C:\Programing Java\ambut uyy\publish\Attendance\app\*"; DestDir: "{app}\app"; Flags: recursesubdirs createallsubdirs ignoreversion
; Install runtime subfolder
Source: "C:\Programing Java\ambut uyy\publish\Attendance\runtime\*"; DestDir: "{app}\runtime"; Flags: recursesubdirs createallsubdirs ignoreversion

[Icons]
Name: "{userdesktop}\Attendance"; Filename: "{app}\Attendance.exe"; WorkingDir: "{app}"
Name: "{group}\Attendance"; Filename: "{app}\Attendance.exe"

[Run]
Filename: "{app}\Attendance.exe"; Description: "Launch Attendance"; Flags: nowait postinstall skipifsilent

; Compile example:
; iscc /DMySourceDir="C:\path\to\publish\Attendance" setup.iss
