package se_tpb_aligner.subtree;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

import se_tpb_aligner.subtree.impl.DtbookLevelSubTreeHandler;
import se_tpb_aligner.subtree.impl.DtbookPageSubTreeHandler;
import se_tpb_aligner.util.XMLSource;


/**
 * A factory producing subtreehandlers.
 * @author Markus Gylling
 */
public class SubTreeHandlerFactory {

	Set <Class<? extends SubTreeHandler>> registry = null;
	
	private SubTreeHandlerFactory() {
		/*
		 * Build the factory registry of subtreehandlers
		 * For now, hardcoded here
		 */
		registry = new HashSet<Class <? extends SubTreeHandler>>();
		registry.add(DtbookLevelSubTreeHandler.class);
		registry.add(DtbookPageSubTreeHandler.class);
	}
	
	public static SubTreeHandlerFactory newInstance() {
		return new SubTreeHandlerFactory();
	}
	
	/**
	 * @param doc The document to be split into subtrees
	 * @param divider The strategy to employ when dividing the tree.
	 * @return The first registered subtreehandler that supports the input document type and DivisionStrategy.
	 * @throws SubTreeHandlerFactoryException 
	 */
	public SubTreeHandler getHandler(XMLSource doc, DivisionStrategy divider) throws SubTreeHandlerFactoryException {		
		Peeker peeker = null;		
		try{

			peeker = PeekerPool.getInstance().acquire();
			PeekResult peek = peeker.peek(doc);
			
			for (Class<? extends SubTreeHandler> c : registry) {		
				SubTreeHandler test = c.newInstance();
				if(test.supportsDivisionStrategy(divider) && test.supportsDocumentType(peek)) {
					Constructor<?> constr = c.getDeclaredConstructor(new Class[] {XMLSource.class, DivisionStrategy.class});
					return  (SubTreeHandler) constr.newInstance(new Object[] {doc,divider});
				}								 			
			}
		} catch (Exception e) {
			throw new SubTreeHandlerFactoryException(e.getMessage(),e);					
		}finally{
			PeekerPool.getInstance().release(peeker);
		}		
		throw new SubTreeHandlerFactoryException("No SubtreeHandler could be instantiated for the given input document type and language");		
	}
}
