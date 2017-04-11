package systemGeneralClasses;


import java.util.ArrayList;

import stack.IntStack;

/**
 * 
 * @author pirvos
 *
 * Class implementing the data type for objects to manage commands
 * as they are given by the user of the system. 
 * 
 */

public abstract class CommandProcessor {

	public final static FixedLengthCommandValidator 
	   STDCOMMANDVALIDATOR = new FixedLengthCommandValidator(); 
	
	private  ArrayList<SystemCommand>[] commandList; 
	// The lists of commands that are possible to be executed 
	// in this type of component. Each list corresponds to the
	// possible commands on each of the states of the system. 
	// Every entry in each list contains a particular system 
	// command object, and for each one of these objects it 
	// contains the operator name, the type of each operand and
	// the command-processor of the command in the particular 
	// system. 
	// 
	
	protected IntStack currentState; 
	// In some ocasions it may be required to know the previous
	// state ... just in case....
	
	// Every command processor has at least one general state....
	protected static final int GENERALSTATE = 0; 
	
	/**
	 * This method does some preliminary processing of the attempted 
	 * command being read from user.Determines if a CommandLine object 
	 * correctly matches the syntax of some particular system command.
	 * @param c CommandLine object that is to be analyzed
	 * @param errMsg References an empty object where an error
	 *   message can be inserted if an error is detected. 
	 * @return true if command command is valid; false, otherwise. 
	 *   If the command is valid, it has the side effect of adding
	 *   reference to the particular system command.
	 */
	public void preProcessCommand(CommandLine c, ErrMsg errMsg) {
	    // check if the CommandLine c is a syntactically correct command
		
		// see if there is a matching system command for the command
		// referred to in the input line
		SystemCommand mSCommand = searchCommand(c.getToken(1)); 
		if (mSCommand == null) { 
			errMsg.setMessage("Command " + c.getToken(1) + " does not exist.");
			c.setSc(null); 
		}
		else if (mSCommand.validate(c, errMsg)) 
				c.setSc(mSCommand);
		else c.setSc(null); 
	} 
	
	/**
	 * 
	 * @param cl
	 * @return
	 */
	private SystemCommand searchCommand(String cname) {  
		for (int i=0; i<commandList[currentState.top()].size(); i++)
			if (commandList[currentState.top()].get(i).getName().equals(cname))
				return commandList[currentState.top()].get(i); 
		
		return null; 
	}

	
	protected void createCommandList(int nStates) { 
		commandList = (ArrayList<SystemCommand>[]) new ArrayList[nStates]; 
		for (int p=0; p<commandList.length; p++)
			commandList[p] = new ArrayList<SystemCommand>(); 
	}
	
	protected void setCurrentState(int sc) { 
		currentState.push(sc); 
	}
	
	protected void add(int state, SystemCommand sc) { 
		commandList[state].add(sc); 
	}
	
	protected SystemCommand getCommand(String cName) { 
		
		int nCommands = commandList[currentState.top()].size(); 
		for (int i=0; i<nCommands; i++) { 
			if (cName.equals(commandList[currentState.top()].get(i).getName())) 
				return commandList[currentState.top()].get(i); 
		}
		// cName does not represent a valid command...
		return null; 
	}
	
	
	/**
	 * Attempts to execute a particular command.
	 * @param cah The command action handler that executes the 
	 * @param commandToE the command to be executed. 
	 * @return List of output that are produced by the execution
	 * of the command. These might be error messages or output
	 * produced by its successful execution. 
	 */
	public ArrayList<String> executeCommand(SystemCommand sc, Command commandToE) {
		// get the command execution object corresponding to the command
	    return sc.getCommandActionHandler().execute(commandToE); 			
	}

	/**
	 *  This class is used to support the execution of command
	 *  help wherever valid to submit such command. Note that 
	 *  the output is shown directly, without going through an
	 *  output list or results list as it is done with other
	 *  commmands...
	 *  
	 * @author Pedro I. Rivera-Vega
	 *
	 */
	protected class HelpProcessor implements CommandActionHandler { 
		public HelpProcessor() {}
		public ArrayList<String> execute(Command c) { 
			System.out.println("Current state is " + currentState.top()); 
			System.out.println("Available commands are: ");
			for (int i=0; i<commandList[currentState.top()].size(); i++)
				System.out.println("\t"+ commandList[currentState.top()].get(i)); 
				
			return null; 
		} 
	}

	

}
