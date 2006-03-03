<?xml version="1.0" encoding="utf-8"?>

<xsl:transform version="1.0" 
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:n="http://www.daisy.org/z3986/2005/ncx/"
               xmlns:s="http://www.w3.org/2001/SMIL20/"
               xmlns:c="http://daisymfc.sf.net/xslt/config"
               xmlns:d="http://www.daisy.org/z3986/2005/dtbook/"
               exclude-result-prefixes="n s c d">

  <c:config>
  	<c:generator>DMFC z3986-2005 to Daisy 2.02</c:generator>
    <c:name>smil2smil</c:name>
    <c:version>0.2</c:version>
    
    <c:author>Linus Ericson</c:author>
    <c:description>Converts a z3986 SMIL file to a Daisy 2.02 SMIL file.</c:description>    
  </c:config>

  <!--
   ASSUMED:
    - noteref in smil must be immediately followed by note
    
   NOTES:
    - dur attribute on base seq is fixed later
    - ncc:totalElapsedTime and ncc:timeInThisSmil are fixed later
   -->

  <xsl:param name="xhtml_document">content.html</xsl:param>
  <xsl:param name="ncx_document"/>
  <xsl:param name="baseDir"/>

<!--
  <xsl:output method="xml" 
	      encoding="utf-8" 
	      indent="yes" 
	      doctype-public="-//W3C//DTD SMIL 1.0//EN" 
	      doctype-system="http://www.w3.org/TR/REC-SMIL/SMIL10.dtd"
	/>
-->
  <xsl:output method="xml" 
	      encoding="utf-8" 
	      indent="yes" 	 
	/>

  <!-- ****************************************************************
       Root template
       **************************************************************** -->
  <xsl:template match="/">
    <smil>
      <head>        
        <xsl:call-template name="metadata"/>
        <layout>
          <region id="txtView"/>
        </layout>
      </head>
      <body>
        <seq dur="FIXME">
          <xsl:apply-templates/>
        </seq>
      </body>
    </smil>
  </xsl:template>


  <!-- ****************************************************************
       Meatadata elements 
       **************************************************************** -->
  <xsl:template name="metadata">
    <meta name="ncc:generator">
      <xsl:attribute name="content">
	      <xsl:value-of select="document('')//c:config/c:generator"/>
	      <xsl:text> (</xsl:text>
	      <xsl:value-of select="document('')//c:config/c:name"/>
	      <xsl:text> v</xsl:text>
	      <xsl:value-of select="document('')//c:config/c:version"/>
	      <xsl:text>)</xsl:text>
      </xsl:attribute>
    </meta>
    <meta name="dc:format" content="Daisy 2.02"/>
    <meta name="dc:identifier">
      <xsl:attribute name="content">
        <xsl:value-of select="/s:smil/s:head/s:meta[@name='dtb:uid']/@content"/>
      </xsl:attribute>
    </meta>
    <xsl:variable name="sectionTitle">
    	<xsl:call-template name="get_current_heading">
    		<xsl:with-param name="uri" select="(//s:text/@src)[1]"/>
    	</xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($sectionTitle='')">
	    <meta name="title">
	      <xsl:attribute name="content"><xsl:value-of select="$sectionTitle"/></xsl:attribute>
	    </meta> 
    </xsl:if>
    <xsl:variable name="docTitle">
    	<xsl:call-template name="get_book_title"/>
    </xsl:variable>
    <xsl:if test="not($docTitle='')">
	    <meta name="dc:title">
	      <xsl:attribute name="content"><xsl:value-of select="$docTitle"/></xsl:attribute>
	    </meta> 
    </xsl:if>
    <!--
    <meta name="ncc:totalElapsedTime">
      <xsl:attribute name="content">
        <xsl:value-of select="/s:smil/s:head/s:meta[@name='dtb:totalElapsedTime']/@content"/>
      </xsl:attribute>
    </meta>
    -->
    <meta name="ncc:totalElapsedTime">
      <xsl:attribute name="content">FIXME</xsl:attribute>
    </meta>
    <meta name="ncc:timeInThisSmil">
      <xsl:attribute name="content">FIXME</xsl:attribute>
    </meta>
  </xsl:template>


  <!-- ****************************************************************
       Audio element
       **************************************************************** -->
  <xsl:template match="s:audio" mode="inPar">
    <audio>
      <xsl:attribute name="clip-begin">
        <xsl:value-of select="@clipBegin"/>
      </xsl:attribute>
      <xsl:attribute name="clip-end">
        <xsl:value-of select="@clipEnd"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
          	<xsl:text>aud</xsl:text>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:copy-of select="@src"/>      
    </audio>
  </xsl:template>


  <!-- ****************************************************************
       Text element
       **************************************************************** -->
  <xsl:template match="s:text" mode="inPar">
    <text>
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
          	<xsl:text>txt</xsl:text>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="src">
        <xsl:value-of select="$xhtml_document"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="substring-after(@src, '#')"/>
      </xsl:attribute>
    </text>
  </xsl:template>


  <!-- ****************************************************************
       Par element
       **************************************************************** -->
  <xsl:template match="s:par[s:text|s:audio]">
  	<xsl:variable name="isPagenum">
  		<xsl:call-template name="get_system_required">
  			<xsl:with-param name="customTest" select="@customTest"/>
  		</xsl:call-template>
  	</xsl:variable>
  	<xsl:choose>
  		<!-- is this a pagenum? In that case we set the system-required=pagenumber-on
  		     even though it's wrapped inside another skippable structure -->
  		<xsl:when test="$isPagenum='pagenumber-on'">
  			<par id="{@id}" endsync="last" system-required="pagenumber-on">
		      <xsl:apply-templates mode="inPar"/>
	      </par>
  		</xsl:when>
  		<!-- does a parent have a customTest attribute? -->
  		<xsl:when test="ancestor::s:seq/@customTest">
  			<xsl:variable name="systemRequired">
	      	<xsl:call-template name="get_system_required">
	      		<xsl:with-param name="customTest" select="(ancestor::s:seq/@customTest)[1]"/>
	      	</xsl:call-template>
	      </xsl:variable>
	      <par endsync="last">
	      	<xsl:copy-of select="@id"/>
		      <xsl:if test="$systemRequired!=''">
		       	<xsl:attribute name="system-required">
		       		<xsl:value-of select="$systemRequired"/>
		       	</xsl:attribute>
		      </xsl:if>
		      <xsl:apply-templates mode="inPar"/>
	      </par>
  		</xsl:when>
  		<!-- does this element have a customTest attribute? -->
  		<xsl:when test="@customTest">
  			<xsl:variable name="systemRequired">
      		<xsl:call-template name="get_system_required">
      			<xsl:with-param name="customTest" select="@customTest"/>
      		</xsl:call-template>
      	</xsl:variable>
      	<xsl:variable name="isNoteref">
      		<xsl:call-template name="is_noteref">
      			<xsl:with-param name="customTest" select="@customTest"/>
      		</xsl:call-template>
      	</xsl:variable>
      	<xsl:choose>
      		<!-- is the customTest something that should be skippable in Daisy 2.02 smil? -->
	        <xsl:when test="$systemRequired!=''">
	        	<par endsync="last">
	        		<xsl:copy-of select="@id"/>
		        	<xsl:attribute name="system-required">
		        		<xsl:value-of select="$systemRequired"/>
		        	</xsl:attribute>
		        	<xsl:apply-templates mode="inPar"/>
	        	</par>
	        </xsl:when>
	        <!-- is it a noteref? is so, wrap note reference and note body in a seq -->
	        <xsl:when test="$isNoteref='yes'">
	        	<seq>
	        		<xsl:comment>Note reference</xsl:comment>
	        		<par endsync="last">
	        			<xsl:copy-of select="@id"/>
	        			<xsl:apply-templates mode="inPar"/>
	        		</par>
	        		<xsl:comment>Note body</xsl:comment>
	        		<xsl:apply-templates select="following::*[1]" mode="noteOnly"/>
	        	</seq>
	        </xsl:when>
	        <!-- just some unknown customTest. ignore it -->
	        <xsl:otherwise>
	        	<par endsync="last">
	        		<xsl:copy-of select="@id"/>
	        		<xsl:apply-templates mode="inPar"/>
	        	</par>
	        </xsl:otherwise>
        </xsl:choose>
  		</xsl:when>
  		<xsl:otherwise>
  			<!-- No, no customTest -->
      	<par endsync="last">
      		<xsl:copy-of select="@id"/>
      		<xsl:apply-templates mode="inPar"/>
      	</par>
      </xsl:otherwise>
  	</xsl:choose>  
  </xsl:template>
  
  
  <!-- ****************************************************************
       merge the contents of a note into a single par
       
       This template should only be applied to the first following
       sibling of a noteref. If this is a note, a single par is created,
       all text elements inside the note (could be a lot of pars and
       seqs) are thrown away, and a new text element pointing to the
       parent note in the dtbook file is created. All audio elements
       are merged into a single seq.
       **************************************************************** -->
  <xsl:template match="s:seq" mode="noteOnly">
  	<!-- is this a note? -->
  	<xsl:if test="@customTest">
  			<xsl:variable name="systemRequired">
      		<xsl:call-template name="get_system_required">
      			<xsl:with-param name="customTest" select="@customTest"/>
      		</xsl:call-template>
      	</xsl:variable>
      	<xsl:if test="$systemRequired='footnote-on'">
      		<!-- yes, this is a note -->
        	<par endsync="last" system-required="footnote-on">
        		<xsl:copy-of select="@id"/>
        		<text>
        			<xsl:attribute name="id">
        				<xsl:text>txt</xsl:text>
        				<xsl:value-of select="generate-id()"/>
        			</xsl:attribute>
        			<xsl:attribute name="src">
        				<xsl:value-of select="$xhtml_document"/>
				        <xsl:text>#</xsl:text>
				        <xsl:call-template name="get_id_of_note_in_dtbook">
				        	<xsl:with-param name="subElementUri" select="descendant::s:text[1]/@src"/>
				        </xsl:call-template>
        			</xsl:attribute>
        		</text>
        		<xsl:choose>
        			<xsl:when test="count(descendant::s:audio)=1">
        				<xsl:apply-templates select="descendant::s:audio" mode="inPar"/>
        			</xsl:when>
        			<xsl:otherwise>
        				<seq>
	        				<xsl:apply-templates select="descendant::s:audio" mode="inPar"/>
			        	</seq>
        			</xsl:otherwise>
        		</xsl:choose>        		
        	</par>
        </xsl:if>
     </xsl:if>
  </xsl:template>


	<!-- ****************************************************************
       get the ID of a parent note element in DTBook
       
       Gets the ID of a parent note element in the DTBook file given
       an ID of a sub-element (such as a sent in the note).
       **************************************************************** -->
	<xsl:template name="get_id_of_note_in_dtbook">
		<xsl:param name="subElementUri"/>
		<xsl:variable name="dtbook" select="substring-before($subElementUri, '#')"/>
		<xsl:variable name="fragment" select="substring-after($subElementUri, '#')"/>
						
		<xsl:value-of select="document(concat($baseDir,$dtbook))//d:note[descendant-or-self::*[@id=$fragment]]/@id"/>		
	</xsl:template>


  <!-- ****************************************************************
       seq (not in par) element
       
       if this is a note, and the preceding element is a noteref,
       we skip this element because the note has already been inserted
       in the result tree when the noteref was found.
       **************************************************************** -->
  <xsl:template match="s:seq">
  	<!-- note? -->
  	<xsl:variable name="isNote">
  		<xsl:call-template name="is_note">
  			<xsl:with-param name="customTest" select="@customTest"/>
  		</xsl:call-template>
  	</xsl:variable>
  	<xsl:choose>
  		<xsl:when test="$isNote='yes'">
  			<!-- yes, a note. sibling(child) a noteref? -->
  			<xsl:variable name="isSiblingNoteref">
  				<xsl:call-template name="is_noteref">
  					<xsl:with-param name="customTest" select="preceding-sibling::*[1][self::s:par]/@customTest"/>
  				</xsl:call-template>
  			</xsl:variable>
  			<xsl:variable name="isSiblingChildNoteref">
  				<xsl:call-template name="is_noteref">
  					<xsl:with-param name="customTest" select="preceding-sibling::*[1][self::s:a]/s:par/@customTest"/>
  				</xsl:call-template>
  			</xsl:variable>  			
  			<xsl:choose>
  				<xsl:when test="$isSiblingChildNoteref='yes'">
  					<!-- yes, siblingChild is a noteref -->
  				</xsl:when>
  				<xsl:when test="$isSiblingNoteref='yes'">
  					<!-- yes, sibling is a noteref -->
  				</xsl:when>
  				<xsl:otherwise>
  					<!-- no, sibling(child) is not a noteref -->
  					<xsl:apply-templates/>
  				</xsl:otherwise>
  			</xsl:choose>
  		</xsl:when>
  		<xsl:otherwise>
  			<!-- no, no note -->  			
  			<xsl:apply-templates/>
  		</xsl:otherwise>
  	</xsl:choose>  	  	
  </xsl:template>


  <!-- ****************************************************************
       Get system-required property
       
       Get the value to use in the system-required attribute, given a
       customTest attribute value. The result can becomputed  in two
       ways: if a ncx document is defined (preferred), the bookStruct
       in the ncx is used, otherwise the value of the customTest itself
       is used to make a guess...
       **************************************************************** -->
  <xsl:template name="get_system_required">
  	<xsl:param name="customTest"/>
  	<xsl:choose>
  		<xsl:when test="$ncx_document">
  			<xsl:variable name="bookStruct">
  				<xsl:value-of select="document($ncx_document)//*[@id=$customTest]/@bookStruct"/>
  			</xsl:variable>
  			<xsl:choose>
  				<xsl:when test="$bookStruct='OPTIONAL_SIDEBAR'">sidebar-on</xsl:when>
  				<xsl:when test="$bookStruct='OPTIONAL_PRODUCER_NOTE'">prodnote-on</xsl:when>
  				<xsl:when test="$bookStruct='NOTE'">footnote-on</xsl:when>
  				<xsl:when test="$bookStruct='PAGE_NUMBER'">pagenumber-on</xsl:when>
  			</xsl:choose>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:message>smil2smil: No NCX defined. Using heuristics to find system-required properties.</xsl:message>
  			<xsl:choose>
  				<xsl:when test="$customTest='sidebar'">sidebar-on</xsl:when>
  				<xsl:when test="$customTest='prodnote'">prodnote-on</xsl:when>
  				<xsl:when test="$customTest='note'">footnote-on</xsl:when>
  				<xsl:when test="$customTest='pagenum'">pagenumber-on</xsl:when>
  			</xsl:choose>
  		</xsl:otherwise>
  	</xsl:choose>  	
  </xsl:template>
  
  
  <!-- ****************************************************************
       is this a noteref?
       
       Checks if the given customTest argument belongs to a noteref.
       Same algorithm as in the get_system_required template.
       **************************************************************** -->
  <xsl:template name="is_noteref">
  	<xsl:param name="customTest"/>
  	<xsl:choose>
  		<xsl:when test="not($customTest)">no</xsl:when>
  		<xsl:when test="$ncx_document">
  			<xsl:variable name="bookStruct">
  				<xsl:value-of select="document($ncx_document)//*[@id=$customTest]/@bookStruct"/>
  			</xsl:variable>
  			<xsl:choose>
  				<xsl:when test="$bookStruct='NOTE_REFERENCE'">yes</xsl:when>
  				<xsl:otherwise>no</xsl:otherwise>
  			</xsl:choose>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:message>smil2smil: No NCX defined. Using heuristics to find if is_noteref.</xsl:message>
  			<xsl:choose>
  				<xsl:when test="$customTest='noteref'">yes</xsl:when>
  				<xsl:otherwise>no</xsl:otherwise>
  			</xsl:choose>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
  
  <!-- ****************************************************************
       is this a note?
       
       Checks if the given customTest argument belongs to a noteref.
       Same algorithm as in the get_system_required template.
       **************************************************************** -->
  <xsl:template name="is_note">
  	<xsl:param name="customTest"/>
  	<xsl:choose>
  		<xsl:when test="not($customTest)">no</xsl:when>
  		<xsl:when test="$ncx_document">
  			<xsl:variable name="bookStruct">
  				<xsl:value-of select="document($ncx_document)//*[@id=$customTest]/@bookStruct"/>
  			</xsl:variable>
  			<xsl:choose>
  				<xsl:when test="$bookStruct='NOTE'">yes</xsl:when>
  				<xsl:otherwise>no</xsl:otherwise>
  			</xsl:choose>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:message>smil2smil: No NCX defined. Using heuristics to find if is_note.</xsl:message>
  			<xsl:choose>
  				<xsl:when test="$customTest='note'">yes</xsl:when>
  				<xsl:otherwise>no</xsl:otherwise>
  			</xsl:choose>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>


	<!-- ****************************************************************
       get name of the current heading in DTBook
       
       Gets the name of the current heading in the DTBook file.
       **************************************************************** -->
	<xsl:template name="get_current_heading">
		<xsl:param name="uri"/>
		<xsl:variable name="dtbook" select="substring-before($uri, '#')"/>
		<xsl:variable name="fragment" select="substring-after($uri, '#')"/>
						
		<xsl:value-of select="document(concat($baseDir,$dtbook))//*[self::d:h1 or self::d:h2 or self::d:h3 or self::d:h4 or self::d:h5 or self::d:h6 or (self::d:hd and parent::d:level)][descendant-or-self::*[@id=$fragment]]"/>
	</xsl:template>
	
	
	<!-- ****************************************************************
       get title of book from NCX
       
       Gets the title of the book from the NCX document
       **************************************************************** -->
	<xsl:template name="get_book_title">
		<xsl:if test="$ncx_document">
			<xsl:value-of select="document($ncx_document)//n:docTitle/n:text"/>
		</xsl:if>
	</xsl:template>

</xsl:transform>

