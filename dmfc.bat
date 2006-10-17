@echo off
echo.
java -classpath "%~p0bin";"%~p0lib/jing.jar";"%~p0lib/saxon.jar";"%~p0lib/saxon8.jar";"%~p0lib/saxon8-dom.jar";"%~p0lib/sac.jar";"%~p0lib/batik-css.jar";"%~p0lib/batik-util.jar";"%~p0lib/jsr173_1.0_api.jar";"%~p0lib/wstx-lgpl-2.0.3.jar";"%~p0lib/jl1.0.jar";"%~p0lib/chardet.jar";"%~p0lib/xercesImpl.jar";"%~p0lib/icu4j_3_4_4.jar";"%~p0lib/tagsoup-1.0rc4.jar" org.daisy.dmfc.ui.CommandLineUI %1 %2 %3 %4 %5 %6 %7 %8 %9
echo.
