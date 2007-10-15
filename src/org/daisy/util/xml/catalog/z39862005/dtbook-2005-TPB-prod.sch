<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - producer</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 54: prodnote in imggroup -->
  <sch:pattern name="dtbook_TPBprod_prodnoteInImggroup" id="dtbook_TPBprod_prodnoteInImggroup">
    <sch:rule context="dtbk:imggroup">
    	<sch:assert test="*[last()][self::dtbk:prodnote]">[tpbprod54] The production note must be the last element in an image group</sch:assert>
    </sch:rule>
    <sch:rule context="dtbk:imggroup/dtbk:prodnote">
    	<sch:assert test=".='Bildbeskrivning' or .='Image description'">[tpbprod54] Value of prodnote in imggroup must be 'Bildbeskrivning' or 'Image description'</sch:assert>
    	<sch:report test="lang('en') and .='Bildbeskrivning'">[tpbprod54] Value of prodnote in imggroup must be 'Image description' in english context</sch:report>
    	<sch:report test="lang('sv') and .='Image description'">[tpbprod54] Value of prodnote in imggroup must be 'Bildbeskrivning' in swedish context</sch:report>
    	<sch:assert test="@render='optional'">[tpbprod54] The value of the render attribute must be 'optional'</sch:assert>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule 108: Check the value of dc:Identifier for SIT -->
  <!--
  <sch:pattern name="dtbook_TPBprod_id" id="dtbook_TPBprod_id">
    <sch:rule context="dtbk:head[dtbk:meta[@name='dc:Publisher' and @content='SIT']]">    	
    	<sch:assert test="translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')='X00000A' or
    	                  translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')='X00000C'
    	                 ">[tpbprod108] The dc:Identifier for SIT must be on the form 'X00000A' or 'X00000C'.</sch:assert>
    	<sch:assert test="count(//dtbk:imggroup)=0 or translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')='X00000A'">[tpbprod108] The dc:Identifier for SIT must be on the form 'X00000A'.</sch:assert>
    </sch:rule>
  </sch:pattern> 
  -->
    
</sch:schema>
