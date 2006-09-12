#!/bin/bash
#######################################################################
# Simple DMFC GUI launch script for Unix systems
# Linus Ericson 2006
#######################################################################

JAVA=java

BASE=`dirname $0`

CP=
for jar in `ls $BASE/lib/*.jar`; do
    CP="$jar":$CP
done

CP=${CP}$BASE/org.daisy.dmfc.gui.jar:$BASE/dmfc/lib/org.daisy.util.jar:$BASE/dmfc/bin:$BASE/jython/jython.jar

#echo $JAVA -Djava.library.path=$BASE -classpath $CP org.daisy.dmfc.gui.DMFCMain
$JAVA -Djava.library.path=$BASE -classpath $CP org.daisy.dmfc.gui.DMFCMain
