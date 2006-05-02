#!/bin/sh

JAVA=java
BASE=`dirname $0`

BIN=$BASE/bin
LIB=$BASE/lib
    


LIB_TMP=
for jar in `ls $LIB/*.jar`; do
    LIB_TMP="$jar":$LIB_TMP
done

CP="$BIN":"$LIB_TMP"

#echo $CP

$JAVA -classpath $CP org.daisy.dmfc.ui.CommandLineUI $@
