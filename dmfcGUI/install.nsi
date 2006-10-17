###############################################################################
## DMFC GUI install script                                                       ##
##  - using Nullsoft Scriptable Install System (NSIS)                            ##
##                                                                               ##
## Linus Ericson 2006                                                            ##
##                                                                               ##
## Depends on ZipDLL, http://nsis.sourceforge.net/wiki/ZipDLL   
## Depends on ScrollLicense.DLL  http://nsis.sourceforge.net/ScrollLicense_plug-in
###############################################################################

; Initial setup
;
;   1. Download and install NSIS from http://nsis.sourceforge.net
;   2. Install the EclipseNSIS plugin http://eclipsensis.sourceforge.net/#download
;   3. Download and install the ZipDLL plugin from http://nsis.sourceforge.net/wiki/ZipDLL
;      Remember to fix the bug as stated on the download page.


; Download dependencies
;
;   Lame for windows can be downloaded at http://www.rarewares.org/mp3.html. Use the latest
;   stable release.
;

; Usage
;   
;   DMFC
;     Use the build.xml ANT script in the DMFC project to build DMFC and the
;     util library. Target names are 'buildUtil' and 'buildDMFC-NSIS'.
;
;   GUI
;     Build the dmfcgui.exe using the dmfcgui.nsi NSIS script in the DMFCGUI project.
;     This file will seldom need to be rebuilt.
;
;     Use the build.xml ANT script in the DMFCGUI project to build the GUI.
;     Target name is 'buildZip-NSIS'.
;
;   NSIS
;     Make sure the defines in the 'Defines' part are correct. Most importantly,
;     make sure the ECLIPSEDIR and LAMEDIR defines are correct.
;
;     (optionally) Change the value of the 'OutFile' command in the 
;     'Installer attributes' part.
;
;     Apply any changes to the 'DMFC', 'GUI', and 'Other' sections.


Name "DAISY Pipeline"

### Defines ###################################################################
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION October 17, 2006
!define COMPANY "DAISY Consortium"
!define URL http://www.daisy.org/projects/dmfc

; Path to Eclipse directory
!define ECLIPSEDIR "C:\Program Files\eclipse"

; Path to and name of the LAME install ZIP
!define LAMEDIR "C:\lame3.96.1"
!define LAMENAME "lame3.96.1.zip"

; Major and minor version of .NET required
!define DOT_MAJOR 2
!define DOT_MINOR 0

; Major and minor version of Python
!define PYTHON_MAJOR 2 
!define PYTHON_MINOR 3 

; Version of Java required
!define JRE_VERSION "1.5.0"

### Included files ############################################################
!include Sections.nsh
!include ZipDLL.nsh
!include WordFunc.nsh
!include LogicLib.nsh

!insertmacro VersionCompare


### Reserved Files ############################################################

### Variables #################################################################
Var StartMenuGroup

Var /GLOBAL PythonInstallPath

### Installer pages ###########################################################
Page License "" LicenseShow
Page directory
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles

### Installer attributes ######################################################
OutFile dist\dmfcgui-oct17.exe
InstallDir "$PROGRAMFILES\DAISY Pipeline"
CRCCheck on
XPStyle on
ShowInstDetails show
AutoCloseWindow false
VIProductVersion 1.0.0.0
VIAddVersionKey ProductName "DAISY Pipeline"
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion ""
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""
InstallDirRegKey HKCU "${REGKEY}" Path
ShowUninstDetails show



########################################
###License Agreement
###Acceptance
#######################################

LicenseData PipelineSoftwareUserAgreement.txt
#LicenseData DMFCAgreement.txt

LicenseForceSelection checkbox

Function LicenseShow
 #ScrollLicense::Set /NOUNLOAD /CHECKBOX
 ScrollLicense::Set /NOUNLOAD 
FunctionEnd

Function .onGUIEnd
 ScrollLicense::Unload
FunctionEnd




############################################################################
### Installer sections ########################################################
Section -Main SEC0000
    # Register section
    WriteRegStr HKCU "${REGKEY}\Components" Main 1
SectionEnd








#########################
### DMFC              ### 
#########################
Section -AppDMFC SEC0002
    SetOverwrite on

    # util library
    SetOutPath $INSTDIR\dmfc\lib
    File ..\dmfc\dist\org.daisy.util.jar
    
    # dmfc nsis
    SetOutPath $INSTDIR
    File ..\dmfc\dist\org.daisy.dmfc.zip 
    ZipDLL::extractall $INSTDIR\org.daisy.dmfc.zip  $INSTDIR\dmfc
    Delete $INSTDIR\org.daisy.dmfc.zip 
    
    # Register section
    WriteRegStr HKCU "${REGKEY}\Components" AppDMFC 1
SectionEnd


#########################
### GUI               ### 
#########################
Section -AppGUI SEC0003
    SetOverwrite on

    # gui zip
    SetOutPath $INSTDIR
    File dist\org.daisy.dmfc.gui-extras.zip
    ZipDLL::extractall $INSTDIR\org.daisy.dmfc.gui-extras.zip $INSTDIR
    Delete $INSTDIR\org.daisy.dmfc.gui-extras.zip
    File dist\org.daisy.dmfc.gui.jar
    
    # copy jars
    SetOutPath $INSTDIR\lib
    File "${ECLIPSEDIR}\plugins\org.eclipse.swt.win32.*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.core.runtime_*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.core.commands_*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.jface_*.jar"
    File "${ECLIPSEDIR}\plugins\org.eclipse.equinox.common_*.jar"
    
    # extract swt dll
    SetOutPath $INSTDIR    
    ; find swt jar
    FindFirst $0 $1 $INSTDIR\lib\org.eclipse.swt.win32.*.jar
    FindClose $0
    StrCmp $1 "" SwtJarNotFound
    Goto SwtJarFound
  SwtJarNotFound:
    MessageBox MB_OK "No SWT JAR found. Very strange... Aborting."
    Abort "Installation error (No SWT JAR!)";
  SwtJarFound:
    ; unzip swt jar
    ZipDLL::extractall $INSTDIR\lib\$1 $INSTDIR\_swttmp_
    ; copy extracted swt dll
    CopyFiles /silent $INSTDIR\_swttmp_\swt-win32-*.dll $INSTDIR
    ; remove temp swt dir
    RMDir /r $INSTDIR\_swttmp_
    
    # Register section
    WriteRegStr HKCU "${REGKEY}\Components" AppGUI 1
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
    
    
    
    # update dmfc.python.path in dmfc.properties  
    #value of last character of $PythonInstallPath in $2 register
    StrCpy $2 $PythonInstallPath 1 -1     
    
    ${If} $2 == '\'
      #MessageBox MB_OK '$$2 is a slash'
      StrCpy $0 $PythonInstallPath -1 
      Push "$0\python.exe"
    ${Else}
      #MessageBox MB_OK '$$2 is "$2"'
      Push "$PythonInstallPath\python.exe"
    ${EndIf}
    
    goto pythonDirDone
  
  #pythonDirSet:
  #   Push "$0\python.exe"
   
  pythonDirDone:
    Push "\"
    Push "\\"
    Call StrRep
    Pop "$MY_PATH"    
    Push $INSTDIR\dmfc\bin\dmfc.properties
    Push "dmfc.python.path"
    Push "dmfc.python.path = $MY_PATH"
    #messageBox MB_OK $MY_PATH
    Call ReplaceLineStr
    
    # update dmfcgui.bat
    #MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Update dmfcgui.bat"    
    #Push $INSTDIR\dmfcgui.bat
    #Push "SET INST="
    #Push "SET INST=$INSTDIR"
    #Call ReplaceLineStr
    
    # Register section
    WriteRegStr HKCU "${REGKEY}\Components" AppOther 1
SectionEnd


Section -post SEC0001
    # Update registry
    WriteRegStr HKCU "${REGKEY}" Path $INSTDIR
    WriteRegStr HKCU "${REGKEY}" StartMenuGroup $StartMenuGroup
    
    WriteUninstaller $INSTDIR\uninstall.exe
    
    # Create start menu items
    SetOutPath $INSTDIR
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup"
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk" $INSTDIR\dmfcgui.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Release Notes.lnk" $INSTDIR\dmfc\doc\releasenotes\release-notes.txt
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe   
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup\Licenses\GUI"
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup\Licenses\Framework"
   # CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Licenses\GUI\License.lnk" $INSTDIR\licenses\GPL\gpl.txt
   # CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Licenses\Framework\User Agreement.lnk" $INSTDIR\dmfc\licenses\LGPL\lgpl.txt
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Licenses\GUI\License\UserAgreement.lnk" $INSTDIR\licenses\GPL\gpl.txt
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Licenses\Framework\User Agreement.lnk" $INSTDIR\dmfc\licenses\LGPL\lgpl.txt
    
    
    
    # Create uninstall registry info
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
    
    # Register section
    WriteRegStr HKCU "${REGKEY}\Components" post 1
SectionEnd

### Macro for selecting uninstaller sections ##################################
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKCU "${REGKEY}\Components" "${SECTION_NAME}"
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
    DeleteRegValue HKCU "${REGKEY}\Components" Main
SectionEnd

Section /o un.AppDMFC UNSEC0002
    RMDir /r /REBOOTOK $INSTDIR\dmfc
    
    DeleteRegValue HKCU "${REGKEY}\Components" AppDMFC
SectionEnd

Section /o un.AppGUI UNSEC0003
    Delete $INSTDIR\lib\org.eclipse.*.jar
    RMDir /REBOOTOK $INSTDIR\lib
    
    RMDir /r /REBOOTOK $INSTDIR\licenses
    
    RMDir /r /REBOOTOK $INSTDIR\scripts
    
    Delete $INSTDIR\swt-win32-*.dll    
    Delete $INSTDIR\org.daisy.dmfc.gui.jar
    Delete $INSTDIR\dmfcgui.exe
    Delete $INSTDIR\*.*
   
    
    

    DeleteRegValue HKCU "${REGKEY}\Components" AppGUI
SectionEnd

Section /o un.AppOther UNSEC0004
    RMDir /r /REBOOTOK $INSTDIR\lame

    DeleteRegValue HKCU "${REGKEY}\Components" AppOther
SectionEnd

Section un.post UNSEC0001
    DeleteRegKey HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    #Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Licenses\GUI\License.lnk"
    #Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Licenses\Framework\License.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Licenses\GUI\User Agreement.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Licenses\Framework\User Agreement.lnk"
    
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Release Notes.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKCU "${REGKEY}" StartMenuGroup
    DeleteRegValue HKCU "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKCU "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKCU "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup\Licenses\GUI
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup\Licenses\Framework
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup\Licenses
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
    
    IfFileExists $INSTDIR\*.* 0 +2
    MessageBox MB_OK|MB_ICONINFORMATION "Some items in directory '$INSTDIR'$\r$\n were not uninstalled automatically."
SectionEnd



### Installer functions #######################################################
Function .onInit
    InitPluginsDir
    
    ; Check for Java
    Push $1
    Push $2
    Call DetectJRE
    Pop $1
    StrCmp $1 "OK" JreFound
    Pop $2
    StrCmp $2 "None" JreNotFound JreFoundOld
    MessageBox MB_OK|MB_ICONINFORMATION "FIXME: Check for Java 5"
  JreNotFound:
    MessageBox MB_OK "You need to have Java version ${JRE_VERSION} installed. No Java found. Aborting."
    Pop $2
    Pop $1    
    Abort "Installation aborted (no Java)"
  JreFoundOld:
    MessageBox MB_OK "You need to have Java version ${JRE_VERSION} installed. You have version $2. Aborting."
    Pop $2
    Pop $1    
    Abort "Installation aborted (old Java)"
  JreFound:
    Pop $2
    Pop $1
    
    ; Check for .NET
    Call IsDotNetInstalled
    Pop $R3
    StrCmp $R3 1 dotNetFinished
    MessageBox MB_YESNO|MB_ICONQUESTION "You need to have v${DOT_MAJOR}.${DOT_MINOR} or greater of the .NET Framework \
                      installed to run some of the conversion (e.g. the Narrator chain).$\r$\n$\r$\n\
                      If you continue the installation and wish to run conversions using .NET you have to install that \
                      at a later time.$\r$\n$\r$\n\
                      Press 'Yes' to continue the installation.$\r$\n\
                      Press 'No' to abort the installation." IDYES dotNetFinished
    MessageBox MB_YESNO|MB_ICONQUESTION "Would you like your browser to be directed to the .NET 2.0 download site?" IDYES dotNetDownload IDNO dotNetDontDownload
  dotNetDownload:
    ExecShell open "http://msdn2.microsoft.com/en-us/netframework/aa731542.aspx"
    Abort "Installation aborted (.NET missing, starting browser)"
  dotNetDontDownload:
    Abort "Installation aborted (no .NET)"
  dotNetFinished:
    
    ; Check for Python
    Call IsPythonInstalled
    Pop $R3
    StrCmp $R3 "" +1 pythonFinished
    MessageBox MB_YESNO|MB_ICONEXCLAMATION "Python version ${PYTHON_MAJOR}.${PYTHON_MINOR} or later is required to run \
                                            some of the conversions (e.g. the RTF to DTBook conversion).$\r$\n$\r$\n\
                                            If you continue the installation now and wish to run conversions using Python \
                                            you have to install that afterwards and make sure python.exe is in your system \
                                            path.$\r$\n$\r$\n\
                                            Press 'Yes' to continue the installation.$\r$\n\
                                            Press 'No' to abort the installation." IDYES pythonFinished

    MessageBox MB_YESNO|MB_ICONQUESTION "Would you like your browser to be directed to the Python download site?" IDYES pythonDownload IDNO pythonDontDownload
  pythonDownload:
    ExecShell open "http://www.python.org/download/"
    Abort "Installation aborted (python missing, starting browser)"
  pythonDontDownload:
    Abort "Installation aborted (python missing)"
  pythonFinished:  
    StrCpy $PythonInstallPath $R3
    
    StrCpy $StartMenuGroup "DAISY Pipeline"
   # StrCpy $StartMenuGroup "DAISY Pipeline"
    
    Var /GLOBAL ALREADY_INSTALLED
    ClearErrors
    ReadRegStr $ALREADY_INSTALLED HKCU "${REGKEY}" Path
    IfErrors continue
    MessageBox MB_YESNO|MB_ICONQUESTION "This software is alreay installed in$\r$\n $ALREADY_INSTALLED$\r$\n$\r$\nInstall anyway?" IDYES continue
    Abort "Installation aborted (already installed)"
    
  continue:    
FunctionEnd

### Uninstaller functions #####################################################
Function un.onInit
    ReadRegStr $INSTDIR HKCU "${REGKEY}" Path
    ReadRegStr $StartMenuGroup HKCU "${REGKEY}" StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
    !insertmacro SELECT_UNSECTION AppDMFC ${UNSEC0002}
    !insertmacro SELECT_UNSECTION AppGUI ${UNSEC0003}
    !insertmacro SELECT_UNSECTION AppOther ${UNSEC0004}
    !insertmacro SELECT_UNSECTION post ${UNSEC0001}    
FunctionEnd



### Utitlity functions ########################################################


### ReplaceLineStr ###
# This function replaces lines with a string in a text file that start with a
# specified string.
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


### StrRep ###
# This function searches and replaces all occurrences of a substring in a
# string.
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

### IsPythonInstalled ###
; Usage
;   Define two constants
;   PYTHON_MAJOR "Major Python version required"
;   PYTHON_MINOR "Minor Python version required"
;   
;   Call IsPythonInstalled
Function IsPythonInstalled
  Push $0 ; Return value
  Push $2 ; Reg key counter
  Push $3 ; Reg key value
  Push $4 ; Version comparison result

  ; Return value
  StrCpy $0 ""
  
  ; Reg key counter
  StrCpy $2 0

  StartEnum:
    ; Enumerate the versions installed
    EnumRegKey $3 HKLM "SOFTWARE\Python\PythonCore" $2
    
    ; If we don't find any versions installed, it's not here
    StrCmp $3 "" noPython notEmpty
    
  notEmpty:
    ; Check if version matched the requirement. Otherwise goto next.
    
    ${VersionCompare} $3 "${PYTHON_MAJOR}.${PYTHON_MINOR}" $4
    ; 0 - same version
    ; 1 - newer version found
    ; 2 - older version found    
    IntCmp $4 1 yesPython yesPython goNext
  
  goNext:
    ; Go to the next RegKey.
    IntOp $2 $2 + 1
    goto StartEnum
  
  noPython:
    ; Python was not detected.
    
    ; Set return value
    StrCpy $0 ""
    goto done
  
  yesPython:  
    ; Python was deteted. Not get the install path
    
    ReadRegStr $2 HKLM "SOFTWARE\Python\PythonCore\$3\InstallPath" ""
    StrCmp $2 "" noPython
  
    ; Set return value
    StrCpy $0 $2
    goto done
    
  done:
    ;MessageBox MB_OK "Python result is $0"
    Pop $4
    Pop $3
    Pop $2
    Exch $0
FunctionEnd

### IsDotNetInstalled ###
# Source:
#  http://nsis.sourceforge.net/How_to_insure_a_required_version_of_.NETFramework_is_installed
#  2006-08-07 linuse - modified to return 0 or 1
; Usage
; Define in your script two constants:
;   DOT_MAJOR "(Major framework version)"
;   DOT_MINOR "{Minor frameword version)"
; 
; Call IsDotNetInstalled
; This function will abort the installation if the required version 
; or higher version of the .NETFramework is not installed.  Place it in
; either your .onInit function or your first install section before 
; other code.
Function IsDotNetInstalled
  Push $0
  Push $1
  Push $2
  Push $3
  Push $4
  StrCpy $0 "0"
  StrCpy $1 "SOFTWARE\Microsoft\.NETFramework" ;registry entry to look in.
  StrCpy $2 0
 
  StartEnum:
    ;Enumerate the versions installed.
    EnumRegKey $3 HKLM "$1\policy" $2
    
    ;If we don't find any versions installed, it's not here.
    StrCmp $3 "" noDotNet notEmpty
    
    ;We found something.
    notEmpty:
      ;Find out if the RegKey starts with 'v'.  
      ;If it doesn't, goto the next key.
      StrCpy $4 $3 1 0
      StrCmp $4 "v" +1 goNext
      StrCpy $4 $3 1 1
      
      ;It starts with 'v'.  Now check to see how the installed major version
      ;relates to our required major version.
      ;If it's equal check the minor version, if it's greater, 
      ;we found a good RegKey.
      IntCmp $4 ${DOT_MAJOR} +1 goNext yesDotNetReg
      ;Check the minor version.  If it's equal or greater to our requested 
      ;version then we're good.
      StrCpy $4 $3 1 3
      IntCmp $4 ${DOT_MINOR} yesDotNetReg goNext yesDotNetReg
 
    goNext:
      ;Go to the next RegKey.
      IntOp $2 $2 + 1
      goto StartEnum
 
  yesDotNetReg:
    ;Now that we've found a good RegKey, let's make sure it's actually
    ;installed by getting the install path and checking to see if the 
    ;mscorlib.dll exists.
    EnumRegValue $2 HKLM "$1\policy\$3" 0
    ;$2 should equal whatever comes after the major and minor versions 
    ;(ie, v1.1.4322)
    StrCmp $2 "" noDotNet
    ReadRegStr $4 HKLM $1 "InstallRoot"
    ;Hopefully the install root isn't empty.
    StrCmp $4 "" noDotNet
    ;build the actuall directory path to mscorlib.dll.
    StrCpy $4 "$4$3.$2\mscorlib.dll"
    IfFileExists $4 yesDotNet noDotNet
 
  noDotNet:
    ;Nope, something went wrong along the way.  Looks like the 
    ;proper .NETFramework isn't installed.  
    ;MessageBox MB_OK "You must have v${DOT_MAJOR}.${DOT_MINOR} or greater of the .NETFramework installed.  Aborting!"
    StrCpy $0 0
    Goto done

  yesDotNet:
    ;Everything checks out.  Go on with the rest of the installation.
    StrCpy $0 1
    
  done:  
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Exch $0  
FunctionEnd


### DetectJRE ###
# Source:
#  http://nsis.sourceforge.net/Simple_installer_with_JRE_check
#  2006-08-08 linuse - slightly modified
Function DetectJRE
    Push $2
    Push $3
    Push $R1
    Push $R2
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    StrCmp $2 "" DetectTry2
    ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
    StrCmp $3 "" DetectTry2
    Goto GetJRE
 
  DetectTry2:
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
    StrCmp $2 "" NoFound
    ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
    StrCmp $3 "" NoFound
 
  GetJRE:
    IfFileExists "$3\bin\java.exe" 0 NoFound
    StrCpy $R1 $2 1
    StrCpy $R2 ${JRE_VERSION} 1
    IntCmp $R1 $R2 0 FoundOld FoundNew
    StrCpy $R1 $2 1 2
    StrCpy $R2 ${JRE_VERSION} 1 2
    IntCmp $R1 $R2 FoundNew FoundOld FoundNew
 
  NoFound:
    ; r2 r1 3 2
    Pop $R2
    ; r1 3 2
    Pop $R1
    ; 3 2
    Pop $3
    ; 2
    Pop $2
    ; 
    Push "None"
    ; None
    Push "NOK"
    ; NOK None
    Return
 
  FoundOld:
    ; r2 r1 3 2
    Pop $R2
    ; r1 3 2
    Pop $R1
    ; 3 2
    Pop $3
    ; 2
    Exch $2
    ; path    
    Push "NOK"
    ; NOK path
    Return
  
  FoundNew:
    ; r2 r1 3 2
    Pop $R2
    ; r1 3 2
    Pop $R1
    ; 3 2
    Push "$3\bin\java.exe"
    ; path 3 2
    Pop $3
    ; 3 2
    Exch $3
    ; path 2
    Pop $2
    ; 2
    Exch $2
    ; path
    Push "OK"
    ; OK path
    Return 
FunctionEnd