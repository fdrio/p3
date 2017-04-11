package diskUtilities;

import diskUnitExceptions.*;

public class VirtualDiskBlock {
	
	private static final int DEFAULT_BLOCK_SIZE = 256;
	
	private int capacity;
	private byte[] elements;
	
	/**
	 * Creates a block of size equal to 256 bytes.
	 */
	public VirtualDiskBlock() {
		this(DEFAULT_BLOCK_SIZE);

	}
	/**
	 * Creates a block of size (number of bytes) equal to blockCapacity.
	 * @param blockCapacity size of virtual disk block
	 */
	public VirtualDiskBlock(int blockCapacity) {
		if (blockCapacity < 32)
			blockCapacity = DEFAULT_BLOCK_SIZE;
		
		elements = new byte[blockCapacity];
		capacity = blockCapacity;
	}
	/**
	 * Returns a positive integer value that corresponds to the capacity 
	 * (number of character spaces or elements) of the current instance of block. 
	 * @return positive integer value that corresponds to the capacity of the current instance of block.
	 */
	public int getCapacity() {
		
		return capacity;
	}
	/**
	 * Changes the content of element at position index to that of nuevo 
	 * in the current disk block instance. It is assumed that 
	 * index is valid for the current disk block instance. 
	 * @param index index of elements array
	 * @param nuevo new value to set in the index position
	 */
	public void setElement(int index, byte nuevo) throws InvalidVirtualDiskBlockIndexException {
		if (index < 0 || index > elements.length) {
			throw new InvalidVirtualDiskBlockIndexException("The index "+index+" is out "
					+ "of bounds. VirtualDiskBlock block capacity: "+elements.length);
		}
		elements[index] = nuevo;
	}
	/**
	 * Returns a copy of the character at the position index 
	 * in the current block instance. It is assumed that
	 * index is valid for the current disk block instance.
	 * @param index index of elements array
	 * @return element in the position index
	 */
	public byte getElement(int index) throws InvalidVirtualDiskBlockIndexException {
		if (index < 0 || index > elements.length) {
			throw new InvalidVirtualDiskBlockIndexException("The index "+index+" is out "
					+ "of bounds. VirtualDiskBlock block capacity: "+elements.length);
		}
		return elements[index];
	}
	
}
