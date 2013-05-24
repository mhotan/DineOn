package uw.cse.dineon.library;

/**
 * Class that encapsulates a users order thats dynamically built.
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
	
	/**
	 * @param menuItem MenuItem to add to current order
	 */
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
	 * Set the value of the quantity.
	 * @param quantity int
	 */
	public void setQuantity(int quantity) {
		if (quantity >= 0) {
			this.mQuantity = quantity;
		}
	}
	
	/**
	 * Increment the quantity.
	 */
	public void incrementQuantity() {
		this.mQuantity++;
	}
	
	/**
	 * Decrement the quantity.
	 */
	public void decrementQuantity() {
		if (this.mQuantity > 0) {
			this.mQuantity--;
		}
	}
}
