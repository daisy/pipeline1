<?xml version="1.0" encoding="utf-8"?>

<xsl:transform version="1.0" 
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:n="http://www.daisy.org/z3986/2005/ncx/" 
               xmlns:c="http://daisymfc.sf.net/xslt/config"
               xmlns:d="http://www.daisy.org/z3986/2005/dtbook/"
               exclude-result-prefixes="n c d">

  <c:config>
  	<c:generator>DMFC z3986-2005 to Daisy 2.02</c:generator>
    <c:name>smilAddTitle</c:name>
    <c:version>0.1</c:version>
    
    <c:author>Linus Ericson</c:author>
    <c:description>Adds a doctitle smil element in the beginning.</c:description>    
  </c:config>

	<xsl:param name="xhtml_document">content.html</xsl:param>
	<xsl:param name="dtbook_document"/>
	<xsl:param name="ncx_document"/>
	<xsl:param name="add_title">false</xsl:param>

  <xsl:output method="xml" 
	      encoding="utf-8" 
	      indent="yes"
	      doctype-public="-//W3C//DTD SMIL 1.0//EN" 
	      doctype-system="http://www.w3.org/TR/REC-SMIL/SMIL10.dtd"/>


	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="body/seq">
		<seq>
			<xsl:copy-of select="@*"/>
			
			<xsl:if test="$add_title='true'">
				<xsl:for-each select="document($ncx_document)//n:docTitle">
	      	<par endsync="last" id="doctitle">
	      		<text id="doctitleText">
	      			<xsl:attribute name="src">
	      				<xsl:call-template name="find_doctitle"/>
	      			</xsl:attribute>
	      		</text>
	      		<audio id="doctitleAudio">
	      			<xsl:copy-of select="n:audio/@src"/>
	      			<xsl:attribute name="clip-begin">
	      				<xsl:value-of select="n:audio/@clipBegin"/>
	      			</xsl:attribute>
	      			<xsl:attribute name="clip-end">
	      				<xsl:value-of select="n:audio/@clipEnd"/>
	      			</xsl:attribute>
	      		</audio>
	      	</par>
	    	</xsl:for-each>
    	</xsl:if>
    	
    	<xsl:apply-templates/>
		</seq>
	</xsl:template>
  
  
  <xsl:template name="find_doctitle">  	
  	<xsl:variable name="titleId" select="document($dtbook_document)//d:doctitle[1]"/>
  	<xsl:choose>
  		<xsl:when test="$titleId/@id">
  			<xsl:value-of select="concat($xhtml_document, '#', $titleId/@id)"/>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:value-of select="concat($xhtml_document, '#h1classtitle')"/>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
  <xsl:template name="find_docauthor">  	
  	<xsl:variable name="titleId" select="document($dtbook_document)//d:docauthor[1]"/>
  	<xsl:choose>
  		<xsl:when test="$titleId/@id">
  			<xsl:value-of select="concat($xhtml_document, '#', $titleId/@id)"/>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:value-of select="concat($xhtml_document, '#h1classauthor')"/>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
</xsl:transform>
