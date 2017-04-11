package listsManagementClasses;

import java.util.ArrayList;

public class NamedList extends ArrayList<Integer> {

	private String name; 
	public NamedList(String name) { 
		this.name = name; 
	}
	
	public String getName() {
		return name;
	}
	
}
