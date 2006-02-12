#!/bin/sh

JAVA=java
BASE=`dirname $0`

BIN=$BASE/bin
LIB=$BASE/lib


CP="$BIN":"$LIB/jing.jar":"$LIB/saxon.jar":"$LIB/saxon8.jar":"$LIB/sac.jar":"$LIB/batik-css.jar":"$LIB/batik-util.jar":"$LIB/jl1.0.jar":"$LIB/jsr173_1.0_api.jar":"$LIB/wstx-lgpl-2.0.3.jar":"$LIB/saxon8-dom.jar"

$JAVA -classpath $CP org.daisy.dmfc.ui.CommandLineUI $@
