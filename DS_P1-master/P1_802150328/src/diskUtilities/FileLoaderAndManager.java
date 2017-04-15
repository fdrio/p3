package diskUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import diskExceptions.FullDiskException;

/**
 * Class that manages the existing files and is able to load the file in the current disk
 * @author Francisco Diaz
 *
 */
public class FileLoaderAndManager {

	/**
	 * Loads an external into a file that lives inside the current disk
	 * @param externalFileToReadFrom Name of the file to read
	 * @param fileName Name of the new file
	 */
	public static void load(String externalFileToReadFrom, String fileName) throws FullDiskException{


		File fileToRead = new File(externalFileToReadFrom); // file to read from
		if (!fileToRead.exists()) {
			System.out.println("There is no such file named :"+externalFileToReadFrom);
			System.out.println(fileToRead.getAbsolutePath());
			return;
		}	
		//File newFile = new File(file);  // file to copy into inside the disk unit

		// TODO: Verify disk has enough space.

		// Format file string to fit 20 bytes
		fileName = DiskUtils.formatFileName(fileName);

		// Place file content inside an ArrayList of VirtualDiskBlock
		DiskUnit disk = DiskManager.currentMountedDisk;
		int blockSize = disk.getBlockSize();
		ArrayList<VirtualDiskBlock> externalFileList; // Will hold contents of external file
		int fileSize; // Will hold size of the external file (measured in bytes)
		try {
			RandomAccessFile rafToRead = new RandomAccessFile(externalFileToReadFrom, "rw"); // read, write
			fileSize = (int) rafToRead.length(); // Get size of external file in bytes
			rafToRead.close();
			// Create random access file to read data.
			// Set the contents of the external file into an ArrayList of VirtualDiskBlocks
			externalFileList = DiskUtils.setExtFileContentToVDBs(externalFileToReadFrom, blockSize);  

		} catch (FileNotFoundException e) {
			System.err.println("Unable to open external file.");
			return;
		} catch (IOException e) {
			System.err.println("Unable to read external file.");
			return;
		} 

		// Block Number of the root directory
		int rootBlockNumber = iNodesManager.getFirstDataBlockFromiNode(disk, 0);
		// Verify if File already exists.
		ArrayList<Integer> fileFoundedInDir = findFileInDir(disk, fileName, rootBlockNumber);
		try {
			if (fileFoundedInDir != null) { // If found the file erases the foundFile and creates it with new content.
				VirtualDiskBlock foundFileBlock = DiskUtils.copyBlockToVDB(disk, fileFoundedInDir.get(0));
				int filePos = fileFoundedInDir.get(1);
				// Get iNode reference to that file 
				int iNodeFileReference = DiskUtils.getIntFromBlock(foundFileBlock, filePos+20); // Reads the integer right after the filename, which is the iNode ref
				int fileDataBlock = iNodesManager.getFirstDataBlockFromiNode(disk, iNodeFileReference);  // Data block from the i-node
				// Set size in of file into its i-node
				iNodesManager.setSizeOfiNode(disk, iNodeFileReference, fileSize);
				// Delete file from disk
				deleteFileAtDisk(disk, fileDataBlock);
				// Write new file in place of the older one
				addNewFileInDisk(disk, fileDataBlock, externalFileList);

			}
			else { // Create the new file.
				// Write new file into root directory

				int iNodeRef = writeFileInDirectory(disk, fileName, rootBlockNumber); // Returns the iNode reference.
				// Enter the iNode and assign a free block
				// Get free block number
				int freeBN = BlockManager.getFreeBlockNumber(disk);
				// Set data block in i-node to the free block 
				iNodesManager.setDataBlockToINode(disk, iNodeRef, freeBN);
				// Set size in of file into its i-node
				iNodesManager.setSizeOfiNode(disk,iNodeRef, fileSize);
				// Write file into the free block
				addNewFileInDisk(disk, freeBN, externalFileList);
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
		DiskUnit disk = DiskManager.currentMountedDisk;
		// Block Number of the root directory
		int rootBlockNum = iNodesManager.getFirstDataBlockFromiNode(disk, 0);
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
		int inputFileSize = iNodesManager.getSizeiNode(disk, inputINodeRef);
		int inputFileDataBlock = iNodesManager.getFirstDataBlockFromiNode(disk, inputINodeRef);

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
				int fileDataBlock = iNodesManager.getFirstDataBlockFromiNode(disk, iNodeRef);  // Data block from the i-node
				// Set size of file into its i-node
				iNodesManager.setSizeOfiNode(disk, iNodeRef, inputFileSize);
				// Delete file from disk
				deleteFileAtDisk(disk, fileDataBlock);
				// Write new file in place of the older one
				addNewFileInDisk(disk, fileDataBlock, content);

			}
			else { // Create the new file.
				// Write new file into root directory

				int iNodeRef = writeFileInDirectory(disk, file, rootBlockNum); // Returns the iNode reference.
				// Enter the iNode and assign a free block
				// Get free block number
				int freeBN = BlockManager.getFreeBlockNumber(disk);
				// Set data block in i-node to the free block 
				iNodesManager.setDataBlockToINode(disk, iNodeRef, freeBN);
				// Set size in of file into its i-node
				iNodesManager.setSizeOfiNode(disk,iNodeRef, inputFileSize);
				// Write file into the free block
				addNewFileInDisk(disk, freeBN, content);
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
		DiskUnit disk = DiskManager.currentMountedDisk;

		// Write the title
		System.out.println();
		System.out.println("Filename:           Size (Bytes)");
		System.out.println("-------------------------------------");
		// Block Number of the root directory
		int rootNumber = iNodesManager.getFirstDataBlockFromiNode(disk, 0);
		//System.out.println("Root Number: "+rootNumber);
		printFilesFromDir(disk, rootNumber); 

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
		DiskUnit disk = DiskManager.currentMountedDisk;
		// Block Number of the root directory
		int rootBlockNum = iNodesManager.getFirstDataBlockFromiNode(disk, 0);
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
		int fileDataBlock = iNodesManager.getFirstDataBlockFromiNode(disk, iNodeRef);

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
				int nextFreeBlock = BlockManager.getFreeBlockNumber(d);
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
			String filesize = Integer.toString(iNodesManager.getSizeiNode(d, iNodeRef));
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
	 * @param disk DiskUnit in use.
	 * @param file New file to write into directory.
	 * @param blockNum Number of the block to write in.
	 * @param blockSize Bytes per block.
	 * @throws IllegalArgumentException Thrown if filename length not 20 characters long.
	 * @throws FullDiskException The disk is full.
	 * @return Returns reference to the i-node of the new file.
	 */
	public static int  writeFileInDirectory(DiskUnit disk, String file, int blockNum) 
			throws IllegalArgumentException, FullDiskException {

		// Capacity
		int blockSize = disk.getBlockSize();

		// New file name string into char array.
		char[] fileCharArray = file.toCharArray();
		if (fileCharArray.length != 20) {
			throw new IllegalArgumentException("File name must be <= 20");
		}

		// Get free byte position in directory
		ArrayList<Integer> newFileInRoot = getFreePosInDirectory(disk, blockNum, blockSize);

		// TODO: Verify file fits into new block;

		// Write file name inside root and assign a free i-node to reference it.
		// Obtain free valid i-node position
		int iNodePos;
		try {
			iNodePos = iNodesManager.getFreeiNode(disk);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new FullDiskException();  // Could not get more i-node
		}

		// Write file name and node index into root;
		int newFileBlockNum = newFileInRoot.get(0);
		int newFileBytePos = newFileInRoot.get(1);

		VirtualDiskBlock auxVirtualBlock = new VirtualDiskBlock(blockSize); // Auxiliary Virtual Disk Block
		disk.read(newFileBlockNum, auxVirtualBlock);

		// Write file name inside the block
		for (int i=0; i<fileCharArray.length; i++) {
			DiskUtils.copyCharToBlock(auxVirtualBlock, newFileBytePos+i, fileCharArray[i]);
		}
		// Copy i-node reference into the directory.
		DiskUtils.copyIntToBlock(auxVirtualBlock, newFileBytePos+20, iNodePos);

		// Write the Virtual block back into the disk unit.
		disk.write(newFileBlockNum, auxVirtualBlock);

		return iNodePos; // Returns reference to the i-node of the new file.
	}
	/**
	 * Prints the filename and file size of all the files in a directory 
	 * @param disk DiskUnit in use
	 * @param dirBlockNum Number of the first data block of the directory
	 */
	private static void printFilesFromDir(DiskUnit disk, int dirBlockNum) {

		ArrayList<Integer> dirBlockNums = allFileBlockNums(disk, dirBlockNum);

		for (Integer blockNum : dirBlockNums) {		
			System.out.println("Number of directory block numbers: "+dirBlockNums.size());
			VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(disk, blockNum);
			ArrayList<String> files = filesInDirBlock(disk, vdb);
			System.out.println("Number of Files: " +files.size());
			for (String fileContent : files) {
				System.out.println(fileContent);
			}
		}
	}

	/**
	 * Writes the new file inside the specified disk .
	 * @param disk
	 * @param firstFB
	 * @param listOfVDB
	 */
	private static void addNewFileInDisk(DiskUnit disk, int firstFB, ArrayList<VirtualDiskBlock> listOfVDB) {
		try {
			if (listOfVDB.size() < 1) { // Nothing 
				System.out.println("There is nothing in the file!");
				return;
			}
			VirtualDiskBlock vdb = listOfVDB.get(0);  // Get the VirtualDiskBlock from the ArrayList
			int nextAvailableFreeBlock;  // Holds the next free block to use
			if (listOfVDB.size() == 1) // Only one virtual block
				nextAvailableFreeBlock = 0;
			else
				nextAvailableFreeBlock = BlockManager.getFreeBlockNumber(disk); // Look for a free block
			DiskUtils.copyIntToBlock(vdb, vdb.getCapacity()-4, nextAvailableFreeBlock);  // Write free block number into last 4-bytes of block
			disk.write(firstFB, vdb);   // Write virtual disk block into disk 

			for (int i=1; i<listOfVDB.size(); i++) {
				int freeBlock = nextAvailableFreeBlock;  // Save the next block, in order to write in that block
				vdb = listOfVDB.get(i);
				if (i == listOfVDB.size()-1)
					nextAvailableFreeBlock = 0;
				else
					nextAvailableFreeBlock = BlockManager.getFreeBlockNumber(disk);  // Look for a free block
				DiskUtils.copyIntToBlock(vdb, vdb.getCapacity()-4, nextAvailableFreeBlock);  // Write free block number into last 4-bytes of block

				disk.write(freeBlock, vdb);  // Write virtual disk block into disk 
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
	private static void deleteFileAtDisk(DiskUnit d, int firstFreeBlock) {

		ArrayList<Integer> fileBlockNums = allFileBlockNums(d, firstFreeBlock);

		for (int i=0; i<fileBlockNums.size(); i++) {
			int blockNum = fileBlockNums.get(i);
			VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
			clearDiskBlock(d, blockNum, vdb);         // Clear the block
			if (i != 0) // Doesn't register the firstFreeBlock into free blocks
				BlockManager.registerFB(d, blockNum); // register free block to the free block collection.
		}
	}
	/**
	 * Clears a block by setting all its bytes to zero.
	 * @param disk
	 * @param blockNum
	 * @param vdb
	 */
	private static void clearDiskBlock(DiskUnit disk, int blockNum, VirtualDiskBlock vdb) {

		for (int i=0; i<vdb.getCapacity(); i++) {
			vdb.setElement(i, (byte) 0); // set every byte to 0 (empty) in the block
		}
		disk.write(blockNum, vdb); // Write the emptied block into the disk.

	}
	/**
	 * Method returns the largest possible file size
	 * @param disk The disk itself to be analyzed
	 * @return Returns the largest possible file given the properties of the disk
	 */
	private static int getMaxFileSizeFromDisk(DiskUnit disk){
		int N = disk.getBlockSize(); // Where N is the BlockSize 
		int maxFileSize = (N-20) + 3*N + (N/4)*N + (N/4)*(N/4)*N ;
		return maxFileSize;
	}
	
	
}

