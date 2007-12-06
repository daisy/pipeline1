package int_daisy_recorder2dtb.read.audacity;


import java.util.regex.Pattern;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.dtb.ncxonly.model.Semantic;
import org.daisy.util.dtb.ncxonly.model.Value;

/**
 *
 * @author Markus Gylling
 */
public class AupLabel {
	private static final Pattern PATTERN_ALPHA_NUMERIC = Pattern.compile("[0-9]*");
	private static final Pattern PATTERN_ROMAN_NUMERAL = Pattern.compile("[MmDdCcLlVvIi]*");
	private double mStartSeconds = -1;
	private double mEndSeconds = -1;
	private String mInitTitle;
	private Value mValue;
	private Semantic mSemantic;
	private TransformerDelegateListener mTransformer = null;
	
	
	public AupLabel(String t, String t1, String title, TransformerDelegateListener tdl) {
		mTransformer = tdl;
		mInitTitle = title;
		/*
		 * t and t1 are second strings, eg 4.70888889
		 * dont store as smilclock as we loose precision 
		 */
		mStartSeconds = Double.parseDouble(t);
		mEndSeconds = Double.parseDouble(t1);
		mSemantic = getSemantic(title);
		mValue = getValue(title,mSemantic);		
		//System.err.println("AupLabel times: " + mStartClock.toString() + "->" + mEndClock.toString());
	}
	
	/**
	 * Is this an Audition one-point marker, or a marker
	 * with a duration? 
	 */
	public boolean hasDuration() {	
		//very short intervals are considered non-durs, therefore allow some diff.		
		return (mEndSeconds - mStartSeconds > 0.1);
	}
	
	/**
	 *  Get the duration of this label in seconds. If this is a one-point marker, 0 is returned. 
	 */
	public double getDurationSeconds() {
		if(!hasDuration()) {
			return 0;
		}
		return mEndSeconds - mStartSeconds;
	}
	
	private Value getValue(String title, Semantic sem) {
		
		if(sem== Semantic.UNKNOWN) return new Value(title);
		
		try{
			if(title!=null||title.length()>0){
				char semanticStart = sem.toString().charAt(0);
				switch(semanticStart) {
					case 'p':
						return new Value(title.substring(1).trim());
					case 'h':
						return new Value(title.substring(2).trim());						
				}						
			}
		}catch (Exception e) {

		}
		String message = mTransformer.delegateLocalize("MARKER_ERROR", title);
		mTransformer.delegateMessage(this, message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);		
		return new Value("MARKER ERROR");
	}

	private Semantic getSemantic(String title) {
		//pages always and exclusively begin with 'p'
		//headings always and exclusively begin with 'hn'
		
		try{			
			if(title!=null||title.length()>0){
				char first = title.charAt(0);
				switch (first) {
					case 'p':
						String val = title.substring(1);
						if(isNumeric(val)) {
							return Semantic.PAGE_NORMAL;
						}else if(isRoman(val)) {
							return Semantic.PAGE_FRONT;
						}
						return Semantic.PAGE_SPECIAL;								
					case 'h':
						char depth = title.charAt(1);
						switch(depth) {
							case '1': return Semantic.HEADING1;
							case '2': return Semantic.HEADING2;
							case '3': return Semantic.HEADING3;
							case '4': return Semantic.HEADING4;
							case '5': return Semantic.HEADING5;
							case '6': return Semantic.HEADING6;
						}
				}
			}
		}catch (Exception e) {
			
		}	
		String message = mTransformer.delegateLocalize("MARKER_ERROR", title);
		mTransformer.delegateMessage(this, message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);
		return Semantic.UNKNOWN;
	}
	
	private boolean isNumeric(String string) {
		return PATTERN_ALPHA_NUMERIC.matcher(string).matches();	
	}
		
	private boolean isRoman(String string) {
		return PATTERN_ROMAN_NUMERAL.matcher(string).matches();
	}

	public double getStartTimeSeconds() {
		return mStartSeconds;
	}
	
	public double getEndTimeSeconds() {
		return mEndSeconds;
	}
	
	public Value getValue() {
		return mValue;
	}

	public Semantic getSemantic() {
		return mSemantic;
	}

}
