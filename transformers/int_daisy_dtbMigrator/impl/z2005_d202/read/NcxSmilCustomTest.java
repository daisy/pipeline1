package int_daisy_dtbMigrator.impl.z2005_d202.read;

import int_daisy_dtbMigrator.BookStruct;

/**
 *
 * @author Markus Gylling
 */
public class NcxSmilCustomTest {
	final String override;
	final String defaultState;
	final String id;
	final BookStruct bookStruct;

	NcxSmilCustomTest(String id, String defaultState, String override, String bookStruct) {
		this.id = id;
		this.defaultState = defaultState;
		this.override = override;
		this.bookStruct = BookStruct.valueOf(bookStruct);			
	}
}
