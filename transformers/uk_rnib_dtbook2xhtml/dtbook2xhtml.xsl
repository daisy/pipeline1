<?xml version="1.0" encoding="utf-8"?>
<out:stylesheet 
  xmlns:out="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:dtb="http://www.loc.gov/nls/z3986/2005/dtbook/"
  xmlns:d="rnib.org.uk/tbs#"
  xmlns="http://www.w3.org/1999/xhtml"

  version="1.0" exclude-result-prefixes="d  dtb">


<d:doc xmlns:d="rnib.org.uk/tbs#">
 <d:revhistory>
   <d:purpose><d:para>This stylesheet uses dtbook 2005 to produce XHTML</d:para></d:purpose>
   <d:revision>
    <d:revnumber>1.0</d:revnumber>
    <d:date>11 May  2005</d:date>
    <d:authorinitials>DaveP</d:authorinitials>
    <d:revdescription>
     <d:para>Initial issue</d:para>
    </d:revdescription>
    <d:revremark></d:revremark>
   </d:revision>
   <d:revision>
    <d:revnumber>1.1</d:revnumber>
    <d:date>27 May  2005</d:date>
    <d:authorinitials>DaveP</d:authorinitials>
    <d:revdescription>
     <d:para>Amended for namespace issues</d:para>
    </d:revdescription>
    <d:revremark></d:revremark>
   </d:revision>


  </d:revhistory>
  </d:doc>


  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="code samp "/>
  <xsl:variable name="rev" select="document('')//d:doc/d:revhistory/d:revision[position()=last()]/d:revnumber"/>
   <xsl:variable name="date" select="document('')//d:doc/d:revhistory/d:revision[position()=last()]/d:date"/>



   <xsl:output method="xml" encoding="utf-8" indent="yes"
     doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
     doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>


   <out:template match="/">
  
      <out:apply-templates/>
   </out:template>


   <out:template match="dtb:dtbook">
     <xsl:element name="html" namespace="http://www.w3.org/1999/xhtml">
       <xsl:comment>
         DAISY dtb 2005 -> XHTML 1.0 transform, 
         rev: <xsl:value-of select="$rev"/>
       date: <xsl:value-of select="$date"/>     
     </xsl:comment>
     <out:apply-templates/>
   </xsl:element>
   </out:template>


   <out:template match="dtb:head">
     <head>
       <title>
         <xsl:value-of select="dtb:meta[@name='dc:title']/@content"/>
       </title>

      <out:apply-templates/>
    </head>
   </out:template>


   <out:template match="dtb:meta">
     <meta>
       <xsl:copy-of select="@*"/>
     </meta>
   </out:template>


   <out:template match="dtb:book">
     <body><out:apply-templates/></body>
   </out:template>


   <out:template match="dtb:frontmatter">
     <div class="frontmatter">
       <xsl:copy-of select="@*"/>  
       <out:apply-templates/></div>
   </out:template>




   <out:template match="dtb:level1">
     <div class="level1">
       <xsl:copy-of select="@*"/>  
       <out:apply-templates/></div>
   </out:template>
   <out:template match="dtb:level2">
     <div class="level2">
       <xsl:copy-of select="@*"/>  
       <out:apply-templates/></div>
   </out:template>
   <out:template match="dtb:level3">
     <div class="level3">
       <xsl:copy-of select="@*"/>  
       <out:apply-templates/></div>
   </out:template>
   <out:template match="dtb:level4">
     <div class="level4">
       <xsl:copy-of select="@*"/>  
       <out:apply-templates/></div>
   </out:template>
   <out:template match="dtb:level5">
     <div class="level5">
       <xsl:copy-of select="@*"/>  
     <out:apply-templates/></div>
   </out:template>
   <out:template match="dtb:level6">
     <div class="level6">
       <xsl:copy-of select="@*"/>    
       <out:apply-templates/></div>
   </out:template>

   <out:template match="dtb:level">
     <div class="level">
        <xsl:copy-of select="@*"/>
       <out:apply-templates/></div>
   </out:template>





   <out:template match="dtb:p">
     <p>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/></p>
   </out:template>


   <out:template match="dtb:pagenum">
     <span class="pagenum">
     <xsl:copy-of select="@*[not(name()='page')]"/>
      <out:apply-templates/>
    </span>
   </out:template>


   <out:template match="dtb:h1">
     <h1>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h1>
   </out:template>

   <out:template match="dtb:h2">
     <h2>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h2>
   </out:template>

   <out:template match="dtb:h3">
     <h3>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h3>
   </out:template>

   <out:template match="dtb:h4">
     <h4>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h4>
   </out:template>

   <out:template match="dtb:h5">
     <h5>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h5>
   </out:template>

   <out:template match="dtb:h6">
     <h6>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </h6>
   </out:template>


  <out:template match="dtb:bridgehead">
    <div class="bridgehead"><out:apply-templates/></div>
   </out:template>
   



   <out:template match="dtb:list[not(@type)]">
     <ul><out:apply-templates/></ul>
   </out:template>


 


   <out:template match="dtb:lic">
     <span class="lic"><out:apply-templates/></span>
   </out:template>


   <out:template match="dtb:br">
     <br />  <out:apply-templates/>
   </out:template>


  


   <out:template match="dtb:bodymatter">
     <div class="bodymatter"><out:apply-templates/></div>
   </out:template>


 

   <out:template match="dtb:noteref">
     <span class="noteref">
       <a href="{idref}">
       <out:apply-templates/>
     </a>
   </span>
   </out:template>


 


   <out:template match="dtb:img">
     <img >
     <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </img>
   </out:template>


   <out:template match="dtb:caption">
     <caption>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </caption>
   </out:template>


   <out:template match="dtb:imggroup/dtb:caption">
     <p class="caption">
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </p>
   </out:template>


  


   <out:template match="dtb:div">
     <div>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </div>
   </out:template>

   <out:template match="dtb:imggroup">
     <div class="imggroup">
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </div>
   </out:template>




  <out:template match="dtb:annotation">
    <div class="annotation"><out:apply-templates/></div>
   </out:template>

  <out:template match="dtb:author">
    <div class="author"><out:apply-templates/></div>
   </out:template>


   <out:template match="dtb:blockquote">
     <blockquote>
       <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </blockquote>
   </out:template>


  <out:template match="dtb:byline">
    <div class="byline"><out:apply-templates/></div>
   </out:template>

  <out:template match="dtb:dateline">
    <div class="dateline"><out:apply-templates/></div>
   </out:template>

  <out:template match="dtb:doctitle">
    <div class="doctitle"><out:apply-templates/></div>
   </out:template>

  <out:template match="dtb:docauthor">
    <div class="docauthor"><out:apply-templates/></div>
   </out:template>

  <out:template match="dtb:epigraph">
    <div class="epigraph"><out:apply-templates/></div>
   </out:template>



   <out:template match="dtb:note">
      <div class="note">
        <xsl:copy-of select="@*"/>
        <out:apply-templates/>
      </div>
   </out:template>

   <out:template match="dtb:sidebar">
      <div class="sidebar">
        <xsl:copy-of select="@*[not(local-name()='render')]"/>
        <out:apply-templates/>
      </div>
   </out:template>

   <out:template match="dtb:hd">
      <div class="hd">
        <xsl:copy-of select="@*"/>
        <out:apply-templates/>
      </div>
   </out:template>

   <out:template match="dtb:list/dtb:hd">
      <li class="hd">
        <xsl:copy-of select="@*"/>
        <out:apply-templates/>
      </li>
   </out:template>




   <xsl:template match="dtb:list[@type='ol']">
     <ol> <xsl:copy-of select="@*[not(name()='type')]"/>
       <xsl:apply-templates/>
     </ol>
   </xsl:template>





   <xsl:template match="dtb:list[@type='ul']">
     <ul> <xsl:copy-of select="@*[not(name()='type')]"/>
       <xsl:apply-templates/>
     </ul> 
   </xsl:template>

   <xsl:template match="dtb:list[@type='pl']">
     <ul class="plain"> <xsl:copy-of select="@*[not(name()='type')]"/>
       <xsl:apply-templates/>
     </ul>
   </xsl:template>

   <xsl:template match="dtb:li">
     <li>
       <xsl:copy-of select="@*[not(name()='type')]"/>
       <xsl:apply-templates/>
     </li>
   </xsl:template>


   <xsl:template match="dtb:table">
     <table>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </table>
   </xsl:template>


   <xsl:template match="dtb:tbody">
     <tbody>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </tbody>
   </xsl:template>

  

   <xsl:template match="dtb:thead">
     <thead>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </thead>
   </xsl:template>

   <xsl:template match="dtb:tfoot">
     <tfoot>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </tfoot>
   </xsl:template>

   <xsl:template match="dtb:tr">
     <tr>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </tr>
   </xsl:template>

   <xsl:template match="dtb:th">
     <th>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </th>
   </xsl:template>

   <xsl:template match="dtb:td">
     <td>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </td>
   </xsl:template>

   <xsl:template match="dtb:colgroup">
     <colgroup>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </colgroup>
   </xsl:template>

   <xsl:template match="dtb:col">
     <col>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates/>
     </col>
   </xsl:template>

 






   <out:template match="dtb:poem">
  <div class="poem">
    <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </div>
   </out:template>

   <out:template match="dtb:cite">
     <cite>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </cite>
   </out:template>



   <out:template match="dtb:code">
     <code>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </code>
   </out:template>

   <out:template match="dtb:kbd">
     <kbd>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </kbd>
   </out:template>

   <out:template match="dtb:q">
     <q>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </q>
   </out:template>

   <out:template match="dtb:samp">
     <samp>
       <xsl:copy-of select="@*"/>
       <out:apply-templates/>
     </samp>
   </out:template>



   <out:template match="dtb:linegroup">
     <div class="linegroup">
       <xsl:copy-of select="@*"/>  
      <out:apply-templates/>
    </div>
   </out:template>


   <out:template match="dtb:line">
   <div class="line">
       <xsl:copy-of select="@*"/>  
      <out:apply-templates/>
    </div>
   </out:template>

   <out:template match="dtb:linenum">
   <span class="linenum">
       <xsl:copy-of select="@*"/>  
      <out:apply-templates/>
    </span>
   </out:template>




   <out:template match="dtb:prodnote">
     <div class="prodnote">
       <xsl:copy-of select="@*"/>  
      <out:apply-templates/>
    </div>
   </out:template>


   <out:template match="dtb:rearmatter">
     <div class="rearmatter"><out:apply-templates/></div>
   </out:template>


   <!-- Inlines -->

   <out:template match="dtb:a">
     <span class="anchor"><out:apply-templates/></span>
   </out:template>

 <out:template match="dtb:em">
   <em>
     <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </em>
   </out:template>

 <out:template match="dtb:strong">
   <strong>
     <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </strong>
   </out:template>
 <out:template match="dtb:sup">
   <sup>
     <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </sup>
   </out:template>
 <out:template match="dtb:sub">
   <sub>
     <xsl:copy-of select="@*"/>
      <out:apply-templates/>
    </sub>
   </out:template>



   <out:template match="dtb:a[@href]">
     <a>
       <xsl:copy-of select="@*[not(name()='external')]"/>
       <out:apply-templates/>
     </a>
   </out:template>

  <out:template match="dtb:annoref">
     <span class="annoref"><out:apply-templates/></span>
   </out:template>

   <xsl:template match="dtb:*">
     <xsl:message>
  *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
     </xsl:message>
   </xsl:template>


</out:stylesheet>