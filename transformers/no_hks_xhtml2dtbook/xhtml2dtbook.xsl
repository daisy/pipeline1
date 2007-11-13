<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns="http://www.daisy.org/z3986/2005/dtbook/"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="xs html">
	
<!--	<xsl:output method="xml" 
		encoding="UTF-8"
		indent="yes"
		doctype-public="-//NISO//DTD dtbook 2005-2//EN"
		doctype-system="V:\MFBP\piXi\DTD\dtbook-2005-2.dtd" /> -->

	<xsl:output method="xml" 
		encoding="UTF-8"
		indent="yes"
		doctype-public="-//NISO//DTD dtbook 2005-2//EN"
		doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd" />
	
	<xsl:variable name="smil" as="xs:string" select="'.smil#'" />
	
	<xsl:template match="/">
		<dtbook>
			<xsl:apply-templates />
		</dtbook>
	</xsl:template>
	
	<xsl:template match="html:head">
		<head>
			<xsl:apply-templates select="html:meta" />
			<xsl:apply-templates select="html:link[@rel='stylesheet']" />
		</head>
	</xsl:template>
	
	<xsl:template match="html:meta">
		<meta>
			<xsl:copy-of select="@name, @content, @scheme" />
		</meta>
	</xsl:template>
	
	<xsl:template match="html:link">
		<link rel="stylesheet" type="text/css" href="{concat('dtbook_',@href)}"  />
	</xsl:template>

	<!-- In the following template, the various h1 elements are associated with the proper
	frontmatter, bodymatter og rearmatter element -->
	<xsl:template match="html:body">
		<book>
			<xsl:call-template name="copy-attributes" />
			<xsl:if test="html:h1[@class eq 'frontmatter']">
				<frontmatter>
					<!-- Assuming that the title element in head represents the title -->
					<xsl:apply-templates select="//html:head/html:title" />
					<xsl:apply-templates select="html:h1[@class eq 'frontmatter']" />
				</frontmatter>
			</xsl:if>
			<bodymatter>
				<xsl:apply-templates select="html:h1[not(@class eq 'frontmatter' or @class eq 'rearmatter')]" />
			</bodymatter>
			<xsl:if test="html:h1[@class eq 'rearmatter']">
				<rearmatter>
					<xsl:apply-templates select="html:h1[@class eq 'rearmatter']" />
				</rearmatter>
			</xsl:if>
		</book>
	</xsl:template>
	
	<xsl:template match="html:title">
		<doctitle>
			<xsl:apply-templates />
		</doctitle>
	</xsl:template>
	
	<!-- The following template handles everything concerning levels in the DTBook -->
	<xsl:template match="element()[matches(local-name(),'^h[1-6]$')]">
		<!-- The purpose of this template:
			1) Create the proper levelx element
			2) Create the proper hx element
			3) Process all elements up to the next heading (may be none)
			4) Apply templates for the following h[x+1] elements (may be none) up to the next hx element
		-->
		<!-- Name of current element (heading) -->
		<xsl:variable name="h.this.name" as="xs:string" select="local-name()" />
		<!-- Get the level -->
		<xsl:variable name="h.this.level" as="xs:integer" select="xs:integer(substring-after($h.this.name,'h'))" />
		<!-- Get the next heading (no matter the level), assuming it is a sibling of the current element. 
			This requirement is stated in the documentation -->
		<xsl:variable name="h.next" select="following-sibling::*[matches(local-name(),'^h[1-6]$')][1]" />
		<!-- Get the name of the next heading -->
		<xsl:variable name="h.next.name" select="local-name($h.next)" />
		<!-- NOT USED: Get the level for the next element. 0 if there is no next element 
		<xsl:variable name="h.next.level" as="xs:integer" select="
			if ($h.next)
			then xs:integer(substring-after($h.next.name,'h'))
			else 0" /> -->
		<!-- Get all the following sibling elements (may be none) up to the next heading. 
			If there is no next heading, then get all the following siblings  -->
		<xsl:variable name="e.up-to-next-heading" select="
			if ($h.next)
			then following-sibling::*[. &lt;&lt; $h.next]
			else following-sibling::*" />
		<!-- Get the next heading on the same, or higher, level as the current heading, 
			assuming it is a sibling of the current element. This requirement is stated in the documentation -->
		<xsl:variable name="h.next-on-same-level-or-higher" 
			select="following-sibling::*[matches(local-name(),concat('^h[1-',string($h.this.level),']$'))][1]" />
		<!-- Get all following headings on the next level, ie if the current is a h3 then look for h4 -->
		<xsl:variable name="h.next-level.all" select="following-sibling::*[matches(local-name(),concat('^h',string($h.this.level + 1),'$'))]" />
		<!-- Get only following headings (may be none) on the next level, 
			located before the next heading on the same level or higher -->
		<xsl:variable name="h.next-level.relevant" select="
			if ($h.next-on-same-level-or-higher)
			then $h.next-level.all[. &lt;&lt; $h.next-on-same-level-or-higher]
			else $h.next-level.all" />
		<!-- Create the levelx element -->
		<xsl:element name="{concat('level',string($h.this.level))}">
			<xsl:copy-of select="@class" /> <!-- Transfer the class attribute from the hx element to the levelx element -->
			<!-- Create the hx element -->
			<xsl:element name="{local-name()}">
<!--				<xsl:call-template name="smilref" />-->
				<xsl:call-template name="copy-heading-attributes" />
				<xsl:apply-templates />
			</xsl:element>
<!-- DEBUG:		<div class="h-info">
				<strong>This heading:</strong><br />
				Name: <xsl:value-of select="$h.this.name" /><br />
				Level: <xsl:value-of select="$h.this.level" /><br />
				Content: <xsl:value-of select="." /><br />
				<strong>Next heading:</strong><br />
				Name: <xsl:value-of select="$h.next.name" /><br />
				Level: <xsl:value-of select="$h.next.level" /><br />
				Content: <xsl:value-of select="$h.next" /><br />
				<strong>Next heading on the same, or higher, level:</strong><br />
				Content: <xsl:value-of select="$h.next-on-same-level-or-higher" /><br />
				<strong>Following headings on level <xsl:value-of select="$h.this.level + 1" />:</strong><br />
				Count: <xsl:value-of select="count($h.next-level.all)" /><br />
				Number of relevant: <xsl:value-of select="count($h.next-level.relevant)" /><br />
				<strong>Relevant children:</strong><br />
				Count: <xsl:value-of select="count($e.up-to-next-heading)" />
			</div>-->			
			<!-- Apply templates for all elements up to the next heading -->
			<xsl:apply-templates select="$e.up-to-next-heading" />
			<!-- Apply templates for all headings on the next level -->
			<xsl:apply-templates select="$h.next-level.relevant" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="html:span[starts-with(@class,'page-')]">
		<pagenum page="{substring-after(@class,'page-')}" id="{concat('page-',.)}">
<!--			<xsl:call-template name="smilref" />-->
			<xsl:call-template name="copy-page-attributes" />
			<xsl:value-of select="." />
		</pagenum>
	</xsl:template>
	
	<xsl:template match="html:span[@class eq 'sentence']">
		<xsl:variable name="e.next" select="following-sibling::*[1]" />
		<sent>
			<xsl:call-template name="copy-span-attributes" />
			<xsl:apply-templates />
		</sent>
	</xsl:template>

	<xsl:template match="html:span[ends-with(@class,'-prodnote')]">
		<xsl:variable name="e.p.1" select="preceding-sibling::*[1]" />
		<xsl:variable name="e.p.2" select="preceding-sibling::*[2]" />
		<xsl:variable name="part-of-imggroup" as="xs:boolean" select="
			local-name($e.p.1) eq 'img'
			or
			(	
				local-name($e.p.1) eq 'span' and $e.p.1/@class eq 'caption' and local-name($e.p.2) eq 'img'
			)" />
		<xsl:choose>
			<xsl:when test="$part-of-imggroup" />
			<xsl:otherwise>
				<prodnote render="{substring-before(@class,'-prodnote')}">
					<xsl:call-template name="copy-span-attributes" />
					<xsl:apply-templates />
				</prodnote>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="html:span[ends-with(@class,'-prodnote')]" mode="inside-imgrp">
		<xsl:param name="group-id" as="xs:string" />
		<prodnote render="{substring-before(@class,'-prodnote')}" imgref="{concat('img-',$group-id)}" id="{concat('pnote-',$group-id)}">
			<xsl:call-template name="copy-std-attr" />
<!--			<xsl:comment>TREFF</xsl:comment>-->
			<xsl:apply-templates />
		</prodnote>
	</xsl:template>

	<xsl:template match="html:span[@class eq 'caption']">
		<xsl:variable name="e.p.1" select="preceding-sibling::*[1]" />
		<xsl:variable name="e.p.2" select="preceding-sibling::*[2]" />
		<xsl:variable name="part-of-imggroup" as="xs:boolean" select="
			local-name($e.p.1) eq 'img'
			or
			(	
				local-name($e.p.1) eq 'span' and ends-with($e.p.1/@class,'-prodnote') and local-name($e.p.2) eq 'img'
			)" />
		<xsl:choose>
			<xsl:when test="$part-of-imggroup" /> <!-- The element his handled by the template for img elements -->
			<xsl:otherwise>
				<xsl:next-match />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="html:span[@class eq 'caption']" mode="inside-imgrp">
		<xsl:param name="group-id" as="xs:string" />
		<caption imgref="{concat('img-',$group-id)}" id="{concat('caption-',$group-id)}">
			<xsl:call-template name="copy-std-attr" />
			<xsl:apply-templates />
		</caption>
	</xsl:template>

	<xsl:template match="html:span[@class eq 'noteref']">
		<noteref>
			<xsl:attribute name="idref" select="
				if (contains(@bodyref,'#'))
				then substring-after(@bodyref,'#')
				else @bodyref" />
			<xsl:copy-of select="@id" />
			<xsl:call-template name="copy-std-attr" />
			<xsl:apply-templates />
		</noteref>
	</xsl:template>

	<xsl:template match="html:a[contains(@href,$smil)]">
		<!-- This is the a element used to represent a reference to a SMIL file in a DAISY 2.02 content doc.
			We don't need it in the DTBook, as the @href will end up as a @smilref in the parent element.
			This is handled by the named template 'copy-std-attr'
-->
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="html:div[@class eq 'notebody']">
		<note>
			<xsl:copy-of select="@id" />
			<xsl:call-template name="copy-std-attr" />
			<xsl:apply-templates />
		</note>
	</xsl:template>

	<xsl:template match="html:img">
		<xsl:variable name="e.f.1" select="following-sibling::*[1]" />
		<xsl:variable name="e.f.2" select="following-sibling::*[2]" />
		<xsl:variable name="imggrp.associate.first" as="xs:boolean"
			select="
				local-name($e.f.1) eq 'span' and ends-with($e.f.1/@class,'-prodnote')
				or
				local-name($e.f.1) eq 'span' and $e.f.1/@class eq 'caption'
				" />
		<xsl:variable name="imggrp.associate.second" as="xs:boolean"
			select="
				(	local-name($e.f.1) eq 'span' and ends-with($e.f.1/@class,'-prodnote')
					and
					local-name($e.f.2) eq 'span' and $e.f.2/@class eq 'caption'
				)
				or
				(	local-name($e.f.2) eq 'span' and ends-with($e.f.2/@class,'-prodnote')
					and
					local-name($e.f.1) eq 'span' and $e.f.1/@class eq 'caption'
				)
				" />
		<xsl:choose>
			<xsl:when test="$imggrp.associate.first">
				<xsl:variable name="id" as="xs:string" select="generate-id()" />
				<imggroup id="{concat('imggrp-',$id)}">
					<img>
						<xsl:copy-of select="@src, @alt" />
						<xsl:call-template name="copy-attributes-not-id" />
						<xsl:attribute name="id" select="concat('img-',$id)" />
					</img>
					<xsl:apply-templates select="$e.f.1" mode="inside-imgrp">
						<xsl:with-param name="group-id" as="xs:string" select="$id" />
					</xsl:apply-templates>
					<xsl:if test="$imggrp.associate.second">
						<xsl:apply-templates select="$e.f.2" mode="inside-imgrp">
							<xsl:with-param name="group-id" as="xs:string" select="$id" />
						</xsl:apply-templates>
					</xsl:if>
				</imggroup>
			</xsl:when>
			<xsl:otherwise>
				<img>
					<xsl:copy-of select="@src, @alt" />
					<xsl:call-template name="copy-attributes" />
				</img>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:ol | html:ul">
		<list type="{local-name()}">
			<xsl:call-template name="copy-attributes" />
			<xsl:apply-templates />
		</list>
	</xsl:template>
	
	<xsl:template match="html:a | html:p | html:li | html:dl | html:dt | html:dd | html:span | html:strong | html:em | html:sub | html:sup | html:br | html:div">
		<xsl:element name="{local-name()}">
			<xsl:call-template name="copy-attributes" />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="html:table | html:table/html:caption | html:tr | html:td | html:th | html:col">
		<xsl:element name="{local-name()}">
			<xsl:call-template name="copy-table-attributes" />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template name="copy-attributes">
		<xsl:copy-of select="@id, @class, @href" />
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-attributes-not-id">
		<xsl:copy-of select="@class, @href" />
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-heading-attributes">
		<xsl:copy-of select="@id" />
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-table-attributes">
		<xsl:copy-of select="@id, @rowspan, @colspan, @class, @valign" />
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-span-attributes">
		<xsl:copy-of select="@id" />
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-page-attributes">
		<xsl:call-template name="copy-std-attr" />
	</xsl:template>
	<xsl:template name="copy-std-attr">
<!--	Per Sennels, 20070927: Don't copy @style, as it's not allowed in 2005-2
		<xsl:copy-of select="@title, @style, @dir, @xml:lang" /> -->
		<xsl:copy-of select="@title, @dir, @xml:lang" />
		<xsl:call-template name="smilref" />
	</xsl:template>

	<xsl:template name="smilref">
<!-- 	Per Sennels, 20071026: Handle the html:a/@href attribute, if present -->
		<xsl:if test="contains(html:a/@href,$smil)">
			<xsl:attribute name="smilref" select="html:a/@href" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:if test="local-name() ne 'html'">
			<xsl:text>
</xsl:text>
			<xsl:comment> **** No template for element: <xsl:value-of select="local-name()" /> **** </xsl:comment>
		</xsl:if>
			
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
