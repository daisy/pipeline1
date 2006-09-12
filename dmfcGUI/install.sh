#!/bin/bash
#######################################################################
# Simple DMFC GUI install script for Unix systems
# Linus Ericson 2006
# Warning, not finished!
#######################################################################



###################################
# Function fullPath
function fullpath() {
	oldDir=`pwd`
	# check for one arg
	if [ $# -ne 1 ]; then
		echo "Usage : $0 {relative path}"
		cd $oldDir
		return -1
	fi

	# check to see if $1 exists
	if [ -e $1 ]; then
		B=`basename $1`
		P=`dirname $1`
		#  echo BASE:$B  PATH:$P
		cd $P
	
		if [ `pwd` != "/" ]
		then
			FULLPATH=`pwd`/$B
		else
			FULLPATH=/$B
		fi
		cd $oldDir
		return 0
	else
		echo "$1 - Not a regular file"
		cd $oldDir
		return -1
	fi
}



###################################
# Beginning of install script
if [ $# != 1 ] 
then
	echo "No installation directory given.";
	echo "Usage:"
	echo "  $0 <install_dir>"
	exit 1
fi
if [ -e $1 ]
then
	echo "File or directory $1 alreday exists. You must install to a new directory.";
	echo "Usage:"
	echo "  $0 <install_dir>"
	exit 1
fi

UTIL="org.daisy.util.jar"
DMFC="org.daisy.dmfc.zip"
GUI="org.daisy.dmfc.gui.jar"
EXTRA="org.daisy.dmfc.gui-extras.zip"
JYTHON="jython_Release_2_2alpha1.jar"
LAME=`which lame`

PREV=`pwd`

mkdir -p "$1"
# Set the full path
FULLPATH=""
fullpath $1

echo "Installing to \"$FULLPATH\"..."
echo



###################################
# GUI
# GUI Extras
echo "Installing Graphical User Interface..."
cp -p $GUI "$1"
cd "$1"
echo "  $GUI"
unzip -q "$PREV/$EXTRA"
chmod a+x dmfcgui.sh
cd "$PREV"
echo "  $EXTRA"
echo



###################################
# SWT jars + .so
mkdir -p "$1/lib"
echo "Installing Standard Widget Toolkit (SWT)..."
cp -p org.eclipse.*.jar "$1/lib"

if [ -e "swt.jar" ]
then
	# An 'external' swt.jar was included in the package.
	cp -p swt.jar "$1/lib"
	cp -p lib*.so "$1"
else
	# No 'external' swt.jar.
	cd "$1"
	unzip -q lib/org.eclipse.swt.*.jar 'libswt*.so'
	cd "$PREV"
fi

echo



###################################
# Jython
mkdir "$1/jython"
echo "Installing Jython..."
cd "$1/jython"
unzip -q "$PREV/$JYTHON"
cd "$PREV"
echo "  $JYTHON"
echo



###################################
# DMFC
mkdir "$1/dmfc"
cd "$1/dmfc"
echo "Installing DMFC Framework..."
unzip -q "$PREV/$DMFC"
cd "$PREV"
echo "  $DMFC"
echo



###################################
# Update dmfc.properties
echo "Updating property files..."
JYTH=$FULLPATH/jython
cd "$1/dmfc/bin"
sed -e "s/^dmfc\.lame\.path.*/dmfc.lame.path = lame/" < dmfc.properties > dmfc.properties.bak
sed -e "s:^python\.home.*:python.home = ${JYTH}:" < dmfc.properties.bak > dmfc.properties
cd "$PREV"
echo "  dmfc.properties"
echo



###################################
# Util
mkdir -p "$1/dmfc/lib"
echo "Installing Daisy Utility Library..."
cp -p $UTIL "$1/dmfc/lib"
echo "  $UTIL"
echo



###################################
# Check Java installation
echo "Checking for Java version..."
JAVAPATH=`which java 2> /dev/null`
if [ -n "$JAVAPATH" ]
then
  JAVAVERSION=`java -version 2>&1 | grep version | cut -d '"' -f 2 | cut -d '.' -f 1-2`
  JAVAMAJOR=`echo $JAVAVERSION | cut -d '.' -f 1`
  JAVAMINOR=`echo $JAVAVERSION | cut -d '.' -f 2`
  if [ $JAVAMAJOR > 1 -a $JAVAMINOR -ge 5 ]
  then
    echo "  Java $JAVAVERSION found: OK"
    echo  
  else
    echo
    echo "Warning: This application requires Java 1.5 to run. Only version $JAVAVERSION"
    echo "         was found on your system path. Make sure Java 1.5 is"
    echo "         installed on the system and edit the dmfcgui.sh to insert"
    echo "         the path to the 'java' command."  
    echo
  fi
else
  echo
  echo "Warning: Java was not found on your system path. Make sure Java is"
  echo "         installed on the system and edit the dmfcgui.sh to insert"
  echo "         the path to the 'java' command."  
  echo
fi

echo "Done!"

