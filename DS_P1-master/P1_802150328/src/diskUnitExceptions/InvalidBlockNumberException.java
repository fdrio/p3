package diskUnitExceptions;

public class InvalidBlockNumberException extends RuntimeException {
	/**
	 * Exception thrown when block number is not within the accepted range. 
	 */
	public InvalidBlockNumberException() {}
	
	public InvalidBlockNumberException(String arg0) {
		super(arg0);
	}

	public InvalidBlockNumberException(Throwable arg0) {
		super(arg0);
	}

	public InvalidBlockNumberException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
}
