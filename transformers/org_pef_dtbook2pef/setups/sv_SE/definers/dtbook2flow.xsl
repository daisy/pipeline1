<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="dtb">
	
	<xsl:param name="css_path"/>
	<xsl:param name="daisy_noteref"/>

	<xsl:param name="page_cols" select="30"/> <!-- in characters -->
	<xsl:param name="page_rows" select="29"/> <!-- in rows -->
	<xsl:param name="header_rows" select="1"/> <!--in rows -->
	<xsl:param name="styleUrl" select="'style-sv-SE.xml'"/>
	
	<xsl:variable name="style" select="document($styleUrl)/style"/>
	<!-- pts -->
	<xsl:variable name="char_width_pt" select="7.1"/> <!-- in pts -->
	<xsl:variable name="char_height_pt" select="12"/> <!-- in pts -->
	<xsl:variable name="page_width_pt" select="$char_width_pt*$page_cols+$char_width_pt div 2"/>
	<xsl:variable name="page_height_pt" select="($char_height_pt)*$page_rows" />
	<xsl:variable name="header_height_pt" select="($char_height_pt)*$header_rows" />
	
	<!-- 
TODO: 	Pagenum FIXME
				Pre-process move-pagenum last in levelx to first in next levely
				Pre-process, pagenum block only
				pre-process break p with list or dl
-->

	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	
	<xsl:template match="/">
		<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="folint.xsl"</xsl:processing-instruction>
		<root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<xsl:call-template name="insertLayoutMaster"/>
			<xsl:apply-templates/>
		</root>
	</xsl:template>
	
	<xsl:template name="insertLayoutMaster"></xsl:template>
	
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

	<xsl:template match="dtb:dtbook | dtb:book">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="dtb:head | dtb:meta | dtb:link"></xsl:template>
	
	<xsl:template match="dtb:frontmatter">
		<sequence master="title" hyphenate="true">
			<xsl:apply-templates select="dtb:docauthor"/>
			<xsl:apply-templates select="dtb:doctitle"/>
		</sequence>
		<xsl:call-template name="insertPageSequence">
			<xsl:with-param name="format" select="i"/>
			<xsl:with-param name="master" select="'front'"/>
			<xsl:with-param name="node" select="*[not(self::dtb:docauthor or self::dtb:doctitle)]"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dtb:bodymatter|dtb:rearmatter">
		<xsl:call-template name="insertPageSequence">
			<xsl:with-param name="format" select="1"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="insertPageSequence">
		<xsl:param name="format" select="1"/>
		<xsl:param name="node" select="node()"/>
		<xsl:param name="master" select="'main'"/>
		<sequence master="{$master}" hyphenate="true" format="{$format}" initial-page-number="1" orphans="0" widows="0">
			<xsl:apply-templates select="$node"/>
		</sequence>
	</xsl:template>

	<xsl:template match="dtb:level1">
		<block break-before="page">
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:level2 | dtb:level3 | dtb:level4 | dtb:level5 | dtb:level6 | dtb:level">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="dtb:p">
		<block>
			<xsl:if test="preceding-sibling::dtb:p">
				<xsl:attribute name="first-line-indent">2</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="inlineOnly"/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:pagenum">
		<xsl:choose>
			<!-- FIXME: All pagenum refs must be handled -->
			<xsl:when test="parent::dtb:level1 or parent::dtb:level2 or parent::dtb:level3 or parent::dtb:level4 or parent::dtb:level5 or parent::dtb:level6 or parent::dtb:level">
				<block>
					<xsl:call-template name="processPagenum"/>
				</block>
			</xsl:when>
			<xsl:otherwise><xsl:call-template name="processPagenum"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="processPagenum">
		<marker class="pagenum" value="{text()}"/>
		<xsl:variable name="preceding-pagenum" select="preceding::dtb:pagenum[1]"/>
		<xsl:variable name="preceding-marker">
			<xsl:if test="not($preceding-pagenum) or generate-id($preceding-pagenum/ancestor::dtb:level1/parent::*)=
						generate-id(ancestor::dtb:level1/parent::*)">
				<xsl:value-of select="$preceding-pagenum"/><xsl:text>-</xsl:text>
			</xsl:if>
		</xsl:variable>
		<marker class="pagenum-turn" value="{$preceding-marker}"/>
	</xsl:template>
	
	<xsl:template match="dtb:list/dtb:pagenum" priority="1">
		<xsl:param name="inlineFix"/>
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::*) or $inlineFix='true'">
				<block>
					<!--<xsl:call-template name="pagenum"/>-->
				</block>
			</xsl:when>
			<xsl:otherwise>
				<!--<xsl:message>Skipping pagenum element <xsl:value-of select="@id"/></xsl:message>-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="dtb:list/dtb:pagenum" mode="pagenumInLi">
		<!-- <xsl:call-template name="pagenum"/> -->
		<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
	</xsl:template>
	
	<xsl:template match="dtb:list/dtb:prodnote">
		<block>
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:address">
		<xsl:call-template name="copyCncatts"/>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:list[not(@type)]">
		<block>
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:lic">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
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
	
	<xsl:template match="dtb:imggroup/dtb:caption">
		<block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</block>
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
	
	<xsl:template match="dtb:byline">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dateline">
			<xsl:apply-templates/>
	</xsl:template>

	<!-- block elements or pagenum -->
	<xsl:template match="dtb:blockquote">
		<block margin-left="2" margin-top="1" margin-bottom="1">
			<xsl:apply-templates mode="wrapper"/>
		</block>
	</xsl:template>
	
	<xsl:template match="*" mode="wrapper">
		<block>
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:pagenum" mode="wrapper">
	   <block>
			<!-- <xsl:call-template name="pagenum"/> -->
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:note">
		<xsl:choose>
			<xsl:when test="dtb:p"><xsl:apply-templates/></xsl:when>
			<xsl:otherwise><block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			</block></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template match="dtb:list/dtb:hd">
		<block>
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
			<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='ol']">
		<block list-type="ol" margin-left="2">
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='ul']">
		<block list-type="ul" margin-left="2">
			<xsl:apply-templates/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:list[@type='pl']">
		<block list-type="pl" margin-left="2">
			<xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template match="dtb:li">
		 <block first-line-indent="3" text-indent="3">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
			<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:li[parent::dtb:list[@type='pl']]">
		 <block text-indent="3">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
			<xsl:apply-templates select="following-sibling::*[1][self::dtb:pagenum]" mode="pagenumInLi"/>
		</block>
	</xsl:template>
	
	<xsl:template match="dtb:dl">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dl/dtb:pagenum" priority="1">
			<!-- <xsl:call-template name="pagenum"/> -->
			<xsl:comment>empty</xsl:comment>
	</xsl:template>
	
	<xsl:template match="dtb:dt">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:dd">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:colgroup">
			<xsl:call-template name="copyCatts"/>
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:col">
		<xsl:call-template name="copyCatts"/>
		<xsl:apply-templates/>
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

	<xsl:template match="dtb:linenum">
		<span class="linenum">
			<xsl:call-template name="copyCncatts"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<xsl:template match="dtb:div[@class='pgroup']">
		<block margin-bottom="1">
			<xsl:apply-templates select="." mode="block-mode"/>
		</block>
	</xsl:template>
	
<!-- block elements / -->
	<xsl:template match="dtb:h1 | dtb:h2 | dtb:h3 | dtb:h4 | dtb:h5 | dtb:h6 | 
								dtb:bridgehead | dtb:covertitle | dtb:docauthor | dtb:doctitle | dtb:author |
								dtb:linegroup | dtb:line | dtb:poem | dtb:div | dtb:annotation | dtb:caption |
								dtb:epigraph | dtb:sidebar | dtb:hd">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
<!-- / block elements -->

	<xsl:template match="dtb:h1" mode="apply-block-attributes">
		<xsl:attribute name="margin-top">3</xsl:attribute>
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
		<xsl:if test="parent::dtb:level1/preceding-sibling::dtb:level1">
			<xsl:attribute name="break-before">page</xsl:attribute>
		</xsl:if>
	</xsl:template>

<!-- inlines that may alternatively be in block elements / -->
	<xsl:template match="dtb:a | dtb:cite | dtb:code | dtb:samp | dtb:kbd">
		<xsl:apply-templates select="." mode="block-mode"/>
	</xsl:template>
<!-- / inlines that may alternatively be in block elements -->

<!-- inline elements / -->
	<xsl:template match="dtb:em | dtb:strong | dtb:sup | dtb:sub | dtb:w |  
								dtb:sent | dtb:span | dtb:acronym | dtb:abbr | dtb:q">
		<xsl:apply-templates select="." mode="inline-mode"/>
	</xsl:template>
<!-- / inline elements -->

<!-- OK? -->
	<xsl:template match="dtb:br">
		<br/>
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
	
	<xsl:template match="dtb:table">
		<fo:table-and-caption>
			<fo:table table-layout="fixed">
				<xsl:choose>
					<xsl:when test="dtb:tbody"><xsl:apply-templates/></xsl:when>
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
			<xsl:apply-templates/>
		</fo:table-header>
	</xsl:template>

	<xsl:template match="dtb:tfoot">
		<fo:table-footer>
			<xsl:apply-templates/>
		</fo:table-footer>
	</xsl:template>

	<xsl:template match="dtb:tr">
		<fo:table-row>
			<xsl:apply-templates/>
		</fo:table-row>
	</xsl:template>
	
	<xsl:template match="dtb:th">
		<fo:table-cell>
			<xsl:apply-templates/>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="dtb:td">
		<fo:table-cell>
			<xsl:choose>
				 <xsl:when test="dtb:p"><xsl:apply-templates/></xsl:when>
				 <xsl:otherwise><block><xsl:apply-templates/></block></xsl:otherwise>
			</xsl:choose>
		</fo:table-cell>
	</xsl:template>
	
	<xsl:template match="*" mode="structure-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-container-attributes"/>
			<xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template match="*" mode="block-mode">
		<block>
			<xsl:apply-templates select="." mode="apply-block-attributes"/>
			<xsl:apply-templates/>
		</block>
	</xsl:template>

	<xsl:template match="*" mode="inline-mode">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*" mode="apply-block-container-attributes"/>
	<xsl:template match="*" mode="apply-block-attributes"/>

</xsl:stylesheet>
