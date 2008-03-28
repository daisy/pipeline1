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
package se_tpb_speechgen2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
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
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerAbortException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import se_tpb_filesetcreator.SrcExtractor;
import se_tpb_speechgen2.audio.AudioConcatQueue;
import se_tpb_speechgen2.audio.AudioFiles;
import se_tpb_speechgen2.tts.TTS;
import se_tpb_speechgen2.tts.TTSBuilder;
import se_tpb_speechgen2.tts.TTSBuilderException;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.TTSOutput;


/**
 * @author Martin Blomberg
 *
 */
public class SpeechGen2 extends Transformer {
	
	// output of xml including smil:
	private XMLEventFactory eventFactory;						// creates stax events to output
	private XMLEventWriter writer;		 						// for writing stax events to file.
	private String smilURI = "http://www.w3.org/2001/SMIL20/";	// namespace identifier
	private String smilPrefix = "smil";							// namespace prefix
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
	private static int AFTER_LAST;								// milliseconds: silence after last sync point in an audio file
	private static int AFTER_FIRST;								// milliseconds: silence after first sync point in an audio file
	private static int BEFORE_ANNOUNCEMENT;						// milliseconds: silence before announcement
	private static int AFTER_ANNOUNCEMENT;						// milliseconds: silence after announcement
	private static int AFTER_REGULAR_PHRASE;					// milliseconds: silence after each phrase

	// misc variables
	private File outputDir;										// output directory
	private boolean concurrentMerge;							// merge in a parallel thread? May save time on some systems.
	private boolean mp3Output;									// whether or not to encode the generated files as mp3.
	private int numAudioFiles;									// approx. number of resulting audio files
	private DocumentBuilder domBuilder;							// used for constructing a small Document for each sync point.
	private AudioFormat mSilenceFormat = 
		new AudioFormat(22050, 16, 1,true, false);
	
	private CountDownLatch countOnce = new CountDownLatch(1);	// thread synchronization
	private AudioConcatQueue audioConcatQueue = new AudioConcatQueue(countOnce); // wav files concatenation and possibly mp3 encoding.
	
	public SpeechGen2(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
		eventFactory = XMLEventFactory.newInstance();
	}


	/* (non-Javadoc)
	 * @see org.daisy.dmfc.core.transformer.Transformer#execute(java.util.Map)
	 */
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		FileInputStream fis = null;
		BookmarkedXMLEventReader reader = null;
		
		try {
			/* get the params */
			File configFile = new File((String) parameters.remove("sgConfigFilename"));
			File ttsBuilderConfig = new File((String)parameters.remove("ttsBuilderConfig"));
			File inputFile = new File((String) parameters.remove("inputFilename"));
			File outputFile = new File((String) parameters.remove("outputFilename"));

			outputDir = new File((String) parameters.get("outputDirectory"));
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			concurrentMerge = Boolean.parseBoolean((String) parameters.remove("concurrentAudioMerge"));
			mp3Output = Boolean.parseBoolean((String) parameters.remove("mp3Output"));
			
			// validate the configuration file for ttsbuilder
			ErrorHandler handler = new ErrorHandler() {
				public void warning(SAXParseException e) {
					sendMessage(readable(e), MessageEvent.Type.WARNING);
				}
				public void fatalError(SAXParseException e) {
					sendMessage(readable(e), MessageEvent.Type.ERROR);
				}				
				public void error(SAXParseException e) {
					sendMessage(readable(e), MessageEvent.Type.ERROR);
				}				
				private String readable(SAXParseException e) {
					return e.getMessage() + " at line " + e.getLineNumber() + 
					", column " + e.getColumnNumber();
				}
			};

			File rng = new File((String)parameters.remove("ttsBuilderRNG"));
			try {
				RelaxngSchematronValidator validator;
				validator = new RelaxngSchematronValidator(rng, handler, true, true);
				if (!validator.isValid(ttsBuilderConfig)) {
					String msg = i18n("INVALID_TTSBUILDER_CONFIG", ttsBuilderConfig.getAbsolutePath());
					throw new IllegalArgumentException(msg);
				}
			} catch (ValidationException e) {
				e.printStackTrace();
				String msg = "Validation process of TTS Builder configuration failed." + e.getMessage();
				throw new TransformerRunException(msg, e);
			}
			
			parseConfigFile(configFile.getAbsolutePath());

			// copy the additional files
			sendMessage(i18n("COPYING_REFERRED_FILES"), MessageEvent.Type.DEBUG);
			SrcExtractor extr = new SrcExtractor(inputFile);
			FileBunchCopy.copyFiles(extr.getBaseDir(), outputDir, extr.getSrcValues(), null, true);

			// Get ready to count the number of phrases to generate with TTS.
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));


			fis = new FileInputStream(inputFile);
			XMLEventReader plainReader = factory.createXMLEventReader(fis);
			reader = new BookmarkedXMLEventReader(plainReader);

			sendMessage(i18n("COUNTING_PHRASES"), MessageEvent.Type.DEBUG);
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
			//System.err.println(languages);

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

			for (Iterator<String> it = languages.iterator(); it.hasNext(); ) {
				String lang = it.next();

				TTS tts = null;
				// try to get a tts for the language
				try {
					tts = ttsb.newTTS(lang);
				} catch (TTSBuilderException e) {
					tts = null;
					String locMsg = i18n("TTS_NOT_FOUND", lang);
					sendMessage(locMsg, MessageEvent.Type.WARNING);

					// try and get a default voice by passing null as lang
					try {
						tts = ttsb.newTTS(null);
					} catch (TTSBuilderException e2) {
						tts = null;
						String msg = i18n("DEFAULT_TTS_NOT_FOUND") + "\n" + e2.getMessage();
						throw new TransformerRunException(msg, e2);
					}
				} finally {
					if (tts != null) {
						ttsEngines.put(lang, tts);
					}
				}
			}

			//sendMessage(Level.FINEST, i18n("DONE"));
			sendMessage(i18n("FOUND_NUMBER", String.valueOf(numSPoints)), MessageEvent.Type.DEBUG);


			//-----------------------------------------------------------------
			//-----------------------------------------------------------------

			// Here goes the loading of text into tts system...
			sendMessage(i18n("LOADING_TEXT"), MessageEvent.Type.DEBUG);

			numAudioFiles = lastSynchNumber.size() + 1;

			// how many audio files will be produced?
			// make sure the filenames will be ok.
			digitLen = Math.max(digitLen, String.valueOf(lastSynchNumber.size()).length());

			xmlContext = new ContextStack();

			fis = new FileInputStream(inputFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName(characterEncoding));
			plainReader = factory.createXMLEventReader(isr);
			reader = new BookmarkedXMLEventReader(plainReader);

			
			//-----------------------------------------------------------------
			// Load the text
			if (!doLoadText(inputFile, fis, reader)) {
				return false;
			}
			
			isr.close();
			fis.close();
			reader.close();
			
			// start all tts instances
			for (Iterator<String> it = ttsEngines.keySet().iterator(); it.hasNext(); ) {
				String xmlLang = it.next();
				TTS tts = ttsEngines.get(xmlLang);
				tts.start();
			}
			
			// create new streams
			fis = new FileInputStream(inputFile);
			isr = new InputStreamReader(fis, Charset.forName(characterEncoding));
			plainReader = factory.createXMLEventReader(isr);
			reader = new BookmarkedXMLEventReader(plainReader);
			
			//-----------------------------------------------------------------
			// Start the audio merger queue
			//-----------------------------------------------------------------
			Thread t = new Thread(audioConcatQueue);
			t.setPriority(t.getPriority() + 1);
			t.start();
			
			//---------------------------------------------------------------------------
			// Fetch the audio
			//---------------------------------------------------------------------------
			sendMessage(i18n("FETCHING_AUDIO"), MessageEvent.Type.DEBUG);
			
			if (doFetchAudio(inputFile, outputFile, reader)) {
				sendMessage(i18n("AWAIT_LAST_MERGE"), MessageEvent.Type.DEBUG);
				mergeAudio();
				audioConcatQueue.finish();
				
				int mergeProgress = audioConcatQueue.numFilesMerged();
				// make sure progress does not exceed 1.
				progress(Math.min((double) (numAudioFiles - 1) / numAudioFiles, (double) mergeProgress / numAudioFiles));
				while (countOnce.getCount() > 0) {
					Thread.sleep(3 * 1000);
					mergeProgress = audioConcatQueue.numFilesMerged();
					progress(Math.min((double) (numAudioFiles - 1) / numAudioFiles, (double) mergeProgress / numAudioFiles));
				}
				progress(1.0);
			}
			sendMessage(i18n("DONE"), MessageEvent.Type.DEBUG);
			
			
		} catch (NullPointerException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (InterruptedException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (UnsupportedAudioFileException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);			
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);			
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw new TransformerRunException(e.getMessage(), e);
		} catch (TransformerRunException e) {
			sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			System.err.println(e + " " + e.getMessage());
			e.printStackTrace();
			audioConcatQueue.abort();
			throw e;
		} finally {
		
			try {
				closeStreams(fis, reader);
			} catch (XMLStreamException e) {
				e.printStackTrace();
				throw new TransformerRunException(e.getMessage(), e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new TransformerRunException(e.getMessage(), e);
			}
			terminateTTSInstances();
			sendMessage("Leaving SpeechGen2", MessageEvent.Type.DEBUG);
		}

		return true;
	}

	private boolean doFetchAudio(File inputFile, File outputFile, BookmarkedXMLEventReader reader) throws XMLStreamException, IOException, UnsupportedAudioFileException, InterruptedException, FactoryConfigurationError, FileNotFoundException, TransformerRunException {
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
					for (Iterator it = curr.getNamespaces(); it.hasNext(); ) {
						ns.add((Namespace) it.next());
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
					//System.err.println("TTS Progress: " + synchronizationPointCounter + " / " + numSPoints);
					//System.err.println("Merge/LAME Progress: " + audioConcatQueue.numFilesMerged() + " / ~" + numAudioFiles);
										
					// first phrase in a file
					boolean isFirst = workingFiles.size() == 0;						
					String smilTimeStartValue = null;
					try {
						smilTimeStartValue = fetchSynchronizationPoint(reader, event.asStartElement());
					} catch (Exception e) {
						// print debug info about where an error occured
						Location loc = event.getLocation();
						String lineCol = "[" + loc.getLineNumber() + ", " + loc.getColumnNumber() + "]";
						String msg = "Error trying to process phrase near [line, col]: " + lineCol + " in file " + inputFile;
						sendMessage(msg, MessageEvent.Type.ERROR);
						System.err.println(msg);
						e.printStackTrace();
						throw new TransformerRunException(e.getMessage(), e);
					}
					
					if (isFirst) {
						addSilence(AFTER_FIRST, mSilenceFormat);
					}

					// Add some slience before each hx?
					if (!lastSynchNumber.isEmpty()) {
						Integer integer = (Integer) lastSynchNumber.peek();
						if (integer.intValue() == synchronizationPointCounter) {
							// this was the last synch point of the current part
							lastSynchNumber.poll();
							addSilence(AFTER_LAST, mSilenceFormat);
						}
					}

					checkAbort();
					double progress = Math.min(0.99, (double) audioConcatQueue.numFilesMerged() / numAudioFiles);
					progress(progress);
					
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
		return true;
	}

	
	private boolean doLoadText(File inputFile, FileInputStream fis, BookmarkedXMLEventReader reader) throws XMLStreamException, IOException, UnsupportedAudioFileException, InterruptedException, TransformerRunException {
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			xmlContext.addEvent(event);

			if (event.isStartElement()) {
				if (isMergeAudio(event.asStartElement())) {
					//mergeAudio();
				}

				if (isAnnouncement(event.asStartElement())) {
					before.add(event.asStartElement());
					after.push(event.asStartElement());
					afterLevels.push(new Integer(xmlContext.getContext().size()));
				}

				if (isSynchronizationPoint(reader, event.asStartElement())) {
					loadSynchronizationPoint(reader, event.asStartElement());
					
					try {
						checkAbort();
					} catch (TransformerAbortException tae) {
						// close all open streams
						closeStreams(fis, reader);
						terminateTTSInstances();

						// exit the loop by returning false.
						return false;
					}
					
					event = null; // to avoid being written to file once more
					boolean writeToFile = false;
					fastForward(reader, writeToFile);
				}
			} 
		}
		return true;
	}


	/**
	 * Loads speech for the synchronization point represented by <code>se</code>
	 * and possibly announcements.
	 * @param reader a reader for the input document.
	 * @param se the start element.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws TransformerRunException
	 * @throws InterruptedException 
	 */
	private void loadSynchronizationPoint(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, IOException, UnsupportedAudioFileException, TransformerRunException, InterruptedException {
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
		TTS tts = (TTS) ttsEngines.get(lang);

		

		/*
		 * Introductions
		 */		
		File file = null;
		if (introductions.size() > 0) {
			file = getNextTempAudioFile(false);
			file.deleteOnExit();
			tts.addAnnouncements(introductions, announceBefore, file);
		}

		/*
		 * Common phrase
		 */
		file = getNextTempAudioFile(false);
		file.deleteOnExit();
		tts.addSyncPoint(scope, file);
		
		/*
		 * Terminations
		 */
		if (terminations.size() > 0) {
			file = getNextTempAudioFile(false);
			file.deleteOnExit();
			tts.addAnnouncements(terminations, announceAfter, file);
		}
	}
	
	/**
	 * Fetches speech for the synchronization point represented by <code>se</code>
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
	private String fetchSynchronizationPoint(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, IOException, UnsupportedAudioFileException, TransformerRunException, InterruptedException {
		TTSOutput ttsOutput;
		Document scope = getDOM(reader, se);
		List<StartElement> introductions = new ArrayList<StartElement>();
		introductions.addAll(before);
		before.clear();

		List<StartElement> terminations = new ArrayList<StartElement>();

		int minLevel = getMinLevelBeforeNextSynchronization(reader);
		while (!after.isEmpty()) {
			Integer integer = (Integer) afterLevels.peek();
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
			
			temp = getSilentFile(BEFORE_ANNOUNCEMENT, mSilenceFormat);
			synchPointClock = new SmilClock(BEFORE_ANNOUNCEMENT + synchPointClock.millisecondsValue());
			synchPointFiles.add(temp);

			
			ttsOutput = tts.getNext();
			
			duration = ttsOutput.getDuration();
			file = ttsOutput.getFile();
			introductions.clear();
			if (duration == 0) {
				file.delete();
			} else {
				synchPointFiles.add(file);
				synchPointClock = new SmilClock(duration + synchPointClock.millisecondsValue());
				
				temp = getSilentFile(AFTER_ANNOUNCEMENT, mSilenceFormat);
				synchPointFiles.add(temp);
				synchPointClock = new SmilClock(AFTER_ANNOUNCEMENT + synchPointClock.millisecondsValue());
			}
		}

		/*
		 * Common phrase
		 */
		
		ttsOutput = tts.getNext();
		
		duration = ttsOutput.getDuration();
		file = ttsOutput.getFile();
		if (duration == 0) {
			file.delete();
		} else {
			synchPointClock = new SmilClock(synchPointClock.millisecondsValue() + duration);
			synchPointFiles.add(file);
		}
		temp = getSilentFile(AFTER_REGULAR_PHRASE, mSilenceFormat);
		synchPointFiles.add(temp);
		synchPointClock = new SmilClock(AFTER_REGULAR_PHRASE + synchPointClock.millisecondsValue());
		
		/*
		 * Terminations
		 */
		if (terminations.size() > 0) {

			temp = getSilentFile(BEFORE_ANNOUNCEMENT, mSilenceFormat);
			synchPointFiles.add(temp);
			synchPointClock = new SmilClock(BEFORE_ANNOUNCEMENT + synchPointClock.millisecondsValue());
			
			
			ttsOutput = tts.getNext();
			
			file = ttsOutput.getFile();
			duration = ttsOutput.getDuration(); 
			terminations.clear();
			
			if (duration == 0) {
				file.delete();
			} else {
				synchPointFiles.add(file);
				synchPointClock = new SmilClock(synchPointClock.millisecondsValue() + duration);
				
				temp = getSilentFile(AFTER_ANNOUNCEMENT, mSilenceFormat);
				synchPointFiles.add(temp);
				synchPointClock = new SmilClock(AFTER_ANNOUNCEMENT + synchPointClock.millisecondsValue());
			}
		}
		
		// check if there is a need to merge the audio before
		// adding this last phrase to the set of files yet 
		// to be merged.
		// The wav file format allows files to be no bigger 
		// than 2^(31) - 1 bytes.
		// --------------------------------------------------------------------
		// TODO:
		// Should I count headers too?
		// int numTmpFiles = workingFiles.size() + syncPointFiles.size();
		// if (((wfs + sps) - ((numTmpFiles - 1) * RIFFHeader)) > maxFileSize) { ...?
		// how many headers are there in a wav file?
		long maxFileSize = (long) Math.pow(2, 31) - 1;
		long wfs = getTotalFileSize(workingFiles.iterator());
		long sps = getTotalFileSize(synchPointFiles.iterator());
		if ((wfs + sps) > maxFileSize) {
			boolean tempConcurrentMerge = this.concurrentMerge;
			this.concurrentMerge = false;	// to save an optional barrier from this unplanned merge
			mergeAudio();
			this.concurrentMerge = tempConcurrentMerge;
		}

		startValue = clock.toString(SmilClock.FULL);
		workingFiles.addAll(synchPointFiles);
		clock = new SmilClock(synchPointClock.millisecondsValue() + clock.millisecondsValue());

		return startValue;
	}



	/**
	 * Merges the small clips (each synchpoint) so far into one
	 * .wav file. Performes mp3-encoding as well, in a separate thread.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws InterruptedException
	 */
	private void mergeAudio() throws IOException, UnsupportedAudioFileException, InterruptedException {
		if (0 == workingFiles.size()) {
			numAudioFiles--;
			return;
		}

		/* merge! */
		currentAudioFile = getCurrentAudioFile();
		List<File> wf = new ArrayList<File>();
		wf.addAll(workingFiles);
		
		File mp3file = null;
		if (mp3Output) {
			mp3file = goMp3(currentAudioFile);
		}
		
		audioConcatQueue.addAudio(wf, currentAudioFile, mp3file);	

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

	/**
	 * Gets the reader past the current scope/synchronization point.
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
		for (Iterator it = se.asStartElement().getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
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

				for (Iterator it = e.asStartElement().getAttributes(); it.hasNext(); ) {
					Attribute at = (Attribute) it.next();
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
		for (Iterator atIt = se.getAttributes(); atIt.hasNext(); ) {
			Attribute at = (Attribute) atIt.next();
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




	// --------------------------------------------------------------
	// Utils

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
	/*
	private File getSilentFile(int timeMillis, File audioFileModel) throws IOException, UnsupportedAudioFileException {
		
		if (workingFiles.size() < 1 && null == audioFileModel) {
			//return null;
			throw new IOException("No file format available for silence!");
		}

		if (timeMillis < 0) {
			//return null;
			throw new IOException("Not possible to produce " + timeMillis + "ms of silence, must be >= 0.");
		} 

		
		File model = audioFileModel != null ? audioFileModel : (File) workingFiles.get(0);
		File target = getNextTempAudioFile(false);
		return AudioFiles.getSilentAudio(target, timeMillis, model);
	}
	*/
	
	private File getSilentFile(int timeMillis, AudioFormat format) throws IOException, UnsupportedAudioFileException {
		File target = getNextTempAudioFile(false);
		AudioFiles.getSilentAudio(target, timeMillis, format);
		return target;
	}

	/**
	 * Adds <code>timeMillis</code> millisecs of silence.
	 * @param timeMillis the duration of the silence.
	 * @param audioFormat the silence audio format
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private void addSilence(int timeMillis, AudioFormat audioFormat) throws IOException, UnsupportedAudioFileException {
		File silentFile = getSilentFile(timeMillis, audioFormat);
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
		for (Iterator it = event.asStartElement().getAttributes(); it.hasNext(); ) {
			attributes.add((Attribute) it.next());
		}
		attributes.add(clipBegin);
		attributes.add(clipEnd);
		attributes.add(src);

		Set<Namespace> namespaces = new HashSet<Namespace>();
		for (Iterator it = event.asStartElement().getNamespaces(); it.hasNext(); ) {
			namespaces.add((Namespace) it.next());
		}

		StartElement se = eventFactory.createStartElement(elemName, attributes.iterator(), namespaces.iterator());
		return se;
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
	 * Returns the sum of lengths of the files pointed at by <code>it</code>.
	 * @param it An iterator over some files.
	 * @return the sum of lengths of the files pointed at by <code>it</code>.
	 */
	private long getTotalFileSize(Iterator it) {
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

	// --------------------------------------------------------------
	// Config
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

	// --------------------------------------------------------------
	// Cleanup

	/**
	 * Closes all open streams.
	 * 
	 * @param fis
	 * @param reader
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void closeStreams(FileInputStream fis, BookmarkedXMLEventReader reader) throws XMLStreamException, IOException {
		if (writer != null) {
			writer.close();
		}
		if (fis != null) {
			fis.close();	
		}
		
		if (reader != null) {
			reader.close();
		}
	}


	/**
	 * Calls <code>close()</code> on each <code>TTS</code> instance.
	 * @throws TTSException 
	 * @throws TransformerRunException 
	 */
	private void terminateTTSInstances() throws TransformerRunException {
		List<Exception> exceptions = new ArrayList<Exception>();
		for (Iterator<String> it = ttsEngines.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			//System.err.println("Closing tts for xml:lang >>" + key + "<<");
			TTS t = ttsEngines.get(key);
			try {
				t.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Continuing...");
			} catch (TTSException e) {
				exceptions.add(e);
			}
		}
		
		if (exceptions.size() > 0) {
			TTSException cause = (TTSException) exceptions.get(0);
			throw new TransformerRunException(cause.getMessage(), cause);
		}
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
			DEBUG("SpeechGen2#DEBUG(Document):");
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
				System.out.println("--end SpeechGen2");
			}

		}
	}
}

