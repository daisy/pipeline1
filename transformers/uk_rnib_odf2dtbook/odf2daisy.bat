@echo off
echo Convert input file %1 to Daisy text file, 2005-2

echo abstract xml from odf

if not exist %1 goto notfound

unzip -o -j %1 content.xml styles.xml

echo Abstract the style information from content.xml and styles.xml, to _styles.xml and _headings.xml


java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar 
-Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  
-x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o _styles.xml   -it initial  odfGetStyles.xsl

if exist op.xml del op.xml

rem now generate all heading styles
java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar 
-Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  
-x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o _headings.xml  _styles.xml  odfHeadings.xsl




echo Remove list wrappers from heading X elements, declarations and forms
java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  -x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o op.xml content.xml odf2.cleanHeadings.xsl  "headingsfile=_headings.xml"

if  errorlevel 1 goto :structureerror

del content.xml
ren op.xml content.xml


echo Determine the document structure, using styles and heading information

java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  -x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o %1.struct.xml  content.xml odfStructure.xsl "stylefile=_styles.xml" "headingsfile=_headings.xml"
if  errorlevel 1 goto :structureerror


echo Structure available in %1.struct.xml. Analyse structure for appropriate nesting



java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  -x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o op.xml %1.struct.xml  odfNestcheck.xsl

if not errorlevel 0 goto :structureerror


echo convert to daisy format.

java  -mx120m -ms120m -cp .;/myjava/saxon8.jar;/myjava/xercesImpl.jar -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration net.sf.saxon.Transform  -x org.apache.xerces.parsers.SAXParser -y org.apache.xerces.parsers.SAXParser   -w1 -l -o %1.xml content.xml odf2daisy.xsl "stylefile=_styles.xml" "headingsfile=_headings.xml"

if errorlevel 0 goto :end

:structureerror
echo Transform  error found. Quitting
goto :end

goto end

:notfound
  File not found, %1

:end
