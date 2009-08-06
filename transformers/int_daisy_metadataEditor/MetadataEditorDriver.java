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
package int_daisy_metadataEditor;

import java.io.File;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;

/**
 * This transformer is able to edit metadata of a file.
 * 
 * <p>
 * It requires the following parameters:
 * </p>
 * <dl>
 * <dt><code>input</code></dt>
 * <dd>The file or fileset manifest to edit the metadata of.</dd>
 * 
 * <dt><code>name</code></dt>
 * <dd>The name of the metadata to edit</dd>
 * 
 * <dt><code>value</code></dt>
 * <dd>A comma-separated list of values for the metadata.</dd>
 * 
 * <dt><code>mode</code></dt>
 * <dd>The edition mode. This can be:
 * <dl>
 * <dt>ADD</dt>
 * <dd>Appends a new metadata value even if previous values already exist (this
 * can however be impossible for some non-repeatable metadata, in which case it
 * will revert to the 'IGNORE' mode).</dd>
 * <dt>MERGE</dt>
 * <dd>Merges the value with the existing metadata content: does nothing if a
 * metadata with the same name and value is already present, appends a new value
 * otherwise.</dd>
 * <dt>OVERWRITE</dt>
 * <dd>Overwrites the first occurrence of the metadata of the given name with
 * the given value.</dd>
 * <dt>IGNORE</dt>
 * <dd>Adds a new metadata only if no metadata with the given name is already
 * present.</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * <p>
 * The transformer first creates a {@link Fileset} from the input, then gets an
 * implementation of the {@link MetadataEditor} for this fileset type from a
 * {@link MetadataEditorFactory} to edit the metadata for each value in the
 * comma-separated value list.
 * </p>
 * 
 */
public class MetadataEditorDriver extends Transformer {

	private final MetadataEditorFactory editorFactory = new MetadataEditorFactory();
	private final FilesetErrorHandler filsetErrorHandler = new FilesetErrorHandler() {
		public void error(FilesetFileException ffe) throws FilesetFileException {
			sendMessage(ffe);
		}
	};

	public MetadataEditorDriver(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/**
	 * Adds the values in the <code>value</code> parameter (comma-separated) as
	 * metadata named with the <code>name</code> parameter in the file specified
	 * by the <code>input</code> parameter, using the overwrite mode specified
	 * in the <code>mode</code> parameter.
	 * 
	 * <p>
	 * This transformer first creates a {@link Fileset} from the input, then
	 * gets an implementation of the {@link MetadataEditor} for this fileset
	 * type from the {@link MetadataEditorFactory} to add the metadata specified
	 * by the <code>name/value</code> pair.
	 * </p>
	 * 
	 * @param parameters
	 *            this transformer parameters
	 */
	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		try {
			File input = FilenameOrFileURI.toFile(parameters.remove("input"));
			String metaName = parameters.remove("name");
			String[] metaValues = parameters.remove("value").split(",");
			MetadataEditor.Mode mode = MetadataEditor.Mode.valueOf(parameters
					.remove("mode"));
			Fileset fileset = new FilesetImpl(input.toURI(),
					filsetErrorHandler, false, true);
			MetadataEditor metadataEditor = editorFactory.getEditor(fileset
					.getFilesetType());
			for (String value : metaValues) {
				metadataEditor.editMetadata(input, metaName, value.trim(), mode);
			}

		} catch (Exception e) {
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}

		return true;

	}
}
