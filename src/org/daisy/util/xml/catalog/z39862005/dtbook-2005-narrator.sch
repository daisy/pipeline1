<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for Narrator</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <sch:key name="pageNormalValues" match="dtbk:pagenum[@page='normal' or not(@page)]" path="."/>

  <!-- Rule 7: No <list> or <dl> inside <p> -->
  <sch:pattern name="dtbook_narrator_noListOrDlinP" id="dtbook_narrator_noListOrDlinP">
    <sch:rule context="dtbk:p">
      <sch:report test="dtbk:list">[narrator07] Lists are not allowed inside paragraphs.</sch:report>
      <sch:report test="dtbk:dl">[narrator07] Definition lists are not allowed inside paragraphs.</sch:report>
    </sch:rule>
  </sch:pattern> 
  
  <!-- Rule 10: Metadata for dc:Language, dc:Date and dc:Publisher must exist -->
  <sch:pattern name="dtbook_MetaDate" id="dtbook_MetaDate">
    <sch:rule context="dtbk:head">
      <!-- dc:Language -->
      <sch:assert test="count(dtbk:meta[@name='dc:Language'])>=1">[narrator10] Meta dc:Language must occur at least once</sch:assert>  
      <!-- dc:Date -->
      <sch:assert test="count(dtbk:meta[@name='dc:Date'])=1">[narrator10] Meta dc:Date=YYYY-MM-DD must occur once</sch:assert>
      <sch:report test="dtbk:meta[@name='dc:Date' and translate(@content, '0123456789', '0000000000')!='0000-00-00']">[narrator10] Meta dc:Date must have format YYYY-MM-DD</sch:report>
      <!-- dc:Publisher -->
      <sch:assert test="count(dtbk:meta[@name='dc:Publisher'])=1">[narrator10] Meta dc:Publisher must occur once</sch:assert>
    </sch:rule>
    <sch:rule context="dtbk:meta[@name='dc:Publisher']">      
      <sch:assert test="string-length(normalize-space(@content)) > 0">[narrator10] Meta dc:Publisher must contain non-whitespace content</sch:assert>      
    </sch:rule>
  </sch:pattern>
    
  <!-- Rule 11: Root element must have @xml:lang -->
  <sch:pattern name="dtbook_narrator_xmlLang" id="dtbook_narrator_xmlLang">
  	<sch:rule context="dtbk:dtbook">
  		<sch:assert test="@xml:lang">[narrator11] Root element must have an xml:lang attribute</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 14:  Don't allow <h x+1> in <level x+1> unless <h x> in <level x> is present -->
  <sch:pattern name="dtbook_narrator_levelHeading" id="dtbook_narrator_levelHeading">
    <sch:rule context="dtbk:level1[dtbk:level2/dtbk:h2]">
      <sch:assert test="dtbk:h1">[narrator14] level1 with no h1 when level2 is present</sch:assert>  
    </sch:rule>
    <sch:rule context="dtbk:level2[dtbk:level3/dtbk:h3]">
      <sch:assert test="dtbk:h2">[narrator14] level2 with no h2 when level3 is present</sch:assert>  
    </sch:rule>
    <sch:rule context="dtbk:level3[dtbk:level4/dtbk:h4]">
      <sch:assert test="dtbk:h3">[narrator14] level3 with no h3 when level4 is present</sch:assert>  
    </sch:rule>
    <sch:rule context="dtbk:level4[dtbk:level5/dtbk:h5]">
      <sch:assert test="dtbk:h4">[narrator14] level4 with no h4 when level5 is present</sch:assert>  
    </sch:rule>
    <sch:rule context="dtbk:level5[dtbk:level6/dtbk:h6]">
      <sch:assert test="dtbk:h5">[narrator14] level5 with no h5 when level6 is present</sch:assert>  
    </sch:rule>
    <sch:rule context="dtbk:level[dtbk:level/dtbk:hd]">
      <sch:assert test="dtbk:hd">[narrator14] level with no hd when level is present</sch:assert>  
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 63: Only note references within the same document -->
  <sch:pattern name="dtbook_narrator_noterefLocal" id="dtbook_narrator_noterefLocal">
  	<sch:rule context="dtbk:noteref">
  		<sch:assert test="not(contains(@idref, '#')) or starts-with(@idref,'#')">[narrator63] Only note references within the same document are allowed</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 64: Only annotation references within the same document -->
  <sch:pattern name="dtbook_narrator_annorefLocal" id="dtbook_narrator_annorefLocal">
  	<sch:rule context="dtbk:annoref">
  		<sch:assert test="not(contains(@idref, '#')) or starts-with(@idref,'#')">[narrator63] Only annotation references within the same document are allowed</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 100: Every document needs at least one heading on level 1 -->
  <sch:pattern name="dtbook_narrator_headingLeve1" id="dtbook_narrator_headingLeve1">
  	<sch:rule context="dtbk:book">
  		<sch:assert test="current()//dtbk:level1/dtbk:h1 or 
  		                  dtbk:frontmatter/dtbk:level/dtbk:hd or 
  		                  dtbk:bodymatter/dtbk:level/dtbk:hd or 
  		                  dtbk:rearmatter/dtbk:level/dtbk:hd">[narrator100] At least one heading on first level is required</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 104: Headings may not be empty elements -->
  <sch:pattern name="dtbook_narrator_emptyElements" id="dtbook_narrator_emptyElements">
  	<sch:rule context="dtbk:*[self::dtbk:h1 or self::dtbk:h2 or self::dtbk:h3 or self::dtbk:h4 or self::dtbk:h5 or self::dtbk:h6 or self::dtbk:hd[parent::dtbk:level]]">
  		<sch:report test="normalize-space(.)=''">[narrator104] Heading <name/> may not be empty</sch:report>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 107: doctitle in frontmatter must exist (for Daisy 2.02) -->
  <sch:pattern name="dtbook_narrator_frontmatterDoctitle" id="dtbook_narrator_frontmatterDoctitle">
  	<sch:rule context="dtbk:book">
  		<sch:assert test="not(dtbk:frontmatter) or count(dtbk:frontmatter/dtbk:doctitle)>=1">[narrator107] If there is a frontmatter, there must be a doctitle element</sch:assert>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 109: Only allow images in JPG, PNG or SVG format -->
  <sch:pattern name="dtbook_narrator_imageFormat" id="dtbook_narrator_imageFormat">
  	<sch:rule context="dtbk:img">
  		<sch:assert test="string-length(@src)>=5">[narrator109] Invalid image filename.</sch:assert>
  		<sch:assert test="substring(@src,string-length(@src) - 3, 4)='.jpg' or
  		                  substring(@src,string-length(@src) - 3, 4)='.png' or
  		                  substring(@src,string-length(@src) - 3, 4)='.svg'">[narrator109] Images must be in JPG (*.jpg), PNG (*.png) or SVG (*.svg) format.</sch:assert>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 113: pagenum value must be unique for page type 'normal' -->  
  <sch:pattern name="dtbook_narrator_pagenumValueUnique" id="dtbook_narrator_pagenumValueUnique">
  	<sch:rule context="dtbk:pagenum[@page='normal' or not(@page)]">
  		<sch:assert test="count(key('pageNormalValues', .))=1">[narrator113] pagenum value must be unique for page type 'normal'</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 114: disallow title element in 2005-1 and 2005-2 documents -->  
  <sch:pattern name="dtbook_narrator_titleElement" id="dtbook_narrator_titleElement">
  	<sch:rule context="dtbk:title">
  		<sch:report test="/dtbk:dtbook/@version='2005-1'">[narrator114] title element is not allowed (due to a bug in the 2005-1 DTD)</sch:report>
  		<sch:report test="/dtbk:dtbook/@version='2005-2'">[narrator114] title element is not allowed (due to a bug in the 2005-2 DTD)</sch:report>
  	</sch:rule>
  </sch:pattern>
  
</sch:schema>
