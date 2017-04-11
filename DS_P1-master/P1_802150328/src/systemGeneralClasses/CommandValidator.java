package systemGeneralClasses;


public interface CommandValidator {
	/**
	 * Determine if the content of user's input matches a particular
	 * system's command
	 * @param sc the SystemCommand object to try to match to
	 * @param c the user's input line
	 * @param errMsg error message, if any
	 * @return true if valid; false, otherwise
	 */
	boolean validate(SystemCommand sc, CommandLine c, ErrMsg errMsg); 
}
