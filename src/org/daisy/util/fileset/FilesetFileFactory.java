package org.daisy.util.fileset;

import java.net.URI;

/**
 * Use to retrieve single instances 
 * from the FilesetFile hierarchy, ie
 * a FilesetFile descendant without a Fileset owner
 * @author Markus Gylling
 */
public final class FilesetFileFactory {

	private FilesetFileFactory(){}
	
	
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
	 * Tries to create and return an OpfFile using the input resource. 
	 * @throws FilesetException 
	 */
	public static OpfFile newOpfFile(URI resource) throws FilesetException {		
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
	
}
