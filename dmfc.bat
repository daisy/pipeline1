@echo off
echo.
java -classpath "%~p0bin";"%~p0lib/jing.jar";"%~p0lib/saxon.jar";"%~p0lib/saxon8.jar";"%~p0lib/sac.jar";"%~p0lib/batik-css.jar";"%~p0lib/batik-util.jar" org.daisy.dmfc.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9
echo.
