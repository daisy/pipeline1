<?xml version="1.0" encoding="utf-8"?>
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

                version="1.0">
<d:doc xmlns:d="rnib.org.uk/ns#">
 <revhistory>
   <purpose><para>This stylesheet uses XSLT 1.0. Processes ODF
tables. Imported from odf2daisy.xsl </para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2006-02-23T13:52:18.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</revremark>
   </revision>
  </revhistory>
  </d:doc>
 
  <xsl:output method="xml" indent="yes"/>

 

 <!-- table root -->
  <xsl:template match="table:table">
  <xsl:if test="$debug">
      <xsl:message>
        Processing body / table
      </xsl:message>
    </xsl:if>
    <table>
      <xsl:apply-templates select="following-sibling::text:p[@text:style-name = 'Table'][1]" mode="caption"/>
      <xsl:apply-templates/></table>
  </xsl:template>

 <!--  -->
 <!--Process the table caption  -->
 <!--  -->
 <xsl:template match="text:p[@text:style-name = 'Table']" mode="caption">
   <caption>
     <xsl:apply-templates/>
   </caption>
 </xsl:template>

 <!-- Unwanted. Processed within the table -->
 <xsl:template match="text:p[@text:style-name = 'Table']" />

 <!-- Currently not processed for dAISY -->
  <xsl:template match="table:table-column"/>


<!-- DAISY does not differentiate between header rows and ordinary rows -->
  <xsl:template match="table:table-header-rows">
    <xsl:apply-templates/>
  </xsl:template>

 
  <xsl:template match="table:table-row">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

 <!-- Headers -->
 <xsl:template match="table:table-header-rows/table:table-row/table:table-cell">
    <th><xsl:apply-templates/></th>
  </xsl:template>

 <xsl:template match="table:table-cell">
   <xsl:choose>
     <xsl:when test="@table:number-columns-spanned">
       <xsl:call-template name="rspan">
         <xsl:with-param name="spans" select="@table:number-columns-spanned"/>
       </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>
       <td><xsl:apply-templates/></td>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <!-- Repeat spans -->
 <xsl:template name="rspan">
   <xsl:param name="spans" select="0"/>
   <xsl:message>
 rspan:    <xsl:value-of select="$spans"/>
   </xsl:message>
   <td><xsl:apply-templates/></td>
   <xsl:if test="$spans > 0 ">
       <xsl:call-template name="rspan">
         <xsl:with-param name="spans" select="$spans - 1"/>
       </xsl:call-template>
   </xsl:if>
 </xsl:template>

 <!-- Dummy for omitted cells -->
 <xsl:template match="table:covered-table-cell"/>


 <!-- p is valid within td, but not needed -->
 <xsl:template match="table:table-cell/text:p">
   <xsl:apply-templates/>
 </xsl:template>

 <!--
<xsl:template match="*" >
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/<xsl:value-of select="name()"/>******
    </xsl:message>
</xsl:template> 
-->
</xsl:stylesheet>
