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

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se_tpb_filesetcreator.SrcExtractor;


/**
 * @author Martin Blomberg
 *
 */
public class SpeechGenerator extends Transformer {

	// output of xml including smil:
	private XMLOutputFactory outputFactory;
	private XMLEventFactory eventFactory;
	private XMLEventWriter writer;
	private String smilURI = "http://www.w3.org/2001/SMIL20/";
	private String smilPrefix = "smil";
	private String smilClipBegin = "clipBegin";
	private String smilClipEnd = "clipEnd";
	private String smilSrc = "src";
	
	// speech generation
	private Map ttsEngines = new HashMap();
	private List workingFiles = new ArrayList();
	private List finishedFiles = new ArrayList();
	private Set mergeAudio = new HashSet();
	private int synchronizationPointCounter = 0;
		
	// creation of audio files:
	private String audioOutputPrefix = "speechgen";
	private String audioOutputSuffix = ".wav";
	private int digitLen = 4;
	private int fileCounter = 0;
	private int numSPoints = 0;
	private File currentAudioFile = null;
	private SmilClock clock = new SmilClock(0);
	private Queue lastSynchNumber = new LinkedList();
	
	// announcements:
	private List before = new ArrayList();
	private Stack after = new Stack();
	private Stack afterLevels = new Stack();
	private QName announceBefore;
	private QName announceAfter;

	// a stack to keep track of the xml-context.
	private ContextStack xmlContext = new ContextStack();
	
	// elems sets
	private Set absoluteSynch = new HashSet();
	private Set containsSynch = new HashSet();
	
	// silence
	private static int AFTER_LAST; 
	private static int AFTER_FIRST;
	private static int BEFORE_ANNOUNCEMENT;
	private static int AFTER_ANNOUNCEMENT;
	private static int AFTER_REGULAR_PHRASE;
	
	// misc variables
	private File outputDir;
	private boolean concurrentMerge;
	private boolean mergeToMp3;
	private static CountDownLatch barrier;
	private DocumentBuilder domBuilder;
	boolean DEBUG = false;


	public SpeechGenerator(InputListener inputListener, Set eventListeners, Boolean bool) {
		super(inputListener, eventListeners, bool);
		
		eventFactory = XMLEventFactory.newInstance();
		outputFactory = XMLOutputFactory.newInstance();
	}
	
	public boolean execute(Map parameters) throws TransformerRunException {
		
		try {
			/* get the params */
			String inputFilename = (String) parameters.remove("inputFilename");
			String outputFilename = (String) parameters.remove("outputFilename");
			String outputDirectory = (String) parameters.get("outputDirectory");
			String configFilename = (String) parameters.remove("sgConfigFilename");
			
			// are we supposed to merge audio concurrently?
			if (null != parameters.get("concurrentAudioMerge")) {
				concurrentMerge = Boolean.parseBoolean((String) parameters.remove("concurrentAudioMerge"));
			}
			
			// are we supposed to use mp3 as output format?
			if (null != parameters.get("mp3Output")) {
				mergeToMp3 = Boolean.parseBoolean((String) parameters.remove("mp3Output"));
			}
			
			// sort out which files to use
			File inputFile = new File(inputFilename);
			File outputFile = new File(outputFilename);
			outputDir = new File(outputDirectory);
			if (!outputDir.exists()) {
				outputDir.mkdir();
			}
				
			configFilename = new File(getTransformerDirectory(), configFilename).getAbsolutePath();
			parseConfigFile(configFilename);
			
			// copy the additional files
			SrcExtractor extr = new SrcExtractor(inputFile);
			FileBunchCopy.copyFiles(extr.getBaseDir(), outputDir, extr.getSrcValues(), null, true);
			
			// Get ready to count the number of phrases to generate with TTS.
			FileInputStream fis = new FileInputStream(inputFile);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader plainReader = factory.createXMLEventReader(fis);
			BookmarkedXMLEventReader reader = new BookmarkedXMLEventReader(plainReader);
					
			sendMessage(Level.FINEST, i18n("COUNTING_PHRASES"));
			numSPoints = 0;
			String characterEncoding = "utf-8";
			ContextStack peek = new ContextStack();
			Set languages = new HashSet();
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
			File ttsConfig = new File((String)parameters.remove("ttsBuilderConfig"));
			Map m = new HashMap();
			m.put("transformer_dir", getTransformerDirectory().getAbsolutePath());
			
			TTSBuilder ttsb = new TTSBuilder(ttsConfig, m);
			for (Iterator it = languages.iterator(); it.hasNext(); ) {
				String lang = (String) it.next();
				TTS tts = ttsb.newTTS(lang);
				if (tts != null) {
					ttsEngines.put(lang, tts);
					if (lang.equals(new Locale("en", "", "").getLanguage())) {
						ttsEngines.put("default", tts);
					}
				}
			}
			
			sendMessage(Level.FINEST, i18n("DONE"));
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
						Set ns = new HashSet();
						for (Iterator it = curr.getNamespaces(); it.hasNext(); ) {
							ns.add(it.next());
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
							System.err.println("Error trying to process phrase near [line, col]: " + lineCol + " in file " + inputFile);
							e.printStackTrace();
						}
						if (isFirst) {
							addSilence(AFTER_FIRST);
						}
						
						// Add some slience before each hx?
						if (!lastSynchNumber.isEmpty()) {
							Integer integer = (Integer) lastSynchNumber.peek();
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
					StartDocument sd = (StartDocument)event;
					DEBUG("outputFile: " + outputFile);
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
			
			sendMessage(Level.FINEST, "Waiting for the last audio file merge, this may take a few minutes...");
			mergeAudio();
			if (concurrentMerge) {
				barrier.await();
			}
			closeStreams(fis, reader);
			terminateTTSInstances();
			
			sendMessage(Level.FINEST, i18n("DONE"));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TransformerRunException(e.getMessage(), e);
		}

		return true;
	}

	private void closeStreams(FileInputStream fis, BookmarkedXMLEventReader reader) throws XMLStreamException, IOException {
		writer.close();
		fis.close();
		reader.close();
	}

	private void terminateTTSInstances() {
		for (Iterator it = ttsEngines.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			TTS t = (TTS) ttsEngines.get(key);
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
		DEBUG("Next scope:");
		Document scope = getDOM(reader, se);
		DEBUG(scope);
		List introductions = new ArrayList();
		introductions.addAll(before);
		before.clear();
		
		List terminations = new ArrayList();
		
		int minLevel = getMinLevelBeforeNextSynchronization(reader);
		while (!after.isEmpty()) {
			Integer integer = (Integer) afterLevels.peek();
			if (minLevel <= integer.intValue()) {
				// terminate this element structure
				afterLevels.pop();
				StartElement elem = (StartElement) after.pop();
				terminations.add(elem);
			} else {
				// this element should not be terminated, hence we should
				// stop searching through the stack.
				break;
			}
		}
		
		// choose tts
		TTS tts = null;		
		Stack context = xmlContext.getContext();
		HashSet langs = new HashSet();
		int i = context.size();
		ContextStack.ContextInfo info = null;
		do {
			i--;
			info = (ContextStack.ContextInfo) context.get(i);
			Locale tmp = info.getLocale();
			if (null == tmp) {
				continue;
			}
			
			tts = (TTS) ttsEngines.get(tmp.getLanguage());
			if (null == tts) {
				langs.add(tmp.getLanguage());
			}
		} while (i > 0 && null == tts);
		
		if (null == tts) {
			tts = (TTS) ttsEngines.get("default");
			String warning = "No TTS for ";
			for (Iterator it = langs.iterator(); it.hasNext();) {
				warning += it.next();
				if (it.hasNext()) {
					warning += ", ";
				}
			}
			warning += " found. Fallback to default voice.";
		}
		
		File file = null;
		File temp = null;
		String startValue = clock.toString(SmilClock.FULL);
		long duration;
		
		
		SmilClock synchPointClock = new SmilClock(0);
		ArrayList synchPointFiles = new ArrayList();
		
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
			this.concurrentMerge = false;
			mergeAudio();
			this.concurrentMerge = concurrentMerge;
		}

		startValue = clock.toString(SmilClock.FULL);
		workingFiles.addAll(synchPointFiles);
		clock = new SmilClock(synchPointClock.millisecondsValue() + clock.millisecondsValue());
		
		return startValue;
	}
	
	
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
	
	
	private File getSilentFile(int timeMillis) throws IOException, UnsupportedAudioFileException {
		File model = null;
		if (workingFiles.size() < 1) {
			return null;
		}
		
		if (timeMillis <= 0) {
			return null;
		} 
		
		model = (File) workingFiles.get(0);
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
		if (mergeToMp3) {
			currentAudioFilename = goMp3(currentAudioFilename);
		}
		Attribute src = eventFactory.createAttribute(srcName, currentAudioFilename);
		
		Set attributes = new HashSet();
		for (Iterator it = event.asStartElement().getAttributes(); it.hasNext(); ) {
			attributes.add(it.next());
		}
		attributes.add(clipBegin);
		attributes.add(clipEnd);
		attributes.add(src);

		Set namespaces = new HashSet();
		for (Iterator it = event.asStartElement().getNamespaces(); it.hasNext(); ) {
			namespaces.add(it.next());
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
	 */
	private void parseConfigFile(String filename) throws ParserConfigurationException, SAXException, IOException {
		DEBUG("parseConfigFile()");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		domBuilder = factory.newDocumentBuilder();
		File temp = new File(filename);
		Document config = null;
		try {
			config = domBuilder.parse(temp);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		Node root = config.getDocumentElement();
		
		getConfigItems(absoluteSynch, root, "//absoluteSynch");
		getConfigItems(containsSynch, root, "//containsSynch");		
		getConfigItems(mergeAudio, root, "//mergeAudio");
		
		announceBefore = getConfigQName(root, "/sgConfig/announceAttributes/item[@id='before']");
		announceAfter = getConfigQName(root, "/sgConfig/announceAttributes/item[@id='after']");
		
		Node node = null;
		String tmp = null;
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterFirst/text()");
		if (node != null) {
			try {
				tmp = node.getTextContent();
				AFTER_FIRST = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse " + tmp + ", continuing...");
			} catch (Throwable t) {
				System.err.println(t);
			}
		}
		
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterLast/text()");
		if (node != null) {
			try {
				tmp = node.getTextContent();
				AFTER_LAST = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse " + tmp + ", continuing...");
			} catch (Throwable t) {
				System.err.println(t);
			}
		}
		
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/beforeAnnouncement/text()");
		if (node != null) {
			try {
				tmp = node.getTextContent();
				BEFORE_ANNOUNCEMENT = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse " + tmp + ", continuing...");
			} catch (Throwable t) {
				System.err.println(t);
			}
		}
		
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterAnnouncement/text()");
		if (node != null) {
			try {
				tmp = node.getTextContent();
				AFTER_ANNOUNCEMENT = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse " + tmp + ", continuing...");
			} catch (Throwable t) {
				System.err.println(t);
			}
		}
		
		node = XPathUtils.selectSingleNode(root, "/sgConfig/silence/afterRegularPhrase/text()");
		if (node != null) {
			try {
				tmp = node.getTextContent();
				AFTER_REGULAR_PHRASE = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse " + tmp + ", continuing...");
			} catch (Throwable t) {
				System.err.println(t);
			}
		}
	}
	
	private QName getConfigQName(Node root, String xPath) {
		Element elem = (Element) XPathUtils.selectSingleNode(root, xPath);
		String uri = elem.getAttribute("uri");
		String local = elem.getAttribute("local");
		String prefix = elem.getAttribute("prefix");
		return new QName(uri, local, prefix);
	}
	
	
	/* Avgör om ett element ska vara en synkpunkt.
	 * Indata: BookmarkedXMLEventReader reader, StartElement se.
	 */
	
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
		DEBUG("isSynchronizationPoint");
		
		int elemCount = 1;
		String textContent = "";
		while (elemCount > 0 && reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				elemCount++;
				String nodeName = event.asStartElement().getName().getLocalPart();
				
				if (nodeName != null && (absoluteSynch.contains(nodeName) || "sent".equals(nodeName))) {
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
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws InterruptedException
	 */
	private void mergeAudio() throws IOException, UnsupportedAudioFileException, InterruptedException {
		if (0 == workingFiles.size()) {
			return;
		}
		
		/* merge! */
		currentAudioFile = getCurrentAudioFile();
		List wf = new ArrayList();
		wf.addAll(workingFiles);
		File mp3file = mergeToMp3 ? goMp3(currentAudioFile) : null;
		
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
		DEBUG("getDOM(reader, " + se.getName().getLocalPart() + ")");
		String bookmark = "TPB Narrator.SpeechGenerator.getDOM";
		reader.setBookmark(bookmark);
		//long dt = System.currentTimeMillis();
		
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
		Stack treeStack = new Stack();
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
				
				Element parent = (Element) treeStack.peek();
				parent.appendChild(elem);
				treeStack.push(elem);
			} else if (e.isEndElement()) {
				elemCount--;
				treeStack.pop();
			} else if (e.isCharacters()) {
				String data = e.asCharacters().getData();
				Element parent = (Element) treeStack.peek();
				parent.appendChild(dom.createTextNode(data));
			}
		}
		
		//domTime += System.currentTimeMillis() - dt;
		reader.gotoAndRemoveBookmark(bookmark);
		DEBUG(dom);
		return dom;
	}
	
	/* Returnerar det minsta level-värde som förekommer fram till nästa 
	 * synkpunkt.
	 */
	
	/**
	 * Returns the minimum element level before the next synchronization point is encountered.
	 * This method assumes that an element representing a synchronization point just have been read.
	 * @param reader a reader for the input document.
	 * @return the minimum element level before the next synchronization point is encountered.
	 * @throws XMLStreamException
	 */
	private int getMinLevelBeforeNextSynchronization(BookmarkedXMLEventReader reader) throws XMLStreamException {
		DEBUG("getMinLevelBeforeNextSynchronization(reader)");
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
	
	/* Svarar på om startelementet ska annonseras på något sätt, dvs om 
	 * det finns before- eller after-attribut, typ. Dessa attribut måste
	 * finnas konfiguerade i konfigfilen till SpeechGenerator (inte samma som
	 * till transformern) med uri, lokal- och prefixdelar.
	 */
	
	/**
	 * Returns <code>true</code> if this element has some kind of announcement
	 * specified, <code>false</code> otherwise. That is, if this element contains any of the two attributes
	 * possibly specified in the configuration.
	 * @param se the start element.
	 * @return <code>true</code> if this element has some kind of announcement
	 * specified, <code>false</code> otherwise.
	 */
	private boolean isAnnouncement(StartElement se) {
		DEBUG("announceElement(" + se.getName().getLocalPart() + ")");
		
		for (Iterator atIt = se.getAttributes(); atIt.hasNext(); ) {
			Attribute at = (Attribute) atIt.next();
			if (null == at.getName()) {
				System.err.println("null som qname: " + se);
				//continue;
			}
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
		
		for (Iterator it = mergeAudio.iterator(); it.hasNext(); ) {
			String elem = (String) it.next();
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
	
	
	protected void DEBUG(String msg) {
		if (DEBUG) {
			System.err.println("SpeechGenerator: " + msg);
		}
	}
	
	protected void DEBUG(Document doc) {
		if (DEBUG) {
			System.err.println("--SpeechGenerator:");
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(doc);
				Result output = new StreamResult(System.err);
				idTransform.transform(input, output);
				System.err.println();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.err.println("--end SpeechGenerator");
			}
			
		}
	}
	
	protected String DEBUG_STR(Stack s) {
		String str = "/";
		for (int i = s.size() - 1; i >= 0; i--) {
			if (s.get(i) instanceof StartElement) {
				StartElement se = (StartElement) s.get(i);
				str += se.getName().getLocalPart() + "\t/";
			} else if (s.get(i) instanceof Integer) {
				str += s.get(i).toString() + "\t/";
			}
			
		}
		return str;
	}
	
	
	
	
	
	/* Returnerar speltiden i millisec. */
	/*private long getAudioFileDuration(File file) throws UnsupportedAudioFileException, IOException {
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = aff.getFormat();
		return (long)(1000.0 * aff.getFrameLength() / format.getFrameRate());
	}*/
	
	/*private void parseConfigFile(
			String filename, 
			Set absoluteSynch, 
			Set containsSynch,
			Set announceAttributes, 
			Set mergeAudio) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document config = db.parse(new File(filename));
		Node root = config.getDocumentElement();
		
		getConfigItems(absoluteSynch, root, "//absoluteSynch");
		getConfigItems(containsSynch, root, "//containsSynch");		
		getConfigItemsQNames(announceAttributes, root, "//announceAttributes");
		getConfigItems(mergeAudio, root, "//mergeAudio");		
	}*/
	
	
	
	/**
	 * Reads config data from the DOM rooted at <code>root</code> using
	 * the xpath expression <code>xPath</code>. The config items are stored
	 * as <code>String</code>s in the <code>Set set</code>.
	 * @param set the container for the config strings.
	 * @param root the root of the config DOM.
	 * @param xPath the expression to apply to <code>root</code>.
	 */
	private void getConfigItems(Set set, Node root, String xPath) {
		DEBUG("getConfigItems()");
		Set elemNames = new HashSet();
		Node parent = XPathUtils.selectSingleNode(root, xPath);
		NodeList items = parent.getChildNodes();
		DEBUG("xPath: " + xPath);
		for (int i = 0; i < items.getLength(); i++) {
			Node node = items.item(i);
			String content = node.getTextContent().trim();
			if (content.length() == 0) {
				continue;
			}
			elemNames.add(node.getTextContent());
			set.add(node.getTextContent().trim());
			DEBUG("tc: " + node.getTextContent());
		}
	}
}
