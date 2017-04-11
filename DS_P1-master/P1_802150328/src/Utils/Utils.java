package Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Utilities used in the package.
 * @author Francisco Diaz
 *
 */
public class Utils {
	
	/**
	 * Checks whether the input number is a power of 2.
	 * @param number the number to be analyzed
	 * @return returns true if and only if the number is a power of 2 
	 */
	public static boolean powerOf2(int number){
		return ((number != 0) && (number & (number - 1)) == 0);//;
	}
}
	