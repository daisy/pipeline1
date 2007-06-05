@echo off
echo.
java -classpath "%~p0pipeline.jar";"%~p0" org.daisy.pipeline.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9
echo.
