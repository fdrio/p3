package diskUnitExceptions;

public class NonExistingDiskException extends RuntimeException {
	/**
	 * Exception for the verification of the non existence of a disk instance.
	 * Is thrown when the disk does not exists.
	 */
	public NonExistingDiskException() {}
	
	public NonExistingDiskException(String arg0) {
		super(arg0);
	}

	public NonExistingDiskException(Throwable arg0) {
		super(arg0);
	}

	public NonExistingDiskException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
