package uw.cse.dineon.library;

import java.util.List;
import java.util.ArrayList;

import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Order object representing an order placed by a client at a restaurant.
 * 
 * @author zachr81, mhotan
 */
public class Order extends TimeableStorable {

	// ID's used for easier parsing
	public static final String TABLE_ID = "tableID";
	public static final String USER_INFO = "userInfo";
	public static final String MENU_ITEMS = "menuItems";

	private final int mTableID;		// ID for table the order is from
	private final UserInfo mUserInfo;			// Info of user who placed order
	private final List<MenuItem> mMenuItems;	// list of items in this order

	/**
	 * Creates a new Order object from the given parameters.
	 * 
	 * @param tableID int ID of the table the order was place from
	 * @param originator Info of user placing order
	 * @param menuItems List of items in the order
	 */
	public Order(int tableID, UserInfo originator, List<MenuItem> menuItems) {
		super(Order.class);
		this.mTableID = tableID;
		this.mUserInfo = originator;
		this.mMenuItems = new ArrayList<MenuItem>(menuItems);
	}

	/**
	 * Creates a new Order in from the given Parcel.
	 * 
	 * @param po Parse Object to use to build orders
	 * @throws ParseException 
	 */
	public Order(ParseObject po) throws ParseException {
		super(po);
		mTableID = po.getInt(TABLE_ID);
		mUserInfo = new UserInfo(po.getParseObject(USER_INFO));
		mMenuItems = ParseUtil.toListOfStorables(MenuItem.class, po.getList(MENU_ITEMS));
		
	}

	/**
	 * @return the tableID
	 */
	public int getTableID() {
		return mTableID;
	}

	//	/**
	//	 * @param tableID the tableID to set
	//	 */
	//	public void setTableID(int tableID) {
	//		this.mTableID = tableID;
	//	}

	/**
	 * @return the userID
	 */
	public UserInfo getOriginalUser() {
		return mUserInfo;
	}

	/**
	 * @return the menuItems
	 */
	public List<MenuItem> getMenuItems() {
		return new ArrayList<MenuItem>(mMenuItems);
	}

	/**
	 * Packs this Order into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(TABLE_ID, mTableID);
		po.put(USER_INFO, mUserInfo.packObject());
		po.put(MENU_ITEMS, ParseUtil.toListOfParseObjects(mMenuItems));
		return po;
	}
}


///**
//* Unpacks the given ParseObject into this Order setting
//* field values to the given data.
//* 
//* @param pobj ParseObject to be unpacked into an Order
//*/
//@Override
//public void unpackObject(ParseObject pobj) {
//	this.setObjId(pobj.getObjectId());
//	this.setTableID(pobj.getInt(Order.TABLE_ID));
//	this.setUserID(pobj.getInt(Order.USER_ID));
//	this.setRestID(pobj.getInt(Order.REST_ID));
//	this.setTimestamp(pobj.getInt(Order.TIME_STAMP));
//	
//	List<Storable> storable = ParseUtil.unpackListOfStorables(pobj.getParseObject(Menu.ITEMS));
//	List<MenuItem> items = new ArrayList<MenuItem>(storable.size());
//	for (Storable item : storable) {
//		items.add((MenuItem) item);
//	}
//	setMenuItems(items);
//}
//
//@Override
//public int describeContents() {
//	return 0;
//}
//
//
///**
//* Writes this Order to Parcel dest in the order:
//* int, int, int, int, List<MenuItem>
//* to be retrieved at a later time.
//* 
//* @param dest Parcel to write Order data to.
//* @param flags int
//*/
//// NOTE: if you change the write order you must change the read order
//// below.
//@Override
//public void writeToParcel(Parcel dest, int flags) {
//	dest.writeInt(mTableID);
//	dest.writeInt(mUserInfo);
//	dest.writeInt(restID);
//	dest.writeInt(timestamp);
//	dest.writeTypedList(mMenuItems);
//}
//
///**
//* Helper method for updating Order with the data from a Parcel.
//* @param source Parcel containing data in the order:
//* 		List<User>, long, long, (boolean stored as an) int, List<Order>, int, int
//*/
//private void readFromParcel(Parcel source) {
//	mTableID = source.readInt();
//	mUserInfo = source.readInt();
//	restID = source.readInt();
//	timestamp = source.readInt();
//	source.readTypedList(mMenuItems, MenuItem.CREATOR);
//}
//
///**
//* Parcelable creator object of a Order.
//* Can create a Order from a Parcel.
//*/
//public static final Parcelable.Creator<Order> CREATOR = 
//		new Parcelable.Creator<Order>() {
//
//			@Override
//			public Order createFromParcel(Parcel source) {
//				return new Order(source);
//			}
//
//			@Override
//			public Order[] newArray(int size) {
//				return new Order[size];
//			}
//		};