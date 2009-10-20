<?xml version="1.0" encoding="utf-8"?>
<!-- Rules generated on: 2009-10-14 11:55:29 -->
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">
	<sch:title>DTBook 2005-3 Schematron basic tests</sch:title>
	<sch:ns prefix="dtb" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
	<!-- Rule 1: Disallowed element: note -->
	<sch:pattern name="no_note" id="no_note">
		<sch:rule context="dtb:note">
			<sch:assert test="false">[Rule 1] No 'note'</sch:assert>
		</sch:rule>
	</sch:pattern>
	<!-- Rule 2: Disallowed element: table -->
	<sch:pattern name="no_table" id="no_table">
		<sch:rule context="dtb:table">
			<sch:assert test="false">[Rule 2] No 'table'</sch:assert>
		</sch:rule>
	</sch:pattern>
</sch:schema>
