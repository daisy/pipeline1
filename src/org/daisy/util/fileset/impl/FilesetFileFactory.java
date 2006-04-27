package org.daisy.util.fileset.impl;

import java.net.URI;

import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.interfaces.audio.Mp2File;
import org.daisy.util.fileset.interfaces.audio.Mp3File;
import org.daisy.util.fileset.interfaces.audio.WavFile;
import org.daisy.util.fileset.interfaces.image.GifFile;
import org.daisy.util.fileset.interfaces.image.JpgFile;
import org.daisy.util.fileset.interfaces.image.PngFile;
import org.daisy.util.fileset.interfaces.text.CssFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;


/**
 * Use to retrieve single instances 
 * from the FilesetFile hierarchy, ie
 * a FilesetFile without a Fileset owner
 * @author Markus Gylling
 */
public final class FilesetFileFactory {

	private FilesetFileFactory(){}
	
//	public static FilesetFile newFilesetFile(String className, URI resource) throws FilesetException, ClassNotFoundException {
//		Class fclass = Class.forName("className");
//        Object consoleObj = consoleClass.newInstance();
//        Method hookStandards = consoleClass.getDeclaredMethod("hookStandards", null);
//        hookStandards.invoke(consoleObj,null);
//
//	}
		
	/**
	 * Tries to create and return a CssFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static CssFile newCssFile(URI resource) throws FilesetException {	
		
		try {
			return new CssFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
		
	/**
	 * Tries to create and return a Z3986SmilFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static Z3986SmilFile newZ3986SmilFile(URI resource) throws FilesetException {		
		try {
			return new Z3986SmilFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a Z3986NcxFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static Z3986NcxFile newZ3986NcxFile(URI resource) throws FilesetException {		
		try {
			return new Z3986NcxFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a Z3986DtbookFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static Z3986DtbookFile newZ3986DtbookFile(URI resource) throws FilesetException {		
		try {
			return new Z3986DtbookFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a Z3986OpfFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static Z3986OpfFile newZ3986OpfFile(URI resource) throws FilesetException {		
		try {
			return new OpfFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return an Mp3File using the input resource. 
	 * @throws FilesetException 
	 */
	public static Mp3File newMp3File(URI resource) throws FilesetException {		
		try {
			return new Mp3FileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return an Mp2File using the input resource. 
	 * @throws FilesetException 
	 */
	public static Mp2File newMp2File(URI resource) throws FilesetException {		
		try {
			return new Mp2FileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return an WavFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static WavFile newWavFile(URI resource) throws FilesetException {		
		try {
			return new WavFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a JpgFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static JpgFile newJpgFile(URI resource) throws FilesetException {		
		try {
			return new JpgFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a GifFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static GifFile newGifFile(URI resource) throws FilesetException {		
		try {
			return new GifFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a PngFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static PngFile newPngFile(URI resource) throws FilesetException {		
		try {
			return new PngFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
	/**
	 * Tries to create and return a Z3986ResourceFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static Z3986ResourceFile newZ3986ResourceFile(URI resource) throws FilesetException {		
		try {
			return new Z3986ResourceFileImpl(resource);
		} catch (Exception e) {
			throw new FilesetException(e.getMessage(),e);
		}
	}
	
}
