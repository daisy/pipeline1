package int_daisy_dtbMigrator.impl.z2005_d202;

import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.interfaces.Fileset;

import int_daisy_dtbMigrator.DtbDescriptor;
import int_daisy_dtbMigrator.Migrator;

/**
 * An implementation of DtbMigrator that supports downgrading z2005 books to Daisy 2.02
 * @author Markus Gylling, Per Sennels, Linus Ericson
 */

public class MigratorImpl implements Migrator {
	private TransformerDelegateListener mTransformer = null;
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#migrate(int_daisy_dtbMigrator.DtbDescriptor, int_daisy_dtbMigrator.DtbDescriptor, java.util.Map, org.daisy.util.fileset.interfaces.Fileset, org.daisy.util.file.EFolder)
	 */
	public void migrate(DtbDescriptor input, DtbDescriptor output, Map parameters,
			Fileset inputFileset, EFolder destination) {				
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#setDelegateListener(org.daisy.pipeline.core.transformer.TransformerDelegateListener)
	 */
	public void setListener(
			TransformerDelegateListener transformer) {
		mTransformer = transformer;		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#supports(int_daisy_dtbMigrator.DtbDescriptor, int_daisy_dtbMigrator.DtbDescriptor, java.util.Map)
	 */
	public boolean supports(DtbDescriptor input, Fileset inputFileset, DtbDescriptor output, Map<String,String> parameters) {
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#getNiceName()
	 */
	public String getNiceName() {
		// TODO Auto-generated method stub
		return null;
	}



}
