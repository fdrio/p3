package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DirectoryManager {
		
	/**
	 * Creates the DiskUnit directory. This directory stores all the RAF files
	 * and the DiskNames text file.
	 * @return Returns true if the directory was created.
	 */
	public static boolean createDiskDirectory() {
		
		File diskDir = new File("DiskUnits");
		try{
		// if the directory does not exist, create it
		if (!diskDir.exists()) {
		        diskDir.mkdir();
		        return true;
		    } 
		} catch(SecurityException se){
	        se.printStackTrace();
	    }          		  
		return false;
	}
	/**
	 * Creates the DiskNames text file. This file stores the name of the 
	 * DiskUnits created. 
	 * @return Returns true if the file was created.
	 */
	private static boolean createDiskNamesFile() {
		File diskName = new File("DiskUnits", "DiskNames.txt");
		// if the file does not exist, return true
		try {
			if (diskName.createNewFile())
			   return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Adds Disk Unit name to the Disks Name text file
	 * @param name Name of the disk unit
	 */
	public static void addUnitToDiskNames(String name) {
		DiskManager.diskUnitNames.add(name);
		updateDiskNames();
	}
	/**
	 * Removes Disk Unit name from the Disks Name text file
	 * @param name Name of the disk unit
	 */
	public static void removeUnitFromDiskNames(String name) {
		DiskManager.diskUnitNames.remove(name);
		updateDiskNames();
	}
	/**
	 * Adds newly created Disk Unit name to the Disks Name text file
	 * @param name Name of the disk unit
	 */
	public static void updateDiskNames() {
		try {
			createDiskNamesFile();
			File diskNames = new File("DiskUnits", "DiskNames.txt");
			if (!diskNames.exists()) {
				System.out.println("Could not find DiskNames.txt file");
				return;
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(diskNames));
				for (String unitName : DiskManager.diskUnitNames)
					writer.write(unitName+"\n"); // write the names into the text file
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Gets the disk unit names and place it into an ArrayList.
	 * In order to keep the names in memory.
	 */
	public static void getDiskUnitNames() {
		String line; // Hold the reader lines
		try {
			File diskNamesText = new File("DiskUnits", "DiskNames.txt");
			if (!diskNamesText.exists())
				return;
			BufferedReader reader = new BufferedReader(new FileReader(diskNamesText));		
			do {
				line = reader.readLine();
				if (line != null)
					DiskManager.diskUnitNames.add(line);
			} while (line != null);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
}
