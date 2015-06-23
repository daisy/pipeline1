/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package int_daisy_dtbMigrator;

import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.file.Directory;
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
	public void migrate(DtbDescriptor input, DtbDescriptor output, Map<String,String> parameters, Fileset inputFileset, Directory destination) throws MigratorException;
	
	/**
	 * Get a localized nicename for this migrator.
	 */
	public String getNiceName();
			
}
