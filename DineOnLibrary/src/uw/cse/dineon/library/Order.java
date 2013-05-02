package uw.cse.dineon.library;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * 
 * @author zachr81
 *
 */
public class Order extends Storable implements Parcelable {

	public static final String TABLE_ID = "tableID";
	public static final String USER_ID = "userID";
	public static final String REST_ID = "restID";
	public static final String TIME_STAMP = "timestamp";
	public static final String MENU_ITEMS = "menuItems";
	
	private int tableID;
	private int userID;
	private int restID;
	private int timestamp;
	private List<MenuItem> menuItems;
	
	/**
	 * 
	 * @param tableID
	 * @param userID
	 * @param restID
	 * @param timestamp
	 * @param menuItems
	 */
	public Order(int tableID, int userID, int restID, int timestamp, List<MenuItem> menuItems) {
		super();
		this.tableID = tableID;
		this.userID = userID;
		this.restID = restID;
		this.timestamp = timestamp;
		this.menuItems = menuItems;
	}
	
	public Order(Parcel source) {
		readFromParcel(source);
	}

	/**
	 * @return the tableID
	 */
	public int getTableID() {
		return tableID;
	}
	
	/**
	 * @param tableID the tableID to set
	 */
	public void setTableID(int tableID) {
		this.tableID = tableID;
	}
	
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @return the restID
	 */
	public int getRestID() {
		return restID;
	}

	/**
	 * @param restID the restID to set
	 */
	public void setRestID(int restID) {
		this.restID = restID;
	}

	/**
	 * @return the timestamp
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the menuItems
	 */
	public List<MenuItem> getMenuItems() {
		List<MenuItem> copy = new ArrayList<MenuItem>(menuItems.size());
		Collections.copy(copy, menuItems);
		return copy;
	}

	/**
	 * @param menuItems the menuItems to set
	 */
	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	@SuppressWarnings("static-access")
	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(this.TABLE_ID, this.tableID);
		pobj.add(this.USER_ID, this.userID);
		pobj.add(this.REST_ID, this.restID);
		pobj.add(this.TIME_STAMP, this.timestamp);
		pobj.add(this.MENU_ITEMS, this.menuItems);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
		
		return pobj;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setTableID(pobj.getInt(this.TABLE_ID));
		this.setUserID(pobj.getInt(this.USER_ID));
		this.setRestID(pobj.getInt(this.REST_ID));
		this.setTimestamp(pobj.getInt(this.TIME_STAMP));
		this.menuItems.addAll((List<MenuItem>) pobj.get(this.MENU_ITEMS));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	
	/**
	 * Writes this Order to Parcel dest in the order:
	 * int, int, int, int, List<MenuItem>
	 * to be retrieved at a later time.
	 * 
	 * @param dest Parcel to write Order data to.
	 * @param flags int
	 */
	// NOTE: if you change the write order you must change the read order
	// below.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(tableID);
		dest.writeInt(userID);
		dest.writeInt(restID);
		dest.writeInt(timestamp);
		dest.writeTypedList(menuItems);
	}
	
	/**
	 * Helper method for updating Order with the data from a Parcel.
	 * @param source Parcel containing data in the order:
	 * 		List<User>, long, long, (boolean stored as an) int, List<Order>, int, int
	 */
	private void readFromParcel(Parcel source) {
		tableID = source.readInt();
		userID = source.readInt();
		restID = source.readInt();
		timestamp = source.readInt();
		source.readTypedList(menuItems, MenuItem.CREATOR);
	}
	
	/**
	 * Parcelable creator object of a Order.
	 * Can create a Order from a Parcel.
	 */
	public static final Parcelable.Creator<Order> CREATOR = 
			new Parcelable.Creator<Order>() {

				@Override
				public Order createFromParcel(Parcel source) {
					return new Order(source);
				}

				@Override
				public Order[] newArray(int size) {
					return new Order[size];
				}
			};
}
