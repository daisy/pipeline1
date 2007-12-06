package int_daisy_recorder2dtb.read.audacity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;
import org.tritonus.share.sampled.file.TAudioFileReader;


/** Class for reading Sun/Next AU files.
 * @author markusg: fixed little endian reading from Audacity block files.
 * @author Florian Bomers
 * @author Matthias Pfisterer
 */
public class AupBlockFileReader extends TAudioFileReader
{
	private static final int	READ_LIMIT = 1000;



	public AupBlockFileReader()
	{
		super(READ_LIMIT);
		
	}

	private static String readDescription(DataInputStream dis, int len) throws IOException {
		byte c=-1;
		String ret="";
		while (len>0 && (c=dis.readByte())!=0) {
			ret=ret+(char) c;
			len--;
		}
		if (len>1 && c==0) {
			dis.skip(len-1);
		}
		return ret;
	}

	private int readIntLittleEndian(DataInputStream	dataInputStream) throws IOException {
		byte[] fourBytes = new byte[4];
		dataInputStream.read(fourBytes);		
		ByteBuffer buffer = ByteBuffer.wrap(fourBytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN).rewind();
		return buffer.getInt();
	}

	@Override
	protected AudioFileFormat getAudioFileFormat(InputStream inputStream, long lFileSizeInBytes) throws UnsupportedAudioFileException, IOException {
				
		DataInputStream	dataInputStream = new DataInputStream(inputStream);
		
		int	nMagic = readIntLittleEndian(dataInputStream);						
		if (nMagic != AuTool.AU_HEADER_MAGIC) {
			throw new UnsupportedAudioFileException(
			    "not an AU file: wrong header magic");
		}
		
		//int nDataOffset = dataInputStream.readInt();
		int nDataOffset = readIntLittleEndian(dataInputStream);
		//System.err.println("data offset: " +nDataOffset);
		if (nDataOffset < AuTool.DATA_OFFSET) {
			throw new UnsupportedAudioFileException(
			    "not an AU file: data offset must be 24 or greater");
		}
		
		//int nDataLength = dataInputStream.readInt();
		int nDataLength = readIntLittleEndian(dataInputStream);
		
		if (nDataLength < 0 && nDataLength!=AuTool.AUDIO_UNKNOWN_SIZE) {
			throw new UnsupportedAudioFileException(
			    "not an AU file: data length must be positive, 0 or -1 for unknown");
		}
		
		if (nDataLength < 0) {
			//mg: get it physically
			nDataLength = (int)lFileSizeInBytes - nDataOffset;
		}
		
		AudioFormat.Encoding encoding = null;
		int nSampleSize = 0;
		//int nEncoding = dataInputStream.readInt();		
		int nEncoding = readIntLittleEndian(dataInputStream);
		
		switch (nEncoding) {
		case AuTool.SND_FORMAT_MULAW_8:		// 8-bit uLaw G.711
			encoding = AudioFormat.Encoding.ULAW;
			nSampleSize = 8;
			break;

		case AuTool.SND_FORMAT_LINEAR_8:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			nSampleSize = 8;
			break;

		case AuTool.SND_FORMAT_LINEAR_16:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			nSampleSize = 16;
			break;

		case AuTool.SND_FORMAT_LINEAR_24:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			nSampleSize = 24;
			break;

		case AuTool.SND_FORMAT_LINEAR_32:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			nSampleSize = 32;
			break;
		//mg add:	
		case AuTool.SND_FORMAT_FLOAT:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			//nSampleSize = 32;
			nSampleSize = 16; //TODO this seems to be a bug in audacity, the file is (sometimes?) 16 bit
			System.err.println("Warning: setting a SND_FORMAT_FLOAT to 16 bit");
			break;	

		case AuTool.SND_FORMAT_ALAW_8:	// 8-bit aLaw G.711
			encoding = AudioFormat.Encoding.ALAW;
			nSampleSize = 8;
			break;
		}
		if (nSampleSize == 0) {
			throw new UnsupportedAudioFileException(
			    "unsupported AU file: unknown encoding " + nEncoding);
		}
		//int nSampleRate = dataInputStream.readInt();
		int nSampleRate = readIntLittleEndian(dataInputStream);
		if (nSampleRate <= 0) {
			throw new UnsupportedAudioFileException(
			    "corrupt AU file: sample rate must be positive");
		}
		//int nNumChannels = dataInputStream.readInt();
		int nNumChannels = readIntLittleEndian(dataInputStream); 
		if (nNumChannels <= 0) {
			throw new UnsupportedAudioFileException(
			    "corrupt AU file: number of channels must be positive");
		}
		// skip header information field
		//inputStream.skip(nDataOffset - AuTool.DATA_OFFSET);
		// read header info field
		String desc = readDescription(dataInputStream, nDataOffset - AuTool.DATA_OFFSET);
		// add the description to the file format's properties
		Map<String,Object> properties = new HashMap<String, Object>();
		if (desc!="") {
			properties.put("title", desc);
		}

		AudioFormat format = new AudioFormat(encoding,
		                                     nSampleRate,
		                                     nSampleSize,
		                                     nNumChannels,
		                                     calculateFrameSize(nSampleSize, nNumChannels),
		                                     nSampleRate,
		                                     false);
		
		AudioFileFormat	audioFileFormat = new TAudioFileFormat(
			AudioFileFormat.Type.AU,
			format,
			(nDataLength==AuTool.AUDIO_UNKNOWN_SIZE)?
			AudioSystem.NOT_SPECIFIED:(nDataLength / format.getFrameSize()),
			(nDataLength==AuTool.AUDIO_UNKNOWN_SIZE)?
			AudioSystem.NOT_SPECIFIED:(nDataLength + nDataOffset),
			properties);
		
		return audioFileFormat;
	}
}



/*** AuAudioFileReader.java ***/

