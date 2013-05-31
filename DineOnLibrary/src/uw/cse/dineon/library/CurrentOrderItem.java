package uw.cse.dineon.library;

import com.parse.ParseException;
import com.parse.ParseObject;

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
	 * Creates a new CurrentOrderItem in from the given Parcel.
	 * 
	 * @param po Parse Object to use to build CurrentOrderItem
	 * @throws ParseException 
	 */
	public CurrentOrderItem(ParseObject po) throws ParseException {
		super(po);
		this.mMenuItem = new MenuItem(po.getParseObject(MENUITEM));
		this.mQuantity = po.getInt(QUANTITY);
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
	
	/**
	 * Get the menu item tied to this object.
	 * @return menu item
	 */
	public MenuItem getMenuItem() {
		return this.mMenuItem;
	}
	
	/**
	 * Set the menu item for this order item.
	 * @param item item to set
	 */
	public void setMenuItem(MenuItem item) {
		this.mMenuItem = item;
	}
	
	/**
	 * Packs this CurrentOrderItem into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(MENUITEM, this.mMenuItem.packObject());
		po.put(QUANTITY, this.mQuantity);
		return po;
	}
	
//	/**
//	 * Writes this CurrentOrderItem to Parcel dest in the order:
//	 * MenuItem, int
//	 * to be retrieved at a later time.
//	 * 
//	 * @param dest Parcel to write CurrentOrderItem data to.
//	 * @param flags int
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		super.writeToParcel(dest, flags);
//		dest.writeParcelable(this.mMenuItem, flags);
//		dest.writeInt(this.mQuantity);
//	}
//	
//	/**
//	 * Creates an CurrentOrderItem from a Parcel.
//	 * @param source Source to create an CurrentOrderItem from
//	 */
//	public CurrentOrderItem(Parcel source) {
//		super(source);
//		this.mMenuItem = source.readParcelable(MenuItem.class.getClassLoader());
//		this.mQuantity = source.readInt();
//	}
//	
//	/**
//	 * Parcelable creator object of a CurrentOrderItem.
//	 * Can create a CurrentOrderItem from a Parcel.
//	 */
//	public static final Parcelable.Creator<CurrentOrderItem> CREATOR = 
//			new Parcelable.Creator<CurrentOrderItem>() {
//
//		@Override
//		public CurrentOrderItem createFromParcel(Parcel source) {
//			return new CurrentOrderItem(source);
//		}
//
//		@Override
//		public CurrentOrderItem[] newArray(int size) {
//			return new CurrentOrderItem[size];
//		}
//	};
}
