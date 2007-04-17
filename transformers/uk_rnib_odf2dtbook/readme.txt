Notes: ODF to Daisy Conversion.
Author. Dave Pawson
Date. 2007-03-29T10:01:21.0Z
Rev. 1
Date. 2007-04-16T14:29:04Z
Rev. 2

Filelist needed
odf2daisy.bat | odf2daisy.  script to drive the process.
odfGetStyles.xsl. xslt to abstract style information from content.xml, styles.xml. Save to _styles.xml
odfStructure.xsl. xslt to construct a structure map of the headings and paragraphs used.
odfNestcheck.xsl. xslt to analyse the structure map and report errors.

odf2daisy.xsl. xslt to transform the content.xml file into dtbook 2005-2
  uses odf2daisy.body.xsl. Handles main body content.
       odf2daisy.meta.xsl. Handles metadata.
       odf2daisy.table.xsl. Handles tables.
       odf2daisy.tableOf.xsl. Handles table of contents.

analyseStyles.xsl       
clean2.xsl              identity transform, uses identity2.xsl

odfHeadings.xsl  -create an xml file containing all heading style names

odf2.cleanHeadings.xsl  Remove list structure from around main headings


Process sequence for X.odt. See odf2daisy, odf2daisy.bat scripts

1. Unzip the X.odt, extracting content.xml and styles.xml
2. Create _styles.xml and _headings.xml, style information and heading style names.
3. If auto numbering has been used,
   remove listing around headings.
@FIXME. This will really screw up if someone has deliberately put  a heading inside a list -
   especially since no nesting check has yet been done.
4. Determine document structure from the content and the style information. (op to X.struct.xml)
5. If valid, convert content.xml to X.xml (a daisy file)





=======
Issues list. 

odfNestCheck.xsl. xslt to analyse the structure map and report errors.@FIXME, where should output go?
odf2Daisy.xsl. xslt to convert ODF to daisy.
   uses odf2daisy.table.xsl odf2daisy.tableOf.xsl odf2daisy.body.xsl. Reads _styles.xml

If there is any content in the body prior to the first heading (which must be at level 1)
then the output will be invalid.