package int_daisy_dtbMigrator;

import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.Fileset;

/**
 * Base interface for DTB migrators. Any implementor of this interface must also provide
 * a zero-argument constructor as its default constructor.
 * @author Markus Gylling
 */
public interface Migrator {

	/**
	 * Method called during factory discovery. Does this migrator support converting input to output?
	 * @param input The source DTB
	 * @param output The result DTB
	 * @param parameters Additional info on the conversions. May be null. 
	 */
	public boolean supports(DtbDescriptor input, Fileset inputFileset, DtbDescriptor output, Map<String,String> parameters);
	
	/**
	 * Register a TransformerDelegateListener with this DtbMigrator.
	 */
	public void setListener(TransformerDelegateListener transformer);
	
	/**
	 * Migrate a DTB.
	 * @throws MigratorException If migration fails.
	 */
	public void migrate(DtbDescriptor input, DtbDescriptor output, Map<String,String> parameters, Fileset inputFileset, EFolder destination) throws MigratorException;
	
	/**
	 * Get a localized nicename for this migrator.
	 */
	public String getNiceName();
			
}
