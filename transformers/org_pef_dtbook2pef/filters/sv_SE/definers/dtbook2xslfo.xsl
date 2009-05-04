<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="dtb">
	<xsl:param name="filter_word"/>
	<xsl:param name="baseDir"/>
	<xsl:param name="first_smil"/>
	<xsl:param name="css_path"/>
	<xsl:param name="daisy_noteref"/>
	<xsl:param name="svg_mathml"/>
	
	<xsl:variable name="char_width" select="7"/> <!-- in pts -->
	<xsl:variable name="char_height" select="12"/> <!-- in pts -->
	<xsl:variable name="page_width" select="30"/> <!-- in characters -->
	<xsl:variable name="page_height" select="29"/> <!-- in rows -->

	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	<xsl:template match="/">
		<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="folint.xsl"</xsl:processing-instruction>
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="standard" page-height="{($char_height+1)*$page_height}pt" page-width="{$char_width*$page_width+$char_width div 2}pt">
					<fo:region-body region-name="main"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates/>
		</fo:root>
	</xsl:template>
	<xsl:template name="copyCatts">
		<!-- <xsl:copy-of select="@id|@class|@title|@xml:lang"/> -->
	</xsl:template>
	<xsl:template name="copyCncatts">
		<!-- <xsl:copy-of select="@id|@title|@xml:lang"/> -->
	</xsl:template>
	<xsl:template name="inlineParent">
		<xsl:param name="class"/>
		<xsl:choose>
			<xsl:when test="ancestor::*[self::dtb:h1 or self::dtb:h2 or self::dtb:h3 or self::dtb:h4 or self::dtb:h5 or self::dtb:h6 or self::dtb:hd or self::dtb:span or self::dtb:p or self::dtb:lic]">
				<xsl:apply-templates select="." mode="inlineOnly"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- <div class="{$class}"> -->
				<xsl:call-template name="copyCncatts"/>
				<xsl:apply-templates/>
				<!-- </div> -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dtb:dtbook">
		<fo:page-sequence master-reference="standard" font-family="Courier" font-size="12pt" line-height="12pt" line-stacking-strategy="line-height" hyphenate="true">
			<fo:flow flow-name="main" width="120pt" border="1px">
				<xsl:apply-templates/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>
	<xsl:template match="dtb:head">
		<!--
     <head>
     	 <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8"/>
       <title>
         <xsl:value-of select="dtb:meta[@name='dc:Title']/@content"/>
       </title>
      <xsl:apply-templates/>
      <xsl:if test="$css_path!=''">
        <link rel="stylesheet" type="text/css">
          <xsl:attribute name="href">
            <xsl:value-of select="$css_path"/>
      	  </xsl:attribute>
        </link>
      </xsl:if>
    </head>-->
	</xsl:template>
	<xsl:template match="dtb:meta">
		<!--
     <meta>
       <xsl:if test="@name">
         <xsl:attribute name="name">
           <xsl:choose>
             <xsl:when test="@name='dtb:uid'"><xsl:value-of select="'dc:identifier'"/></xsl:when>
             <xsl:when test="@name='dc:Title'"><xsl:value-of select="'dc:title'"/></xsl:when>
             <xsl:when test="@name='dc:Subject'"><xsl:value-of select="'dc:subject'"/></xsl:when>
             <xsl:when test="@name='dc:Description'"><xsl:value-of select="'dc:description'"/></xsl:when>
             <xsl:when test="@name='dc:Type'"><xsl:value-of select="'dc:type'"/></xsl:when>
             <xsl:when test="@name='dc:Source'"><xsl:value-of select="'dc:source'"/></xsl:when>
             <xsl:when test="@name='dc:Relation'"><xsl:value-of select="'dc:relation'"/></xsl:when>
             <xsl:when test="@name='dc:Coverage'"><xsl:value-of select="'dc:coverage'"/></xsl:when>
             <xsl:when test="@name='dc:Creator'"><xsl:value-of select="'dc:creator'"/></xsl:when>
             <xsl:when test="@name='dc:Publisher'"><xsl:value-of select="'dc:publisher'"/></xsl:when>
             <xsl:when test="@name='dc:Contributor'"><xsl:value-of select="'dc:contributor'"/></xsl:when>
             <xsl:when test="@name='dc:Rights'"><xsl:value-of select="'dc:rights'"/></xsl:when>
             <xsl:when test="@name='dc:Date'"><xsl:value-of select="'dc:date'"/></xsl:when>
             <xsl:when test="@name='dc:Format'"><xsl:value-of select="'dc:format'"/></xsl:when>
             <xsl:when test="@name='dc:Identifier'"><xsl:value-of select="'dc:identifier'"/></xsl:when>
             <xsl:when test="@name='dc:Language'"><xsl:value-of select="'dc:language'"/></xsl:when>
             <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
           </xsl:choose>
         </xsl:attribute>
       </xsl:if>	
       <xsl:copy-of select="@http-equiv"/>
       <xsl:copy-of select="@content"/>
       <xsl:copy-of select="@scheme"/>       
     </meta>-->
	</xsl:template>
	<xsl:template match="dtb:link">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="dtb:book">
			<xsl:for-each select="(//dtb:doctitle)[1]">
				<fo:block>
					<xsl:value-of select="."/>
				</fo:block>
			</xsl:for-each>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:frontmatter|dtb:bodymatter|dtb:rearmatter">
		<xsl:apply-templates/>
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
	<xsl:template match="dtb:covertitle">
		<fo:block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates mode="inlineOnly"/>
		</fo:block>
	</xsl:template>
	<xsl:template match="dtb:p">
		<fo:block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates mode="inlineOnly"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template name="pagenum">
	<!--
		<span class="pagenum">
			<xsl:call-template name="copyCncatts"/>
			<xsl:choose>
				<xsl:when test="@page='front'">
					<xsl:attribute name="class">page-front</xsl:attribute>
				</xsl:when>
				<xsl:when test="@page='special'">
					<xsl:attribute name="class">page-special</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">page-normal</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates/>
		</span>
-->
	<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:pagenum">
		<xsl:choose>
			<xsl:when test="parent::dtb:level1 or parent::dtb:level2 or parent::dtb:level3">
				<fo:block>
					<xsl:apply-templates/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	<xsl:template match="dtb:list/dtb:pagenum" priority="1">
		<xsl:param name="inlineFix"/>
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::*) or $inlineFix='true'">
				<fo:list-item>
					<fo:list-item-label><fo:block></fo:block></fo:list-item-label>
					 <fo:list-item-body>
						<xsl:call-template name="pagenum"/>
					</fo:list-item-body>
				</fo:list-item>
			</xsl:when>
			<xsl:otherwise>
				<!--<xsl:message>Skipping pagenum element <xsl:value-of select="@id"/></xsl:message>-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="dtb:list/dtb:pagenum" mode="pagenumInLi">
		<xsl:call-template name="pagenum"/>
		<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
	</xsl:template>
	
	<xsl:template match="dtb:list/dtb:prodnote">
		<fo:list-item>
			<fo:list-item-label><fo:block></fo:block></fo:list-item-label>
			 <fo:list-item-body>
			<xsl:apply-templates/>
		</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	
	<xsl:template match="dtb:address">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6">
		<fo:block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="dtb:h1">
		<fo:block break-before="odd-page">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<xsl:template match="dtb:bridgehead">
		<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:list[not(@type)]">
		<fo:list-block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</fo:list-block>
	</xsl:template>
	
	<xsl:template match="dtb:lic">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:br">
		<xsl:text>#x0a;</xsl:text>
	</xsl:template>
	
	<xsl:template match="dtb:noteref">
	<!--
		<xsl:choose>
			<xsl:when test="$daisy_noteref='true'">
				<span class="noteref">
					<xsl:call-template name="copyCncatts"/>
					<xsl:attribute name="bodyref"><xsl:if test="not(contains(@idref,'#'))"><xsl:text>#</xsl:text></xsl:if><xsl:value-of select="@idref"/></xsl:attribute>
					<xsl:apply-templates/>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<a class="noteref">
					<xsl:call-template name="copyCncatts"/>
					<xsl:attribute name="href"><xsl:choose><xsl:when test="@smilref"><xsl:value-of select="@smilref"/></xsl:when><xsl:otherwise><xsl:text>#</xsl:text><xsl:value-of select="translate(@idref, '#', '')"/></xsl:otherwise></xsl:choose></xsl:attribute>
					<xsl:apply-templates/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
-->
	<xsl:apply-templates/>
	<!-- TODO: Fix noteref -->
	</xsl:template>
	
	<xsl:template match="dtb:img">
	<!--
		<img>
			<xsl:call-template name="copyCatts"/>
			<xsl:copy-of select="@src|@alt|@longdesc|@height|@width"/>
		</img>-->
	</xsl:template>
	
	<xsl:template match="dtb:caption">
		<fo:block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates mode="inlineOnly"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:imggroup/dtb:caption">
		<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:div">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:imggroup">
		<xsl:call-template name="inlineParent">
			<xsl:with-param name="class" select="'imggroup'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:prodnote">
		<xsl:call-template name="inlineParent">
			<xsl:with-param name="class" select="'optional-prodnote'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dtb:annotation">
		<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:author">
		<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:byline">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dateline">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:doctitle">
			<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</fo:block>
	</xsl:template>

	<xsl:template match="dtb:docauthor">
			<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</fo:block>
	</xsl:template>

	<xsl:template match="dtb:epigraph">
		<fo:block>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<!-- block elements or pagenum -->
	<xsl:template match="dtb:blockquote">
		<fo:block-container margin-left="{$char_width*2}pt">
			<xsl:apply-templates mode="wrapper"/>
		</fo:block-container>
	</xsl:template>
	
	<xsl:template match="*" mode="wrapper">
		<fo:block>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:pagenum" mode="wrapper">
	   <fo:block>
			<xsl:call-template name="pagenum"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:note">
		<xsl:choose>
			<xsl:when test="dtb:p"><xsl:apply-templates/></xsl:when>
			<xsl:otherwise><fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</fo:block></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="dtb:sidebar">
			<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:hd">
			<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</fo:block>
	</xsl:template>
	
	<xsl:template match="dtb:list/dtb:hd">
		<fo:list-item>
		 <fo:list-item-label><fo:block></fo:block></fo:list-item-label>
		 <fo:list-item-body>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
		</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='ol']">
	<fo:list-block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</fo:list-block>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='ul']">
<fo:list-block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</fo:list-block>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='pl']">
		<fo:list-block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</fo:list-block>
	</xsl:template>
	
	<xsl:template match="dtb:li">
			<fo:list-item>
			 <fo:list-item-label><fo:block></fo:block></fo:list-item-label>
			 <fo:list-item-body>
			 <fo:block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
			<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
			</fo:block>
</fo:list-item-body>
</fo:list-item>
	</xsl:template>
	
	<xsl:template match="dtb:dl">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dl/dtb:pagenum" priority="1">
			<xsl:call-template name="pagenum"/>
			<xsl:comment>empty</xsl:comment>
	</xsl:template>
	
	<xsl:template match="dtb:dt">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dd">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:table">
	
<fo:table-and-caption>
<fo:table table-layout="fixed">
			<xsl:call-template name="copyCatts"/>
			<xsl:choose>
				<xsl:when test="dtb:tbody"><xsl:apply-templates/>
</xsl:when>
				<xsl:otherwise><fo:table-body><xsl:apply-templates/></fo:table-body></xsl:otherwise>
			</xsl:choose>
		</fo:table>
		</fo:table-and-caption>
	</xsl:template>
	
	<xsl:template match="dtb:tbody">
		<fo:table-body>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</fo:table-body>
	</xsl:template>
	
	<xsl:template match="dtb:thead">
	<fo:table-header>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
			</fo:table-header>
	</xsl:template>
	
	<xsl:template match="dtb:tfoot">
	<!--
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>-->
	</xsl:template>
	
	<xsl:template match="dtb:tr">
		<fo:table-row>
			<xsl:call-template name="copyCatts"/>
			<xsl:copy-of select="@rowspan|@colspan"/>
			<xsl:apply-templates/>
		</fo:table-row>
	</xsl:template>
	
	<xsl:template match="dtb:th">
		<fo:table-cell>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</fo:table-cell>
	</xsl:template>
	<xsl:template match="dtb:td">
		<fo:table-cell>
			<xsl:call-template name="copyCatts"/>
			<xsl:choose>
				 <xsl:when test="dtb:p"><xsl:apply-templates/></xsl:when>
				 <xsl:otherwise><fo:block><xsl:apply-templates/></fo:block></xsl:otherwise>
			</xsl:choose>
		</fo:table-cell>
	</xsl:template>
	
	<xsl:template match="dtb:colgroup">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:col">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:poem">
		<div class="poem">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="dtb:poem/dtb:title">
		<p class="title">
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	<xsl:template match="dtb:cite/dtb:title">
		<span class="title">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="dtb:cite/dtb:author">
		<span class="author">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="dtb:cite">
		<cite>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</cite>
	</xsl:template>
	<xsl:template match="dtb:code">
		<code>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</code>
	</xsl:template>
	<xsl:template match="dtb:kbd">
		<kbd>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</kbd>
	</xsl:template>
	<xsl:template match="dtb:q">
		<q>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</q>
	</xsl:template>
	<xsl:template match="dtb:samp">
		<samp>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</samp>
	</xsl:template>
	<xsl:template match="dtb:linegroup">
		<div class="linegroup">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="dtb:line">
		<fo:block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates mode="inlineOnly"/>
		</fo:block>
	</xsl:template>
	<xsl:template match="dtb:linenum">
		<span class="linenum">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<!-- Inlines -->
	<xsl:template match="dtb:a">
		<span class="anchor">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="dtb:em">
		<em>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</em>
	</xsl:template>
	<xsl:template match="dtb:strong">
		<strong>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>
	<xsl:template match="dtb:abbr">
		<abbr>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</abbr>
	</xsl:template>
	<xsl:template match="dtb:acronym">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="dtb:bdo">
		<bdo>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</bdo>
	</xsl:template>
	<xsl:template match="dtb:dfn">
		<span class="definition">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="dtb:sent">
		
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		
	</xsl:template>
	<xsl:template match="dtb:w">
					<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="dtb:sup">
		<sup>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</sup>
	</xsl:template>
	<xsl:template match="dtb:sub">
		<sub>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</sub>
	</xsl:template>
	<xsl:template match="dtb:span">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	<!-- FIXME internal and external -->
	<xsl:template match="dtb:a[@href]">
		<xsl:choose>
			<xsl:when test="ancestor::dtb:*[@smilref]">
					<xsl:call-template name="copyCncatts"/>
					<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:call-template name="copyCatts"/>
					<xsl:copy-of select="@href"/>
					<xsl:apply-templates/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dtb:annoref">
		<a class="annoref">
			<xsl:call-template name="copyCncatts"/>
			<xsl:attribute name="href"><xsl:choose><xsl:when test="@smilref"><xsl:value-of select="@smilref"/></xsl:when><xsl:otherwise><xsl:text>#</xsl:text><xsl:value-of select="translate(@idref, '#', '')"/></xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<xsl:template match="dtb:*">
		<xsl:message>
  *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
   </xsl:message>
	</xsl:template>


	<xsl:template match="dtb:*" mode="inlineOnly">
		<xsl:choose>
			<xsl:when test="self::dtb:a or self::dtb:em or self::dtb:strong or self::dtb:abbr or self::dtb:acronym or self::dtb:bdo or self::dtb:dfn or self::dtb:sent or self::dtb:w or self::dtb:sup or self::dtb:sub or self::dtb:span or self::dtb:annoref or self::dtb:noteref or self::dtb:img or self::dtb:br or self::dtb:q or self::dtb:samp or self::dtb:pagenum">
				<xsl:apply-templates select=".">
					<xsl:with-param name="inlineFix" select="'true'"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
					<xsl:call-template name="get_class_attribute">
						<xsl:with-param name="element" select="."/>
					</xsl:call-template>
					<xsl:call-template name="copyCncatts"/>
					<xsl:apply-templates mode="inlineOnly"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="get_class_attribute">
		<xsl:param name="element"/>
		<xsl:choose>
			<xsl:when test="name($element)='imggroup'">
				<xsl:attribute name="class">imggroup</xsl:attribute>
			</xsl:when>
			<xsl:when test="name($element)='caption'">
				<xsl:attribute name="class">caption</xsl:attribute>
			</xsl:when>
			<xsl:when test="$element/@class">
				<xsl:attribute name="class"><xsl:value-of select="$element/@class"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="class"><xsl:text>inline-</xsl:text><xsl:value-of select="name($element)"/></xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
</xsl:stylesheet>
