<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		xmlns:func="http://my-functions"
		exclude-result-prefixes="dtb">
  
<!-- This stylesheet inserts split points in a dtbook so that a book
     can be split into several volumes later on, e.g. by the latex
     transformer. The splitting is done as follows: 
     - First determine the number of words per volume, i.e. total
       number of words divided by number_of_volumes.  
     - Then recurse through all p while checking if the words so far
       exceed the allowed limit. If yes mark this p as a split point. 
     - Now if any of the split points are near a level1 move the split
       point to just before this level1, so that the new level will
       end up in the new volume. The allowed_stretch defines how much
       a volume can be streched so that volume splits occur at nearby
       level1. -->  

  <xsl:output method="xml" encoding="utf-8" indent="no"/>

  <xsl:param name="number_of_volumes" select="1"/>
  <xsl:param name="allowed_stretch" select="0.15"/>

  <!-- Count the words in a given paragraph -->
  <xsl:function name="func:wc" as="xs:integer">
    <xsl:param name="para" as="element()"/>
    <xsl:value-of select="count(tokenize(normalize-space(string($para)), '\s+'))"/>
  </xsl:function>

  <!-- Determine paragraphs where a volume should be split, i.e.
       paragraphs where the numbers of words since the last split
       point are greater that the wanted words per volume -->
  <xsl:function name="func:splitInternal">
    <xsl:param name="wordsSoFar" as="xs:double"/>
    <xsl:param name="wordsPerVolume" as="xs:double"/>
    <xsl:param name="paragraphSequence" as="element()*"/>
    <xsl:variable name="head" select="$paragraphSequence[1]"/>
    <xsl:variable name="tail" select="$paragraphSequence[position() gt 1]"/>
    <xsl:variable name="currentWordCount" select="$wordsSoFar + func:wc($head)"/>
    <xsl:choose>
      <xsl:when test="empty($paragraphSequence)">
        <xsl:sequence select="$paragraphSequence"/>
      </xsl:when>
      <xsl:when test="$wordsSoFar >= $wordsPerVolume">
        <xsl:sequence select="$head,func:splitInternal(0, $wordsPerVolume, $tail)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="func:splitInternal($currentWordCount, $wordsPerVolume, $tail)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <xsl:function name="func:split">
    <xsl:param name="wordsPerVolume" as="xs:double"/>
    <xsl:param name="paragraphSequence" as="element()*"/>
    <xsl:sequence select="func:splitInternal(0, $wordsPerVolume, $paragraphSequence)"/>
  </xsl:function>
  
  <!-- Given a p within a level1, level2, level3, linegroup, poem, sidebar, blockquote or list,
       move to the beginning/ending of that block if it's within a certain threshold of words -->
  <xsl:function name="func:replaceWithClosestBlock" as="element()">
    <xsl:param name="split_point" as="element()"/>
    <xsl:param name="allowed_stretch_in_words" as="xs:double"/>
    <xsl:variable name="block_names" select="('level1', 'level2', 'level3', 'linegroup', 'poem', 'sidebar', 'blockquote', 'list')"/>
    <xsl:variable name="blocks" select="$split_point/ancestor::dtb:*[local-name()=$block_names]"/>
    <xsl:choose>
      <xsl:when test="exists($blocks)">
        <xsl:variable name="moveBefore"
          select="($blocks[sum(for $p in (descendant::dtb:p intersect $split_point/preceding::*) return func:wc($p)) &lt; $allowed_stretch_in_words])[1]"/>
        <xsl:variable name="moveAfter"
          select="($blocks[sum(for $p in (descendant::dtb:p intersect ($split_point,$split_point/following::*)) return func:wc($p)) &lt; $allowed_stretch_in_words])[1]"/>
        <xsl:choose>
          <xsl:when test="exists($moveBefore) and exists($moveAfter)">
            <xsl:sequence select="if (count($moveBefore/ancestor::*) le count($moveAfter/ancestor::*)) 
              then $moveBefore else $moveAfter/following::dtb:*[local-name()=($block_names,'p')][1]"/>
          </xsl:when>
          <xsl:when test="exists($moveBefore)">
            <xsl:sequence select="$moveBefore"/>
          </xsl:when>
          <xsl:when test="exists($moveAfter)">
            <xsl:sequence select="$moveAfter/following::dtb:*[local-name()=($block_names,'p')][1]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:sequence select="$split_point"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="$split_point"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <xsl:variable name="all_p" select="//dtb:p|//dtb:li|//dtb:line"/>
  <xsl:variable name="total_words" select="sum(for $p in $all_p return func:wc($p))"/>
  <xsl:variable name="words_per_volume" select="ceiling($total_words div number($number_of_volumes)) + 1"/>
  <xsl:variable name="split_nodes" select="func:split($words_per_volume, $all_p)"/>
  
  <xsl:param name="allowed_stretch_in_words" select="ceiling($words_per_volume * number($allowed_stretch))"/>
  
  <xsl:variable name="valid_split_nodes"
    select="for $split_point in $split_nodes 
    return func:replaceWithClosestBlock($split_point, $allowed_stretch_in_words)"/>

  <xsl:template match="dtb:level1|dtb:level2|dtb:level3|dtb:linegroup|dtb:poem|dtb:sidebar|dtb:blockquote|dtb:list|dtb:p">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:if test="some $node in $valid_split_nodes satisfies current() is $node">
        <xsl:attribute name="class" select="string-join((string(@class), 'volume-split-point'), ' ')"/>
      </xsl:if>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- Copy all other elements and attributes -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
