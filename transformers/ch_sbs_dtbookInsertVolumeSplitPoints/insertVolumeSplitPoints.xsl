<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		exclude-result-prefixes="dtb">
  
<!-- This stylesheet inserts split points in a dtbook so that a book
     can be split into several volumes later on, e.g. by the latex
     transformer. The splitting is done as follows: First determine
     the number of paragraphs per volume, i.e. total number of
     paragraphs divided by number_of_volumes. Then simply calculate
     the split point based on that. Now if any of the split points are
     near a level1 move the split point to just before this level1, so
     that the new level will end up in the new volume. The
     allowed_stretch defines how much a volume can be streched so that
     volume splits occur at nearby level1. -->

  <xsl:output method="xml" encoding="utf-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="code samp"/>
	
  <xsl:param name="number_of_volumes" select="1"/>
  <xsl:param name="allowed_stretch" select="0.1"/>

  <xsl:variable name="all_p" select="//dtb:p"/>
  <xsl:variable name="p_per_volume" select="ceiling(count($all_p) div number($number_of_volumes)) + 1"/>
  <xsl:variable name="split_nodes" select="$all_p[position() mod $p_per_volume = 0]"/>

  <!-- Calculate all the poor split points, i.e the ones close to a level1 -->
  <xsl:param name="allowed_stretch_in_nodes" select="ceiling($p_per_volume * number($allowed_stretch))"/>
  <xsl:variable name="p_right_after_level1" 
		select="//dtb:level1/descendant::dtb:p[position() le $allowed_stretch_in_nodes]"/>
  <xsl:variable name="p_right_before_level1" 
		select="//dtb:level1/descendant::dtb:p[(last() - position()) lt $allowed_stretch_in_nodes]"/>
  <xsl:variable name="poor_choices" 
		select="$p_right_after_level1 union $p_right_before_level1"/>
  <xsl:variable name="good_split_nodes" select="$split_nodes except $poor_choices"/>

  <!-- if any of the split nodes are in the set of poor choices
       replace this node with the level1 node where we want to break -->
  <xsl:variable name="replaced_after_split_nodes"
  		select="for $x in ($split_nodes intersect $p_right_after_level1) return $x/ancestor::dtb:level1"/>
  <xsl:variable name="replaced_before_split_nodes"
  		select="for $x in ($split_nodes intersect ($p_right_before_level1 except $p_right_after_level1)) return $x/following::dtb:level1[1]"/>
  <xsl:variable name="replaced_split_nodes" 
		select="$replaced_after_split_nodes union $replaced_before_split_nodes"/>
  <xsl:variable name="valid_split_nodes" 
		select="$good_split_nodes union $replaced_split_nodes"/>

  <xsl:template match="dtb:level1">
    <xsl:if test="some $node in $valid_split_nodes satisfies current() is $node">
      <xsl:element name="div" namespace="http://www.daisy.org/z3986/2005/dtbook/">
	<xsl:attribute name="class">volume-split-point</xsl:attribute>
	<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/"/>
      </xsl:element>
    </xsl:if>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="dtb:p">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    <xsl:if test="some $node in $valid_split_nodes satisfies current() is $node">
      <xsl:element name="div" namespace="http://www.daisy.org/z3986/2005/dtbook/">
	<xsl:attribute name="class">volume-split-point</xsl:attribute>
	<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/"/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!-- Copy all other elements and attributes -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
