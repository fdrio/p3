package theSystem;

import java.util.ArrayList;
import java.util.Scanner;

import stack.IntStack;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandLine;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;

/**
 * Class that represents the type of the object used for Input and Output
 * @author pirvos
 *
 */
public class IOCommandsProcessor extends CommandProcessor {
	private ArrayList<String> resultsList;
	private int outputBlockLength; // the size of current output block settings
	private int currentIndex; 
	// the current index from where to resume next time the "showNextBlock"
	// method is executed....
	
	private boolean hasOutputToProcess;   
	// this field is false whenever there is no output in progress
	
    // all input needed shall be done through this scanner
	private Scanner in = new Scanner(System.in); 
	
	public IOCommandsProcessor() { 		
		currentState = new IntStack(); 
		
		// the system starts in sate GENERALSTATE - upper class static field
		currentState.push(GENERALSTATE); 

		outputBlockLength = 10;    // default block lenght is 10
		currentIndex = 0; 
		hasOutputToProcess = false; 
		this.currentIndex = 0; 
		
		// This command processor has only one (1) state: state 0.
		createCommandList(1); 

		// commands for the state GENERALSTATE (state 0)
		add(GENERALSTATE, SystemCommand.getFLSC("setrows int", new SetRowsProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("next", new NextProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("prev", new PrevProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ExitProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
	}
	
	/**
	 * Prompts and reads the next user's command. If needed, the reading is repeated
	 * until the input has some content. 
	 * @return The whole line of input as an object of type CommandLine. 
	 */
	public CommandLine readCommandLine() { 
		CommandLine cLine; 
		do { 
			System.out.print("COMMAND: "); 
			cLine = new CommandLine( in.nextLine() ); 
		} while (cLine.getNumberOfTokens() == 0);
		return cLine; 
	}

	
	/**
	 * Initializes the list whose output is controlled by this 
	 * OutputProcessor object. 
	 * @param rList the list
	 */
	public void setResultsList(ArrayList<String> rList) { 
		resultsList = rList; 
		currentIndex = 0; 
	}
	
	/**
	 * Sets the length of the block (number of lines) that will be
	 * shown whenever the output is being produced whenever the
	 * methods showNextBlock() or showPrevBlock() are applied 
	 * to the current OutputProcessor object.
	 * @param bl the new length of the output block...
	 */
	public void setBlockLength(int bl) { 
		if (bl < 1)
			bl = 10; // the default value is 10
		outputBlockLength = bl; 
	}
	
	/**
	 * Outputs the next block, beginning from the line at currentIndex
	 * in the list. 
	 */
	private void showNextBlock() { 
		boolean moreLinesToShow = true; 
		int lastLineToShow = 
			Math.min(resultsList.size()-1, currentIndex+outputBlockLength-1); 
		int firstLineToShow = currentIndex;
		currentIndex = lastLineToShow+1; 
		for (int line = firstLineToShow, count = 1; 
			count <= outputBlockLength && moreLinesToShow; 
			line++, count++)
		{ 
			try { 
				if (line <= lastLineToShow)
					System.out.println(resultsList.get(line)); 
				else { 
					System.out.println("--END HAS ALREADY BEEN REACHED--"); 
					moreLinesToShow = false; 
				}
			}
			catch (Exception e) { 
				System.out.println(e); 
			}
		}				
	}

	/**
	 * Outputs the previous block, where the last line will be the
	 * line at currentIndex-1 in the list. 
	 */
	private void showPrevBlock() { 
		boolean moreLinesToShow = true; //currentIndex >= 1; 
		int lastLineToShow = currentIndex-1; 
		int firstLineToShow = Math.max(0, currentIndex-outputBlockLength);
		currentIndex = firstLineToShow; 
		for (int line = firstLineToShow, count = 1; 
			count <= outputBlockLength && moreLinesToShow; 
			line++, count++)
		{ 
			try { 
				if (line <= lastLineToShow)
					System.out.println(resultsList.get(line)); 
				else { 
					System.out.println("--TOP HAS ALREADY BEEN REACHED--"); 
					moreLinesToShow = false; 
				}
			}
			catch (Exception e) { 
				System.out.println(e); 
			}
		}				

	}

	/*
	private boolean hasMoreResultsPrev() { 
		return currentIndex > 0; 
	}
	
	private boolean hasMoreResultsNext() { 
		return currentIndex < resultsList.size(); 
	}
	*/
	
	/**
	 *  The following are inner classes. Notice that there is one such class
	 *  for each command. The idea is that enclose the implementation of each
	 *  command in a particular unique place. Notice that, for each command, 
	 *  what you need is to implement the internal method "execute(Command c)".
	 *  In each particular case, your implementation assumes that the command
	 *  received as parameter is of the type corresponding to the particular
	 *  inner class. For example, the command received by the "execute(...)" method
	 *  inside the "DeclareProcessor" class must be a "declare" command. 
	 *
	 */
	private class NextProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			showNextBlock();
			return null;
		} 
	}
	
	private class PrevProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			showPrevBlock();
			return null; 
		} 
	}
	
	private class ExitProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			hasOutputToProcess = false; 
			return null; 
		} 
	}
		
	private class SetRowsProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			FixedLengthCommand fc = (FixedLengthCommand) c; 
			String operand = fc.getOperand(1); 
			setBlockLength(Integer.parseInt(operand)); 
			return null; 
		} 
	}

	public void processOutput() {
		if (this.outputBlockLength < resultsList.size()) { 
			hasOutputToProcess = true; 
			this.showNextBlock(); 
		} 
		else 
			showAllOutput(); 
	}

	private void showAllOutput() {	
		for (int line = 0; line < resultsList.size(); line++)
			System.out.println(resultsList.get(line)); 
	}

	public boolean hasMoreOutputToProcess() {
		return hasOutputToProcess;
	}

}
