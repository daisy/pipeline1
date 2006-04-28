<!--
 * WordML2DTBook
 * Copyright © 2006 The Swedish Library of Talking Books and Braille, TPB (www.tpb.se)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -->
 <xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml"
	xmlns:v="urn:schemas-microsoft-com:vml"
	xmlns:w10="urn:schemas-microsoft-com:office:word"
	xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core"
	xmlns:aml="http://schemas.microsoft.com/aml/2001/core"
	xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint"
	xmlns:o="urn:schemas-microsoft-com:office:office"
	xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882"
	xmlns:st1="urn:schemas-microsoft-com:office:smarttags"
	xmlns:d="http://www.tpb.se/word2xmlstylesheet"
	xmlns:meta="rnib.org.uk/tbs#"
	xmlns="http://www.daisy.org/z3986/2005/dtbook/"
	exclude-result-prefixes="w v w10 sl aml wx o dt st1 d meta">
	
<meta:doc xmlns:meta="rnib.org.uk/tbs#">
	<meta:revhistory>
		<meta:purpose>
			<meta:para>This stylesheet uses WordML to produce DTBook</meta:para>
		</meta:purpose>
		<meta:revision>
			<meta:revnumber>1.0</meta:revnumber>
			<meta:date>30 August  2005</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Initial issue with DMFC in mind</meta:para>
			</meta:revdescription>
			<meta:revremark>
				<meta:para>Issues:</meta:para>
				<meta:para>Only flat lists work</meta:para>
				<meta:para>No bold or italic other than characterstyles</meta:para>
				<meta:para>Tables don't come out right if they include rowspanning</meta:para>
			</meta:revremark>
		</meta:revision>
		<meta:revision>
			<meta:revnumber>1.1</meta:revnumber>
			<meta:date>28 October  2005</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Added support for standard footnotes</meta:para>
			</meta:revdescription>
			<meta:revremark/>
		</meta:revision>
		<meta:revision>
			<meta:revnumber>1.1.01</meta:revnumber>
			<meta:date>7 November  2005</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Removed XE-data</meta:para>
			</meta:revdescription>
			<meta:revremark/>
		</meta:revision>
		<meta:revision>
			<meta:revnumber>1.1.1</meta:revnumber>
			<meta:date>28 December  2005</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Removed spaces in pagenum id's</meta:para>
				<meta:para>Fixed a problem with smartTags</meta:para>
			</meta:revdescription>
			<meta:revremark/>
		</meta:revision>
		<meta:revision>
			<meta:revnumber>1.1.11</meta:revnumber>
			<meta:date>28 March  2006</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Fixed a problem with lists.</meta:para>
			</meta:revdescription>
			<meta:revremark/>
		</meta:revision>
		<meta:revision>
			<meta:revnumber>1.2</meta:revnumber>
			<meta:date>28 April  2006</meta:date>
			<meta:authorinitials>JoelH</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Externalized tag set definitions and created an XML schema for them.</meta:para>
				<meta:para>Fixed a precedence problem.</meta:para>
				<meta:para>Added attribute "merge" to wrap element (tag set definitions). The effect is that all consecutive paras using the same style will be included in the same outer element.</meta:para>
				<meta:para>Added element "attribute" to wrap element (tag set definitions).</meta:para>
				<meta:para>Added support for rowspan in tables</meta:para>
				<meta:para>Added support for image alt-text</meta:para>
				<meta:para>Changed naming convention for images</meta:para>
				<meta:para>Added page-attribute: normal (for arabic numerals), front (for roman numerals), special (otherwise)</meta:para>
			</meta:revdescription>
			<meta:revremark/>
		</meta:revision>
	</meta:revhistory>
</meta:doc>

<xsl:output method="xml" indent="no" encoding="UTF-8" 
	doctype-public="-//NISO//DTD dtbook 2005-1//EN"
	doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd"/>

<xsl:param name="tagSet" select="'4'"/>
<xsl:param name="customStyle" select="'tpb'"/>
<xsl:param name="mapset" select="document('custom-tagset.xml')|document('default-tagset.xml')"/>

<xsl:key name="matchStyle" match="/w:wordDocument/w:styles/w:style" use="@w:styleId"/>

<xsl:template match="w:wordDocument">
	<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="dtbook2xhtml.xsl"</xsl:processing-instruction>
	<xsl:variable name="revision" select="document('')//meta:doc/meta:revhistory/meta:revision[last()]"/>
	<xsl:variable name="version" select="$revision/meta:revnumber"/>
	<xsl:variable name="date" select="$revision/meta:date"/>
	<xsl:comment xml:space="preserve">
		WordML2DTBook
		rev: <xsl:value-of select="$version"/>
		date: <xsl:value-of select="$date"/>
	</xsl:comment>
	<dtbook>
		<head/>
		<book>
			<bodymatter>
				<xsl:apply-templates select="w:body"/>
			</bodymatter>
			<xsl:if test="count(//w:footnote[ancestor::w:body])&gt;0">
				<rearmatter>
					<level1>
						<xsl:apply-templates select="//w:footnote[ancestor::w:body]" mode="rearmatter"/>
					</level1>
				</rearmatter>
			</xsl:if>
		</book>
	</dtbook>
</xsl:template>

<xsl:template match="w:body">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="wx:sub-section">
	<xsl:variable name="ename" select="concat('level', count(ancestor-or-self::wx:sub-section))"/>
	<xsl:element name="{$ename}">
		<xsl:apply-templates/>
	</xsl:element>
</xsl:template>

<xsl:template match="wx:sect">
	<xsl:if test="count(*[not(self::wx:sub-section)])&gt;0">
		<level1>
			<xsl:apply-templates select="*[not(self::wx:sub-section)]"/>
		</level1>
	</xsl:if>
	<xsl:apply-templates select="*[self::wx:sub-section]"/>
</xsl:template>

<!-- Begin list -->
<xsl:template match="w:p[w:pPr/w:listPr and count(preceding-sibling::w:p[1][w:pPr/w:listPr])=0]" priority="10"> 
	<list type="pl"><xsl:apply-templates select="." mode="processList"/></list>
</xsl:template>

<!-- Not the beginning of the list -->
<xsl:template match="w:p[w:pPr/w:listPr]" priority="5"/>

<!-- process next list-item -->
<xsl:template match="w:p[w:pPr/w:listPr]" mode="processList">
	<li>
		<xsl:value-of select="w:pPr/w:listPr/wx:t/@wx:val"/>
		<xsl:text> </xsl:text>
		<!-- <xsl:apply-templates select="w:r"/> -->
		<xsl:apply-templates/>
	</li>
	<xsl:apply-templates select="following-sibling::w:p[1]" mode="processList"/>
</xsl:template>

<!-- override the default rule for this mode, needed for the last call above -->
<xsl:template match="*" mode="processList"/>

<xsl:template match="w:p" mode="processBlock">
	<xsl:param name="pStyleName"/>
	<xsl:variable name="styleName" select="key('matchStyle', w:pPr/w:pStyle/@w:val)/w:name/@w:val"/>
	<xsl:variable name="cTags" select="$mapset//d:custom[@style=$customStyle]/d:paragraphs/d:tag[@name=$styleName]"/>
	<xsl:variable name="sTags" select="$mapset//d:standardWord[@version=$tagSet]/d:paragraphs/d:tag[@name=$styleName]"/>
	<xsl:variable name="tag" select="$cTags[1] | ($sTags[count($cTags)=0])[1]"/>
	
	<xsl:if test="$styleName=$pStyleName">
		<xsl:element name="{$tag/d:wrap/@using}">
			<xsl:apply-templates select="descendant::w:r"/>
		</xsl:element>
		<xsl:apply-templates select="following::w:p[1]" mode="processBlock">
			<xsl:with-param name="pStyleName" select="$styleName"/>
		</xsl:apply-templates>
	</xsl:if>
</xsl:template>

<!-- override the default rule for this mode, needed for the last call above -->
<xsl:template match="*" mode="processBlock"/>

<xsl:template match="w:p">
	<!-- <xsl:variable name="style" select="key('matchStyle', w:pPr/w:pStyle/@w:val)"/> -->
	<xsl:variable name="styleName" select="key('matchStyle', w:pPr/w:pStyle/@w:val)/w:name/@w:val"/>
	<xsl:variable name="cTags" select="$mapset//d:custom[@style=$customStyle]/d:paragraphs/d:tag[@name=$styleName]"/>
	<xsl:variable name="sTags" select="$mapset//d:standardWord[@version=$tagSet]/d:paragraphs/d:tag[@name=$styleName]"/>
	<xsl:variable name="tag" select="$cTags[1] | ($sTags[count($cTags)=0])[1]"/>
	
	<xsl:choose>
		<!-- found a matching action -->
		<xsl:when test="count($tag)&gt;0">
			<xsl:choose>
				<xsl:when test="$tag/d:map">
					<xsl:element name="{$tag/d:map/@value}">
						<xsl:apply-templates select="descendant::w:r"/>
					</xsl:element>
				</xsl:when>
				<!-- wrap, but only if merge=false or if merge=true and this style name is different from the preceding -->
				<xsl:when test="$tag/d:wrap">
					<xsl:variable name="pStyleName" select="string(key('matchStyle', preceding::w:p[1]/w:pPr/w:pStyle/@w:val)/w:name/@w:val)"/>
					<xsl:if test="$tag/d:wrap/@merge='false' or $pStyleName!=$styleName">
						<!--
						<xsl:comment>¤¤Merge:<xsl:value-of select="$tag/d:wrap/@merge"/>:¤¤</xsl:comment>
						<xsl:comment>¤¤pStyleName:<xsl:value-of select="$pStyleName"/>:¤¤</xsl:comment>
						<xsl:comment>¤¤styleName:<xsl:value-of select="$styleName"/>:¤¤</xsl:comment>-->
						<xsl:element name="{$tag/d:wrap/@value}">
							<xsl:if test="$tag/d:wrap/@addId='true'">
								<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
							</xsl:if>
							<xsl:for-each select="$tag/d:wrap/d:attribute">
								<xsl:attribute name="{@name}"><xsl:value-of select="@value"/>
							</xsl:attribute>
							</xsl:for-each>
							<xsl:element name="{$tag/d:wrap/@using}">
								<xsl:apply-templates select="descendant::w:r"/>
							</xsl:element>
							<xsl:if test="$tag/d:wrap/@merge='true'">
								<xsl:apply-templates select="following::w:p[1]" mode="processBlock">
									<xsl:with-param name="pStyleName" select="$styleName"/>
								</xsl:apply-templates>
							</xsl:if>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$tag/d:comment">
					<xsl:comment><xsl:apply-templates select="descendant::w:r"/></xsl:comment>
				</xsl:when>
			</xsl:choose>
		</xsl:when>
		<!-- no matching action found for this paragraph style -->
		<xsl:otherwise><p><xsl:apply-templates select="descendant::w:r"/></p></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="w:r">
	<xsl:variable name="styleName" select="key('matchStyle', w:rPr/w:rStyle/@w:val)/w:name/@w:val"/>
	<xsl:variable name="cTags" select="$mapset//d:custom[@style=$customStyle]/d:character/d:tag[@name=$styleName]"/>
	<xsl:variable name="sTags" select="$mapset//d:standardWord[@version=$tagSet]/d:character/d:tag[@name=$styleName]"/>
	<xsl:variable name="tag" select="$cTags[1] | ($sTags[count($cTags)=0])[1]"/>
	
	<xsl:choose>
		<!-- found a matching action -->
		<xsl:when test="count($tag)&gt;0">
			<xsl:choose>
				<xsl:when test="$tag/d:map">
					<xsl:element name="{$tag/d:map/@value}"><xsl:apply-templates/></xsl:element>
				</xsl:when>
				<xsl:when test="$tag/d:pagenum">
					<pagenum id="p-{translate(.,' ','')}">
						<xsl:attribute name="page">
							<xsl:choose>
								<xsl:when test="string(.)=string(number(.))">normal</xsl:when>
								<xsl:when test="string-length(translate(., 'ivxlcdmIVXLCDM', ''))=0">front</xsl:when>
								<xsl:otherwise>special</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:apply-templates/>
					</pagenum>
				</xsl:when>
				<xsl:when test="$tag/d:noteref">
					<xsl:choose>
						<xsl:when test="count(w:footnote)&gt;0"><xsl:apply-templates/></xsl:when>
						<xsl:otherwise><noteref idref=""><xsl:apply-templates/></noteref></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:when>
		<!-- no matching action found for this paragrap style -->
		<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- Field information. No output. -->
<xsl:template match="w:instrText"/>

<xsl:template name="addZeros">
	<xsl:param name="value"/>
	<xsl:choose>
	<xsl:when test="string-length($value)&lt;3">
		<xsl:call-template name="addZeros">
			<xsl:with-param name="value" select="concat(0, $value)"/>
		</xsl:call-template>
	</xsl:when>
	<xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="w:pict">
  <xsl:if test="v:shape/v:imagedata">
	  <xsl:variable name="img-no"><xsl:call-template name="addZeros">
			  <xsl:with-param name="value" select="count(preceding::v:shape/v:imagedata)+1"/>
		  </xsl:call-template></xsl:variable>
		<imggroup>
			<img src="image{$img-no}.jpg" alt="{v:shape/@alt}"/>
		</imggroup>
  </xsl:if>
</xsl:template>

<xsl:template match="w:t"><xsl:value-of select="."/></xsl:template>
<xsl:template match="w:tab"><xsl:text>&#x0009;</xsl:text></xsl:template>
<xsl:template match="w:br"><br/></xsl:template>

<xsl:template match="w:tbl">
	<table>
		<xsl:apply-templates/>
	</table>
</xsl:template>

<xsl:template match="w:tr">
	<tr>
		<xsl:apply-templates/>
	</tr>
</xsl:template>

<xsl:template match="w:tc">
	<td>
		<xsl:if test="w:tcPr/w:gridSpan">
			<xsl:attribute name="colspan"><xsl:value-of select="w:tcPr/w:gridSpan/@w:val"/></xsl:attribute>
		</xsl:if>
		<xsl:apply-templates/>
	</td>
</xsl:template>

<!-- Handle rowspan -->
<xsl:template match="w:tc[w:tcPr/w:vmerge/@w:val='restart']" priority="10">
	<td>
		<xsl:if test="w:tcPr/w:gridSpan">
			<xsl:attribute name="colspan"><xsl:value-of select="w:tcPr/w:gridSpan/@w:val"/></xsl:attribute>
		</xsl:if>
		<xsl:variable name="val"><xsl:apply-templates select="." mode="getGridPos"/></xsl:variable>
		<xsl:attribute name="rowspan">
			<xsl:apply-templates select="ancestor::w:tr" mode="countRowspan">
				<xsl:with-param name="gridPos" select="number($val)"/>
			</xsl:apply-templates>
		</xsl:attribute>
		<xsl:apply-templates/>
	</td>
</xsl:template>

<xsl:template match="w:tc[w:tcPr/w:vmerge]" priority="5"/>

<xsl:template match="w:tc" mode="getGridPos"><xsl:value-of select="1+count(preceding-sibling::w:tc)+sum(preceding-sibling::w:tc/w:tcPr/w:gridSpan[@w:val&gt;1]/@w:val) - count(preceding-sibling::w:tc/w:tcPr/w:gridSpan[@w:val&gt;1])"/></xsl:template>

<xsl:template match="w:tr" mode="findSiblingPos">
	<xsl:param name="gridPos"/> <!-- position in grid -->
	<xsl:param name="siblingPos" select="number($gridPos)"/> <!-- sibling position -->
	<xsl:variable name="cp">
		<xsl:apply-templates select="w:tc[$siblingPos]" mode="getGridPos"/>
	</xsl:variable>
	<xsl:variable name="calcPos" select="number($cp)"/>
	<xsl:choose>
		<xsl:when test="$calcPos=$gridPos"><xsl:value-of select="$siblingPos"/></xsl:when>
		<xsl:when test="$calcPos&lt;$gridPos or $siblingPos=1">0</xsl:when>
		<xsl:otherwise><xsl:apply-templates select="." mode="findSiblingPos">
				<xsl:with-param name="gridPos" select="$gridPos"/>
				<xsl:with-param name="siblingPos" select="$siblingPos - 1"/>
			</xsl:apply-templates></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="w:tr" mode="countRowspan">
	<xsl:param name="gridPos"/>
	<xsl:variable name="tmp"><xsl:apply-templates select="." mode="findSiblingPos">
			<xsl:with-param name="gridPos" select="$gridPos"/>
		</xsl:apply-templates></xsl:variable>
	<xsl:variable name="siblingPos" select="number($tmp)"/>
	<xsl:choose>
		<xsl:when test="w:tc[$siblingPos]/w:tcPr/w:vmerge">
			<xsl:variable name="val"><xsl:choose>
				<xsl:when test="following-sibling::w:tr">
				<xsl:apply-templates select="(following-sibling::w:tr)[1]" mode="countRowspan">
					<xsl:with-param name="gridPos" select="$gridPos"/>
				</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose></xsl:variable><xsl:value-of select="1+number($val)"/></xsl:when>
		<xsl:otherwise>0</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!-- / Handle rowspan -->

<xsl:template match="w:footnote">
	<noteref idref="note-{count(preceding::w:footnote[ancestor::w:body])+1}"><xsl:value-of select="count(preceding::w:footnote[ancestor::w:body])+1"/></noteref>
</xsl:template>

<xsl:template match="w:footnote" mode="rearmatter">
	<note id="note-{count(preceding::w:footnote[ancestor::w:body])+1}">
		<xsl:for-each select="w:p">
			<p><xsl:value-of select="."/></p>
		</xsl:for-each>
	</note>
</xsl:template>

<xsl:template match="*">
	<xsl:apply-templates/>
</xsl:template>

<!--
<xsl:template name="copy">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>-->

</xsl:stylesheet>