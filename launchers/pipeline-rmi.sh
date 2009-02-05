#!/bin/bash

#####################################
# Parameters:
#   $1 - The Pipeline instance RMI ID
#
# The script is supposed to be called
# in the Pipeline home directory
#
# ###################################

# This is the path to Java. Edit this variable to suit your needs.
JAVA=`which java 2> /dev/null`

# Add all the libraries to the classpath
CP=pipeline.jar:.
for jar in `ls lib/*.jar`; do
	CP="$jar":$CP
done

$JAVA -Xms256m -Xmx1024m -cp $CP org.daisy.pipeline.rmi.RMIPipelineApp $1

RESULT=$?

exit $RESULT
