Installer files and instructions

PowerShell installer (quick, no external tools):
- File: installer/Install.ps1
- Usage (run as admin):

```powershell
# From PowerShell (run elevated)
Set-ExecutionPolicy Bypass -Scope Process -Force
.\Install.ps1 -SourceExePath "C:\path\to\Attendance.exe" -SourceDir "C:\path\to\publish\Attendance"
```

This copies the EXE to `C:\Program Files\AttendanceApp` and places a shortcut on the current user's Desktop.

Inno Setup (full installer with UI):
- File: installer/setup.iss
- Requires Inno Setup (ISCC.exe). To build an installer:

```powershell
iscc /DMySourceDir="C:\path\to\publish\Attendance" setup.iss
```

The resulting installer will install into Program Files and create a Desktop shortcut automatically.

If you want, I can:
- Adjust the install folder name or shortcut name
- Add an uninstaller entry (registry) and shortcuts in Start Menu
- Build the Inno Setup installer here if you provide the EXE path and allow installation tools to run
