package theSystem;

import java.util.ArrayList;

import diskUnitExceptions.ExistingDiskException;
import diskUnitExceptions.FullDiskException;
import diskUnitExceptions.InvalidParameterException;
import operandHandlers.OperandValidatorUtils;
import lists.DLDHDTList;
import lists.LLIndexList1;
import listsManagementClasses.ListsManager;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;
import systemGeneralClasses.VariableLengthCommand;
import stack.IntStack;

import diskUtilities.DiskManager;


/**
 * 
 * @author Pedro I. Rivera-Vega
 *
 */
public class SystemCommandsProcessor extends CommandProcessor { 
	
	
	//NOTE: The HelpProcessor is inherited...

	// To initially place all lines for the output produced after a 
	// command is entered. The results depend on the particular command. 
	private ArrayList<String> resultsList; 
	
	SystemCommand attemptedSC; 
	// The system command that looks like the one the user is
	// trying to execute. 

	boolean stopExecution; 
	// This field is false whenever the system is in execution
	// Is set to true when in the "administrator" state the command
	// "shutdown" is given to the system.
	
	////////////////////////////////////////////////////////////////
	// The following are references to objects needed for management 
	// of data as required by the particular actions of the command-set..
	// The following represents the object that will be capable of
	// managing the different lists that are created by the system
	// to be implemented as a lab exercise. 
	private ListsManager listsManager = new ListsManager(); 

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		
		// stack of states
		currentState = new IntStack(); 
		
		// The system may need to manage different states. For the moment, we
		// just assume one state: the general state. The top of the stack
		// "currentState" will always be the current state the system is at...
		currentState.push(GENERALSTATE); 

		// Maximum number of states for the moment is assumed to be 1
		// this may change depending on the types of commands the system
		// accepts in other instances...... 
		createCommandList(1);    // only 1 state -- GENERALSTATE

		// commands for the state GENERALSTATE
		
		// the following are for the different commands that are accepted by
		// the shell-like system that manage lists of integers
		
		// the command to create a new list is treated here as a command of variable length
		// as in the case of command testoutput, it is done so just to illustrate... And
		// again, all commands can be treated as of variable length command... 
		// One need to make sure that the corresponding CommandActionHandler object
		// is also working (in execute method) accordingly. See the documentation inside
		// the CommandActionHandler class for testoutput command.
		
		// the following commands are treated as fixed length commands...
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 		
		add(GENERALSTATE, SystemCommand.getFLSC("deletedisk name", new DeleteDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("mount name", new MountDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("unmount", new UnmountDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("loadfile name name", new LoadFileProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("cp name name", new CopyFileProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("ls", new ListDirectoryProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("cat name", new DisplayInternalFileProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		
		// set to execute....
		stopExecution = false; 

	}
		
	public ArrayList<String> getResultsList() { 
		return resultsList; 
	}
	
	// INNER CLASSES -- ONE FOR EACH VALID COMMAND --
	/**
	 *  The following are inner classes. Notice that there is one such class
	 *  for each command. The idea is that enclose the implementation of each
	 *  command in a particular unique place. Notice that, for each command, 
	 *  what you need is to implement the internal method "execute(Command c)".
	 *  In each particular case, your implementation assumes that the command
	 *  received as parameter is of the type corresponding to the particular
	 *  inner class. For example, the command received by the "execute(...)" 
	 *  method inside the "LoginProcessor" class must be a "login" command. 
	 *
	 */
	//////////////////////////////////////////////////////////////////////
	///////////// DATA PROJECT 2 INNER CLASSES ///////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private class CreateDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			int nBlocks = Integer.parseInt(fc.getOperand(2));
			int bSize = Integer.parseInt(fc.getOperand(3));
			
			try {
				DiskManager.createDiskUnit(name, nBlocks, bSize);
				resultsList.add("DiskUnit "+name+" has been created.");
			} catch (InvalidParameterException e) {
				if (bSize < 32)
					resultsList.add("Invalid number: Blocksize needs to be greater than or equal to 32.");
				else if (nBlocks < 0)
					resultsList.add("Invalid number: Capacity needs to be greater than 0.");
				else
					resultsList.add("Capacity and Blocksize need to be power of 2.");
			} catch (ExistingDiskException e) {
				System.out.println("Disk exist with that name already.");
			}
			return resultsList; 
		}
	}
	
	private class DeleteDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			
			DiskManager.deleteDiskUnit(name);
			
			return resultsList; 
		}
	}
	
	private class ShowDisksProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.showDiskUnits();
			
			return resultsList; 
		}
	}
	
	private class MountDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			DiskManager.mountDisk(name);
			
			return resultsList; 
		}
	}
	
	private class UnmountDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.unmountDisk();
			
			return resultsList; 
		}
	}
	
	private class LoadFileProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name1 = fc.getOperand(1);
			String name2 = fc.getOperand(2);
			DiskManager.loadFile(name1, name2);
		
			return resultsList; 
		}
	}
	
	private class CopyFileProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String inputFile = fc.getOperand(1);
			String file = fc.getOperand(2);
			DiskManager.copyFile(inputFile, file);
			return resultsList; 
		}
	}
	
	private class ListDirectoryProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.listDir();
			return resultsList; 
		}
	}
	
	private class DisplayInternalFileProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String filename = fc.getOperand(1);
			DiskManager.catFile(filename);
			
			return resultsList; 
		}
	}
	
	private class ShutDownProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			resultsList.add("SYSTEM IS SHUTTING DOWN!!!!");
			stopExecution = true;
			return resultsList; 
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	
	/**
	 * 
	 * @return
	 */
	public boolean inShutdownMode() {
		return stopExecution;
	}

}		





