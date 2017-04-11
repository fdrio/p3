/**
 * 
 */
package lists;

/**
 * @author pirvos
 *
 */
public class SLList<E> implements LinkedList<E> {
	private SNode<E> head; 
	private int length; 

	public SLList() { 
		head = null; 
		length = 0; 
	}
	
	public void addFirstNode(Node<E> nuevo) {
		// Pre: nuevo is not a node in the list
		((SNode<E>) nuevo).setNext(head); 
		head = (SNode<E>) nuevo; 
		length++; 
	}

	public void addNodeAfter(Node<E> target, Node<E> nuevo) {
		// Pre: target is a node in the list
		// Pre: nuevo is not a node in the list
		((SNode<E>) nuevo).setNext(((SNode<E>) target).getNext()); 
		((SNode<E>) target).setNext((SNode<E>) nuevo); 
		length++; 
	}

	public void addNodeBefore(Node<E> target, Node<E> nuevo) {
		// Pre: target is a node in the list
		// Pre: nuevo is not a node in the list

		if (target == head)
			this.addFirstNode(nuevo); 
		else { 
			Node<E> prevNode = findNodePrevTo(target);  
			this.addNodeAfter(prevNode, nuevo); 
		}
	}

	private Node<E> findNodePrevTo(Node<E> target) {
		// Pre: target is a node in the list
		if (target == head) 
			return null; 
		else { 
			SNode<E> prev = head; 
			while (prev != null && prev.getNext() != target) 
				prev = prev.getNext();  
			return prev; 
		}
	}

	public Node<E> getLastNode() 
	throws NodeOutOfBoundsException 
	{
		if (head == null)
			throw new NodeOutOfBoundsException("getLastNode(): Empty list."); 
		else { 
			SNode<E> curr = head; 
			while (((SNode<E>) curr).getNext() != null)
				curr = curr.getNext(); 
			return curr; 
		}
	}

	public Node<E> getNodeAfter(Node<E> target) 
	throws NodeOutOfBoundsException 
	{
		// Pre: target is a node in the list
		SNode<E> aNode = ((SNode<E>) target).getNext(); 
		if (aNode == null)  
			throw new NodeOutOfBoundsException("getNextNode(...) : target is the last node."); 
		else 
			return aNode;
	}


	public Node<E> getNodeBefore(Node<E> target) 
	throws NodeOutOfBoundsException 
	{
		// Pre: target is a node in the list
		if (target == head)  
			throw new NodeOutOfBoundsException("getPrevNode(...) : target is the first node."); 
		else 
			return findNodePrevTo(target);
	}

	public int length() {
		return this.length;
	}

	public Node<E> removeFirstNode() 
	throws NodeOutOfBoundsException 
	{
		if (head == null) 
			throw new NodeOutOfBoundsException("removeFirstNode(): linked list is empty."); 

		// the list is not empty....
		SNode<E> ntr = head; 
		head = head.getNext(); 
		ntr.setNext(null);      // notice that the node keeps its data..
		length--; 
		return ntr;
	}

	public Node<E> removeLastNode() 
	throws NodeOutOfBoundsException 
	{
		if (head == null) 
			throw new NodeOutOfBoundsException("removeFirstNode(): linked list is empty."); 

		// the list is not empty....
		if (head.getNext() == null)
			return this.removeFirstNode(); 
		else { 
			SNode<E> prevNode = head; 
			SNode<E> ntr = prevNode.getNext(); 
			while (ntr.getNext() != null)
			{
				prevNode = ntr; 
				ntr = ntr.getNext(); 
			}
			prevNode.setNext(ntr.getNext()); 
			length--; 
			return ntr;
		} 
	}

	public void removeNode(Node<E> target) {
		// Pre: target is a node in the list
		
		if (target == head) 
			this.removeFirstNode(); 
		else { 
			SNode<E> prevNode = (SNode<E>) this.getNodeBefore(target); 
			prevNode.setNext(((SNode<E>) target).getNext()); 
			length--; 
		}
		
	}

	public Node<E> removeNodeAfter(Node<E> target) 
	throws NodeOutOfBoundsException 
	{
		// Pre: target is a node in the list
		if (((SNode<E>) target).getNext() == null)
			throw new NodeOutOfBoundsException("removeNodeAfter(...) : target is the last node...");			

		SNode<E> ntr = ((SNode<E>) target).getNext(); 
		((SNode<E>) target).setNext(ntr.getNext()); 
		ntr.setNext(null); 
		length--; 
		return ntr;
	}

	public Node<E> removeNodeBefore(Node<E> target) 
	throws NodeOutOfBoundsException 
	{
		// Pre: target is a node in the list
		if (target == head)
			throw new NodeOutOfBoundsException("removeNodeBrfore(...) : target is the first node...");			

		// head is not the first node. 
		// need to find the node before target and the one before it, if any...
		Node<E> prevNode = this.getNodeBefore(target); 
		this.removeNode(prevNode); 
		return prevNode;
	}
	
	public Node<E> getFirstNode() 
	throws NodeOutOfBoundsException {
		if (head == null)
			throw new NodeOutOfBoundsException("getFirstNode() : linked list is empty..."); 
		
		// the linked list is not empty....
		return head;
	}
	
	/**
	 * Prepares every node so that the garbage collector can free 
	 * its memory space, at least from the point of view of the
	 * list. This method is supposed to be used whenever the 
	 * list object is not going to be used anymore. Removes all
	 * physical nodes (data nodes and control nodes, if any)
	 * from the linked list
	 */
	private void removeAll() {
		while (head != null) { 
			Node<E> nnode = head.getNext(); 
			head.setElement(null); 
			head.setNext(null); 
			head = (SNode<E>) nnode; 
		}
	}
	
	/**
	 * The execution of this method removes all the data nodes
	 * from the current instance of the list. 
	 */
	public void makeEmpty() { 
		// TODO
	}
		
	protected void finalize() throws Throwable {
	    try {
			System.out.println("GC is WORKING!");
			System.out.println("Number of nodes to remove is: "+ this.length); 
			this.removeAll(); 
	    } finally {
	        super.finalize();
	    }
	}
	
	public Node<E> createNewNode() {
		return new SNode<E>();
	}

	
	private static class SNode<E> implements Node<E> {
		private E element; 
		private SNode<E> next; 
		public SNode() { 
			element = null; 
			next = null; 
		}
		public SNode(E data, SNode<E> next) { 
			this.element = data; 
			this.next = next; 
		}
		public SNode(E data)  { 
			this.element = data; 
			next = null; 
		}
		public E getElement() {
			return element;
		}
		public void setElement(E data) {
			this.element = data;
		}
		public SNode<E> getNext() {
			return next;
		}
		public void setNext(SNode<E> next) {
			this.next = next;
		}
	}

}
