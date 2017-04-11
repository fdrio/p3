package testers;
import diskUtilities.*;

public class DiskUnitTester0 {

	/**
	 * @param args Systems argument
	 */
	public static void main(String[] args) {
		DiskUnit.createDiskUnit("disk1", 256, 32);
		DiskUnit.createDiskUnit("disk2", 256, 32);
		DiskUnit.createDiskUnit("disk3", 256, 32);
		DiskUnit.createDiskUnit("disk4", 256, 32);
		DiskUnit.createDiskUnit("disk5", 256, 32);

		
		String[] disks = {"disk1", "disk2", "disk3", "disk4", "disk5"};


		String s = "You can modify the testers to make them more user-friendly. The " +
				"current versions of tester 1 and of tester 2 require some minor editing in" +
				" order to use it on a particular disk wanted. Just edit (inside the main method) " +
				"the line that   mounts   the particular disk that you want to use. Remember that " +
				"tester 0 only creates the six disks mentioned above. Again, you can modify to " +
				"create others or to make those testers easier to use. You should run tester 1 " +
				"on a particular disk unit before running the tester 2 on that same unit. " +
				"As part of this exercise, you must figure out what each tester does and how it " +
				"works. Why is the output as it is on each case? Basically, we are creating a file. "; 

		for (int i=0; i<disks.length; i++) {
			//DiskUnit d = DiskUnit.mount(disks[i]);  			
			//splitAndWriteToDisk(s, d);  
			//d.shutdown(); 
		}

	}


	private static void splitAndWriteToDisk(String s, DiskUnit d) {
		// The following is the list of blocks from the virtual
		// disk that are assigned to the file. The first block
		// for the file is block 1. Each block has a "next block"
		// corresponding the number of the next disk block assigned
		// the the file. The file may require less blocks that the 
		// number of blocks in the following array. If more, then
		// an error explodes somewhere. But that's ok as for this
		// test. These is just a sequence of different block numbers
		// arbitrarily chosen.
		int[] fileBlocks = {1, 23, 15, 2, 17, 3, 12, 11, 205, 42, 32, 
				13, 5, 16, 58, 70, 221, 10, 71, 72, 26, 8, 73, 38, 
				4, 6, 7, 105, 9, 14, 104, 40, 95, 94,  
				18, 107, 19, 220, 20, 139, 21, 180, 243,
				22, 24, 25, 27, 28, 29, 30, 31, 242, 
				33, 34, 181, 35, 36, 37, 39, 41, 43, 
				44, 45, 46, 47, 48, 49, 50, 191, 51, 52, 53, 54,
				55, 56, 57, 59, 182, 60, 61, 62, 63, 64, 65, 
				66, 67, 68, 69, 74, 75, 76, 192, 223, 193, 
				77, 78, 79, 80, 81, 184, 222, 82, 83, 84, 85, 86, 87, 
				90, 91, 92, 93, 96, 97, 98, 99, 100, 241, 224,
				101, 185, 102, 103, 106, 108, 109,  195, 240,
				110, 111, 112, 194, 113, 114, 115, 116, 117, 118, 
				119, 120, 121, 122, 128, 88, 89, 123, 124, 
				125, 126, 127, 200, 201, 202, 203, 204, 225,
				206, 207, 208, 209, 130, 131, 132, 133, 134, 
				135, 136, 137, 138, 140, 141, 142, 143, 
				144, 145, 146, 147, 148, 149, 190, 226, 227}; 

		int bsize = d.getBlockSize(); 
		VirtualDiskBlock vdb = new VirtualDiskBlock(bsize); 
		int chunkSize = bsize - 4; 

		boolean done = false; 
		int chn = 0, bn = 0; 
		byte[] barr = new byte[bsize]; 
		while (!done) { 
			int b = 0; 
			for (int i=0; i<chunkSize && chn < s.length(); i++) { 
				barr[i] = (byte) s.charAt(chn); 
				chn++; 
				b++; 
			}
			// fill with zeroes if needed
			for (int i=b; i<chunkSize; i++)
				barr[i] = (byte) 0; 

			// copy the content of barr to block number fileBlocks[bn-1]
			// in the virtual disk unit
			for (int i=0; i<chunkSize; i++)  
				vdb.setElement(i, barr[i]);  			

			copyNextBNToBlock(vdb, fileBlocks[bn+1]); 
			bn++;
			if ((chn >= s.length() || bn >= fileBlocks.length)) { 
				copyNextBNToBlock(vdb, 0); 
				done = true; 
			}

			d.write(fileBlocks[bn-1], vdb); 

		}

	}

	public static void copyNextBNToBlock(VirtualDiskBlock vdb, int value) { 
		int lastPos = vdb.getCapacity()-1;
		for (int index = 0; index < 4; index++) { 
			vdb.setElement(lastPos - index, (byte) (value & 0x000000ff)); 	
			value = value >> 8; 
		}

	}

}


