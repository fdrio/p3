package operandHandlers;

import java.util.ArrayList;

import systemGeneralClasses.OperandAnalyzer;
import systemGeneralClasses.StringCharactersExtractor;

public class NameOperandAnalyzer implements OperandAnalyzer {

	private static final NameOperandAnalyzer NAMEOPANALIZER = new NameOperandAnalyzer(); 
	
	private StringCharactersExtractor sce; 
	private boolean isValidOperand; 
	private String operand; 
	
	private NameOperandAnalyzer() { 
		
	}
	
	public static NameOperandAnalyzer getInstance() { 
		return NAMEOPANALIZER; 
	}
	
	public ArrayList<String> disectOperandFromInput(String is, int cp) {
		sce = new StringCharactersExtractor(is, cp); 
		isValidOperand = true; 

		if (!sce.hasMoreContent())
			isValidOperand = false; 
		else {
			operand = sce.extractStringUpToWhiteSpaceChar(); 
		}
		
		if (isValidOperand) 
			isValidOperand = OperandValidatorUtils.isValidName(operand); 
		
		// if still valid add to the list of validated operands for the 
		// command
		if (isValidOperand) { 
			ArrayList<String> opName = new ArrayList<String>(); 
			opName.add(operand); 
			return opName; 
		}
		else
			return null; 
	}

	public int currentIndexInInput() { 
		return sce.currentIndexValue(); 
	}


}
