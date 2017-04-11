package diskUtilities;
import diskUnitExceptions.*;

public interface DiskUnitInterface {
	
	/**
	 * Writes the content of block b into the disk block corresponding to blockNum; i.e., whatever 
	 * is the actual content of the disk block corresponding to the specified block number (blockNum) 
	 * is changed to (or overwritten by) that of b in the current disk instance. 
	 * The first exception is thrown whenever the block number received is not valid for the current disk instance. 
	 * The second exception is thrown whenever b does not represent a valid disk block for the current disk instance 
	 * (for example, if b is null, or if that block instance does not match the block size of the current disk instance).
	 * @param blockNum number of blocks in the disk unit
	 * @param b VirtualDiskBlock object
	 * @throws InvalidBlockNumberException thrown when block number is not within the accepted range
	 * @throws InvalidBlockException thrown when a blocks properties are not valid
	 */
	public void write(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException;
	/**
	 * Reads a given block from the disk. The content of the specified disk block (identified by its number � blockNum) is copied 
	 * as the new content of the current instance of block being referenced by parameter b. 
	 * Notice that b must reference an existing instance of VirtualDiskBlock, and that the current content of that instance 
	 * shall be overwritten by the content of the disk block to be read. The announced exceptions are thrown as described for the write operation.
	 * @param blockNum number of blocks in the disk unit
	 * @param b VirtualDiskBlock object
	 * @throws InvalidBlockNumberException thrown when block number is not within the accepted range
	 * @throws InvalidBlockException thrown when a blocks properties are not valid
	 */
	public void read(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException; 
	/**
	 * Returns a nonnegative integer value corresponding to the number of valid blocks (unused + used) that the current disk instance has.
	 * @return Nonnegative integer value corresponding to the number of valid blocks that the current disk instance has.
	 */
	public int getCapacity();
	/**
	 * Returns a nonnegative integer value which corresponds to the size (number of character elements) of a block in the current disk instance.
	 * @return Nonnegative integer value which corresponds to the size of a block in the current disk instance.
	 */
	public int getBlockSize();
	/**
	 * Formats the disk. This operation visits every �physical block� in the disk and fills with zeroes all those that are valid.
	 */
	public void lowLevelFormat();
	/**
	 * Turns off the current disk unit. It saves all data needed in order for the same content of the disk to be available 
	 * whenever the disk is activated (or  mounted) in the future.
	 */
	public void shutdown(); 
}
