package systemGeneralClasses;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Represents a candidate command to be executed. The command
 * relates to a particular SystemCommand and to a CommandProcessor that 
 * dictates how the command is to be executed in the particular system.
 * @author Pedro I. Rivera Vega
 *
 */
public class FixedLengthCommand extends Command { 
	
    /**
     * Constructor of a Command object. 
     * @param line the attempted command with its operands.
     */
	public FixedLengthCommand(CommandLine line) { 
		super(line.toString()); 
	} 
			
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getOperand(int index) { 
		// operands are in positions 1, 2, � of the list cTokensList
		// precondition:  1 <= index < cTokensList.size()
		return tokensList.get(index); 
	}


}

