<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>DTBook 2005 Schematron tests for TPB</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <sch:key name="noterefs" match="dtbk:noteref[@idref]" path="substring-after(@idref,'#')"/>
  <sch:key name="annorefs" match="dtbk:annoref[@idref]" path="substring-after(@idref,'#')"/>
  <sch:key name="pageFrontValues" match="dtbk:pagenum[@page='front']" path="."/>
  <sch:key name="notes" match="dtbk:note[@id]" path="@id"/>

  <!-- Rule 8: Only allow pagenum[@front] in frontmatter -->
  <sch:pattern name="dtbook_TPB_pageFront" id="dtbook_TPB_pageFront">
  	<sch:rule context="dtbk:pagenum[@page='front']">
  		<sch:assert test="ancestor::dtbk:frontmatter">[tpb08]&lt;pagenum page="front"/&gt; may only occur in &lt;frontmatter/&gt;</sch:assert>
  	</sch:rule>  	
  </sch:pattern>
    
  <!-- Rule 9: Disallow empty elements (with a few exceptions) -->
  <sch:pattern name="dtbook_TPB_emptyElements" id="dtbook_TPB_emptyElements">
  	<sch:rule context="dtbk:*">
  		<sch:report test="normalize-space(.)='' and not(*) and not(self::dtbk:img or self::dtbk:br or self::dtbk:meta or self::dtbk:link or self::dtbk:col or self::dtbk:th or self::dtbk:td or self::dtbk:dd)">[tpb09] Element may not be empty</sch:report>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 12: Frontmatter starts with doctitle and docauthor -->
  <sch:pattern name="dtbook_TPB_frontmatterStart" id="dtbook_TPB_frontmatterStart">
  	<sch:rule context="dtbk:frontmatter">
  		<sch:assert test="dtbk:*[1][self::dtbk:doctitle]">[tpb12] Frontmatter must begin with a doctitle element</sch:assert>
  	</sch:rule>
  	<sch:rule context="dtbk:frontmatter/dtbk:docauthor">
  		<sch:assert test="preceding-sibling::*[self::dtbk:doctitle or self::dtbk:docauthor]">[tpb12] Docauthor may only be preceded by doctitle</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 13: All documents must have frontmatter and bodymatter -->
  <sch:pattern name="dtbook_TPB_bookParts" id="dtbook_TPB_bookParts">
  	<sch:rule context="dtbk:book">
  		<sch:assert test="dtbk:frontmatter">[tpb13] A document must have frontmatter</sch:assert>
  		<sch:assert test="dtbk:bodymatter">[tpb13] A document must have bodymatter</sch:assert>
  	</sch:rule>  	
  </sch:pattern>  
  
  <!-- Rule 18: Disallow level -->
  <sch:pattern name="dtbook_TPB_noLevel" id="dtbook_TPB_noLevel">
  	<sch:rule context="dtbk:level">
  		<sch:report test="true()">[tpb18] Element level is not allowed</sch:report>
  	</sch:rule>  	
  </sch:pattern>  
  
  <!-- Rule 20: No imggroup in inline context -->
  <sch:pattern name="dtbook_TPB_imggroupInline" id="dtbook_TPB_imggroupInline">
    <sch:rule context="dtbk:imggroup">
    	<sch:report test="ancestor::dtbk:a        or ancestor::dtbk:abbr       or ancestor::dtbk:acronym    or ancestor::dtbk:annoref   or 
                          ancestor::dtbk:bdo      or ancestor::dtbk:code       or ancestor::dtbk:dfn        or ancestor::dtbk:em        or 
                          ancestor::dtbk:kbd      or ancestor::dtbk:linenum    or ancestor::dtbk:noteref    or                                      
                          ancestor::dtbk:q        or ancestor::dtbk:samp       or ancestor::dtbk:sent       or ancestor::dtbk:span      or 
                          ancestor::dtbk:strong   or ancestor::dtbk:sub        or ancestor::dtbk:sup        or ancestor::dtbk:w         or 
                          ancestor::dtbk:address  or ancestor::dtbk:author     or ancestor::dtbk:bridgehead or ancestor::dtbk:byline    or
                          ancestor::dtbk:cite     or ancestor::dtbk:covertitle or ancestor::dtbk:dateline   or ancestor::dtbk:docauthor or
                          ancestor::dtbk:doctitle or ancestor::dtbk:dt         or ancestor::dtbk:h1         or ancestor::dtbk:h2        or
                          ancestor::dtbk:h3       or ancestor::dtbk:h4         or ancestor::dtbk:h5         or ancestor::dtbk:h6        or
                          ancestor::dtbk:hd       or ancestor::dtbk:line       or ancestor::dtbk:p">[tpb20] Image groups are not allowed in inline context</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 21: No nested tables -->
  <sch:pattern name="dtbook_TPB_nestedTables" id="dtbook_TPB_nestedTables">
  	<sch:rule context="dtbk:table">
  		<sch:report test="ancestor::dtbk:table">[tpb21] Nested tables are not allowed</sch:report>
  	</sch:rule>  	
  </sch:pattern> 
  
  <!-- Rule 22: Disallow cite -->
  <sch:pattern name="dtbook_TPB_noCite" id="dtbook_TPB_noCite">
  	<sch:rule context="dtbk:cite">
  		<sch:report test="true()">[tpb22] Element cite is not allowed</sch:report>
  	</sch:rule>  	
  </sch:pattern> 
  
  <!-- Rule 23: Increasing pagenum[@page='normal'] values -->
  <sch:pattern name="dtbook_TPB_pagenumIncrease" id="dtbook_TPB_pagenumIncrease">
  	<sch:rule context="dtbk:pagenum[@page='normal' and preceding::dtbk:pagenum[@page='normal']]">
  		<sch:assert test="number(current()) > number(preceding::dtbk:pagenum[@page='normal'][1])">[tpb23] pagenum[@page='normal'] values must increase</sch:assert>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 24: Values of pagenum[@page='front'] must be unique -->
  <sch:pattern name="dtbook_TPB_pagenumUnique" id="dtbook_TPB_pagenumUnique">
  	<sch:rule context="dtbk:pagenum[@page='front']">
  		<sch:assert test="count(key('pageFrontValues', .))=1">[tpb24] pagenum[@page='front'] values must be unique</sch:assert>
  		<!--<sch:assert test="count(//dtbk:pagenum[@page='front' and string(.)=string(current())])=1">[tpb24] pagenum[@page='front'] values must be unique</sch:assert>-->
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 26: Each note must have a noteref -->
  <sch:pattern name="dtbook_TPB_noteNoteref" id="dtbook_TPB_noteNoteref">
  	<sch:rule context="dtbk:note">
  		<sch:assert test="count(key('noterefs', @id))>=1">[tpb26] Each note must have at least one noteref</sch:assert>
  		<!--<sch:assert test="count(//dtbk:noteref[translate(@idref, '#', '')=current()/@id])>=1">[tpb26] Each note must have at least one noteref</sch:assert>-->
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 27: Each annotation must have an annoref -->
  <sch:pattern name="dtbook_TPB_annotationAnnoref" id="dtbook_TPB_annotationAnnoref">
  	<sch:rule context="dtbk:annotation">
  		<sch:assert test="count(key('annorefs', @id))>=1">[tpb27] Each annotation must have at least one annoref</sch:assert>
  		<!--<sch:assert test="count(//dtbk:annoref[translate(@idref, '#', '')=current()/@id])>=1">[tpb27] Each annotation must have at least one annoref</sch:assert>-->
  	</sch:rule>  	
  </sch:pattern>  
  
  <!-- Rule 29: No block elements in inline context -->
  <sch:pattern id="dtbook_noBlockInInline" name="dtbook_noBlockInInline">
  	<sch:rule context="dtbk:*[self::dtbk:address    or self::dtbk:annotation or self::dtbk:author   or 
  	                          self::dtbk:blockquote or self::dtbk:bridgehead or self::dtbk:caption  or 
  	                          self::dtbk:dateline   or self::dtbk:div        or self::dtbk:dl       or
  	                          self::dtbk:docauthor  or self::dtbk:doctitle   or
  	                          self::dtbk:epigraph   or self::dtbk:hd         or self::dtbk:line     or 
  	                          self::dtbk:linegroup  or
  	                          self::dtbk:list       or self::dtbk:note       or self::dtbk:p        or 
  	                          self::dtbk:poem       or self::dtbk:sidebar    or self::dtbk:table    or 
  	                          self::dtbk:title]">
  	  <sch:report test="ancestor::dtbk:a      or ancestor::dtbk:abbr or ancestor::dtbk:acronym or ancestor::dtbk:annoref or 
  	                    ancestor::dtbk:bdo    or ancestor::dtbk:code or ancestor::dtbk:dfn     or ancestor::dtbk:em      or 
  	                    ancestor::dtbk:kbd or ancestor::dtbk:linenum or ancestor::dtbk:noteref or 
  	                    ancestor::dtbk:q      or ancestor::dtbk:samp or ancestor::dtbk:sent    or ancestor::dtbk:span    or 
  	                    ancestor::dtbk:strong or ancestor::dtbk:sub  or ancestor::dtbk:sup     or ancestor::dtbk:w"
  	  >[tpb29] Block element <sch:name/> used in inline context</sch:report>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 29: No block elements in inline context - continued -->
  <sch:pattern id="dtbook_noBlockSiblingWithInline" name="dtbook_noBlockSiblingWithInline">
  	<sch:rule context="dtbk:*[self::dtbk:address    or self::dtbk:annotation or self::dtbk:author   or 
  	                          self::dtbk:blockquote or self::dtbk:bridgehead or self::dtbk:caption  or 
  	                          self::dtbk:dateline   or self::dtbk:div        or self::dtbk:dl       or
  	                          self::dtbk:epigraph   or self::dtbk:hd         or self::dtbk:linegoup or 
  	                          self::dtbk:list       or self::dtbk:note       or self::dtbk:p        or 
  	                          self::dtbk:poem       or self::dtbk:sidebar    or self::dtbk:table    or 
  	                          self::dtbk:title      or self::dtbk:level      or self::dtbk:level1   or
  	                          self::dtbk:level2     or self::dtbk:level3     or self::dtbk:level4    or
  	                          self::dtbk:level5     or self::dtbk:level6]">
  	  <sch:report test="following-sibling::dtbk:a      or following-sibling::dtbk:abbr or following-sibling::dtbk:acronym or following-sibling::dtbk:annoref or 
  	                    following-sibling::dtbk:bdo    or following-sibling::dtbk:code or following-sibling::dtbk:dfn     or following-sibling::dtbk:em      or 
  	                    following-sibling::dtbk:kbd or following-sibling::dtbk:linenum or following-sibling::dtbk:noteref or 
  	                    following-sibling::dtbk:q      or following-sibling::dtbk:samp or following-sibling::dtbk:sent    or following-sibling::dtbk:span    or 
  	                    following-sibling::dtbk:strong or following-sibling::dtbk:sub  or following-sibling::dtbk:sup     or following-sibling::dtbk:w       or
  	                    normalize-space(following-sibling::text())!=''">Block element as sibling to inline element</sch:report>
  	  <sch:report test="preceding-sibling::dtbk:a      or preceding-sibling::dtbk:abbr or preceding-sibling::dtbk:acronym or preceding-sibling::dtbk:annoref or 
  	                    preceding-sibling::dtbk:bdo    or preceding-sibling::dtbk:code or preceding-sibling::dtbk:dfn     or preceding-sibling::dtbk:em      or 
  	                    preceding-sibling::dtbk:kbd or preceding-sibling::dtbk:linenum or preceding-sibling::dtbk:noteref or 
  	                    preceding-sibling::dtbk:q      or preceding-sibling::dtbk:samp or preceding-sibling::dtbk:sent    or preceding-sibling::dtbk:span    or 
  	                    preceding-sibling::dtbk:strong or preceding-sibling::dtbk:sub  or preceding-sibling::dtbk:sup     or preceding-sibling::dtbk:w       or
  	                    normalize-space(preceding-sibling::text())!=''">[tpb29] Block element <sch:name/> as sibling to inline element</sch:report>                  
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 29: No block elements in inline context - continued -->
  <sch:pattern id="dtbook_prodnoteImggroupCheck" name="dtbook_prodnoteImggroupCheck">
    <sch:rule context="dtbk:prodnote[ancestor::dtbk:a        or ancestor::dtbk:abbr       or ancestor::dtbk:acronym    or ancestor::dtbk:annoref   or 
                                     ancestor::dtbk:bdo      or ancestor::dtbk:code       or ancestor::dtbk:dfn        or ancestor::dtbk:em        or 
                                     ancestor::dtbk:kbd      or ancestor::dtbk:linenum    or ancestor::dtbk:noteref    or                                      
                                     ancestor::dtbk:q        or ancestor::dtbk:samp       or ancestor::dtbk:sent       or ancestor::dtbk:span      or 
                                     ancestor::dtbk:strong   or ancestor::dtbk:sub        or ancestor::dtbk:sup        or ancestor::dtbk:w         or 
                                     ancestor::dtbk:address  or ancestor::dtbk:author     or ancestor::dtbk:bridgehead or ancestor::dtbk:byline    or
                                     ancestor::dtbk:cite     or ancestor::dtbk:covertitle or ancestor::dtbk:dateline   or ancestor::dtbk:docauthor or
                                     ancestor::dtbk:doctitle or ancestor::dtbk:dt         or ancestor::dtbk:h1         or ancestor::dtbk:h2        or
                                     ancestor::dtbk:h3       or ancestor::dtbk:h4         or ancestor::dtbk:h5         or ancestor::dtbk:h6        or
                                     ancestor::dtbk:hd       or ancestor::dtbk:line       or ancestor::dtbk:p]">
      <sch:report test="descendant::dtbk:*[self::dtbk:address    or self::dtbk:annotation or self::dtbk:author   or 
  	                                       self::dtbk:blockquote or self::dtbk:bridgehead or self::dtbk:caption  or 
                                           self::dtbk:dateline   or self::dtbk:div        or self::dtbk:dl       or
                                           self::dtbk:docauthor  or self::dtbk:doctitle   or
                                           self::dtbk:epigraph   or self::dtbk:hd         or self::dtbk:line     or 
  	                                       self::dtbk:linegroup  or
                                           self::dtbk:list       or self::dtbk:note       or self::dtbk:p        or 
                                           self::dtbk:poem       or self::dtbk:sidebar    or self::dtbk:table    or 
                                           self::dtbk:title]">[tpb29] Prodnote in inline context used as block element</sch:report>
    </sch:rule>
    
    <!--
    <sch:rule context="dtbk:imggroup[ancestor::dtbk:a        or ancestor::dtbk:abbr       or ancestor::dtbk:acronym    or ancestor::dtbk:annoref   or 
                                       ancestor::dtbk:bdo      or ancestor::dtbk:code       or ancestor::dtbk:dfn        or ancestor::dtbk:em        or 
                                       ancestor::dtbk:kbd      or ancestor::dtbk:linenum    or ancestor::dtbk:noteref    or                                      
                                       ancestor::dtbk:q        or ancestor::dtbk:samp       or ancestor::dtbk:sent       or ancestor::dtbk:span      or 
                                       ancestor::dtbk:strong   or ancestor::dtbk:sub        or ancestor::dtbk:sup        or ancestor::dtbk:w         or 
                                       ancestor::dtbk:address  or ancestor::dtbk:author     or ancestor::dtbk:bridgehead or ancestor::dtbk:byline    or
                                       ancestor::dtbk:cite     or ancestor::dtbk:covertitle or ancestor::dtbk:dateline   or ancestor::dtbk:docauthor or
                                       ancestor::dtbk:doctitle or ancestor::dtbk:dt         or ancestor::dtbk:h1         or ancestor::dtbk:h2        or
                                       ancestor::dtbk:h3       or ancestor::dtbk:h4         or ancestor::dtbk:h5         or ancestor::dtbk:h6        or
                                       ancestor::dtbk:hd       or ancestor::dtbk:line       or ancestor::dtbk:p]">
      <sch:report test="descendant::dtbk:*[self::dtbk:address    or self::dtbk:annotation or self::dtbk:author   or 
  	                                       self::dtbk:blockquote or self::dtbk:bridgehead or 
                                           self::dtbk:dateline   or self::dtbk:div        or self::dtbk:dl       or
                                           self::dtbk:docauthor  or self::dtbk:doctitle   or
                                           self::dtbk:epigraph   or self::dtbk:hd         or self::dtbk:line     or 
  	                                       self::dtbk:linegroup  or
                                           self::dtbk:list       or self::dtbk:note       or self::dtbk:p        or 
                                           self::dtbk:poem       or self::dtbk:sidebar    or self::dtbk:table    or 
                                           self::dtbk:title]">[tpb29] Image group in inline context used as block element</sch:report>
    </sch:rule>  
    -->
  </sch:pattern>
  
  <!-- Rule 30: paragraph cannot be sibling with div@class=pgroup -->
  <sch:pattern name="dtbook_TPB_pgroupSibling" id="dtbook_TPB_pgroupSibling">
  	<sch:rule context="dtbk:div[@class='pgroup']">
  		<sch:report test="preceding-sibling::dtbk:p">[tpb30] paragraph group (&lt;div class="pgroup"&gt;) may not be sibling with a paragraph (&lt;p&gt;)</sch:report>
  		<sch:report test="following-sibling::dtbk:p">[tpb30] paragraph group (&lt;div class="pgroup"&gt;) may not be sibling with a paragraph (&lt;p&gt;)</sch:report>
  	</sch:rule>  	
  </sch:pattern>  
  
  <sch:pattern name="dtbook_TPB_IdValue" id="dtbook_TPB_IdValue">
  	<sch:rule context="*[@id]">
  		<sch:assert test="normalize-space(@id)=@id">ID attributes may not contain whitespace</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- TPB Structure Guidelines Validation -->
  
  <!-- Rule 35: Two letter codes in @xml:lang -->
  <sch:pattern name="dtbook_TPB_twoLetterXmlLang" id="dtbook_TPB_twoLetterXmlLang">
  	<sch:rule context="dtbk:*[@xml:lang]">
  		<sch:assert test="string-length(@xml:lang)=2 or string-length(@xml:lang)=5">[tpb35] xml:lang must have two-letter language codes.</sch:assert>
  		<sch:report test="string-length(@xml:lang)=2 and translate(@xml:lang, translate(@xml:lang, 'abcdefghijklmnopqrstuvwxyz', ''), '')!=@xml:lang">[tpb35] xml:lang language must be lowercase [a-z].</sch:report>
  		<sch:report test="string-length(@xml:lang)=5 and translate(substring(@xml:lang, 1, 2), translate(substring(@xml:lang, 1, 2), 'abcdefghijklmnopqrstuvwxyz', ''), '')!=substring(@xml:lang, 1, 2)">[tpb35] xml:lang language part must be lowercase [a-z].</sch:report>
  		<sch:report test="string-length(@xml:lang)=5 and translate(substring(@xml:lang, 4, 2), translate(substring(@xml:lang, 4, 2), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''), '')!=substring(@xml:lang, 4, 2)">[tpb35] xml:lang country part must be uppercase [A-Z].</sch:report>
  		<sch:report test="string-length(@xml:lang)=5 and substring(@xml:lang, 3, 1)!='-'">[tpb35] Separator between language part and country part in xml:lang must be '-'.</sch:report>
  	</sch:rule>  	
  </sch:pattern>  
  
  <!-- Rule 36: Class attributes of level1 in frontmatter -->
  <sch:pattern name="dtbook_TPB_frontmatterLevel1Class" id="dtbook_TPB_frontmatterLevel1Class">
  	<sch:rule context="dtbk:frontmatter/dtbk:level1">
  		<sch:assert test="@class='colophon' or @class='dedication' or @class='toc' or @class='briefToc' or @class='preface' or @class='introduction' or @class='glossary' or @class='other'">[tpb36] Class attribute must be one of: colophon, dedication, toc, briefToc, preface, introduction, glossary and other</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 37: Class attributes of level1 and level2 in bodymatter -->  
  <sch:pattern name="dtbook_TPB_bodymatterLevel1Level2Class" id="dtbook_TPB_bodymatterLevel1Level2Class">
  	<sch:rule context="dtbk:bodymatter/dtbk:level1">
  		<sch:assert test="@class='part' or @class='chapter' or @class='other' or @class='introduction'">[tpb37] Class attribute must be one of: part, chapter, introduction or other</sch:assert>
  		<sch:report test="@class='part' and dtbk:level2/@class!='chapter'">[tpb37] Level2 class attribute must be 'chapter' if level1 class attribute is 'part'</sch:report>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 38: Class attributes of level1 in rearmatter -->
  <sch:pattern name="dtbook_TPB_rearmatterLevel1Class" id="dtbook_TPB_rearmatterLevel1Class">
  	<sch:rule context="dtbk:rearmatter/dtbk:level1">
  		<sch:assert test="@class='colophon' or @class='bibliography' or @class='index' or @class='footnotes' or @class='rearnotes' or @class='glossary' or @class='appendix' or @class='backCoverText' or @class='other'">[tpb38] Class attribute must be one of: bibliography, index, footnotes, rearnotes, glossary, appendix, backCoverText and other</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 39: No class attributes on level[2-6] (unless level1 in bodymatter has @class="part") -->
  <sch:pattern name="dtbook_TPB_noLevel26ClassAttrs" id="dtbook_TPB_noLevel26ClassAttrs">
    <sch:rule context="dtbk:level2">
  		<sch:report test="parent::dtbk:level1/@class!='part' and @class">[tpb39] Class attribute on level2 not allowed unless level1 class attribute is 'part'</sch:report>
  	</sch:rule>
  	<sch:rule context="dtbk:*[self::dtbk:level3 or self::dtbk:level4 or self::dtbk:level5 or self::dtbk:level6]">
  		<sch:assert test="not(@class)">[tpb39] No class attributes are allowed on level3 to level6</sch:assert>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 40: No page numbering gaps -->
  <sch:pattern name="dtbook_TPB_pagenumNoGap" id="dtbook_TPB_pagenumNoGap">
  	<sch:rule context="dtbk:pagenum[@page='normal']">
  		<sch:report test="preceding::dtbk:pagenum[@page='normal'] and number(preceding::dtbk:pagenum[@page='normal'][1]) != number(.)-1">[tpb40] No gaps may occur in page numbering</sch:report>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 41 & 42: pagenum id attribute -->
  <sch:pattern name="dtbook_TPB_idPagenum" id="dtbook_TPB_idPagenum">
  	<sch:rule context="dtbk:pagenum">
  		<!-- 41 -->
  		<sch:assert test="starts-with(@id, 'page-') or starts-with(@id, 'unnum-')">[tpb41] pagenum must have ID on the form page-[number] or unnum-[number].</sch:assert>
  		<sch:report test="starts-with(@id, 'page-') and substring(@id, 6)!=.">[tpb41] ID must be on the form page-[number]</sch:report>
  		<!-- 42 -->
  		<sch:report test="starts-with(@id, 'unnum-') and @page!='special'">[tpb42] Unnumbered pages nust be of type @page="special".</sch:report>
  		<sch:report test="starts-with(@id, 'unnum-') and lang('sv') and .!='Onumrerad sida'">[tpb42] Value of unnumbered pages must be 'Onumrerad sida' in swedish context.</sch:report>
  		<sch:report test="starts-with(@id, 'unnum-') and lang('en') and .!='Unnumbered page'">[tpb42] Value of unnumbered pages must be 'Unnumbered page' in english context.</sch:report>
  	</sch:rule>
  </sch:pattern>
  
  <!-- Rule 43: dc:Publisher must be 'TPB' -->
  <sch:pattern name="dtbook_TPB_metaDcublisher" id="dtbook_TPB_metaDcublisher">
    <sch:rule context="dtbk:head">      
      <!-- dc:Publisher -->
      <sch:assert test="count(dtbk:meta[@name='dc:Publisher' and @content='TPB'])=1">[tpb43] Meta dc:Publisher must exist and have value 'TPB'</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 44: Two letter codes in dc:Language -->
  <sch:pattern name="dtbook_TPB_twoLetterDcLanguage" id="dtbook_TPB_twoLetterDcLanguage">
  	<sch:rule context="dtbk:head">
  		<sch:assert test="count(dtbk:meta[@name='dc:Language'])>=1">[tpb44] Meta dc:Language must exist</sch:assert>
  	</sch:rule>
  	<sch:rule context="dtbk:head/dtbk:meta[@name='dc:Language']">
  		<sch:assert test="string-length(@content)=2 or string-length(@content)=5">[tpb44] Meta dc:Language must have two-letter language code.</sch:assert>
  		<sch:report test="string-length(@content)=2 and translate(@content, translate(@content, 'abcdefghijklmnopqrstuvwxyz', ''), '')!=@content">[tpb44] Meta dc:Language language must be lowercase [a-z].</sch:report>
  		<sch:report test="string-length(@content)=5 and translate(substring(@content, 1, 2), translate(substring(@content, 1, 2), 'abcdefghijklmnopqrstuvwxyz', ''), '')!=substring(@content, 1, 2)">[tpb44] Meta dc:Language language part must be lowercase [a-z].</sch:report>
  		<sch:report test="string-length(@content)=5 and translate(substring(@content, 4, 2), translate(substring(@content, 4, 2), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''), '')!=substring(@content, 4, 2)">[tpb44] Meta dc:Language country part must be uppercase [A-Z].</sch:report>
  		<sch:report test="string-length(@content)=5 and substring(@content, 3, 1)!='-'">[tpb44] Separator between language part and country part in meta dc:Language must be '-'.</sch:report>
  	</sch:rule>  	
  </sch:pattern>
  
  <!-- Rule 45: noteref and note must have the same class attribute -->
  <sch:pattern name="dtbook_TPB_noterefNoteClass" id="noterefNoteClass">
    <sch:rule context="dtbk:noteref">
    	<!-- Support both IDREF and URI specification of @idref -->
    	<sch:assert test="@class=key('notes', translate(current()/@idref, '#', ''))/@class">[tpb45] note and noteref must have the same class attribute</sch:assert>
    	<!--<sch:report test="@class!=//dtbk:note[@id=translate(current()/@idref, '#', '')]/@class">[tpb45] note and noteref must have the same class attribute</sch:report>-->
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 46: Allowed class attribute values of note -->
  <sch:pattern name="dtbook_TPB_allowedNoteClass" id="dtbook_TPB_allowedNoteClass">
    <sch:rule context="dtbk:note">
    	<sch:assert test="@class='endnote' or @class='rearnote'">[tpb46] class attribute for note must be 'endnote' or 'rearnote'</sch:assert>
    	<sch:report test="@class='rearnote' and (not(ancestor::dtbk:level1[@class='footnotes']) and not(ancestor::dtbk:level1[@class='rearnotes']))">[tpb46] Rearnotes must be in level1@class='rearnotes' in rearmatter</sch:report>
    	<sch:report test="@class='endnote' and ancestor::dtbk:rearmatter and ancestor::dtbk:level1[@class='rearnotes']">[tpb46] Endnotes may not be in level1@class='rearnotes' in rearmatter</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 48: Headings in notes section in rearmatter -->
  <sch:pattern name="dtbook_TPB_headingsInNotesSection" id="dtbook_TPB_headingsInNotesSection">
    <sch:rule context="dtbk:rearmatter/dtbk:level1[@class='footnotes']/dtbk:level2/dtbk:h2">    
    	<sch:assert test="count(/dtbk:dtbook/dtbk:book/dtbk:*/dtbk:level1/dtbk:level2[@class='chapter']/dtbk:h2[.=current()]) + 
    	                  count(/dtbk:dtbook/dtbk:book/dtbk:*/dtbk:level1[@class='chapter']/dtbk:h1[.=current()]) +
    	                  count(/dtbk:dtbook/dtbk:book/dtbk:*/dtbk:level1[@class='introduction']/dtbk:h1[.=current()]) +
    	                  count(/dtbk:dtbook/dtbk:book/dtbk:*/dtbk:level1[@class='other']/dtbk:h1[.=current()]) >= 1"
    	   >[tpb48] Heading in notes section does not exist in the bodymatter of the book</sch:assert>
    </sch:rule>    
    <sch:rule context="dtbk:rearmatter/dtbk:level1[@class='footnotes']/dtbk:level2/dtbk:note">    
          <sch:assert test="count(//dtbk:level2[@class='chapter' and descendant::dtbk:noteref[translate(@idref,'#','')=current()/@id] and dtbk:h2=current()/parent::dtbk:level2/dtbk:h2]) +
                            count(//dtbk:level1[(@class='chapter' or @class='introduction' or @class='other') and descendant::dtbk:noteref[translate(@idref,'#','')=current()/@id] and dtbk:h1=current()/parent::dtbk:level2/dtbk:h2]) >= 1"
           >[tpb48] There is no note reference to this note in the corresponding section in the bodymatter</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 49: Sidebar must have @render="optional" -->
  <sch:pattern name="dtbook_TPB_renderSidebar" id="dtbook_TPB_renderSidebar">
    <sch:rule context="dtbk:sidebar">
    	<sch:assert test="@render='optional'">[tpb49] sidebar must have a 'render' attribute equal to 'optional'</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 50: imgage alt attribute -->
  <sch:pattern name="dtbook_TPB_imgAlt" id="dtbook_TPB_imgAlt">
    <sch:rule context="dtbk:img">
    	<sch:report test="lang('sv') and @alt!='illustration'">[tpb50] an image in swedish language context must have attribute alt="illustration"</sch:report>
    	<sch:report test="lang('en') and @alt!='image'">[tpb50] an image in english language context must have attribute alt="image"</sch:report>    	
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 51 & 52: -->
  <sch:pattern name="dtbook_TPB_imgNames" id="dtbook_TPB_imgNames">
    <sch:rule context="dtbk:img">
    	<sch:assert test="contains(@src,'.jpg') and substring-after(@src,'.jpg')=''">[tpb52] Images must have the .jpg file extension.</sch:assert>
    	<sch:report test="contains(@src,'.jpg') and string-length(@src)=4">[tpb52] Images must have a base name, not just an extension.</sch:report>
    	<sch:report test="contains(@src,'/')">[tpb51] Images must be in the same folder as the DTBook file.</sch:report>
    	<sch:assert test="string-length(translate(substring(@src,1,string-length(@src)-4),'-_abcdefghijklmnopqrstuvwxyz0123456789',''))=0">[tpb52] Image file name contains an illegal character (must be -_a-z0-9).</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 53: caption in imggoup -->
  <sch:pattern name="dtbook_TPB_captionInImggroup" id="dtbook_TPB_captionInImggroup">
    <sch:rule context="dtbk:imggroup/dtbk:caption">
    	<sch:assert test="preceding-sibling::*[1][self::dtbk:img]">[tpb53] caption must immediately follow an img element</sch:assert>
    </sch:rule>
  </sch:pattern>
   
  <!-- Rule 55: no prodnotes as direct children of list -->
  <sch:pattern name="dtbook_TPB_prodnoteInList" id="dtbook_TPB_prodnoteInList">
    <sch:rule context="dtbk:list">
    	<sch:assert test="not(dtbk:prodnote)">[tpb55] prodnotes are not allowed in lists</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 57: table of contents -->
  <sch:pattern name="dtbook_TPB_listIntoc" id="dtbook_TPB_listIntoc">
    <sch:rule context="dtbk:frontmatter/dtbk:level1[@class='toc']">
    	<sch:assert test="dtbk:list">[tpb57] table of contents must have a list</sch:assert>
    </sch:rule>      
  </sch:pattern>
  
  <!-- Rule 59: No pagegenum between a term and a definition in definition lists -->
  <sch:pattern name="dtbook_TPB_noDtPagenumDD" id="dtbook_TPB_noDtPagenumDD">
    <sch:rule context="dtbk:dl/dtbk:pagenum">
    	<sch:assert test="preceding-sibling::*[1][self::dtbk:dd] and following-sibling::*[1][self::dtbk:dt]">[tpb59] pagenum in definition list must occur between dd and dt</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 60: pagenum only in first cell of a row in a table -->
  <sch:pattern name="dtbook_TPB_pagenumInTable" id="dtbook_TPB_pagenumInTable">
    <sch:rule context="dtbk:*[self::dtbk:th or self::dtbk:td]">
    	<sch:report test="descendant::dtbk:pagenum and count(preceding-sibling::*[self::dtbk:th or self::dtbk:td])!=0">[tpb60] pagenum element must be in the first cell of a table row</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 61: Heading for the notes section -->
  <sch:pattern name="dtbook_TPB_h1NotesSection" id="dtbook_TPB_h1NotesSection">
    <sch:rule context="dtbk:rearmatter/dtbk:level1[@class='rearnotes' or @class='footnotes']">
    	<sch:report test="lang('sv') and h1!='Noter'">[tpb61] Heading of notes section must be 'Noter' (swedish)</sch:report>
    	<sch:report test="lang('en') and h1!='Notes'">[tpb61] Heading of notes section must be 'Notes' (english)</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 62: Terms and definitions in definition lists must come in pairs -->
  <sch:pattern name="dtbook_TPB_dtDdPairs" id="dtbook_TPB_dtDdPairs">
    <sch:rule context="dtbk:dl/dtbk:dt">
      <sch:assert test="following-sibling::*[1][self::dtbk:dd]">[tpb62] Data terms and data definitions come in pairs</sch:assert>
    </sch:rule>
    <sch:rule context="dtbk:dl/dtbk:dd">
      <sch:assert test="preceding-sibling::*[1][self::dtbk:dt]">[tpb62] Data terms and data definitions come in pairs</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 65: No border attribute on tables -->
  <sch:pattern name="dtbook_TPB_noTableBorder" id="dtbook_TPB_noTableBorder">
    <sch:rule context="dtbk:table">
    	<sch:report test="@border">[tpb65] Border attributes on tables is not allowed</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 67: doctitle and docauthor only allowed in frontmatter -->
  <sch:pattern name="dtbook_TPB_titleAuthorInFront" id="dtbook_TPB_titleAuthorInFront">
    <sch:rule context="dtbk:doctitle">
    	<sch:assert test="parent::dtbk:frontmatter">[tpb67] doctitle is only allowed in frontmatter</sch:assert>
    </sch:rule>
    <sch:rule context="dtbk:docauthor">
    	<sch:assert test="parent::dtbk:frontmatter">[tpb67] docauthor is only allowed in frontmatter</sch:assert>
    </sch:rule>    
  </sch:pattern>
  
  <!-- Rule 68: No smilref attributes -->
  <sch:pattern name="dtbook_TPB_noSmilref" id="dtbook_TPB_noSmilref">
    <sch:rule context="dtbk:*/@smilref">
    	<sch:assert test="false()">[tpb68] smilref attributes in a plain DTBook file is not allowed</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 72: Only allow DTBook 2005-2 -->
  <sch:pattern name="dtbook_TPB_dtbookVersion" id="dtbook_TPB_dtbookVersion">
    <sch:rule context="dtbk:dtbook">
    	<sch:assert test="@version='2005-2'">[tpb72] DTBook version must be 2005-2.</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 93: Some elements may not start of end with whitespace -->
  <sch:pattern name="dtbook_TPB_trimmedWhitespace" id="dtbook_TPB_trimmedWhitespace">
    <sch:rule context="dtbk:*[self::dtbk:h1 or self::dtbk:h2 or self::dtbk:h3 or self::dtbk:h4 or self::dtbk:h5 or self::dtbk:h6 or self::dtbk:hd or self::dtbk:lic]">
    	<sch:report test="normalize-space(substring(.,1,1))=''">[tpb93] element <sch:name/> may not have leading whitespace</sch:report>
    	<sch:report test="normalize-space(substring(.,string-length(.),1))=''">[tpb93] element <sch:name/> may not have trailing whitespace</sch:report>
    </sch:rule>
  </sch:pattern>  
  
  <!-- Rule 96: no nested prodnotes or image groups -->
  <sch:pattern name="dtbook_TPB_nestedProdnoteImggroup" id="dtbook_TPB_nestedProdnoteImggroup">
    <sch:rule context="dtbk:prodnote">
    	<sch:report test="ancestor::dtbk:prodnote">[tpb96] nested production notes are not allowed</sch:report>
    </sch:rule>
    <sch:rule context="dtbk:imggroup">
    	<sch:report test="ancestor::dtbk:imggroup">[tpb96] nested image groups are not allowed</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 99: dc:Identifier and dtb:uid must have the same value -->
  <sch:pattern name="dtbook_TPB_IdentifierUid" id="dtbook_TPB_IdentifierUid">
    <sch:rule context="dtbk:head">
    	<sch:report test="dtbk:meta[@name='dc:Identifier'] and dtbk:meta[@name='dc:Identifier']/@content!=dtbk:meta[@name='dtb:uid']/@content">[tpb99] dc:Identifier must (if present) have the same value as dtb:uid</sch:report>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 101: All imggroup elements must have a img element -->
  <sch:pattern name="dtbook_TPB_imgInImggroup" id="dtbook_TPB_imgInImggroup">
    <sch:rule context="dtbk:imggroup">
    	<sch:assert test="dtbk:img">[tpb101] There must be an img element in every imggroup</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 103: No img without imggroup -->
  <sch:pattern name="dtbook_TPB_imgWithoutImggroup" id="dtbook_TPB_imgWithoutImggroup">
    <sch:rule context="dtbk:img">
    	<sch:assert test="parent::dtbk:imggroup">[tpb103] There must be an imggroup element wrapping every img</sch:assert>
    </sch:rule>
  </sch:pattern>
  
  <!-- Rule 105: Page attribute must appear on all pagenum elements -->
  <sch:pattern name="dtbook_TPB_pagenumPage" id="dtbook_TPB_pagenumPage">
  	<sch:rule context="dtbk:pagenum">
  		<sch:assert test="@page">[tpb105] Page attribute must appear on pagenum elements</sch:assert>
  	</sch:rule>  	
  </sch:pattern>  
  
  <!-- Rule 106: All documents must have at least one pagenum[@page='normal'] -->
  <sch:pattern name="dtbook_TPB_pagenumNormal" id="dtbook_TPB_pagenumNormal">
  	<sch:rule context="dtbk:book">
  		<sch:assert test="count(//dtbk:pagenum[@page='normal'])>=1">[tpb106] All documents must contain normal page numbers (&lt;pagenum page="normal" ...&gt;)</sch:assert>
  	</sch:rule>  	
  </sch:pattern>  
      
</sch:schema>

