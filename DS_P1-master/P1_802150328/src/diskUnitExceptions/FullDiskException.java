package diskUnitExceptions;

/**
 * Exception for the verifying if a disk is full.
 * Is thrown when the disk has no more available space.
 * @author jahdiel.alvarez
 *
 */
public class FullDiskException extends RuntimeException {
	/**
	 * Exception for the verification of the existence of a disk instance.
	 * Is thrown when the disk already exists.
	 */
	public FullDiskException() {}
	
	public FullDiskException(String arg0) {
		super(arg0);
	}

	public FullDiskException(Throwable arg0) {
		super(arg0);
	}

	public FullDiskException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

