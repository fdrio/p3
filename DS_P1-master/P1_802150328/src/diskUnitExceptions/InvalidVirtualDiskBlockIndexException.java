package diskUnitExceptions;

/**
 * Exception for the invalid accessing of an index in a virtual disk block.
 * @author jahdiel.alvarez
 *
 */
public class InvalidVirtualDiskBlockIndexException extends RuntimeException {
	/**
	 * Exception for the invalid accessing of an index in a virtual disk block.
	 */
	public InvalidVirtualDiskBlockIndexException() {}

	public InvalidVirtualDiskBlockIndexException(String arg0) {
		super(arg0);
	}

	public InvalidVirtualDiskBlockIndexException(Throwable arg0) {
		super(arg0);
	}

	public InvalidVirtualDiskBlockIndexException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
