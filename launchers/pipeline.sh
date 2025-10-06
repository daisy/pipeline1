#!/bin/bash
############################################
# Simple launch script for the command line
# version of the Daisy Pipeline
############################################

# This is the path to Java. Edit this variable to suite your needs.
JAVA=`which java 2> /dev/null`
#JAVA=/opt/jre1.5.0_04/bin/java

# Test java version
if [ -n "$JAVA" ]
then
  # Get the full version string and extract major version
  JAVA_VERSION_OUTPUT=`$JAVA -version 2>&1 | head -1`

  # Handle both old format (1.x.y) and new format (x.y.z)
  if echo "$JAVA_VERSION_OUTPUT" | grep -q '"1\.'
  then
    # Old format: "1.8.0_xxx" -> extract second number
    JAVA_MAJOR_VERSION=`echo "$JAVA_VERSION_OUTPUT" | cut -d'"' -f2 | cut -d'.' -f2`
  else
    # New format: "11.0.x" or "17.0.x" or "25-ea" -> extract first number
    JAVA_MAJOR_VERSION=`echo "$JAVA_VERSION_OUTPUT" | cut -d'"' -f2 | cut -d'.' -f1 | cut -d'-' -f1`
  fi

  echo "$JAVA_MAJOR_VERSION"
  if [ "$JAVA_MAJOR_VERSION" -lt 8 ]
  then
    echo
    echo "Error: This application requires Java 8 (or later) to run. Only"
    echo "       version $JAVA_MAJOR_VERSION was found on your system path. Make sure"
    echo "       Java 8 (or later) is installed on the system and edit this"
    echo "       pipeline.sh script to insert the path to the 'java' command."
    echo
    echo "       Current (incorrect) java path: $JAVA"
    echo
    exit 1
  fi
else
  echo
  echo "Error: Java 8 was not found on your system path. Make sure Java 8"
  echo "       (or later) is installed on the system and edit this"
  echo "       pipeline.sh script to insert the path to the 'java' command."
  echo
  exit 1
fi

# Set classpath
if [[ "$(uname)" == 'Linux' ]]; then
    # resolve the path to this script in the face of symlinks
    DIR="$(dirname "$(readlink -f "$0")")"
else
    # other platforms might not support readlink -f
    DIR=`dirname $0`
fi

CP=$DIR/pipeline.jar:$DIR

# Execute Daisy Pipeline
$JAVA -classpath $CP org.daisy.pipeline.ui.CommandLineUI "$@"
