@echo off
echo.
java -classpath "%~p0bin";"%~p0lib/dom4j-1.5.2.jar";"%~p0lib/jaxen-1.1-beta-4.jar";"%~p0lib/jing.jar" org.daisy.dmfc.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9
echo.
