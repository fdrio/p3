package operandHandlers;

import java.util.ArrayList;
import exceptions.InvalidIntListExpException;
import exceptions.InvalidIntListExprComponentException;

import systemGeneralClasses.OperandAnalyzer;
import systemGeneralClasses.StringCharactersExtractor;



public class IntListOperandAnalyzer 
implements OperandAnalyzer
{
	private static final IntListOperandAnalyzer INT_LST_OPERAND_ANALYZER =
		new IntListOperandAnalyzer(); 
	
	private ArrayList<String> itemsList; 
	private StringCharactersExtractor sce; 
	private boolean isValidOperand; 

	private IntListOperandAnalyzer() {}
	
	public static IntListOperandAnalyzer getInstance() { 
		return INT_LST_OPERAND_ANALYZER;
	}
	
	public ArrayList<String> disectOperandFromInput(String is, int cp) { 
		sce = new StringCharactersExtractor(is, cp); 
		itemsList = new ArrayList<String>(); 

		isValidOperand = true;  

		try { 
			analyzeComponentSequence(); 
		}
		catch (Exception e) { 
			isValidOperand = false; 
		}
		
		if (isValidOperand)
			return itemsList; 
		else 
			return null; 
	}
	
	private void analyzeComponentSequence() 
	throws InvalidIntListExpException
	{
		try { 
			do { 
				analyzeExpressionComponent(); 
			} while (sce.hasMoreContent()); 
		} catch (Exception e) { 
			isValidOperand = false; 
		}
	}

	private void analyzeExpressionComponent() 
	throws IndexOutOfBoundsException, InvalidIntListExprComponentException  
	{
		sce.skipSpaces(); 
		String item = sce.extractStringUpToWhiteSpaceChar(); 
		if (OperandValidatorUtils.isValidInt(item)) {			
			// add item to item's list
			itemsList.add(item); 
		} 
		else 
			throw new InvalidIntListExprComponentException(
			"Invalid name in attributes list.");

	}
	
	public int currentIndexInInput() { 
		return sce.currentIndexValue(); 
	}


}
