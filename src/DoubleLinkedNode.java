/**
 * Double Linked Node
 * 
 * DoubleLinkedNode.java
 * CS201 | Assignment 3
 *
 * @author Ryan Josey
 * 
 * This code was inspired by sudo code from "Fundamentals of Python: 
 * Data Structures, by Kenneth Lambert, Cengage Learning, 2014" and
 * concepts from "en.wikipedia.org/wiki/Double-ended_queue".
 */
public class DoubleLinkedNode {
	private int _character;
	private DoubleLinkedNode _previous;
	private DoubleLinkedNode _next;
	
	/**
	 * Create a double linked node holding a vertex.
	 * @param vertex the vertex being stored
	 * @param previous the pointer to the previous double linked node
	 * @param next the pointer to the next double linked node
	 */
	public DoubleLinkedNode(int character, DoubleLinkedNode previous, DoubleLinkedNode next) {
		this._character = character;
		this._previous = previous;
		this._next = next;
	}

	/**
	 * Get the current vertex stored.
	 * @return vertex the vertex that is stored
	 */	
	public int getCharacter() {
		return this._character;
	}

	/**
	 * Store a vertex in a double linked node.
	 * @param vertex the vertex that will be stored
	 */	
	protected void setNode(int character) {
		this._character = character;
	}

	/**
	 * Get the previous double linked node.
	 * @return node the previous double linked node
	 */
	public DoubleLinkedNode getPrevious() {
		return this._previous;
	}

	/**
	 * Store previous double linked node.
	 * @param node the previous double linked node that will be stored
	 */
	protected void setPrevious(DoubleLinkedNode node) {
		this._previous = node;
	}

	/**
	 * Get the next double linked node.
	 * @return node the next double linked node
	 */
	public DoubleLinkedNode getNext() {
		return this._next;
	}

	/**
	 * Store next double linked node.
	 * @param node the next double linked node that will be stored
	 */
	protected void setNext(DoubleLinkedNode node) {
		this._next = node;
	}
}