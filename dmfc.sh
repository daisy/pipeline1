#!/bin/sh

JAVA=java
BASE=`dirname $0`
CP="$BASE/bin":"$BASE/lib/jing.jar":"$BASE/lib/saxon.jar":"$BASE/lib/saxon8.jar":"$BASE/lib/sac.jar":"$BASE/lib/batik-css.jar":"$BASE/lib/batik-util.jar"

$JAVA -classpath $CP org.daisy.dmfc.ui.CommandLineUI $@
