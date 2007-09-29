<?xml version="1.0" encoding="utf-8"?>
<!--made 24.5.04-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">	
	<xsl:output indent="no" method="text" encoding="windows-1252"/>	
	<xsl:include href="dtbook_rtf_call.xsl"/>
	<xsl:strip-space elements="*"/>
	<xsl:template match="/">
		<xsl:call-template name="BIGHEADER"/>
		<xsl:apply-templates/>
		<xsl:text>}	</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
