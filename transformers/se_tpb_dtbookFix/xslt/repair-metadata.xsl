<?xml version="1.0" encoding="UTF-8"?>
<!--
	Repair metadata items
		Version
			2008-01-07

		Description
		    - fix metadata case errors
		    - remove unknown dc-metadata
			- add dtb:uid (if missing) from dc:Identifier
			- add dc:Title (if missing) from doctitle

		Nodes
			meta,head

		Namespaces
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype
			(x) DTBook

		Author
			Linus Ericson, TPB
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/">

	<xsl:include href="recursive-copy2.xsl"/>
	<xsl:include href="output2.xsl"/>

	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:title\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Title</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:subject\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Subject</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:description\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Description</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:type\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Type</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:source\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Source</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:relation\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Relation</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:coverage\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Coverage</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:creator\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Creator</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:publisher\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Publisher</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:contributor\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Contributor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:rights\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Rights</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:date\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Date</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:format\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Format</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:language\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Language</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:meta[matches(@name, '^\s*dc:identifier\s*$','si')]">
		<xsl:call-template name="handleMeta">
			<xsl:with-param name="name">dc:Identifier</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- The known dc metadata are matched by the previous templates, this
		 matches everything else -->
	<xsl:template match="dtb:meta">		
		<xsl:choose>
			<!-- Remove unknown dc metadata -->
			<xsl:when test="starts-with(translate(@name,'DC','dc'), 'dc:')">
				<xsl:variable name="msg">
					<xsl:text>Removing unknown dc metadata '</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>'</xsl:text>
				</xsl:variable>
				<xsl:message><xsl:value-of select="$msg"/></xsl:message>
			</xsl:when>
			<!-- Copy everything else -->
			<xsl:otherwise>
				<xsl:copy>
					<xsl:copy-of select="@*"/>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="dtb:head">
		<xsl:copy>
			<!-- If dtb:uid is missing but dc:Identifier is present, use that -->
			<xsl:if test="not(dtb:meta[matches(@name, '^\s*dtb:uid\s*$','si')]) and
			                  dtb:meta[matches(@name, '^\s*dc:identifier\s*$','si')]">
			    <xsl:message>Adding dtb:uid metadata</xsl:message>
				<xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<xsl:attribute name="name">
						<xsl:text>dtb:uid</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="content">
						<xsl:value-of select="dtb:meta[matches(@name, '^\s*dc:identifier\s*$','si')][1]/@content"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<!-- If there is no dc:Title, but a doctitle is present, use that -->
			<xsl:if test="not(dtb:meta[matches(@name, '^\s*dc:title\s*$','si')]) and //dtb:doctitle">
				<xsl:message>Adding dc:Title from doctitle</xsl:message>
				<xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<xsl:attribute name="name">
						<xsl:text>dc:Title</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="content">
						<xsl:value-of select="(//dtb:doctitle)[1]"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
		
	<xsl:template name="handleMeta">
		<xsl:param name="name"/>
		<xsl:if test="not(@name=$name)">
			<xsl:variable name="msg">
				<xsl:text>Changing meta attribute name from '</xsl:text>
				<xsl:value-of select="@name"/>
				<xsl:text>' to '</xsl:text>
				<xsl:value-of select="$name"/>
				<xsl:text>'</xsl:text>
			</xsl:variable>
			<xsl:message>				
				<xsl:value-of select="$msg"/>				
			</xsl:message>
		</xsl:if>
		<xsl:copy>
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
			<xsl:copy-of select="@*[not(local-name()='name')]"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
