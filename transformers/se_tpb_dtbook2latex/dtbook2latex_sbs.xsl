<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		xmlns:my="http://my-functions"
		extension-element-prefixes="my"
		exclude-result-prefixes="dtb my">
  
  <xsl:import href="dtbook2latex_common.xsl"/>

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
  </xsl:variable>

  <xsl:template name="current_volume_string">
    <xsl:param name="current_volume_number"/>
    <xsl:value-of select="concat('Band ', $current_volume_number, ' von ', $number_of_volumes, '\\[0.5cm]&#10;')"/>
  </xsl:template>

  <xsl:template name="total_volumes_string">
    <xsl:variable name="volumes-string" select="if ($number_of_volumes = 1) then 'Band' else 'Bänden'"/>
    <xsl:value-of select="concat('Grossdruck in ', $language-map/entry[@key=$number_of_volumes], ' ', $volumes-string, '\\[0.5cm]&#10;')"/>
  </xsl:template>

  <xsl:template name="publisher">
    <xsl:text>SBS Schweizerische Bibliothek für Blinde, Seh- und Lesebehinderte\\[0.5cm]&#10;</xsl:text>
  </xsl:template>

  <xsl:template name="imprint">
    <xsl:text>\clearpage&#10;</xsl:text>
    <xsl:text>Verlag, Satz und Druck:\\[0.5cm]&#10;</xsl:text>
    <xsl:text>SBS Schweizerische Bibliothek für Blinde, Seh- und Lesebehinderte, Zürich\\[0.5cm]&#10;</xsl:text>
    <xsl:text>www.sbs.ch\\[0.5cm]&#10;</xsl:text>
    <xsl:variable name="year" select="tokenize(//dtb:meta[lower-case(@name)='dc:date']/@content, '-')[1]"/>
    <xsl:value-of select="concat('SBS ', $year, '\\[0.5cm]&#10;')"/>
    <xsl:text>\vfill&#10;</xsl:text>
    <xsl:text>Dieses Grossdruckbuch ist die ausschliesslich für die Nutzung durch seh- und lesebehinderte Menschen bestimmte zugängliche Version eines urheberrechtlich geschützten Werks. Sie können es im Rahmen des Urheberrechts persönlich nutzen, dürfen es aber nicht weiter verbreiten oder öffentlich zugänglich machen.&#10;</xsl:text>
  </xsl:template>

  <!-- Contrary to the default front matter we would like to rearrange
       some of the front matter chapters, hence we overwrite the
       default template and invoke apply-templates for levels with
       class titlepage. -->
   <xsl:template match="dtb:frontmatter">
     <xsl:if test="$pageStyle='plain'">
       <xsl:text>\pagestyle{empty}&#10;</xsl:text>
     </xsl:if>
     <xsl:if test="$pageStyle='withPageNums'">
       <xsl:text>\pagestyle{plain}&#10;</xsl:text>
     </xsl:if>
     <xsl:text>\frontmatter&#10;</xsl:text>
     <xsl:call-template name="cover"/>
     <xsl:apply-templates select="//dtb:meta" mode="titlePage"/>
     <xsl:if test="dtb:level1/dtb:list[descendant::dtb:lic]">
       <xsl:text>\tableofcontents&#10;</xsl:text>
     </xsl:if>
     <xsl:apply-templates select="dtb:level1[@class='titlepage']"/>
     <xsl:text>\clearpage&#10;</xsl:text>
     <xsl:apply-templates select="dtb:*[not(@class='titlepage')]"/>
     <xsl:text>\cleartorecto&#10;</xsl:text>
   </xsl:template>

   <xsl:template name="volumecover">
     <xsl:text>\cleartorecto&#10;</xsl:text>
     <xsl:text>\savepagenumber&#10;</xsl:text>
     <xsl:text>\pagestyle{empty}&#10;</xsl:text>
     <xsl:call-template name="cover">
       <xsl:with-param name="current_volume_number" 
		       select="count(preceding::dtb:div[@class='volume-split-point'])+2"/>
     </xsl:call-template>
     <xsl:apply-templates select="//dtb:level1[@class='titlepage']"/>
     <xsl:text>\cleartorecto&#10;</xsl:text>
     <xsl:text>\pagestyle{plain}&#10;</xsl:text>
     <xsl:text>\restorepagenumber&#10;</xsl:text>
   </xsl:template>

   <xsl:template match="dtb:level1[@class='titlepage']" priority="100">
     <xsl:text>\clearpage&#10;</xsl:text>
     <xsl:text>Verwendete Vorlage:\\[0.5cm]&#10;</xsl:text>
     <xsl:apply-templates/>
   </xsl:template>

</xsl:stylesheet>
