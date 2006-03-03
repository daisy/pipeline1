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
    <c:name>createNcc</c:name>
    <c:version>0.2</c:version>
    
    <c:author>Linus Ericson</c:author>
    <c:description>Creates the Daisy 2.02 ncc.html file.</c:description>    
  </c:config>
  
  <xsl:param name="date"/>
  <xsl:param name="baseDir"/>

  <!-- Don't add doctype yet. Let the ncc-clean.xsl handle that. -->
  <xsl:output method="xml" 
	      encoding="utf-8" 
	      indent="yes"/>

  <!-- ****************************************************************
       Root template
       **************************************************************** -->
  <xsl:template match="/">
    <ncc:html xmlns="http://www.w3.org/1999/xhtml">
      <ncc:head>        
        <xsl:call-template name="metadata"/>
      </ncc:head>
      <ncc:body>   
        <xsl:call-template name="navigation"/>
      </ncc:body>
    </ncc:html>
  </xsl:template>

	<xsl:template name="navigation">
		<xsl:apply-templates select="//o:itemref[1]">
			<xsl:with-param name="first" select="'true'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="//o:itemref[position() != 1]">
			<xsl:with-param name="first" select="'false'"/>
		</xsl:apply-templates>
	</xsl:template>
  
  <xsl:template match="o:itemref">
  	<xsl:param name="first"/>
  	<xsl:variable name="idref" select="@idref"/>
  	<!--<xsl:message>SMIL: <xsl:value-of select="concat($baseDir, //o:item[@id=$idref]/@href)"/></xsl:message>-->

  	<xsl:apply-templates select="document(concat($baseDir, //o:item[@id=$idref]/@href))//body/seq">
  		<xsl:with-param name="first" select="$first"/>
  		<xsl:with-param name="doc" select="//o:item[@id=$idref]/@href"/>
  	</xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="body/seq">
  	<xsl:param name="first"/>
  	<xsl:param name="doc"/>
  	<xsl:apply-templates>
  		<xsl:with-param name="first" select="$first"/>
  		<xsl:with-param name="doc" select="$doc"/>
  	</xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="par">
  	<xsl:param name="first"/>
  	<xsl:param name="doc"/>
  	<xsl:choose>
  		<xsl:when test="@system-required and not(@system-required='footnote-on' and count(ancestor::seq)!=2)">
  			<ncc:span>
  				<xsl:attribute name="id">
  					<xsl:value-of select="generate-id()"/>
  				</xsl:attribute>
	  			<xsl:choose>
	  				<xsl:when test="@system-required='pagenumber-on'">
	  					<xsl:attribute name="class">
	  						<xsl:call-template name="get_class_value_of">
	  							<xsl:with-param name="uri" select="text/@src"/>
	  						</xsl:call-template>
	  					</xsl:attribute>
	  				</xsl:when>
	  				<xsl:when test="@system-required='sidebar-on'">
	  					<xsl:attribute name="class">
	  						<xsl:value-of select="'sidebar'"/>
	  					</xsl:attribute>
	  				</xsl:when>
	  				<xsl:when test="@system-required='prodnote-on'">
	  					<xsl:attribute name="class">
	  						<xsl:value-of select="'optional-prodnote'"/>
	  					</xsl:attribute>
	  				</xsl:when>
	  				<xsl:when test="@system-required='footnote-on'">
	  					<xsl:attribute name="class">
	  						<xsl:value-of select="'noteref'"/>
	  					</xsl:attribute>
	  				</xsl:when>
	  			</xsl:choose>
	  			<ncc:a>
	  					<xsl:choose>
	  						<xsl:when test="@system-required='footnote-on'">
	  							<xsl:attribute name="href">
	  								<xsl:value-of select="$doc"/>
	  								<xsl:text>#</xsl:text>
	  								<xsl:value-of select="preceding-sibling::par[1]/@id"/>
	  							</xsl:attribute>
	  							<xsl:call-template name="get_content_value_of">
	  								<xsl:with-param name="uri" select="preceding-sibling::par[1]/text/@src"/>
	  							</xsl:call-template>
			  				</xsl:when>
			  				<xsl:otherwise>
	  							<xsl:attribute name="href">
	  								<xsl:value-of select="$doc"/>
	  								<xsl:text>#</xsl:text>
	  								<xsl:value-of select="@id"/>
	  							</xsl:attribute>
	  							<xsl:call-template name="get_content_value_of">
	  								<xsl:with-param name="uri" select="text/@src"/>
	  							</xsl:call-template>
			  				</xsl:otherwise>
	  					</xsl:choose>	  					
	  			</ncc:a>
  			</ncc:span>
  		</xsl:when>
  		<xsl:otherwise>
  			<!--<ncc:hx>hopp</ncc:hx>-->
  			<xsl:call-template name="maybeGenerateHeading">
  				<xsl:with-param name="uri" select="text/@src"/>
  				<xsl:with-param name="doc" select="concat($doc,'#',@id)"/>
	  		</xsl:call-template>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
  <xsl:template name="get_content_value_of">
  	<xsl:param name="uri"/>
  	<xsl:variable name="content" select="substring-before($uri, '#')"/>
		<xsl:variable name="fragment" select="substring-after($uri, '#')"/>		
		<xsl:value-of select="document(concat($baseDir,$content))//*[@id=$fragment]"/>
  </xsl:template>
  
  <xsl:template name="get_class_value_of">
  	<xsl:param name="uri"/>
  	<xsl:variable name="content" select="substring-before($uri, '#')"/>
		<xsl:variable name="fragment" select="substring-after($uri, '#')"/>		
		<xsl:value-of select="document(concat($baseDir,$content))//*[@id=$fragment]/@class"/>
  </xsl:template>
  
  <xsl:template name="maybeGenerateHeading">
  	<xsl:param name="uri"/>
  	<xsl:param name="doc"/>
  	<xsl:variable name="content" select="substring-before($uri, '#')"/>
		<xsl:variable name="fragment" select="substring-after($uri, '#')"/>
		<xsl:for-each select="document(concat($baseDir,$content))//*[@id=$fragment]">
			<xsl:choose>
				<xsl:when test="ancestor-or-self::x:h1">					
					<ncc:h1>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h1>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:h2">
					<ncc:h2>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h2>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:h3">
					<ncc:h3>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h3>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:h4">
					<ncc:h4>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h4>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:h5">
					<ncc:h5>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h5>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:h6">
					<ncc:h6>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:h6>
				</xsl:when>
				<xsl:when test="ancestor-or-self::x:hd">
					<ncc:hd>
						<xsl:call-template name="create_link"><xsl:with-param name="doc" select="$doc"/></xsl:call-template>
					</ncc:hd>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>		
  </xsl:template>
  
  <xsl:template name="create_link">
  	<xsl:param name="doc"/>
  	<xsl:attribute name="id">
  		<xsl:value-of select="generate-id()"/>
  	</xsl:attribute>
  	<ncc:a>
  		<xsl:attribute name="href">
	  		<xsl:value-of select="$doc"/>				
  		</xsl:attribute>
  		<xsl:value-of select="."/>
  	</ncc:a>
  </xsl:template>
  
  <!-- ****************************************************************
       Meatadata elements 
       **************************************************************** -->
  <xsl:template name="metadata">
    <ncc:meta http-equiv="Content-type" content="application/xhtml+xml; charset=utf-8"/>
    <ncc:title><xsl:value-of select="//dc:Title"/></ncc:title>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Creator'"/>
    	<xsl:with-param name="meta" select="'dc:creator'"/>
    </xsl:call-template>
    <xsl:if test="not(//o:dc-metadata/dc:Creator)">
    	<xsl:message>WARNING: No dc:creator found!</xsl:message>
    	<ncc:meta name="dc:creator" content="Unknown"/>
    </xsl:if>
    <ncc:meta name="dc:date">
      <xsl:attribute name="content">
        <xsl:choose>          
          <xsl:when test="$date">
          	<xsl:value-of select="$date"/>
          </xsl:when>
          <xsl:otherwise>
          	<xsl:text>FIXME</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </ncc:meta>
    <ncc:meta name="dc:format" content="Daisy 2.02"/>
    <ncc:meta name="dc:identifier">
      <xsl:attribute name="content">
        <xsl:value-of select="//dc:Identifier"/>
      </xsl:attribute>
    </ncc:meta>        
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Language'"/>
    	<xsl:with-param name="meta" select="'dc:language'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Publisher'"/>
    	<xsl:with-param name="meta" select="'dc:publisher'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Title'"/>
    	<xsl:with-param name="meta" select="'dc:title'"/>
    </xsl:call-template>
    <ncc:meta name="ncc:charset" content="utf-8"/>
    <ncc:meta name="ncc:depth" content="FIXME"/>
    <ncc:meta name="ncc:footnotes" content="FIXME"/>
    <ncc:meta name="ncc:generator">
      <xsl:attribute name="content">
	      <xsl:value-of select="document('')//c:config/c:generator"/>
	      <xsl:text> (</xsl:text>
	      <xsl:value-of select="document('')//c:config/c:name"/>
	      <xsl:text> v</xsl:text>
	      <xsl:value-of select="document('')//c:config/c:version"/>
	      <xsl:text>)</xsl:text>
      </xsl:attribute>
    </ncc:meta>        
    <ncc:meta name="ncc:maxPageNormal" content="FIXME"/>
    <ncc:meta name="ncc:multimediaType" content="audioFullText"/>
    <ncc:meta name="ncc:pageFront" content="FIXME"/>
    <ncc:meta name="ncc:pageNormal" content="FIXME"/>
    <ncc:meta name="ncc:pageSpecial" content="FIXME"/>
    <ncc:meta name="ncc:prodNotes" content="FIXME"/>
    <ncc:meta name="ncc:setInfo" content="1 of 1"/>
    <ncc:meta name="ncc:sidebars" content="FIXME"/>
    <ncc:meta name="ncc:tocItems" content="FIXME"/>
    <ncc:meta name="ncc:totalTime">
      <xsl:attribute name="content">FIXME</xsl:attribute>
    </ncc:meta>    
    
    <!-- Some optional content -->
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Contributor'"/>
    	<xsl:with-param name="meta" select="'dc:contributor'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Coverage'"/>
    	<xsl:with-param name="meta" select="'dc:coverage'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Description'"/>
    	<xsl:with-param name="meta" select="'dc:description'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Relation'"/>
    	<xsl:with-param name="meta" select="'dc:relation'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Rights'"/>
    	<xsl:with-param name="meta" select="'dc:rights'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Source'"/>
    	<xsl:with-param name="meta" select="'dc:source'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Subject'"/>
    	<xsl:with-param name="meta" select="'dc:subject'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_dc_metadata">
    	<xsl:with-param name="name" select="'dc:Type'"/>
    	<xsl:with-param name="meta" select="'dc:type'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:narrator'"/>
    	<xsl:with-param name="meta" select="'ncc:narrator'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:producer'"/>
    	<xsl:with-param name="meta" select="'ncc:producer'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:producedDate'"/>
    	<xsl:with-param name="meta" select="'ncc:producedDate'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:revision'"/>
    	<xsl:with-param name="meta" select="'ncc:revision'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:revisionDate'"/>
    	<xsl:with-param name="meta" select="'ncc:revisionDate'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:sourceDate'"/>
    	<xsl:with-param name="meta" select="'ncc:sourceDate'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:sourceEdition'"/>
    	<xsl:with-param name="meta" select="'ncc:sourceEdition'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:sourcePublisher'"/>
    	<xsl:with-param name="meta" select="'ncc:sourcePublisher'"/>
    </xsl:call-template>
    <xsl:call-template name="opf_x_metadata">
    	<xsl:with-param name="name" select="'dtb:sourceRights'"/>
    	<xsl:with-param name="meta" select="'ncc:sourceRights'"/>
    </xsl:call-template>
  </xsl:template>
  
  
  <xsl:template name="opf_dc_metadata">
  	<xsl:param name="name"/>
  	<xsl:param name="meta"/>  	
  	<xsl:for-each select="//o:dc-metadata/dc:*[name(.)=$name]">
  		<ncc:meta>
  			<xsl:attribute name="name">
  				<xsl:value-of select="$meta"/>
  			</xsl:attribute>
  			<xsl:attribute name="content">
  				<xsl:value-of select="."/>
  			</xsl:attribute>
  		</ncc:meta>
  	</xsl:for-each>
  </xsl:template>
  
  <xsl:template name="opf_x_metadata">
  	<xsl:param name="name"/>
  	<xsl:param name="meta"/>  	
  	<xsl:for-each select="//o:x-metadata/o:meta[@name=$name]">
  		<ncc:meta>
  			<xsl:attribute name="name">
  				<xsl:value-of select="$meta"/>
  			</xsl:attribute>
  			<xsl:attribute name="content">
  				<xsl:value-of select="@content"/>
  			</xsl:attribute>
  		</ncc:meta>
  	</xsl:for-each>
  </xsl:template>
  
</xsl:transform>