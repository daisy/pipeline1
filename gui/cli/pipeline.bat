@echo off

rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

echo.
%JAVA% -classpath "../plugins/org.daisy.pipeline_1.0.3/daisy-pipeline.jar";"../plugins/org.daisy.pipeline_1.0.3/" org.daisy.pipeline.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9
echo.
