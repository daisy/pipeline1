#!/bin/bash
#######################################################################
# Simple DMFC GUI install script for Unix systems
# Linus Ericson 2006
# See also: http://linux.org.mt/article/selfextract
#######################################################################
echo
echo "DMFC GUI - starting installation... please wait"
echo

FULLPATH=""

if [ $# != 1 ] 
then
	echo "No installation directory given.";
	echo "Usage:"
	echo "  $0 <install_dir>"
	exit 1
fi
if [ -e $1 ]
then
	echo "Directory $1 alreday exists. You must install to a new directory.";
	echo "Usage:"
	echo "  $0 <install_dir>"
	exit 1
else
	oldDir=`pwd`
	touch $1
	B=`basename $1`
	P=`dirname $1`
	cd $P
	FULLPATH=`pwd`/$B	
	cd $oldDir
	rm $1
fi

# create a temp directory to extract to.
export WRKDIR=`mktemp -d /tmp/selfextract.XXXXXX`

SKIP=`awk '/^__ARCHIVE_FOLLOWS__/ { print NR + 1; exit 0; }' $0`

# Take the TGZ portion of this file and pipe it to tar.
tail +$SKIP $0 | tar xz -C $WRKDIR

# execute the installation script

PREV=`pwd`
cd $WRKDIR
sh ./install.sh $FULLPATH


# delete the temp files
cd $PREV
rm -rf $WRKDIR

exit 0

__ARCHIVE_FOLLOWS__
