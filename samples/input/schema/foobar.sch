<?xml version="1.0" encoding="utf-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">

  <sch:title>A dummy schematron schema for the DTBook namespace</sch:title>

  <sch:ns prefix="dtbk" uri="http://www.daisy.org/z3986/2005/dtbook/"/>
  
  <sch:pattern name="dtbook_foobar" id="dtbook_foobar">
    <sch:rule context="dtbk:head">      
      <sch:assert test="count(dtbk:meta[@name='dc:FooBar'])>=1">Meta dc:FooBar must occur at least once</sch:assert>  
    </sch:rule>
  </sch:pattern>
  
  
  
</sch:schema>