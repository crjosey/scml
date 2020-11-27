/**
 * Abstract Bag
 * 
 * AbstractBag.java
 * CS201 | Assignment 3
 *
 * @author Ryan Josey
 */
public abstract class AbstractBag {
	private int _size;
	
	/**
	 * Create a AbstractBag. Size will be set to 0.
	 */
	public AbstractBag () {
		_size = 0;  
	};
	
	/**
	 * Get the size of a bag
	 * @return _size the size of a bag
	 */
	public int length () {
		return _size;
	}
	
	/**
	 * Check to see if a bag is empty
	 * @return true if size is bag is empty
	 * @return flase if size is bag is not empty
	 */
	public boolean isEmpty() { 
		return this.length() == 0; 
	}

	/**
	 * Increment the bag size by one
	 */
	protected void incSize () {
	    ++_size;
	}

	/**
	 * Decrement the bag size by one
	 */
	protected void decSize () {
	    --_size;
	}
}