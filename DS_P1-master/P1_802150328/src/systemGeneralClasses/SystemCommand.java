package systemGeneralClasses;


import java.util.StringTokenizer; 
import java.util.ArrayList; 


/**
 * Class to represent commands of the system. All commands follow a
 * similar format:  oper_name op1 op2 ... 
 * 
 * There shall be one instance of this odt for each command that the
 * system is able to process. For each command, it contains
 * the operation name (or command name) and the type for each 
 * operand, if any. 
 * 
 * @author Pedro I. Rivera-Vega
 *
 */
public class SystemCommand extends Command { 
	private CommandValidator cv; 
	// This is the command validator
	
    private CommandActionHandler cp; 
    // This processor establishes how the particular command is
    // executed in the particular system. This is really the
    // command engine for its execution....

    /**
     *  Constructor. 
     *  @param name  the name of the command or operation code
     *  @param operands the list of operants types as a string object
     *  @param cpp the associates command processor (how it is executed)
     */
    private SystemCommand(String commandGrammar, CommandActionHandler cpp, 
    		VariableLengthCommandValidator cv) 
    { 
    	super(commandGrammar);
    	cp = cpp; 
    	cv.addAnalyzersForOperands(this.operands()); 
    	this.cv = cv; 
    }

    /**
     *  Constructor. 
     *  @param name  the name of the command or operation code
     *  @param operands the list of operants types as a string object
     *  @param cpp the associates command processor (how it is executed)
     */
    private SystemCommand(String commandGrammar, CommandActionHandler cpp) { 
    	super(commandGrammar);
    	cp = cpp; 
    	this.cv = CommandProcessor.STDCOMMANDVALIDATOR; 
    }
    
    /**
     * Constructs a new instance of a Fixed Length System Command
     * @param commandGrammar
     * @param cpp
     * @return
     */
    public static SystemCommand getFLSC(String commandGrammar, 
    		CommandActionHandler cpp) 
    { 
    	return new SystemCommand(commandGrammar, cpp); 
    }

    /**
     * Constructs a new instance of a Variable Length System Command
     * @param commandGrammar
     * @param cpp
     * @return
     */
    public static SystemCommand getVLSC(String commandGrammar, 
    		CommandActionHandler cpp) 
    { 
    	return new SystemCommand(commandGrammar, cpp, new VariableLengthCommandValidator()); 
    }

    /** 
     *  To get an operand type of the particular command. 
     *  @param index operand index to be retrieved. 
     *         (PRECONDITION: index > 0 && index <= operandType.size()
     *  @return string value corresponding to the valid type of
     *          the specified operand. 
     */ 
    public String getOperandType(int index) { 
    		return tokensList.get(index); 
    }

    /**
     *  To get a reference to the associated command processor. 
     * @return reference to the associated command processor
     */
    public CommandActionHandler getCommandActionHandler() { 
    		return cp; 
    }
    
    
    public boolean validate(CommandLine c, ErrMsg errMsg) { 
    	return cv.validate(this, c, errMsg); 
    }
    
    public CommandValidator getCommandValidator() { 
    	return cv; 
    }
    
    
    public String operands() { 
    	String s = ""; 
    	for (int i=1; i<tokensList.size(); i++)
    		s = s + tokensList.get(i) + " "; 
    	return s; 
    }
}

