<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
		exclude-result-prefixes="dtb">

<!-- When notes/annotations are typeset as endnotes for the whole document
     (dtbook2latex's endnotes=document), dtbook2latex defers all of them and
     prints them together once, right at the end of the document, regardless of
     where in the book they were referenced. The word-count based volume
     splitter (ch_sbs_dtbookInsertVolumeSplitPoints) has no way of knowing this:
     it counts each note's words in place, wherever the note happens to sit in
     the xml, which is wrong once dtbook2latex moves that text elsewhere.

     This stylesheet fixes the mismatch at the source: when endnotes=document it
     physically moves every note/annotation to the end of the document (as the
     last children of the last level1), in their original order, so the xml word
     order matches the eventual rendering order and the volume splitter's plain
     word count becomes correct again. For endnotes=none or endnotes=chapter,
     notes are rendered where they are referenced (or at the end of their own
     chapter) so this transform leaves the document untouched. -->

  <xsl:output method="xml" encoding="utf-8" indent="no"/>

  <xsl:param name="endnotes" select="'none'"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="dtb:note|dtb:annotation">
    <xsl:if test="$endnotes != 'document'">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <!-- Add all notes to the last level1 in the whole document, wherever it is
       (bodymatter or backmatter) -->
  <xsl:template match="dtb:level1[not(following::dtb:level1)]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:if test="$endnotes = 'document'">
        <xsl:copy-of select="//dtb:note|//dtb:annotation"/>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
