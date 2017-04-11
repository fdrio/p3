/**
 * 
 */
package main;

import java.io.IOException;

import diskUtilities.*;
import systemGeneralClasses.SystemController;

/**
 * @author Pedro I. Rivera-Vega
 *
 */
public class p3Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException  {
		
		// Initialing Shell
		DirManager.createDiskDirectory(); // Creates the DiskUnit directory
		DirManager.getDiskUnitNames();    // Place in memory the names of the already created DiskUnits
		
		// Processing commands
		SystemController system = new SystemController(); 
		system.start(); 
		// the system is shutting down...
		System.out.println("+++++ SYSTEM SHUTDOWN +++++"); 
	}

}
