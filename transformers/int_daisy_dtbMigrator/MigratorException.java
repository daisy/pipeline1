package int_daisy_dtbMigrator;

import org.daisy.util.exception.BaseException;

/**
 * Exception thrown during DTB migrator construction time.
 * @author Markus Gylling
 */
public class MigratorException extends BaseException {
	
	public MigratorException(String message) {
		super(message);
	}

	public MigratorException(String message, Throwable t) {
		super(message);
	}
	
	private static final long serialVersionUID = -7310258911197380265L;

}
