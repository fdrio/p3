package operandHandlers;

import java.util.ArrayList;

import systemGeneralClasses.OperandAnalyzer;
import systemGeneralClasses.StringCharactersExtractor;

public class IntOperandAnalyzer implements OperandAnalyzer {

	private static final IntOperandAnalyzer INTOPANALIZER = new IntOperandAnalyzer(); 
	
	private StringCharactersExtractor sce; 
	private boolean isValidOperand; 
	private String operand; 
	
	private IntOperandAnalyzer() { 
		
	}
	
	public static IntOperandAnalyzer getInstance() { 
		return INTOPANALIZER; 
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
			isValidOperand = OperandValidatorUtils.isValidInt(operand); 
		
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
