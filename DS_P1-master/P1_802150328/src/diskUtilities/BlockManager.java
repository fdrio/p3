package diskUtilities;

import java.io.IOException;
import java.io.RandomAccessFile;

import diskExceptions.FullDiskException;

/**
 * Class that manages the blocks
 * In this implementation works like a tree structure
 * @author Francisco Diaz
 *
 */
public class BlockManager {
	
	private final int INTEGERS_IN_BLOCK; // Amount of integers that fit inside a block, each of 4 bytes
	
	private DiskUnit disk;  // Disk in which the free blocks are managed.
	private int firstFreeBlock;   // Root of the collection of free blocks.
	private int flIndex;    // Index of free blocks available.
	 
	
	/**
	 * Constructor - Sets the disk unit to work with.
	 * @param d DiskUnit to manage.
	 */
	public BlockManager(DiskUnit d) {
		disk = d;
		firstFreeBlock = d.getFirstDataBlock();
		flIndex = d.getNextFreeBlock();
		INTEGERS_IN_BLOCK = d.getBlockSize() / 4;
	}
	
	/**
	 * Obtains the next free block (the root block) in the DiskUnit.
	 * Credit to  Prof. Pedro Rivera
	 * @param disk DiskUnit to be used
	 * @return Returns Block Number of free Block
	 */
	public static int getFreeBlockNumber(DiskUnit disk) throws FullDiskException {
		int intergersInBlock = disk.getBlockSize() / 4;
		int firstFLB = disk.getFirstDataBlock();
		int flIndex = disk.getNextFreeBlock();
		int bn;
		
		if (firstFLB == 0)
			throw new FullDiskException("Disk is full.");
		// disk has space
		if (flIndex != 0) {
			bn = getIntInsideBlock(disk, firstFLB, flIndex);
			flIndex--;
			disk.setNextFreeBlock(flIndex);
		} else {   // the current root node in the tree is the one to be returned
			bn = firstFLB;
			firstFLB = getIntInsideBlock(disk, firstFLB, 0);
			disk.setFirstDataBlock(firstFLB);
			flIndex = intergersInBlock-1;
			disk.setNextFreeBlock(flIndex);
		}
		
		return bn;  // the index of the free block that is taken
	}
	
	/**
	 * Inserts a freed block into the free block structure.
	 * Algorithm presented by Prof. Pedro Rivera
	 * @param d DiskUnit
	 * @param bn Index of the freed block.
	 */
	public static void registerFB(DiskUnit d, int bn) {
		int INTEGERS_IN_BLOCK = d.getBlockSize() / 4;
		int firstFLB = d.getFirstDataBlock();
		int flIndex = d.getNextFreeBlock();
		
		if (firstFLB == 0) {
			firstFLB = bn;
			d.setFirstDataBlock(bn);
			setIntInsideBlock(d, firstFLB, 0, 0);
			d.setNextFreeBlock(0);   // flIndex = 0
		} else if (flIndex == INTEGERS_IN_BLOCK-1) { // If flIndex is the last integer in the block
			setIntInsideBlock(d, bn, 0, firstFLB);
			d.setNextFreeBlock(0);
			d.setFirstDataBlock(bn);
		} else {
			flIndex++;
			d.setNextFreeBlock(flIndex);
			setIntInsideBlock(d, firstFLB, flIndex, bn);
		}
	}
	
	/**
	 * Sets an integer in the provided index inside a free data block.
	 * Equivalent to block[index] = value;
	 * @param blockNum Number of data block to write into.
	 * @param index Index in the block to write to
	 * @param value New integer value to write
	 */
	public static void setIntInsideBlock(DiskUnit d, int blockNum, int index, int value) {
		VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
		DiskUtils.copyIntToBlock(vdb, 4*index, value); // Copies integer into the different possible integer indexes inside VDB
		d.write(blockNum, vdb);
	}
	
	/**
	 * Gets an integer from the provided index inside a free data block.
	 * Equivalent to value = block[index];
	 * @param d DiskUnit in use.
	 * @param blockNum Number of data block to get integer from.
	 * @param index Index in the block where integer's byte begin.
	 */
	public static int getIntInsideBlock(DiskUnit d, int blockNum, int index) {
		VirtualDiskBlock vdb = DiskUtils.copyBlockToVDB(d, blockNum);
		int intInsideBlock = DiskUtils.getIntFromBlock(vdb, 4*index); // Gets integer from the different possible integer indexes inside VDB
		DiskUtils.copyIntToBlock(vdb, 4*index, 0); // Change the reference to the free block into zero.
		d.write(blockNum, vdb); // Write the vdb with zero in the position of the retrieved data block number.
		
		return intInsideBlock;
	}
	
	/**
	 * Initializes the free block structure. 
	 * @param disk DiskUnit in use
	 */
	public static void initializeFreeBlocks(DiskUnit disk) {
		int firstFreeBlock = disk.getFirstDataBlock();
		for (int i=firstFreeBlock+1; i < disk.getCapacity(); i++) {
			registerFB(disk, i);
		}
	}
	
	
	
	
}
