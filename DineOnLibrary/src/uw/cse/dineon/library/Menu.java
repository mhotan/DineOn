package uw.cse.dineon.library;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;


/**
 * Menu object representing a restaurant menu containing various items.
 * 
 * @author zachr81
 *
 */
public class Menu extends Storable implements Parcelable {

	// ID used for easier parsing
	public static final String ITEMS = "items";
	
	private List<MenuItem> items;	// list of items on the menu

	/**
	 * Creates a new Menu object containing MenuItems.
	 * 
	 * @param items list of MenuItems that populate a Menu.
	 */
	public Menu(List<MenuItem> items) {
		super();
		this.items = items;
		
	}
	
	/**
	 * Creates a new Menu from a given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		List<MenuItem> 
	 * 		order.
	 */
	public Menu(Parcel source) {
		super();
		readFromParcel(source);
	}

	/**
	 * @return the items
	 */
	public List<MenuItem> getItems() {
		List<MenuItem> copy = new ArrayList<MenuItem>(items.size());
		Collections.copy(copy, items);
		return copy;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<MenuItem> items) {
		this.items = items;
	}
	
	/**
	 * Add given item to the Menu.
	 * 
	 * @param item MenuItem
	 */
	public void addNewItem(MenuItem item) {
		items.add(item);
	}
	
	/**
	 * Remove given MenuItem from the menu.
	 * 
	 * @param item MenuItem
	 */
	public void removeItem(MenuItem item) {
		items.remove(item);
	}

	/**
	 * Packs this Menu into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@SuppressWarnings("static-access")
	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(this.ITEMS, this.items);
		// in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
		
		return pobj;
	}

	/**
	 * Unpacks the given ParseObject into this Menu setting
	 * field values to the given data.
	 * 
	 * @param pobj ParseObject to be unpacked into a Menu
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.items.addAll((List<MenuItem>) pobj.get(this.ITEMS));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Writes this Menu to Parcel dest in the order:
	 * List<MenuItem>
	 * to be retrieved at a later time.
	 * 
	 * @param dest Parcel to write Menu data to.
	 * @param flags int
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// dest.writeInt(productID);
		dest.writeTypedList(items);
	}
	
	/**
	 * Helper method for updating Menu with the data from a Parcel.
	 * @param source Parcel containing data in the order:
	 * 		List<MenuItem>
	 */
	private void readFromParcel(Parcel source) {
		source.readTypedList(items, MenuItem.CREATOR); // default class load used
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
