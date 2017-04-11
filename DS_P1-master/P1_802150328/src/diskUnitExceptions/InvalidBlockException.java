package diskUnitExceptions;

public class InvalidBlockException extends RuntimeException {
	/**
	 * Exception thrown when a blocks properties are not valid.
	 */
	public InvalidBlockException() {}
	
	public InvalidBlockException(String arg0) {
		super(arg0);
	}

	public InvalidBlockException(Throwable arg0) {
		super(arg0);
	}

	public InvalidBlockException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
}
