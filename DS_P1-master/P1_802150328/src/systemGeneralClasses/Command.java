package systemGeneralClasses;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class Command {
	protected ArrayList<String> tokensList; 	

	/**
	 * 
	 * @return
	 */
	
	public Command() {}
	
	public Command(String input) { 
		tokensList = new ArrayList<String>(); 
		StringTokenizer st = new StringTokenizer(input); 
		while (st.hasMoreTokens())
			tokensList.add(st.nextToken()); 

	}
	
	public int getNumberOfOperands() { 
		return tokensList.size() - 1; 
	}
	
	public String getName() { 
		return tokensList.get(0); 
	}
	
	public String toString() { 
		String rs = ""; 
		for (int i=0; i < tokensList.size(); i++)
			rs = rs + tokensList.get(i)+ " "; 
		return rs; 
	}

}
