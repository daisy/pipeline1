package int_daisy_dtbMigrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.fileset.interfaces.Fileset;

/**
 * A factory producing DTB migrators.
 * @author Markus Gylling
 */
public class MigratorFactory {
	private List<Migrator> registry = null;
	private TransformerDelegateListener listener = null;
	
	private MigratorFactory(TransformerDelegateListener transformer) {
		registry = new ArrayList<Migrator>();
		registry.add(new int_daisy_dtbMigrator.impl.d202_z2005.MigratorImpl());
		registry.add(new int_daisy_dtbMigrator.impl.z2005_d202.MigratorImpl());
		listener = transformer;
	}
	
	public static MigratorFactory newInstance(TransformerDelegateListener transformer) {
		return new MigratorFactory(transformer);
	}
	
	/**
	 * Create a new DTB migrator.
	 * @param input A descriptor of the DTB to be transformed
	 * @param output The desired output type and version
	 * @param parameters Additional info on the desired conversion. May be null.
	 * @throws MigratorFactoryException If a migrator could not be allocated.
	 */
	public Migrator newMigrator(DtbDescriptor input, Fileset inputFileset, DtbDescriptor output, Map<String,String> parameters) throws MigratorFactoryException {
		for(Migrator dm : registry) {
			if(dm.supports(input, inputFileset, output, parameters)) {
				return dm;
			}
		}		
		String message = listener.delegateLocalize("FACTORY_NO_IMPL_FOUND", new Object[]{input,output});		
		throw new MigratorFactoryException(message);
	}
	
}
