package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static final String MENU_ITEMS_QUANTITIES = "menuItemQtys";

	private final int mTableID;		// ID for table the order is from
	private final UserInfo mUserInfo;			// Info of user who placed order
	
	/**
	 * Map from menu item to current order item.
	 */
	private final Map<MenuItem, Integer> mMenuItems;	// list of items in this order

	/**
	 * 
	 * @param tableID 
	 * @param originator User that has the order is originally derived from 
	 */
	public Order(int tableID, UserInfo originator) {
		this(tableID, originator, new ArrayList<MenuItem>());
	}
	
	/**
	 * Creates a new Order object from the given parameters.
	 * 
	 * @param tableID int ID of the table the order was place from
	 * @param originator Info of user placing order
	 * @param menuItems List of items in the order
	 */
	public Order(int tableID, UserInfo originator, List<MenuItem> menuItems) {
		super(Order.class);
		if(originator == null) {
			throw new IllegalArgumentException("Can't create order with null user.");
		}
		
		this.mTableID = tableID;
		this.mUserInfo = originator;
		this.mMenuItems = new HashMap<MenuItem, Integer>();
		for (MenuItem item : menuItems) {
			mMenuItems.put(item, 1);
		}
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
		List<MenuItem> items = ParseUtil.toListOfStorables(MenuItem.class, po.getList(MENU_ITEMS));
		List<Integer> quantities = po.getList(MENU_ITEMS_QUANTITIES);
		assert (items.size() == quantities.size());
		
		mMenuItems = new HashMap<MenuItem, Integer>();
		for (int i = 0; i < items.size(); ++i) {
			mMenuItems.put(items.get(i), quantities.get(i));
		}
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
		
		// Construct sequenced arrays
		List<MenuItem> items = new ArrayList<MenuItem>(mMenuItems.keySet());
		List<Integer> quantities = new ArrayList<Integer>(items.size());
		for (MenuItem item : items) {
			// For each over list is sequential so we are good
			quantities.add(mMenuItems.get(item));
		}
		
		po.put(MENU_ITEMS, ParseUtil.toListOfParseObjects(items));
		po.put(MENU_ITEMS_QUANTITIES, quantities);
		return po;
	}
	
	/**
	 * Sets the current qty of this menu item to the specified value
	 * determined by qty.  if qty is non positive then the item is removed.
	 * Other then that the quantity is set appropiately.
	 * @param item Item whose quantity will change
	 * @param qty Quantity to set to
	 */
	void setItemQuantity(MenuItem item, int qty) {
		if (qty <= 0) {
			// Remove from the order
			mMenuItems.remove(item);
			return;
		}
		
		// Just add like normal
		mMenuItems.put(item, qty);
	}
	
	/**
	 * @return Whether this order is currently empty.
	 */
	public boolean isEmpty() {
		return mMenuItems.isEmpty();
	}

	/**
	 * @return the tableID
	 */
	public int getTableID() {
		return mTableID;
	}

	/**
	 * @return the userID
	 */
	public UserInfo getOriginalUser() {
		return mUserInfo;
	}

	/**
	 * @return the menuItems
	 */
	public Map<MenuItem, Integer> getMenuItems() {
		return new HashMap<MenuItem, Integer>(mMenuItems);
	}

}