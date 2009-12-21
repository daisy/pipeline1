<?xml version="1.0"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oebpackage="http://openebook.org/namespaces/oeb-package/1.0/" xmlns:smil="http://www.w3.org/2001/SMIL20/" exclude-result-prefixes="dtb smil xs fn">

  <xsl:include href="../l10n/l10n.xsl"/>
  <xsl:param name="l10n_default_language" select="'en'"/>
  <!-- Sets the l10n language -->
  <xsl:param name="l10n_language" select="''"/>
  <!-- Use the language of target when generating cross-reference text? -->
  <xsl:param name="l10n_use_xref_language" select="0"/>
  <xsl:variable name="l10n_file" select="document('dtbook2daisy3textonly-l10n.xml')" />

  <xsl:param name="outputname" select="'daisyoutput'"/>
  <xsl:param name="dcIdentifier"/>
  <xsl:param name="dcTitle"/>
  <xsl:param name="dcPublisher"/>
  <xsl:param name="dcDate"/>
  <xsl:param name="dcLanguage"/>
  <xsl:param name="dcCreator"/>
  <xsl:param name="facade" select="false"/>
  <xsl:param name="facade_name"/>

  <xsl:variable name="outputnameEsc" select="encode-for-uri($outputname)"/>
  <xsl:variable name="lower">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="upper">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>


  <xsl:variable name="skippableList">
    <xsl:variable name="lang" select="$metadata/meta[@name='dc:Language']/@content"/>
    <xsl:if test="//dtb:pagenum">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'pagenumlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'pagenum'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="pagenum" id="pagenum" defaultState="false" override="visible" bookStruct="PAGE_NUMBER" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@bookStruct='PAGE_NUMBER']" ressmilnode="//*[@class='pagenum']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:linenum">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'linenumlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'linenum'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="linenum" id="linenum" defaultState="false" override="visible" bookStruct="LINE_NUMBER" ncxnavlabel="{$ncxnavlabel}"  resncxnode="//smilCustomTest[@bookStruct='LINE_NUMBER']" ressmilnode="//*[@class='linenum']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:annotation">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'annotationlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'annotation'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="annotation" id="annotation"  defaultState="true" override="visible" bookStruct="ANNOTATION" ncxnavlabel="{$ncxnavlabel}"  resncxnode="//smilCustomTest[@bookStruct='ANNOTATION']" ressmilnode="//*[@class='annotation']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:sidebar[@render='required']">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'sidebarlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'sidebar'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="sidebar" attrName="render" attrValue="required" id="sidebar" defaultState="true" override="visible" ncxnavlabel="{$ncxnavlabel}"  resncxnode="//smilCustomTest[@id='sidebar']" ressmilnode="//*[@class='sidebar']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:sidebar[@render='optional']">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'sidebar'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'sidebarlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="sidebar" attrName="render" attrValue="optional" id="optsidebar"  defaultState="true" override="visible" bookStruct="OPTIONAL_SIDEBAR" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='optsidebar']" ressmilnode="//*[@class='optsidebar']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:list">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'listlist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'list'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="list" id="list"  defaultState="true" override="visible" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='list']" ressmilnode="//*[@class='list']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:prodnote[@render='required']">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'prodnotelist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'prodnote'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="prodnote" attrName="render" attrValue="required" id="prodnote"  defaultState="true" override="visible" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='prodnote']" ressmilnode="//*[@class='prodnote']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:prodnote[@render='optional']">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'prodnotelist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'prodnote'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="prodnote" attrName="render" attrValue="optional" id="optprodnote" defaultState="true" override="visible" bookStruct="OPTIONAL_PRODUCER_NOTE" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='optprodnote']" ressmilnode="//*[@class='optprodnote']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:note">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'notelist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'note'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="note" id="note"  defaultState="true" override="visible" bookStruct="NOTE" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='note']" ressmilnode="//*[@class='note']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:noteref">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'notereflist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'noteref'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="noteref" id="noteref"  defaultState="true" override="visible" bookStruct="NOTE_REFERENCE" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='noteref']" ressmilnode="//*[@class='noteref']" reslabel="{$reslabel}"/>
    </xsl:if>
    <xsl:if test="//dtb:table">
      <xsl:variable name="ncxnavlabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'tablelist'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="reslabel">
        <xsl:call-template name="getString">
          <xsl:with-param name="key" select="'table'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:variable>
      <skippable elemName="table" id="table"  defaultState="true" override="visible" ncxnavlabel="{$ncxnavlabel}" resncxnode="//smilCustomTest[@id='table']" ressmilnode="//*[@class='table']" reslabel="{$reslabel}"/>
    </xsl:if>
  </xsl:variable>

  <xsl:variable name="timeContList">
    <xsl:for-each select="$skippableList/*">
      <timecont name="{@elemName}"/>
    </xsl:for-each>
    <timecont name="level1"/>
    <timecont name="level2"/>
    <timecont name="level3"/>
    <timecont name="level4"/>
    <timecont name="level5"/>
    <timecont name="level6"/>
    <timecont name="level"/>
    <timecont name="line"/>
    <timecont name="address"/>
    <timecont name="div"/>
    <timecont name="epigraph"/>
    <timecont name="linegroup"/>
    <timecont name="poem"/>
    <timecont name="a"/>
    <timecont name="cite"/>
    <timecont name="annoref"/>
    <timecont name="img"/>
    <timecont name="imggroup"/>
    <timecont name="p"/>
    <timecont name="blockquote"/>
    <timecont name="dl"/>
    <timecont name="dd"/>
    <timecont name="li"/>
    <timecont name="caption"/>
    <timecont name="thead"/>
    <timecont name="tfoot"/>
    <timecont name="tbody"/>
    <timecont name="colgroup"/>
    <timecont name="tr"/>
    <timecont name="th"/>
    <timecont name="td"/>
  </xsl:variable>

  <xsl:variable name="escapableList">
    <escapable name="note"/>
    <escapable name="prodnote"/>
    <escapable name="annotation"/>
    <escapable name="table"/>
    <escapable name="list"/>
  </xsl:variable>

  <xsl:variable name="navListItemsList">
    <xsl:if test="//dtb:note">
      <elem name="note"/>
    </xsl:if>
    <xsl:if test="//dtb:noteref">
      <elem name="noteref"/>
    </xsl:if>
    <xsl:if test="//dtb:linenum">
      <elem name="linenum"/>
    </xsl:if>
    <xsl:if test="//dtb:annotation">
      <elem name="annotation"/>
    </xsl:if>
    <xsl:if test="//dtb:list">
      <elem name="list"/>
    </xsl:if>
    <xsl:if test="//dtb:table">
      <elem name="table"/>
    </xsl:if>
    <xsl:if test="//dtb:prodnote">
      <elem name="prodnote"/>
    </xsl:if>
  </xsl:variable>

  <xsl:template name="setCustomTestAttr">
    <xsl:param name="name" select="local-name()"/>
    <xsl:if test="$skippableList/skippable/@id=$name">
      <xsl:attribute name="customTest" select="$name"/>
    </xsl:if>
  </xsl:template>

  <xsl:template name="getid">
    <xsl:param name="node" select="."/>
    <xsl:choose>
      <xsl:when test="$node/@id">
        <xsl:value-of select="$node/@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$node/generate-id()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getTimeContDescendantList">
    <xsl:param name="node" select="."/>
    <xsl:variable name="id">
      <xsl:call-template name="getid"/>
    </xsl:variable>
    <id id="{concat('smil_par_',$id)}"/>
    <xsl:for-each select="$node/child::*">
      <xsl:variable name="prefix">
        <xsl:choose>
          <xsl:when test="local-name()=$escapableList/*/@name">
             <xsl:value-of select="'smil_seq_'"/>
          </xsl:when>
          <xsl:otherwise>
             <xsl:value-of select="'smil_par_'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="local-name()=$skippableList/*/@elemName">
          <id id="{concat($prefix,$id)}"/>
        </xsl:when>
        <xsl:when test="local-name()=$timeContList/*/@name">
          <id id="{concat($prefix,$id)}"/>
          <xsl:call-template name="getTimeContDescendantList"><xsl:with-param name="node" select="."/></xsl:call-template>
        </xsl:when>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="getLastTimeContDescendantId">
    <xsl:variable name="list">
      <xsl:call-template name="getTimeContDescendantList">
        <xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="$list/*[position()=last()]/@id"/>
  </xsl:template>


  <xsl:template name="timeContainerClass" as="xs:string">
    <xsl:param name="node" select="."/>
    <xsl:choose>
      <xsl:when test="$node/local-name()='prodnote' and $node/@render='optional'">
        <xsl:value-of select="'optprodnote'"/>
      </xsl:when>
      <xsl:when test="$node/local-name()='sidebar' and $node/@render='optional'">
        <xsl:value-of select="'optsidebar'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$node/local-name()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:variable name="metadata">
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Identifier'"/>
      <xsl:attribute name="id" select="'uid'"/>
      <xsl:attribute name="scheme" select="'DTB'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcIdentifier">
            <xsl:value-of select="$dcIdentifier"/>
          </xsl:when>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:uid']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:uid']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcIdentifier is required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Title'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcTitle">
            <xsl:value-of select="$dcTitle"/>
          </xsl:when>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Title']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Title']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcTitle is required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Publisher'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcPublisher">
            <xsl:value-of select="$dcPublisher"/>
          </xsl:when>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Publisher']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Publisher']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcPublisher is required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Date'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcDate">
            <xsl:value-of select="$dcDate"/>
          </xsl:when>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Date']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Date']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcDate is required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Format'"/>
      <xsl:attribute name="content" select="'ANSI/NISO Z39.86-2005'"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dc:Language'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcLanguage">
            <xsl:value-of select="$dcLanguage"/>
          </xsl:when>
          <xsl:when test="//*[1]/@xml:lang[1]">
            <xsl:value-of select="(//*/@xml:lang)[1]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcLanguage is required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:apply-templates mode="copydcmetadata" select="/dtb:dtbook/dtb:head/dtb:meta"/>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dtb:multimediaType'"/>
      <xsl:attribute name="content" select="'textNCX'"/>
      <xsl:attribute name="where" select="'opf,dtbook'"/>
    </xsl:element>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dtb:multimediaContent'"/>
      <xsl:variable name="content">
        <xsl:value-of select="'text'"/>
        <xsl:if test="//dtb:img">
          <xsl:value-of select="',image'"/>
        </xsl:if>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'opf,dtbook'"/>
    </xsl:element>
    <xsl:element name="meta" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
      <xsl:attribute name="name" select="'dtb:totalTime'"/>
      <xsl:attribute name="content" select="'00:00:00.00'"/>
      <xsl:attribute name="where" select="'opf,dtbook'"/>
    </xsl:element>
    <xsl:apply-templates select="/dtb:dtbook/dtb:head/dtb:meta" mode="copyothermetadatas"/>
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dtb:depth'"/>
      <xsl:variable name="content">
        <xsl:variable name="ndepth">
          <xsl:choose>
            <xsl:when test="//dtb:level6">
              <xsl:value-of select="6"/>
            </xsl:when>
            <xsl:when test="//dtb:level5">
              <xsl:value-of select="5"/>
            </xsl:when>
            <xsl:when test="//dtb:level4">
              <xsl:value-of select="4"/>
            </xsl:when>
            <xsl:when test="//dtb:level3">
              <xsl:value-of select="3"/>
            </xsl:when>
            <xsl:when test="//dtb:level2">
              <xsl:value-of select="2"/>
            </xsl:when>
            <xsl:when test="//dtb:level1">
              <xsl:value-of select="1"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="0"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="udepth">
          <xsl:choose>
            <xsl:when test="//dtb:level">
              <xsl:value-of select="max(//dtb:level/count(ancestor::dtb:level))+1"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="0"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="max(($ndepth,$udepth))"/>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'dtbook,ncx'"/>
    </xsl:element>
    <xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:attribute name="name" select="'dtb:totalPageCount'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:totalPageCount']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:totalPageCount']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="count(//dtb:pagenum)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'dtbook,ncx'"/>
    </xsl:element>
    <xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:attribute name="name" select="'dtb:maxPageNumber'"/>
      <xsl:variable name="maxNumPage">
        <xsl:variable name="list" as="xs:integer+">
          <xsl:value-of select="0"/>
          <xsl:for-each select="//dtb:pagenum/text()">
            <xsl:if test="number()">
              <xsl:value-of select="."/>
            </xsl:if>
          </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="max($list)"/>
      </xsl:variable>
      <xsl:attribute name="content" select="$maxNumPage"/>
      <xsl:attribute name="where" select="'dtbook,ncx'"/>
    </xsl:element>
    <xsl:element name="meta" namespace="http://www.w3.org/2001/SMIL20/">
      <xsl:attribute name="name" select="'dtb:uid'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="$dcIdentifier">
            <xsl:value-of select="$dcIdentifier"/>
          </xsl:when>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:uid']">
            <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:uid']/@content"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>dcIdentifier required</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'dtbook,ncx,smil'"/>
    </xsl:element>
    <xsl:element name="meta" namespace="http://www.w3.org/2001/SMIL20/">
      <xsl:attribute name="name" select="'dtb:totalElapsedTime'"/>
      <xsl:attribute name="content" select="'00:00:00.00'"/>
      <xsl:attribute name="where" select="'dtbook,smil'"/>
    </xsl:element>
-->
    <xsl:element name="meta">
      <xsl:attribute name="name" select="'dtb:generator'"/>
      <xsl:variable name="content">
        <xsl:choose>
          <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:generator']">
            <xsl:value-of select="concat(/dtb:dtbook/dtb:head/dtb:meta[@name='dtb:uid']/@content,', dtbook2daisy')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'dtbook2daisy'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="content" select="$content"/>
      <xsl:attribute name="where" select="'dtbook,ncx,smil'"/>
    </xsl:element>
  </xsl:variable>

  <xsl:template name="write_opf_headers">
       <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE package PUBLIC "+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN" "http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd" &gt;
</xsl:text>
  </xsl:template>

  <xsl:template name="write_opf_metadata">
         <xsl:element name="metadata" namespace="http://openebook.org/namespaces/oeb-package/1.0/" >
           <dc-metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oebpackage="http://openebook.org/namespaces/oeb-package/1.0/" xmlns="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:for-each select="$metadata/*">
               <xsl:if test="contains(@name,'dc:') and contains(@where,'opf')">
                 <xsl:element name="{@name}" namespace="http://purl.org/dc/elements/1.1/">
                   <xsl:copy-of select="@scheme|@id"/>
                   <xsl:value-of select="@content"/>
                 </xsl:element>
               </xsl:if>
             </xsl:for-each>
           </dc-metadata>
           <xsl:element name="x-metadata" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:for-each select="$metadata/*">
               <xsl:if test="not(contains(@name,'dc:')) and (contains(@where,'opf'))">
                 <xsl:element name="meta" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
                   <xsl:attribute name="name" select="@name"/>
                   <xsl:attribute name="content" select="@content"/>
                 </xsl:element>
               </xsl:if>
             </xsl:for-each>
           </xsl:element>
         </xsl:element>
  </xsl:template>

  <xsl:template name="write_opf_manifest">
    <xsl:param name="prefix" select="$outputnameEsc"/>
         <xsl:element name="manifest" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
           <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:attribute name="id" select="'opf'"/>
             <xsl:attribute name="href" select="concat($prefix,'.opf')"/>
             <xsl:attribute name="media-type" select="'text/xml'"/>
           </xsl:element>
           <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:attribute name="id" select="'ncx'"/>
             <xsl:attribute name="href" select="concat($prefix,'.ncx')"/>
             <xsl:attribute name="media-type" select="'application/x-dtbncx+xml'"/>
           </xsl:element>
           <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:attribute name="id" select="'SMIL'"/>
             <xsl:attribute name="href" select="concat($prefix,'.smil')"/>
             <xsl:attribute name="media-type" select="'application/smil'"/>
           </xsl:element>
           <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:attribute name="id" select="'dtbook'"/>
             <xsl:attribute name="href" select="concat($prefix,'.xml')"/>
             <xsl:attribute name="media-type" select="'application/x-dtbook+xml'"/>
           </xsl:element>
           <xsl:if test="$skippableList/skippable">
             <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
               <xsl:attribute name="id" select="'resource'"/>
               <xsl:attribute name="href" select="concat($prefix,'.res')"/>
               <xsl:attribute name="media-type" select="'application/x-dtbresource+xml'"/>
             </xsl:element>
           </xsl:if>
           <xsl:for-each select="//dtb:img">
             <xsl:element name="item" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
               <xsl:attribute name="id" select="'image'"/>
               <xsl:attribute name="href" select="@src"/>
               <xsl:variable name="srclc" select="translate(@src,$upper,$lower)"/>
               <xsl:if test="contains($srclc,'.jpg') or contains($srclc,'.jpeg')">
                 <xsl:attribute name="media-type" select="'image/jpeg'"/>
               </xsl:if>
               <xsl:if test="contains($srclc,'.png')">
                 <xsl:attribute name="media-type" select="'image/png'"/>
               </xsl:if>
               <xsl:if test="contains($srclc,'.svg')">
                 <xsl:attribute name="media-type" select="'image/svg+xml'"/>
               </xsl:if>
             </xsl:element>
           </xsl:for-each>
         </xsl:element>
  </xsl:template>

  <xsl:template name="write_opf_spine">
         <xsl:element name="spine" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
           <xsl:element name="itemref" namespace="http://openebook.org/namespaces/oeb-package/1.0/">
             <xsl:attribute name="idref" select="'SMIL'" />
           </xsl:element>
         </xsl:element>
  </xsl:template>

  <xsl:template name="write_opf">
    <xsl:result-document href="{$outputname}.opf" method="xml" encoding="UTF-8" indent="yes" >
       <xsl:call-template name="write_opf_headers"/>
       <xsl:element name="package" namespace="http://openebook.org/namespaces/oeb-package/1.0/" >
         <xsl:attribute name="unique-identifier" select="'uid'"/>
         <xsl:call-template name="write_opf_metadata"/>
         <xsl:call-template name="write_opf_manifest"/>
         <xsl:call-template name="write_opf_spine"/>
       </xsl:element>
    </xsl:result-document>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='note']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_note_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="//dtb:noteref[@idref=concat('#',$id)]/text()"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='noteref']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_noteref_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="text()"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='linenum']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_linenum_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="text()"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>


  <xsl:template mode="ncxnavlist" match="*[local-name()='annotation']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_annotation_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="concat('Annotation ',count(preceding::dtb:annotation)+1)"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='list']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_list_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="concat('Liste ',count(preceding::dtb:list)+1)"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='table']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_table_',$id)"/>
      <xsl:attribute name="class" select="$skippableList/skippable[@elemName=$localname]/@id"/>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:choose>
            <xsl:when test="dtb:caption">
              <xsl:value-of select="dtb:caption/text()"/> 
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat('Tableau ',count(preceding::dtb:table)+1)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="ncxnavlist" match="*[local-name()='prodnote']">
    <xsl:element name="navTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="localname" select="local-name()" as="xs:string"/>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('ncx_prodnote_',$id)"/>
      <xsl:choose>
        <xsl:when test="@render='optional'">
          <xsl:attribute name="class" select="'optprodnote'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class" select="'prodnote'"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="concat('Note du producteur ',count(preceding::dtb:prodnote)+1)"/>
        </xsl:element>
      </xsl:element>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template name="write_ncx_headers">
      <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE ncx PUBLIC "-//NISO//DTD ncx 2005-1//EN" "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd" &gt;
</xsl:text>
  </xsl:template>

  <xsl:template name="write_ncx_head">
        <xsl:element name="head" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:for-each select="$metadata/*">
            <xsl:if test="contains(@where,'ncx')">
              <xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/ncx/">
                <xsl:attribute name="name" select="@name"/>
                <xsl:attribute name="content" select="@content"/>
              </xsl:element>
            </xsl:if>
          </xsl:for-each>
          <xsl:for-each select="$skippableList/skippable">
            <xsl:element name="smilCustomTest" namespace="http://www.daisy.org/z3986/2005/ncx/">
              <xsl:copy-of select="@id|@defaultState|@override|@bookStruct"/>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_ncx_docTitle">
        <xsl:element name="docTitle" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:variable name="content">
              <xsl:choose>
                <xsl:when test="$dcTitle">
                  <xsl:value-of select="$dcTitle"/>
                </xsl:when>
                <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Title']">
                  <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Title']/@content"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>dcTitle is required</xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$content"/>
          </xsl:element>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_ncx_docAuthor">
        <xsl:if test="($dcCreator) or (/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Creator'])">
          <xsl:element name="docAuthor" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
              <xsl:variable name="content">
                <xsl:choose>
                  <xsl:when test="$dcCreator">
                    <xsl:value-of select="$dcCreator"/>
                  </xsl:when>
                  <xsl:when test="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Creator']">
                    <xsl:value-of select="/dtb:dtbook/dtb:head/dtb:meta[@name='dc:Creator']/@content"/>
                  </xsl:when>
                </xsl:choose>
              </xsl:variable>
              <xsl:value-of select="$content"/>
            </xsl:element>
          </xsl:element>
        </xsl:if>
  </xsl:template>

  <xsl:template name="write_ncx_navMap">
        <xsl:element name="navMap" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:apply-templates mode="ncxtoc" select="/"/>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_ncx_pageList">
        <xsl:if test="//dtb:pagenum">
          <xsl:element name="pageList" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:attribute name="id" select="'pages'"/>
            <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
              <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
                <xsl:value-of select="$skippableList/skippable[@elemName='pagenum']/@ncxnavlabel"/>
              </xsl:element>
            </xsl:element>
            <xsl:apply-templates mode="ncxpagelist" select="//dtb:pagenum"/>
          </xsl:element>
        </xsl:if>
  </xsl:template>

  <xsl:template name="write_ncx_navList">
        <xsl:variable name="curnode" select="."/>
        <xsl:for-each select="$skippableList/*">
          <xsl:variable name="elemName" select="@elemName"/>
          <xsl:variable name="attrName" select="@attrName"/>
          <xsl:variable name="attrValue" select="@attrValue"/>
          <xsl:choose>
            <xsl:when test="@elemName='pagenum' or (@elemName='prodnote' and @attrValue='optional') or elemName='sidebar'"/>
            <xsl:otherwise>
              <xsl:if test="$curnode//*[local-name()=$elemName]">
                <xsl:element name="navList" namespace="http://www.daisy.org/z3986/2005/ncx/">
                  <xsl:attribute name="id" select="concat('ncx_navlist_',@id)"/>
                  <xsl:attribute name="class" select="@id"/>
                  <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
                    <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
                      <xsl:value-of select="@ncxnavlabel"/>
                    </xsl:element>
                  </xsl:element>
                  <xsl:apply-templates mode="ncxnavlist" select="$curnode//*[local-name()=$elemName]"/>
                </xsl:element>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
  </xsl:template>

  <xsl:template name="write_ncx">
    <xsl:result-document href="{$outputname}.ncx" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:call-template name="write_ncx_headers"/>
      <xsl:element name="ncx" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="version" select="'2005-1'"/>
        <xsl:call-template name="write_ncx_head"/>
        <xsl:call-template name="write_ncx_docTitle"/>
        <xsl:call-template name="write_ncx_docAuthor"/>
        <xsl:call-template name="write_ncx_navMap"/>
        <xsl:call-template name="write_ncx_pageList"/>
        <xsl:call-template name="write_ncx_navList"/>
      </xsl:element>
    </xsl:result-document>
    <xsl:if test="$facade='true'">
      <xsl:result-document href="{$facade_name}.ncx" method="xml" encoding="UTF-8" indent="yes" >
        <xsl:call-template name="write_ncx_headers"/>
        <xsl:element name="ncx" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:attribute name="version" select="'2005-1'"/>
          <xsl:call-template name="write_ncx_head"/>
          <xsl:call-template name="write_ncx_docTitle"/>
          <xsl:call-template name="write_ncx_docAuthor"/>
          <xsl:element name="navMap" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:element name="navPoint" namespace="http://www.daisy.org/z3986/2005/ncx/">
              <xsl:attribute name="id" select="'ncx_warning'"/>
              <xsl:attribute name="playOrder" select="'1'"/>
              <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
                <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
                  <xsl:value-of select="'Avertissement'"/>
                </xsl:element>
              </xsl:element>
              <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
                <xsl:attribute name="src" select="concat($facade_name,'.smil#smil_warning')"/>
              </xsl:element>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:result-document>
    </xsl:if>
  </xsl:template>

  <xsl:template name="write_smil_headers">
      <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE smil PUBLIC "-//NISO//DTD dtbsmil 2005-2//EN" "http://www.daisy.org/z3986/2005/dtbsmil-2005-2.dtd"&gt;
</xsl:text>
  </xsl:template>

  <xsl:template name="write_smil_head">
        <xsl:element name="head" namespace="http://www.w3.org/2001/SMIL20/">
          <xsl:for-each select="$metadata/*">
            <xsl:if test="contains(@where,'smil')">
              <xsl:element name="meta" namespace="http://www.w3.org/2001/SMIL20/">
                <xsl:attribute name="name" select="@name"/>
                <xsl:attribute name="content" select="@content"/>
              </xsl:element>
            </xsl:if>
          </xsl:for-each>
          <xsl:if test="$skippableList/skippable">
            <xsl:element name="customAttributes" namespace="http://www.w3.org/2001/SMIL20/">
              <xsl:for-each select="$skippableList/skippable">
                <xsl:element name="customTest" namespace="http://www.w3.org/2001/SMIL20/">
                  <xsl:copy-of select="@id|@defaultState|@override"/>
                </xsl:element>
              </xsl:for-each>
            </xsl:element>
          </xsl:if>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_smil_body">
        <xsl:element name="body" namespace="http://www.w3.org/2001/SMIL20/">
          <xsl:element name="seq" namespace="http://www.w3.org/2001/SMIL20/">
            <xsl:attribute name="id" select="concat('smil_seq_',generate-id())"/>
            <xsl:apply-templates mode="smil" select="/dtb:dtbook/dtb:book"/>
          </xsl:element>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_smil">
    <xsl:result-document href="{$outputname}.smil" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:call-template name="write_smil_headers"/>
      <xsl:element name="smil" namespace="http://www.w3.org/2001/SMIL20/">
        <xsl:call-template name="write_smil_head"/>
        <xsl:call-template name="write_smil_body"/>
      </xsl:element>
    </xsl:result-document>    
    <xsl:if test="$facade='true'">
    <xsl:result-document href="{$facade_name}.smil" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:call-template name="write_smil_headers"/>
      <xsl:element name="smil" namespace="http://www.w3.org/2001/SMIL20/">
        <xsl:call-template name="write_smil_head"/>
        <xsl:element name="body" namespace="http://www.w3.org/2001/SMIL20/">
          <xsl:element name="seq" namespace="http://www.w3.org/2001/SMIL20/">
            <xsl:attribute name="id" select="'smil_warning'"/>
            <xsl:element name="text" namespace="http://www.w3.org/2001/SMIL20/">
              <xsl:attribute name="src" select="concat($facade_name,'.xml#warning')"/>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:result-document>    
    </xsl:if>
  </xsl:template>

  <xsl:template name="rewrite_dtbook_headers">
      <xsl:choose>
        <xsl:when test="/dtb:dtbook/@version='2005-1'">
          <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE dtbook PUBLIC "-//NISO//DTD dtbook 2005-1//EN" "http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd"&gt;
</xsl:text>
        </xsl:when>
        <xsl:when test="/dtb:dtbook/@version='2005-2'">
          <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE dtbook PUBLIC "-//NISO//DTD dtbook 2005-2//EN" "http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd"&gt;
</xsl:text>
        </xsl:when>
        <xsl:when test="/dtb:dtbook/@version='2005-3'">
          <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE dtbook PUBLIC "-//NISO//DTD dtbook 2005-3//EN" "http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd"&gt;
</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>Unknown DTBook version</xsl:message>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template name="rewrite_dtbook">
    <xsl:result-document href="{$outputname}.xml" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:call-template name="rewrite_dtbook_headers"/>
      <xsl:apply-templates mode="dtbook" select="/" />
    </xsl:result-document>
    <xsl:if test="$facade='true'">
    <xsl:result-document href="{$facade_name}.xml" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:call-template name="rewrite_dtbook_headers"/>

      <xsl:apply-templates mode="dtbook_facade" select="/" />
    </xsl:result-document>
    </xsl:if>
  </xsl:template>

  <xsl:template name="write_resources_scope">
        <xsl:element name="scope" namespace="http://www.daisy.org/z3986/2005/resource/">
          <xsl:attribute name="nsuri" select="'http://www.daisy.org/z3986/2005/ncx/'"/>
          <xsl:for-each select="$skippableList/skippable">
            <xsl:element name="nodeSet" namespace="http://www.daisy.org/z3986/2005/resource/">
              <xsl:attribute name="id" select="concat('res_ncx_ns_',generate-id())"/>
              <xsl:attribute name="select" select="@resncxnode"/>
              <xsl:element name="resource" namespace="http://www.daisy.org/z3986/2005/resource/">
                <xsl:attribute name="xml:lang" select="'fr'"/>
                <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/resource/">
                  <xsl:value-of select="@reslabel"/>
                </xsl:element>
              </xsl:element>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
        <xsl:element name="scope" namespace="http://www.daisy.org/z3986/2005/resource/">
          <xsl:attribute name="nsuri" select="'http://www.w3.org/2001/SMIL20/'"/>
          <xsl:for-each select="$skippableList/skippable">
            <xsl:element name="nodeSet" namespace="http://www.daisy.org/z3986/2005/resource/">
              <xsl:attribute name="id" select="concat('res_smil_ns_',generate-id())"/>
              <xsl:attribute name="select" select="@ressmilnode"/>
              <xsl:element name="resource" namespace="http://www.daisy.org/z3986/2005/resource/">
                <xsl:attribute name="xml:lang" select="'fr'"/>
                <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/resource/">
                  <xsl:value-of select="@reslabel"/>
                </xsl:element>
              </xsl:element>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
  </xsl:template>

  <xsl:template name="write_resources">
    <xsl:result-document href="{$outputname}.res" method="xml" encoding="UTF-8" indent="yes" >
      <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE resources PUBLIC "-//NISO//DTD resource 2005-1//EN" "http://www.daisy.org/z3986/2005/resource-2005-1.dtd"&gt;
</xsl:text>
      <xsl:element name="resources" namespace="http://www.daisy.org/z3986/2005/resource/">
        <xsl:attribute name="version" select="'2005-1'"/>
        <xsl:call-template name="write_resources_scope"/>
      </xsl:element>
    </xsl:result-document>
  </xsl:template>

  <xsl:template match="/">
    <xsl:call-template name="write_opf"/>
    <xsl:call-template name="write_smil"/>
    <xsl:call-template name="rewrite_dtbook"/>
    <xsl:call-template name="write_ncx"/>
    <xsl:if test="$skippableList/skippable">
      <xsl:call-template name="write_resources"/>
    </xsl:if>
  </xsl:template>

  <xsl:template mode="copydcmetadata" match="dtb:meta">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:if test="contains($name,'dc:') and $name!='dc:Identifier' and $name!='dc:Title' and $name!='dc:Publisher' and $name!='dc:Date'  and $name!='dc:Language'">
      <xsl:element name="meta">
        <xsl:attribute name="name" select="$name"/>
        <xsl:attribute name="content" select="@content"/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template mode="copyothermetadatas" match="dtb:meta">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:if test="not(contains($name,'dc:')) and $name!='dtb:uid' and $name!='dtb:multimediaType' and $name!='dtb:multimediaContent' and $name!='dtb:totalTime' and $name!='dtb:totalPageCount' ">
      <xsl:element name="meta">
        <xsl:copy-of select="@*"/>
        <xsl:attribute name="where" select="'dtbook'"/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template mode="smil" match="*">
  <xsl:variable name="localname" select="local-name()"/>
  <xsl:choose>
   <xsl:when test="local-name()=$escapableList/*/@name">
    <xsl:element name="seq" namespace="http://www.w3.org/2001/SMIL20/">
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('smil_seq_',$id)"/>
      <xsl:variable name="timeContainerClass">
        <xsl:call-template name="timeContainerClass"/>
      </xsl:variable>
      <xsl:attribute name="class" select="$timeContainerClass"/>
      <xsl:call-template name="setCustomTestAttr"/>
      <xsl:variable name="childId-value">
        <xsl:call-template name="getLastTimeContDescendantId"/>
      </xsl:variable>
      <xsl:attribute name="end" select="concat('DTBuserEscape;',$childId-value,'.end')"/>
      <xsl:element name="par" namespace="http://www.w3.org/2001/SMIL20/">
        <xsl:attribute name="id" select="concat('smil_par_',$id)"/>
        <xsl:variable name="timeContainerClass">
          <xsl:call-template name="timeContainerClass"/>
        </xsl:variable>
        <xsl:attribute name="class" select="$timeContainerClass"/>
        <xsl:call-template name="setCustomTestAttr"/>
        <xsl:element name="text" namespace="http://www.w3.org/2001/SMIL20/">
          <xsl:attribute name="src" select="concat($outputnameEsc,'.xml','#',$id)"/>
        </xsl:element>
      </xsl:element>
      <xsl:apply-templates mode="smil" select="*"/>
    </xsl:element>
   </xsl:when>
   <xsl:when test="local-name()=$timeContList/*/@name">
    <xsl:element name="par" namespace="http://www.w3.org/2001/SMIL20/">
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('smil_par_',$id)"/>
      <xsl:variable name="timeContainerClass">
        <xsl:call-template name="timeContainerClass"/>
      </xsl:variable>
      <xsl:attribute name="class" select="$timeContainerClass"/>
      <xsl:call-template name="setCustomTestAttr"/>
      <xsl:element name="text" namespace="http://www.w3.org/2001/SMIL20/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.xml','#',$id)"/>
      </xsl:element>
    </xsl:element>
    <xsl:apply-templates mode="smil" select="*"/>
   </xsl:when>
   <xsl:otherwise>
     <xsl:apply-templates mode="smil" select="*"/>
   </xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  <xsl:template mode="smil" match="dtb:a">
    <xsl:element name="a" namespace="http://www.w3.org/2001/SMIL20/">
      <xsl:variable name="href">
        <xsl:choose>
          <xsl:when test="@external='false'">
            <xsl:value-of select="concat($outputnameEsc,'.smil','#smil_par_',substring-after(@href,'#'))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:attribute name="id" select="concat('smil_a_',$id)"/>
      <xsl:attribute name="href" select="$href"/>
      <xsl:attribute name="external" select="@external"/>
      <xsl:variable name="timeContainerClass">
        <xsl:call-template name="timeContainerClass"/>
      </xsl:variable>
      <xsl:attribute name="class" select="$timeContainerClass"/>
      <xsl:call-template name="setCustomTestAttr"/>
      <xsl:element name="text" namespace="http://www.w3.org/2001/SMIL20/">
        <xsl:variable name="id">
          <xsl:call-template name="getid"/>
        </xsl:variable>
        <xsl:attribute name="src" select="concat($outputnameEsc,'.xml','#',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="dtbook" match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="local-name()=$timeContList/*/@name">
        <xsl:variable name="id">
          <xsl:call-template name="getid"/>
        </xsl:variable>
        <xsl:attribute name="id" select="$id"/>
        <xsl:choose>
          <xsl:when test="local-name()='a'">
            <xsl:attribute name="smilref" select="concat($outputnameEsc,'.smil#smil_a_',$id)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="smilref" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
      <xsl:apply-templates mode="dtbook" />
    </xsl:copy>
  </xsl:template>

  <xsl:template mode="dtbook" match="dtb:meta[@name='dtb:uid']">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="$dcIdentifier">
        <xsl:attribute name="content" select="$dcIdentifier"/>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <xsl:template mode="dtbook_facade" match="*">
    <xsl:apply-templates mode="dtbook_facade" select="*"/>
  </xsl:template>

  <xsl:template mode="dtbook_facade" match="dtb:dtbook|dtb:head|dtb:meta">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="dtbook_facade" select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template mode="dtbook_facade" match="dtb:book">
    <xsl:copy>
      <xsl:if test="//dtb:docauthor or //dtb:doctitle">
        <xsl:element name="frontmatter" namespace="http://www.daisy.org/z3986/2005/dtbook/">
          <xsl:copy-of select="//dtb:doctitle[1]|//dtb:docauthor[1]"/>
        </xsl:element>
      </xsl:if>
      <xsl:element name="bodymatter" namespace="http://www.daisy.org/z3986/2005/dtbook/">
        <xsl:element name="level" namespace="http://www.daisy.org/z3986/2005/dtbook/">
          <xsl:attribute name="id" select="'warning'"/>
          <xsl:element name="hd" namespace="http://www.daisy.org/z3986/2005/dtbook/">
            <xsl:value-of select="'Avertissement'"/>
            <xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/">
              <xsl:value-of select="'Ce livre est crypté conformément à la norme PDTB. Si vous lisez ce message, c''est que votre lecteur ne supporte pas cette norme.'"/>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:copy>
  </xsl:template>

  <xsl:template mode="ncxtoc" match="*">
    <xsl:apply-templates mode="ncxtoc" select="*" />
  </xsl:template>

  <xsl:template mode="ncxtoc" match="dtb:level|dtb:level1|dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6">
<!--
    <xsl:choose>
      <xsl:when test="dtb:hd|dtb:h1|dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6"> -->
        <xsl:element name="navPoint" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:variable name="id">
            <xsl:call-template name="getid"/>
          </xsl:variable>
          <xsl:attribute name="id" select="concat('ncx_',$id)"/>
          <xsl:attribute name="class" select="local-name()"/>
          <xsl:variable name="playOrder">
            <xsl:call-template name="playOrder"/>
          </xsl:variable>
          <xsl:attribute name="playOrder" select="$playOrder"/>
          <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
              <xsl:choose>
                <xsl:when test="dtb:hd|dtb:h1|dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6">
                  <xsl:value-of select="dtb:hd/text()|dtb:h1/text()|dtb:h2/text()|dtb:h3/text()|dtb:h4/text()|dtb:h5/text()|dtb:h6/text()"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:variable name="lang" select="$metadata/meta[@name='dc:Language']/@content"/>
                  <xsl:call-template name="getString">
                    <xsl:with-param name="key" select="'notitle'"/>
                    <xsl:with-param name="lang" select="$lang"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:element>
          </xsl:element>      
          <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
            <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
          </xsl:element>
          <xsl:apply-templates mode="ncxtoc" select="dtb:level|dtb:level1|dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6"/>
        </xsl:element>
<!--      </xsl:when> -->
<!--      <xsl:otherwise>
        <xsl:apply-templates mode="ncxtoc" select="dtb:level|dtb:level1|dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6"/>
      </xsl:otherwise>
    </xsl:choose>
-->
  </xsl:template>

  <xsl:template name="playOrder">
    <xsl:param name="node" select="."/>
    <xsl:variable name="elems">
     <xsl:for-each select="$navListItemsList/*">
       <elem name="{@name}"/>
     </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="temp" as="xs:integer*">
    <xsl:for-each select="$elems/*">
      <xsl:variable name="name" select="@name"/>
      <xsl:value-of select="$node/count(preceding::*[local-name()=$name])+$node/count(ancestor-or-self::*[local-name()=$name])"/>
    </xsl:for-each>
    </xsl:variable>
<!--    <xsl:value-of select="sum($temp)+count(preceding::dtb:level/dtb:hd)+count(preceding::dtb:level1/dtb:h1)+count(preceding::dtb:level2/dtb:h2)+count(preceding::dtb:level3/dtb:h3)+count(preceding::dtb:level4/dtb:h4)+count(preceding::dtb:level5/dtb:h5)+count(preceding::dtb:level6/dtb:h6)+count(preceding::dtb:pagenum)  +  count(ancestor-or-self::dtb:level/dtb:hd)+count(ancestor-or-self::dtb:level1/dtb:h1)+count(ancestor-or-self::dtb:level2/dtb:h2)+count(ancestor-or-self::dtb:level3/dtb:h3)+count(ancestor-or-self::dtb:level4/dtb:h4)+count(ancestor-or-self::dtb:level5/dtb:h5)+count(ancestor-or-self::dtb:level6/dtb:h6)+count(ancestor-or-self::dtb:pagenum)"/> -->

    <xsl:value-of select="sum($temp)+count(preceding::dtb:level)+count(preceding::dtb:level1)+count(preceding::dtb:level2)+count(preceding::dtb:level3)+count(preceding::dtb:level4)+count(preceding::dtb:level5)+count(preceding::dtb:level6)+count(preceding::dtb:pagenum)  +  count(ancestor-or-self::dtb:level)+count(ancestor-or-self::dtb:level1)+count(ancestor-or-self::dtb:level2)+count(ancestor-or-self::dtb:level3)+count(ancestor-or-self::dtb:level4)+count(ancestor-or-self::dtb:level5)+count(ancestor-or-self::dtb:level6)+count(ancestor-or-self::dtb:pagenum)"/>

  </xsl:template>

  <xsl:template mode="ncxpagelist" match="dtb:pagenum">
    <xsl:element name="pageTarget" namespace="http://www.daisy.org/z3986/2005/ncx/">
      <xsl:variable name="value" select="text()"/>
      <xsl:attribute name="id" select="concat('p',$value)"/>
      <xsl:attribute name="class" select="local-name()"/>
      <xsl:choose>
        <xsl:when test="@page">
          <xsl:attribute name="type" select="@page"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="ancestor::dtb:frontmatter">
              <xsl:attribute name="type" select="front"/>
            </xsl:when>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="number()">
        <xsl:attribute name="value" select="$value"/>
      </xsl:if>
      <xsl:variable name="playOrder">
        <xsl:call-template name="playOrder"/>
      </xsl:variable>
      <xsl:attribute name="playOrder" select="$playOrder"/>
      <xsl:element name="navLabel" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:element name="text" namespace="http://www.daisy.org/z3986/2005/ncx/">
          <xsl:value-of select="$value"/>
        </xsl:element>
      </xsl:element>
      <xsl:variable name="id">
        <xsl:call-template name="getid"/>
      </xsl:variable>
      <xsl:element name="content" namespace="http://www.daisy.org/z3986/2005/ncx/">
        <xsl:attribute name="src" select="concat($outputnameEsc,'.smil#smil_par_',$id)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
