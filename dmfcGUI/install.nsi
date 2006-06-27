# 2006-jun-21 23:59:04

Name "DMFC GUI"
# Defines
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.0b1
!define COMPANY "Daisy Consortium"
!define URL http://www.daisy.org/projects/dmfc


!define ECLIPSEDIR "C:\Program Files\Eclipse-3.1"

!define JYTHONDIR "C:\Documents and Settings\Linus Ericson\My Documents\Program"
!define JYTHONNAME "jython_Release_2_2alpha1.jar"

!define LAMEDIR "C:\Documents and Settings\Linus Ericson\My Documents\Program"
!define LAMENAME "lame3.96.1.zip"

# Included files
!include Sections.nsh
!include ZipDLL.nsh

# Reserved Files

# Variables
Var StartMenuGroup

# Installer pages
Page directory
Page instfiles

# Installer attributes
OutFile dist\dmfcgui-1.0b1.exe
InstallDir "$PROGRAMFILES\DMFC GUI"
CRCCheck on
XPStyle on
ShowInstDetails show
AutoCloseWindow false
VIProductVersion 1.0.0.0
VIAddVersionKey ProductName "DMFC GUI"
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion ""
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

# Installer sections
Section -Main SEC0000
    SetOutPath $INSTDIR\dmfc
    SetOverwrite on
    File ..\dmfc\dmfc.bat
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd


#########################
### DMFC              ### 
#########################
Section -AppDMFC SEC0002
    SetOverwrite on

    # util library
    SetOutPath $INSTDIR\dmfc\lib
    File ..\dmfc\dist\org.daisy.util-bin.jar
    
    # dmfc fat
    SetOutPath $INSTDIR
    File ..\dmfc\dist\dmfc-fat.zip
    ZipDLL::extractall $INSTDIR\dmfc-fat.zip $INSTDIR\dmfc
    Delete $INSTDIR\dmfc-fat.zip
SectionEnd


#########################
### GUI               ### 
#########################
Section -AppGUI SEC0003
    SetOverwrite on

    # gui zip
    SetOutPath $INSTDIR
    File dist\dmfcgui.zip
    ZipDLL::extractall $INSTDIR\dmfcgui.zip $INSTDIR
    Delete $INSTDIR\dmfcgui.zip
    
    # copy jars
    SetOutPath $INSTDIR\lib
    File "${ECLIPSEDIR}\plugins\org.eclipse.swt.win32.*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.core.runtime_*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.jface_*.jar"

SectionEnd


#########################
### Other             ### 
#########################
Section -AppOther SEC0004
    SetOverwrite on

    # lame
    SetOutPath $INSTDIR
    File "${LAMEDIR}\${LAMENAME}"
    ZipDLL::extractall "$INSTDIR\${LAMENAME}" "$INSTDIR\lame"
    Delete "$INSTDIR\${LAMENAME}"
    
    # jython
    SetOutPath $INSTDIR
    File "${JYTHONDIR}\${JYTHONNAME}"
    ZipDLL::extractall "$INSTDIR\${JYTHONNAME}" "$INSTDIR\jython"
    Delete "$INSTDIR\${JYTHONNAME}"
    
    # update dmfc.properties
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Update dmfc.properties"
    
SectionEnd


Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    WriteRegStr HKLM "${REGKEY}" StartMenuGroup $StartMenuGroup
    WriteUninstaller $INSTDIR\uninstall.exe
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o un.Main UNSEC0000
    Delete /REBOOTOK $INSTDIR\dmfc\dmfc.bat
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
SectionEnd

# Installer functions
Function .onInit
    InitPluginsDir
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Check for Java 5"
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Check for .NET"
    StrCpy $StartMenuGroup "DMFC GUI"
FunctionEnd

# Uninstaller functions
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    ReadRegStr $StartMenuGroup HKLM "${REGKEY}" StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

