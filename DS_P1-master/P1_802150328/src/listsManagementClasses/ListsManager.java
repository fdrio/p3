package listsManagementClasses;

import java.util.ArrayList;

/**
 * This class defines the type of object that manages the different 
 * lists being managed by the system for the lab session 
 * 
 * @author pedroirivera-vega
 *
 */
public class ListsManager {
	private ArrayList<NamedList> lists; 
	
	public ListsManager() { 
		lists = new ArrayList<>(); 
	}

	/**
	 * Find the index of the position where a list with a given name is. 
	 * If no such list it returns -1; otherwise, it returns the index of
	 * the position where it is located in the list of lists....
	 * @param name
	 * @return
	 */
	public int getListIndex(String name) { 
		for (int i=0; i<lists.size(); i++) 
			if (lists.get(i).getName().equals(name)) 
				return i; 
		return -1; 
	}

	/**
	 * Creates a new named list with the given name. 
	 * @param lName the name of the new list. 
	 *     PRE: the name given is assumed to be a valid name for 
	 *     a list. 
	 */
	public void createNewList(String lName) {
		lists.add(new NamedList(lName)); 
	}
	
	public void addElement(int listIndex, int value) 
			throws IndexOutOfBoundsException 
	{ 
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		lists.get(listIndex).add(value); 		
	}
	
	public int removeElement(int listIndex, int index) 
			throws IndexOutOfBoundsException 
	{
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		return lists.get(listIndex).remove(index); 				
	}
		
	public void addElement(int listIndex, int index, int value) 
			throws IndexOutOfBoundsException 
	{ 
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		lists.get(listIndex).add(index, value); 		
	}

	public int getElement(int listIndex, int index) 
			throws IndexOutOfBoundsException 
	{
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		return lists.get(listIndex).get(index); 				
	}
	
	public int getSize(int listIndex) 
	{
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		return lists.get(listIndex).size(); 				
	}
	
	public String getName(int listIndex) {
		// PRE: the listIndex is assumed to be a valid index for the
		// list of lists....
		return lists.get(listIndex).getName(); 
	}
	
	public int getNumberOfLists() { 
		return lists.size(); 
	}
	
	public boolean nameExists(String name) { 
		int index = getListIndex(name); 
		return index != -1; 
	}
}
