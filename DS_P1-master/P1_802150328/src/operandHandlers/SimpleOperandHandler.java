package operandHandlers;

import java.security.InvalidParameterException;

/**
import dataTypesHandlers.DateComparator;
import dataTypesHandlers.DateValidator;
import dataTypesHandlers.IntComparator;
import dataTypesHandlers.IntValidator;
import dataTypesHandlers.MoneyComparator;
import dataTypesHandlers.MoneyValidator;
**/


public class SimpleOperandHandler {
	
	/**
	 * Validates of a token complies with the grammar
	 * @param tType The type of token to be matched with (the grammar)
	 * @param token The token content to be analyzed. 
	 * @return true if valid; false, otherwise.
	 */
	public static boolean isValidToken(String tType, String token) { 
		// current simple token types are: name, int, 
		// more need to be added...
		if (tType.equals("name")) 
			return isValidName(token); 
		else if (tType.equals("int"))
			return isValidInt(token); 
		else 
			return false; 
		
		
	}

	private static boolean isValidName(String operand) { 
		if (operand.length() == 0) 
			return false; 
		
		// operand is not empty string 
		boolean isName = (Character.isLetter(operand.charAt(0)));
		int cp=1; 
		while (cp < operand.length() && isName) { 
			char c = operand.charAt(cp); 
			if (!(Character.isDigit(c) || Character.isLetter(c)))
				isName = false; 
			cp++; 
		}		
		return isName;

	}

	private static boolean isValidInt(String operand) { 
		try { 
			Integer.parseInt(operand); 
			return true; 
		} 
		catch(Exception e) { 
			return false; 
		}		
	}

}
