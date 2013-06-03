package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

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
	private final List<CurrentOrderItem> mMenuItems;	// list of items in this order

	/**
	 * Creates a new Order object from the given parameters.
	 * 
	 * @param tableID int ID of the table the order was place from
	 * @param originator Info of user placing order
	 * @param menuItems List of items in the order
	 */
	public Order(int tableID, UserInfo originator, List<CurrentOrderItem> menuItems) {
		super(Order.class);
		if(originator == null) {
			throw new IllegalArgumentException("Can't create order with null user.");
		}
		
		this.mTableID = tableID;
		this.mUserInfo = originator;
		this.mMenuItems = new ArrayList<CurrentOrderItem>(menuItems);
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
		if(mUserInfo == null) {
			throw new IllegalArgumentException("Can't create order with null user.");	
		}
		
		mMenuItems = ParseUtil.toListOfStorables(CurrentOrderItem.class, po.getList(MENU_ITEMS));

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
	public List<CurrentOrderItem> getMenuItems() {
		return new ArrayList<CurrentOrderItem>(mMenuItems);
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

//	/**
//	 * Writes this Order to Parcel dest in the order:
//	 * int, int, int, int, List<MenuItem>
//	 * to be retrieved at a later time.
//	 * 
//	 * @param dest Parcel to write Order data to.
//	 * @param flags int
//	 */
//	// NOTE: if you change the write order you must change the read order
//	// below.
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		super.writeToParcel(dest, flags);
//		dest.writeInt(mTableID);
//		dest.writeParcelable(mUserInfo, flags);
//		dest.writeTypedList(mMenuItems);
//	}
//	
//	/**
//	 * Creates an Order from a Parcel.
//	 * @param source Source to create an order from
//	 */
//	public Order(Parcel source) {
//		super(source);
//		mTableID = source.readInt();
//		mUserInfo = source.readParcelable(UserInfo.class.getClassLoader());
//		mMenuItems = new ArrayList<CurrentOrderItem>();
//		source.readTypedList(mMenuItems, CurrentOrderItem.CREATOR);
//	}
//
//	/**
//	 * Parcelable creator object of a Order.
//	 * Can create a Order from a Parcel.
//	 */
//	public static final Parcelable.Creator<Order> CREATOR = 
//			new Parcelable.Creator<Order>() {
//
//		@Override
//		public Order createFromParcel(Parcel source) {
//			return new Order(source);
//		}
//
//		@Override
//		public Order[] newArray(int size) {
//			return new Order[size];
//		}
//	};
}