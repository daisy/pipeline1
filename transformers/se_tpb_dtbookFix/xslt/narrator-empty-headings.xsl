<?xml version="1.0" encoding="UTF-8"?>
<!--
    
    Version
    2008-04-03
    
    Description
    Adds a text node to empty headings. 
    
    
    
    Nodes
    dtbook/h1|h2|h3|h4|h5|h6|hd
    
    Namespaces
    (x) "http://www.daisy.org/z3986/2005/dtbook/"
    
    Doctype
    (x) DTBook
    
    Author
    Markus Gylling, DAISY
	
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">
    
    <xsl:include href="recursive-copy2.xsl"/>
    <xsl:include href="output2.xsl"/>
   
    <xsl:template match="dtb:dtbook/dtb:book//dtb:h1|dtb:dtbook/dtb:book//dtb:h2|dtb:dtbook/dtb:book//dtb:h3|dtb:dtbook/dtb:book//dtb:h4|dtb:dtbook/dtb:book//dtb:h5|dtb:dtbook/dtb:book//dtb:h6|dtb:dtbook/dtb:book//dtb:hd">
        	<xsl:choose>	    		
	    		<xsl:when test="normalize-space(.) eq ''">	    				    			
	        		<xsl:message terminate="no">Adding text to empty heading</xsl:message>	            	
	            	<xsl:element name="{local-name(.)}">
    					<xsl:copy-of select="@*"/>
    					<xsl:choose>
    						<xsl:when test="lang('fr')"><xsl:text>Titre vide</xsl:text></xsl:when>    					
    						<xsl:when test="lang('sv')"><xsl:text>Tom Rubrik</xsl:text></xsl:when>    					
    						<xsl:when test="lang('de')"><xsl:text>Leere Überschrift</xsl:text></xsl:when>
    						<xsl:when test="lang('es')"><xsl:text>Título vacío</xsl:text></xsl:when>
    						<xsl:when test="lang('it')"><xsl:text>Intestazione vuota</xsl:text></xsl:when>
    						<xsl:when test="lang('nl')"><xsl:text>Lege rubriek</xsl:text></xsl:when>
    						<xsl:when test="lang('ja')"><xsl:text>空のヘッディング</xsl:text></xsl:when>
    						<xsl:when test="lang('ch')"><xsl:text>空的标题</xsl:text></xsl:when>
    						<xsl:otherwise>
    							<xsl:text>Empty Heading</xsl:text>
    						</xsl:otherwise>
    					</xsl:choose>
					</xsl:element>					
	        	</xsl:when>	        	
	       		<xsl:otherwise>
	        		<xsl:copy-of select="."/>
	        	</xsl:otherwise>
        </xsl:choose> 
    </xsl:template>

     
</xsl:stylesheet>
