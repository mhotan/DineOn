package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;


/**
 * Menu object representing a restaurant menu containing various items.
 * 
 * @author zachr81, mhotan
 */
public class Menu extends Storable {

	// ID used for easier parsing
	public static final String ITEMS = "menuItems";
	public static final String NAME = "menuName";

	
	
	/**
	 * Name of the menu.
	 * IE "Dinner Menu", "Breakfast Menu", "Drinks"
	 */
	private String mName;

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
	 * @param name The new name for the menu
	 */
	public void setName(String name) {
		this.mName = name;
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
		return mItems.contains(nItem);
	}

	/**
	 * Add given item to the Menu.
	 * Doesn't add the menu item if it is already in the list.
	 * @param item MenuItem to add
	 * @return true if item was added, false if item already exists
	 */
	public boolean addNewItem(MenuItem item) {
		// If we are able to remove a menu item
		if (mItems.contains(item)) {
			return false;
		}
		return mItems.add(item);
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
		po.addAllUnique(Menu.ITEMS, ParseUtil.toListOfParseObjects(this.mItems));
		// in case this storable is going to be used after the pack.
		return po;
	}

	/**
	 * Return the menu name for string.
	 * @return String name of the menu
	 */
	@Override
	public String toString() {
		return mName;
	}
	
	
	
	@Override
	public void deleteFromCloud() {
		for (MenuItem item: mItems) {
			item.deleteFromCloud();
		}
		super.deleteFromCloud();
	}
}