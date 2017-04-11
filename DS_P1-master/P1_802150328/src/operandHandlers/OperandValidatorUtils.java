package operandHandlers;

import systemGeneralClasses.OperandAnalyzer;


public class OperandValidatorUtils {
	
	public static boolean isValidName(String operand) { 
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

	public static boolean isValidInt(String operand) { 
		try { 
			Integer.parseInt(operand); 
			return true; 
		} 
		catch(Exception e) { 
			return false; 
		}		
	}
	
	public static OperandAnalyzer getAnalyzerFor(String op) {
		if (op.equals("int"))
			return IntOperandAnalyzer.getInstance(); 
		else if (op.equals("int_list"))
			return IntListOperandAnalyzer.getInstance(); 
		else if (op.equals("name"))
			return NameOperandAnalyzer.getInstance(); 
		
		// need to expand the above if to include for other analyzers that
		// are required...
		
		
		// these three are good enough for the moment...
		return null;   // if nothing matches
	}


}
