<?xml version="1.0" encoding="utf-8"?>
<!--
	TODO:
		- komplexa sub, sup
		- länkar, e-postadresser
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:import href="dtbook2flow_sv_SE.xsl" />
	<xsl:output method="xml" encoding="utf-8" indent="no"/>

	<xsl:template name="insertLayoutMaster">
		<layout-master name="front" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<header>
					<field><current-page style="roman"/></field>
				</header>
				<footer></footer>
			</template>
			<default-template>
				<header>
					<field><string value=""/></field>
					<field><current-page style="roman"/></field>
				</header>
				<footer></footer>
			</default-template>
		</layout-master>
		<layout-master name="main" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<template use-when="(= (% $page 2) 0)">
				<header>
					<field><current-page style="default"/></field>
					<field>
						<marker-reference marker="pagenum-turn" direction="forward" scope="page_content"/>
						<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
					</field>
				</header>
				<footer></footer>
			</template>
			<default-template>
				<header>
					<field>
						<marker-reference marker="pagenum-turn" direction="forward" scope="page_content"/>
						<marker-reference marker="pagenum" direction="backward" scope="sequence"/>
					</field>
					<field><current-page style="default"/></field>
				</header>
				<footer></footer>
			</default-template>
		</layout-master>
		<layout-master name="plain" page-width="{$page-width}" 
							page-height="{$page-height}" inner-margin="{$inner-margin}"
							outer-margin="{$outer-margin}" row-spacing="{$row-spacing}" duplex="{$duplex}">
			<default-template>
				<header><field><string value=""/></field></header>
				<footer></footer>
			</default-template>
		</layout-master>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:em[not(ancestor::dtb:list[@class='toc'])]" mode="inline-mode">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2820;&#x2824;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:strong[not(ancestor::dtb:list[@class='toc'])]" mode="inline-mode">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2828;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2828;&#x2828;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sup" mode="inline-mode">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x282c;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sub" mode="inline-mode">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x2823;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Redigering och avskrivning, page 148 -->
	<xsl:template match="dtb:dd" mode="block-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-attributes"/>
			<xsl:text>&#x2820;&#x2804; </xsl:text><xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template name="addMarkersAlfaNum">
		<xsl:param name="prefix" select="''"/>
		<xsl:param name="postfix" select="''"/>
		<xsl:choose>
			<!-- text contains a single alfa/numerical string -->
			<xsl:when test="count(node())=1 and text() and matches(text(),'^[a-zA-Z0-9]*$')">
				<xsl:value-of select="$prefix"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix"/>
			</xsl:when>
			<!-- Otherwise -->
			<xsl:otherwise>
				<xsl:message terminate="no">Error: sub/sub contains a complex expression for which there is no specified formatting.</xsl:message>
				<xsl:apply-templates/>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

	<xsl:template name="addMarkers">
		<xsl:param name="prefix-single-word" select="''"/>
		<xsl:param name="postfix-single-word" select="''"/>
		<xsl:param name="prefix-multi-word" select="''"/>
		<xsl:param name="postfix-multi-word" select="''"/>
		<xsl:choose>
			<!-- if text contains one word only -->
			<xsl:when test="count(text())=1 and translate(text(), ' ', '')=text()">
				<xsl:value-of select="$prefix-single-word"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix-single-word"/>
			</xsl:when>
			<!-- text contains several words -->
			<xsl:otherwise>
				<xsl:value-of select="$prefix-multi-word"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix-multi-word"/>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
