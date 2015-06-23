package se_tpb_aligner.align;

import java.util.HashSet;
import java.util.Set;

import se_tpb_aligner.align.impl.FallbackAlignerImpl;
import se_tpb_aligner.align.impl.TPBAlignerImpl;

/**
 * A factory that produces instances of Aligner.
 * @author Markus Gylling
 */
public class AlignerFactory {

	Set <Class<? extends Aligner>> registry = null;
	Class<? extends Aligner> mFallback = null;
	
	public AlignerFactory() {
		/*
		 * Build the factory registry of aligners
		 * For now, hardcoded here, use discovery later
		 */
		registry = new HashSet<Class <? extends Aligner>>();
		registry.add(TPBAlignerImpl.class);		
		
		mFallback = FallbackAlignerImpl.class;
	}
	
	public static AlignerFactory newInstance() {
		return new AlignerFactory();
	}
	
	/**
	 * @param language An ISO 3066 compliant language identifier string
	 * @throws AlignerFactoryException 
	 */
	public Aligner getAligner(String language) throws  AlignerFactoryException {		
		try{			
			for (Class<? extends Aligner> c : registry) {		
				Aligner test = c.newInstance();
				if(test.supportsLanguage(language)) {
					return test;
				}								 			
			}
		} catch (Exception e) {
			throw new AlignerFactoryException(e.getMessage(),e);					
		}				
		throw new AlignerFactoryException("language "+language+" not supported");
	}
	
	public Aligner getFallbackInstance() throws AlignerFactoryException {		
		try {
			return mFallback.newInstance();
		} catch (Exception e) {
			throw new AlignerFactoryException(e.getMessage(),e);
		}
	}
}
