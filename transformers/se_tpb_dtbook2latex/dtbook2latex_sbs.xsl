<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		xmlns:my="http://my-functions"
		extension-element-prefixes="my"
		exclude-result-prefixes="dtb my">
  
  <xsl:import href="dtbook2latex_common.xsl"/>
  <xsl:import href="dtbook2latex_nordic.xsl"/>

  <xsl:variable name="language-map">
    <entry key="0">null</entry>
    <entry key="1">einem</entry>
    <entry key="2">zwei</entry>
    <entry key="3">drei</entry>
    <entry key="4">vier</entry>
    <entry key="5">fünf</entry>
    <entry key="6">sechs</entry>
    <entry key="7">sieben</entry>
    <entry key="8">acht</entry>
    <entry key="9">neun</entry>
    <entry key="10">zehn</entry>
    <entry key="11">elf</entry>
    <entry key="12">zwölf</entry>
    <entry key="13">dreizehn</entry>
    <entry key="14">vierzehn</entry>
    <entry key="15">fünfzehn</entry>
    <entry key="16">sechzehn</entry>
    <entry key="17">siebzehn</entry>
    <entry key="18">achtzehn</entry>
    <entry key="19">neunzehn</entry>
    <entry key="20">zwanzig</entry>
    <entry key="21">einundzwanzig</entry>
    <entry key="22">zweiundzwanzig</entry>
    <entry key="23">dreiundzwanzig</entry>
    <entry key="24">vierundzwanzig</entry>
    <entry key="25">fünfundzwanzig</entry>
    <entry key="26">sechsundzwanzig</entry>
    <entry key="27">siebenundzwanzig</entry>
    <entry key="28">achtundzwanzig</entry>
    <entry key="29">neunundzwanzig</entry>
    <entry key="30">dreissig</entry>
  </xsl:variable>

  <xsl:template name="current_volume_string">
    <xsl:param name="current_volume_number"/>
    <xsl:value-of select="concat('Band ', $current_volume_number, ' von ', $number_of_volumes, '\\[0.5cm]&#10;')"/>
  </xsl:template>

  <xsl:template name="total_volumes_string">
    <xsl:variable name="volumes-string" select="if ($number_of_volumes = 1) then 'Band' else 'Bänden'"/>
    <xsl:value-of select="concat('Grossdruck in ', $language-map/entry[@key=$number_of_volumes], ' ', $volumes-string, '\\[0.5cm]&#10;')"/>
  </xsl:template>

   <xsl:template name="author">
     <xsl:param name="font_size" select="'\large'"/>
     <xsl:value-of select="concat('{', $font_size, ' ')"/>
     <xsl:variable name="author">
       <xsl:apply-templates select="//dtb:docauthor" mode="cover"/>
     </xsl:variable>
     <xsl:sequence select="if (normalize-space($author) != '') then $author else '\ '"/>
     <xsl:text>}\\[1.5cm]&#10;</xsl:text>
   </xsl:template>

   <xsl:template name="title">
     <xsl:param name="font_size" select="'\huge'"/>
     <xsl:text>\begin{Spacing}{1.75}&#10;</xsl:text>
     <xsl:value-of select="concat('{', $font_size, ' ')"/>
     <xsl:apply-templates select="//dtb:doctitle" mode="cover"/>
     <xsl:text>}\\[0.5cm]&#10;</xsl:text>
     <xsl:text>\end{Spacing}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:docauthor" mode="cover">
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:doctitle" mode="cover">
     <xsl:apply-templates/>
   </xsl:template>

  <xsl:template name="publisher">
    <xsl:text>SBS Schweizerische Bibliothek </xsl:text>
    <xsl:if test="$fontsize = '17pt'"><xsl:text>\\&#10;</xsl:text></xsl:if>
    <xsl:text>für Blinde, Seh- und Lesebehinderte\\[0.5cm]&#10;</xsl:text>
  </xsl:template>

  <xsl:template name="imprint">
    <xsl:text>\clearpage&#10;</xsl:text>
    <xsl:text>Dieses Grossdruckbuch ist die ausschliesslich für die Nutzung durch seh- und lesebehinderte Menschen bestimmte zugängliche Version eines urheberrechtlich geschützten Werks. Sie können es im Rahmen des Urheberrechts persönlich nutzen, dürfen es aber nicht weiter verbreiten oder öffentlich zugänglich machen.&#10;&#10;</xsl:text>
    <xsl:if test="//dtb:meta[lower-case(@name)='prod:source']/@content = 'electronicData'">
      <xsl:value-of select="if ($fontsize = '20pt') then '\vfill&#10;' else '\bigskip&#10;'"/>
      <xsl:text>Wir danken dem Verlag für die freundliche Bereitstellung der elektronischen Textdaten.&#10;&#10;</xsl:text>
      <xsl:if test="$fontsize = '25pt'">\clearpage&#10;<xsl:text></xsl:text></xsl:if>
    </xsl:if>
    <xsl:text>\vfill&#10;</xsl:text>
    <xsl:text>Verlag, Satz und Druck:</xsl:text>
    <xsl:value-of select="if ($fontsize = '25pt') then '\\&#10;' else '\\[0.5cm]&#10;'"/>
    <xsl:text>SBS Schweizerische Bibliothek </xsl:text>
    <xsl:if test="$fontsize = '17pt'"><xsl:text>\\&#10;</xsl:text></xsl:if>
    <xsl:text>für Blinde, Seh- und Lesebehinderte, Zürich</xsl:text>
    <xsl:value-of select="if ($fontsize = '25pt') then '\\&#10;' else '\\[0.5cm]&#10;'"/>
    <xsl:text>www.sbs.ch</xsl:text>
    <xsl:value-of select="if ($fontsize = '25pt') then '\\&#10;' else '\\[0.5cm]&#10;'"/>
    <xsl:variable name="year" select="tokenize(//dtb:meta[lower-case(@name)='dc:date']/@content, '-')[1]"/>
    <xsl:value-of select="concat('SBS ', $year, '&#10;')"/>
  </xsl:template>

  <!-- Contrary to the default front matter we would like to rearrange
       some of the front matter chapters, hence we overwrite the
       default template and invoke apply-templates for levels with
       class titlepage. -->
   <xsl:template match="dtb:frontmatter">
     <xsl:call-template name="set_frontmatter_pagestyle"/>
     <xsl:text>\frontmatter&#10;</xsl:text>
     <xsl:call-template name="cover"/>
     <xsl:apply-templates select="dtb:level1[@class='titlepage']"/>
     <xsl:if test="dtb:*[not(@class='titlepage' or @class='toc')]">
       <xsl:text>\clearpage&#10;</xsl:text>
       <xsl:apply-templates select="dtb:*[not(@class='titlepage' or @class='toc')]"/>
     </xsl:if>
     <xsl:text>\cleartorecto&#10;</xsl:text>
     <xsl:if test="dtb:level1/dtb:list[descendant::dtb:lic]">
       <xsl:text>\tableofcontents*&#10;</xsl:text>
     </xsl:if>
   </xsl:template>

   <xsl:template name="volumecover">
     <xsl:text>\cleartorecto&#10;</xsl:text>
     <xsl:text>\savepagenumber&#10;</xsl:text>
     <xsl:call-template name="set_frontmatter_pagestyle"/>
     <xsl:call-template name="cover">
       <xsl:with-param name="current_volume_number" 
		       select="count(preceding::dtb:div[@class='volume-split-point'])+2"/>
     </xsl:call-template>
     <xsl:apply-templates select="//dtb:level1[@class='titlepage']"/>
     <xsl:if test="//dtb:frontmatter//dtb:level1/dtb:list[descendant::dtb:lic]">
       <xsl:text>\pagestyle{empty}&#10;</xsl:text>
       <xsl:text>\cleartorecto&#10;</xsl:text>
       <xsl:text>\tableofcontents*&#10;</xsl:text>
     </xsl:if>
     <xsl:text>\cleartorecto&#10;</xsl:text>
     <xsl:call-template name="restore_pagestyle"/>
     <xsl:text>\restorepagenumber&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h1">
   	<xsl:text>\chapter*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h2">
   	<xsl:text>\section*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h3">
   	<xsl:text>\subsection*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h4">
   	<xsl:text>\subsubsection*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h5">
   	<xsl:text>\paragraph*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <xsl:template match="dtb:frontmatter//dtb:h6">
   	<xsl:text>\subparagraph*{</xsl:text>
   	<xsl:apply-templates/>
   	<xsl:text>}&#10;</xsl:text>   
   </xsl:template>

   <!-- Do not fake an empty chapter if the only children are level2 -->
   <xsl:template match="dtb:frontmatter/dtb:level1[not(child::*[not(self::dtb:level2)])]">
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level1[@class='titlepage']" priority="100">
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level1[@class='titlepage']/dtb:level2[1]">
     <xsl:text>\cleartorecto&#10;</xsl:text>

     <xsl:call-template name="author">
       <xsl:with-param name="font_size" select="'\normalsize'"/>
     </xsl:call-template>
     <xsl:call-template name="title">
       <xsl:with-param name="font_size" select="'\Large'"/>
     </xsl:call-template>
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:level1[@class='titlepage']/dtb:level2[1]/dtb:p[contains(@class,'sourcePublisher')]">
     <xsl:text>\vfill&#10;</xsl:text>
     <xsl:apply-templates/>
     <xsl:text>&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:level1[@class='titlepage']/dtb:level2[2]">
     <xsl:text>\clearpage&#10;</xsl:text>
     <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dtb:p[contains(@class,'precedingemptyline')]">   
     <xsl:text>\plainbreak{1.5}&#10;&#10;</xsl:text>
     <xsl:apply-templates/>
     <xsl:text>&#10;&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:p[contains(@class,'precedingseparator')]">   
     <xsl:text>\fancybreak{***}&#10;&#10;</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>&#10;&#10;</xsl:text>
   </xsl:template>

   <!-- The span@class='linenum' is a "proprietary extension" of SBS if you will
        that allows to markup books where you have very long passages of numbered
        lines, i.e. basically spanning the whole book. Linegroup and line is not
        suited for this as you can no longer markup paragraphs, blockquotes or
        anything else for that matter. -->
   <xsl:template match="dtb:span[@class='linenum']">   
     <xsl:variable name="num">
       <xsl:apply-templates/>
     </xsl:variable>
     <xsl:if test="my:has-preceding-non-empty-textnode-within-block(.)">
       <xsl:text>\\&#10;</xsl:text>
     </xsl:if>
     <xsl:value-of select="concat('\sidepar[',$num,']{',$num,'}')"/>
   </xsl:template>

   <xsl:template match="text()[preceding-sibling::*[1][self::dtb:span[@class='linenum']]]">
     <!-- trim whitespace after the linenum span -->
     <xsl:value-of select="my:quoteSpecialChars(replace(string(current()), '^\s+', ''))"/>
   </xsl:template>

</xsl:stylesheet>
