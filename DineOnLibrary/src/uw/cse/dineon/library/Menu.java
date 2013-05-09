package uw.cse.dineon.library;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import uw.cse.dineon.library.util.ParseUtil;

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
	public Menu(ParseObject po) {
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
		List<MenuItem> copy = new ArrayList<MenuItem>(mItems.size());
		Collections.copy(copy, mItems);
		return copy;
	}

	/**
	 * Add given item to the Menu.
	 * 
	 * @param item MenuItem
	 */
	public void addNewItem(MenuItem item) {
		mItems.add(item);
	}

	/**
	 * Remove given MenuItem from the menu.
	 * 
	 * @param item MenuItem
	 */
	public void removeItem(MenuItem item) {
		mItems.remove(item);
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
}

//	/**
//	 * Creates a new Menu from a given Parcel.
//	 * 
//	 * @param source Parcel of information in:
//	 * 		List<MenuItem> 
//	 * 		order.
//	 */
//	public Menu(Parcel source) {
//		super();
//		readFromParcel(source);
//	}	

//	/**
//	 * Unpacks the given ParseObject into this Menu setting
//	 * field values to the given data.
//	 * 
//	 * @param pobj ParseObject to be unpacked into a Menu
//	 */
//	@Override
//	public void unpackObject(ParseObject pobj) {
//		this.setObjId(pobj.getObjectId());
//		this.setName(pobj.getString(Menu.NAME));
//		
//		List<Storable> storable = ParseUtil.unpackListOfStorables(pobj.getParseObject(Menu.ITEMS));
//		List<MenuItem> items = new ArrayList<MenuItem>(storable.size());
//		for (Storable item : storable) {
//			items.add((MenuItem) item);
//		}
//		setItems(items);
//	}
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/**
//	 * Writes this Menu to Parcel dest in the order:
//	 * String, List<MenuItem>
//	 * to be retrieved at a later time.
//	 * 
//	 * @param dest Parcel to write Menu data to.
//	 * @param flags int
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		// dest.writeInt(productID);
//		dest.writeString(mName);
//		dest.writeTypedList(mItems);
//	}
//	
//	/**
//	 * Helper method for updating Menu with the data from a Parcel.
//	 * @param source Parcel containing data in the order:
//	 * 		String, List<MenuItem>
//	 */
//	private void readFromParcel(Parcel source) {
//		this.mName = source.readString();
//		source.readTypedList(mItems, MenuItem.CREATOR); // default class load used
//	}
//	
//	/**
//	 * Parcelable creator object of a Menu.
//	 * Can create a Menu from a Parcel.
//	 */
//	public static final Parcelable.Creator<Menu> CREATOR = 
//			new Parcelable.Creator<Menu>() {
//
//				@Override
//				public Menu createFromParcel(Parcel source) {
//					return new Menu(source);
//				}
//
//				@Override
//				public Menu[] newArray(int size) {
//					return new Menu[size];
//				}
//			};
//}
