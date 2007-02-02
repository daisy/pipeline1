<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - no media specific information allowed</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 97: Check to make sure there is no §17 information -->
  <sch:pattern name="dtbook_TPBclean_paragraph17" id="dtbook_TPBclean_paragraph17">
    <sch:rule context="dtbk:*">
    	<sch:report test="@id='para17'">[tpbclean97] The 'para17' ID is reserved for §17 information which is not allowed at this point.</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 98: Check to make sure there is no "Infomation about the talking book" -->
  <sch:pattern name="dtbook_TPBclean_notice" id="dtbook_TPBclean_notice">
    <sch:rule context="dtbk:*">
    	<sch:report test="@id='notice'">[tpbclean98] The 'notice' ID is reserved for "Information about the talking book" which is not allowed at this point.</sch:report>
    </sch:rule>
  </sch:pattern>
    
</sch:schema>
