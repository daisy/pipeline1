<?xml version="1.0" encoding="utf-8"?>
<!--
  Daisy Pipeline (C) 2005-2008 DBB and Daisy Consortium
  
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
