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
    	<sch:report test="count(dtbk:*)=1 and dtbk:strong and dtbk:*[normalize-space(preceding-sibling::text())=''] and dtbk:*[normalize-space(following-sibling::text())='']">[tpbHeuM2] A complete section is marked up as strong. Is that correct?</sch:report>
    	<sch:report test="count(dtbk:*)=1 and dtbk:em     and dtbk:*[normalize-space(preceding-sibling::text())=''] and dtbk:*[normalize-space(following-sibling::text())='']">[tpbHeuM2] A complete section is marked up as em. Is that correct?</sch:report>
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
  
  <!-- Rule M5: entry and pagenum in toc -->
  <sch:pattern name="dtbook_TPBheuristic_tocEntryPagenum" id="dtbook_TPBheuristic_tocEntryPagenum">
    <sch:rule context="dtbk:frontmatter/dtbk:level1[@class='toc']/dtbk:list/dtbk:li">
    	<sch:assert test="count(dtbk:lic[@class='entry'])=1 and count(dtbk:lic[@class='pagenum'])=1">[tpbHeuM5] Should this item in the table of contents have a 'entry' - 'pagenum' pair?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M6: Only poem in blockquote -->
  <sch:pattern name="dtbook_TPBheuristic_poemInBlockquote" id="dtbook_TPBheuristic_poemInBlockquote">
    <sch:rule context="dtbk:*[self::dtbk:blockquote]">
    	<sch:report test="count(dtbk:*)=1 and dtbk:poem">[tpbHeuM6] Should the poem really be wrapped in a blockquote?</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M8: Missing docauthor -->
  <sch:pattern name="dtbook_TPBheuristic_missingDocauthor" id="dtbook_TPBheuristic_missingDocauthor">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:docauthor">[tpbHeuM8] No docauthor found. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M9: pagenum normal in frontmatter -->
  <sch:pattern name="dtbook_TPBheuristic_pageNormalInFrontmatter" id="dtbook_TPBheuristic_pageNormalInFrontmatter">
    <sch:rule context="dtbk:pagenum[@type='normal']">
    	<sch:report test="ancestor::dtbk:frontmatter">[tpbHeuM9] Should there really be a normal page number in frontmatter?</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M10: pagenum special in bodymatter -->
  <sch:pattern name="dtbook_TPBheuristic_pageSpecialInBodymatter" id="dtbook_TPBheuristic_pageSpecialInBodymatter">
    <sch:rule context="dtbk:pagenum[@type='special']">
    	<sch:report test="ancestor::dtbk:bodymatter">[tpbHeuM10] Should there really be a special page number in bodymatter?</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M11: Missing colophon -->
  <sch:pattern name="dtbook_TPBheuristic_missingColophon" id="dtbook_TPBheuristic_missingColophon">
    <sch:rule context="dtbk:frontmatter">
    	<sch:assert test="dtbk:level1[@class='colophon']">[tpbHeuM11] No colophon found. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M12: Table headings on rows other than the first -->
  <sch:pattern name="dtbook_TPBheuristic_thNotOnFirstRow" id="dtbook_TPBheuristic_thNotOnFirstRow">
    <sch:rule context="dtbk:tr[position()!=1]">
    	<sch:report test="dtbk:th">[tpbHeuM12] This table row has one or more 'th' element on a row other than the first. Is that correct?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M13: Mixing td and th in the same row -->
  <sch:pattern name="dtbook_TPBheuristic_thTdSameRow" id="dtbook_TPBheuristic_thTdSameRow">
    <sch:rule context="dtbk:tr">
    	<sch:report test="count(dtbk:th)>0 and count(dtbk:td)>0">[tpbHeuM13] Should this table row really be mixing 'th' and 'td' elements?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M14: Mixing td and th in the same column (except first row) -->
  <!-- FIXME this doesn't work...
  <sch:pattern name="dtbook_TPBheuristic_thTdSameColumn" id="dtbook_TPBheuristic_thTdSameColumn">
    <sch:rule context="dtbk:tr[position()!=1]/dtbk:td">
    	<sch:report test="ancestor::dtbk:tr[position()!=1]/dtbk:th[position()=current()/position()]">[tpbHeuM14] Should this table column really be mixing 'th' and 'td' elements?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:tr[position()!=1]/dtbk:th">
    	<sch:report test="ancestor::dtbk:tr[position()!=1]/dtbk:td[position()=current()/position()]">[tpbHeuM14] Should this table column really be mixing 'th' and 'td' elements?</sch:report>
    </sch:rule>
  </sch:pattern> 
  -->
  
  <!-- Rule M15: Multiple br -->
  <sch:pattern name="dtbook_TPBheuristic_multipleBr" id="dtbook_TPBheuristic_multipleBr">
    <sch:rule context="dtbk:br">
    	<sch:report test="following-sibling::*[1][self::dtbk:br] and normalize-space(following-sibling::node()[1][self::text()])=''">[tpbHeuM15] Multiple line breaks. Should this be a paragraph?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M16: Strange meta info -->
  <sch:pattern name="dtbook_TPBheuristic_metaPrefix" id="dtbook_TPBheuristic_metaPrefix">
    <sch:rule context="dtbk:meta">
    	<sch:report test="starts-with(@name, 'cd:')">[tpbHeuM16] Misspelled meta information?</sch:report>
    	<sch:report test="not(contains(@name, ':'))">[tpbHeuM16] Missing prefix on metadata name?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M17: image groups in tables -->
  <sch:pattern name="dtbook_TPBheuristic_imggroupInTable" id="dtbook_TPBheuristic_imggroupInTable">
    <sch:rule context="dtbk:imggroup">
    	<sch:report test="ancestor::dtbk:table">[tpbHeuM17] This is an image group inside a table. Would a simple image element be enough?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M18: xml:lang on root element is neither swedish nor english -->
  <sch:pattern name="dtbook_TPBheuristic_rootXmlLang" id="dtbook_TPBheuristic_rootXmlLang">
    <sch:rule context="dtbk:dtbook">
    	<sch:report test="not(lang('sv')) and not(lang('en'))">[tpbHeuM18] xml:lang on root element is neither english nor swedish. Is that correct?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M20: Missing metadata -->
  <sch:pattern name="dtbook_TPBheuristic_missingMetadata" id="dtbook_TPBheuristic_missingMetadata">
    <sch:rule context="dtbk:head">
    	<sch:assert test="meta[@name='dc:Creator']">[tpbHeuM20] This document has no dc:Creator metadata. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M22 & M23: lists of type ul or ol have list items starting with a bullet -->
  <sch:pattern name="dtbook_TPBheuristic_listUlwithBullet" id="dtbook_TPBheuristic_listUlwithBullet">
    <sch:rule context="dtbk:list[@type='ul' or @type='ol']/dtbk:li">
    	<!-- 2022 BULLET, 25A0 BLACK SQUARE, 25C6 BLACK DIAMOND, 25E6 WHITE BULLET, 2713 CHECK MARK, 25A1 WHITE SQUARE-->
    	<sch:report test="string-length(translate(substring(normalize-space(.),1,1),'&#x2022;&#x25a0;&#x25c6;&#x25e6;&#x2713;&#x25a1;',''))=0">[tpbHeuM22] This list item starts with a bullet. Shouldn't that be removed, or is this a list of type="pl"?</sch:report>
    	<sch:report test="string-length(translate(substring(normalize-space(.),1,1),'0123456789',''))=0">[tpbHeuM23] This list item starts with a number. Shouldn't that be removed, or is this a list of type="pl"?</sch:report>
    </sch:rule>
  </sch:pattern> 
    
</sch:schema>
