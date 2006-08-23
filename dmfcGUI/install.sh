#!/bin/bash
#######################################################################
# Simple DMFC GUI install script for Unix systems
# Linus Ericson 2006
# Warning, not finished!
#######################################################################

UTIL="org.daisy.util.jar"
DMFC="org.daisy.dmfc.zip "
GUI="org.daisy.dmfc.gui.jar"
EXTRA="org.daisy.dmfc.gui-extras.zip"
JYTHON="jython_Release_2_2alpha1.jar"
LAME=`which lame`

echo "Destination directory is $1..."

PREV=`pwd`


###################################
# GUI
# GUI Extras
mkdir "$1"
echo "Installing $GUI..."
cp -p $GUI "$1"
cd "$1"
echo "Installing $EXTRA..."
unzip -q "$PREV/$EXTRA"
cd "$PREV"


###################################
# SWT jars + .so
mkdir -p "$1/lib"
cp -p org.eclipse.*.jar "$1/lib"
cd "$1"
unzip -q lib/org.eclipse.swt.*.jar 'libswt*.so'
cd "$PREV"


###################################
# Jython
mkdir "$1/jython"
echo "Installing $JYTHON..."
cd "$1/jython"
unzip -q "$PREV/$JYTHON"
cd "$PREV"


###################################
# DMFC
mkdir "$1/dmfc"
cd "$1/dmfc"
echo "Installing $DMFC..."
unzip -q "$PREV/$DMFC"
cd "$PREV"


###################################
# Update dmfc.properties
echo "Updating dmfc.properties"
cd "$1/dmfc/bin"
sed -e "s/^dmfc\.lame\.path.*/dmfc.lame.path = lame/" < dmfc.properties > tmp
cd "$PREV"

###################################
# Util
mkdir -p "$1/dmfc/lib"
echo "Installing $UTIL"
cp -p $UTIL "$1/dmfc/lib"

echo "Done!"

