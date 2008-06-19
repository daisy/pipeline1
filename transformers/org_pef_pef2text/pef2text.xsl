<?xml version="1.1" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:bpf="http://www.tpb.se/ns/2006/bpf" exclude-result-prefixes="dc pef bpf">
	<xsl:output method="text" encoding="utf-8" indent="no" omit-xml-declaration="yes"/>

	<xsl:param name="locale" select="'en-US'"/>
	<xsl:param name="min"/>
	<xsl:param name="max"/>

	<xsl:template name="translateRow">
		<xsl:choose>
			<!-- North American ASCII Braille Table -->
			<xsl:when test="$locale='en-US'">
				<xsl:value-of select="translate(., '&#x2800;&#x2801;&#x2802;&#x2803;&#x2804;&#x2805;&#x2806;&#x2807;&#x2808;&#x2809;&#x280A;&#x280B;&#x280C;&#x280D;&#x280E;&#x280F;&#x2810;&#x2811;&#x2812;&#x2813;&#x2814;&#x2815;&#x2816;&#x2817;&#x2818;&#x2819;&#x281A;&#x281B;&#x281C;&#x281D;&#x281E;&#x281F;&#x2820;&#x2821;&#x2822;&#x2823;&#x2824;&#x2825;&#x2826;&#x2827;&#x2828;&#x2829;&#x282A;&#x282B;&#x282C;&#x282D;&#x282E;&#x282F;&#x2830;&#x2831;&#x2832;&#x2833;&#x2834;&#x2835;&#x2836;&#x2837;&#x2838;&#x2839;&#x283A;&#x283B;&#x283C;&#x283D;&#x283E;&#x283F;', ' a1b''k2l@cif/msp&quot;e3h9o6r^djg>ntq,*5&lt;-u8v.%[$+x!&amp;;:4\0z7(_?w]#y)=')"/>
			</xsl:when>
			<!-- Swedish -->
			<xsl:when test="$locale='sv-SE-CX'">
				<xsl:value-of select="translate(., '&#x2800;&#x2801;&#x2802;&#x2803;&#x2804;&#x2805;&#x2806;&#x2807;&#x2808;&#x2809;&#x280A;&#x280B;&#x280C;&#x280D;&#x280E;&#x280F;&#x2810;&#x2811;&#x2812;&#x2813;&#x2814;&#x2815;&#x2816;&#x2817;&#x2818;&#x2819;&#x281A;&#x281B;&#x281C;&#x281D;&#x281E;&#x281F;&#x2820;&#x2821;&#x2822;&#x2823;&#x2824;&#x2825;&#x2826;&#x2827;&#x2828;&#x2829;&#x282A;&#x282B;&#x282C;&#x282D;&#x282E;&#x282F;&#x2830;&#x2831;&#x2832;&#x2833;&#x2834;&#x2835;&#x2836;&#x2837;&#x2838;&#x2839;&#x283A;&#x283B;&#x283C;&#x283D;&#x283E;&#x283F;', ' a,b.k;l^cif/msp''e:h*o!r~djgäntq_å?ê-u(v@îöë§xèç&quot;û+ü)z=à|ôwï#yùé')"/>
			</xsl:when>
			<!-- Add other locales here -->
			<xsl:otherwise>
				<xsl:message terminate="yes"><xsl:value-of select="concat('Unknown locale: ', $locale)"/></xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pef:pef">
		<xsl:if test="//pef:row[matches(text(), '[&#x2840;-&#x28FF;]')]">
			<xsl:message terminate="yes">Error: Found <xsl:value-of select="count(//pef:row[matches(text(), '[&#x2840;-&#x28FF;]')])"/> row(s) containing 8-dot braille. This implementation does not support 8-dot braille.</xsl:message>
		</xsl:if>
		<xsl:variable name="pageMin" select="if (number($min)>0) then number($min) else 1"/>
		<xsl:variable name="pageMax" select="if (number($max)>0) then number($max) else count(descendant::pef:page)"/>
		<xsl:apply-templates select="(descendant::pef:page)[position()>=$pageMin and position()&lt;=$pageMax]"/>
	</xsl:template>

	<xsl:template match="pef:page">
		<xsl:apply-templates select="*"/>
		<!--<xsl:if test="following-sibling::pef:page">-->
			<xsl:text>&#x000C;</xsl:text>
		<!--</xsl:if>-->
	</xsl:template>

	<xsl:template match="pef:row">
		<xsl:call-template name="translateRow"/>
		<xsl:if test="following-sibling::pef:row">
			<xsl:text>&#x000A;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Support for the PEF precursor BPF -->
	<xsl:template match="bpf:bpf">
		<xsl:if test="//bpf:row[matches(text(), '[&#x2840;-&#x28FF;]')]">
			<xsl:message terminate="yes">Error: Found <xsl:value-of select="count(//bpf:row[matches(text(), '[&#x2840;-&#x28FF;]')])"/> row(s) containing 8-dot braille. This implementation does not support 8-dot braille.</xsl:message>
		</xsl:if>
		<xsl:variable name="pageMin" select="if (number($min)>0) then number($min) else 1"/>
		<xsl:variable name="pageMax" select="if (number($max)>0) then number($max) else count(descendant::bpf:page)"/>
		<xsl:apply-templates select="(descendant::bpf:page)[position()>=$pageMin and position()&lt;=$pageMax]"/>
	</xsl:template>
	
	<xsl:template match="bpf:page">
		<xsl:apply-templates select="*"/>
		<!--<xsl:if test="following-sibling::bpf:page">-->
			<xsl:text>&#x000C;</xsl:text>
		<!--</xsl:if>-->
	</xsl:template>
	
	<xsl:template match="bpf:row">
		<xsl:call-template name="translateRow"/>
		<xsl:if test="following-sibling::bpf:row">
			<xsl:text>&#x000A;</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
