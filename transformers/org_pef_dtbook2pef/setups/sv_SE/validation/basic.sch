<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

	<sch:title>DTBook 2005 Schematron tests</sch:title>
	<sch:ns prefix="dtb" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
	<!-- Allow everything in dtbook (head, book) -->
	<!-- Allow everything in head and descendants -->
	<!-- Allow everything in book (frontmatter?, bodymatter?, rearmatter?) -->

	<!-- Rule 1a: Allow in frontmatter (doctitle, docauthor, level1) -->
	<sch:pattern name="dtbook_allow_in_frontmatter" id="dtbook_allow_in_frontmatter">
		<sch:rule context="dtb:frontmatter">
			<sch:assert test="count(dtb:level1|dtb:doctitle|dtb:docauthor)=count(dtb:*)">[Rule 1a] Unallowed element in "<sch:name/>".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 1b: Allow in bodymatter, rearmatter (level1) -->
	<sch:pattern name="dtbook_allow_in_body_and_rearmatter" id="dtbook_allow_in_body_and_rearmatter">
		<sch:rule context="dtb:bodymatter|dtb:rearmatter">
			<sch:assert test="count(dtb:level1)=count(dtb:*)">[Rule 1b] Unallowed element in "<sch:name/>".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 2: Allow in level1 (h1 | level2 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level1" id="dtbook_allow_in_level1">
		<sch:rule context="dtb:level1">
			<sch:assert test="count(dtb:h1|dtb:level2|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 2] Unallowed element in "level1".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 3: Allow in level2 (h2 | level3 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level2" id="dtbook_allow_in_level2">
		<sch:rule context="dtb:level2">
			<sch:assert test="count(dtb:h2|dtb:level3|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 3] Unallowed element in "level2".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 4: Allow in level3 (h3 | level4 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level3" id="dtbook_allow_in_level3">
		<sch:rule context="dtb:level3">
			<sch:assert test="count(dtb:h3|dtb:level4|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 4] Unallowed element in "level3".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 5: Allow in level4 (h4 | level5 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author  | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level4" id="dtbook_allow_in_level4">
		<sch:rule context="dtb:level4">
			<sch:assert test="count(dtb:h4|dtb:level5|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 5] Unallowed element in "level4".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 6: Allow in level5 (h5 | level6 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level5" id="dtbook_allow_in_level5">
		<sch:rule context="dtb:level5">
			<sch:assert test="count(dtb:h5|dtb:level6|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 6] Unallowed element in "level5".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 7: Allow in level6 (h6 | p | list | dl | div | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_level6" id="dtbook_allow_in_level6">
		<sch:rule context="dtb:level6">
			<sch:assert test="count(dtb:h6|dtb:p|dtb:list|dtb:dl|dtb:div|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 7] Unallowed element in "level6".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 8: Allow in div (p | list | dl | blockquote | imggroup | poem | pagenum | sidebar | author | linegroup -->	
	<sch:pattern name="dtbook_allow_in_div" id="dtbook_allow_in_div">
		<sch:rule context="dtb:div">
			<sch:assert test="count(dtb:p|dtb:list|dtb:dl|dtb:blockquote|dtb:imggroup|dtb:poem|dtb:pagenum|dtb:sidebar|dtb:author|dtb:linegroup)=count(dtb:*)">[Rule 8] Unallowed element in "div".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 9: Allow in p (pagenum | em | strong | sub | sup | br | span) -->
	<sch:pattern name="dtbook_allow_in_p" id="dtbook_allow_in_p">
		<sch:rule context="dtb:p">
			<sch:assert test="count(dtb:pagenum|dtb:em|dtb:strong|dtb:sub|dtb:sup|dtb:br|dtb:span)=count(dtb:*)">[Rule 9] Unallowed element in "p".</sch:assert>
		</sch:rule>
	</sch:pattern>
	
	<!-- Rule 10a: Allow in em (pagenum | strong | sub | sup | br | span) -->
	<sch:pattern name="dtbook_allow_in_em" id="dtbook_allow_in_em">
		<sch:rule context="dtb:em">
			<sch:assert test="count(dtb:pagenum|dtb:strong|dtb:sub|dtb:sup|dtb:br|dtb:span)=count(dtb:*)">[Rule 10a] Unallowed element in "em".</sch:assert>
		</sch:rule>
	</sch:pattern>

	<!-- Rule 10b: Allow in strong (pagenum | em | sub | sup | br | span) -->
	<sch:pattern name="dtbook_allow_in_strong" id="dtbook_allow_in_strong">
		<sch:rule context="dtb:strong">
			<sch:assert test="count(dtb:pagenum|dtb:em|dtb:sub|dtb:sup|dtb:br|dtb:span)=count(dtb:*)">[Rule 10b] Unallowed element in "strong".</sch:assert>
		</sch:rule>
	</sch:pattern>

</sch:schema>