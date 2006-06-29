@echo off
echo.

SET INST=%~p0
SET DMFC=%INST%\dmfc
SET DMFCLIB=%DMFC%\lib
SET SWTLIB=%INST%\lib
SET JYTHON="%INST%\jython\jython.jar"

rem echo INST:    %INST%
rem echo DMFCLIB: %DMFCLIB%
rem echo SWTLIB:  %SWTLIB%
rem echo JYTHON:  %JYTHON%
rem echo.

SET SWTJARS="%SWTLIB%\org.eclipse.core.runtime_3.1.1.jar";"%SWTLIB%\org.eclipse.jface_3.1.1.jar";"%SWTLIB%\org.eclipse.swt.win32.win32.x86_3.1.1.jar"
SET DMFCJARS="%DMFCLIB%\batik-css.jar";"%DMFCLIB%\batik-util.jar";"%DMFCLIB%\chardet.jar";"%DMFCLIB%\icu4j_3_4_4.jar";"%DMFCLIB%\jing.jar";"%DMFCLIB%\jl1.0.jar";"%DMFCLIB%\jsr173_1.0_api.jar";"%DMFCLIB%\org.daisy.util-bin.jar";"%DMFCLIB%\sac.jar";"%DMFCLIB%\saxon8.jar";"%DMFCLIB%\saxon8-dom.jar";"%DMFCLIB%\saxon.jar";"%DMFCLIB%\tagsoup-1.0rc4.jar";"%DMFCLIB%\wstx-lgpl-2.0.3.jar";"%DMFCLIB%\xercesImpl.jar"
SET BIN="%INST%\bin";"%DMFC%\bin"
SET SWTDLL="%INST%"

rem echo SWTJARS:  %SWTJARS%
rem echo DMFCJARS: %DMFCJARS%
rem echo JYTHON:   %JYTHON%
rem echo BIN:      %BIN%
rem echo.

SET CP=%SWTJARS%;%DMFCJARS%;%JYTHON%;%BIN%

java -Djava.library.path=%SWTDLL% -classpath %CP% org.daisy.dmfc.gui.DMFCMain

rem pause
