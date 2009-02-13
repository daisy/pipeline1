###           Pipeline Installer NSIS Script            ###


###########################################################
###               General & UI Settings                 ###
###########################################################

;----------------------------------------------------------
;   General Defines
;----------------------------------------------------------
!define PRODUCT_NAME "DAISY Pipeline"
!define PRODUCT_VERSION "20090213 Beta"
!define PRODUCT_PUBLISHER "DAISY Consortium"
!define PRODUCT_WEB_SITE "http://www.daisy.org/"
!define PRODUCT_REG_ROOT SHCTX
!define PRODUCT_REG_KEY "SOFTWARE\${PRODUCT_NAME}"
!define PRODUCT_REG_VALUENAME_INSTDIR "Path"
!define PRODUCT_REG_VALUENAME_STARTMENU "StartMenuGroup"
!define PRODUCT_REG_KEY_UNINST "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define UNINSTALLER_NAME "Uninstall ${PRODUCT_NAME}"
!define REQUIRED_JAVA_VER "1.5"


;----------------------------------------------------------
;   Installer General Settings
;----------------------------------------------------------
Name "${PRODUCT_NAME}"
OutFile "setup.exe"
ShowInstDetails show
ShowUnInstDetails show
SetCompressor /SOLID lzma
InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"

;----------------------------------------------------------
;   Multi-User settings
;----------------------------------------------------------
!define MULTIUSER_EXECUTIONLEVEL Highest
!define MULTIUSER_INSTALLMODE_INSTDIR "${PRODUCT_NAME}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_KEY "${PRODUCT_REG_KEY}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUENAME "${PRODUCT_REG_VALUENAME_INSTDIR}"
!include MultiUser.nsh



;----------------------------------------------------------
;   MUI Settings
;----------------------------------------------------------
; --- Includes Modern UI 2 ---
!include "MUI2.nsh"
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
; --- Registry storage of selected language ---
!define MUI_LANGDLL_ALWAYSSHOW
!define MUI_LANGDLL_REGISTRY_ROOT ${PRODUCT_REG_ROOT}
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_REG_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
; ---- StartMenu Page Configuration ---
var SMGROUP
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "${PRODUCT_NAME}"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT ${PRODUCT_REG_ROOT}
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_REG_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_REG_VALUENAME_STARTMENU}"


;----------------------------------------------------------
;   Installer Pages
;----------------------------------------------------------
; ---- Installer Pages ----
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "UserAgreement.txt"
!insertmacro MUI_PAGE_COMPONENTS
!define MUI_PAGE_CUSTOMFUNCTION_SHOW CheckInstDirReg
!insertmacro MUI_PAGE_DIRECTORY
!define MUI_PAGE_CUSTOMFUNCTION_SHOW CheckSMDirReg
!insertmacro MUI_PAGE_STARTMENU Application $SMGROUP
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
; ---- Uninstaller Pages ----
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

Function CheckInstDirReg
  ; Disable the directory chooser if it's an upgrade
  ReadRegStr $R0 ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY}" "${PRODUCT_REG_VALUENAME_INSTDIR}"
  StrCmp $R0 "" donothing
    FindWindow $R0 "#32770" "" $HWNDPARENT
    GetDlgItem $R1 $R0 1019
    EnableWindow $R1 0
    GetDlgItem $R1 $R0 1001
    EnableWindow $R1 0
    GetDlgItem $R0 $HWNDPARENT 1
    System::Call "user32::SetFocus(i R0)"
  donothing:
FunctionEnd


Function CheckSMDirReg
  ; Disable the start menu chooser if it's an upgrade
  ReadRegStr $R0 ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY}" "${PRODUCT_REG_VALUENAME_STARTMENU}"
  StrCmp $R0 "" donothing
    FindWindow $R0 "#32770" "" $HWNDPARENT
    GetDlgItem $R1 $R0 1002
    EnableWindow $R1 0
    GetDlgItem $R1 $R0 1004
    EnableWindow $R1 0
    GetDlgItem $R1 $R0 1005
    EnableWindow $R1 0
    GetDlgItem $R0 $HWNDPARENT 1
    System::Call "user32::SetFocus(i R0)"
  donothing:
FunctionEnd


;----------------------------------------------------------
;   Headers and Macros
;----------------------------------------------------------
; required for JRE check:
!include WordFunc.nsh
!insertmacro VersionConvert
!insertmacro VersionCompare

;----------------------------------------------------------
;   Language Files
;----------------------------------------------------------
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "French"

;----------------------------------------------------------
;   Reserve Files
;----------------------------------------------------------
!insertmacro MUI_RESERVEFILE_LANGDLL

;----------------------------------------------------------
;   Version Information
;----------------------------------------------------------
VIProductVersion "0.0.0.0" ;TBD
VIAddVersionKey ProductName "${PRODUCT_NAME}"
VIAddVersionKey ProductVersion "${PRODUCT_VERSION}"
VIAddVersionKey CompanyName "${PRODUCT_PUBLISHER}"
VIAddVersionKey CompanyWebsite "${PRODUCT_WEB_SITE}"
VIAddVersionKey FileDescription "${PRODUCT_NAME} Installer"
VIAddVersionKey FileVersion "${PRODUCT_VERSION}"
VIAddVersionKey LegalCopyright "© ${PRODUCT_PUBLISHER}"



###########################################################
###               Installer Sections                    ###
###########################################################

InstType "Default"
InstType /COMPONENTSONLYONCUSTOM
;----------------------------------------------------------
;   Initialization Callback
;----------------------------------------------------------
Function .onInit
  ; check the user priviledges
  !insertmacro MULTIUSER_INIT
  ; show the language selection dialog
  !insertmacro MUI_LANGDLL_DISPLAY


FunctionEnd


;----------------------------------------------------------
;   Legacy Clean-Up Section
;----------------------------------------------------------
Section -LegacyCleanUp SEC00
    ; clean the configuration and workspce area
    RMDir /r /REBOOTOK "$INSTDIR\configuration"
    RMDir /r /REBOOTOK "$INSTDIR\plugins"
    RMDir /r /REBOOTOK "$INSTDIR\features"
    RMDir /r /REBOOTOK "$INSTDIR\workspace"
    ; clean the start menu
    ReadRegStr $SMGROUP ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY}" "${PRODUCT_REG_VALUENAME_STARTMENU}"
    StrCmp $SMGROUP "" end
    ReadRegStr $R0 HKCU "Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" "Programs"
    IfFileExists $R0\$SMGROUP 0 end
    RMDIR /r "$R0\$SMGROUP\workspace"
    ; If the current user doesn't have a user install, clean everything in the menu
    ReadRegStr $R1 HKCU "${PRODUCT_REG_KEY}" "${PRODUCT_REG_VALUENAME_STARTMENU}"
    StrCmp $R1 "" 0 end
    Delete "$R0\$SMGROUP\${PRODUCT_NAME}.lnk"
    Delete "$R0\$SMGROUP\Uninstall ${PRODUCT_NAME}.lnk"
    RMDIR "$R0\$SMGROUP"
    end:
SectionEnd

;----------------------------------------------------------
;   JRE Check
;----------------------------------------------------------

Section -JRECheck SEC00-1

  var /GLOBAL JAVA_VER
  var /GLOBAL JAVA_HOME
  var /GLOBAL MSG_JRE_INSTALL

  DetailPrint "Checking JRE version..."
  ReadRegStr $JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
  StrCmp "" "$JAVA_VER" JavaNotPresent CheckJavaVersion
  
  CheckJavaVersion:
    ;First check version number
    ${VersionConvert} $JAVA_VER "" $R1
    ${VersionCompare} $R1 ${REQUIRED_JAVA_VER} $R2
    IntCmp 2 $R2 JavaTooOld
    ;Then check binary file exist
    ReadRegStr $JAVA_HOME HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$JAVA_VER" JavaHome
    IfFileExists "$JAVA_HOME\bin\javaw.exe" 0 JavaNotPresent
    DetailPrint "Found a compatible JVM ($JAVA_VER)"
    Goto End
  
  JavaTooOld:
    DetailPrint "JRE found ($JAVA_VER) is too old"
    StrCpy $MSG_JRE_INSTALL $(MSG_JRE_TOO_OLD)
    Goto InstallJRE
  
  JavaNotPresent:
    DetailPrint "No JRE found"
    StrCpy $MSG_JRE_INSTALL $(MSG_JRE_NOT_FOUND)
    Goto InstallJRE
  
  InstallJRE:
    MessageBox MB_OK "$MSG_JRE_INSTALL"
    SetOutPath $INSTDIR\ext
    File /r ext\jre*.exe
    FindFirst $0 $1 $INSTDIR\ext\jre*.exe
    StrCmp $1 "" End
    ExecWait "$INSTDIR\ext\$1"
    Delete "$INSTDIR\ext\$1"
    Goto End
  
  End:
SectionEnd

;----------------------------------------------------------
;   Main Section
;----------------------------------------------------------
Section -Main SEC01
    SetOutPath $INSTDIR
    SetOverwrite on
    File UserAgreement.txt
    File release-notes.txt
    File .eclipseproduct
    File "DAISY Pipeline.exe"
    File "DAISY Pipeline.ini"
    SetOutPath $INSTDIR\configuration
    File configuration\config.ini
    SetOutPath $INSTDIR\features
    File /r features\*
    SetOutPath $INSTDIR\licenses
    File /r licenses\*
    SetOutPath $INSTDIR\plugins
    File /r plugins\*
SectionEnd


;----------------------------------------------------------
;   External Tools (Lame, ImageMagick...)
;----------------------------------------------------------
SectionGroup "External Tools" SEC_TOOLS

Section "Lame" SEC_LAME
  SectionIn 1
  SetOutPath $INSTDIR\ext
  File ext\lame.exe
SectionEnd

;Section "ImageMagick" SEC_IM ;TBD
;  SectionIn 1
  ;SetOutPath $INSTDIR\ext
  ;File /r ext\ImageMagick-6.4.0-0-Q16-windows-dll.exe
;SectionEnd

Section "MathDAISY" SEC_DESSCI
  SectionIn 1
  SetOutPath $INSTDIR\ext
  File ext\MathDAISY10_install.exe
  ExecWait "$INSTDIR\ext\MathDAISY10_install.exe"
  Delete "$INSTDIR\ext\MathDAISY10_install.exe"
SectionEnd

SectionGroupEnd


;----------------------------------------------------------
;   Language Pack
;----------------------------------------------------------

SectionGroup "Language Packs" SEC_LANG

Section "Hindi" SEC_LANG_HI
  SectionIn 1
SectionEnd

SectionGroupEnd

;----------------------------------------------------------
;   Finalization
;----------------------------------------------------------
Section -AdditionalIcons
  SetOutPath $INSTDIR
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateDirectory "$SMPROGRAMS\$SMGROUP"
  CreateShortCut "$SMPROGRAMS\$SMGROUP\${UNINSTALLER_NAME}.lnk" "$INSTDIR\${UNINSTALLER_NAME}.exe"
  CreateShortCut "$SMPROGRAMS\$SMGROUP\${PRODUCT_PUBLISHER}.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\$SMGROUP\${PRODUCT_NAME}.lnk" "$INSTDIR\DAISY Pipeline.exe"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -Post
  ; Initialize the RCP configuration area
  ExecWait '"$INSTDIR\DAISY Pipeline.exe" -initialize'
  ; If it is a shared install, set the config and instance areas to the AppData dir
  StrCmp $MultiUser.InstallMode "AllUsers" 0 next
  FileOpen $0 "$INSTDIR\configuration\config.ini" a
  FileSeek $0 0 END
  FileWriteByte $0 "13"
  FileWriteByte $0 "10"
  FileWrite $0 "osgi.instance.area=@user.home/Application Data/${PRODUCT_NAME}/data"
  FileWriteByte $0 "13"
  FileWriteByte $0 "10"
  FileWrite $0 "osgi.configuration.area=@user.home/Application Data/${PRODUCT_NAME}/configuration"
  FileClose $0
  next:
  ; Create the uninstaller
  WriteUninstaller "$INSTDIR\${UNINSTALLER_NAME}.exe"
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}" "UninstallString" "$INSTDIR\${UNINSTALLER_NAME}.exe"
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}" "Publisher" "${PRODUCT_PUBLISHER}"
  ; Save the Install Path
  WriteRegStr ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY}" "${PRODUCT_REG_VALUENAME_INSTDIR}" $INSTDIR
SectionEnd


;----------------------------------------------------------
;   Section Descriptions
;----------------------------------------------------------
  ;English Language strings
  LangString DESC_TOOLS ${LANG_ENGLISH} "Third-Party Tools used by some DAISY Pipeline transformers."
  LangString DESC_LAME ${LANG_ENGLISH} "Lame MP3 Encoder$\r$\n$\r$\nlame.sourceforge.net"
  LangString DESC_DESSCI ${LANG_ENGLISH} "MathML Plugin by Design Science$\r$\n$\r$\nwww.dessci.com"
  LangString DESC_LANG ${LANG_ENGLISH} "Support for local languages."
  LangString MSG_JRE_TOO_OLD ${LANG_ENGLISH} "This software requires a more recent Java Runtime Environment.$\r$\nLaunching the Java installer."
  LangString MSG_JRE_NOT_FOUND ${LANG_ENGLISH} "This software requires a Java Runtime Environment.$\r$\nLaunching the Java installer."

  ;French Language strings
  LangString DESC_TOOLS ${LANG_FRENCH} "Outils logiciels externes utilisés par certains convertisseurs du DAISY Pipeline."
  LangString DESC_LAME ${LANG_FRENCH} "Encodeur MP3 Lame$\r$\n$\r$\nlame.sourceforge.net"
  LangString DESC_DESSCI ${LANG_FRENCH} "Module MathML de Design Science$\r$\n$\r$\nwww.dessci.com"
  LangString DESC_LANG ${LANG_FRENCH} "Support pour d'autres langues."
  LangString MSG_JRE_TOO_OLD ${LANG_FRENCH} "Ce logiciel requiert une version de Java plus récente.$\r$\nLancement de l'installeur Java."
  LangString MSG_JRE_NOT_FOUND ${LANG_FRENCH} "Ce logiciel requiert l'environnement d'exécution Java.$\r$\nLancement de l'installeur Java."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SEC_TOOLS} $(DESC_TOOLS)
    !insertmacro MUI_DESCRIPTION_TEXT ${SEC_LAME} $(DESC_LAME)
    !insertmacro MUI_DESCRIPTION_TEXT ${SEC_DESSCI} $(DESC_DESSCI)
    !insertmacro MUI_DESCRIPTION_TEXT ${SEC_LANG} $(DESC_LANG)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

###########################################################
###              Uninstaller Sections                   ###
###########################################################
; Header for HKU parsing (multi-users)
!include EnumUsersReg.nsh

Function un.onInit
  ; check the user priviledges
  !insertmacro MULTIUSER_UNINIT
  ; get the unistaller language
  !insertmacro MUI_UNGETLANGUAGE
FunctionEnd

Function "un.rmAppDataDir"
  Pop $0
  ReadRegStr $0 HKU "$0\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" "AppData"
  IfFileExists "$0\${PRODUCT_NAME}" 0 +1
    RMDir /r /REBOOTOK "$0\${PRODUCT_NAME}"
FunctionEnd

Section Uninstall
  ; --- Clean the install dir ---
  Delete /REBOOTOK "$INSTDIR\${PRODUCT_NAME}.url"
  Delete /REBOOTOK "$INSTDIR\${UNINSTALLER_NAME}.exe"
  Delete /REBOOTOK "$INSTDIR\UserAgreement.txt"
  Delete /REBOOTOK "$INSTDIR\release-notes.txt"
  Delete /REBOOTOK "$INSTDIR\.eclipseproduct"
  Delete /REBOOTOK "$INSTDIR\DAISY Pipeline.exe"
  Delete /REBOOTOK "$INSTDIR\DAISY Pipeline.ini"
  Delete /REBOOTOK "$INSTDIR\configuration\config.ini"
  RMDir /r /REBOOTOK "$INSTDIR\features"
  RMDir /r /REBOOTOK "$INSTDIR\licenses"
  RMDir /r /REBOOTOK "$INSTDIR\plugins"
  RMDir /r /REBOOTOK "$INSTDIR\ext"
  RMDir /r /REBOOTOK "$INSTDIR\configuration"
  RMDir /r /REBOOTOK "$INSTDIR\workspace"
  RMDir /REBOOTOK "$INSTDIR"

  ; --- Clean the App Data dirs if it is a shared install
  StrCmp "$MultiUser.InstallMode" "AllUsers" 0 next
  ${un.EnumUsersReg} "un.rmAppDataDir" temp.key
  next:

  ; --- Clean the start menu ---
  !insertmacro MUI_STARTMENU_GETFOLDER Application $SMGROUP
  Delete "$SMPROGRAMS\$SMGROUP\${PRODUCT_NAME}.lnk"
  Delete "$SMPROGRAMS\$SMGROUP\${PRODUCT_PUBLISHER}.lnk"
  Delete "$SMPROGRAMS\$SMGROUP\${UNINSTALLER_NAME}.lnk"
  RMDir "$SMPROGRAMS\$SMGROUP"

  ; --- Clean the registry ---
  DeleteRegKey ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY_UNINST}"
  DeleteRegKey ${PRODUCT_REG_ROOT} "${PRODUCT_REG_KEY}"
SectionEnd

