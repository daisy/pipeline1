<?xml version="1.0" encoding="UTF-8"?>
<!--
		Pagenum fix
		Removes
			* empty (or nested) p
			* otherwise empty p or li around pagenum (except p in td)
			* empty em, strong, sub, sup
		Moves
			* pagenum inside h[x] before h[x]
			* pagenum inside a word after the word
		Adds an empty p-tag back(?) if: 
				- levelx is empty or contains nothing but empty p-tags
				- hx is the last element or followed by nothing but empty p-tags

		Joel Håkansson, TPB
		Version 2007-10-15

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>

 <!-- pagenum-fix -->
	<xsl:template match="dtb:p[
						count(text()[normalize-space()!='']|comment()|processing-instruction())=0 and
						( 
							(	count(descendant::dtb:pagenum)=count(descendant::*) 
								and not(parent::dtb:td)
							) or (	
								count(descendant::dtb:p)=count(descendant::*)
							)
						)
						]">
			<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="dtb:level1|dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6|dtb:level">
		<xsl:choose>
			<xsl:when test="count(*)=count(dtb:p[count(descendant::node())=0])">
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates/>
					<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise><xsl:call-template name="copy"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="dtb:li[count(text())=0 and 
						((count(descendant::dtb:pagenum)=count(descendant::*))) and count(descendant::*)&gt;0]">
		<xsl:apply-templates/>
	</xsl:template>
 <!-- /pagenum-fix -->
	
 <!-- move pagenum -->
	<xsl:template match="dtb:h1|dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6|dtb:hd">
		<!-- Move all pagenums inside hx before hx -->
		<xsl:for-each select="descendant::dtb:pagenum">
			<xsl:copy-of select="."/>
		</xsl:for-each>
		<xsl:call-template name="copy"/>
		<!-- belongs to pagenum-fix -->
		<xsl:if test="count(following-sibling::*)=count(following-sibling::dtb:p[count(descendant::node())=0])">
			<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/"/>
		</xsl:if>
		<!-- / belongs to pagenum-fix -->
	</xsl:template>
	
	<!-- Ignore pagenums inside hx, they are processed above -->
	<xsl:template match="dtb:pagenum[ancestor::dtb:h1|ancestor::dtb:h2|ancestor::dtb:h3|
	ancestor::dtb:h4|ancestor::dtb:h5|ancestor::dtb:h6|ancestor::dtb:hd]"/>

	<!-- Ignore these text nodes, they are processed below -->
	<xsl:template match="text()[preceding-sibling::node()[1][self::dtb:pagenum] and
							preceding-sibling::node()[2][self::text()] and
not(ancestor::dtb:h1|ancestor::dtb:h2|ancestor::dtb:h3|ancestor::dtb:h4|ancestor::dtb:h5|ancestor::dtb:h6|ancestor::dtb:hd)]"/>
							
	<!-- Process pagenum with text nodes on both sides -->
	<xsl:template match="dtb:pagenum[preceding-sibling::node()[1][self::text()] and 
							 following-sibling::node()[1][self::text()] and
not(ancestor::dtb:h1|ancestor::dtb:h2|ancestor::dtb:h3|ancestor::dtb:h4|ancestor::dtb:h5|ancestor::dtb:h6|ancestor::dtb:hd)]">
		<xsl:variable name="A1" select="following-sibling::node()[1]"/>
		<xsl:variable name="A2" select="preceding-sibling::node()[1]"/>
		<xsl:choose>
			<!-- 
          ends-with: substring($A, string-length($A) - string-length($B) + 1) = $B
                     Se XSLT programmers reference, second edition, Michael Kay, sidan 541
      -->
			<!-- 
         Om föregående textnod slutar med mellanslag eller om nästkommande textnod börjar med mellanslag
         så ska denna tagg inte flyttas.
      -->
			<xsl:when test="starts-with($A1, ' ') or substring($A2, string-length($A2))=' '">
				<xsl:call-template name="copy"/>
				<xsl:value-of select="$A1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains($A1,' ')">
						<xsl:value-of select="substring-before($A1,' ')"/>
						<xsl:call-template name="copy"/>
						<xsl:value-of select="concat(' ',substring-after($A1,' '))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$A1"/>
						<xsl:call-template name="copy"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
 <!-- /move pagenum -->
 
 <xsl:template match="dtb:strong[text() and count(node())=1 and normalize-space()='']">
	 <xsl:apply-templates/>
 </xsl:template>
 
 <xsl:template match="dtb:em[text() and count(node())=1 and normalize-space()='']">
	 <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="dtb:sub[text() and count(node())=1 and normalize-space()='']">
	 <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="dtb:sup[text() and count(node())=1 and normalize-space()='']">
	 <xsl:apply-templates/>
 </xsl:template>
  
</xsl:stylesheet>
