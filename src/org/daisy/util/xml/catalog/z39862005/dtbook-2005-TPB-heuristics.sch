<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - error heuristics</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule M4: empty th, td, dt, dd -->
  <sch:pattern name="dtbook_TPBheuristic_emptyCheck" id="dtbook_TPBheuristic_emptyCheck">
    <sch:rule context="dtbk:th">
    	<sch:report test="normalize-space(.)=''">[tpbHeuM4] Should this th be empty?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:td">
    	<sch:report test="normalize-space(.)=''">[tpbHeuM4] Should this td be empty?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:dt">
    	<sch:report test="normalize-space(.)=''">[tpbHeuM4] Should this dt be empty?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:dd">
    	<sch:report test="normalize-space(.)=''">[tpbHeuM4] Should this dd be empty?</sch:report>
    </sch:rule>
  </sch:pattern>
    
</sch:schema>
