<?xml version="1.0"?>

<!-- xml2dtbook.xsl

Part of the rtf2dtbook transformer, in the DAISY MultiFormat Converter. Apply to output from rtf2xml.py

Supported styles:

Paragraph styles:
- "Heading 1" ... "Heading 6"
- "Normal"
- Lists:
	- "List Bullet" => <list type="ul">
	- "List Number" => <list type="ol"> (with guesses made at enum types and start values > 1)
	- Nesting supported for "List Bullet x", "List Number x"

Structures:
- Tables:
	- use "Header" style for header cells, "Normal" style for data cells
- Footnotes/Endnotes
	- use either Word's built-in footnote features, or use the styles "Footnote Reference" and "Footnote Text"
- Images:
	- an <imggroup> is created for any paragraph with a "Caption" style, beside an image.

Character styles:
- "Page Number" (with guesses made at page types front/normal/special)

Formatting:
- bold => <strong>
- italic/underline => <em>
- superscript, subscript

B Nelson, CNIB Library 

-->

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rtf="http://rtf2xml.sourceforge.net/"
		xmlns:dtbook="http://www.daisy.org/z3986/2005/dtbook/"
		exclude-result-prefixes="rtf dtbook">

	<xsl:output method="xml" indent="yes" doctype-public="-//NISO//DTD dtbook 2005-2//EN"
			doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd" />

	<xsl:template match="rtf:doc">
		<xsl:processing-instruction name="xml-stylesheet">
			<xsl:text>href="dtbook.2005-2.basic.css" type="text/css"</xsl:text>
		</xsl:processing-instruction>
		<dtbook version="2005-2">
			<xsl:apply-templates/>
		</dtbook>
	</xsl:template>

	<xsl:template match="rtf:preamble">
		<head>
			<meta name="dc:Title">
				<xsl:attribute name="content">
					<xsl:call-template name="getTitle" />
				</xsl:attribute>
			</meta>
			<xsl:apply-templates select="rtf:doc-information/(rtf:author | rtf:subject)"/>
		</head>
	</xsl:template>

	<xsl:template name="getTitle">
		<xsl:choose>
			<xsl:when test="/rtf:doc/rtf:preamble/rtf:doc-information/rtf:title">
				<xsl:value-of select="/rtf:doc/rtf:preamble/rtf:doc-information/rtf:title" />
			</xsl:when>
			<xsl:when test="/rtf:doc/rtf:body/rtf:paragraph-definition[@name='heading 1'][1]/rtf:para[1]">
				<xsl:value-of select="/rtf:doc/rtf:body/rtf:paragraph-definition[@name='heading 1'][1]/rtf:para[1]" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="/rtf:doc/rtf:body/descendant::rtf:para[1]" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="rtf:author">
		<meta name="dc:Creator" content="{.}" />
	</xsl:template>

	<xsl:template match="rtf:subject">
		<meta name="dc:Subject" content="{.}" />
	</xsl:template>

	<xsl:template match="rtf:rtf-definition | rtf:font-table | rtf:color-table | rtf:style-table | rtf:page-definition | rtf:list-table | rtf:override-table | rtf:override-list | rtf:list-text | rtf:page-break" />

	<xsl:template match="rtf:body">
		<book>
			<bodymatter>
				<xsl:choose>
					<xsl:when test="rtf:section/rtf:section">
						<xsl:apply-templates select="rtf:section/*" />
					</xsl:when>
					<xsl:otherwise>
						<level>
							<xsl:apply-templates select="rtf:section/*" />
						</level>
					</xsl:otherwise>
				</xsl:choose>
			</bodymatter>
		</book>
	</xsl:template>

	<xsl:template match="rtf:section">
		<xsl:element name="level{@level}">
			<xsl:apply-templates select="*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="rtf:paragraph-definition[starts-with(@name, 'heading')]">
		<xsl:element name="h{../@level}">
			<xsl:apply-templates select="rtf:para/node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="rtf:paragraph-definition">
		<xsl:apply-templates select="*"/>
	</xsl:template>


	<xsl:template match="rtf:para">
		<!-- handle the various cases (order below is important) where a p tag should be applied, when it should not. All lumped here to move footnotes outside paras. -->
		<xsl:choose>
			<!-- if there's a picture beside a caption, wrap them in an imggroup -->
			<xsl:when test="parent::rtf:paragraph-definition[@name='caption'
				and	(preceding-sibling::rtf:*[1]/rtf:para/rtf:pict
				 or following-sibling::rtf:*[1]/rtf:para/rtf:pict) ]">
				<imggroup>
					<caption><xsl:apply-templates /></caption>
					<img src="" alt="" />
				</imggroup>
			</xsl:when>
		
			<!-- no p: for any paragraph with no text nodes, e.g. an image, or only a pagenum -->
			<xsl:when test="count( node() ) = 1 and count( text() ) = 0 and (rtf:pict or rtf:inline[@character-style='page number'])">
				<xsl:apply-templates />
			</xsl:when>
			
			<!-- p: if there's a sibling para with text -->
			<xsl:when test="preceding-sibling::rtf:para[string() != ''] or following-sibling::rtf:para[string() != '']">
				<p><xsl:apply-templates /></p>
			</xsl:when>
			
			<!-- no p: if it's a list item, and skip over the text node before the list-item child -->
			<xsl:when test="ancestor::rtf:item">
				<xsl:apply-templates select="rtf:list-text/following-sibling::node()" />
			</xsl:when>
			
			<!-- no p: if it's a table cell -->
			<xsl:when test="ancestor::rtf:cell">
				<xsl:apply-templates />
			</xsl:when>
			
			<!-- note tags for "footnote text" type -->
			<xsl:when test="parent::rtf:paragraph-definition[@name='footnote text']">
				<note>
					<xsl:attribute name="id">
						<xsl:text>note</xsl:text>
						<xsl:choose>
							<xsl:when test="matches(., '^[1-9][0-9]*')">
								<xsl:value-of select="replace(., '(^[1-9][0-9]*).*', '$1')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="generate-id" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<p>
						<xsl:apply-templates />
					</p>
				</note>
			</xsl:when>
			
			<xsl:otherwise>
				<p><xsl:apply-templates select="node()" /></p>
			</xsl:otherwise>
		</xsl:choose>
		
		<!-- if there were footnotes, put them after the p -->
		<xsl:if test="descendant::rtf:footnote">
			<xsl:apply-templates select="descendant::rtf:footnote" mode="outsidep" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="rtf:inline[@character-style='page number']" priority="10">
		<pagenum id="p{.}">
			<!--<xsl:attribute name="id">p<xsl:value-of select="." />
			</xsl:attribute>-->
			<xsl:attribute name="page">
				<xsl:choose>
					<xsl:when test="number(.) &gt; 0">normal</xsl:when>
					<xsl:when test="matches(., '[ivxl]+')">front</xsl:when>
					<xsl:otherwise>special</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute><xsl:value-of select="." />
		</pagenum>
	</xsl:template>


	<!-- inline character attributes. (italics, underline, bold, super, sub) -->
	<xsl:template match="rtf:inline[@bold]" priority="5"><strong><xsl:next-match /></strong></xsl:template>

	<xsl:template match="rtf:inline[@italics | @underlined]" priority="4"><em><xsl:next-match /></em></xsl:template>

	<xsl:template match="rtf:inline[@superscript]" priority="2"><sup><xsl:next-match /></sup></xsl:template>

	<xsl:template match="rtf:inline[@subscript]" priority="1"><sub><xsl:next-match /></sub></xsl:template>


	<!-- lists: drop lists after the first in a consecutive group with matching list-ids -->
	<xsl:template match="rtf:list[@list-id=preceding-sibling::*[1][self::rtf:list]/@list-id]" />

	<!-- lists: handle all enums, and guess at start values for enum="a|A" -->
	<xsl:template match="rtf:list">
		<xsl:if test="descendant::rtf:para[text() or node()]"> <!-- only use non-empty lists -->
		<xsl:text> </xsl:text>
			<list>
				<xsl:choose>
					<xsl:when test="@list-type='unordered'">
						<xsl:attribute name="type">ul</xsl:attribute>
					</xsl:when>
					<xsl:when test="@list-type='ordered'">
						<xsl:attribute name="type">ol</xsl:attribute>
						<!-- for simplicity: set the context to the first character of the list-text element --> 
						<xsl:for-each select="substring(descendant::rtf:list-text[1]/rtf:inline, 1, 1)">
							<xsl:choose>
								<xsl:when test="matches(., '^[1-9]')">
									<xsl:attribute name="enum">1</xsl:attribute>
									<xsl:if test="matches(., '^[2-9]')"><xsl:attribute name="start"><xsl:value-of select="." /></xsl:attribute></xsl:if>
								</xsl:when>
								<xsl:when test="matches(., '^[a-h]')">
									<xsl:attribute name="enum">a</xsl:attribute>
									<xsl:if test="matches(., '^[b-h]')"><xsl:attribute name="start"><xsl:value-of select="translate(., 'bcdefgh', '2345678')" /></xsl:attribute></xsl:if>
								</xsl:when>
								<xsl:when test="matches(., '^[ivx]')">
									<xsl:attribute name="enum">i</xsl:attribute>
								</xsl:when>							
								<xsl:when test="matches(., '^[A-H]')">
									<xsl:attribute name="enum">A</xsl:attribute>
									<xsl:if test="matches(., '^[B-H]')"><xsl:attribute name="start"><xsl:value-of select="translate(., 'BCDEFGH', '2345678')" /></xsl:attribute></xsl:if>
								</xsl:when>							
								<xsl:when test="matches(., '^[IVX]')">
									<xsl:attribute name="enum">I</xsl:attribute>
								</xsl:when>							
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="*" />
				<xsl:apply-templates select="following-sibling::*[self::rtf:list][@list-id=current()/@list-id]/*" />
			</list>
		</xsl:if>
	</xsl:template>

	<xsl:template match="rtf:item[ancestor::rtf:list]">
		<li><xsl:apply-templates select="*" /></li>
	</xsl:template>

	<!-- tables -->

	<xsl:template match="rtf:table">
		<table>
			<xsl:apply-templates select="*" />
		</table>
	</xsl:template>

	<xsl:template match="rtf:row">
		<tr>
			<xsl:apply-templates select="*" />
		</tr>
	</xsl:template>

	<xsl:template match="rtf:cell[rtf:paragraph-definition[@name='header']]">
		<th><xsl:apply-templates select="rtf:paragraph-definition/rtf:para" /></th>
	</xsl:template>

	<xsl:template match="rtf:cell">
		<td><xsl:apply-templates select="rtf:paragraph-definition/rtf:para" /></td>
	</xsl:template>


	<!-- notes. Watch for duplicated IDs of notes -->

	<xsl:template match="rtf:inline[@character-style='footnote reference']" priority="10">
		<noteref idref="note{.}"><xsl:apply-templates /></noteref>
	</xsl:template>

	<xsl:template match="rtf:footnote" />

	<xsl:template match="rtf:footnote" mode="outsidep">
		<note id="note{@num}"><p><xsl:value-of select="@num" />. <xsl:apply-templates select="rtf:paragraph-definition/rtf:para/node()[not(self::rtf:inline[@character-style='footnote reference'])]" /></p></note>
	</xsl:template>


	<!-- undefined symbols - dirty, dirty hack -->

	<xsl:template match="rtf:udef_symbol">
		<xsl:text disable-output-escaping="yes">&amp;#x</xsl:text><xsl:value-of select="substring-after(@num, '&quot;')" /><xsl:text>;</xsl:text>
	</xsl:template>

</xsl:stylesheet>

