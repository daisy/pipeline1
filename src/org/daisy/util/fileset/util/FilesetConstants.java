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

/**
 * A repository of canonical static strings.
 * The mime type strings available here are deprecated in favor of the mimeTypeConstant strings on filesetfile interfaces 
 * 
 * @author mgylling
 */

public final class FilesetConstants {
	
		private FilesetConstants() {}

		public static final String PUBLIC_ID_RESOURCE_Z2005 = "-//NISO//DTD resource 2005-1//EN";
		public static final String PUBLIC_ID_RESOURCE_Z2002 = "-//NISO//DTD resource v1.1.0//EN";
		public static final String NAMESPACEURI_RESOURCE_Z2005 = "http://www.daisy.org/z3986/2005/resource/";
		public static final String MIMETYPE_RESOURCE_Z2005 = "application/x-dtbresource+xml";
		public static final String MIMETYPE_RESOURCE_Z2002 = "text/xml";
			
		public static final String PUBLIC_ID_DTBOOK_Z2005_1 = "-//NISO//DTD dtbook 2005-1//EN";
		public static final String SYSTEM_ID_DTBOOK_Z2005_1 = "http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd";
		public static final String PUBLIC_ID_DTBOOK_Z2002 = "-//NISO//DTD dtbook v1.1.0//EN";
		public static final String NAMESPACEURI_DTBOOK_Z2005 = "http://www.daisy.org/z3986/2005/dtbook/";
		public static final String MIMETYPE_DTBOOK_Z2005 = "application/x-dtbook+xml";
		public static final String MIMETYPE_DTBOOK_Z2002 = "text/xml";
		
		public static final String PUBLIC_ID_SMIL_Z2005 = "-//NISO//DTD dtbsmil 2005-1//EN";
		public static final String PUBLIC_ID_SMIL_Z2002 = "-//NISO//DTD dtbsmil v1.1.0//EN";
		public static final String NAMESPACEURI_SMIL_Z2005 = "http://www.w3.org/2001/SMIL20/";	
		public static final String MIMETYPE_SMIL = "application/smil";
		
		public static final String PUBLIC_ID_NCX_Z2005 = "-//NISO//DTD ncx 2005-1//EN";
		public static final String PUBLIC_ID_NCX_Z2002 = "-//NISO//DTD ncx v1.1.0//EN";
		public static final String NAMESPACEURI_NCX_Z2005 = "http://www.daisy.org/z3986/2005/ncx/";
		public static final String MIMETYPE_NCX_Z2005 = "application/x-dtbncx+xml";
		public static final String MIMETYPE_NCX_Z2002 = "text/xml";

		public static final String PUBLIC_ID_OPF_Z2005 = "+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN";
		public static final String PUBLIC_ID_OPF_Z2002 = "+//ISBN 0-9673008-1-9//DTD OEB 1.0.1 Package//EN";
		public static final String NAMESPACEURI_OPF_Z2005 = "http://openebook.org/namespaces/oeb-package/1.0/";
		public static final String NAMESPACEURI_OPF_Z2002 = "http://openebook.org/namespaces/oeb-package/1.0/";
		public static final String MIMETYPE_OPF = "text/xml";
		
		public static final String MIMETYPE_D202_NCC = "application/x-dtbd202ncc+xml";
		public static final String MIMETYPE_D202_MASTERSMIL = "application/x-dtbd202msmil+xml";
		public static final String MIMETYPE_D202_SMIL = "application/x-dtbd202smil+xml";
		public static final String MIMETYPE_D202_CONTENT = "application/x-dtbd202xhtml+xml";
				
		public static final String MIMETYPE_AUDIO_MP4AAC_Z2005 = "audio/mpeg4-generic";
		
		public static final String MIMETYPE_AUDIO_MP4AAC_Z2002 = "audio/MP4A-LATM";
		
		public static final String MIMETYPE_AUDIO_MP3 = "audio/mpeg";
		
		public static final String MIMETYPE_AUDIO_MP2 = "audio/mpeg2";
			
		public static final String MIMETYPE_AUDIO_PCMWAV = "audio/x-wav";
		
		//public static final String MIMETYPE_AUDIO_UNSPECIFIED = "audio/x-unspecified";

		public static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
		
		public static final String MIMETYPE_IMAGE_PNG = "image/png";
		
		public static final String MIMETYPE_IMAGE_GIF = "image/gif";
		
		public static final String MIMETYPE_IMAGE_SVG = "image/svg+xml";
		
		public static final String MIMETYPE_IMAGE_UNSPECIFIED = "image/x-unspecified";
		
		public static final String MIMETYPE_CSS = "text/css";
		
		public static final String MIMETYPE_XSL = "text/xml";
		
		public static final String MIMETYPE_XHTML10 = "application/xhtml+xml";
		
		public static final String MIMETYPE_PLAYLIST_M3U = "audio/x-mpegurl";
		public static final String MIMETYPE_PLAYLIST_PLS = "audio/x-scpls";
		
		
		public static final String Z3986_VERSION_2002 = "ANSI/NISO Z39.86-2002";
		public static final String Z3986_VERSION_2005 = "ANSI/NISO Z39.86-2005";

}
