<?xml version="1.0" encoding="utf-8"?>
<!-- 
Sequence:
Use "sequence-mode" to override default sequence element and "apply-sequence-attributes" to override default sequence attributes

Block:
Use "block-mode" to override default block element and "apply-block-attributes" to override default element attributes

Inline:
Use "inline-mode" to override default inline processing
-->
<!--
sequence elements: 
bodymatter, frontmatter, rearmatter

block elements:
	no text:
		level, level1, level2, level3, level4, level5, level6
	can be surrounded by text, does not contain text:
		list, blockquote, linegroup, poem, div, annotation, dl, imggroup
	can contain text, but cannot be surrounded by text:
		bridgehead, caption, covertitle, docauthor, doctitle, li, h1, h2, h3, h4, h5, h6
	can be surrounded by text and contain text:
		author, address, hd, line, p, sidebar, byline, dateline, epigraph, prodnote

inline/block:
a, cite, kbd, samp

inline elements:
code, bdo, em, strong, span, sub, sup, abbr, acronym, 
dfn, q, noteref, annoref, sent, w, linenum, lic, dd, dt

special:
br, pagenum

no-op:
book, dtbook, head, meta, link, img

unhandled:
note, table, col, colgroup, tbody, td, tfoot, th, thead, title, tr
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:output method="xml" encoding="utf-8" indent="no"/>

	<xsl:template match="/"><root><xsl:apply-templates/></root></xsl:template>
	<xsl:template match="dtb:dtbook | dtb:book"><xsl:apply-templates/></xsl:template>
	<xsl:template match="dtb:head | dtb:meta | dtb:link"></xsl:template>
	
<!-- sequence elements / -->
	<xsl:template match="dtb:frontmatter | dtb:bodymatter | dtb:rearmatter">
		<xsl:apply-templates select="." mode="sequence-mode"/>
	</xsl:template>
<!-- / sequence elements -->

<!-- block elements / -->
	<!-- No text elements -->
	<xsl:template match="dtb:level1 | dtb:level2 | dtb:level3 | dtb:level4 | dtb:level5 | dtb:level6 | dtb:level">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can be surrounded by text, but does not contain text -->
	<xsl:template match="dtb:list | dtb:blockquote | dtb:linegroup | dtb:poem |
								dtb:div | dtb:annotation | dtb:dl | dtb:imggroup">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can contain text, but isn't surrounded by text -->
	<xsl:template match="dtb:caption | dtb:h1 | dtb:h2 | dtb:h3 | dtb:h4 | dtb:h5 | dtb:h6 | dtb:li |
								dtb:bridgehead | dtb:covertitle | dtb:docauthor | dtb:doctitle">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
	<!-- Can be surrounded by text and contain text -->
	<xsl:template match="dtb:address | dtb:prodnote | dtb:hd | dtb:p | dtb:author | dtb:line | 
								dtb:epigraph | dtb:sidebar | dtb:byline | dtb:dateline">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
<!-- / block elements -->

<!-- inlines that may alternatively be in block elements / -->
	<xsl:template match="dtb:a | dtb:cite | dtb:samp | dtb:kbd">
		<xsl:choose>
			<xsl:when test="parent::dtb:level1 or parent::dtb:level2 or parent::dtb:level3 or parent::dtb:level4 or parent::dtb:level5 or parent::dtb:level6 or parent::dtb:level or parent::dtb:div or parent::dtb:annotation">
			<xsl:message><xsl:apply-templates select="." mode="block-mode"/></xsl:message>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates select="." mode="inline-mode"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
<!-- / inlines that may alternatively be in block elements -->

<!-- inline elements / -->
	<xsl:template match="dtb:bdo | dtb:code | dtb:em | dtb:strong | dtb:sup | dtb:sub | dtb:w |  
								dtb:sent | dtb:span | dtb:acronym | dtb:abbr | dtb:q | dtb:dfn |
								dtb:annoref | dtb:noteref | dtb:linenum | dtb:lic | dtb:dt | dtb:dd">
		<xsl:apply-templates select="." mode="inline-mode"/>
	</xsl:template>
<!-- / inline elements -->

<!-- special / -->
	<xsl:template match="dtb:pagenum">
		<marker class="pagenum" value="{text()}"/>
		<xsl:variable name="preceding-pagenum" select="preceding::dtb:pagenum[1]"/>
		<xsl:variable name="preceding-marker">
			<xsl:if test="not($preceding-pagenum) or generate-id($preceding-pagenum/ancestor::dtb:level1/parent::*)=
						generate-id(ancestor::dtb:level1/parent::*)">
				<xsl:value-of select="$preceding-pagenum"/><xsl:text>-</xsl:text>
			</xsl:if>
		</xsl:variable>
		<marker class="pagenum-turn" value="{$preceding-marker}"/>
	</xsl:template>

	<xsl:template match="dtb:br"><br/></xsl:template>

	<xsl:template match="dtb:img"></xsl:template>
<!-- / special -->

<!-- disallowed elements / -->
	<xsl:template match="dtb:col | dtb:colgroup | dtb:table | dtb:tbody | dtb:thead | dtb:tfoot | dtb:tr | dtb:th | dtb:td">
		<xsl:message terminate="yes">Tables are not supported.</xsl:message>
	</xsl:template>
	<xsl:template match="dtb:note">
		<xsl:message terminate="yes">Notes are not supported.</xsl:message>
	</xsl:template>
<!-- / disallowed elements -->

<!-- default mode templates / -->
	<xsl:template match="*" mode="sequence-mode">
		<sequence>
			<xsl:apply-templates select="." mode="apply-sequence-attributes"/>
			<xsl:apply-templates/>
		</sequence>
	</xsl:template>

	<xsl:template match="*" mode="block-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-attributes"/>
			<xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template match="*" mode="inline-mode">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="*" mode="apply-sequence-attributes"/>
	<xsl:template match="*" mode="apply-block-attributes"/>
<!-- / default mode templates -->

</xsl:stylesheet>
