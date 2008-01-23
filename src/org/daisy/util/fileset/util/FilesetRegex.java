/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.fileset.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>A singleton source for common and <em>compiled</em> regex patterns.</p>
 * <p>Usage example:</p>
 * <code><pre>
 *   if (Regex.getInstance().matches(Regex.getInstance().URI_REMOTE), string)) {
 *     //match
 *   }		    
 * </pre></code>
 * @author Markus Gylling
 */
public class FilesetRegex {
	
	private FilesetRegex(){
		URI_REMOTE = Pattern.compile("(^http:.+)|(^https:.+)|(^ftp:.+)|(^mailto:.+)|(^gopher:.+)|(^news:.+)|(^nntp:.+)|(^rtsp:.+)|(^bundleresource:.+)");		
		URI_SMIL_FILE_WITH_FRAGMENT = Pattern.compile(".+\\.[Ss][Mm][Ii][Ll]#.+");
		URI_WITH_FRAGMENT = Pattern.compile(".+\\..+#.+");  
		
		URL_JAR = Pattern.compile("^jar:.+");
		
		FILE_SMIL = Pattern.compile(".+\\.[Ss][Mm][Ii][Ll]$");  
		FILE_CSS = Pattern.compile(".+\\.[Cc][Ss][Ss]$");
		FILE_XSL = Pattern.compile(".+\\.[Xx][Ss][Ll][Tt]?$");
		FILE_NCC = Pattern.compile("[Nn][Cc][Cc].[Hh][Tt][Mm][Ll]?$");
		FILE_OPF = Pattern.compile(".+\\.[Oo][Pp][Ff]$");
		FILE_AUDIO = Pattern.compile("(.+\\.[Mm][Pp]3$)|(.+\\.[Ww][Aa][Vv]$)|(.+\\.[Mm][Pp][2]$)");
		FILE_MP3 = Pattern.compile(".+\\.[Mm][Pp]3$");
		FILE_MP2 = Pattern.compile(".+\\.[Mm][Pp]2$");		
		FILE_WAV = Pattern.compile(".+\\.[Ww][Aa][Vv]$");
		FILE_DTD = Pattern.compile("(.+\\.[Dd][Tt][Dd]$)|(.+\\.[Mm][Oo][Dd]$)|.+\\.[Ee][Nn][Tt]$");
		FILE_XHTML = Pattern.compile(".+\\.[Xx]?[Hh][Tt][Mm][Ll]?$");
		FILE_JPG = Pattern.compile("(.+\\.[Jj][Pp][Gg]$)|(.+\\.[Jj][Pp][Ee][Gg]$)");
		FILE_GIF = Pattern.compile(".+\\.[Gg][Ii][Ff]$");
		FILE_PNG = Pattern.compile(".+\\.[Pp][Nn][Gg]$");
		FILE_BMP = Pattern.compile(".+\\.[Bb][Mm][Pp]$");
// jpritchett@rfbd.org:  Added SVG
		FILE_SVG = Pattern.compile(".+\\.[Ss][Vv][Gg]$");
		FILE_IMAGE = Pattern.compile( "(.+\\.[Jj][Pp][Gg]$)|(.+\\.[Jj][Pp][Ee][Gg]$)|(.+\\.[Pp][Nn][Gg]$)|(.+\\.[Gg][Ii][Ff]$)|(.+\\.[Bb][Mm][Pp]$)");
		FILE_NCX = Pattern.compile(".+\\.[Nn][Cc][Xx]$");
		FILE_RESOURCE = Pattern.compile(".+\\.[Rr][Ee][Ss]$");
		FILE_DTBOOK = Pattern.compile(".+\\.[Xx][Mm][Ll]$");
		FILE_XML = Pattern.compile(".+\\.[Xx][Mm][Ll]$");
		FILE_M3U = Pattern.compile(".+\\.[Mm][3][Uu]$");
		FILE_PLS = Pattern.compile(".+\\.[Pp][Ll][Ss]$");
		FILE_PDF = Pattern.compile(".+\\.[Pp][Dd][Ff]$");
		FILE_EPUB = Pattern.compile(".+\\.[Ee][Pp][Uu][Bb]$");
		
		XHTML_ELEMENTS_WITH_URI_ATTRS = Pattern.compile("(^a$)|(^link$)|(^img$)");
		XHTML_ATTRS_WITH_URIS = Pattern.compile("(^href$)|(^src$)"); 
		XHTML_HEADING_ELEMENT = Pattern.compile("^h[1-6]$");
		
		SMIL_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^text$)|(^audio$)|(^img$)|(^a$)");
		SMIL_ATTRIBUTES_WITH_URIS = Pattern.compile("(^href$)|(^src$)");
		
		CSS_PROPERTIES_WITH_URLS = Pattern.compile("(^background$)|(^background-image$)|(^list-style$)");
		
		NCX_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^content$)|(^audio$)|(^img$)");
		RESOURCE_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^audio$)|(^img$)");
		
		DTBOOK_ATTRIBUTES_WITH_URIS = Pattern.compile( "(^smilref$)|(^src$)|(^href$)");	
		
		//extend the DTBOOK_ATTRIBUTES_WITH_URIS pattern with foreign namespace URI carriers
		//TODO unsure whether to start dealing with QNames et al or if this suffices.
		//The only problem is if attribute 'bar' is a URI carrier in one namespace but not
		//in another; then we will have false positives.
		DTBOOK_COMPOUND_ATTRIBUTES_WITH_URIS = Pattern.compile( "(^altimg$)|(^src$)|(^dtbook:smilref$)");
	}
	
	public Pattern URI_REMOTE;	
	public Pattern URI_SMIL_FILE_WITH_FRAGMENT;
	public Pattern URI_WITH_FRAGMENT;  
	public Pattern URL_JAR;
	
	public Pattern FILE_SMIL;  
	public Pattern FILE_CSS;
	public Pattern FILE_XSL;
	public Pattern FILE_NCC;
	public Pattern FILE_OPF;
	public Pattern FILE_AUDIO;
	public Pattern FILE_MP3;
	public Pattern FILE_MP2;
	public Pattern FILE_WAV;
	public Pattern FILE_DTD;
	public Pattern FILE_XHTML;
	public Pattern FILE_JPG;
	public Pattern FILE_GIF;
	public Pattern FILE_PNG;
	public Pattern FILE_BMP;
// jpritchett@rfbd.org:  Added SVG
	public Pattern FILE_SVG;
	public Pattern FILE_IMAGE;
	public Pattern FILE_NCX;
	public Pattern FILE_RESOURCE;
	public Pattern FILE_DTBOOK;
	public Pattern FILE_XML;
	public Pattern FILE_M3U; 
	public Pattern FILE_PLS;
	public Pattern FILE_PDF;
	public Pattern FILE_EPUB;
	
	public Pattern XHTML_ELEMENTS_WITH_URI_ATTRS;
	public Pattern XHTML_ATTRS_WITH_URIS;  
	public Pattern XHTML_HEADING_ELEMENT;
	
	public Pattern SMIL_ELEMENTS_WITH_URI_ATTRS;
	public Pattern SMIL_ATTRIBUTES_WITH_URIS;
	
	public Pattern CSS_PROPERTIES_WITH_URLS;	
	public Pattern NCX_ELEMENTS_WITH_URI_ATTRS;
	public Pattern RESOURCE_ELEMENTS_WITH_URI_ATTRS;
	public Pattern DTBOOK_ATTRIBUTES_WITH_URIS;
	public Pattern DTBOOK_COMPOUND_ATTRIBUTES_WITH_URIS;
			
	static private FilesetRegex _instance = null;    
	
	static public FilesetRegex getInstance() {
		if (null == _instance) _instance = new FilesetRegex();        
		return _instance;
	}
	
	public boolean matches(Pattern compiledPattern, String match) {
	 	Matcher m = compiledPattern.matcher(match);
	 	return m.matches();	
	 }
	
//	Uniform Resource Identifier (URI) SCHEMES
//
//	(last updated 19 May 2005)
//
//	This is the Official IANA Registry of URI Schemes
//
//	In the Uniform Resource Identifier (URI) definition [RFC3986,RFC1738]
//	there is a field, called "scheme", to identify the type of resource
//	and access method.
//
//	Scheme Name     Description                                    Reference
//	-----------     -----------------------------------------      ---------
//	ftp             File Transfer Protocol                         [RFC1738]
//	http            Hypertext Transfer Protocol                    [RFC2616]
//	gopher          The Gopher Protocol                            [RFC1738]
//	mailto          Electronic mail address                        [RFC2368]
//	news            USENET news                                    [RFC1738]
//	nntp            USENET news using NNTP access                  [RFC1738]
//	telnet          Reference to interactive sessions              [RFC-hoffman-telnet-uri-04.txt]
//	wais            Wide Area Information Servers                  [RFC-hoffman-wais-uri-03.txt]
//	file            Host-specific file names                       [RFC1738]
//	prospero        Prospero Directory Service                     [RFC-hoffman-prospero-uri-03.txt]
//
//	z39.50s         Z39.50 Session                                 [RFC2056]
//	z39.50r         Z39.50 Retrieval                               [RFC2056]
//
//	cid             content identifier                             [RFC2392]
//	mid             message identifier                             [RFC2392]
//
//	vemmi           versatile multimedia interface                 [RFC2122]
//
//	service         service location                               [RFC2609]
//
//	imap            internet message access protocol               [RFC2192]
//
//	nfs             network file system protocol                   [RFC2224]
//
//	acap            application configuration access protocol      [RFC2244]
//
//	rtsp            real time streaming protocol                   [RFC2326]
//
//	tip             Transaction Internet Protocol                  [RFC2371] 
//
//	pop             Post Office Protocol v3                        [RFC2384]
//
//	data            data                                           [RFC2397]
//
//	dav             dav                                            [RFC2518]
//	opaquelocktoken opaquelocktoken                                [RFC2518]
//
//	sip             session initiation protocol                    [RFC3261]
//	sips            secure session intitiaion protocol             [RFC3261]
//
//	tel             telephone                                      [RFC2806]
//	fax             fax                                            [RFC2806]
//	modem           modem                                          [RFC2806]
//
//	ldap            Lightweight Directory Access Protocol          [RFC-ietf-ldapbis-url-09.txt]
//	https           Hypertext Transfer Protocol Secure             [RFC2818]
//
//	soap.beep       soap.beep                                      [RFC3288]
//	soap.beeps      soap.beeps                                     [RFC3288]
//
//	xmlrpc.beep     xmlrpc.beep                                    [RFC3529]
//	xmlrpc.beeps    xmlrpc.beeps                                   [RFC3529]
//
//	urn             Uniform Resource Names                         [RFC2141]
//	                (please see: http://www.iana.org/assignments/urn-namespaces)             
//	go              go                                             [RFC3368]
//	h323            H.323                                          [RFC3508]
//	ipp             Internet Printing Protocol                     [RFC3510]
//	tftp            Trivial File Transfer Protocol                 [RFC3617]
//	mupdate         Mailbox Update (MUPDATE) Protocol              [RFC3656]
//	pres            Presence                                       [RFC3859]
//	im              Instant Messaging                              [RFC3860]
//	mtqp            Message Tracking Query Protocol                [RFC3887]
//	iris.beep       iris.beep                                      [RFC3983]
//	dict            dictionary service protocol                    [RFC2229]
//	snmp            Simple Network Management Protocol             [RFC-black-snmp-uri-09.txt]
//	crid            TV-Anytime Content Reference Identifier        [RFC-earnshaw-tv-anytime-crid-04.txt]
//	tag             tag                                            [RFC-kindberg-tag-uri-07.txt]
//
//	Reserved URI Scheme Names:
//
//	   afs              Andrew File System global file names
//	   tn3270           Interactive 3270 emulation sessions
//	   mailserver       Access to data available from mail servers
}
