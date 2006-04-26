package org.daisy.util.mime;

/**
 * 
 * @author Markus Gylling
 */
public final class MIMEConstants {

	//call the MIMETypeRegistry.printConstants() method to autogenerate these.
	public static final String MIME_APPLICATION_MSWORD = "application/msword";
	public static final String MIME_APPLICATION_PDF = "application/pdf";
	public static final String MIME_APPLICATION_POSTSCRIPT = "application/postscript";
	public static final String MIME_APPLICATION_PRS_PLUCKER = "application/prs.plucker";
	public static final String MIME_APPLICATION_RELAX_NG_COMPACT_SYNTAX = "application/relax-ng-compact-syntax";
	public static final String MIME_APPLICATION_RTF = "application/rtf";
	public static final String MIME_APPLICATION_SMIL = "application/smil";
	public static final String MIME_APPLICATION_SMIL_XML = "application/smil+xml";
	public static final String MIME_APPLICATION_XHTML_XML = "application/xhtml+xml";
	public static final String MIME_APPLICATION_XML = "application/xml";
	public static final String MIME_APPLICATION_XML_DTD = "application/xml-dtd";
	public static final String MIME_APPLICATION_XML_EXTERNAL_PARSED_ENTITY = "application/xml-external-parsed-entity";
	public static final String MIME_APPLICATION_X_COMPRESS = "application/x-compress";
	public static final String MIME_APPLICATION_X_COMPRESSED = "application/x-compressed";
	public static final String MIME_APPLICATION_X_DTBD202MSMIL_XML = "application/x-dtbd202msmil+xml";
	public static final String MIME_APPLICATION_X_DTBD202NCC_XML = "application/x-dtbd202ncc+xml";
	public static final String MIME_APPLICATION_X_DTBD202SMIL_XML = "application/x-dtbd202smil+xml";
	public static final String MIME_APPLICATION_X_DTBD202XHTML_XML = "application/x-dtbd202xhtml+xml";
	public static final String MIME_APPLICATION_X_DTBFILESETD202_XML = "application/x-dtbfilesetd202+xml";
	public static final String MIME_APPLICATION_X_DTBFILESETZ3986_XML = "application/x-dtbfilesetz3986+xml";
	public static final String MIME_APPLICATION_X_DTBNCX_XML = "application/x-dtbncx+xml";
	public static final String MIME_APPLICATION_X_DTBOOK_XML = "application/x-dtbook+xml";
	public static final String MIME_APPLICATION_X_DTBRESOURCE_XML = "application/x-dtbresource+xml";
	public static final String MIME_APPLICATION_X_GTAR = "application/x-gtar";
	public static final String MIME_APPLICATION_X_GZIP = "application/x-gzip";
	public static final String MIME_APPLICATION_X_LATEX = "application/x-latex";
	public static final String MIME_APPLICATION_X_NIMASFILESET_XML = "application/x-nimasfileset+xml";
	public static final String MIME_APPLICATION_X_STUFFIT = "application/x-stuffit";
	public static final String MIME_APPLICATION_X_TAR = "application/x-tar";
	public static final String MIME_APPLICATION_X_TEX = "application/x-tex";
	public static final String MIME_APPLICATION_X_TEXINFO = "application/x-texinfo";
	public static final String MIME_APPLICATION_ZIP = "application/zip";
	public static final String MIME_AUDIO_3GPP = "audio/3gpp";
	public static final String MIME_AUDIO_MP4A_LATM = "audio/MP4A-LATM";
	public static final String MIME_AUDIO_MPEG = "audio/mpeg";
	public static final String MIME_AUDIO_MPEG2 = "audio/mpeg2";
	public static final String MIME_AUDIO_MPEG4_GENERIC = "audio/mpeg4-generic";
	public static final String MIME_AUDIO_X_AIFF = "audio/x-aiff";
	public static final String MIME_AUDIO_X_LDABADPCM = "audio/x-ldabadpcm";
	public static final String MIME_AUDIO_X_MP3 = "audio/x-mp3";
	public static final String MIME_AUDIO_X_MPEG = "audio/x-mpeg";
	public static final String MIME_AUDIO_X_MPEGURL = "audio/x-mpegurl";
	public static final String MIME_AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio";
	public static final String MIME_AUDIO_X_SCPLS = "audio/x-scpls";
	public static final String MIME_AUDIO_X_WAV = "audio/x-wav";
	public static final String MIME_ENUM = "enum";
	public static final String MIME_IMAGE_BMP = "image/bmp";
	public static final String MIME_IMAGE_GIF = "image/gif";
	public static final String MIME_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_IMAGE_PNG = "image/png";
	public static final String MIME_IMAGE_SVG_XML = "image/svg+xml";
	public static final String MIME_IMAGE_TIFF = "image/tiff";
	public static final String MIME_IMAGE_X_ICON = "image/x-icon";
	public static final String MIME_STRING = "string";
	public static final String MIME_TEXT_CSS = "text/css";
	public static final String MIME_TEXT_HTML = "text/html";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String MIME_TEXT_SGML = "text/sgml";
	public static final String MIME_TEXT_XML = "text/xml";
	public static final String MIME_TEXT_XML_EXTERNAL_PARSED_ENTITY = "text/xml-external-parsed-entity";
	public static final String MIME_VIDEO_MPEG = "video/mpeg";
	public static final String MIME_VIDEO_QUICKTIME = "video/quicktime";
	public static final String MIME_VIDEO_X_MS_ASF = "video/x-ms-asf";

	/**
	 * some string parse utility methods
	 * also exposed publicly in MimeType
	 */

	public static String getContentTypePart(String mimeString) {
		StringBuilder sb = new StringBuilder();
		char end = '/';
		for (int i = 0; i < mimeString.length(); i++) {
			if (mimeString.charAt(i) == end) break;
			sb.append(mimeString.charAt(i));
		}
		return sb.toString();		
	}
		
	public static String getSubTypePart(String mimeString) {
		// assumes ContentTypePart/SubTypePart(;charsetPart)
		StringBuilder sb = new StringBuilder();
		boolean started = false;
		char start = '/';
		char end = ';';
		for (int i = 1; i < mimeString.length(); i++) {
			if (mimeString.charAt(i) == start) {
				started = true;
			} else if (mimeString.charAt(i) == end) {
				break;
			}
			if (started)
				sb.append(mimeString.charAt(i));
		}
		return sb.substring(1,sb.length());
	}

	public static String getParametersPart(String mimeString) {
		// assumes ContentTypePart/SubTypePart(;optionalParametersPart)
		StringBuilder sb = new StringBuilder();
		char sep = ';';
		int sepPos = -1;
		for (int i = 0; i < mimeString.length(); i++) {
			if (mimeString.charAt(i) == sep) {
				sepPos = i;
			} else {
				if (sepPos > -1)
					sb.append(mimeString.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String dropParametersPart(String mimeString) {
		// assumes ContentTypePart/SubTypePart(;optionalParametersPart)		
		StringBuilder sb = new StringBuilder();
		char end = ';';
		for (int i = 0; i < mimeString.length(); i++) {
			if (mimeString.charAt(i) == end)
				break;
			sb.append(mimeString.charAt(i));
		}
		return sb.toString();
	}

}
