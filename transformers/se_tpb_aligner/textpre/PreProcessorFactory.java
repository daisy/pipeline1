package se_tpb_aligner.textpre;

import java.util.HashSet;
import java.util.Set;

import se_tpb_aligner.textpre.impl.FallbackPreProcessorImpl;
import se_tpb_aligner.textpre.impl.TpbPreProcessorImpl;

/**
 * A factory that produces instances of PreProcessor.
 * @author Markus Gylling
 */
public class PreProcessorFactory {
	Set <Class<? extends PreProcessor>> mRegistry = null;
	Class<? extends PreProcessor> mFallback = null;
	
	private PreProcessorFactory() {
		/*
		 * Build the factory registry of aligners
		 * For now, hardcoded here, use discovery later
		 */
		mRegistry = new HashSet<Class <? extends PreProcessor>>();
		mRegistry.add(TpbPreProcessorImpl.class);
		
		mFallback = FallbackPreProcessorImpl.class;
	}
	
	public static PreProcessorFactory newInstance() {
		return new PreProcessorFactory();
	}
	
	/**
	 * @param language An ISO 3066 compliant language identifier string
	 */
	public PreProcessor getPreProcessor(String language) throws PreProcessorFactoryException {
		try{			
			for (Class<? extends PreProcessor> c : mRegistry) {		
				PreProcessor test = c.newInstance();
				if(test.supportsLanguage(language)) {
					return test;
				}								 			
			}
		} catch (Exception e) {
			throw new PreProcessorFactoryException(e.getMessage(),e);					
		}
		
		throw new PreProcessorFactoryException("language "+language+" not supported");
	}

	public PreProcessor getFallbackInstance() throws PreProcessorFactoryException {		
		try {
			return mFallback.newInstance();
		} catch (Exception e) {
			throw new PreProcessorFactoryException(e.getMessage(),e);
		}
	}
}
