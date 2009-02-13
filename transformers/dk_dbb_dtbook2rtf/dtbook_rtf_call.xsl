<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
  Daisy Pipeline (C) 2005-2008 DBB and Daisy Consortium
  
  This library is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the Free
  Software Foundation; either version 2.1 of the License, or (at your option)
  any later version.
  
  This library is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
  details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--> 
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
	>
	<xsl:output method="text" version="1.0" encoding="windows-1252"/>

	<xsl:template name="BIGHEADER">
	<xsl:text>{\rtf1\ansi\ansicpg1252\uc1 \deff0\deflang1033\deflangfe1030{\fonttbl{\f0\froman\fcharset0\fprq2{\*\panose 02020603050405020304}Times New Roman;}{\f1\fswiss\fcharset0\fprq2{\*\panose 020b0604020202020204}Arial;}
{\f7\fswiss\fcharset0\fprq2{\*\panose 00000000000000000000}Geneva{\*\falt Arial};}{\f54\froman\fcharset238\fprq2 Times New Roman CE;}{\f55\froman\fcharset204\fprq2 Times New Roman Cyr;}{\f57\froman\fcharset161\fprq2 Times New Roman Greek;}
{\f58\froman\fcharset162\fprq2 Times New Roman Tur;}{\f59\froman\fcharset177\fprq2 Times New Roman (Hebrew);}{\f60\froman\fcharset178\fprq2 Times New Roman (Arabic);}{\f61\froman\fcharset186\fprq2 Times New Roman Baltic;}
{\f62\fswiss\fcharset238\fprq2 Arial CE;}{\f63\fswiss\fcharset204\fprq2 Arial Cyr;}{\f65\fswiss\fcharset161\fprq2 Arial Greek;}{\f66\fswiss\fcharset162\fprq2 Arial Tur;}{\f67\fswiss\fcharset177\fprq2 Arial (Hebrew);}
{\f68\fswiss\fcharset178\fprq2 Arial (Arabic);}{\f69\fswiss\fcharset186\fprq2 Arial Baltic;}}{\colortbl;\red0\green0\blue0;\red0\green0\blue255;\red0\green255\blue255;\red0\green255\blue0;\red255\green0\blue255;\red255\green0\blue0;\red255\green255\blue0;
\red255\green255\blue255;\red0\green0\blue128;\red0\green128\blue128;\red0\green128\blue0;\red128\green0\blue128;\red128\green0\blue0;\red128\green128\blue0;\red128\green128\blue128;\red192\green192\blue192;}{\stylesheet{
\ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs24\lang2057\langfe1033\cgrid\langnp2057\langfenp1033 \snext0 Normal;}{
\s1\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f7\fs32\lang1030\langfe1033\kerning36\cgrid\langnp1044\langfenp1033 \sbasedon0 \snext1 heading 1;}{
\s2\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f7\fs28\lang1030\langfe1033\cgrid\langnp1044\langfenp1033 \sbasedon0 \snext2 heading 2;}{
\s3\ql \li0\ri0\sb240\sa60\keepn\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f1\fs26\lang2057\langfe1033\cgrid\langnp2057\langfenp1033 \sbasedon0 \snext0 heading 3;}{\*\cs10 \additive Default Paragraph Font;}}{\info
{\creatim\yr2002\mo1\dy20\hr8\min43}{\revtim\yr2002\mo1\dy20\hr13\min56}{\version4}{\edmins1}{\nofpages1}{\nofwords0}{\nofchars0}{\*\company Hiof}{\nofcharsws0}
{\vern8247}}\paperw11906\paperh16838\margl1417\margr1417\margt1417\margb1417 \widowctrl\ftnbj\aenddoc\noxlattoyen\expshrtn\noultrlspc\dntblnsbdb\nospaceforul\hyphcaps0\formshade\horzdoc\dgmargin\dghspace180\dgvspace180\dghorigin1417\dgvorigin1417\dghshow1
\dgvshow1\jexpand\viewkind1\viewscale100\pgbrdrhead\pgbrdrfoot\splytwnine\ftnlytwnine\htmautsp\nolnhtadjtbl\useltbaln\alntblind\lytcalctblwd\lyttblrtgr\lnbrkrule \fet0\sectd \linex0\headery708\footery708\colsx708\endnhere\sectlinegrid360\sectdefaultcl
{\*\pnseclvl1\pnucrm\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl2\pnucltr\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl3\pndec\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl4\pnlcltr\pnstart1\pnindent720\pnhang{\pntxta )}}{\*\pnseclvl5
\pndec\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl6\pnlcltr\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl7\pnlcrm\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl8\pnlcltr\pnstart1\pnindent720\pnhang
{\pntxtb (}{\pntxta )}}{\*\pnseclvl9\pnlcrm\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}
</xsl:text>
<xsl:value-of select="h1|dtb:h1"/>
</xsl:template>

<xsl:template name="H1PSTART">
<xsl:text>\pard\plain \s1\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\outlinelevel0\adjustright\rin0\lin0\itap0
\b\f7\fs32\lang1030\langfe1033\kerning36\cgrid\langnp1044\langfenp1033
{</xsl:text>
</xsl:template>

<xsl:template name="H2PSTART">
<xsl:text>\pard\plain \s2\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\outlinelevel1\adjustright\rin0\lin0\itap0
\b\f7\fs28\lang1030\langfe1033\cgrid\langnp1044\langfenp1033
{</xsl:text>
</xsl:template>


<xsl:template name="H3PSTART">
<xsl:text>\pard\plain \s3\ql \li0\ri0\sb240\sa60\keepn\widctlpar\aspalpha\aspnum\faauto\outlinelevel2\adjustright\rin0\lin0\itap0
\b\f1\fs26\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{</xsl:text>
</xsl:template>

<xsl:template name="H4PSTART">
<xsl:text>\pard\plain \s4\sb240\sa60\keepn\widctlpar\outlinelevel3\adjustright \b\f1\lang1030\cgrid
{</xsl:text>
</xsl:template>

<xsl:template name="H5PSTART">
<xsl:text>\pard\plain \s5\sb240\sa60\widctlpar\outlinelevel4\adjustright \f1\fs22\lang1030\cgrid
{</xsl:text>
</xsl:template>

<xsl:template name="H6PSTART">
<xsl:text>\pard\plain \s6\sb240\sa60\widctlpar\outlinelevel5\adjustright \i\fs22\lang1030\cgrid
{</xsl:text>
</xsl:template>



<xsl:template name="PPSTART">
<xsl:text>
\pard\plain \ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs24\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{
</xsl:text>
</xsl:template>

<xsl:template name="PPSTARTSTRONG">
<xsl:text>
\b
</xsl:text>
</xsl:template>

<xsl:template name="PPSTOPSTRONG">
<xsl:text>
\b0
</xsl:text>
</xsl:template>


<xsl:template name="PPSTARTITALIC">
<xsl:text>
\i
</xsl:text>
</xsl:template>

<xsl:template name="PPSTOPITALIC">
<xsl:text>
\i0
</xsl:text>
</xsl:template>

<xsl:template name="PSTOP">
<xsl:text>
\par }
</xsl:text>
</xsl:template>

<xsl:template name="NEWLINE">
<xsl:text>\line </xsl:text>
</xsl:template>

<xsl:template name="PAGENUM">
<xsl:text>
\pard\plain \ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs16 \i\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{
</xsl:text>
</xsl:template>

<xsl:template name="TABLESTART">
<xsl:text>\trowd \trgaph70\trleft-70\trbrdrt
\brdrs\brdrw10 \trbrdrl\brdrs\brdrw10 \trbrdrb\brdrs\brdrw10 \trbrdrr\brdrs\brdrw10 \trbrdrh\brdrs\brdrw10 \trbrdrv\brdrs\brdrw10
</xsl:text>
</xsl:template>

<xsl:template name="CELLNUMBER">
<xsl:text>\clvertalt\clbrdrt\brdrs\brdrw10 \clbrdrl\brdrs\brdrw10 \clbrdrb\brdrs\brdrw10 \clbrdrr\brdrs\brdrw10 \cltxlrtb \cellx</xsl:text>
</xsl:template>

<xsl:template name="CELLSTOP">
<xsl:text>\pard\plain \widctlpar\intbl\adjustright \lang1030 \f1\fs22\cgrid {</xsl:text>
</xsl:template>



<xsl:template name="CELLINDHOLD">
<xsl:text>\cell </xsl:text>
</xsl:template>

<xsl:template name="TABLESTOP">
<xsl:text>} \pard \widctlpar\intbl\adjustright {\row }</xsl:text>
</xsl:template>

<xsl:template name="TABLEEND">
<xsl:text>\pard \widctlpar\adjustright {</xsl:text>
</xsl:template>

<xsl:template name="LIST">
<xsl:text>{\pntext\pard\plain\f3\fs22\lang1030\cgrid \loch\af3\dbch\af0\hich\f3 \'b7\tab}
\pard\plain \fi-360\li360\widctlpar\jclisttab\tx360{\*\pn \pnlvlblt\ilvl0\ls1\pnrnot0\pnf3\pnstart1\pnindent360\pnhang{\pntxtb \'b7}}\ls1\adjustright \f1\fs22\cgrid{</xsl:text>
</xsl:template>

<xsl:template name="OBJECTSTART">
<xsl:text>{\object\objemb\objw720\objh720{\*\objdata</xsl:text>
</xsl:template>


<xsl:template name="OBJECTSTOP">
<xsl:text>}}}\par</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKSTART">
<xsl:text>\pard\plain \s15\sb240\sa120\widctlpar
\tqr\tx9062\adjustright \b\fs20\lang1030\cgrid{</xsl:text>
</xsl:template>



<xsl:template name="BOGMARKMID">
<xsl:text>}{\field{\*\fldinst {PAGEREF _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKEND">
<xsl:text>\\h }</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKSTART2">
<xsl:text>\pard\plain \adjustright \fs18\lang1030\cgrid{</xsl:text>
</xsl:template>


<xsl:template name="BOGMARKREFSTART">
<xsl:text>{\*\bkmkstart _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFSTARTEND">
<xsl:text>}</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFEND">
<xsl:text>{\*\bkmkend _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFENDEND">
<xsl:text>}</xsl:text>
</xsl:template>

<!--
RTF is a 8-bit format. That would limit it to ASCII, but RTF can encode characters 
beyond ASCII by escape sequences. The character escapes are of two types: code page 
escapes and Unicode escapes. In a code page escape, two hexadecimal digits following 
an apostrophe are used for denoting a character taken from a Windows code page. 

For example, if control codes specifying Windows-1256 are present, the sequence 
\'c8 will encode the Arabic letter beh (ب).

If a Unicode escape is required, the control word \u is used, followed by a 16-bit 
signed decimal integer giving the Unicode codepoint number. For the benefit of programs 
without Unicode support, this must be followed by the nearest representation of this 
character in the specified code page. For example, \u1576? would give the Arabic letter beh, 
specifying that older programs which do not have Unicode support should render it as a 
question mark instead.

The control word \uc0 can be used to indicate that subsequent Unicode escape sequences 
within the current group do not specify a substitution character.
-->

<xsl:template name="rtf-encode">
	<xsl:param name="str"/>
	<xsl:if test="$str">
		<xsl:for-each select="string-to-codepoints($str)">
			<xsl:choose>
				<xsl:when test=". = 92">\\</xsl:when>
				<xsl:when test=". = 123">\{</xsl:when>
				<xsl:when test=". = 125">\}</xsl:when>
				<xsl:when test=". &lt; 160">
					<xsl:value-of select="codepoints-to-string(.)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('\u',.,'?')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:if>
</xsl:template>
	<!-- =========================================================================
dtbook
	============================================================================== -->
	<xsl:template match="dtbook|dtb:dtbook">		
		<xsl:apply-templates/>
	</xsl:template>	
	<!-- =========================================================================
head
	============================================================================== -->	
	<xsl:template match="head|dtb:head">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
title
	============================================================================== -->	
	<xsl:template match="title|dtb:title">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
link
	============================================================================== -->	
	<xsl:template match="link|dtb:link">			
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
meta
	============================================================================== -->	
	<xsl:template match="meta|dtb:meta">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
style
	============================================================================== -->	
	<xsl:template match="style|dtb:style">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
book
	============================================================================== -->	
	<xsl:template match="book|dtb:book">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
level
	============================================================================== -->	
	<xsl:template match="level|dtb:level">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
linenum
	============================================================================== -->	
	<xsl:template match="linenum|dtb:linenum">	
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:text> </xsl:text>				
	</xsl:template>	
	<!-- =========================================================================
adress
	============================================================================== -->	
	<xsl:template match="address|dtb:address">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
author
	============================================================================== -->	
	<xsl:template match="author|dtb:author">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
notice
	============================================================================== -->	
	<xsl:template match="notice|dtb:notice">	
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
note
	============================================================================== -->	
	<xsl:template match="note|dtb:note">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
annotation
	============================================================================== -->	
	<xsl:template match="annotation|dtb:annotation">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
line
	============================================================================== -->	
	<xsl:template match="line|dtb:line">	
	    <xsl:call-template name="PPSTART"/>
		<xsl:apply-templates/>				
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
a
	============================================================================== -->	
	<xsl:template match="a|dtb:a">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
strong
	============================================================================== -->	
	<xsl:template match="strong|dtb:strong">					
		<xsl:call-template name="PPSTARTSTRONG"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPSTRONG"/>
	</xsl:template>	
	<!-- =========================================================================
em
	============================================================================== -->	
	<xsl:template match="em|dtb:em">
		<xsl:call-template name="PPSTARTITALIC"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPITALIC"/>
	</xsl:template>	
	<!-- =========================================================================
dfn
	============================================================================== -->	
	<xsl:template match="dfn|dtb:dfn">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
kbd
	============================================================================== -->	
	<xsl:template match="kbd|dtb:kbd">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
code
	============================================================================== -->	
	<xsl:template match="code|dtb:code">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
samp
	============================================================================== -->	
	<xsl:template match="samp|dtb:samp">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
cite
	============================================================================== -->	
	<xsl:template match="cite|dtb:cite">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
abbr
	============================================================================== -->	
	<xsl:template match="abbr|dtb:abbr">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
acronym
	============================================================================== -->	
	<xsl:template match="acronym|dtb:acronym">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sub
	============================================================================== -->	
	<xsl:template match="sub|dtb:sub">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sup
	============================================================================== -->	
	<xsl:template match="sup|dtb:sup">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
span
	============================================================================== -->	
	<xsl:template match="span|dtb:span">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
bdo
	============================================================================== -->	
	<xsl:template match="bdo|dtb:bdo">			
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sent
	============================================================================== -->	
	<xsl:template match="sent|dtb:sent">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
w
	============================================================================== -->	
	<xsl:template match="w|dtb:w">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
noteref
	============================================================================== -->	
	<xsl:template match="noteref|dtb:noteref">		
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
annoref
	============================================================================== -->	
	<xsl:template match="annoref|dtb:annoref">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
q
	============================================================================== -->	
	<xsl:template match="q|dtb:q">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
img
	============================================================================== -->	
	<xsl:template match="img|dtb:img">
	<!--<xsl:call-template name="OBJECTSTART"/>	-->
		<xsl:apply-templates/>	
	<!--<xsl:call-template name="OBJECTSTOP"/> -->
	</xsl:template>	
	<!-- =========================================================================
imggroup
	============================================================================== -->	
	<xsl:template match="imggroup|dtb:imggroup">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
hr
	============================================================================== -->	
	<xsl:template match="hr|dtb:hr">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
levelhd
	============================================================================== -->	
	<xsl:template match="levelhd|dtb:levelhd">	
		<xsl:choose>	
			<xsl:when test="@depth='1'">		
				<xsl:call-template name="H1PSTART"/>	
				<xsl:call-template name="BOGMARKREFSTART"/>	
				<xsl:value-of select="count(preceding::level)+300"/>	
				<xsl:call-template name="BOGMARKREFSTARTEND"/>	
				<xsl:apply-templates/>	
				<xsl:call-template name="BOGMARKREFEND"/>	
				<xsl:value-of select="count(preceding::level)+300"/>	
				<xsl:call-template name="BOGMARKREFENDEND"/>	
				<xsl:call-template name="PSTOP"/>	
			</xsl:when>	
			<xsl:when test="@depth='2'">	
				<xsl:call-template name="H2PSTART"/>	
				<xsl:call-template name="BOGMARKREFSTART"/>	
				<xsl:value-of select="count(preceding::level)+2000000"/>	
				<xsl:call-template name="BOGMARKREFSTARTEND"/>
				<xsl:apply-templates/>	
				<xsl:call-template name="BOGMARKREFEND"/>	
				<xsl:value-of select="count(preceding::level)+2000000"/>	
				<xsl:call-template name="BOGMARKREFENDEND"/>	
				<xsl:call-template name="PSTOP"/>	
			</xsl:when>
			<xsl:when test="@depth='3'">	
				<xsl:call-template name="H3PSTART"/>
				<xsl:apply-templates/>				
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='4'">	
				<xsl:call-template name="H4PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='5'">	
				<xsl:call-template name="H5PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='6'">	
				<xsl:call-template name="H6PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
		</xsl:choose>	
	</xsl:template>	
	<!-- =========================================================================
h1
	============================================================================== -->	
	<xsl:template match="h1|dtb:h1">	
		<xsl:call-template name="H1PSTART"/>	
		<xsl:call-template name="BOGMARKREFSTART"/>	
		<xsl:value-of select="count(preceding::level1)"/>	
		<xsl:call-template name="BOGMARKREFSTARTEND"/>
		<xsl:apply-templates/>	
		<xsl:call-template name="BOGMARKREFEND"/>	
		<xsl:value-of select="count(preceding::level1)"/>	
		<xsl:call-template name="BOGMARKREFENDEND"/>	
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
h2
	============================================================================== -->	
	<xsl:template match="h2|dtb:h2">	
		<xsl:call-template name="H2PSTART"/>	
		<xsl:call-template name="BOGMARKREFSTART"/>	
		<xsl:value-of select="count(preceding::level2)+1000000"/>	
		<xsl:call-template name="BOGMARKREFSTARTEND"/>
		<xsl:apply-templates/>	
		<xsl:call-template name="BOGMARKREFEND"/>	
		<xsl:value-of select="count(preceding::level2)+1000000"/>	
		<xsl:call-template name="BOGMARKREFENDEND"/>	
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h3
	============================================================================== -->	
	<xsl:template match="h3|dtb:h3">	
		<xsl:call-template name="H3PSTART"/>
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h4
	============================================================================== -->	
	<xsl:template match="h4|dtb:h4">	
		<xsl:call-template name="H4PSTART"/>
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h5
	============================================================================== -->	
	<xsl:template match="h5|dtb:h5">	
		<xsl:call-template name="H5PSTART"/>	
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h6
	============================================================================== -->	
	<xsl:template match="h6|dtb:h6">	
		<xsl:call-template name="H6PSTART"/>	
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
blockqoute
	============================================================================== -->	
	<xsl:template match="blockquote|dtb:blockquote">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
dl
	============================================================================== -->	
	<xsl:template match="dl|dtb:dl">		
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>					
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
dt
	============================================================================== -->	
	<xsl:template match="dt|dtb:dt">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
dd
	============================================================================== -->	
	<xsl:template match="dd|dtb:dd">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
list
	============================================================================== -->	
	<xsl:template match="list|dtb:list">
		<xsl:call-template name="NEWLINE"/>
		<xsl:for-each select="li|dtb:li">
			<xsl:call-template name="LIST"/>	
			<xsl:apply-templates/>	
			<xsl:call-template name="PSTOP"/>						
		</xsl:for-each>						
		<xsl:call-template name="NEWLINE"/>
	</xsl:template>	
	<!-- =========================================================================
li
	============================================================================== -->	
	<xsl:template match="li|dtb:li">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
lic
	============================================================================== -->	
	<xsl:template match="lic|dtb:lic">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
doctitle
	============================================================================== -->	
	<xsl:template match="doctitle|dtb:doctitle">		
	    <xsl:call-template name="H1PSTART"/>
			<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
docauthor
	============================================================================== -->	
	<xsl:template match="docauthor|dtb:docauthor">
		<xsl:call-template name="H2PSTART"/>		
			<xsl:apply-templates/>	
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>		
	<!-- =========================================================================
br
	============================================================================== -->	
	<xsl:template match="br|dtb:br">		
		<xsl:call-template name="NEWLINE"/>
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
table
	============================================================================== -->	
	<xsl:template match="table|dtb:table">
		<xsl:choose>
			<xsl:when test="child::tbody | child::dtb:tbody">	
				<xsl:for-each select="tbody|dtb:tbody">
					<xsl:for-each select="tr|dtb:tr">
						<xsl:call-template name="TR"/>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:for-each select="tr|dtb:tr">
					<xsl:call-template name="TR"/>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="TABLEEND"/>	
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	
	<xsl:template name="TR">
		<xsl:variable name="tables" select="9000 div count(td | th | dtb:td |dtb:th)"/>
		<xsl:call-template name="TABLESTART"/>			
		<xsl:for-each select="td | th | dtb:td | dtb:th">			
			<xsl:call-template name="CELLNUMBER"/>			
			<xsl:value-of select="$tables*position()"/>			
		</xsl:for-each>			
		<xsl:call-template name="CELLSTOP"/>			
		<xsl:for-each select="td | th | dtb:td | dtb:th ">			
			<xsl:apply-templates/>				
			<xsl:call-template name="CELLINDHOLD"/>					
		</xsl:for-each>	
		<xsl:call-template name="TABLESTOP"/>
	</xsl:template>
	<!-- =========================================================================
tbody
	============================================================================== -->	
	<xsl:template match="tbody|dtb:tbody">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
tr
============================================================================== -->	
	<xsl:template match="tr|dtb:tr">	
	
		<xsl:apply-templates/>	
		
	</xsl:template>	
	<!-- =========================================================================
td
============================================================================== -->	
	<xsl:template match="td|dtb:td">		
		<xsl:apply-templates/>			
	</xsl:template>	
	<!-- =========================================================================
caption
	============================================================================== -->	
	<xsl:template match="caption|dtb:caption">	
	    <xsl:call-template name="PPSTARTITALIC"/>	    
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPITALIC"/>	
		<xsl:call-template name="NEWLINE"/>
	</xsl:template>	
	<!-- =========================================================================
thead
	============================================================================== -->	
	<xsl:template match="thead|dtb:thead">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
tfoot
	============================================================================== -->	
	<xsl:template match="tfoot|dtb:tfoot">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
colgroup
	============================================================================== -->	
	<xsl:template match="colgroup|dtb:colgroup">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
col
	============================================================================== -->	
	<xsl:template match="col|dtb:col">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
th
	============================================================================== -->	
	<xsl:template match="th|dtb:th">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
p
	============================================================================== -->	
	<xsl:template match="p|dtb:p">
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
prodnote
	============================================================================== -->	
	<xsl:template match="prodnote|dtb:prodnote">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sidebar
	============================================================================== -->	
	<xsl:template match="sidebar|dtb:sidebar">
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
div
	============================================================================== -->	
	<xsl:template match="div|dtb:div">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
pagenum
	============================================================================== -->	
	<xsl:template match="pagenum|dtb:pagenum">
		<xsl:call-template name="PAGENUM"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>
	</xsl:template>
	<!-- =========================================================================
text
	============================================================================== -->	
	<xsl:template match="text()">
		<xsl:choose>	
			<xsl:when test="parent::title | parent::dtb:title">	</xsl:when>	
			<xsl:when test="parent::th | parent::dtb:th">
				<xsl:call-template name="PPSTARTSTRONG"/>
				<xsl:call-template name="rtf-encode">
					<xsl:with-param name="str" select="."/>
				</xsl:call-template>
				<xsl:call-template name="PPSTOPSTRONG"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="rtf-encode">
					<xsl:with-param name="str" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
