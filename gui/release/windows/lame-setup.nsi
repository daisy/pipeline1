# NSIS installer for Lame
# Usage:
#   - Download the lame release from (i.e.) www.rarewares.org
#   - Unzip the lame release you wish to create an installer for into the LAME_DIR
#     directory.
#   - Set the PRODUCT_VERSION define to the version of the lame release

Name Lame
!define PRODUCT_VERSION "3.98.2"
!define LAME_DIR "ext"

# General Symbol Definitions
!define REGKEY "SOFTWARE\$(^Name)"
!define PRODUCT_REG_ROOT SHCTX

# MultiUser Symbol Definitions
!define MULTIUSER_EXECUTIONLEVEL Highest
!define MULTIUSER_MUI
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_KEY "${REGKEY}"
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_VALUENAME MultiUserInstallMode
!define MULTIUSER_INSTALLMODE_COMMANDLINE
!define MULTIUSER_INSTALLMODE_INSTDIR Lame
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_KEY "${REGKEY}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUE "Path"

# MUI Symbol Definitions
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# Included files
!include MultiUser.nsh
!include Sections.nsh
!include MUI2.nsh

# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MULTIUSER_PAGE_INSTALLMODE
!define MUI_PAGE_CUSTOMFUNCTION_PRE DirectoryPre
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE English

# Installer attributes
Caption "$(^Name) ${PRODUCT_VERSION} Setup"
OutFile "lame-setup.exe"
InstallDir Lame
CRCCheck on
XPStyle on
ShowInstDetails show
;InstallDirRegKey ${PRODUCT_REG_ROOT} "${REGKEY}" Path
ShowUninstDetails show

# Version Information
#VIProductVersion "0.0.0.0"
#VIAddVersionKey ProductName "$(^Name)"
#VIAddVersionKey ProductVersion "${PRODUCT_VERSION}"
#VIAddVersionKey FileDescription "$(^Name) Installer"

# Installer sections
Section -Main SEC0000    
    SetOutPath $INSTDIR
    SetOverwrite on
    File /r "${LAME_DIR}\lame.exe"
    WriteRegStr ${PRODUCT_REG_ROOT} "${REGKEY}\Components" Main 1
SectionEnd

Section -post SEC0001
    WriteRegStr ${PRODUCT_REG_ROOT} "${REGKEY}" Path "$INSTDIR"
    WriteRegStr ${PRODUCT_REG_ROOT} "${REGKEY}" Version "${PRODUCT_VERSION}"
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    WriteRegStr ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name) ${PRODUCT_VERSION}"
    WriteRegStr ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 ${PRODUCT_REG_ROOT} "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    RmDir /r /REBOOTOK $INSTDIR
    DeleteRegValue ${PRODUCT_REG_ROOT} "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey ${PRODUCT_REG_ROOT} "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue ${PRODUCT_REG_ROOT} "${REGKEY}" Path
    DeleteRegValue ${PRODUCT_REG_ROOT} "${REGKEY}" Version
    DeleteRegKey /IfEmpty ${PRODUCT_REG_ROOT} "${REGKEY}\Components"
    DeleteRegKey /IfEmpty ${PRODUCT_REG_ROOT} "${REGKEY}"
    RmDir /REBOOTOK $INSTDIR
SectionEnd

# Installer functions
Function .onInit
    InitPluginsDir
    !insertmacro MULTIUSER_INIT
FunctionEnd

# Uninstaller functions
Function un.onInit
    !insertmacro MULTIUSER_UNINIT
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

# Function run before the directory selection dialog is shown
Function DirectoryPre
    ReadRegStr $R0 ${PRODUCT_REG_ROOT} "${REGKEY}" Path
    ReadRegStr $R1 ${PRODUCT_REG_ROOT} "${REGKEY}" Version
    StrCmp $R1 "" continueInstall 0
        MessageBox MB_YESNO|MB_ICONINFORMATION "Lame (version $R1) is already installed on this system. It's recommended that you uninstall the old version before continuing.$\r$\n$\r$\n Abort installation now?" IDYES abortInstall IDNO continueInstall
   continueInstall:    
    Goto endLabel
   abortInstall:
    Quit
   endLabel: 
FunctionEnd


