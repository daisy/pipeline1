<?xml version="1.0" encoding="utf-8"?>
<!--
	TODO:
		- komplexa sub, sup
		- länkar, e-postadresser
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:import href="dtbook2flow_sv_SE.xsl" />
	<xsl:output method="xml" encoding="utf-8" indent="no"/>

	<xsl:template match="dtb:doctitle[parent::dtb:frontmatter] | dtb:docauthor[parent::dtb:frontmatter]" priority="20">
		<block><xsl:apply-templates/></block>
	</xsl:template>

</xsl:stylesheet>
