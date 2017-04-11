package stack;

import exceptions.EmptyStackException;
import exceptions.FullStackException;

public class IntStack {
	private static final int DS = 10;    // stack's default capacity
	private int[] element;               // the stack's content
	private int top; 
	
	public IntStack() { 
		element = new int[DS]; 
		top = -1; 
	}
	
	public IntStack(int s) { 
		if (s<=0) s = DS; 
		element = new int[s]; 
		top = -1; 
	}
	
	public boolean isEmpty() { 
		return top == -1; 
	}
	
	public int size() { 
		return top+1; 
	}
	
	public int pop() throws EmptyStackException {
		if (isEmpty()) 
			throw new EmptyStackException(); 
		return element[top--]; 
	}
	
	public void push(int n) throws FullStackException { 
		if (top == element.length - 1) 
			throw new FullStackException("Full stack in push..."); 
		else 
			element[++top] = n; 
	}
	
	public int top() throws EmptyStackException { 
		if (isEmpty())
			throw new EmptyStackException(); 
		return element[top]; 
	}

}
