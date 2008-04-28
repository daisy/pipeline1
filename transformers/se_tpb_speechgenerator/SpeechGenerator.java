/* 
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package se_tpb_speechgenerator;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerAbortException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import se_tpb_filesetcreator.SrcExtractor;


/**
 * Main class for transformer <code>se_tpb_speechgenerator</code>.
 * 
 * @author Martin Blomberg
 *
 */
@SuppressWarnings("deprecation")
public class SpeechGenerator extends Transformer {

	// output of xml including smil:
	private XMLEventFactory eventFactory;						// creates stax events to output
	private XMLEventWriter writer;								// for writing stax events to file.
	private String smilURI = "http://www.w3.org/2001/SMIL20/";	// namespace identifier
	private String smilPrefix = "smil";							// namespcace prefix
	private String smilClipBegin = "clipBegin";					// attribute name
	private String smilClipEnd = "clipEnd";						// attribute name
	private String smilSrc = "src";								// attribute name
	
	// speech generation
	private Map<String, TTS> ttsEngines = new HashMap<String, TTS>();						// container for tts instances. xml:lang->instance
	private List<File> workingFiles = new ArrayList<File>();				// a container for the audio files not yet finally merged
	private List<File> finishedFiles = new ArrayList<File>();				// a container for the audio files merged to final state
	private Set<String> mergeAudio = new HashSet<String>();						// container of element names on which to merge earlier generated audio
	private int synchronizationPointCounter = 0;				// number of sync points in document, used for progress reporting
		
	// creation of audio files:
	private String audioOutputPrefix = "speechgen";				// name stem of generated audio files
	private String audioOutputSuffix = ".wav";					// file name suffix for generated audio files
	private int digitLen = 4;									// length of "counter" part in generated names
	private int fileCounter = 0;								// counter for creation of audio file names
	private int numSPoints = 0;									// number of sync points processed so far, used for progress reporting
	private File currentAudioFile = null;						// name of the audio file currently being generated, or will be generated by merging temp clips.
	private SmilClock clock = new SmilClock(0);					// clock used for inserting valid time values on xml elements.
	private Queue<Integer> lastSynchNumber = new LinkedList<Integer>();			// a queue containing the sync point number of the last sync point before a merge. Used for inserting silence.
	
	// announcements:
	private List<StartElement> before = new ArrayList<StartElement>();						// list of elements to introduce using speech 
	private Stack<StartElement> after = new Stack<StartElement>();							// stack of elements to close/terminate using speech
	private Stack<Integer> afterLevels = new Stack<Integer>();					// stack of levels keeping track of to which element level the terminating announcements correspond to.
	private QName announceBefore;								// the attribute containing the before announcements
	private QName announceAfter;								// the attribute containing the after announcements

	// a stack to keep track of the xml-context.
	private ContextStack xmlContext = new ContextStack();		// keeping track of the document xml context
	
	// elems sets
	private Set<String> absoluteSynch = new HashSet<String>();					// element names upon which a sync point must be generated.
	private Set<String> containsSynch = new HashSet<String>();					// names of elements which may contain sync points.
	
	// silence
	private static int AFTER_LAST;								// miliseconds: silence after last sync point in an audio file
	private static int AFTER_FIRST;								// miliseconds: silence after first sync point in an audio file
	private static int BEFORE_ANNOUNCEMENT;						// miliseconds: silence before announcement
	private static int AFTER_ANNOUNCEMENT;						// miliseconds: silence after announcement
	private static int AFTER_REGULAR_PHRASE;					// miliseconds: silence after each phrase
	
	// misc variables
	private File outputDir;										// output directory
	private boolean concurrentMerge;							// merge in a parallel thread? May save time on some systems.
	private boolean mp3Output;									// whether or not to encode the generated files as mp3.
	private static CountDownLatch barrier;						// thread synchronization
	private DocumentBuilder domBuilder;							// used for constructing a small Document for each sync point.


	/**
	 * The usual constructor.
	 * @param inputListener
	 * @param eventListeners
	 * @param bool
	 */
	public SpeechGenerator(InputListener inputListener, Boolean bool) {
		super(inputListener, bool);
		
		eventFactory = XMLEventFactory.newInstance();
	}
	
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	public boolean execute(Map<String,String> parameters) throws TransformerRunException {
		
		try {
			/* get the params */
			File configFile = new File(parameters.remove("sgConfigFilename"));
			File ttsBuilderConfig = new File(parameters.remove("ttsBuilderConfig"));
			File inputFile = new File(parameters.remove("inputFilename"));
			File outputFile = new File(parameters.remove("outputFilename"));
			
			outputDir = new File(parameters.get("outputDirectory"));
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			concurrentMerge = Boolean.parseBoolean(parameters.remove("concurrentAudioMerge"));
			mp3Output = Boolean.parseBoolean(parameters.remove("mp3Output"));
			
	
			// validate the configuration file for ttsbuilder
			ErrorHandler handler = new ErrorHandler() {				
				public void warning(SAXParseException e) {
					sendMessage(Level.WARNING, readable(e));
				}
				public void fatalError(SAXParseException e) {
					sendMessage(Level.SEVERE, readable(e));
				}				
				public void error(SAXParseException e) {
					sendMessage(Level.SEVERE, readable(e));
				}				
				private String readable(SAXParseException e) {
					return e.getMessage() + " at line " + e.getLineNumber() + 
						", column " + e.getColumnNumber();
				}
			};
			
			File rng = new File(parameters.remove("ttsBuilderRNG"));
			RelaxngSchematronValidator validator = 
				new RelaxngSchematronValidator(rng, handler, true, true);
			
			if (!validator.isValid(ttsBuilderConfig)) {
				String msg = i18n("INVALID_TTSBUILDER_CONFIG", ttsBuilderConfig.getAbsolutePath());
				throw new IllegalArgumentException(msg);
			}
			
			parseConfigFile(configFile.getAbsolutePath());
			
			// copy the additional files
			sendMessage(Level.FINER, i18n("COPYING_REFERRED_FILES"));
			SrcExtractor extr = new SrcExtractor(inputFile);
			FileBunchCopy.copyFiles(extr.getBaseDir(), outputDir, extr.getSrcValues(), null, true);
			
			
			// Get ready to count the number of phrases to generate with TTS.
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			
			FileInputStream fis = new FileInputStream(inputFile);
			XMLEventReader plainReader = factory.createXMLEventReader(fis);
			BookmarkedXMLEventReader reader = new BookmarkedXMLEventReader(plainReader);
					
			sendMessage(Level.FINEST, i18n("COUNTING_PHRASES"));
			numSPoints = 0;
			String characterEncoding = "utf-8";
			ContextStack peek = new ContextStack();
			Set<String> languages = new HashSet<String>();
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				peek.addEvent(event);
				xmlContext.addEvent(event);
				if (event.isStartElement()) {
					languages.add(peek.getCurrentLocale().getLanguage());
					if (isSynchronizationPoint(reader, event.asStartElement())) {
						numSPoints++;
						fastForward(reader);
					} else if (isMergeAudio(event.asStartElement())) {
						lastSynchNumber.add(new Integer(numSPoints));
					}
				}
				
				else if (event.isStartDocument()) {
					StartDocument sd = (StartDocument) event;
					if (sd.encodingSet()) {
						characterEncoding = sd.getCharacterEncodingScheme();
					}
				}
			}
			fis.close();
			reader.close();
			
			// get some tts impls.
			// the languages (xml:lang) used in this text are stored in the Set languages.			
			Map<String, String> m = new HashMap<String, String>();
			m.put("transformer_dir", getTransformerDirectory().getAbsolutePath());
			
			TTSBuilder ttsb = null;
			try {
				ttsb = new TTSBuilder(ttsBuilderConfig, m);
			} catch (TTSBuilderException e) {
				throw new TransformerRunException(e.getMessage(), e);
			}
			
			boolean defaultMissing = false;
			for (Iterator<String> it = languages.iterator(); it.hasNext(); ) {
				String lang = it.next();
				
				TTS tts = null;
				// try to get a tts for the language
				try {
					tts = ttsb.newTTS(lang);
				} catch (TTSBuilderException e) {
					String locMsg = i18n("TTS_NOT_FOUND", lang);
					sendMessage(Level.WARNING, locMsg);
					
					// try and get a default voice by passing null as lang
					try {
						tts = ttsb.newTTS(null);
					} catch (TTSBuilderException e2) {
						defaultMissing = true;
					}
				} finally {
					if (tts != null) {
						ttsEngines.put(lang, tts);
					}
				}
			}
			
			// any exceptions occurred? if so, throw an exception
			if (defaultMissing) {
				throw new TransformerRunException(i18n("DEFAULT_TTS_NOT_FOUND"));
			}
			
			//sendMessage(Level.FINEST, i18n("DONE"));
			sendMessage(Level.FINEST, i18n("FOUND_NUMBER", String.valueOf(numSPoints)));
						
			
			//-----------------------------------------------------------------
			//-----------------------------------------------------------------
			
			// Here goes the real thing...
			sendMessage(Level.FINEST, i18n("IN_PROGRESS"));
			
			barrier = new CountDownLatch(lastSynchNumber.size() + 1);

			// how many audio files will be produced?
			// make sure the filenames will be ok.
			digitLen = Math.max(digitLen, String.valueOf(lastSynchNumber.size()).length());
			
			xmlContext = new ContextStack();
				
			fis = new FileInputStream(inputFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName(characterEncoding));
			plainReader = factory.createXMLEventReader(isr);
			reader = new BookmarkedXMLEventReader(plainReader);
			
			boolean first = true;
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				xmlContext.addEvent(event);
				
				if (event.isStartElement()) {
					
					/* adds the namespace at the first element */
					if (first) {
						StartElement curr = event.asStartElement();
						first = false;
						Set<Namespace> ns = new HashSet<Namespace>();
						for (Iterator<?> it = curr.getNamespaces(); it.hasNext(); ) {
							ns.add((Namespace)it.next());
						}
						ns.add(eventFactory.createNamespace(smilPrefix, smilURI));
						event = eventFactory.createStartElement(curr.getName(), curr.getAttributes(), ns.iterator());
					}
					
					if (isMergeAudio(event.asStartElement())) {
						mergeAudio();
					}
					
					if (isAnnouncement(event.asStartElement())) {
						before.add(event.asStartElement());
						after.push(event.asStartElement());
						afterLevels.push(new Integer(xmlContext.getContext().size()));
					}
					
					if (isSynchronizationPoint(reader, event.asStartElement())) {
						synchronizationPointCounter++;
						
						// first phrase in a file
						boolean isFirst = workingFiles.size() == 0;						
						String smilTimeStartValue = null;
						try {
							smilTimeStartValue = doSynchronizationPoint(reader, event.asStartElement());
						} catch (Exception e) {
							// print debug info about where an error occured
							Location loc = event.getLocation();
							String lineCol = "[" + loc.getLineNumber() + ", " + loc.getColumnNumber() + "]";
							String msg = "Error trying to process phrase near [line, col]: " + lineCol + " in file " + inputFile;
							sendMessage(Level.SEVERE, msg);
							System.err.println(msg);
							e.printStackTrace();
						}
						if (isFirst) {
							addSilence(AFTER_FIRST);
						}
						
						// Add some slience before each hx?
						if (!lastSynchNumber.isEmpty()) {
							Integer integer = lastSynchNumber.peek();
							if (integer.intValue() == synchronizationPointCounter) {
								// this was the last synch point of the current part
								lastSynchNumber.poll();
								addSilence(AFTER_LAST);
							}
						}
									
						try {
							checkAbort();
						} catch (TransformerAbortException tae) {
							// close all open streams
							closeStreams(fis, reader);
							terminateTTSInstances();
							
							// exit the main loop by returning false.
							return false;
						}
						progress((double) synchronizationPointCounter / numSPoints);

						// create the new startElement, i e. the same element + smil-attributes.
						StartElement se = addSmilAttrs(event, smilTimeStartValue);
						writeEvent(se);
						event = null; // to avoid being written to file once more
						boolean writeToFile = true;
						fastForward(reader, writeToFile);
					}
					
				} else if (event.isStartDocument()) {
					XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
					StartDocument sd = (StartDocument)event;
					DEBUG("SpeechGenerator#execute outputFile: " + outputFile);
					if (sd.encodingSet()) {
						writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), sd.getCharacterEncodingScheme());
					} else {
						writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
						event = eventFactory.createStartDocument("utf-8", "1.0");             
					}
				}
				
				if (event != null) {
					writeEvent(event);
				}
			}
			
			sendMessage(Level.FINEST, i18n("AWAIT_LAST_MERGE"));
			mergeAudio();
			if (concurrentMerge) {
				barrier.await();
			}
			closeStreams(fis, reader);
			terminateTTSInstances();
			
			sendMessage(Level.FINEST, i18n("DONE"));
		} catch (Throwable e) {
			sendMessage(Level.SEVERE, i18n("FATAL_ERROR", e.getMessage()));
			e.printStackTrace();
			throw new TransformerRunException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Closes all open streams.
	 * 
	 * @param fis
	 * @param reader
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void closeStreams(FileInputStream fis, BookmarkedXMLEventReader reader) throws XMLStreamException, IOException {
		writer.close();
		fis.close();
		reader.close();
	}

	
	/**
	 * Calls <code>close()</code> on each <code>TTS</code> instance.
	 */
	private void terminateTTSInstances() {
		for (Iterator<String> it = ttsEngines.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			TTS t = ttsEngines.get(key);
			try {
				t.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Continuing...");
			}
		}
	}
	
	
	/**
	 * Produces speech for the synchronization point represented by <code>se</code>
	 * and possibly announcements.
	 * @param reader a reader for the input document.
	 * @param se the start element.
	 * @return the smil clock start time value before this phrase.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws TransformerRunException
	 * @throws InterruptedException 
	 */
	private String doSynchronizationPoint(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, IOException, UnsupportedAudioFileException, TransformerRunException, InterruptedException {
		Document scope = getDOM(reader, se);
		List<StartElement> introductions = new ArrayList<StartElement>();
		introductions.addAll(before);
		before.clear();
		
		List<StartElement> terminations = new ArrayList<StartElement>();
		
		int minLevel = getMinLevelBeforeNextSynchronization(reader);
		while (!after.isEmpty()) {
			Integer integer = afterLevels.peek();
			if (minLevel <= integer.intValue()) {
				// terminate this element structure
				afterLevels.pop();
				StartElement elem = after.pop();
				terminations.add(elem);
			} else {
				// this element should not be terminated, hence we should
				// stop searching through the stack.
				break;
			}
		}
		
		Element scopeRoot = scope.getDocumentElement();
		String lang = scopeRoot.getAttribute("xml:lang");
		
		// choose tts
		TTS tts = ttsEngines.get(lang);		
				
		File file = null;
		File temp = null;
		String startValue = clock.toString(SmilClock.FULL);
		long duration;
		
		
		SmilClock synchPointClock = new SmilClock(0);
		ArrayList<File> synchPointFiles = new ArrayList<File>();
		
		/*
		 * Introductions
		 */		
		if (introductions.size() > 0) {
			temp = getSilentFile(BEFORE_ANNOUNCEMENT);
			if (temp != null) {
				synchPointClock = new SmilClock(BEFORE_ANNOUNCEMENT + synchPointClock.millisecondsValue());
				synchPointFiles.add(temp);
			}
			file = getNextTempAudioFile(false);
			duration = tts.introduceStruct(introductions, announceBefore, file);
			introductions.clear();
			if (duration == 0) {
				file.delete();
			} else {
				synchPointFiles.add(file);
				temp = getSilentFile(AFTER_ANNOUNCEMENT);
				if (temp != null) {
					synchPointClock = new SmilClock(AFTER_ANNOUNCEMENT + synchPointClock.millisecondsValue());
					synchPointFiles.add(temp);
				}
				synchPointClock = new SmilClock(duration + synchPointClock.millisecondsValue());
			}
		}
		
		
		file = getNextTempAudioFile(false);
		
		/*
		 * Common phrase
		 */
		duration = tts.say(scope, file);
		if (duration == 0) {
			file.delete();
		} else {
			synchPointClock = new SmilClock(synchPointClock.millisecondsValue() + duration);
			synchPointFiles.add(file);
		}
		temp = getSilentFile(AFTER_REGULAR_PHRASE);
		if (null != temp) {
			synchPointFiles.add(temp);
			synchPointClock = new SmilClock(AFTER_REGULAR_PHRASE + synchPointClock.millisecondsValue());
		}
		
		/*
		 * Terminations
		 */
		if (terminations.size() > 0) {
			
			temp = getSilentFile(BEFORE_ANNOUNCEMENT);
			if (null != temp) {
				synchPointFiles.add(temp);
				synchPointClock = new SmilClock(BEFORE_ANNOUNCEMENT + synchPointClock.millisecondsValue());
			}
			file = getNextTempAudioFile(false);
			duration = tts.terminateStruct(terminations, announceAfter, file);
			terminations.clear();
			if (duration == 0) {
				file.delete();
			} else {
				synchPointFiles.add(file);
				temp = getSilentFile(AFTER_ANNOUNCEMENT);
				if (null != temp) {
					synchPointFiles.add(temp);
					synchPointClock = new SmilClock(AFTER_ANNOUNCEMENT + synchPointClock.millisecondsValue());
				}
				synchPointClock = new SmilClock(synchPointClock.millisecondsValue() + duration);
			}
		}
		
		// check if there is a need to merge the audio before
		// adding this last phrase to the set of files yet 
		// to be merged.
		// The wav file format allows files to be no bigger 
		// than 2^(31) - 1 bytes.
		long maxFileSize = (long) Math.pow(2, 31) - 1;
		long wfs = getTotalFileSize(workingFiles.iterator());
		long sps = getTotalFileSize(synchPointFiles.iterator());
		if ((wfs + sps) > maxFileSize) {
			boolean concurrentMerge = this.concurrentMerge;
			this.concurrentMerge = false;	// to save an optional barrier from this unplanned merge
			mergeAudio();
			this.concurrentMerge = concurrentMerge;
		}

		startValue = clock.toString(SmilClock.FULL);
		workingFiles.addAll(synchPointFiles);
		clock = new SmilClock(synchPointClock.millisecondsValue() + clock.millisecondsValue());
		
		return startValue;
	}
	
	
	/**
	 * Returns the sum of lengths of the files pointed at by <code>it</code>.
	 * @param it An iterator over some files.
	 * @return the sum of lengths of the files pointed at by <code>it</code>.
	 */
	private long getTotalFileSize(Iterator<File> it) {
		long s = 0;
		File tmp;
		for (; it.hasNext(); ) {
			Object obj = it.next();
			if (obj instanceof File) {
				tmp = (File) obj;
				s += tmp.length();
			}
		}
		return s;
	}
	
	
	
	/**
	 * Returns an audio file containing <code>timeMillis</code> milliseconds silence,
	 * of <code>null</code> if no other audio has been produced or the produced file
	 * is corrupt. 
	 * @param timeMillis the duration of the silent file.
	 * @return an audio file containing <code>timeMillis</code> milliseconds silence,
	 * of <code>null</code> if no other audio has been produced or the produced file
	 * is corrupt.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private File getSilentFile(int timeMillis) throws IOException, UnsupportedAudioFileException {
		File model = null;
		if (workingFiles.size() < 1) {
			return null;
		}
		
		if (timeMillis <= 0) {
			return null;
		} 
		
		model = workingFiles.get(0);
		File silentFile = getNextTempAudioFile(false);
		// Make a silent audio file, duration: timeMillis
		try {
			SilenceAudioFile.writeSilentFile(silentFile, timeMillis, model);		
		} catch (EOFException e) {
			if (!silentFile.delete()) {
				silentFile.deleteOnExit();
			}
			e.printStackTrace();
		}
		return silentFile;
	}
	
	/**
	 * Adds <code>timeMillis</code> millisecs of silence.
	 * @param timeMillis the duration of the silence.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private void addSilence(int timeMillis) throws IOException, UnsupportedAudioFileException {
		File silentFile = getSilentFile(timeMillis);
		clock = new SmilClock(clock.millisecondsValue() + timeMillis);
		workingFiles.add(silentFile);
	}

	
	/**
	 * Returns a substitute for <code>event</code> to be written to the output file.
	 * The substitute has the correct smil:clipBegin and smil:clipEnd attributes.
	 * @param event the event which to enrich with clipBegin, clipEnd and src.
	 * @param startValue the value for the smil:clipBegin-attribute.
	 * @return a substitute for <code>event</code> to be written to the output file.
	 */
	private StartElement addSmilAttrs(XMLEvent event, String startValue) {
		QName elemName = event.asStartElement().getName();
		
		QName cbName = new QName(smilURI, smilClipBegin, smilPrefix);
		Attribute clipBegin = eventFactory.createAttribute(cbName, startValue);
		
		QName ceName = new QName(smilURI, smilClipEnd, smilPrefix);
		Attribute clipEnd = eventFactory.createAttribute(ceName, clock.toString(SmilClock.FULL));
		
		QName srcName = new QName(smilURI, smilSrc, smilPrefix);
		String currentAudioFilename = getCurrentAudioFile().getName();
		if (mp3Output) {
			currentAudioFilename = goMp3(currentAudioFilename);
		}
		Attribute src = eventFactory.createAttribute(srcName, currentAudioFilename);
		
		Set<Attribute> attributes = new HashSet<Attribute>();
		for (Iterator<?> it = event.asStartElement().getAttributes(); it.hasNext(); ) {
			attributes.add((Attribute)it.next());
		}
		attributes.add(clipBegin);
		attributes.add(clipEnd);
		attributes.add(src);

		Set<Namespace> namespaces = new HashSet<Namespace>();
		for (Iterator<?> it = event.asStartElement().getNamespaces(); it.hasNext(); ) {
			namespaces.add((Namespace)it.next());
		}
		
		StartElement se = eventFactory.createStartElement(elemName, attributes.iterator(), namespaces.iterator());
		return se;
	}
	
	
	/**
	 * Returns the input parameter with the suffix ".mp3" instead of
	 * whatever was from the last '.' and after.
	 * @param filename the filename to make .mp3.
	 * @return the filename with the suffix changed to .mp3.
	 */
	private String goMp3(String filename) {
		int dotIndex = filename.lastIndexOf('.');
		return filename.substring(0, dotIndex) + ".mp3";
	}
	
	/**
	 * Returns a file with the same prefix as <code>wavFile</code>, 
	 * but with the suffix changed to .mp3 instead of
	 * whatever was from the last '.' and after.
	 * @param wavFile the file to make .mp3.
	 * @return a file with the same prefix as <code>wavFile</code>, 
	 * but with the suffix changed to .mp3.
	 */
	private File goMp3(File wavFile) {
		return new File(goMp3(wavFile.getAbsolutePath()));
	}
	
	
	/**
	 * Writes an XMLEvent to the output file.
	 * @param event the event.
	 * @throws XMLStreamException
	 */
	private void writeEvent(XMLEvent event) throws XMLStreamException {
		writer.add(event);
	}
	
	
	/* Initializerar DOMByggare och dess fabrik. */
	/* Fyller på mängder med elementnamn.. ska detta vara konfigurerbart? */
	/* ...och är det rätt att bara ha local part av element- och attributnamn här? */
	/**
	 * Parses the config file <code>filename</code>
	 * @param filename the absolute path of the config file.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerRunException 
	 */
	private void parseConfigFile(String filename) throws ParserConfigurationException, SAXException, IOException, TransformerRunException {
		DEBUG("SpeechGenerator#parseConfigFile()");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		domBuilder = factory.newDocumentBuilder();
		File temp = new File(filename);
		Document config = null;
		config = domBuilder.parse(temp);
		Node root = config.getDocumentElement();
		
		getConfigItems(absoluteSynch, root, "//absoluteSynch");
		getConfigItems(containsSynch, root, "//containsSynch");		
		getConfigItems(mergeAudio, root, "//mergeAudio");
		
		announceBefore = getConfigQName(root, "/sgConfig/announceAttributes/item[@id='before']");
		announceAfter = getConfigQName(root, "/sgConfig/announceAttributes/item[@id='after']");
		
		Node node = null;
		String tmp = null;
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterFirst/text()");
		try {
			if (node != null) {
				tmp = node.getTextContent();
				AFTER_FIRST = Integer.parseInt(tmp);
			}
	
			node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterLast/text()");
			if (node != null) {
				tmp = node.getTextContent();
				AFTER_LAST = Integer.parseInt(tmp);
			}
	
			node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/beforeAnnouncement/text()");
			if (node != null) {
				tmp = node.getTextContent();
				BEFORE_ANNOUNCEMENT = Integer.parseInt(tmp);
			}
	
			node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterAnnouncement/text()");
			if (node != null) {
				tmp = node.getTextContent();
				AFTER_ANNOUNCEMENT = Integer.parseInt(tmp);
			}
	
			node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterRegularPhrase/text()");
			if (node != null) {
				tmp = node.getTextContent();
				AFTER_REGULAR_PHRASE = Integer.parseInt(tmp);
			}
		} catch (NumberFormatException e) {
			String msg = "Unable to parse " + tmp;
			throw new TransformerRunException(msg, e);
		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
	}
	
	/**
	 * Extracts data from qnames in the config file.
	 * @param root the config document root
	 * @param xPath the xpath pointing out the qname-elements relative the root
	 * @return a QName instance.
	 */
	private QName getConfigQName(Node root, String xPath) {
		Element elem = (Element) XPathUtils.selectSingleNode(root, xPath);
		String uri = elem.getAttribute("uri");
		String local = elem.getAttribute("local");
		String prefix = elem.getAttribute("prefix");
		return new QName(uri, local, prefix);
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> represents a 
	 * synchronization point, <code>false</code> otherwise.
	 * @param reader a reader for the input document.
	 * @param se the start element.
	 * @return <code>true</code> if <code>se</code> represents a 
	 * synchronization point, <code>false</code> otherwise.
	 * @throws XMLStreamException
	 */
	private boolean isSynchronizationPoint(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		String bookmark = "TPB Narrator.SpeechGenerator.getSynchronizationPoint";
		reader.setBookmark(bookmark);
		
		
		int elemCount = 1;
		String textContent = "";
		while (elemCount > 0 && reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				elemCount++;
				String nodeName = event.asStartElement().getName().getLocalPart();
				
				//if (nodeName != null && (absoluteSynch.contains(nodeName) || "sent".equals(nodeName))) {
				if (nodeName != null && (absoluteSynch.contains(nodeName) || containsSynch.contains(nodeName))) {
					reader.gotoAndRemoveBookmark(bookmark);
					return false;
				}
			} else if (event.isEndElement()) {
				elemCount--;
			} else if (event.isCharacters()) {
				textContent += event.asCharacters().getData() + " ";
			}
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return textContent.trim().length() > 0;
	}
	
	
	/**
	 * Merges the small clips (each synchpoint) so far into one
	 * .wav file. Performes mp3-encoding according to the configuration, as well
	 * as handling all this in a separate thread.
	 * @throws InterruptedException
	 */
	private void mergeAudio() throws InterruptedException {
		if (0 == workingFiles.size()) {
			barrier.countDown();
			return;
		}
		
		/* merge! */
		currentAudioFile = getCurrentAudioFile();
		List<File> wf = new ArrayList<File>();
		wf.addAll(workingFiles);
		File mp3file = mp3Output ? goMp3(currentAudioFile) : null;
		
		if (concurrentMerge) {	
			WavConcatWorker wcw = new WavConcatWorker(wf, currentAudioFile, mp3file, barrier);
			new Thread(wcw).start();
		} else {
			CountDownLatch currentMerge = new CountDownLatch(1);
			WavConcatWorker wcw = new WavConcatWorker(wf, currentAudioFile, mp3file, currentMerge);
			new Thread(wcw).start();
			currentMerge.await();
		}		
		
		workingFiles.clear();
		clock = new SmilClock(0);
		
		/* and finally: */
		currentAudioFile = null;
	}
	
	
	/**
	 * Returns the name of the current audio file. That is the name of the file that
	 * a call to <code>mergeAudio()</code> will produce.
	 * @return the name of the current audio file. That is the name of the file that
	 * a call to <code>mergeAudio()</code> will produce.
	 */
	private File getCurrentAudioFile() {
		if (null == currentAudioFile) {
			
			String numPart = String.valueOf(++fileCounter);
			while (numPart.length() < digitLen) {
				numPart = "0" + numPart;
			}
			currentAudioFile = new File(outputDir, audioOutputPrefix + numPart + audioOutputSuffix);
			finishedFiles.add(currentAudioFile);
		}
		
		return currentAudioFile;
	}
	
	/* Anropas när vi just skickat en synkpunkt till talsyntesen.
	 * I det fallet vill vi att läsaren ska hoppa över scopet
	 * för detta element innan det fortsätter i den vanliga kvarnen
	 * igen. På så sätt slipper vi synka för <w> etc.
	 */
	
	/**
	 * Gets the reader past the current scope/suýnchronization point.
	 * @param reader a reader for the input document.
	 * @param write are we supposed to write the elements to the output file?
	 * @throws XMLStreamException
	 */
	private void fastForward(BookmarkedXMLEventReader reader, boolean write) throws XMLStreamException {
		int elemCount = 1;
		XMLEvent event = null;
		while (elemCount > 0 && reader.hasNext()) {
			event = reader.nextEvent();
			
			if (event.isStartElement()) {
				elemCount++;	
			}
			
			if (event.isEndElement()) {
				elemCount--;
			}
			
			xmlContext.addEvent(event);
			if (write) {
				writeEvent(event);
			}
		}
	}
	
	
	
	/**
	 * Gets the reader past the current scope/suýnchronization point.
	 * @param reader a reader for the input document.
	 * @throws XMLStreamException
	 */
	private void fastForward(BookmarkedXMLEventReader reader) throws XMLStreamException {
		fastForward(reader, false);
	}
	
	
	/* Konstruerar en (liten) DOM-instans av det scope vi är inne i.
	 * Indata: BookmarkedXMLEventReader reader, StartElement se 
	 */
	/**
	 * Returns a small DOM representing a synchronization point starting with 
	 * <code>se</code>. The attribute <code>xml:lang</code> with its current
	 * value is added to the elements of the DOM when absent.
	 * @param reader a reader for the input document.
	 * @param se the start of the synchronization point.
	 * @return a small DOM representing a synchronization point starting with 
	 * <code>se</code>.
	 * @throws XMLStreamException
	 */
	private Document getDOM(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		String bookmark = "TPB Narrator.SpeechGenerator.getDOM";
		reader.setBookmark(bookmark);
	
		Document dom = domBuilder.newDocument();
		Element root = dom.createElement(se.getName().getLocalPart());
		for (Iterator<?> it = se.asStartElement().getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute)it.next();
			QName qn = at.getName();
			root.setAttribute(qn.getLocalPart(), at.getValue());
		}
		Locale currentLocale = xmlContext.getCurrentLocale();
		if (currentLocale != null && currentLocale.getLanguage() != null) {
			root.setAttribute("xml:lang", currentLocale.getLanguage());
		}
		
		dom.appendChild(root);
		Stack<Element> treeStack = new Stack<Element>();
		treeStack.push(root);
		
		int elemCount = 1;
		while (elemCount > 0 && reader.hasNext()) {			
			XMLEvent e = reader.nextEvent();
			if (e.isStartElement()) {
				elemCount++;
				Element elem = dom.createElement(e.asStartElement().getName().getLocalPart());
				
				for (Iterator<?> it = e.asStartElement().getAttributes(); it.hasNext(); ) {
					Attribute at = (Attribute)it.next();
					QName qn = at.getName();
					elem.setAttribute(qn.getLocalPart(), at.getValue());
				}
				// namespace?!
				
				Element parent = treeStack.peek();
				parent.appendChild(elem);
				treeStack.push(elem);
			} else if (e.isEndElement()) {
				elemCount--;
				treeStack.pop();
			} else if (e.isCharacters()) {
				String data = e.asCharacters().getData();
				Element parent = treeStack.peek();
				parent.appendChild(dom.createTextNode(data));
			}
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		DEBUG(dom);
		return dom;
	}
	
	/**
	 * Returns the minimum element level before the next synchronization point is encountered.
	 * This method assumes that an element representing a synchronization point just have been read.
	 * @param reader a reader for the input document.
	 * @return the minimum element level before the next synchronization point is encountered.
	 * @throws XMLStreamException
	 */
	private int getMinLevelBeforeNextSynchronization(BookmarkedXMLEventReader reader) throws XMLStreamException {
		String bookmark = "TPB Narrator.SpeechGenerator.getMinLevelBeforeNextSynchronization";
		reader.setBookmark(bookmark);
		
		// get past the current point of synchronization
		int elemCount = 1;
		while (elemCount != 0 && reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				elemCount++;
			}
			
			if (event.isEndElement()) {
				elemCount--;
			}
		}
		
		int localLevel = xmlContext.getContext().size();
		int minLevel = xmlContext.getContext().size();
		
		// find the next one
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				localLevel++;
				if (isSynchronizationPoint(reader, event.asStartElement())) {
					break;
				}
			}
			
			if (event.isEndElement()) {
				localLevel--;
			}
			
			minLevel = Math.min(minLevel, localLevel);
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return minLevel;
	}
	

	/**
	 * Returns <code>true</code> if this element has some kind of announcement
	 * specified, <code>false</code> otherwise. That is, if this element contains any of the two attributes
	 * possibly specified in the configuration.
	 * @param se the start element.
	 * @return <code>true</code> if this element has some kind of announcement
	 * specified, <code>false</code> otherwise.
	 */
	private boolean isAnnouncement(StartElement se) {		
		for (Iterator<?> atIt = se.getAttributes(); atIt.hasNext(); ) {
			Attribute at = (Attribute)atIt.next();
			if (announceBefore.equals(at.getName())) {
				return true;
			}
			if (announceAfter.equals(at.getName())) {
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * Returns <code>true</code> if this element means a merge of
	 * the audio files, <code>false</code> otherwise.
	 * @param se the start element.
	 * @return <code>true</code> if this element means a merge of
	 * the audio files, <code>false</code> otherwise.
	 */
	private boolean isMergeAudio(StartElement se) {
		String context = xmlContext.getContextXPath(
				ContextStack.XPATH_SELECT_ELEMENTS_ONLY,
				ContextStack.XPATH_PREDICATES_NONE);
		
		for (Iterator<String> it = mergeAudio.iterator(); it.hasNext(); ) {
			String elem = it.next();
			if (context.endsWith(elem)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Returns the next tempFile to use for audio output.
	 * @param addToWorkingFiles if the generated file should be 
	 * added right now to the set of file that will be merged, or not.
	 * @return the next tempFile to use for audio output.
	 * @throws IOException
	 */
	private File getNextTempAudioFile(boolean addToWorkingFiles) throws IOException {
		File file = File.createTempFile("tempfile-tpbnarrator-", ".wav", outputDir);
		if (addToWorkingFiles) {
			workingFiles.add(file);
		}
		return file;
	}
	
	
	/**
	 * Prints optional debug messages to stdout.
	 * @param msg the debug message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			System.out.println("DEBUG: " + msg);
		}
	}
	
	/**
	 * Outputs the document doc as an optional debug
	 * message to stdout.
	 * @param doc the document
	 */
	private void DEBUG(Document doc) {
		if (System.getProperty("org.daisy.debug") != null) {
			DEBUG("SpeechGenerator#DEBUG(Document):");
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(doc);
				Result output = new StreamResult(System.out);
				idTransform.transform(input, output);
				System.out.println();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} finally {
				System.out.println("--end SpeechGenerator");
			}
			
		}
	}
	
	
	/**
	 * Reads config data from the DOM rooted at <code>root</code> using
	 * the xpath expression <code>xPath</code>. The config items are stored
	 * as <code>String</code>s in the <code>Set</code> set.
	 * @param set the container for the config strings.
	 * @param root the root of the config DOM.
	 * @param xPath the expression to apply to <code>root</code>.
	 */
	private void getConfigItems(Set<String> set, Node root, String xPath) {
		Set<String> elemNames = new HashSet<String>();
		Node parent = XPathUtils.selectSingleNode(root, xPath);
		NodeList items = parent.getChildNodes();
		DEBUG("SpeechGenerator#getConfigItems: xPath: " + xPath);
		for (int i = 0; i < items.getLength(); i++) {
			Node node = items.item(i);
			String content = node.getTextContent().trim();
			if (content.length() == 0) {
				continue;
			}
			elemNames.add(node.getTextContent());
			set.add(node.getTextContent().trim());
			DEBUG("SpeechGenerator#getConfigItems: time container: " + node.getTextContent());
		}
	}
}
