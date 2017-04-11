package diskUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import diskUnitExceptions.FullDiskException;

public class FileManager {

	/**
	 * Attempts to read a new file into the current directory in the current 
	 * working disk unit. The first operand is the name of the file to read. 
	 * Such file must exist in the same directory where the program is being executed. 
	 * If no such file, then the command ends with an appropriate message. 
	 * The second operand is the name of the new file; how the system will record it 
	 * in the current directory. If such name already exists in the current directory, 
	 * and if it corresponds to an existing data file, then the current content of such 
	 * file is erased and replaced by the content of the file being read. If the given 
	 * name (second operand) is new, then a new file with that name is created and its 
	 * content will be a copy of the actual content of the file being read. If the disk 
	 * unit does not have enough space for the new file, the command also ends with a message.
	 * @param extFile Name of the file to read
	 * @param file Name of the new file
	 */
	public static void loadFile(String extFile, String file) throws FullDiskException{
		
		//extFile += ".txt"; 
		File fileToRead = new File(extFile); // file to read from
		if (!fileToRead.exists()) {
			System.out.println(extFile+": No such file.");
			System.out.println(fileToRead.getAbsolutePath());
			return;
		}	
		//File newFile = new File(file);  // file to copy into inside the disk unit
		
		// TODO: Verify disk has enough space.
		
		// Format file string to fit 20 bytes
		file = DiskUtils.formatFileName(file);
		
		// Place file content inside an ArrayList of VirtualDiskBlock
		DiskUnit disk = DiskManager.mountedDiskUnit;
		int blockSize = disk.getBlockSize();
		ArrayList<VirtualDiskBlock> extFileArrayList; // Will hold contents of external file
		int rafSize; // Will hold size of the external file (measured in bytes)
		try {
			RandomAccessFile rafToRead = new RandomAccessFile(extFile, "rw");
			rafSize = (int) rafToRead.length(); // Get size of external file in bytes
			rafToRead.close();
			// Create random access file to read data.
			// Set the contents of the external file into an ArrayList of VirtualDiskBlocks
			extFileArrayList = DiskUtils.setExtFileContentToVDBs(extFile, blockSize);  
			
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open external file.");
			return;
		} catch (IOException e) {
			System.err.println("Unable to read external file.");
			return;
		} 
	
		// Block Number of the root directory
		int rootBlockNum = INodeManager.getDataBlockFromINode(disk, 0);
		// Verify if File already exists.
		ArrayList<Integer> foundFile = findFileInDir(disk, file, rootBlockNum);
		try {
			if (foundFile != null) { // If found the file erases the foundFile and creates it with new content.
				VirtualDiskBlock foundFileBlock = DiskUtils.copyBlockToVDB(disk, foundFile.get(0));
				int fileBytePos = foundFile.get(1);
				// Get iNode reference to that file 
				int iNodeRef = DiskUtils.getIntFromBlock(foundFileBlock, fileBytePos+20); // Reads the integer right after the filename, which is the iNode ref
				int fileDataBlock = INodeManager.getDataBlockFromINode(disk, iNodeRef);  // Data block from the i-node
				// Set size in of file into its i-node
				INodeManager.setSizeIntoINode(disk, iNodeRef, rafSize);
				// Delete file from disk
				deleteFileFromDisk(disk, fileDataBlock);
				// Write new file in place of the older one
				writeNewFileIntoDisk(disk, fileDataBlock, extFileArrayList);

			}
			else { // Create the new file.
				// Write new file into root directory

				int iNodeRef = writeNewFileIntoDirectory(disk, file, rootBlockNum); // Returns the iNode reference.
				// Enter the iNode and assign a free block
				// Get free block number
				int freeBN = FreeBlockManager.getFreeBN(disk);
				// Set data block in i-node to the free block 
				INodeManager.setDataBlockToINode(disk, iNodeRef, freeBN);
				// Set size in of file into its i-node
				INodeManager.setSizeIntoINode(disk,iNodeRef, rafSize);
				// Write file into the free block
				writeNewFileIntoDisk(disk, freeBN, extFileArrayList);
			}
		} catch (FullDiskException e) {
			throw new FullDiskException(e);
		}	
		
	}
	/**
	 * Copies one internal file to another internal file. It works similar to the 
	 * command loadfile, but this time the input file (name given first) is also an 
	 * internal file that must be a data file in the current directory.
	 * @param inputFile Internal file to copy from
	 * @param file Internal file to copy content into
	 */
	public static void copyFile(String inputFile, String file) throws FullDiskException {
		
		// Format file strings to fit 20 bytes
		inputFile = DiskUtils.formatFileName(inputFile);
		file = DiskUtils.formatFileName(file);
		// Mounted disk unit 
		DiskUnit disk = DiskManager.mountedDiskUnit;
		// Block Number of the root directory
		int rootBlockNum = INodeManager.getDataBlockFromINode(disk, 0);
		// Get input file from root
		ArrayList<Integer> inputFileInfo = findFileInDir(disk, inputFile, rootBlockNum);
		if (inputFileInfo == null || inputFileInfo.isEmpty()) {
			System.out.println("File not found in directory");
			return;
		}
		VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(disk, inputFileInfo.get(0));
		int inputFileBytePos = inputFileInfo.get(1);   // Starting byte position of the file name
		// Get data block from i-node
		int inputINodeRef = DiskUtils.getIntFromBlock(vdb, inputFileBytePos+20);
		int inputFileSize = INodeManager.getSizeFromINode(disk, inputINodeRef);
		int inputFileDataBlock = INodeManager.getDataBlockFromINode(disk, inputINodeRef);
		
		// Get content of input file
		ArrayList<VirtualDiskBlock> content = DiskUtils.setFileContentToVDBs(disk, inputFileDataBlock);
		
		
		// Verify if File already exists.
		ArrayList<Integer> foundFile = findFileInDir(disk, file, rootBlockNum);
		
		try {
			if (foundFile != null) { // If found the file erases the foundFile and creates it with new content.
				VirtualDiskBlock foundFileBlock = DiskUtils.copyBlockToVDB(disk, foundFile.get(0));
				int fileBytePos = foundFile.get(1);
				// Get iNode reference to that file 
				int iNodeRef = DiskUtils.getIntFromBlock(foundFileBlock, fileBytePos+20); // Reads the integer right after the filename, which is the iNode ref
				int fileDataBlock = INodeManager.getDataBlockFromINode(disk, iNodeRef);  // Data block from the i-node
				// Set size of file into its i-node
				INodeManager.setSizeIntoINode(disk, iNodeRef, inputFileSize);
				// Delete file from disk
				deleteFileFromDisk(disk, fileDataBlock);
				// Write new file in place of the older one
				writeNewFileIntoDisk(disk, fileDataBlock, content);

			}
			else { // Create the new file.
				// Write new file into root directory

				int iNodeRef = writeNewFileIntoDirectory(disk, file, rootBlockNum); // Returns the iNode reference.
				// Enter the iNode and assign a free block
				// Get free block number
				int freeBN = FreeBlockManager.getFreeBN(disk);
				// Set data block in i-node to the free block 
				INodeManager.setDataBlockToINode(disk, iNodeRef, freeBN);
				// Set size in of file into its i-node
				INodeManager.setSizeIntoINode(disk,iNodeRef, inputFileSize);
				// Write file into the free block
				writeNewFileIntoDisk(disk, freeBN, content);
			} 
		} catch (FullDiskException e) {
			throw new FullDiskException(e);
		}
		
	}
	/**
	 * List the names and sizes of all the files and directories that are part 
	 * of the current directory. Notice that this command will read the content 
	 * of the file corresponding to the directory and display the specified 
	 * information about each file stored in that file. 
	 */
	public static void listDir() {
		DiskUnit disk = DiskManager.mountedDiskUnit;
		
		// Write the title
		System.out.println();
		System.out.println("Filename:           Size (Bytes)");
		System.out.println("-------------------------------------");
		// Block Number of the root directory
		int rootBlockNum = INodeManager.getDataBlockFromINode(disk, 0);
		printFilesFromDir(disk, rootBlockNum); // Print the filenames with the sizes
		
		System.out.println();
	}
	/**
	 * Displays the contents of a file in the current directory.
	 * @param file Name of file to be displayed.
	 */
	public static void catFile(String file) {
		
		// Format file string to fit 20 bytes
		file = DiskUtils.formatFileName(file);
		// Mounted disk unit 
		DiskUnit disk = DiskManager.mountedDiskUnit;
		// Block Number of the root directory
		int rootBlockNum = INodeManager.getDataBlockFromINode(disk, 0);
		// Get file from root
		ArrayList<Integer> fileInfo = findFileInDir(disk, file, rootBlockNum);
		if (fileInfo == null || fileInfo.isEmpty()) {
			System.out.println("File not found in directory");
			return;
		}
		VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(disk, fileInfo.get(0));
		int fileBytePos = fileInfo.get(1);   // Starting byte position of the file name
		// Get data block from i-node
		int iNodeRef = DiskUtils.getIntFromBlock(vdb, fileBytePos+20);
		int fileDataBlock = INodeManager.getDataBlockFromINode(disk, iNodeRef);
		
		// Get content from file
		ArrayList<VirtualDiskBlock> content = DiskUtils.setFileContentToVDBs(disk, fileDataBlock);
		
		// Print the file content
		System.out.println();
		DiskUtils.printContentOfVDBlocks(content);
	}
	
	/**
	 * Provides ArrayList with block number in first index and free byte position in second index.
	 * @param d DiskUnit in use
	 * @param blockNum Number of data block of the directory
	 * @param blockSize Bytes per block
	 * @return ArrayList with block number in first index and free byte position in second index.
	 */
	public static ArrayList<Integer> getFreePosInDirectory(DiskUnit d, int firstDirBlockNum, int blockSize) throws FullDiskException {
		
		int usableBytes = blockSize - 4;
		int filesPerBlock = usableBytes / 24;
		ArrayList<Integer> freeDirArray = new ArrayList<>(); // Holds the free byte position to write into 
		
		ArrayList<Integer> dirBlockNums = allFileBlockNums(d, firstDirBlockNum);
		
		int blockNum = dirBlockNums.get(dirBlockNums.size()-1); // The last block number in the list
		VirtualDiskBlock lastDataBlock = DiskUtils.copyBlockToVDB(d, blockNum); // last data block in the directory
		for (int i=1; i <= filesPerBlock; i++) {
			int iNodeIdx = DiskUtils.getIntFromBlock(lastDataBlock, (i*24)-4); // i-node index inside the directory, after the filename
			if (iNodeIdx < 1 || iNodeIdx > d.getiNodeNum()) {  // No reference to an iNode. byte position = blockNum*blockSize+((i*24)-24)
				int bytePos = (i*24)-24;
				freeDirArray.add(blockNum);
				freeDirArray.add(bytePos);
			}
		}
		if (freeDirArray.isEmpty()) {
			try{
				int nextFreeBlock = FreeBlockManager.getFreeBN(d);
				DiskUtils.copyIntToBlock(lastDataBlock, usableBytes, nextFreeBlock); // Copy new data block into last 4 bytes
				d.write(blockNum, lastDataBlock); 	// Write the next block number into the block on the disk
				freeDirArray.add(nextFreeBlock); 	// Block number to write into
				freeDirArray.add(0);   // Byte position to use
			} catch (FullDiskException e) {
				System.out.println(e.getMessage());
				throw new FullDiskException(e);
			}
		}
			
		return freeDirArray;
	}
	
	/**
	 * Find if file is inside the directory. Returns the block number 
	 * and byte position in the block if it is found. If not found returns null. 
	 * @param d DiskUnit to be used
	 * @param file Name of file being searched.
	 * @param dirBlockNum Block number of the directory.
	 * @return Returns ArrayList<Integer> with blockNumber in first index and
	 * byte position in block in the second index. If file not found returns null.
	 */
	public static ArrayList<Integer> findFileInDir(DiskUnit d, String file, int dirBlockNum) {

		ArrayList<Integer> dirBlockNums = allFileBlockNums(d, dirBlockNum);
		for (Integer blockNum : dirBlockNums) {
			VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
			Integer bytePos = findFileInDirBlock(vdb, file); // Find file in the block.
			if (bytePos != null) {
				ArrayList<Integer> foundFileInfo = new ArrayList<>();
				foundFileInfo.add(blockNum);   // BlockNum of where the file resides.
				foundFileInfo.add(bytePos);    // Byte position in the block specified in the line before. 
				return foundFileInfo;
			}
		}
		return null;
	}
	/**
	 * Reads the file names inside a block and returns the byte position where
	 * the file name begins in reference to the block. If file not found returns null.
	 * @param vdb
	 * @return Integer with the byte position of the filename. 
	 */
	public static Integer findFileInDirBlock(VirtualDiskBlock vdb, String file) {
		
		int usableBytes = vdb.getCapacity() - 4;
		int filesPerBlock = usableBytes / 24;
		
		for (int i=1; i <= filesPerBlock; i++) {
			
			int iNodeIdx = DiskUtils.getIntFromBlock(vdb, (i*24)-4); // i-node index inside the directory, after the filename
			if (iNodeIdx == 0)   // byte position = blockNum*blockSize+((i*24)-24)
				break;
			
			char[] fileCharArray = new char[20];
			int fileBytePos = (i*24) - 24;
			for (int j=0; j<20; j++) {
				fileCharArray[j] = DiskUtils.getCharFromBlock(vdb, (fileBytePos+j));
			}
			String filename = new String(fileCharArray);
			if (filename.equals(file)) {
				return fileBytePos;
			}	
		}
		return null;
	}
	/**
	 * List of strings with information about the files in a directory block.
	 * @param d DiskUnit in use.
	 * @param vdb Virtual block of a directory.
	 * @return Returns list of strings with information about the files in a directory block.
	 */
	private static ArrayList<String> filesInDirBlock(DiskUnit d, VirtualDiskBlock vdb) {
		
		int usableBytes = vdb.getCapacity() - 4;
		int filesPerBlock = usableBytes / 24;
		ArrayList<String> filesInDir = new ArrayList<>();
		
		for (int i=1; i <= filesPerBlock; i++) {
			
			int iNodeIdx = DiskUtils.getIntFromBlock(vdb, (i*24)-4); // i-node index inside the directory, after the filename
			if (iNodeIdx == 0)   // If no reference to i-node is found, no file is stored. Byte position = blockNum*blockSize+((i*24)-24)
				break;
			
			char[] fileCharArray = new char[20];
			int fileBytePos = (i*24) - 24;   // Starting byte position of the file name
			for (int j=0; j<20; j++) {
				fileCharArray[j] = DiskUtils.getCharFromBlock(vdb, (fileBytePos+j));
			}
			String filename = new String(fileCharArray);
			int iNodeRef = DiskUtils.getIntFromBlock(vdb, fileBytePos+20);
			String filesize = Integer.toString(INodeManager.getSizeFromINode(d, iNodeRef));
			filename += " "+filesize;
			filesInDir.add(filename);
			
		}
		return filesInDir;
	}
	
	/**
	 * Returns an ArrayList with all the block numbers of a file or directory.
	 * @param d DiskUnit to be used.
	 * @param firstFileBlockNum First block number of the file
	 * @return Returns an ArrayList with all the block numbers of a file or directory.
	 */
	public static ArrayList<Integer> allFileBlockNums(DiskUnit d, int firstFileBlockNum) {
		
		int blockSize = d.getBlockSize();
		int lastInt = blockSize - 4;
		ArrayList<Integer> dirBlockNums = new ArrayList<>(); 
		
		VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
		d.read(firstFileBlockNum, vdb);
		int nextBlockInt = DiskUtils.getIntFromBlock(vdb, lastInt); // Read the last 4 bytes in the block
		dirBlockNums.add(firstFileBlockNum);
		
		while (nextBlockInt != 0) {
			dirBlockNums.add(nextBlockInt);
			vdb = new VirtualDiskBlock(blockSize);
			d.read(nextBlockInt, vdb);
			nextBlockInt = DiskUtils.getIntFromBlock(vdb, lastInt);
		}
		return dirBlockNums;
	}
	
	/**
	 * Write the file name and i-node reference into a directory.
	 * Writes file name right after the last file name in directory.
	 * @param d DiskUnit in use.
	 * @param file New file to write into directory.
	 * @param blockNum Number of the block to write in.
	 * @param blockSize Bytes per block.
	 * @throws IllegalArgumentException Thrown if filename length not 20 characters long.
	 * @throws FullDiskException The disk is full.
	 * @return Returns reference to the i-node of the new file.
	 */
	public static int  writeNewFileIntoDirectory(DiskUnit d, String file, int blockNum) 
			throws IllegalArgumentException, FullDiskException {
		
		// Blocksize of the blocks
		int blockSize = d.getBlockSize();
		
		// New file string into char array.
		char[] fileCharArray = file.toCharArray();
		if (fileCharArray.length != 20) {
			throw new IllegalArgumentException("File name is not 20 characters.");
		}
			
		// Get free byte position in directory
		ArrayList<Integer> newFileInRoot = getFreePosInDirectory(d, blockNum, blockSize);
		
		// TODO: Verify file fits into new block;
		
		// Write file name inside root and assign a free i-node to reference it.
		// Obtain free i-node index
		int iNodeRef;
		try {
			iNodeRef = INodeManager.getFreeINode(d);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new FullDiskException();  // Could not get more i-node
		}

		// Write file name and node index into root;
		int newFileBlockNum = newFileInRoot.get(0);
		int newFileBytePos = newFileInRoot.get(1);
		
		VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
		d.read(newFileBlockNum, vdb);
		
		// Write file name inside the block
		for (int i=0; i<fileCharArray.length; i++) {
			DiskUtils.copyCharToBlock(vdb, newFileBytePos+i, fileCharArray[i]);
		}
		// Copy i-node reference into the directory.
		DiskUtils.copyIntToBlock(vdb, newFileBytePos+20, iNodeRef);
		// Write the Virtual block back into the disk unit.
		d.write(newFileBlockNum, vdb);
		
		return iNodeRef; // Returns reference to the i-node of the new file.
	}
	/**
	 * Prints the filename and file size of all the files in a directory 
	 * @param d DiskUnit in use
	 * @param dirBlockNum Number of the first data block of the directory
	 */
	private static void printFilesFromDir(DiskUnit d, int dirBlockNum) {
		
		ArrayList<Integer> dirBlockNums = allFileBlockNums(d, dirBlockNum);
		
		for (Integer blockNum : dirBlockNums) {		
			VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
			ArrayList<String> files = filesInDirBlock(d, vdb);
			for (String file : files) {
				System.out.println(file);
			}
		}
	}
	
	/**
	 * Writes the new file into the disk unit.
	 * @param d
	 * @param firstFreeBlock
	 * @param vdbArray
	 */
	private static void writeNewFileIntoDisk(DiskUnit d, int firstFreeBlock, ArrayList<VirtualDiskBlock> vdbArray) {
		try {
			if (vdbArray.size() < 1) {
				System.out.println("No content retrieved from the external file.");
				return;
			}
			VirtualDiskBlock vdb = vdbArray.get(0);  // Get the VirtualDiskBlock from the ArrayList
			int nextFreeBlock;  // Holds the next free block to use
			if (vdbArray.size() == 1)
				nextFreeBlock = 0;
			else
				nextFreeBlock = FreeBlockManager.getFreeBN(d); // Look for a free block
			DiskUtils.copyIntToBlock(vdb, vdb.getCapacity()-4, nextFreeBlock);  // Write free block number into last 4-bytes of block
			d.write(firstFreeBlock, vdb);   // Write virtual disk block into disk 

			for (int i=1; i<vdbArray.size(); i++) {
				int freeBlock = nextFreeBlock;  // Save the next block, in order to write in that block
				vdb = vdbArray.get(i);
				if (i == vdbArray.size()-1)
					nextFreeBlock = 0;
				else
					nextFreeBlock = FreeBlockManager.getFreeBN(d);  // Look for a free block
				DiskUtils.copyIntToBlock(vdb, vdb.getCapacity()-4, nextFreeBlock);  // Write free block number into last 4-bytes of block
				d.write(freeBlock, vdb);  // Write virtual disk block into disk 
			}
		} catch (FullDiskException e) {
				System.out.println(e.getMessage());
				throw new FullDiskException();
		}
		
		

		}
	/**
	 * Deletes a file from the disk by wiping its data blocks.
	 * @param d DiskUnit in use
	 * @param firstFreeBlock Number of the first data block in the file.
	 */
	private static void deleteFileFromDisk(DiskUnit d, int firstFreeBlock) {
		
		ArrayList<Integer> fileBlockNums = allFileBlockNums(d, firstFreeBlock);
		
		for (int i=0; i<fileBlockNums.size(); i++) {
			int blockNum = fileBlockNums.get(i);
			VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
			clearDiskBlock(d, blockNum, vdb);         // Clear the block
			if (i != 0) // Doesn't register the firstFreeBlock into free blocks
				FreeBlockManager.registerFB(d, blockNum); // register free block to the free block collection.
		}
	}
	/**
	 * Clears a block by setting all its bytes to zero.
	 * @param d
	 * @param blockNum
	 * @param vdb
	 */
	private static void clearDiskBlock(DiskUnit d, int blockNum, VirtualDiskBlock vdb) {
		
		for (int i=0; i<vdb.getCapacity(); i++) {
			vdb.setElement(i, (byte) 0); // set every byte to 0 in the block
		}
		d.write(blockNum, vdb); // Write the wiped block into the disk.
		
	}
	
	
		
	
	
	
	
	
	
	
}

