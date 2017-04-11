package lists;

public class DLList<E> implements LinkedList<E> {
	private DNode<E> head; 
	private int length; 

	public DLList() { 
		head = null; 
		length = 0; 
	}
	
	public void addFirstNode(Node<E> nuevo) {
		// Pre: nuevo is not a node in the list
		DNode<E> dNuevo = (DNode<E>) nuevo; 
		head.setPrev(dNuevo); 
		dNuevo.setNext(head); 
		dNuevo.setPrev(null); 
		head = dNuevo; 
		length++; 
	}

	public void addNodeAfter(Node<E> target, Node<E> nuevo) {
		// Pre: target is a node in the list
		// Pre: nuevo is not a node in the list
		DNode<E> dTarget = (DNode<E>) target;
		DNode<E> nAfter = dTarget.getNext(); 
		DNode<E> dNuevo = (DNode<E>) nuevo; 
		dTarget.setNext(dNuevo); 
		dNuevo.setPrev(dTarget); 
		dNuevo.setNext(nAfter); 
		if (nAfter != null) 
			nAfter.setPrev(dNuevo); 
		length++; 
	}

	public void addNodeBefore(Node<E> target, Node<E> nuevo) {
		// Pre: target is a node in the list
		// Pre: nuevo is not a node in the list
		DNode<E> dTarget = (DNode<E>) target;
		if (dTarget == head)
			this.addFirstNode(dTarget); 
		else { 
			DNode<E> nBefore = dTarget.getPrev(); 
			this.addNodeAfter(nBefore, dTarget); 
		}
	}

	public Node<E> getLastNode() throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<E> getNodeAfter(Node<E> target) throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<E> getNodeBefore(Node<E> target) throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node<E> removeFirstNode() throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<E> removeLastNode() throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeNode(Node<E> target) {
		// TODO Auto-generated method stub

	}

	public Node<E> removeNodeAfter(Node<E> target) throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<E> removeNodeBefore(Node<E> target) throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<E> getFirstNode() throws NodeOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO
	}
	
	/**
	 * The execution of this method removes all the data nodes
	 * from the current instance of the list. 
	 */
	public void makeEmpty() { 
		// TODO
	}
	
	protected void finalize() { 
		System.out.println("GC is WORKING!");
		System.out.println("Number of nodes to remove is: "+ this.length); 
		this.removeAll(); 
	}
	

	public Node<E> createNewNode() {
		return new DNode<E>();
	}

	
	/**
	 * Class DNode, the type of node in this list. 
	 * @author pirvos
	 *
	 * @param <E> data type for the data element that 
	 *       the node is set to hold
	 */
	private static class DNode<E> implements Node<E> {
		private E element; 
		private DNode<E> prev, next; 
		public DNode() { 
			element = null; 
			prev = next = null; 
		}
		public DNode(E data, DNode<E> prev, DNode<E> next) { 
			this.element = data; 
			this.prev = prev; 
			this.next = next; 
		}
		public DNode(E data)  { 
			this.element = data; 
			next = null; 
		}
		public E getElement() {
			return element;
		}
		public void setElement(E data) {
			this.element = data;
		}
		public DNode<E> getPrev() {
			return prev;
		}
		public void setPrev(DNode<E> prev) {
			this.prev = prev;
		}
		public DNode<E> getNext() {
			return next;
		}
		public void setNext(DNode<E> next) {
			this.next = next;
		}
	}

}
