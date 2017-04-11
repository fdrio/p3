package diskUnitExceptions;

public class InvalidParameterException extends RuntimeException {
	/**
	 * Exception for the verification of the proper properties
	 * in the creation of a RandomAccessFile.
	 */
	public InvalidParameterException() {}
	
	public InvalidParameterException(String arg0) {
		super(arg0);
	}

	public InvalidParameterException(Throwable arg0) {
		super(arg0);
	}

	public InvalidParameterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
