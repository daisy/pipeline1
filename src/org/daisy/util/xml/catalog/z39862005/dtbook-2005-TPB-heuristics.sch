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
    <sch:rule context="dtbk:book">
    	<sch:assert test="dtbk:*[self::dtbk:frontmatter or self::dtbk:rearmatter]/dtbk:level1[@class='colophon']">[tpbHeuM11] No colophon found. Is that correct?</sch:assert>
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
    	<sch:assert test="dtbk:meta[@name='dc:Creator']">[tpbHeuM20] This document has no dc:Creator metadata. Is that correct?</sch:assert>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M21: comments -->
  <sch:pattern name="dtbook_TPBheuristic_comments" id="dtbook_TPBheuristic_comments">
    <sch:rule context="comment()">
    	<sch:assert test="false()">[tpbHeuM21] Should this comment be here?</sch:assert>
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
  
  <!-- Rule M25: pagenum before list items in a lists -->
  <sch:pattern name="dtbook_TPBheuristic_pagenumBeforeLi" id="dtbook_TPBheuristic_pagenumBeforeLi">
    <sch:rule context="dtbk:list/dtbk:pagenum">
    	<sch:assert test="preceding-sibling::dtbk:li">[tpbHeuM25] This pagenum element inside a list has no list items before it. Shouldn't the pagenum element be placed before the list?</sch:assert>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M27: 'and' or 'och' in dc:Creator/docauthor -->
  <sch:pattern name="dtbook_TPBheuristic_andOch" id="dtbook_TPBheuristic_andOch">
    <sch:rule context="dtbk:docauthor">
    	<sch:report test="contains(.,' and ') or contains(.,' och ')">[tpbHeuM27] Does this <name/> element list two authors that should be marked up as separate elements?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:meta[@name='dc:Creator']">
    	<sch:report test="contains(@content,' and ') or contains(@content,' och ')">[tpbHeuM27] Does this <name/> element list two creators that should be marked up as separate meta data elements?</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M28: Pagenum element incorrectly breaks a paragraph into two -->
  <sch:pattern name="dtbook_TPBheuristic_pagenumBreaksP" id="dtbook_TPBheuristic_pagenumBreaksP">
    <sch:rule context="dtbk:pagenum[preceding-sibling::*[1][self::dtbk:p] and following-sibling::*[1][self::dtbk:p]]">
    	<sch:report test="string-length(
    	                     translate(
    	                         substring( 
    	                            normalize-space(preceding-sibling::*[1]/descendant::text()[last()]),
    	                            string-length(normalize-space(preceding-sibling::*[1]/descendant::text()[last()]))
    	                         ),
    	                         '.?!',
    	                         ''
    	                      )
    	                   )>0">[tpbHeuM28] Does this pagenum element incorrectly break one paragraph into two?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M30: lic in table of contents -->
  <sch:pattern name="dtbook_TPBheuristic_licInToc" id="dtbook_TPB_licInToc">
    <sch:rule context="dtbk:frontmatter/dtbk:level1[@class='toc']/dtbk:list//dtbk:li">
    	<sch:assert test="child::*[self::dtbk:lic and (@class='entry' or @class='pagenum')]">[tpbHeuM30] Should this list item in table of contents only have lic children having class attribute 'entry' or 'pagenum'?</sch:assert>
    	<sch:assert test="normalize-space(text())=''">[tpbHeuM30] Should there really be a list item having text outside a lic here?</sch:assert>
    </sch:rule>
    <sch:rule context="dtbk:frontmatter/dtbk:level1[@class='toc']/dtbk:list//dtbk:lic[@class='pagenum']">
    	<sch:assert test=".=//dtbk:pagenum">[tpbHeuM30] The pagenum element referenced by this lic does not exist. Is that really OK?</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule M31: nested em/strong -->
  <sch:pattern name="dtbook_TPBheuristic_nestedEmStrong" id="dtbook_TPBheuristic_nestedEmStrong">
    <sch:rule context="dtbk:em">
    	<sch:report test="dtbk:em">[tpbHeuM31] Should nested em really be used here?</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:strong">
    	<sch:report test="dtbk:strong">[tpbHeuM31] Should nested strong really be used here?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M32: only items with bullets or only items with numbers in a list of type 'pl' -->
  <sch:pattern name="dtbook_TPBheuristic_listPlBullet" id="dtbook_TPBheuristic_listPlBullet">
    <sch:rule context="dtbk:list[@type='pl' and not(ancestor::dtbk:level1[@class='toc'])]">
    	<sch:report test="not(string-length(translate(substring(normalize-space(dtbk:li),1,1),'&#x2022;&#x25a0;&#x25c6;&#x25e6;&#x2713;&#x25a1;',''))!=0)">[tpbHeuM32] Should this be a list of type 'ul'?</sch:report>
    	<sch:report test="not(string-length(translate(substring(normalize-space(dtbk:li),1,1),'0123456789',''))!=0)">[tpbHeuM32] Should this be a list of type 'ol'?</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule M33: whitespace around em/strong -->
  <sch:pattern name="dtbook_TPBheuristic_whitespaceEmStrong" id="dtbook_TPBheuristic_whitespaceEmStrong">
    <sch:rule context="dtbk:*[self::dtbk:em or self::dtbk:strong]">
    	<sch:report test="string-length(normalize-space(substring(.,1,1)))=0">[tpbHeuM33] Should this <sch:name/> really have leading whitespace?</sch:report>
    	<sch:report test="string-length(normalize-space(substring(.,string-length(.),1)))=0">[tpbHeuM33] Should this <sch:name/> really have trailing whitespace?</sch:report>
    	<sch:report test="preceding-sibling::node()[1 and text()] and string-length(normalize-space(substring(preceding-sibling::node()[1],string-length(preceding-sibling::node()[1]),1)))!=0">[tpbHeuM33] Shouldn't there be whitespace before this <sch:name/>?</sch:report>
    	<sch:report test="following-sibling::node()[1 and text()] and string-length(normalize-space(substring(following-sibling::node()[1],1,1)))!=0 and string-length(translate(substring(following-sibling::node()[1],1,1),'.,!?',''))!=0">[tpbHeuM33] Shouldn't there be whitespace after this <sch:name/>?</sch:report>
    </sch:rule>
  </sch:pattern> 
    
</sch:schema>
