@echo off

echo %0
echo %cd%
rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

SETLOCAL ENABLEDELAYEDEXPANSION  

set PIPELINE_DIR=.
set CP=%PIPELINE_DIR%;%PIPELINE_DIR%\daisy-pipeline.jar;%PIPELINE_DIR%\daisy-util.jar
for %%f IN (%PIPELINE_DIR%\lib\*.jar) do set CP=!CP!;%%f

%JAVA% -classpath %CP% org.daisy.pipeline.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9