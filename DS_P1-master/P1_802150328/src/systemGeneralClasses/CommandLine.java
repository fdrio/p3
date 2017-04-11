package systemGeneralClasses;

/**
 * author: Pedro I. Rivera Vega
 */
import java.util.ArrayList;
import java.util.StringTokenizer; 

/**
 * Whenever a command is expected, the whole input line is read. From the 
 * input read, a preliminary processing of that input is done to create
 * an object of this type. Such object is later used to continue the
 * with the validation and further execution of the command. Part of this
 * preliminary analysis is to match the input with a valid command in the
 * current state of the system. 
 *  
 * @author pedroirivera-vega
 *
 */
public class CommandLine { 
	private ArrayList<String> tokensList; 	
	// list of tokens extracted from the input
	
	private SystemCommand sc; 
	// this is the system command that is associated with the
	// input line. Null if the input line does not match any valid
	// SystemCommand not valid. 
	
	/**
	 * 
	 * @param line
	 */
	public CommandLine(String line) { 
		tokensList = new ArrayList<String>(); 
		StringTokenizer st = new StringTokenizer(line); 
		while 	(st.hasMoreTokens()) 
			tokensList.add(st.nextToken()); 
		
		sc = null; 
	} 
		
	// the first token in a command line has index value 1
	// the second token has index value 2
	// ... an so on

	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getToken(int index) { 
		//  index is assumed to be >=1 and <= tokenList.size()	
		return tokensList.get(index - 1); 
	} 
	
	/**
	 * 
	 * @return
	 */
	public int getNumberOfTokens() { 
		return tokensList.size(); 
	} 
	
	/**
	 * 
	 */
	public String toString() { 
		String rs = ""; 
		for (String token : tokensList)
			rs = rs + token + " "; 
		return rs; 
	}
	
	/**
	 * 
	 * @return
	 */
	public String stringOfOperands() { 
		String rs = ""; 
		for (int i=1; i<tokensList.size(); i++)
			rs = rs + tokensList.get(i) + " "; 
		return rs; 
	}

	/**
	 * 
	 * @return
	 */
	public SystemCommand getSc() {
		return sc;
	}

	/**
	 * 
	 * @param sc
	 */
	public void setSc(SystemCommand sc) {
		this.sc = sc;
	}
}
