package int_daisy_metadataEditor;

import java.io.File;

/**
 * A metadata editor able to edit name/value metadata in a file.
 */
public interface MetadataEditor {
	/**
	 * Define the possible overwrite modes for a metadata edition.
	 * 
	 */
	public enum Mode {
		/**
		 * In the ADD mode the metadata value is appended even if previous
		 * values already exist (this can however be impossible for some
		 * non-repeatable metadata, in which case it will revert to the 'IGNORE'
		 * mode).
		 */
		ADD,
		/**
		 * In the MERGE mode the metadata value is merged with the existing
		 * metadata content: it does nothing if a metadata with the same name
		 * and value is already present, appends a new value otherwise.
		 */
		MERGE,
		/**
		 * In the OVERWRITE mode the first occurrence of the metadata of the
		 * given name is overwritten with the given value.
		 */
		OVERWRITE,
		/**
		 * In the IGNORE mode a new metadata is added only if no metadata with
		 * the given name is already present.
		 */
		IGNORE
	}

	/**
	 * Edit the metadata specified with the name/value pair in the given file,
	 * abiding by the given edition mode.
	 * 
	 * @param file
	 *            the file of fileset manifest in which to edit the metadata
	 * @param name
	 *            the name of the metadata to edit
	 * @param value
	 *            the value of the metadata to edit
	 * @param mode
	 *            the edition overwrite mode (see {@link Mode}).
	 * @throws Exception
	 *             if an exception occurs during the edition.
	 */
	public void editMetadata(File file, String name, String value, Mode mode)
			throws Exception;
}
