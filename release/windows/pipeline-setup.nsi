###           Pipeline Installer NSIS Script            ###


###########################################################
###               General & UI Settings                 ###
###########################################################

; Includes Modern UI 2
!include "MUI2.nsh"
; Use LZMA compression
SetCompressor lzma

;----------------------------------------------------------
;   General Defines
;----------------------------------------------------------
!define PRODUCT_NAME "DAISY Pipeline"
!define PRODUCT_VERSION "20070404"
!define PRODUCT_PUBLISHER "DAISY Consortium"
!define PRODUCT_WEB_SITE "http://www.daisy.org/"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
!define PRODUCT_STARTMENU_REGVAL "NSIS:StartMenuDir"
!define UNINSTALLER_NAME "Uninstall ${PRODUCT_NAME}"


;----------------------------------------------------------
;   MUI Settings
;----------------------------------------------------------
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
; --- Registry storage of selected language ---
!define MUI_LANGDLL_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
; ---- StartMenu Page Configuration ---
var SMGROUP
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "${PRODUCT_NAME}"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"


;----------------------------------------------------------
;   Installer Pages
;----------------------------------------------------------
; ---- Installer Pages ----
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "UserAgreement.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $SMGROUP
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
; ---- Uninstaller Pages ----
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES


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
;   Installer General Settings
;----------------------------------------------------------
Name "${PRODUCT_NAME}"
OutFile "setup.exe"
InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"
ShowInstDetails show
ShowUnInstDetails show
;InstallDirRegKey HKLM "${REGKEY}" Path ;TBD


;----------------------------------------------------------
;   Version Information (TBD)
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

;----------------------------------------------------------
;   Initialization Callback
;----------------------------------------------------------
Function .onInit
  ; show the language selection dialog
  !insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

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
SectionGroup "External Tools"

Section "Lame" SEC_LAME
  SectionIn 1
  SetOutPath $INSTDIR\ext
  File /r ext\lame.exe
SectionEnd

;Section "ImageMagick" SEC_IM
;  SectionIn 1
  ;SetOutPath $INSTDIR\ext
  ;File /r ext\ImageMagick-6.4.0-0-Q16-windows-dll.exe
;SectionEnd
SectionGroupEnd


;----------------------------------------------------------
;   Language Pack
;----------------------------------------------------------

SectionGroup "Language Packs"

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
  ; Clean legacy installer remainings
  RMDIR /r /REBOOTOK "$SMPROGRAMS\$SMGROUP\workspace"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -Post
  ; Initialize the RCP configuration area
  ExecWait '"$INSTDIR\DAISY Pipeline.exe" -initialize'
;  FileOpen $0 "$INSTDIR\configuration\config.ini" a
;  FileSeek $0 0 END
;  FileWriteByte $0 "13"
;  FileWriteByte $0 "10"
;  FileWrite $0 "osgi.instance.area=@user.home/Application Data/${PRODUCT_NAME}/data"
;  FileWriteByte $0 "13"
;  FileWriteByte $0 "10"
;  FileWrite $0 "osgi.configuration.area=@user.home/Application Data/${PRODUCT_NAME}/configuration"
;  FileClose $0
  ; Create the uninstaller
  WriteUninstaller "$INSTDIR\${UNINSTALLER_NAME}.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\${UNINSTALLER_NAME}.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


;----------------------------------------------------------
;   Section Descriptions
;----------------------------------------------------------
  ;Language strings
  LangString DESC_LAME ${LANG_ENGLISH} "Lame."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SEC_LAME} $(DESC_LAME)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

###########################################################
###              Uninstaller Sections                   ###
###########################################################
Function un.onInit
  !insertmacro MUI_UNGETLANGUAGE
FunctionEnd

Section Uninstall
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

  !insertmacro MUI_STARTMENU_GETFOLDER Application $SMGROUP
  Delete "$SMPROGRAMS\$SMGROUP\${PRODUCT_NAME}.lnk"
  Delete "$SMPROGRAMS\$SMGROUP\${PRODUCT_PUBLISHER}.lnk"
  Delete "$SMPROGRAMS\$SMGROUP\${UNINSTALLER_NAME}.lnk"
  RMDir "$SMPROGRAMS\$SMGROUP"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
SectionEnd