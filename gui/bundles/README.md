# Pipeline UI libraries

This folder is a reimport of the [daisycmfgui](https://svn.code.sf.net/p/daisymfcgui/code/trunk) bundles folder.
This includes the main GUI libraries required to build the pipeline to be embedded into the SaveAsDAISY.

## Build libs with maven

The libraries required for the addin build script can be obtained using the following maven command:
`mvn clean package -Dosgi.platform=win32.win32.x86_64`
