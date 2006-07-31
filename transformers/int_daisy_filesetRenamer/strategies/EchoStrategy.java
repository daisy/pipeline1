//package int_daisy_filesetRenamer.strategies;
//
//import java.net.URI;
//import java.util.Iterator;
///**
// * <p>A naming strategy implementation that does not rename anything, ever.</p>
// * <p>Used as a recovery path.</p>  
// * @author Markus Gylling
// */
//public class EchoStrategy extends AbstractStrategy {
//
//	public void createStrategy() {
//		//populate the URI(old), URI(new) map.
//		this.namingStrategy.clear();
//		for (Iterator iter = this.inputFileset.getLocalMembersURIs().iterator(); iter.hasNext();) {
//			URI uri = (URI)iter.next();
//			this.namingStrategy.put(uri,uri);
//		}		
//	}
//}
