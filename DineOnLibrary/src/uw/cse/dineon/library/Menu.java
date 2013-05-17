package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;


/**
 * Menu object representing a restaurant menu containing various items.
 * 
 * @author zachr81, mhotan
 */
public class Menu extends Storable {

	// ID used for easier parsing
	public static final String ITEMS = "items";
	public static final String NAME = "name";

	/**
	 * Name of the menu.
	 * IE "Dinner Menu", "Breakfast Menu", "Drinks"
	 */
	private final String mName;

	/**
	 * This is all the items that it contains.
	 */
	private final List<MenuItem> mItems;	// list of items on the menu

	/**
	 * Creates a new Menu object containing MenuItems.
	 * 
	 * @param name of menu
	 */
	public Menu(String name) {
		super(Menu.class);
		this.mItems = new ArrayList<MenuItem>();
		this.mName = name;
	}

	/**
	 * Generates a Menu from a ParseObject that was orginally created by a menu.
	 * @param po parse object to extract menu
	 * @throws ParseException 
	 */
	public Menu(ParseObject po) throws ParseException {
		super(po);
		this.mName = po.getString(NAME);
		this.mItems = ParseUtil.toListOfStorables(MenuItem.class, po.getList(ITEMS));

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the items
	 */
	public List<MenuItem> getItems() {
		return new ArrayList<MenuItem>(mItems);
	}

	/**
	 * If this menu has any item of the same product ID.
	 * @param nItem menu item to add.
	 * @return true if an item with the same product ID exists
	 */
	public boolean hasMenuItem(MenuItem nItem) {
		for (MenuItem item: mItems) {
			if (item.getProductID() == nItem.getProductID()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add given item to the Menu.
	 * Replaces menu item with the same product ID if it already exists.
	 * 
	 * @param item MenuItem to add
	 * @return true if item was replaced, false otherwise
	 */
	public boolean addNewItem(MenuItem item) {
		MenuItem toRemove = null;
		// Find item with the same ID
		for (MenuItem i : mItems) {
			if (i.getProductID() == item.getProductID()) {
				toRemove = i;
				break;
			}
		}
		// Remove any old
		removeItem(toRemove);

		mItems.add(item);
		return toRemove != null;
	}

	/**
	 * Remove given MenuItem from the menu.
	 * 
	 * @param item MenuItem
	 * @return True if menu item was remvoed
	 */
	public boolean removeItem(MenuItem item) {
		if (item == null) {
			return false;
		}
		return mItems.remove(item);

		// TODO delete from database
	}

	/**
	 * Packs this Menu into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(Menu.NAME, this.getName());
		po.put(Menu.ITEMS, ParseUtil.toListOfParseObjects(this.mItems));
		// in case this storable is going to be used after the pack.
		return po;
	}


	/**
	 * Creates a new Menu from a given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		List<MenuItem> 
	 * 		order.
	 */
	protected Menu(Parcel source) {
		super(source);
		this.mName = source.readString();
		mItems = new ArrayList<MenuItem>();
		source.readTypedList(mItems, MenuItem.CREATOR); // default class load used
	}	


	/**
	 * Writes this Menu to Parcel dest in the order:
	 * String, List<MenuItem>
	 * to be retrieved at a later time.
	 * 
	 * @param dest Parcel to write Menu data to.
	 * @param flags int
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(mName);
		dest.writeTypedList(mItems);
	}
	
	/**
	 * Parcelable creator object of a Menu.
	 * Can create a Menu from a Parcel.
	 */
	public static final Parcelable.Creator<Menu> CREATOR = 
			new Parcelable.Creator<Menu>() {

		@Override
		public Menu createFromParcel(Parcel source) {
			return new Menu(source);
		}

		@Override
		public Menu[] newArray(int size) {
			return new Menu[size];
		}
	};
}