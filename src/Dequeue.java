/**
 * Double-Ended Queue (Dequeue)
 * 
 * Dequeue.java
 * CS201 | Assignment 3
 *
 * @author Ryan Josey
 * 
 * This code was inspired by sudo code from "Fundamentals of Python: 
 * Data Structures, by Kenneth Lambert, Cengage Learning, 2014" and
 * concepts from "en.wikipedia.org/wiki/Double-ended_queue".
 */
public class Dequeue extends AbstractBag {
	private DoubleLinkedNode _front;
	private DoubleLinkedNode _back;
	
	/**
	 * Creates a empty double-ended queue.
	 */
	public Dequeue() {
		this._front = null;
		this._back = null;
	}
	
	/**
	 * Enqueue a vertex into the back of a double-ended queue.
	 * @param node the vertex that will be enqueued into the back of the queue
	 */
	public void pushBack(int character) {
	    DoubleLinkedNode newItem = new DoubleLinkedNode(character, null, null);
		if (this.isEmpty()) {
			this._front = newItem;
			this._back = newItem;
	    } 
	    else {
	        newItem.setPrevious(this._back);
	        this._back.setNext(newItem);
	        this._back = newItem;
	    }
	    this.incSize();
	}
	
	/**
	 * Enqueue a vertex into the front of a double-ended queue.
	 * @param node the node that will be enqueued into the front of the queue
	 */
	public void pushFront(int character) {
		DoubleLinkedNode newItem = new DoubleLinkedNode(character, null, null);
		if (this.isEmpty()) {
			this._front = newItem;
			this._back = newItem;
	    }
		else {
			newItem.setNext(this._front);
			this._front.setPrevious(newItem);
			this._front = newItem;
		}
		this.incSize();
	}
	
	/**
	 * Dequeue a vertex from the back of a double-ended queue.
	 * @return vertex the vertex that will be removed from the back of the queue
	 */
	public Integer popBack() {
		if (this.isEmpty()) {
			System.err.println("Error: The Dequeue is empty");
			return null;
	    };
	    DoubleLinkedNode item = this._back;
	    Integer character = item.getCharacter();
	    if (this.length() == 1) {
	    	this._front = null;
	    	this._back = null;
	    } 
	    else {
	    	this._back = this._back.getPrevious();
	    	this._back.setNext(null);
	    };
	    this.decSize();
	    return character;
	}
	
	/**
	 * Dequeue a character from the front of a double-ended queue.
	 * @return character the character that will be removed from the front of the queue
	 */
	public Integer popFront() {
		if (this.isEmpty()) {
			System.err.println("Error: The Dequeue is empty");
			return null;
	    };
	    DoubleLinkedNode item = this._front;
	    Integer character = item.getCharacter();
	    if (this.length() == 1) {
	    	this._front = null;
	    	this._back = null;
	    }
	    else {
	    	this._front = this._front.getNext();
	    	this._front.setPrevious(null);
	    }
	    this.decSize();
	    return character;
	}
	
	/**
	 * Returns back character without removing it from the double-ended queue.
	 * @return character the character that is currently at the back of the queue
	 */
	public Integer peekBack() {
		if (this.isEmpty()) {
			return null;
	    }
		else {
			return this._back.getCharacter();
		}
	}
	
	/**
	 * Returns front character without removing it from the double-ended queue.
	 * @return character the character that is currently at the front of the queue
	 */
	public Integer peekFront() {
		if (this.isEmpty()) {
			return null;
	    }
		else {
			return this._front.getCharacter();
		}
	}
}