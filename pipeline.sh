#!/bin/sh

JAVA=/opt/jre1.5.0_04/bin/java

CP=pipeline.jar:`dirname $0`

#echo $CP

$JAVA -classpath $CP org.daisy.dmfc.ui.CommandLineUI $@
