package systemGeneralClasses;

import java.util.ArrayList;
import java.util.StringTokenizer;

import operandHandlers.IntOperandAnalyzer;
import operandHandlers.OperandValidatorUtils;

/**
 * This defines a command validator type. These type of validators are
 * used for those commands with a complex grammar in which the 
 * number of tokens in the operand value is variable. For example: 
 * a list expression, etc. For such type of command it holds the
 * appropriate operand analyzers as the members of an internal list. 
 * These are used to properly analyze the operands that the user
 * includes in a particular command instance of a system command
 * to which the CommandValidator is assigned to.
 * 
 * @author pirvos
 *
 */
public class VariableLengthCommandValidator implements CommandValidator {
	private ArrayList<OperandAnalyzer> opAnalyzersList; 
	private ArrayList<ArrayList<String>>  lastItemsList; 
	// this is the list of list of items extracted from all the operands (one list per
	// operand) when the last input line from user is analyzed....

	/**
	 * 
	 * @param ops the list of tokens in the grammar of
	 * 	the command.
	 */
	public VariableLengthCommandValidator() {
		opAnalyzersList = new ArrayList<OperandAnalyzer>();  
	}
	
	public void addAnalyzersForOperands(String ops) { 
		StringTokenizer operands = new StringTokenizer(ops); 
		while (operands.hasMoreTokens()) { 
			opAnalyzersList.add(OperandValidatorUtils.getAnalyzerFor(operands.nextToken())); 
		}
	}
	
	public boolean validate(SystemCommand corrSC, CommandLine userInput, ErrMsg errMsg) {
		String userOperands = userInput.stringOfOperands(); 
		int nOperands = corrSC.getNumberOfOperands(); 
		this.lastItemsList = new ArrayList<ArrayList<String>>(); 
		int cp = 0; 
		for (int i=1; i<=nOperands; i++) { 
			VariableLengthCommandValidator scv = 
				(VariableLengthCommandValidator) corrSC.getCommandValidator();
			OperandAnalyzer oa = scv.getOperandAnalyzer(i); 
			ArrayList<String> itemsListInLastOperand = oa.disectOperandFromInput(userOperands, cp); 
			if (itemsListInLastOperand != null) { 
				lastItemsList.add(itemsListInLastOperand); 
				cp = oa.currentIndexInInput(); 
			}
			else { 
				errMsg.setMessage("Operand number " + i + " does not match correct syntax."); 
				return false; 
			}
		}
		// still need to verify if input line has more tokens. 
		// in that case the input command is not valid...
		// but don't do it now... later...
		
		// if here, then all tests have been passed...
		return true; 
	}
	
	public OperandAnalyzer getOperandAnalyzer(int index) { 
		return opAnalyzersList.get(index - 1); 
	}
	
	public ArrayList<ArrayList<String>> getLastItemsLists() { 
		return this.lastItemsList; 
	}
}
