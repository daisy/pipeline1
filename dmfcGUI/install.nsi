# 2006-jun-21 23:59:04

Name "DMFC GUI"
# Defines
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.0b1
!define COMPANY "Daisy Consortium"
!define URL http://www.daisy.org/projects/dmfc


!define ECLIPSEDIR "C:\Program Files\Eclipse-3.1"

!define JYTHONDIR "C:\Documents and Settings\Linus Ericson\My Documents\Program\dmfc"
!define JYTHONNAME "jython_Release_2_2alpha1.jar"

!define LAMEDIR "C:\Documents and Settings\Linus Ericson\My Documents\Program\dmfc"
!define LAMENAME "lame3.96.1.zip"

!define SWTDLLDIR "C:\Documents and Settings\Linus Ericson\My Documents\Program\dmfc"

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
    #SetOutPath $INSTDIR\dmfc
    #SetOverwrite on
    #File ..\dmfc\dmfc.bat
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
    
    # extract swt dll
    SetOutPath $INSTDIR
    File "${SWTDLLDIR}\swt-win32-*.dll"

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
    
    # update dmfcgui.bat
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Update dmfcgui.bat"
    Push "SET INST=DUMMY"
    Push "SET INST=$INSTDIR"
    Push all
    Push all
    Push $INSTDIR\dmfcgui.bat
    Call AdvReplaceInFile    
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


Function AdvReplaceInFile
    Exch $0 ;file to replace in
    Exch
    Exch $1 ;number to replace after
    Exch
    Exch 2
    Exch $2 ;replace and onwards
    Exch 2
    Exch 3
    Exch $3 ;replace with
    Exch 3
    Exch 4
    Exch $4 ;to replace
    Exch 4
    Push $5 ;minus count
    Push $6 ;universal
    Push $7 ;end string
    Push $8 ;left string
    Push $9 ;right string
    Push $R0 ;file1
    Push $R1 ;file2
    Push $R2 ;read
    Push $R3 ;universal
    Push $R4 ;count (onwards)
    Push $R5 ;count (after)
    Push $R6 ;temp file name
     
      GetTempFileName $R6
      FileOpen $R1 $0 r ;file to search in
      FileOpen $R0 $R6 w ;temp file
       StrLen $R3 $4
       StrCpy $R4 -1
       StrCpy $R5 -1
 
    loop_read:
     ClearErrors
     FileRead $R1 $R2 ;read line
     IfErrors exit
     
       StrCpy $5 0
       StrCpy $7 $R2
     
    loop_filter:
       IntOp $5 $5 - 1
       StrCpy $6 $7 $R3 $5 ;search
       StrCmp $6 "" file_write2
       StrCmp $6 $4 0 loop_filter
     
    StrCpy $8 $7 $5 ;left part
    IntOp $6 $5 + $R3
    IntCmp $6 0 is0 not0
    is0:
    StrCpy $9 ""
    Goto done
    not0:
    StrCpy $9 $7 "" $6 ;right part
    done:
    StrCpy $7 $8$3$9 ;re-join
     
    IntOp $R4 $R4 + 1
    StrCmp $2 all file_write1
    StrCmp $R4 $2 0 file_write2
    IntOp $R4 $R4 - 1
     
    IntOp $R5 $R5 + 1
    StrCmp $1 all file_write1
    StrCmp $R5 $1 0 file_write1
    IntOp $R5 $R5 - 1
    Goto file_write2
     
    file_write1:
     FileWrite $R0 $7 ;write modified line
    Goto loop_read
 
    file_write2:
     FileWrite $R0 $R2 ;write unmodified line
    Goto loop_read
     
    exit:
      FileClose $R0
      FileClose $R1
     
       SetDetailsPrint none
      Delete $0
      Rename $R6 $0
      Delete $R6
       SetDetailsPrint both
     
    Pop $R6
    Pop $R5
    Pop $R4
    Pop $R3
    Pop $R2
    Pop $R1
    Pop $R0
    Pop $9
    Pop $8
    Pop $7
    Pop $6
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
FunctionEnd
