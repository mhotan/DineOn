package uw.cse.dineon.library;

/**
 * Class that encapsulates a users order thats dynamically built
 * 
 * @author mtrathjen08
 *
 */
public class CurrentOrderItem extends Storable {
	
	private static final String MENUITEM = "menuItem";
	private static final String QUANTITY = "quantity";
	
	/**
	 * The menu item the user is placing in order.
	 */
	private MenuItem mMenuItem;
	/**
	 * The quantity of the menu item.
	 */
	private int mQuantity;
	
	public CurrentOrderItem(MenuItem menuItem) {
		super(CurrentOrderItem.class);
		this.mMenuItem = menuItem;
		this.mQuantity = 1;
	}
	
	/**
	 * The the current quantity of the item.
	 * @return quantity
	 */
	public int getQuantity() {
		return this.mQuantity;
	}
	
	/**
	 * Set the value of the quantity
	 * @param quantity
	 */
	public void setQuantity(int quantity) {
		this.mQuantity = quantity;
	}
	
	/**
	 * Increment the quantity
	 */
	public void incrementQuantity() {
		this.mQuantity++;
	}
	
	/**
	 * Decrement the quantity
	 */
	public void decrementQuantity() {
		this.mQuantity--;
	}
}
