###############################################################################
## DMFC GUI install script                                                   ##
##  - using Nullsoft Scriptable Install System (NSIS)                        ##
##                                                                           ##
## Linus Ericson 2006                                                        ##
##                                                                           ##
## Depends on ZipDLL, http://nsis.sourceforge.net/wiki/ZipDLL                ##
###############################################################################

;
; Usage
;   
;   DMFC
;     Use the build.xml ANT script in the DMFC project to build DMFC and the
;     util library. Target names are 'buildUtil' and 'buildDMFC-NSIS'.
;
;   GUI
;     Use the build.xml ANT script in the DMFCGUI project to build the GUI.
;     Target name is 'buildZip-NSIS'.
;
;   NSIS
;     Make sure the defines in the 'Defines' part are correct.
;
;     (optionally) Change the value of the 'OutFile' command in the 
;     'Installer attributes' part.
;
;     Apply any changes to the 'DMFC', 'GUI', and 'Other' sections.
;
;     Make sure all the (correct) jars are listed in dmfcgui.bat.
;
;     Extract swt-win32-*.dll from the org.eclipse.swt.win32.*.jar and place
;     the result in $SWTDLLDIR


Name "DMFC GUI"

### Defines ###################################################################
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.0b1
!define COMPANY "Daisy Consortium"
!define URL http://www.daisy.org/projects/dmfc


!define ECLIPSEDIR "C:\Program Files\eclipse-3.1.1"

!define JYTHONDIR "C:\Program Files\eclipse-3.1.1\workspace\dmfc\dist"
!define JYTHONNAME "jython_Release_2_2alpha1.jar"

!define LAMEDIR "C:\Program Files\eclipse-3.1.1\workspace\dmfc\dist"
!define LAMENAME "lame3.96.1.zip"

!define SWTDLLDIR "C:\Program Files\eclipse-3.1.1\workspace\dmfc\dist"

### Included files ############################################################
!include Sections.nsh
!include ZipDLL.nsh

### Reserved Files ############################################################

### Variables #################################################################
Var StartMenuGroup

### Installer pages ###########################################################
Page directory
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles

### Installer attributes ######################################################
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

### Installer sections ########################################################
Section -Main SEC0000
    # Register section
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
    
    # dmfc nsis
    SetOutPath $INSTDIR
    File ..\dmfc\dist\dmfc-nsis.zip
    ZipDLL::extractall $INSTDIR\dmfc-nsis.zip $INSTDIR\dmfc
    Delete $INSTDIR\dmfc-nsis.zip
    
    # Register section
    WriteRegStr HKLM "${REGKEY}\Components" AppDMFC 1
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
    
    # extract swt dll
    SetOutPath $INSTDIR
    File "${SWTDLLDIR}\swt-win32-*.dll"
    
    # Register section
    WriteRegStr HKLM "${REGKEY}\Components" AppGUI 1
SectionEnd


#########################
### Other             ### 
#########################
Section -AppOther SEC0004
    SetOverwrite on

    Var /GLOBAL MY_PATH

    ### install LAME
    SetOutPath $INSTDIR
    File "${LAMEDIR}\${LAMENAME}"
    ZipDLL::extractall "$INSTDIR\${LAMENAME}" "$INSTDIR\lame"
    Delete "$INSTDIR\${LAMENAME}"
    # update dmfc.lame.path in dmfc.properties
    Push "$INSTDIR\lame\lame.exe"
    Push "\"
    Push "\\"
    Call StrRep
    Pop "$MY_PATH"    
    Push $INSTDIR\dmfc\bin\dmfc.properties
    Push "dmfc.lame.path"
    Push "dmfc.lame.path = $MY_PATH"
    Call ReplaceLineStr
    
    ### install Jython
    SetOutPath $INSTDIR
    File "${JYTHONDIR}\${JYTHONNAME}"
    ZipDLL::extractall "$INSTDIR\${JYTHONNAME}" "$INSTDIR\jython"
    Delete "$INSTDIR\${JYTHONNAME}"
    # update jython.home in dmfc.properties
    Push "$INSTDIR\jython"
    Push "\"
    Push "\\"
    Call StrRep
    Pop "$MY_PATH"    
    Push $INSTDIR\dmfc\bin\dmfc.properties
    Push "python.home"
    Push "python.home = $MY_PATH"
    Call ReplaceLineStr
    
    # update dmfcgui.bat
    #MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Update dmfcgui.bat"    
    Push $INSTDIR\dmfcgui.bat
    Push "SET INST="
    Push "SET INST=$INSTDIR"
    Call ReplaceLineStr
    
    # Register section
    WriteRegStr HKLM "${REGKEY}\Components" AppOther 1
SectionEnd


Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    WriteRegStr HKLM "${REGKEY}" StartMenuGroup $StartMenuGroup
    WriteUninstaller $INSTDIR\uninstall.exe
    SetOutPath $INSTDIR
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup"
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk" $INSTDIR\dmfcgui.bat
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe    
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
    
    # Register section
    WriteRegStr HKLM "${REGKEY}\Components" post 1
SectionEnd

### Macro for selecting uninstaller sections ##################################
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

### Uninstaller sections ######################################################
Section /o un.Main UNSEC0000
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section /o un.AppDMFC UNSEC0002
    RMDir /r /REBOOTOK $INSTDIR\dmfc
    
    DeleteRegValue HKLM "${REGKEY}\Components" AppDMFC
SectionEnd

Section /o un.AppGUI UNSEC0003
    Delete $INSTDIR\lib\org.eclipse.*.jar
    Delete $INSTDIR\swt-win32-*.dll    

    DeleteRegValue HKLM "${REGKEY}\Components" AppGUI
SectionEnd

Section /o un.AppOther UNSEC0004
    RMDir /r /REBOOTOK $INSTDIR\lame
    RMDir /r /REBOOTOK $INSTDIR\jython

    DeleteRegValue HKLM "${REGKEY}\Components" AppOther
SectionEnd

Section un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
    
    IfFileExists $INSTDIR\*.* 0 +2
    MessageBox MB_OK|MB_ICONINFORMATION "Some items in directory '$INSTDIR'$\r$\n were not uninstalled automatically."
SectionEnd



### Installer functions #######################################################
Function .onInit
    InitPluginsDir
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Check for Java 5"
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Check for .NET"
    StrCpy $StartMenuGroup "DMFC GUI"
    
    Var /GLOBAL ALREADY_INSTALLED
    ClearErrors
    ReadRegStr $ALREADY_INSTALLED HKLM "${REGKEY}" Path
    IfErrors continue
    MessageBox MB_YESNO|MB_ICONQUESTION "This software is alreay installed in$\r$\n $ALREADY_INSTALLED$\r$\n$\r$\nInstall anyway?" IDYES continue
    Abort "Installation aborted"
  continue:
FunctionEnd

### Uninstaller functions #####################################################
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    ReadRegStr $StartMenuGroup HKLM "${REGKEY}" StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
    !insertmacro SELECT_UNSECTION AppDMFC ${UNSEC0002}
    !insertmacro SELECT_UNSECTION AppGUI ${UNSEC0003}
    !insertmacro SELECT_UNSECTION AppOther ${UNSEC0004}
    !insertmacro SELECT_UNSECTION post ${UNSEC0001}    
FunctionEnd

Function ReplaceLineStr
 Exch $R0 ; string to replace that whole line with
 Exch
 Exch $R1 ; string that line should start with
 Exch
 Exch 2
 Exch $R2 ; file
 Push $R3 ; file handle
 Push $R4 ; temp file
 Push $R5 ; temp file handle
 Push $R6 ; global
 Push $R7 ; input string length
 Push $R8 ; line string length
 Push $R9 ; global
 
  StrLen $R7 $R1
 
  GetTempFileName $R4
 
  FileOpen $R5 $R4 w
  FileOpen $R3 $R2 r
 
  ReadLoop:
  ClearErrors
   FileRead $R3 $R6
    IfErrors Done
 
   StrLen $R8 $R6
   StrCpy $R9 $R6 $R7 -$R8
   StrCmp $R9 $R1 0 +3
 
    FileWrite $R5 "$R0$\r$\n"
    Goto ReadLoop
 
    FileWrite $R5 $R6
    Goto ReadLoop
 
  Done:
 
  FileClose $R3
  FileClose $R5
 
  SetDetailsPrint none
   Delete $R2
   Rename $R4 $R2
  SetDetailsPrint both
 
 Pop $R9
 Pop $R8
 Pop $R7
 Pop $R6
 Pop $R5
 Pop $R4
 Pop $R3
 Pop $R2
 Pop $R1
 Pop $R0
FunctionEnd

Function StrRep
  Exch $R4 ; $R4 = Replacement String
  Exch
  Exch $R3 ; $R3 = String to replace (needle)
  Exch 2
  Exch $R1 ; $R1 = String to do replacement in (haystack)
  Push $R2 ; Replaced haystack
  Push $R5 ; Len (needle)
  Push $R6 ; len (haystack)
  Push $R7 ; Scratch reg
  StrCpy $R2 ""
  StrLen $R5 $R3
  StrLen $R6 $R1
loop:
  StrCpy $R7 $R1 $R5
  StrCmp $R7 $R3 found
  StrCpy $R7 $R1 1 ; - optimization can be removed if U know len needle=1
  StrCpy $R2 "$R2$R7"
  StrCpy $R1 $R1 $R6 1
  StrCmp $R1 "" done loop
found:
  StrCpy $R2 "$R2$R4"
  StrCpy $R1 $R1 $R6 $R5
  StrCmp $R1 "" done loop
done:
  StrCpy $R3 $R2
  Pop $R7
  Pop $R6
  Pop $R5
  Pop $R2
  Pop $R1
  Pop $R4
  Exch $R3
FunctionEnd