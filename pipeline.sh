#!/bin/sh

JAVA=`which java`

CP=pipeline.jar:`dirname $0`

#echo $CP

$JAVA -classpath $CP org.daisy.pipeline.ui.CommandLineUI $@
