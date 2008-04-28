<?xml version="1.0" encoding="utf-8"?>
<!--
  Daisy Pipeline (C) 2005-2008 Daisy Consortium
  
  This library is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the Free
  Software Foundation; either version 2.1 of the License, or (at your option)
  any later version.
  
  This library is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
  details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--> 
<!--
	A stylesheet for upgrading version 1.0 task scripts
	in the Daisy Pipeline to version 2.0.
	
	A property named 'input' or 'outputPath' in the version
	1.0 script file will be converted to a file parameter in
	the version 2.0	script file. All other properties will
	remain properties in the version 2.0 script file.
	
	Linus Ericson 2007
-->
<xsl:stylesheet 
	version="1.0"
  	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  	
	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	
	<xsl:template match="taskScript">
		<taskScript version="2.0" name="{generate-id()}">
			<xsl:apply-templates/>
		</taskScript>
	</xsl:template>
	
	<xsl:template match="taskScript/name">
		<nicename>
			<xsl:value-of select="."/>
		</nicename>
	</xsl:template>
	
	<xsl:template match="taskScript/description">
		<description>
			<xsl:value-of select="."/>
		</description>
	</xsl:template>
	
	<!-- Normal properties -->
	<xsl:template match="property">
		<xsl:copy>
			<xsl:copy-of select="@name"/>
			<xsl:copy-of select="@value"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- Input property -->
	<xsl:template match="property[@name='input']">
		<parameter name="{@name}" value="" required="true">
			<nicename>Input file</nicename>
			<description>The input file</description>
			<datatype>
				<xsl:choose>
					<xsl:when test="@type='application/x-filesystemDirectory'">
						<directory type="input"/>
					</xsl:when>
					<xsl:otherwise>
						<file mime="{@type}" type="input"/>
					</xsl:otherwise>
				</xsl:choose>				
			</datatype>
		</parameter>
	</xsl:template>
	
	<!-- OutputPath property -->
	<xsl:template match="property[@name='outputPath']">
		<parameter name="{@name}" value="" required="true">
			<nicename>Output path</nicename>
			<description>The output path</description>
			<datatype>
				<xsl:choose>
					<xsl:when test="@type='application/x-filesystemDirectory'">
						<directory type="output"/>
					</xsl:when>
					<xsl:otherwise>
						<file mime="{@type}" type="output"/>
					</xsl:otherwise>
				</xsl:choose>
			</datatype>
		</parameter>
	</xsl:template>
	
	<!-- Copy everything else... -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<!-- ...except for these attributes -->
	<xsl:template match="parameter/@id">
	</xsl:template>
	<xsl:template match="parameter/@ref">
	</xsl:template>	
  	
</xsl:stylesheet>