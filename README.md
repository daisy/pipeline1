# DAISY Pipeline 1 project
The DAISY Pipeline is a cross-platform, open-source framework for DTB-related document transformations. It provides a comprehensive solution for converting text documents into accessible formats for people with print disabilities.


# Building the pipeline

## Requirements

The pipeline requires a JDK compatible with java 1.8 and ant 1.10 toolset available in your path.
We recommend using Adoptium jdk binaries.
The pipeline is currently tested with the (Temurin JDK 11)[https://adoptium.net/temurin/releases?version=11]. 

## Pipeline core

To build the pipeline core for cli usage, use the following command:
`ant -f .\build-core.xml`

## pipeline lite (for SaveAsDAISY addin on windows)

by default, the pipeline "lite" is built for x86/32 bit.
Along ant, you will need maven 3.6+ available in your path to build the UI libraries.


To build the the pipeline lite folder to embedded in the SaveAsDAISY addin, just launch the following command:
`ant -f .\build-addin.xml buildDistForWin`


