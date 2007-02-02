<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB when running Narrator</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 70: Heading for the colophon -->
  <sch:pattern name="dtbook_TPBnarrator_colophonHeading" id="dtbook_TPBnarrator_colophonHeading">
    <sch:rule context="dtbk:level1[@class='colophon']">
    	<sch:report test="lang('sv') and dtbk:h1!='Kolofon'">[tpbnarrator70] Heading of colophon must be 'Kolofon' (swedish)</sch:report>
    	<sch:report test="lang('en') and dtbk:h1!='Colophon'">[tpbnarrator70] Heading of colophon must be 'Colophon' (english)</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 97: Check for §17 information -->
  <sch:pattern name="dtbook_TPBnarrator_paragraph17" id="dtbook_TPBnarrator_paragraph17">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:level1[@id='para17']">[tpbnarrator97] No §17 information (id="para17") found.</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 98: Check for "Infomation about the talking book" -->
  <sch:pattern name="dtbook_TPBnarrator_notice" id="dtbook_TPBnarrator_notice">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:level1[@id='notice']">[tpbnarrator98] No "information about the talking book" (id="notice") found.</sch:assert>
    </sch:rule>
  </sch:pattern>
    
</sch:schema>
