@echo off

echo %0
echo %cd%
rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

SETLOCAL ENABLEDELAYEDEXPANSION  

set PIPELINE_DIR=.
set CP=%PIPELINE_DIR%
for %%f IN (%PIPELINE_DIR%\lib\*.jar) do set CP=!CP!;%%f

%JAVA% -classpath %CP% org.daisy.pipeline.lite.PipelineLiteCLI %1 %2 %3 %4 %5 %6 %7 %8 %9