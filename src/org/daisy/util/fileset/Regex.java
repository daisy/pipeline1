/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

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
public class Regex {
	
	private Regex(){
		URI_REMOTE = Pattern.compile("(^http:.+)|(^https:.+)|(^ftp:.+)|(^mailto:.+)|(^gopher:.+)|(^news:.+)|(^nntp:.+)|(^rtsp:.+)");		
		URI_SMIL_FILE_WITH_FRAGMENT = Pattern.compile(".+\\.[Ss][Mm][Ii][Ll]#.+");
		URI_WITH_FRAGMENT = Pattern.compile(".+\\..+#.+");  
		
		FILE_SMIL = Pattern.compile(".+\\.[Ss][Mm][Ii][Ll]$");  
		FILE_CSS = Pattern.compile(".+\\.[Cc][Ss][Ss]$");
		FILE_XSL = Pattern.compile(".+\\.[Xx][Ss][Ll][Tt]?$");
		FILE_NCC = Pattern.compile("[Nn][Cc][Cc].[Hh][Tt][Mm][Ll]?$");
		FILE_OPF = Pattern.compile(".+\\.[Oo][Pp][Ff]$");
		FILE_AUDIO = Pattern.compile("(.+\\.[Mm][Pp]3)|(.+\\.[Ww][Aa][Vv])$");
		FILE_MP3 = Pattern.compile(".+\\.[Mm][Pp]3$");
		FILE_DTD = Pattern.compile(".+\\.[Dd][Tt][Dd]$");
		FILE_XHTML = Pattern.compile(".+\\.[Xx]?[Hh][Tt][Mm][Ll]?$");
		FILE_IMAGE = Pattern.compile( "(.+\\.[Jj][Pp][Gg]$)|(.+\\.[Jj][Pp][Ee][Gg]$)|(.+\\.[Pn][Ng][Gg]$)|(.+\\.[Gg][Ii][Ff]$)|(.+\\.[Bb][Mm][Pp]$)");
		FILE_NCX = Pattern.compile(".+\\.[Nn][Cc][Xx]$");
		FILE_RESOURCE = Pattern.compile(".+\\.[Rr][Ee][Ss]$");
		FILE_DTBOOK = Pattern.compile(".+\\.[Xx][Mm][Ll]$");
		FILE_XML = Pattern.compile(".+\\.[Xx][Mm][Ll]$");
		
		XHTML_ELEMENTS_WITH_URI_ATTRS = Pattern.compile("(^a$)|(^link$)|(^img$)");
		XHTML_ATTRS_WITH_URIS = Pattern.compile("(^href$)|(^src$)"); 
		XHTML_HEADING_ELEMENT = Pattern.compile("^h[1-6]$");
		
		SMIL_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^text$)|(^audio$)|(^img$)|(^a$)");
		SMIL_ATTRIBUTES_WITH_URIS = Pattern.compile("(^href$)|(^src$)");
		
		CSS_PROPERTIES_WITH_URLS = Pattern.compile("(^background$)|(^background-image$)|(^list-style$)");
		
		NCX_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^content$)|(^audio$)|(^img$)");
		RESOURCE_ELEMENTS_WITH_URI_ATTRS = Pattern.compile( "(^audio$)|(^img$)");
		DTBOOK_ATTRIBUTES_WITH_URIS = Pattern.compile( "(^smilref$)|(^src$)|(^href$)");				
	}
	
	public Pattern URI_REMOTE;	
	public Pattern URI_SMIL_FILE_WITH_FRAGMENT;
	public Pattern URI_WITH_FRAGMENT;  
	
	public Pattern FILE_SMIL;  
	public Pattern FILE_CSS;
	public Pattern FILE_XSL;
	public Pattern FILE_NCC;
	public Pattern FILE_OPF;
	public Pattern FILE_AUDIO;
	public Pattern FILE_MP3;
	public Pattern FILE_DTD;
	public Pattern FILE_XHTML;
	public Pattern FILE_IMAGE;
	public Pattern FILE_NCX;
	public Pattern FILE_RESOURCE;
	public Pattern FILE_DTBOOK;
	public Pattern FILE_XML; 
	
	public Pattern XHTML_ELEMENTS_WITH_URI_ATTRS;
	public Pattern XHTML_ATTRS_WITH_URIS;  
	public Pattern XHTML_HEADING_ELEMENT;
	
	public Pattern SMIL_ELEMENTS_WITH_URI_ATTRS;
	public Pattern SMIL_ATTRIBUTES_WITH_URIS;
	
	public Pattern CSS_PROPERTIES_WITH_URLS;	
	public Pattern NCX_ELEMENTS_WITH_URI_ATTRS;
	public Pattern RESOURCE_ELEMENTS_WITH_URI_ATTRS;
	public Pattern DTBOOK_ATTRIBUTES_WITH_URIS;
			
	static private Regex _instance = null;    
	
	static public Regex getInstance() {
		if (null == _instance) _instance = new Regex();        
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
