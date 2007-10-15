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
  
  <!-- Rule 108: Check the values of the dtb:uid (and dc:Identifier for SIT) -->
  <sch:pattern name="dtbook_TPBclean_id" id="dtbook_TPBclean_id">
    <sch:rule context="dtbk:head[dtbk:meta[@name='dc:Publisher' and @content='TPB']]">
    	<sch:assert test="translate(dtbk:meta[@name='dtb:uid']/@content,'0123456789','0000000000')='DTB00000' or
    					  translate(dtbk:meta[@name='dtb:uid']/@content,'0123456789','0000000000')='C00000'
    	                  ">[tpbclean108] The dtb:uid must be on the form C00000/DTB00000 for TPB</sch:assert>
    </sch:rule>     
    <sch:rule context="dtbk:head[dtbk:meta[@name='dc:Publisher' and @content='SIT']]">
    	<sch:assert test="translate(dtbk:meta[@name='dtb:uid']/@content,'0123456789','0000000000')='DTB00000'    	                  
    	                  ">[tpbclean108] The dtb:uid must be on the form DTB00000</sch:assert>
    	<sch:assert test="dtbk:meta[@name='dc:Identifier' and @scheme='SIT']">[tpbclean108] There must be a dc:Identifier specifying the identification number for SIT.</sch:assert>
    </sch:rule>      
  </sch:pattern>    
  <sch:pattern name="dtbook_TPBclean_id2" id="dtbook_TPBclean_id2">
    <sch:rule context="dtbk:head[dtbk:meta[@name='dc:Identifier' and @scheme='SIT']]">
    	<sch:assert test="dtbk:meta[@name='dc:Publisher']/@content='SIT'">[tpbclean108] The dc:Publisher must be 'SIT' when there is a SIT specific dc:Identifier.</sch:assert>
    </sch:rule>
  </sch:pattern>
  

</sch:schema>
