rtf2dtbook transformer

This transformer (in its current version) requires the separate installation of jython, a python/java bridge. Below are instructions for installing jython, configuring Eclipse and running a conversion on an RTF file from within Eclipse.

1. Download jython 2.2a1 (http://www.jython.org/download.html)

2. Rename the .jar file to .zip, and extract the folder structure to somewhere like C:\jython.

3. In DMFC/src/dmfc.properties, edit the line that begins with "python.home", to point to the jython installation from step 2.

4. Add jython.jar to the Eclipse project. Select "Project" menu -> "Properties". At the left, select "Java Build Path", then the tab labelled "Libraries". Click "Add External JARs...", navigate to the jython installation and select jython.jar.

5. Edit the parameters in DMFC/doc/examples/rtf2dtbook.xml of the locations of the input RTF and the desired output dtbook.


