#!/bin/sh

JAVA=java
BASE=`dirname $0`
CP="$BASE/bin":"$BASE/lib/dom4j-1.5.2.jar":"$BASE/lib/jaxen-1.1-beta-4.jar":"$BASE/lib/jing.jar"

$JAVA -classpath $CP org.daisy.dmfc.ui.CommandLineUI $@
