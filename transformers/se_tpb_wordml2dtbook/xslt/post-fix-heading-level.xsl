<?xml version="1.0" encoding="UTF-8"?>
<!--
 * WordML2DTBook
 * Copyright © 2006 The Swedish Library of Talking Books and Braille, TPB (www.tpb.se)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -->
<!--
		Fix heading level
		???

		Joel Håkansson, TPB
		Version 2007-05-03
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:include href="./modules/recursive-copy.xsl"/>
	<xsl:include href="./modules/output.xsl"/>
	
	<xsl:template match="dtb:level2">
		<xsl:variable name="level" select="2"/>
		<xsl:variable name="rLevel" select="count(ancestor::*[self::dtb:level1 or self::dtb:level2][count(dtb:level1 or dtb:level2)!=count(*)])"/>
		<xsl:choose>
			<xsl:when test="dtb:h1 or dtb:h2">
				<!-- -->
			</xsl:when>
			<xsl:when test="dtb:level6"></xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
