<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		exclude-result-prefixes="dtb">

  <!-- Excercise answers -->
  <xsl:template match="dtb:span[@class='answer']">
    <xsl:text>\rule[-2pt]{5em}{0.5mm}</xsl:text>
  </xsl:template>

  <xsl:template match="dtb:span[@class='answer_1']">
    <xsl:text>\rule[-2pt]{1em}{0.5mm}</xsl:text>
  </xsl:template>

  <xsl:template match="dtb:span[@class='box']">
    <xsl:text>$\vcenter{\hbox{\fboxsep=.4em \fboxrule=0.5mm\fcolorbox{black}{white}{\null}}}$</xsl:text>
  </xsl:template>

</xsl:stylesheet>
