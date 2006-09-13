###############################################################################
## DMFC GUI launcher script                                                  ##
##  - using Nullsoft Scriptable Install System (NSIS)                        ##
##                                                                           ##
## Linus Ericson 2006                                                        ##
##                                                                           ##
###############################################################################

; This NSIS script creates a Java launcher for the DMFC GUI.
;
; Currently, the output to stdout and stderr are ignored by this launcher.
; If you turn on the debug mode (no need to rebuild the exe, just place
; a file named debug.txt in the same directory as the dmfcgui.exe) stdout and
; stderr will be written to stdout.log and stderr.log, respectively.

!define APPNAME "DMFC GUI"

Name "${APPNAME}"
Caption "${APPNAME}"
;Icon "Java Launcher.ico"
OutFile "dmfcgui.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define CLASS "org.daisy.dmfc.gui.DMFCMain"

Var classpath
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; add swt jars
  ;Push "$EXEDIR\lib"
  ;Call AddJarsInDir
  Push "$EXEDIR\org.daisy.dmfc.gui.jar"
  Call AddEntry
  
  ; add dmfc libs
  ;Push "$EXEDIR\dmfc\lib"
  ;Call AddJarsInDir
  Push "$EXEDIR\dmfc\lib\org.daisy.util.jar"
  Call AddEntry
  
  ; add jython jar
  Push "$EXEDIR\jython"
  Call AddJarsInDir
  
  ; add dmfcgui classpath
  ;Push "$EXEDIR\bin"
  ;Call AddEntry
  
  ; add dmfc classpath
  Push "$EXEDIR\dmfc\bin"
  Call AddEntry
  
  ; read parameters from params.cfg
  IfFileExists $EXEDIR\params.cfg params noparams
 params:
  Push 1
  Push "$EXEDIR\params.cfg"
  Call ReadFileLine
  Pop $7
  Goto paramsdone
 noparams:
  StrCpy $7 ""
 paramsdone: 
 
  MessageBox MB_OK "Params: $7"
  
  ExpandEnvStrings $1 %COMSPEC%
  IfFileExists $EXEDIR\debug.txt debug nodebug
 debug:
  StrCpy $0 '"$1" /C ""$R0" $7 -Djava.library.path="$EXEDIR" -classpath $classpath ${CLASS} > stdout.log 2> stderr.log"'
  Goto exeset
 nodebug:
  StrCpy $0 '"$R0" $7 -Djava.library.path="$EXEDIR" -classpath $classpath ${CLASS}'
 exeset:
 
  ;MessageBox MB_OK "classpath: $classpath"
  MessageBox MB_OK "java: $0"
 
  SetOutPath $EXEDIR
  ExecWait $0 
  
  ;MessageBox MB_OK "Done!"
SectionEnd


Function AddJarsInDir
    Exch $R1
    
    Push $0
    Push $1
    
    FindFirst $0 $1 $R1\*.jar
  loop:
    StrCmp $1 "" done
    Push "$R1\$1"
    Call AddEntry
    FindNext $0 $1
    Goto loop
  done:
    
    Pop $1
    Pop $0
    
    Pop $R1
FunctionEnd


Function AddEntry
    Exch $R1
    
    StrCmp $classpath "" +2
    StrCpy $classpath "$classpath;"
    
    StrCpy $classpath '$classpath"$R1"'
    
    Pop $R1
FunctionEnd


Function GetJRE
;
;  Find JRE (Java.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume java.exe in current dir or PATH
 
  Push $R0
  Push $R1
    
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe" 
  IfErrors 0 JreFound
  
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe" 
  IfErrors 0 JreFound
  
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound
   
  Sleep 1000
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
    'Could not find a Java Runtime Environment installed on your computer. \
    $\nWithout it you cannot run "${APPNAME}". \
	$\n$\nWould you like to visit the Java website to download it?' \
	IDNO +2
  ExecShell open "http://java.sun.com/getjava"
  Quit
  
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd

Function ReadFileLine
    Exch $0 ;file
    Exch
    Exch $1 ;line number
    Push $2
    Push $3
 
    FileOpen $2 $0 r
    StrCpy $3 0
 
  Loop:
    IntOp $3 $3 + 1
    ClearErrors
    FileRead $2 $0
    IfErrors +2
    StrCmp $3 $1 0 loop
    FileClose $2
 
    Pop $3
    Pop $2
    Pop $1
    Exch $0
FunctionEnd
