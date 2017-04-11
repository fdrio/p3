package diskUnitExceptions;

/**
 * Exception for the verification of the existence of a disk instance.
 * Is thrown when the disk already exists.
 * @author jahdiel.alvarez
 *
 */
public class ExistingDiskException extends RuntimeException {
	/**
	 * Exception for the verification of the existence of a disk instance.
	 * Is thrown when the disk already exists.
	 */
	public ExistingDiskException() {}
	
	public ExistingDiskException(String arg0) {
		super(arg0);
	}

	public ExistingDiskException(Throwable arg0) {
		super(arg0);
	}

	public ExistingDiskException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
