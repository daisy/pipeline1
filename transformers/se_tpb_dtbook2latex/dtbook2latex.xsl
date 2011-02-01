<?xml version="1.0" encoding="utf-8"?>

<!-- 
bilder
sidebar
prodnote
annoref
table
språk
hyperlänkar
-->

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		xmlns:my="http://my-functions"
		extension-element-prefixes="my"
		exclude-result-prefixes="dtb my">
  
  <xsl:output method="text" encoding="utf-8" indent="no"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="code samp"/>
	
  <!-- Possible values are 12pt, 14pt, 17pt and 20pt -->
  <xsl:param name="fontsize">17pt</xsl:param>
  <!-- Possible values are for example 'Tiresias LPfont', 'LMRoman10
  Regular', 'LMSans10 Regular' or 'LMTypewriter10 Regular'. Basically
  any installed TrueType or OpenType font -->
  <xsl:param name="font">LMRoman10 Regular</xsl:param>
  <xsl:param name="defaultLanguage">english</xsl:param>
  <xsl:param name="papersize">a4paper</xsl:param>
  <!-- Possible values are 'left', 'justified' -->
  <xsl:param name="alignment">justified</xsl:param>
  <!-- Possible values are 'plain', 'withPageNums' and 'scientific' 
       - 'plain' contains no original page numbers, no section numbering
         and uses the 'plain' pagestyle.
       - 'withPageNums' is similar to 'plain' but enables the display of the
         original page numbers.
       - 'scientific' has original page numbers, has section numbering
         and uses the normal latex page style for the document class book.
    -->
  <xsl:param name="pageStyle">plain</xsl:param>
  <xsl:param name="line_spacing">singlespacing</xsl:param>

  <!-- Fill the following values if you pass a 'custom' papersize -->
  <xsl:param name="paperwidth">200mm</xsl:param>
  <xsl:param name="paperheight">250mm</xsl:param>
  <xsl:param name="left_margin">28mm</xsl:param>
  <xsl:param name="right_margin">20mm</xsl:param>
  <xsl:param name="top_margin">20mm</xsl:param>
  <xsl:param name="bottom_margin">20mm</xsl:param>

  <xsl:function name="my:quoteSpecialChars" as="xs:string">
    <xsl:param name="text"/>
    <xsl:value-of select="replace(replace($text, '\s+', ' '), '(\$|&amp;|%|#|_|\{|\}|\\)', '\\$1')"/>
  </xsl:function>

   <xsl:template match="/">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:dtbook">
	<xsl:text>% ***********************&#10;</xsl:text>
   	<xsl:text>% DMFC dtbook2latex v0.2&#10;</xsl:text>
	<xsl:text>% ***********************&#10;</xsl:text>
   	<xsl:text>\documentclass[</xsl:text>
	<xsl:value-of select="concat($fontsize, ',')"/>
	<xsl:if test="$papersize != 'custom'">
	  <xsl:value-of select="concat($papersize, ',')"/>
	</xsl:if>
	<xsl:text>twoside]{extbook}&#10;</xsl:text>
	<xsl:if test="$papersize = 'custom'">
	  <xsl:value-of select="concat('\usepackage[paperwidth=', $paperwidth, 
				',paperheight=', $paperheight, ',left=', $left_margin,
				',right=', $right_margin,',top=', $top_margin,
				',bottom=', $bottom_margin,']{geometry}}&#10;')"/>
	</xsl:if>
   	<xsl:text>\usepackage{graphicx}&#10;</xsl:text>
   	<xsl:call-template name="findLanguage"/>
   	<xsl:text>\setlength{\parskip}{1.5ex}&#10;</xsl:text>
   	<xsl:text>\setlength{\parindent}{0ex}&#10;</xsl:text>
	<xsl:text>\usepackage{fontspec,xunicode,xltxtra}&#10;</xsl:text>
	<xsl:text>\defaultfontfeatures{Mapping=tex-text}&#10;</xsl:text>
	<xsl:text>\setmainfont{</xsl:text><xsl:value-of select="$font"/><xsl:text>}&#10;</xsl:text>
	<xsl:text>\usepackage{hyperref}&#10;</xsl:text>
	<xsl:text>\usepackage{float}&#10;</xsl:text>
	<xsl:text>\usepackage{alphalph}&#10;&#10;</xsl:text>
	<xsl:text>%% avoid overfull \hbox (which is a serious problem with large fonts)&#10;</xsl:text>
	<xsl:text>\sloppy&#10;&#10;</xsl:text>
	<xsl:if test="$pageStyle!='scientific'">
	  <xsl:text>\usepackage{titlesec}&#10;&#10;</xsl:text>
	  <xsl:text>\titlelabel{}&#10;</xsl:text>
	  <xsl:text>\titleformat{\chapter}[block]{}{}{0cm}{\Large\bfseries}&#10;&#10;</xsl:text>

	  <!-- Drop the numbering from the TOC -->
	  <xsl:text>\usepackage{titletoc}&#10;</xsl:text>
	  <xsl:text>\titlecontents{part}[1.5em]{\addvspace{1em}}{}{}{,~\thecontentspage}&#10;</xsl:text>
	  <xsl:text>\titlecontents{chapter}[1.5em]{\addvspace{1em}}{}{}{,~\thecontentspage}&#10;</xsl:text>
	  <xsl:text>\titlecontents{section}[1.5em]{}{}{}{,~\thecontentspage}&#10;</xsl:text>
	  <xsl:text>\titlecontents{subsection}[1.5em]{}{}{}{,~\thecontentspage}&#10;</xsl:text>
	  <xsl:text>\titlecontents{subsubsection}[1.5em]{}{}{}{,~\thecontentspage}&#10;&#10;</xsl:text>
	</xsl:if>

	<!-- Redefine the second enumerate level so it can handle more than 26 items -->
	<xsl:text>\renewcommand{\theenumii}{\AlphAlph{\value{enumii}}}&#10;</xsl:text>
	<xsl:text>\renewcommand{\labelenumii}{\theenumii}&#10;&#10;</xsl:text>
	<xsl:if test="$line_spacing != 'singlespacing'">
	  <xsl:text>\usepackage{setspace}&#10;</xsl:text>
	  <xsl:value-of select="concat('\', $line_spacing, '&#10;&#10;')"/>
	</xsl:if>
	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template name="iso639toBabel">
     <!-- Could probably also use lookup tables here as explained in
     http://www.ibm.com/developerworks/library/x-xsltip.html and
     http://www.ibm.com/developerworks/xml/library/x-tiplook.html -->
     <xsl:param name="iso639Code"/>
     <xsl:variable name="babelLang">
       <xsl:choose>
   	 <xsl:when test="matches($iso639Code, 'sv(-.+)?')">swedish</xsl:when>
   	 <xsl:when test="matches($iso639Code, 'en-[Uu][Ss]')">USenglish</xsl:when>
   	 <xsl:when test="matches($iso639Code, 'en-[Uu][Kk]')">UKenglish</xsl:when>
   	 <xsl:when test="matches($iso639Code, 'en(-.+)?')">english</xsl:when>
   	 <xsl:when test="matches($iso639Code, 'de(-.+)?')">ngerman</xsl:when>
	 <xsl:otherwise>
	   <xsl:message>
	     ***** <xsl:value-of select="$iso639Code"/> not supported. Defaulting to '<xsl:value-of select="$defaultLanguage"/>' ******
	   </xsl:message>
	   <xsl:value-of select="$defaultLanguage"/></xsl:otherwise>
       </xsl:choose>
     </xsl:variable>
     <xsl:value-of select="$babelLang"/>
   </xsl:template>

   <xsl:template name="findLanguage">
     <xsl:variable name="iso639Code">
       <xsl:choose>
	 <xsl:when test="//dtb:meta[@name='dc:Language']">
	   <xsl:value-of select="//dtb:meta[@name='dc:Language']/@content"/>
	 </xsl:when>
	 <xsl:when test="//dtb:meta[@name='dc:language']">
	   <xsl:value-of select="//dtb:meta[@name='dc:language']/@content"/>
	 </xsl:when>
	 <xsl:when test="/dtb:dtbook/@xml:lang">
	   <xsl:value-of select="/dtb:dtbook/@xml:lang"/>
	 </xsl:when>   			
       </xsl:choose>
     </xsl:variable>
     <xsl:text>\usepackage[</xsl:text>
     <xsl:call-template name="iso639toBabel">
       <xsl:with-param name="iso639Code">
	 <xsl:value-of select="$iso639Code"/>
       </xsl:with-param>
     </xsl:call-template>
     <xsl:text>]{babel}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:head">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:meta">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:book">
	<xsl:text>\begin{document}&#10;</xsl:text>
	<xsl:if test="$pageStyle='plain' or $pageStyle='withPageNums'">
	  <xsl:text>\pagestyle{plain}&#10;</xsl:text>
	</xsl:if>
	<xsl:if test="$alignment='left'">
	  <xsl:text>\raggedright&#10;</xsl:text>
	</xsl:if>
	<xsl:apply-templates/>
	<xsl:text>\end{document}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:frontmatter">
   	<xsl:text>\frontmatter&#10;</xsl:text>
   	<xsl:apply-templates select="//dtb:meta" mode="titlePage"/>
	<xsl:text>\maketitle&#10;</xsl:text>
	<xsl:if test="dtb:level1/dtb:list[descendant::dtb:lic]">
		<xsl:text>\tableofcontents&#10;</xsl:text>
	</xsl:if>
	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:frontmatter/dtb:level1/dtb:list[descendant::dtb:lic]" priority="1">
   	<xsl:message>skip!</xsl:message>
   </xsl:template>

   <xsl:template match="dtb:meta[@name='dc:title' or @name='dc:Title']" mode="titlePage">
     <xsl:text>\title{</xsl:text>
     <xsl:value-of select="my:quoteSpecialChars(string(@content))"/>
     <xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:meta[@name='dc:creator' or @name='dc:Creator']" mode="titlePage">
     <xsl:text>\author{</xsl:text>
     <xsl:value-of select="my:quoteSpecialChars(string(@content))"/>
     <xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:meta[@name='dc:date' or @name='dc:Date']" mode="titlePage">
     <xsl:text>\date{</xsl:text>
     <xsl:value-of select="my:quoteSpecialChars(string(@content))"/>
     <xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:level1">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level2">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level3">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level4">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level5">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level6">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:doctitle">
   </xsl:template>
   
   <xsl:template match="dtb:docauthor">
   </xsl:template>
   
   <xsl:template match="dtb:covertitle">
   </xsl:template>

   <xsl:template match="dtb:p">   
	<xsl:apply-templates/>
	<xsl:text>&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:pagenum">
     <xsl:if test="$pageStyle!='plain'">
       <xsl:text>\marginpar{</xsl:text>
       <xsl:apply-templates/>
       <xsl:text>}&#10;</xsl:text>
     </xsl:if>
   </xsl:template>

   <xsl:template match="dtb:address">
  	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:h1">
   	<xsl:text>\chapter[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:h2">
   	<xsl:text>\section[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:h3">
   	<xsl:text>\subsection[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:h4">
   	<xsl:text>\subsubsection[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:h5">
   	<xsl:text>\paragraph[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:h6">
   	<xsl:text>\subparagraph[</xsl:text>
	<xsl:value-of select="text()"/>
	<xsl:text>]{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

  <xsl:template match="dtb:bridgehead">
  	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:list[not(@type)]">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:lic">
   	<xsl:apply-templates/>
   	<xsl:if test="following-sibling::dtb:lic or normalize-space(following-sibling::text())!=''">
	   	<xsl:text>\dotfill </xsl:text>
   	</xsl:if>
   </xsl:template>

   <xsl:template match="dtb:br">
   	<xsl:text>\\*&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:bodymatter">
   <xsl:text>\mainmatter&#10;</xsl:text>
	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:noteref">
   	<xsl:text>\footnote{</xsl:text>
   	<xsl:variable name="refText">
	  <xsl:value-of select="normalize-space(//*[@id=translate(current()/@idref,'#','')])"/>
   	</xsl:variable>
	<xsl:value-of select="my:quoteSpecialChars(string($refText))"/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:img">
   	<!--<xsl:apply-templates/>-->
   	<!--<xsl:text>\begin{picture}(5,2)&#10;</xsl:text>
   	<xsl:text>\setlength{\fboxsep}{0.25cm}&#10;</xsl:text>
   	<xsl:text>\put(0,0){\framebox(5,2){}}&#10;</xsl:text>
   	<xsl:text>\put(1,1){\fbox{Missing image}}&#10;</xsl:text>
   	<xsl:text>\end{picture}&#10;</xsl:text>
   	-->
   	<xsl:text>\includegraphics{</xsl:text>
   	<xsl:value-of select="@src"/>
   	<xsl:text>}&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:h1/dtb:img|dtb:h2/dtb:img|dtb:h3/dtb:img|dtb:h4/dtb:img|dtb:h5/dtb:img|dtb:h6/dtb:img">
   	<xsl:text>\includegraphics{</xsl:text>
   	<xsl:value-of select="@src"/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:caption">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:imggroup/dtb:caption">
   	<!--<xsl:apply-templates/>-->
   </xsl:template>
   
   <xsl:template match="dtb:table/dtb:caption">
   	<!--<xsl:apply-templates/>-->
   </xsl:template>
   
   <xsl:template match="dtb:caption" mode="captionOnly">
   	<!--<xsl:text>\caption{</xsl:text>-->
   	<xsl:apply-templates mode="textOnly"/>
   	<xsl:text>&#10;</xsl:text>
   	<!--<xsl:text>}&#10;</xsl:text>-->
   </xsl:template>

   <xsl:template match="dtb:div">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:imggroup">
   	<!--
   	<xsl:text>\fbox{\fbox{\parbox{10cm}{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}}}</xsl:text>
   	-->
   	<xsl:text>\begin{figure}[H]&#10;</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:apply-templates select="dtb:caption" mode="captionOnly"/>
   	<xsl:text>\end{figure}&#10;</xsl:text>   	
   </xsl:template>

   <xsl:template match="dtb:annotation">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:author">	
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:blockquote">
   	<xsl:text>\begin{quote}&#10;</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>\end{quote}&#10;</xsl:text>
   </xsl:template>

  <xsl:template match="dtb:byline">
  	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:dateline">
  	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:epigraph">
  	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:note">
   	<!--<xsl:apply-templates/>-->
   </xsl:template>

   <xsl:template match="dtb:sidebar">
   	<xsl:text>\fbox{\parbox{10cm}{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}}&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:hd">
     <xsl:variable name="level">
       <xsl:value-of select="count(ancestor::dtb:level)"/>
     </xsl:variable>
     <xsl:choose>
       <xsl:when test="$level=1"><xsl:text>\chapter</xsl:text></xsl:when>
       <xsl:when test="$level=2"><xsl:text>\section</xsl:text></xsl:when>
       <xsl:when test="$level=3"><xsl:text>\subsection</xsl:text></xsl:when>
       <xsl:when test="$level=4"><xsl:text>\subsubsection</xsl:text></xsl:when>
       <xsl:when test="$level=5"><xsl:text>\paragraph</xsl:text></xsl:when>
       <xsl:when test="$level>5"><xsl:text>\subparagraph</xsl:text></xsl:when>
     </xsl:choose>
     <xsl:text>[</xsl:text>
     <xsl:value-of select="text()"/>
     <xsl:text>]{</xsl:text>
     <xsl:apply-templates/>
     <xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:sidebar/dtb:hd">
   	<xsl:text>\textbf{</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>}&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:list/dtb:hd">
   	<xsl:text>\item \textbf{</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:list[@type='ol']">
   	<xsl:text>\begin{enumerate}&#10;</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>\end{enumerate}&#10;</xsl:text>
   </xsl:template>
   
   <xsl:template match="dtb:list[@type='ul']">
   	<xsl:text>\begin{itemize}&#10;</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>\end{itemize}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:list[@type='pl']">
   	<xsl:text>\begin{trivlist}&#10;</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>\end{trivlist}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:li">
   	<xsl:text>\item </xsl:text>
	<xsl:apply-templates/>
	<xsl:text>&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:dl">
   	<xsl:text>\begin{description}</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>\end{description}</xsl:text>
   </xsl:template>

  <xsl:template match="dtb:dt">
  	<xsl:text>\item[</xsl:text>
  	<xsl:apply-templates/>
  	<xsl:text>] </xsl:text>
   </xsl:template>

  <xsl:template match="dtb:dd">
  	<xsl:apply-templates/>
  	<xsl:text>&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:table">
   	<xsl:text>\begin{table}[H]</xsl:text>
   	<xsl:apply-templates select="dtb:caption" mode="captionOnly"/>
   	<xsl:text>\begin{tabular}{</xsl:text>
   	<xsl:variable name="numcols">
   		<xsl:value-of select="count(descendant::dtb:tr[1]/*[self::dtb:td or self::dtb:th])"/>
   	</xsl:variable>
   	<xsl:for-each select="descendant::dtb:tr[1]/*[self::dtb:td or self::dtb:th]">
   		<xsl:text>|p{</xsl:text>
   		<xsl:value-of select="10 div $numcols"/>
   		<xsl:text>cm}</xsl:text>
   	</xsl:for-each>
   	<xsl:text>|} \hline&#10;</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>\end{tabular}&#10;</xsl:text>
   	<xsl:text>\end{table}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:tbody">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:thead">
   	<xsl:apply-templates/>   
   </xsl:template>

   <xsl:template match="dtb:tfoot">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:tr">
   	<xsl:apply-templates/>
   	<xsl:text>\\ \hline&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:th">
   	<xsl:if test="preceding-sibling::dtb:th">
   		<xsl:text> &amp; </xsl:text>
   	</xsl:if>
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:td">
   	<xsl:if test="preceding-sibling::dtb:td">
   		<xsl:text> &amp; </xsl:text>
   	</xsl:if>
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:colgroup">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:col">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:poem">
   	<xsl:text>\begin{verse}&#10;</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>\end{verse}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:poem/dtb:title">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:cite/dtb:title">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:cite">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:code">
   	<xsl:text>\texttt{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:kbd">
   	<xsl:text>\texttt{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:q">
   	<xsl:text>\textsl{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:samp">
   	<xsl:text>\texttt{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:linegroup">   	
   	<xsl:apply-templates/>
	<xsl:text>&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:line">
   	<xsl:apply-templates/>
	<xsl:text>\\&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:linenum">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:prodnote">
   	<xsl:text>\marginpar{\framebox[5mm]{!}}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:rearmatter">
   	<xsl:text>\backmatter&#10;</xsl:text>
	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:a">
   	<xsl:apply-templates/>
   </xsl:template>

	<xsl:template match="dtb:em">
		<xsl:text>\emph{</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>}</xsl:text>		
   	</xsl:template>

   <xsl:template match="dtb:strong">
   	<xsl:text>\textbf{</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>}</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:abbr">
   	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:acronym">
   	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:bdo">
   	<xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="dtb:dfn">
   	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:sent">
   	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:w">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:sup">
 	<xsl:text>$^{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}$</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:sub">
   	<xsl:text>$_{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}$</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:span">
     <!-- FIXME: What to do with span? It basically depends on the class -->
     <!-- attribute which can be used for anything (colour, typo, error, etc) -->
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:a[@href]">
   	<xsl:apply-templates/>
   </xsl:template>

  <xsl:template match="dtb:annoref">
   	<xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text()">
     <xsl:value-of select="my:quoteSpecialChars(string(current()))"/>
   </xsl:template>
   	
   <xsl:template match="text()" mode="textOnly">
     <xsl:value-of select="my:quoteSpecialChars(string(current()))"/>
   </xsl:template>
   
   <xsl:template match="dtb:*">
     <xsl:message>
  *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
   </xsl:message>
   </xsl:template>

</xsl:stylesheet>
