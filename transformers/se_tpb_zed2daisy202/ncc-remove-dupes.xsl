<?xml version="1.0" encoding="utf-8"?>

<xsl:transform version="1.0" 
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:exslt="http://exslt.org/common"
               xmlns:ncc="http://www.w3.org/1999/xhtml" 
               xmlns:x="http://www.w3.org/1999/xhtml" 
               xmlns:n="http://www.daisy.org/z3986/2005/ncx/" 
               xmlns:c="http://daisymfc.sf.net/xslt/config"
               xmlns:o="http://openebook.org/namespaces/oeb-package/1.0/"
               xmlns:dc="http://purl.org/dc/elements/1.1/"
               exclude-result-prefixes="x n c exslt o dc">

  <c:config>
  	<c:generator>DMFC z3986-2005 to Daisy 2.02</c:generator>
    <c:name>ncc-remove-dupes</c:name>
    <c:version>0.1</c:version>
    
    <c:author>Linus Ericson</c:author>
    <c:description>Remove duplicates in the Daisy 2.02 ncc.html file.</c:description>    
  </c:config>
  
  <xsl:param name="date"/>
  <xsl:param name="baseDir"/>

  <!-- Don't add doctype yet. Let the ncc-clean.xsl handle that. -->
  <xsl:output method="xml" 
	      encoding="utf-8" 
	      indent="yes"/>

  <xsl:template match="ncc:h1|ncc:h2|ncc:h3|ncc:h4|ncc:h5|ncc:h6">
  	<xsl:variable name="hxName" select="local-name()"/>
  	<xsl:variable name="current">
	  	<xsl:call-template name="get_generated_id_of_heading">
	  		<xsl:with-param name="smilUri" select="ncc:a/@href"/>
	  		<xsl:with-param name="headingName" select="$hxName"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:variable name="prev">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="preceding-sibling::ncc:*[position()=1 and local-name()=$hxName]/ncc:a/@href"/>
	  		<xsl:with-param name="headingName" select="$hxName"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<!--<xsl:message>h1 current: <xsl:value-of select="$current"/></xsl:message>-->
  	<xsl:if test="$current != $prev">
	  	<xsl:copy>
	  		<xsl:apply-templates select="@*|node()"/>
	  	</xsl:copy>
  	</xsl:if>
  </xsl:template>
  
  <xsl:template match="ncc:span[@class='optional-prodnote']">
  	<xsl:variable name="current">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'prodnote'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:variable name="prev">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="preceding-sibling::ncc:*[position()=1 and @class='optional-prodnote']/ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'prodnote'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<!--
  	<xsl:message>prodnote current: <xsl:value-of select="$current"/> prev: <xsl:value-of select="$prev"/></xsl:message>
  	<xsl:message>prevUri: <xsl:value-of select="preceding-sibling::ncc:*[position()=1 and @class='optional-prodnote']/ncc:a/@href"/></xsl:message>
  	-->
  	<xsl:if test="$current != $prev">
	  	<xsl:copy>
	  		<xsl:apply-templates select="@*|node()"/>
	  	</xsl:copy>
  	</xsl:if>
  </xsl:template>
  
  <xsl:template match="ncc:span[@class='sidebar']">
  	<xsl:variable name="current">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'sidebar'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:variable name="prev">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="preceding-sibling::ncc:*[position()=1 and @class='sidebar']/ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'sidebar'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:if test="$current != $prev">
	  	<xsl:copy>
	  		<xsl:apply-templates select="@*|node()"/>
	  	</xsl:copy>
  	</xsl:if>
  </xsl:template>
  
  <xsl:template match="ncc:span[@class='noteref']">
  	<xsl:variable name="current">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'noteref'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:variable name="prev">
	  	<xsl:call-template name="get_generated_id_of_element_with_class">
	  		<xsl:with-param name="smilUri" select="preceding-sibling::ncc:*[position()=1 and @class='noteref']/ncc:a/@href"/>
	  		<xsl:with-param name="className" select="'noteref'"/>
	  	</xsl:call-template>
  	</xsl:variable>
  	<xsl:if test="$current != $prev">
	  	<xsl:copy>
	  		<xsl:apply-templates select="@*|node()"/>
	  	</xsl:copy>
  	</xsl:if>
  </xsl:template>
  
  <xsl:template match="@*|node()">
  	<xsl:copy>
  		<xsl:apply-templates select="@*|node()"/>
  	</xsl:copy>
  </xsl:template>
  
	<xsl:template name="get_generated_id_of_heading">
		<xsl:param name="smilUri"/>
		<xsl:param name="headingName"/>
		<xsl:if test="$smilUri!=''">
			<xsl:variable name="smil" select="substring-before($smilUri, '#')"/>
			<xsl:variable name="fragment" select="substring-after($smilUri, '#')"/>		
			<xsl:variable name="contentUri" select="document(concat($baseDir,$smil))//*[@id=$fragment]/text/@src"/>
			<!--<xsl:message>smilUri: <xsl:value-of select="$smilUri"/>, contentUri: <xsl:value-of select="$contentUri"/></xsl:message>-->
			<xsl:variable name="content" select="substring-before($contentUri, '#')"/>
			<xsl:variable name="contFrag" select="substring-after($contentUri, '#')"/>
			
			<xsl:for-each select="document(concat($baseDir,$content))//*[@id=$contFrag]">
					<xsl:value-of select="generate-id(ancestor-or-self::x:*[local-name()=$headingName][1])"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="get_generated_id_of_element_with_class">
		<xsl:param name="smilUri"/>
		<xsl:param name="className"/>
		<xsl:if test="$smilUri!=''">
			<xsl:variable name="smil" select="substring-before($smilUri, '#')"/>
			<xsl:variable name="fragment" select="substring-after($smilUri, '#')"/>		
			<xsl:variable name="contentUri" select="document(concat($baseDir,$smil))//*[@id=$fragment]/text/@src"/>
			<!--<xsl:message>smilUri: <xsl:value-of select="$smilUri"/>, contentUri: <xsl:value-of select="$contentUri"/></xsl:message>-->
			<xsl:variable name="content" select="substring-before($contentUri, '#')"/>
			<xsl:variable name="contFrag" select="substring-after($contentUri, '#')"/>
			
			<xsl:for-each select="document(concat($baseDir,$content))//*[@id=$contFrag]">
					<xsl:value-of select="generate-id(ancestor-or-self::x:*[@class=$className][1])"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
  
</xsl:transform>