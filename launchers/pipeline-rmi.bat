REM ####################################
REM Parameters:
REM   $1 - The Pipeline instance RMI ID
REM
REM The script is supposed to be called
REM in the Pipeline home directory
REM
REM ###################################

@echo off

rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

SETLOCAL ENABLEDELAYEDEXPANSION  

set PIPELINE_DIR=.
set CP=%PIPELINE_DIR%;%PIPELINE_DIR%\pipeline.jar
for %%f IN (%PIPELINE_DIR%\lib\*.jar) do set CP=!CP!;%%f

%JAVA% -Xms256m -Xmx1024m -cp %CP% org.daisy.pipeline.rmi.RMIPipelineApp %1