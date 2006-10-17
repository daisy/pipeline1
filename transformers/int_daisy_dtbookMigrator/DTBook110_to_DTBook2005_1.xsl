<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:d="http://www.daisy.org/z3986/2005/dtbook/"
	xmlns:meta="rnib.org.uk/tbs#"
	xmlns:hks="http://www.statped.no/huseby/xml/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	exclude-result-prefixes="d xs meta hks" 
	>

<!-- Import the generic transformation rules -->
<xsl:import href="DTBookMigratorEngine.xsl" />

<xsl:variable name="DTBook.Out.version" as="xs:string" select="'2005-1'" />

<!-- How to output the upgraded DTBook file -->	
<xsl:output indent="yes" method="xml" version="1.0" encoding="UTF-8"
	doctype-public="-//NISO//DTD dtbook 2005-1//EN"
	doctype-system="dtbook-2005-1.dtd"/>        

<!-- 	===========================================================
		Local transformation rules may be placed below

		As we use xsl:import to refer to generic rules, it is possible to
		define localized rules that will replace the generic ones 
		=========================================================== -->

</xsl:stylesheet>