<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odf2daisy.tableOf.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file, tocs etc  content.
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
# 
# Generates a daisy output file to dtbook-2005-1
#
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns='http://www.daisy.org/z3986/2005/dtbook/'
xmlns:d="rnib.org.uk/ns#"
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

xmlns:xalan="http://xml.apache.org/xalan"
xmlns:exslt="http://exslt.org/common"
             
exclude-result-prefixes="xsi xsd xforms dom oooc ooow ooo 
                         script form math dr3d chart svg 
                         number meta dc xlink fo draw table 
                         text style office xdt xs
                         xalan exslt d"

                version="1.0">
<d:doc >
 <revhistory>
   <purpose><para>This stylesheet uses XSLT 1.0. Processes ODF body
content - table of contents, table of illustrations etc. . Imported (included)
from odf2daisy.xsl </para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2006-02-27T12:37:35.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</revremark>
   </revision>
  </revhistory>
  </d:doc>
  <xsl:output method="xml"/>

 
 <!-- Table of Contents handling. Main toc -->
 <xsl:template match="text:table-of-content">
   <xsl:apply-templates/>
 </xsl:template>



 <xsl:template match="text:index-body[text:p/text:a]" priority="2">
   <div class='toc'>
     <xsl:choose>
    <xsl:when test="not(text:index-title)">
      <bridgehead>Table of contents</bridgehead>
     </xsl:when>
     <xsl:otherwise>
       <xsl:apply-templates select="text:index-title"/>
     </xsl:otherwise>
   </xsl:choose>

   <list type="pl">
     <xsl:for-each select="//text:h">
       <li>
         <a href="#{generate-id()}">
           <xsl:value-of select="."/>
         </a>
       </li>
     </xsl:for-each>
   </list>
 </div>
 </xsl:template>

 <!-- Title, if available  -->
 <xsl:template match="text:index-title">
   <brideghead><xsl:apply-templates /></brideghead>
 </xsl:template>


 <!-- No content in this element. Layout only -->
 <xsl:template match="text:table-of-content-source"/>




 <!-- No p element wrapper -->
 <xsl:template match="text:index-title/text:p" priority="0.6">
   <xsl:apply-templates />
 </xsl:template>


 <xsl:template match="text:index-body/text:p">
   <li><xsl:apply-templates/></li>
 </xsl:template>

 <xsl:template match="text:index-body/text:p/text:tab" priority="0.6">
   <xsl:text>.&#x09;&#x09;Page </xsl:text>
</xsl:template>

 <!-- Index of Tables -->
 <xsl:template match="text:table-index">
   <level class="tableToc">
     <xsl:apply-templates/>
   </level>
 </xsl:template>

<!-- No content in this element. Layout only -->
 <xsl:template match="text:table-index-source"/>


 <!-- Table of Illustrations -->
 <xsl:template match="text:illustration-index">
  <level class="tableToc">
     <xsl:apply-templates/>
   </level>   
 </xsl:template>

<!-- No content in this element. Layout only -->
 <xsl:template match="text:illustration-index-source"/>



 <!--  -->
 <!--Indexes  -->
 <!--  -->

 <xsl:template match="text:alphabetical-index">
   <xsl:comment>
     <xsl:text>How should an index be marked up? </xsl:text>
   </xsl:comment>
   <level>
     <xsl:apply-templates/>
   </level>
 </xsl:template>

 <!-- Provides no usable output -->
 <xsl:template match="text:alphabetical-index-source"/>


 <!-- Index body Note, this is not (necessarily) the toc index-body -->

 <xsl:template match="text:index-body">
  <list type="pl">
    <xsl:apply-templates/>
  </list>
 </xsl:template>


 <xsl:template match="text:index-title">
  <hd><xsl:apply-templates /></hd>
 </xsl:template>

 <!-- No p element wrapper -->
 <xsl:template match="text:index-title/text:p" >
  <xsl:apply-templates />
 </xsl:template>




 <xsl:template match="text:index-body/text:p[not(text:a)]" priority="2">
  <li>
     <lic class="entry"><xsl:value-of select="text()[1]"/></lic>
  <lic class="pagenum"><xsl:value-of select="text()[2]"/></lic>
  </li>
 </xsl:template>


 <xsl:template match="text:index-title">
   <hd><xsl:apply-templates/></hd>
 </xsl:template>

 <xsl:template match="text:index-body/text:p" priority="0.6">
   <li><xsl:apply-templates mode="toc"/></li>
 </xsl:template>



</xsl:stylesheet>
