This directory contains everything needed to make the DAISY Pipeline release:

- windows/pipeline-setup.nsi
	a NSIS script for building a Windows installer.

- pipeline-release
	a shell script that will create the releases.

	Usage: pipeline-release [options] final-dir export-dir install-dir
	Where:
	 - final-dir is the directory where you want to output the installers
	 - export-dir is the directory where you output Eclipse products
	 -isntall-dir is the directory containing the pipeline installer scripts
	Options:
	 -h : print this help
	 -w : create windows installer
	 -l : create linux installer
	 -m : create mac installer
	 -q : quiet

	## For Windows
	- invake makensis with the nsi script
	## For Linux
	- change permissions
	- wrap the files in a tar.gz archive
	## For Mac
	- repackages the Eclipse PDE-exported application into a proper Mac application bundle
	- create a .dmg disk image and puts the application bundle in it
	- puts the metapackage installer for the external tools in the dmg
	- puts license and readme files in the dmg
	- customize the dmg look
	- compress the dmg

- macosx/customizeDMG.scpt
	an AppleScript to customize the look of the release dmg
- macosx/EclipseOSXRepckager
	a script (MIT licensed) used to repackage an Eclipse PDE-exported RCP product to a proper Mac OS X application bundle
	see http://code.google.com/p/eclipse-osx-repackager/
- macosx/artwork/
	contains images and icons used to customize the disk image
- macosx/files/
	contains files to be added to the disk image (license, readme)
- macosx/tool/
	contains package installers for the third party tools used by the Pipeline