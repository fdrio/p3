package diskUtilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Some utility methods to copy/get int values, byte values and char values
 * to/from VirtualDiskBlock or array of bytes...
 * 
 * @author pedroirivera-vega
 */
public class DiskUtils {
	public static final int INTSIZE = 4; 
	
	/**
	 * Copy an integer value into four consecutive bytes in a block.
	 * @param vdb The block where the integer value is copied.
	 * @param index The index of the first byte in the block that
	 * shall be written. The number is stored in four bytes whose 
	 * indexes are: index, index+1, index+2, and index+3, starting
	 * from the less significant byte in the number up to the most
	 * significant. 
	 * @param value The integer value to be written in block. 
	 */
	public static void copyIntToBlock(VirtualDiskBlock vdb, int index, int value) { 
		for (int i = INTSIZE-1; i >= 0; i--) { 
			vdb.setElement(index+i, (byte) (value & 0x000000ff)); 	
			value = value >> 8; 
		}
	}
	
	/**
	 * Extracts an integer value from four consecutive bytes in a block. 
	 * @param vdb The block. 
	 * @param index The index in block of the less significant byte of the  
	 * number to extract. 
	 * @return The value extracted from bytes index+3, index+2, index+1, and index. 
	 * From most significant to less significant bytes of the number's four bytes. 
	 */
	public static int getIntFromBlock(VirtualDiskBlock vdb, int index) {  
		int value = 0; 
		int lSB; 
		for (int i=0; i < INTSIZE; i++) { 
			value = value << 8; 
			lSB = 0x000000ff & vdb.getElement(index + i);
			value = value | lSB; 
		}
		return value; 
	}

	/**
	 * Copy an integer value into four consecutive bytes in an array of bytes.
	 * @param b The array where the integer value is copied.
	 * @param index The index of the first byte in the b that
	 * shall be written. The number is stored in four bytes whose 
	 * indexes are: index, index+1, index+2, and index+3, starting
	 * from the less significant byte in the number up to the most
	 * significant. 
	 * @param value The integer value to be written in the array. 
	 */
	public static void copyIntToBytesArray(byte[] b, int index, int value) { 
		for (int i = INTSIZE-1; i >= 0; i--) { 
			b[index+i] = (byte) (value & 0x000000ff); 	
			value = value >> 8; 
		}
	}
	
	/**
	 * Extracts an integer value from four consecutive bytes in a byte[] array. 
	 * @param b The array. 
	 * @param index The index in block of the less significant byte of the  
	 * number to extract. 
	 * @return The value extracted from bytes index+3, index+2, index+1, and index. 
	 * From most significant to less significant bytes of the number's four bytes. 
	 */
	public static int getIntFromBytesArray(byte[] b, int index) {  
		int value = 0; 
		int lSB; 
		for (int i=0; i < INTSIZE; i++) { 
			value = value << 8; 
			lSB = 0x000000ff & b[index + i];
			value = value | lSB; 
		}
		return value; 
	}

	// working with characters to and from VirtualBlock
	public static void copyCharToBlock(VirtualDiskBlock vdb, int index, char c) { 
		vdb.setElement(index, (byte) c); 
	}	
	public static char getCharFromBlock(VirtualDiskBlock vdb, int index) { 
		return (char) vdb.getElement(index); 
	}
	
	// working with characters to and from an array of bytes
	public static void copyCharToBytesArray(byte[] b, int index, char c) { 
		b[index] = (byte) c; 
	}	
	public static char getCharFromBytesArray(byte[] b, int index) { 
		return (char) b[index]; 
	}	
	
	/**
	 * Copies the contents of a disk block into a Virtual Disk Block
	 * @param d DiskUnit
	 * @param blockSize Bytes in the Block. 
	 * Needs to be the same as the amount of bytes per block of the disk blocks.
	 * @param blockNum Index of disk block to copy.
	 * @return
	 */
	public static VirtualDiskBlock copyBlockToVDB(DiskUnit d, int blockNum) {
		VirtualDiskBlock vdb = new VirtualDiskBlock(d.getBlockSize());
		d.read(blockNum, vdb);
		
		return vdb;
	}
	
	/**
	 * Reads the contents of an external file. It separates the content into VirtualDiskBlocks, 
	 * while leaving 4 bytes for the integer which makes reference to the next data block.
	 * @param file File to be read.
	 * @return Returns ArrayList of VirtualDiskBlocks with the contents of the file.
	 */
	public static ArrayList<VirtualDiskBlock> setExtFileContentToVDBs(String file, int blockSize) {
		
		try {
			RandomAccessFile fileToRead = new RandomAccessFile(file, "rw");
			ArrayList<VirtualDiskBlock> vdbArray = new ArrayList<>();
			double fileToReadSize = (double) fileToRead.length();  // Size of the fileToRead in bytes.
			int usableBytes = blockSize - 4;  // Amount of bytes for the fileToRead content (-4 to reserve space for integer)
			int numOfBlocks =  (int) Math.ceil(fileToReadSize / usableBytes); // Amount of blocks needed to copy fileToRead.		
			int byteCounter = 0; // Counts the bytes read
			
			// Store the contents of the random access file into VDBs
			fileToRead.seek(0);
			for (int i=0; i < numOfBlocks; i++) { // Iterate through number of blocks
				VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
				for (int j=0; j < usableBytes; j++) {
					if (byteCounter == (int) fileToReadSize) // Verify read the total bytes in the RAF
						break;
					vdb.setElement(j, fileToRead.readByte());	// Set the read byte into the VDB
					byteCounter++;  // Increase the counter after reading a byte from RAF
				}
				vdbArray.add(vdb); // Add the block to the ArrayList
			}
			fileToRead.close(); // Close RAF
			return vdbArray;
		}
		catch (IOException e) {
			System.err.println("Unable to open random access file.");
		}	
		return null;
	}
	/**
	 * Sets the content of an internal file into an ArrayList of VirtualDiskBlock
	 * @param d DiskUnit in use.
	 * @param fileBlockNum First block number of the file.
	 * @return Returns an ArrayList of VirtualDiskBlock with contents of internal file.
	 */
	public static ArrayList<VirtualDiskBlock> setFileContentToVDBs(DiskUnit d, int fileBlockNum) {
		
		int blockSize = d.getBlockSize();
		ArrayList<Integer> fileBlockNums = FileManager.allFileBlockNums(d, fileBlockNum); // ArrayList with the block numbers of the file 
		
		ArrayList<VirtualDiskBlock> fileContent = new ArrayList<>(); // To store the VDBs with the content of the file
		
		for (Integer blockNum : fileBlockNums) {  // Iterate through the block numbers of the file
			
			VirtualDiskBlock vdb = copyBlockToVDB(d, blockNum);
			copyIntToBlock(vdb, blockSize-4, 0); // Erase the reference to the next block
			
			fileContent.add(vdb); // add VDB to the ArrayList
			
		}
		
		return fileContent;
		
	}
	/**
	 * Obtains an ArrayList of VirtualDiskBlocks, copies its content as a single string
	 * and prints the file string.
	 * @param vdBlocks ArrayList of VirtualDiskBlocks
	 */
	public static void printContentOfVDBlocks(ArrayList<VirtualDiskBlock> vdBlocks) {
		
		String fileString = ""; // Initial file string
		
		for (VirtualDiskBlock block : vdBlocks) { 
			
			char[] blockCharArray = new char[block.getCapacity()-4]; // Copy bytes in the block into a char array
			for (int i=0; i<block.getCapacity()-4; i++) {    // Get bytes up to the blocksize - 4, the next data block index is stored there.
				blockCharArray[i] = getCharFromBlock(block, i);
			}
			fileString += new String(blockCharArray); // Concatenate the block string into the file string
		}
		
		System.out.println(fileString); // Print the contents of the file
	}
	
	/**
	 * Format file strings to be a 20 byte string.
	 * @param file Filename to format.
	 * @return Formatted string
	 */
	public static String formatFileName(String file) throws IllegalArgumentException {
		
		if (file.length() > 20)
			throw new IllegalArgumentException("Filename is greater than 20 characters.");
			
		int remainingChars = 20 - file.length();
		for (int i=0; i < remainingChars; i++) {
			file += " ";
		}
		return file;
	}
	
	
	
}












