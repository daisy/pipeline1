<?xml version="1.0" encoding="utf-8"?>
<!-- 
#
# odf2daisy.body.xsl,  Dave Pawson; http://www.dpawson.co.uk
# 
# Process the body elements of an odf writer file
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
  xmlns:dp="http://www.dpawson.co.uk/ns#"             
exclude-result-prefixes="xsi xsd xforms dom oooc ooow ooo 
                         script form math dr3d chart svg 
                         number meta dc xlink fo draw table 
                         text style office xdt xs
                         xalan exslt d dp"

                version="2.0">



  <xsl:preserve-space elements="Preformatted_20_Text"/>



 <!-- Process tables of ...content, illustrations etc -->

 <xsl:include href="odf2daisy.tableOf.xsl"/>

<d:doc >
 <revhistory>
   <purpose><para>This stylesheet uses XSLT 1.0. Processes ODF body content. Imported from odf2daisy.xsl </para></purpose>
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
  <xsl:output method="xml"/>



 

  <xsl:template match="office:body">
    <xsl:if test="$debug">
      <xsl:message>
        Processing //office:body 
    </xsl:message>
  </xsl:if>
  <xsl:apply-templates/>
  </xsl:template>


<!-- Set up a variable holding the style information -->
<xsl:variable name="styles" select="document($stylefile)/*" as="item()*"/>



<xsl:template match="office:text">
  <xsl:for-each-group select="*" 
    group-starting-with="text:h[@text:style-name=$level1]">
    <xsl:apply-templates select="." />
  </xsl:for-each-group>
  <!--
  <xsl:message>
    <xsl:value-of select="$level1"/>.
    <xsl:value-of select="$level2"/>.
    <xsl:value-of select="$level3"/>.
    <xsl:value-of select="$level4"/>.
    <xsl:value-of select="$level5"/>.
    <xsl:value-of select="$level6"/>.
  </xsl:message>
-->
</xsl:template>



<xsl:variable name="level1" select="$styles//style[dname='Heading 1'][1]/@name"/>
<xsl:variable name="level2" select="$styles//style[dname='Heading 2'][1]/@name"/>
<xsl:variable name="level3" select="$styles//style[dname='Heading 3'][1]/@name"/>
<xsl:variable name="level4" select="$styles//style[dname='Heading 4'][1]/@name"/>
<xsl:variable name="level5" select="$styles//style[dname='Heading 5'][1]/@name"/>
<xsl:variable name="level6" select="$styles//style[dname='Heading 6'][1]/@name"/>

 <!-- Heading 1 -->
 <xsl:template match="text:h[@text:style-name=$level1]" >
   <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
       </xsl:element>
       <xsl:for-each-group 
         select="current-group() except ."
         group-starting-with="text:h[@text:style-name=$level2]">
         <xsl:apply-templates select="."/>
       </xsl:for-each-group>
     </xsl:element>
   </xsl:template>



 <!-- Heading 2 -->
 <xsl:template match="text:h[@text:style-name=$level2]" >
 <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
     </xsl:element>
  
     <p/>
     <xsl:for-each-group 
       select="current-group() except ."
       group-starting-with="text:h[@text:style-name=$level3]">
       <xsl:apply-templates select="."/>
     </xsl:for-each-group>
   </xsl:element>
 </xsl:template>




 <!-- Heading 3 -->
 <xsl:template match="text:h[@text:style-name=$level3]" >
   <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
       </xsl:element>
       
       <p/>
       <xsl:for-each-group 
         select="current-group() except ."
       group-starting-with="text:h[@text:style-name=$level4]">
         <xsl:apply-templates select=" ."/>
       </xsl:for-each-group>
     </xsl:element>
   </xsl:template>



 <!-- Heading 4-->
 <xsl:template match="text:h[@text:style-name=$level4]" >
 <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
     </xsl:element>
 
     <p/>
     <xsl:for-each-group 
       select="current-group() except ."
       group-starting-with="text:h[@text:style-name=$level5]">
       <xsl:apply-templates select=" ."/>
     </xsl:for-each-group>
   </xsl:element>
 </xsl:template>

 <!-- Heading 5-->
 <xsl:template match="text:h[@text:style-name=$level5]" >
 <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
     </xsl:element>
   
     <p/>
     <xsl:for-each-group 
       select="current-group() except ."
       group-starting-with="text:h[@text:style-name=$level6]">
       <xsl:apply-templates select=" ."/>
     </xsl:for-each-group>
   </xsl:element>
 </xsl:template>




 <!-- Heading 6-->
 <xsl:template match="text:h[@text:style-name=$level6]" >
 <xsl:variable name="level" select="dp:levelFromName(@text:style-name)" as="xs:string"/>
   <xsl:element name="{concat('level',$level)}">
     <xsl:element name="{concat('h',$level)}">
       <xsl:apply-templates />
     </xsl:element>
  
     <p/>
       <xsl:apply-templates select="*[not(self::text:h)]"/>
  
   </xsl:element>
 </xsl:template>


<xsl:template match="text:section">
  <div>
    <xsl:apply-templates/>
  </div>
</xsl:template>



<xsl:template match="text:h[not(string(.))]" priority="0.4"/>


 <!--       -->
 <!-- Lists -->
 <!--       -->


 <!-- bullet style lists -->
<xsl:key name="bulletedLists" 
  match="text:list-style[text:list-level-style-bullet]" 
  use="@style:name"/>

 <!-- Numbered list style -->
 <xsl:key name="numberedLists" 
   match="text:list-style[text:list-level-style-number]" 
   use="@style:name"/>

 <!--  -->
 <!-- List processing -->
 <!-- Note: text:p @text:style-name='List' treated as para -->
 <xsl:template match="text:list[text:list-item]" >
   <list>
     <xsl:choose>
       <xsl:when test="key('bulletedLists',@text:style-name)">
         <xsl:attribute name="type">
           <xsl:text>ul</xsl:text>
         </xsl:attribute>
       </xsl:when>
       <xsl:when test="key('numberedLists',@text:style-name)">
         <xsl:attribute name="type">
           <xsl:text>ol</xsl:text>
         </xsl:attribute>
       </xsl:when>
       <xsl:otherwise>
         <xsl:attribute name="type">
           <xsl:text>pl</xsl:text>
         </xsl:attribute>         
       </xsl:otherwise>
     </xsl:choose>
     <xsl:apply-templates/>
   </list>
 </xsl:template>


<!-- Ignore empty lists -->
 <xsl:template match="text:list[not(text:list-item)]"  />


 <!-- List item -->
 <!-- Needed li markup here, to cater for nested lists -->
<xsl:template match="text:list/text:list-item" >
  <li>  <xsl:apply-templates/></li>
</xsl:template>

 <xsl:template match="text:p[parent::list-item]" priority="0.6"  >
   <xsl:apply-templates/>
 </xsl:template>





 <!-- Default text. Processed lower priority for overrides -->
<xsl:template match="text:p[string(.)]"  >
   <p><xsl:apply-templates /></p>
</xsl:template>


<xsl:template match="text:p[not(string(.))]" />


 <!-- Fixed format, respect ws -->
<xsl:template match="text:p[@text:style-name='Preformatted_20_Text']" priority="0.6"  >
  <samp>
    <xsl:apply-templates/>
</samp>
</xsl:template>





 <!-- Annotations Parent may be a para -->
 <xsl:template match="text:p[office:annotation][not(ancestor::table:table-row)]"  >
   <p>
   <xsl:apply-templates  mode="note"/>
 </p>
     <xsl:apply-templates select="office:annotation"/>
 </xsl:template>

 <!-- Processing text embedded in a note -->
 <xsl:template match="text()" mode="note" >
   <xsl:value-of select="."/>
 </xsl:template>


 


 <xsl:template match="office:annotation" mode="note">
   <noteref idref="{generate-id()}" class="annotation">
     <xsl:text>note </xsl:text>
   </noteref>
 </xsl:template>


 <!-- Date should not produce metadata -->
 <xsl:template match="office:annotation/dc:date"  >
   <p>Date.
<xsl:apply-templates/>
</p>
 </xsl:template>

   <xsl:template match="office:annotation/dc:creator">
     <p>Creator: <xsl:apply-templates/></p>
  </xsl:template>


<xsl:template match="office:annotation">
   <note id="{generate-id()}" class="annotation">
     <xsl:apply-templates/>
   </note>
 </xsl:template>



 



 <!--  -->
 <!-- Footnotes  -->
 <!--  -->
 <xsl:template match="text:p[text:note]"  priority="0.6">
   <p>
<xsl:apply-templates  mode="footnote"/>
   </p>
   <xsl:apply-templates select="text:note"/>
 </xsl:template>


 <xsl:template match="text:note" mode="footnote">
   <noteref idref="{@text:id}" class="{@text:note-class}">
     <xsl:value-of select="text:note-citation"/>
   </noteref>
 </xsl:template>

 <xsl:template match="text:note-citation"/>


 <xsl:template match="text:note">
   <note id="{@text:id}" class="{@text:note-class}">
     <xsl:apply-templates/>
   </note>
 </xsl:template>

 <xsl:template match="text:note-body">
   <xsl:apply-templates/>
 </xsl:template>


<!--  -->
<!-- Change information  -->
<!--  -->


<xsl:template match="text:tracked-changes"  >
  <div class="trackedChanges">
    <xsl:apply-templates/>    
  </div>
</xsl:template>


<xsl:template match="text:changed-region" >
  <div >
    <p> <a href="#{@text:id}">Change</a> </p>
    
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="text:deletion">
  <p>Deletion: <xsl:apply-templates select="office:change-info" mode="change"/></p>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="text:insertion">
  <p>Insertion: <xsl:apply-templates select="office:change-info" mode="change"/></p>
  <xsl:apply-templates/>
</xsl:template>



<xsl:template match="office:change-info"/>

<xsl:template match="office:change-info" mode="change">
  <span><xsl:value-of select="dc:creator"/> <xsl:text> </xsl:text> <xsl:value-of select="dc:date"/></span>
</xsl:template>



<xsl:template match="text:change">
  <a class="change" id="{@text:change-id}"/>
</xsl:template>

<xsl:template match="text:change-start">
  <a class="changeStart" rel="{@text:change-id}"/>
</xsl:template>

<xsl:template match="text:change-end">
  <a class="changeStart" rev="{@text:change-id}"/>
</xsl:template>







 <!--  -->
 <!-- Images. All assumed to be block progression direction.  -->
 <!-- Does not process text:p in draw:text-box -->

<xsl:template match="text:p [draw:frame][not(ancestor::draw:text-box)]"  >
  <p>
  
    <xsl:apply-templates/>
  </p>
</xsl:template>

 <!-- External images only -->
<xsl:template match="draw:frame[.//draw:image]" >
  <imggroup>
    <xsl:apply-templates select=".//draw:image"/>
    <caption>
      <xsl:apply-templates select="draw:text-box/text:p"/>
    </caption>
</imggroup>
</xsl:template>

<xsl:template match="draw:frame/draw:text-box"/>

<xsl:template match="draw:frame" priority="0.1">
  <xsl:apply-templates/>
</xsl:template>



 <!--The wrapping text-box is processed by the ancestor.   -->
 <xsl:template match="draw:text-box/text:p">
   <xsl:for-each select="text()|text:sequence">
     <xsl:value-of select="."/>
   </xsl:for-each>
 </xsl:template>


<!-- sequence references. Reference to an inserted object -->
<xsl:template match="text:sequence-ref">
  <span class="reference"><xsl:apply-templates/></span>
</xsl:template>


 <!-- The actual image -->
 <xsl:template match="draw:image">
   <img title=" " src="{@xlink:href}" alt="{../svg:desc} "/>
 </xsl:template>


 <!-- Audio inserts. -->

 <xsl:template match="draw:frame[.//draw:frame][contains(.//draw:plugin/@draw:mime-type,'vnd.sun.star.media')]">
   <xsl:comment>Abusing the a href use case</xsl:comment>
   <xsl:comment>Mime type is <xsl:value-of select=".//draw:plugin/@draw:mime-type"/></xsl:comment>
   <a href="{.//draw:plugin/@xlink:href}" title="Audio file" type="audio" >
     <xsl:value-of select=".//text:p/text()"/>
   </a>
 </xsl:template>


 <xsl:template match="draw:plugin"/>

 <!-- Bookmarks. Converted to an anchor element -->
 <xsl:template match="text:bookmark[string(@text:name)]">
   <a id="{@text:name}"/>
 </xsl:template>

  <xsl:template match="text:bookmark-start[string(@text:name)]">
    <a id="{@text:name}"/>
  </xsl:template>


  <xsl:template match="text:bookmark-end"/>


<!-- Reference marks -->
<xsl:template match="text:reference-mark">
  <a id="{@text:name}"/>
</xsl:template>



 <!--  bookmark references to links. No hot text. FIXME-->
 <xsl:template match="text:bookmark-ref">
   <a href="{translate(@text:ref-name,' ()','___')}">
     <xsl:text> link </xsl:text>
   </a>
 </xsl:template>

 <!-- Page count -->
<xsl:template match="text:page-count">
  <xsl:apply-templates/>
</xsl:template>



 <!-- External link -->
<xsl:template match="text:a">
  <a href="{@xlink:href}"><xsl:apply-templates/>[<xsl:value-of select="@xlink:href"/>]</a>
</xsl:template>

 <!--                 -->
 <!-- Inline elements -->
 <!--                 -->

 <xsl:template match="text:span" priority="0.4"  >
   <span>
     <xsl:choose>
       <xsl:when test="@text:style-name = exslt:node-set($styles)/d:name ">
         <xsl:attribute name="class">
           <xsl:text>emphasis</xsl:text>
         </xsl:attribute>
       </xsl:when>
       <xsl:otherwise>
           <xsl:attribute name="class">
             <xsl:value-of select="@text:style-name"/>
         </xsl:attribute>
       </xsl:otherwise>
     </xsl:choose>
     <xsl:apply-templates/>
   </span>
 </xsl:template>


 <!-- text:sequence: Just an inline wrapper? -->
 <xsl:template match="text:sequence">
   <span class="{@text:name}"><xsl:apply-templates/></span>
 </xsl:template>

 <!-- line break -->
 <xsl:template match="text:line-break">
   <br />
 </xsl:template>

 <!-- preserve tab and space -->
 <xsl:template match="text:tab">
   <xsl:text>&#x09;</xsl:text>
 </xsl:template>

 <xsl:template match="text:s">
   <xsl:text> </xsl:text>
 </xsl:template>

 <!-- Fields -->
 <xsl:template match="text:date">
   <span class="date" ><xsl:apply-templates/></span>
 </xsl:template>
 <xsl:template match="text:time">
   <span class="time" ><xsl:apply-templates/></span>
 </xsl:template>


 <!--  -->
 <!-- Hard Page breaks  -->
 <!--  -->

 <xsl:key name="pageBreaks" 
   match="style:paragraph-properties[@fo:break-before='page']"
   use ="@fo:break-before"/>



   <xsl:template match="text:p [@text:style-name=key('pageBreaks','page')/../@style:name]"  >

     <xsl:if test="$debug">
     <xsl:message>
       Found page number, <xsl:value-of select="count(preceding::text:p[@text:style-name=
                             key('pageBreaks','page')/../@style:name]) + 1"/>
     </xsl:message>
   </xsl:if>
   <p>
     <pagenum id="{generate-id()}"
       page='normal'>
       <xsl:value-of select="count(preceding::text:p[@text:style-name=
                             key('pageBreaks','page')/../@style:name]) + 1"/>
     </pagenum><xsl:text>&#x20;</xsl:text>
     <xsl:apply-templates/>
   </p>
   </xsl:template>




 <!-- Page numbers manually inserted. Refers to current pagenumber -->

 <xsl:template match="text:page-number">
   <span class="pagenum"><xsl:apply-templates/></span>
 </xsl:template>


<xsl:template match="text:subject">
   <span class="documentSubject"><xsl:apply-templates/></span>
 </xsl:template>

<xsl:template match="text:title">
   <span class="documentTitle"><xsl:apply-templates/></span>
 </xsl:template>

<xsl:template match="text:initial-creator">
   <span class="documentCreator"><xsl:apply-templates/></span>
 </xsl:template>


 <!-- index marks -->
 <!-- Manual entry -->
 <xsl:template match="text:alphabetical-index-mark">
   <a class="{@text:string-value}" id="{generate-id()}"/>
    
 </xsl:template>

 <!-- Spanned index terms. Start and end. -->
 <!-- Note that all 'similar' terms will have same @text:id value -->
 <xsl:template match="text:alphabetical-index-mark-start">
   <a class="indexMark.start" id="{concat(@text:id,'.',generate-id())}"/>
   
 </xsl:template>

 <!-- End mark. Provisionally not processed for DAISY -->
<xsl:template match="text:alphabetical-index-mark-end">
  <!--  <a class ="indexMark.end" 
       id="{concat(@text:id,'.',generate-id())}"/> -->
</xsl:template>




 <!-- all internal text definitions (text:reference-ref
text:ref-name="x" -->
 <!-- Key which tells us if the id value is unique or not -->
 <xsl:key name="ids" match="text:reference-ref" use="@text:ref-name"/>
 <!-- Internal cross references -->
 <xsl:template match="text:reference-ref">
   <xsl:choose>
        <xsl:when test="count(key('ids',@text:ref-name)) > 1">
     <xsl:message>
       Error: More than one id with value {<xsl:value-of select="@text:ref-name"/>}
     </xsl:message>
   <xsl:comment> Note that  id value [<xsl:value-of select="@text:ref-name"/>] is not  unique to the document.   </xsl:comment>
   </xsl:when>
   <xsl:otherwise>
      <a id="{translate(@text:ref-name,' ()','___')}"/>
   </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template match="text:user-field-get">
   <xsl:apply-templates/>

 </xsl:template>





 <!--  -->
 <!-- Drawing items SVG rather than images  -->
 <!--  -->

 <xsl:template match="draw:frame[
                      descendant::draw:rect |
                      descendant::draw:line |
                      descendant::draw:polyline|
                      descendant::draw:polygon|
                      descendant::draw:regular-polygon|
                      descendant::draw:path|
                      descendant::draw:circle|
                      descendant::draw:ellipse|
                      descendant::draw:g|
                      descendant::draw:page-thumbnail|
                      descendant::draw:measure|
                      descendant::draw:custom-shape]">
   <prodnote id="{generate-id()}" class='graphic' render="required">
     <p>     <xsl:text>Vector graphical information. Not reproduced.</xsl:text> </p>
     <xsl:apply-templates select="descendant::text:p" mode="svg"/>
   </prodnote>
 </xsl:template>

 <xsl:template match="draw:frame//text:p" mode="svg">
   <p><xsl:apply-templates/></p>
 </xsl:template>



 <xsl:template match="draw:line|draw:rect|draw:custom-shape">
  
 </xsl:template>


 <!-- Candidate unprocessed elements. -->

 <xsl:template match="office:automatic-styles|office:forms "/>


<!-- OLD code, based on structure information -->
 <xsl:template name="heading" >
   <xsl:variable name='thisHead' select="@level" as="xs:integer"/>
   <xsl:variable name='nextHead' select="($thisHead,following-sibling::heading[1]/@level)[last()]" as="xs:integer"/>
   
   <xsl:choose>
     <xsl:when test="(max(($thisHead,$nextHead)) - min(($thisHead,$nextHead))) le 1">
       <xsl:if test="$debug">
       <xsl:message>
         Yes. OK
       </xsl:message>
     </xsl:if>
     </xsl:when>
     <xsl:otherwise>
       <xsl:message terminate='yes'>
         Structure Error. This heading level <xsl:value-of select="$thisHead"/>, followed by  <xsl:value-of select="$nextHead"/>
       Heading is in position <xsl:value-of select="count(preceding::heading) + 1 "/> 
       <xsl:if test="string(.)">
         Heading content is: "<xsl:value-of select="."/>"
       </xsl:if>
       </xsl:message>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>



<!-- Look up the level from the stylename -->
 <xsl:function name="dp:levelFromName" as="xs:string">
   <xsl:param name="sname" as="xs:string"/>
     <xsl:choose>
       <xsl:when test="$styles/style[$sname=@name]/mapsTo">
         <xsl:variable name="elName" select="$styles/style[$sname=@name][1]/mapsTo"/>
         <xsl:choose>
           <xsl:when test="contains($elName,'level')">
             <xsl:value-of select="substring($elName,string-length($elName),1)"/>
           </xsl:when>
           <xsl:otherwise>
             <xsl:message terminate="yes">
               Unable to generate a level from style <xsl:value-of select="$sname"/>. No mapping provided by styles.
             </xsl:message>
           </xsl:otherwise>
         </xsl:choose>
       </xsl:when>
       <xsl:otherwise>
         <xsl:message terminate="yes">
           Unable to generate a level from style <xsl:value-of select="$sname"/>
         </xsl:message>
       </xsl:otherwise>
     </xsl:choose>
 </xsl:function>

<!-- find the name of a style, given a daisy 'levelX' element name -->

<xsl:function name="dp:getStyleName" as="xs:string">
  <xsl:param name="level" as="xs:string"/>

  <xsl:choose>
    <xsl:when test="$styles/style[mapsTo=$level]">
      <xsl:value-of select="$styles/style[mapsTo=$level]/@name"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        Unable to find a style matching "<xsl:value-of select="$level"/>" in the styles file
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:function>

</xsl:stylesheet>
