@echo off

rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

echo.
%JAVA% -classpath "pipeline.jar";"." org.daisy.pipeline.ui.CommandLineUI %*
echo.
