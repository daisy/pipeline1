<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - image descriptions</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule 54: prodnote in imggroup -->
  <sch:pattern name="dtbook_TPBimgdesc_prodnoteInImggroup" id="dtbook_TPBimgdesc_prodnoteInImggroup">
    <sch:rule context="dtbk:imggroup/dtbk:prodnote">
    	<sch:assert test="translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')!='bildbeskrivning'">[tpbimg54] Value of prodnote in imggroup must not be 'Bildbeskrivning'</sch:assert>
    	<sch:assert test="translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')!='image description'">[tpbimg54] Value of prodnote in imggroup must not be 'Image description'</sch:assert>
    	<sch:assert test="@render='optional'">[tpbimg54] The value of the render attribute must be 'optional'</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 108: Check the value of dc:Identifier for SIT -->
  <!--
  <sch:pattern name="dtbook_TPBimgdesc_id" id="dtbook_TPBimgdesc_id">
    <sch:rule context="dtbk:head[dtbk:meta[@name='dc:Publisher' and @content='SIT']]">    	
    	<sch:report test="count(//dtbk:imggroup)!=0 and 
    	                  (translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')!='X00000B' and
    	                   translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')!='X00000C'
    	                  )">[tpbimg108] The dc:Identifier for SIT must be on the form 'X00000B' or 'X00000C' when there are images without dummy descriptions.</sch:report>
    	<sch:report test="count(//dtbk:imggroup)=0 and 
    	                  (translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')!='X00000A' and
    	                   translate(dtbk:meta[@name='dc:Identifier' and @scheme='SIT']/@content,'0123456789','0000000000')!='X00000C'
    	                  )">[tpbimg108] The dc:Identifier for SIT must be on the form 'X00000A' or 'X00000C' when there are no images.</sch:report>                  
    </sch:rule>
  </sch:pattern> 
  -->
    
</sch:schema>
