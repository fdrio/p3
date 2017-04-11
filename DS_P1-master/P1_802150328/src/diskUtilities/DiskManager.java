package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import diskUnitExceptions.ExistingDiskException;
import diskUnitExceptions.FullDiskException;
import diskUnitExceptions.InvalidParameterException;
import diskUnitExceptions.NonExistingDiskException;

public class DiskManager {
	/**
	 * Class which manages the disks and files stored in the disk unit. 
	 * @author Jahdiel Alvarez
	 */
	
	public static final int I_NODE_SIZE = 9; // Bytes per i-node
	
	public static ArrayList<String> diskUnitNames = new ArrayList<>(); // Stores in memory the name of the disk units created.
	public static String mountedDiskName = null;   // Name of the DiskUnit which is mounted.
	public static DiskUnit mountedDiskUnit = null; // DiskUnit instance object of the mounted disk.
	
	
	/**
	 * Creates a new disk unit with the provided parameters.
	 * It also separates disks blocks for the i-nodes.
	 * @param name Name of the disk unit.
	 * @param capacity amount of disk blocks in the disk unit
	 * @param blockSize bytes per each disk block
	 */
	public static void createDiskUnit(String name, int capacity, int blockSize) 
			throws ExistingDiskException, InvalidParameterException {
		
		//TODO: create DiskNames text file if doesn't exist
		
		// Verifying DiskUnit folder exists and add Unit to DiskNames text file
		DirectoryManager.createDiskDirectory();	
		try {
			DiskUnit.createDiskUnit(name, capacity, blockSize);
			DirectoryManager.addUnitToDiskNames(name);
		} catch (ExistingDiskException e) {
			throw new ExistingDiskException();
		}
		// Mount the disk unit in order to create its root directory.
		DiskUnit d = DiskUnit.mount(name);
		
		// Set the root directory
		setRootDirectory(d, blockSize);
		
		// Initialize the free block structure
		initializeFreeBlocks(d);
		
		d.shutdown(); // Shutdown the disk
	}
	/**
	 * Initializes the free block structure. In the beginning all data blocks
	 * are part of the structure.
	 * @param d DiskUnit to initialize its free blocks (all data blocks)
	 */
	private static void initializeFreeBlocks(DiskUnit d) {
		FreeBlockManager.initializeFreeBlocks(d);
	}
	
	/**
	 * Sets the root directory in the first data block. And sets the i-node 0's first block
	 * to refer to the root directory disk block.
	 * @param d DiskUnit to work with.
	 * @param blockSize Amount of bytes per block.
	 */
	private static void setRootDirectory(DiskUnit d, int blockSize) {
		// Set the root directory
		VirtualDiskBlock root = new VirtualDiskBlock(blockSize);
		int rootDataBlock = d.getFirstDataBlock()-1; // index of the root block number
		DiskUtils.copyIntToBlock(root, blockSize-4, 0);
		d.write(rootDataBlock, root);

		// Set i-node 0 to reference root
		int rootINodePos = 1;   // Index of the root directory i-node 
		// Copy the first i-node block into the Virtual Disk Block
		VirtualDiskBlock firstBlockRef = DiskUtils.copyBlockToVDB(d, rootINodePos);  
		// Write in the i-node 0 the reference to the root directory in the data blocks
		// and set i-node type to directory
		DiskUtils.copyIntToBlock(firstBlockRef, 0, rootDataBlock);
		firstBlockRef.setElement(I_NODE_SIZE-1, (byte) 1);
		// Write into the disk the virtual disk block with updated reference to the root directory data block
		d.write(rootINodePos, firstBlockRef);
	}
	
	/**
	 * Deletes a disk unit with the provided name.
	 * @param name Name of the disk unit to be eliminated.
	 */
	public static void deleteDiskUnit(String name) {

		File unitToDelete = new File("DiskUnits",name); //created to actually delete the disk
		if (!unitToDelete.exists()) {
			System.out.println(name+" does not exist.");
			return;
		}
		System.out.println(name+" has been removed.");
		unitToDelete.delete();
		DirectoryManager.removeUnitFromDiskNames(name);
		
	}
	
	/**
	 * Shows the list of disk units that are active in the system. 
	 * For each disk unit, it displays its name, the number of blocks 
	 * it has and the size for each block it has. It also shows 
	 * if the corresponding disk unit is currently mounted or not-mounted.
	 */
	public static void showDiskUnits() {
		
		if (DiskManager.diskUnitNames.isEmpty()) {
			System.out.println("No disk units in the file system.");
			return;
		}
		System.out.printf("%-22s%-22s%-22s%-22s\n","Name:", "Capacity:", "Blocksize:", "Mounted:");
		System.out.println("---------------------------------------------------------------------------");
		for (String s : DiskManager.diskUnitNames) {
			DiskUnit d = DiskUnit.mount(s);
			int capacity = d.getCapacity();
			int blockSize = d.getBlockSize();
			
			if (mountedDiskUnit != null && mountedDiskName.equals(s))
				System.out.printf("%-22s%-22d%-22d%-22s\n",s,capacity,blockSize,"yes");
			else
				System.out.printf("%-22s%-22d%-22d\n",s,capacity,blockSize);
			
			d.shutdown();
		}
		System.out.println();
	}
	/**
	 * Mounts the specified disk unit and makes it the “current working disk unit”.  The successful 
	 * execution of this command also makes the root directory in the particular disk unit being mounted 
	 * as the current directory in the system. The current directory is the directory of the current 
	 * working disk where the commands specified will work on. Each of the following commands require some 
	 * disk to be the current working disk unit, and they work upon that particular unit. 
	 * If there's no current working disk unit, then the command just ends with an appropriate error message.
	 * @param name Name of the disk unit to mount.
	 */
	public static void mountDisk(String name) {
		
		if (mountedDiskName != null) {
			System.out.println("There is already a mounted disk. Unmount DiskUnit "+mountedDiskName+" first.");
			return;
		}
		try {
			DiskUnit d = DiskUnit.mount(name);
			mountedDiskName = name;
			mountedDiskUnit = d;
			System.out.println(name+" mounted successfully.");
		} catch (NonExistingDiskException e) {
			System.out.println(e.getMessage());
		}
		
	}
	/**
	 * The successful execution of this command unmounts the current working disk unit, if any. 
	 * If there is no current working disk unit, then the command just shows an appropriate message. 
	 * Notice that this command does not delete the disk unit, it just unmounts it 
	 * without altering its content. After the execution of this command the system will 
	 * have no current disk unit. In order to have one again, the mount command needs to be executed. 
	 */
	public static void unmountDisk() {
		
		if (mountedDiskName == null && mountedDiskUnit == null) {
			System.out.println("No disk is mounted.");
			return;
		}
		System.out.println(mountedDiskName+" unmounted successfully.");
		mountedDiskUnit.shutdown();
		mountedDiskName = null;
		mountedDiskUnit = null;

	}
	/**
	 * Determines if the is a mounted disk.
	 * @return Returns true if disk is mounted.
	 */
	public static boolean isDiskMounted() {
		return (mountedDiskName != null && mountedDiskUnit != null);
	}
	
	/**
	 * Attempts to read a new file into the current directory in the current 
	 * working disk unit. Wrapper to the FileManager loadFile method.
	 * @param extFile Name of the file to read
	 * @param newFile Name of the new file
	 */
	public static void loadFile(String extFile, String newFile) {
		if (!isDiskMounted()) {
			System.out.println("Cannot load file. No disk is mounted.");
			return;
		}
		try {
			FileManager.loadFile(extFile, newFile);
		} catch (FullDiskException e) {
			return;
		}
	}
	/**
	 * List the names and sizes of all the files and directories that are part 
	 * of the current directory. Notice that this command will read the content 
	 * of the file corresponding to the directory and display the specified 
	 * information about each file stored in that file.
	 * Prints the filename and its size in bytes.
	 */
	public static void listDir() {
		if (!isDiskMounted()) {
			System.out.println("Cannot list directory. No disk is mounted.");
			return;
		}
		FileManager.listDir();
	}
	/**
	 * Displays the contents of a file in the current directory.
	 * @param file Name of file to be displayed.
	 */
	public static void catFile(String file) {
		if (!isDiskMounted()) {
			System.out.println("Cannot display file. No disk is mounted.");
			return;
		}
		FileManager.catFile(file);
	}
	/**
	 * Copies one internal file to another internal file. It works similar to the 
	 * command loadfile, but this time the input file (name given first) is also an 
	 * internal file that must be a data file in the current directory.
	 * @param inputFile Internal file to copy from
	 * @param file Internal file to copy content into
	 */
	public static void copyFile(String inputFile, String file) {
		if (!isDiskMounted()) {
			System.out.println("Cannot copy file. No disk is mounted.");
			return;
		}
		try {
			FileManager.copyFile(inputFile, file);
		} catch (FullDiskException e) {
			return;
		}
	
	}
	
	

	
}
