package systemGeneralClasses;

/**
 * An object of this type is a wrapper over a String. 
 * It allows dissect the string, character by character...
 * @author pirvos
 *
 */
public class StringCharactersExtractor 
{
	private int cp; 
	private String input; 
		
	public StringCharactersExtractor(String input, int cp) { 
		this.input = input; 
		this.cp = cp; 
	}
	
	public void  skipSpaces() 
	throws IndexOutOfBoundsException 
	{ 
		while (Character.isWhitespace(currentChar())) { 
			skipCurrentChar(); 
		} 
	}

	public void skipCurrentChar() { 
		cp++; 
	}
	
	public char currentChar() { 
		return input.charAt(cp); 
	}
	
	public char extractCurrentChar() 
	throws IndexOutOfBoundsException 
	{ 
		char cc = input.charAt(cp); 
		cp++; 
		return cc; 
	}
		
	public boolean hasMoreContent() { 
		for (int i=cp; i<input.length(); i++)
			if (!Character.isWhitespace(input.charAt(i)))
				return true; 
		return false; 
	}

	public int currentIndexValue() { 
		return cp; 
	}
	
	/**
	 * Reads the substring in input (inherited instance field),
	 * from the current character
	 * up to right before the first '\"' (character ") found, if any. 
	 * @return the substring read
	 * @throws IndexOutOfBoundsException if second closing quote " is never found
	 */
	public String extractStringUpToQuote() 
	throws IndexOutOfBoundsException 
	{
		String s = ""; 
		while (currentChar() != '\"') {
			s = s + extractCurrentChar(); 
		}
		return s;
	}

	public String extractStringUpToWhiteSpaceChar() {
		String s = ""; 
		while (hasMoreContent() && !Character.isWhitespace(currentChar())) {
			s = s + extractCurrentChar(); 
		}
		return s;
	}

	


}
