<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">
  <sch:title>DTBook 2005 Schematron tests for the MathML extension</sch:title>
  <sch:ns prefix="dtbook" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  <sch:ns prefix="m" uri="http://www.w3.org/1998/Math/MathML"/>
  
  <sch:pattern name="mathroot" id="mathroot">
    <sch:rule context="m:math">    
      <!-- alttext exists -->
      <sch:assert test="count(@alttext)=1">[sch][zedid::math_attributeAltTextPresent]</sch:assert>  
      <!-- altimg exists -->
      <sch:assert test="count(@altimg)=1">[sch][zedid::math_attributeAltImgPresent]</sch:assert>		    
    </sch:rule>   
  </sch:pattern>
  
  <sch:pattern name="mathimg" id="mathimg">
    <sch:rule context="m:math[@altimg]">
  	  <!-- altimg has a value -->      
      <sch:assert test="string-length(normalize-space(@altimg)) > 0">[sch][zedid::math_attributeAltImgValue]</sch:assert>            
    </sch:rule>
  </sch:pattern>
 
  <sch:pattern name="mathtext" id="mathtext">
    <sch:rule context="m:math[@alttext]">
   	  <!-- alttext has a value -->      
      <sch:assert test="string-length(normalize-space(@alttext)) > 0">[sch][zedid::math_attributeAltTextValue]</sch:assert>
    </sch:rule>
  </sch:pattern>
  
</sch:schema>