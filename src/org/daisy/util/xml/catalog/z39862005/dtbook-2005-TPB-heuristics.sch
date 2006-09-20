<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB - error heuristics</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <!-- Rule M1: Missing toc -->
  <sch:pattern name="dtbook_TPBheuristic_missingToc" id="dtbook_TPBheuristic_missingToc">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:level1[@class='toc']">[tpbHeuM1] No table of contents found. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M2: A section in bold/italics -->
  <sch:pattern name="dtbook_TPBheuristic_strongSection" id="dtbook_TPBheuristic_strongSection">
    <sch:rule context="dtbk:*[self::dtbk:p or self::dtbk:th or self::dtbk:td or self::dtbk:dt or self::dtbk:dd or self::dtbk:li]">
    	<sch:report test="count(dtbk:*)=1 and dtbk:strong">[tpbHeuM2] A complete section is marked up as strong. Is that correct?</sch:report>
    	<sch:report test="count(dtbk:*)=1 and dtbk:em">[tpbHeuM2] A complete section is marked up as em. Is that correct?</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M3: Same number of cells in each row in a table -->
  <sch:pattern name="dtbook_TPBheuristic_cellCountRow" id="dtbook_TPBheuristic_cellCountRow">
    <sch:rule context="dtbk:table/dtbk:tr">
    	<sch:assert test="count(dtbk:*[self::dtbk:td or self::dtbk:th])=count(parent::dtbk:table/dtbk:tr[1]/dtbk:*[self::dtbk:td or self::dtbk:th])">[tpbHeuM3] Should all rows in this table have the same number of cells?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
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
  
  <!-- Rule M8: Missing docauthor -->
  <sch:pattern name="dtbook_TPBheuristic_missingDocauthor" id="dtbook_TPBheuristic_missingDocauthor">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:docauthor">[tpbHeuM8] No docauthor found. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M15: Multiple br -->
  <sch:pattern name="dtbook_TPBheuristic_multipleBr" id="dtbook_TPBheuristic_multipleBr">
    <sch:rule context="dtbk:br">
    	<sch:report test="following-sibling::*[1][self::dtbk:br] and normalize-space(following-sibling::node()[1][self::text()])=''">[tpbHeuM15] Multiple line breaks. Should this be a paragraph?</sch:report>
    </sch:rule>
  </sch:pattern>  
    
</sch:schema>
