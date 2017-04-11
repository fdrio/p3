package theSystem;

import java.util.ArrayList;

import diskExceptions.ExistingDiskException;
import diskExceptions.FullDiskException;
import diskExceptions.InvalidParameterException;
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
	
	
	
	private ArrayList<String> resultsList; 
	
	SystemCommand attemptedSC; 
	// The system command that looks like the one the user is
	// trying to execute. 

	boolean stopExecution; 
	// This field is false whenever the system is in execution
	// Is set to true when in the "administrator" state the command
	// "shutdown" is given to the system.
	
	
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

		
		// the following commands are treated as fixed length commands...
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 		
		add(GENERALSTATE, SystemCommand.getFLSC("deletedisk name", new DeleteDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("mount name", new MountProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("unmount", new UnmountProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("loadfile name name", new LoadProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("cp name name", new CopyProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("ls", new ListProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("cat name", new CatProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		
		// set to execute....
		stopExecution = false; 

	}
		
	public ArrayList<String> getResultsList() { 
		return resultsList; 
	}
	
	/**
	 * Creates a Disk inside the Virtual File System
	 * @author Francisco Diaz
	 *
	 */
	
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
	/**
	 * Deletes a Disk from the Virtual File System
	 * @author Francisco Diaz 
	 *
	 */
	private class DeleteDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			
			DiskManager.deleteDiskUnit(name);
			
			return resultsList; 
		}
	}
	
	/**
	 * Processor that shows all the disks in the File System
	 * @author Francisco Diaz
	 *
	 */
	private class ShowDisksProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.showDiskUnits();
			
			return resultsList; 
		}
	}
	/**
	 * Processor that mounts a disk and makes it the current working disk
	 * @author francisco
	 *
	 */
	private class MountProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			DiskManager.mountDisk(name);
			
			return resultsList; 
		}
	}
	/**
	 * Unmount's the current working disk
	 * @author Francisco Diaz
	 *
	 */
	private class UnmountProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.unmountDisk();
			
			return resultsList; 
		}
	}
	/**
	 * Loads an external file in the current working disk
	 * @author Francisco Diaz
	 *
	 */
	private class LoadProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name1 = fc.getOperand(1);
			String name2 = fc.getOperand(2);
			DiskManager.loadFile(name1, name2);
		
			return resultsList; 
		}
	}
	
	/**
	 * Copies one file to another
	 * @author Francisco Diaz
	 *
	 */
	private class CopyProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>();
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String inputFile = fc.getOperand(1);
			String file = fc.getOperand(2);
			DiskManager.copyFile(inputFile, file);
			return resultsList; 
		}
	}
	/**
	 * Lists all the directories inside the current directory
	 * @author Francisco Diaz
	 *
	 */
	private class ListProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			DiskManager.listDir();
			return resultsList; 
		}
	}
	/**
	 * Displays the internal content of a disk
	 * @author francisco
	 *
	 */
	private class CatProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String filename = fc.getOperand(1);
			DiskManager.catFile(filename);
			
			return resultsList; 
		}
	}
	/**
	 * Shutdowns the command line
	 * @author Francisco Diaz 
	 *
	 */
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





