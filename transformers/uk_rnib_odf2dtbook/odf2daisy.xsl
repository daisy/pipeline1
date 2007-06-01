<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odf2daisy.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file.
# 
#
# 
# Original: 2006-03-23T14:25:46.0Z
#       :Initial issued
#
#
# Copyright &#xA9; Dave Pawson,  2006
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have seen a copy of the GNU General Public License;
# if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
# uses other files: See the includes
# Generates a daisy output file to dtbook-2005-1
#


 -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns='http://www.daisy.org/z3986/2005/dtbook/'
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes"
xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" 
xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" 
xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" 
xmlns:xlink="http://www.w3.org/1999/xlink" 
xmlns:dc="http://purl.org/dc/elements/1.1/" 
xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" 
xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" 
xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" 
xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" 
xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" 
xmlns:math="http://www.w3.org/1998/Math/MathML" 
xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" 
xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" 
xmlns:ooo="http://openoffice.org/2004/office" 
xmlns:ooow="http://openoffice.org/2004/writer" 
xmlns:oooc="http://openoffice.org/2004/calc" 
xmlns:dom="http://www.w3.org/2001/xml-events" 
xmlns:xforms="http://www.w3.org/2002/xforms" 
xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

exclude-result-prefixes="xsi xsd xforms dom oooc ooow ooo 
                         script form math dr3d chart svg 
                         number meta dc xlink fo draw table 
                         text style office xdt xs"
                version="2.0">


<!-- input parameter is the name of the file holding style information -->
  <xsl:param name="stylefile" select="'none'"/>


 <!-- Using include rather than import simply to facilitate debug. -->
 <!-- Probably better to import if overrides are of benefit -->

 <!-- meta.xml -->
 <xsl:include href="odf2daisy.meta.xsl"/>
 <!-- body of content.xml -->
 <xsl:include href="odf2daisy.body.xsl" />
 <!-- table processing -->
 <xsl:include href="odf2daisy.table.xsl" />

 <xsl:strip-space elements="*"/>


<d:doc xmlns:d="rnib.org.uk/ns#">
 <revhistory>
   <purpose><para>This stylesheet uses XSLT 1.0 to produce a NISO
   z39.86-2005 text file </para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2006-02-23T13:20:07.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</revremark>
   </revision>

   <revision>
    <revnumber>1.1</revnumber>
    <date>2006-11-01T10:08:21Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Updated. Added 1.1 accessibility markup, change bar material.</para>
    </revdescription>
    <revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</revremark>
   </revision>

   <revision>
    <revnumber>1.2</revnumber>
    <date>2007-03-17T09:51:56Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Updated. Added mandatory metadata processing</para>
    </revdescription>
    <revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006,2007</revremark>
   </revision>




  </revhistory>
  </d:doc>
  <xsl:output method="xml" indent="yes" 
doctype-public=  "-//NISO//DTD dtbook 2005-2//EN"

doctype-system = "dtbook-2005-2.dtd"
/>

 <!-- doctype-system = "http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd" -->
 <!-- Debug dump to terminal -->
<xsl:variable name="debug" select="true()"/>

  <xsl:template match="/">

<!-- check that styles are available. -->
<xsl:if test="$stylefile ='none'">
  <xsl:message terminate="yes">
    Parameter stylefile not set[<xsl:value-of select="$stylefile"/>]. Terminating
  </xsl:message>
</xsl:if>
<xsl:if test="not(doc-available($stylefile))">
  <xsl:message terminate="yes">
    odf2daisy.xsl: Unable to open stylefile <xsl:value-of select="$stylefile"/>. Terminating
  </xsl:message>
</xsl:if>

    <xsl:variable name='lang'>
    <xsl:choose>
      <xsl:when test="document('meta.xml')/office:document-meta/office:meta[dc:language]">
        <xsl:value-of select="substring-before(document('meta.xml')//office:document-meta/office:meta/dc:language,'-')"/>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="'en'"/>
        <xsl:message>
Warning          dc:language not found, using 'en'
        </xsl:message>

      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>


    <dtbook  version='2005-2' xmlns='http://www.daisy.org/z3986/2005/dtbook/' xml:lang='{$lang}'>
      <head>
    <xsl:if test="$debug">
      <xsl:message>
        Processing head
      </xsl:message>
    </xsl:if>
    <xsl:apply-templates select="document('meta.xml',/)/office:document-meta"/>
      </head>
 <book>
    <frontmatter>
      <doctitle>
        <xsl:value-of 
     select="/office:document-content/office:body/office:text/text:h
             [contains(@text:style-name,'Heading')][1]"/></doctitle>
    </frontmatter>
    <bodymatter>

      <xsl:apply-templates select="/office:document-content/office:body"/>

    </bodymatter>
  </book>
</dtbook>
  </xsl:template>


 <!--  -->
 <!--Initial estimate of unwanted elements  -->
 <!--  -->

 <xsl:template match="text:sequence-decls"/>



<xsl:template match="*" >
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/<xsl:value-of select="name()"/>******
  (<xsl:value-of select="@text:style-name"/>)
    </xsl:message>
</xsl:template>


</xsl:stylesheet>
