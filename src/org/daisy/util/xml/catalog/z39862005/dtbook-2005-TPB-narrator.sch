<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB when running Narrator</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 70: Heading for the colophon -->
  <sch:pattern name="dtbook_TPBnarrator_colophonHeading" id="dtbook_TPBnarrator_colophonHeading">
    <sch:rule context="dtbk:level1[@class='colophon']">
    	<sch:report test="lang('sv') and dtbk:h1!='Förlagsinformation'">[tpbnarrator70] Heading of colophon must be 'Förlagsinformation' (swedish)</sch:report>
    	<sch:report test='lang("en") and dtbk:h1!="Publisher&apos;s Information"'>[tpbnarrator70] Heading of colophon must be 'Publisher's Information' (english)</sch:report>
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
  
  <!-- Rule 108: Check the value of the dtb:uid -->
  <sch:pattern name="dtbook_TPBnarrator_id" id="dtbook_TPBnarrator_id">
    <sch:rule context="dtbk:head">
    	<sch:assert test="translate(dtbk:meta[@name='dtb:uid']/@content,'0123456789','0000000000')='C00000'">[tpbnarrator108] The dtb:uid must be on the form C00000 when creating a DTB.</sch:assert>
    </sch:rule>
  </sch:pattern>
    
</sch:schema>
