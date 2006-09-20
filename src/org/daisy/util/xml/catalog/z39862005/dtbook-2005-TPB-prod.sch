<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - producer</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 54: prodnote in imggoup -->
  <sch:pattern name="dtbook_TPBprod_prodnoteInImggroup" id="dtbook_TPBprod_prodnoteInImggroup">
    <sch:rule context="dtbk:imggroup/dtbk:prodnote">
    	<sch:assert test=".='Bildbeskrivning'">[tpbprod54] Value of prodnote in imggroup should be 'Bildbeskrivning'</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 73: Only allow DTBook 2005-2 -->
  <sch:pattern name="dtbook_TPBprod_dtbookVersion" id="dtbook_TPBprod_dtbookVersion">
    <sch:rule context="dtbk:dtbook">
    	<sch:assert test="@version='2005-2'">[tpbprod73] DTBook version must be 2005-2.</sch:assert>
    </sch:rule>
  </sch:pattern>
    
</sch:schema>
