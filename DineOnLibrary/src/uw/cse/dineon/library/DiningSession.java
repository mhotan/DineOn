package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * DiningSession object that represents a the particular segment of time
 * where the initial user checks in to the restaurant and begins 
 * a dining session.
 * 
 * NOTE: In the future where users order food to go or pick up
 * A Dining session can also represent these Dining Sessions as well
 * 
 * Mikes: Suggestion.
 * Have the table IDs be coded where certain values represent not physically
 * checked in Dining Sessions.
 * 
 * Table Num of -1 = Delivery
 * Table Num of -2 = Pick up
 * etc...
 * 
 * @author zachr81, mhotan
 */
public class DiningSession extends TimeableStorable {

	// ID's used for easier parsing
	public static final String USERS = "users";
	public static final String ORDERS = "orders";
	public static final String TABLE_ID = "tableId";
	public static final String REQUESTS = "requests";
	public static final String RESTAURANT_INFO = "rest";
	
	
	// list of users involved in this session
	private final List<UserInfo> mUsers;	
	// list of orders made but not completed 
	private final List<Order> mOrders;
	// list of unresolved pending request
	private final List<CustomerRequest> mPendingRequests;
	
	private final RestaurantInfo mRest;

	// ID of table the dining is taking place at
	private int mTableID;	
	
	private double mSubTotal;

	/**
	 * Creates a dining session instance that is associated to a particular table.
	 * Takes current time as 
	 * @param tableID the id of the table
	 * @param uInfo to store
	 * @param rInfo RestaurantInfo of current restaurant
	 */
	public DiningSession(int tableID, UserInfo uInfo, RestaurantInfo rInfo) {
		this(tableID, null, uInfo, rInfo);
	}

	/**
	 * Creates a dining session that is associated with a particular
	 * start date.
	 * @param tableId Table ID to associate to
	 * @param startDate of session, if null sets it as the current date
	 * @param uInfo UserInfo to store
	 * @param rInfo RestaurantInfo of current restaurant
	 */
	public DiningSession(int tableId, Date startDate, UserInfo uInfo, RestaurantInfo rInfo) {
		super(DiningSession.class, startDate);
		resetTableID(tableId);
		mUsers = new ArrayList<UserInfo>();
		mUsers.add(uInfo);
		mOrders = new ArrayList<Order>();
		mPendingRequests = new ArrayList<CustomerRequest>();
		mRest = rInfo;
		mSubTotal = 0;
	}

	/**
	 * Creates a new DiningSession object with a particular table
	 * and ID (sessToken).
	 * 
	 * @param po ParseObject to 
	 * @throws ParseException 
	 */
	public DiningSession(ParseObject po) throws ParseException {
		super(po);
		mTableID = po.getInt(TABLE_ID);
		mUsers = ParseUtil.toListOfStorables(UserInfo.class, po.getList(USERS));
		mOrders = ParseUtil.toListOfStorables(Order.class, po.getList(ORDERS));
		mPendingRequests = ParseUtil.toListOfStorables(CustomerRequest.class, po.getList(REQUESTS));
		mRest = new RestaurantInfo(po.getParseObject(RESTAURANT_INFO));
		
		mSubTotal = 0;
		for (Order order : mOrders) {
			mSubTotal += order.getSubTotalCost();
		}
	}

	/**
	 * Packs this DiningSession into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(USERS, ParseUtil.toListOfParseObjects(mUsers));
		po.put(ORDERS, ParseUtil.toListOfParseObjects(mOrders));
		po.put(REQUESTS, ParseUtil.toListOfParseObjects(mPendingRequests));
		po.put(RESTAURANT_INFO, this.mRest.packObject());
		po.put(TABLE_ID, mTableID);
		return po;
	}

	/**
	 * Return if this dining session has orders.
	 * @return if dining session has orders.
	 */
	public boolean hasOrders() {
		return !mOrders.isEmpty();
	}
	
	/**
	 * Pending orders are defined as orders that have been placed
	 * but not received by the customer.
	 * @return A list of current pending orders
	 */
	public List<Order> getOrders() {
		return new ArrayList<Order>(mOrders);
	}

	/**
	 * @return the users of the dining session.
	 */
	public List<UserInfo> getUsers() {
		return new ArrayList<UserInfo>(mUsers);
	}
	
	/**
	 * @return the requests of the dining session.
	 */
	public List<CustomerRequest> getRequests() {
		return new ArrayList<CustomerRequest>(mPendingRequests);
	}

	/**
	 * Returns the start time of this dining session.
	 * @return the startTime
	 */
	public Date getStartTime() {
		return getOriginatingTime();
	}

	/**
	 * @return the tableID
	 */
	public int getTableID() {
		return mTableID;
	}
	
	/**
	 * @return The subtotal cost excluding tax.
	 */
	public double getSubTotal() {
		return mSubTotal;
	}
	
	/**
	 * @return Current cost of tax for all the orders of the session
	 */
	public double getTax() {
		return getSubTotal() * DineOnConstants.TAX;
	}
	
	/**
	 * @return Total cost of all the current orders in the dining
	 * 	session.
	 */
	public double getTotal() {
		return getSubTotal() + getTax();
	}
	
	/**
	 * @return restaurant
	 */
	public RestaurantInfo getRestaurantInfo() {
		return mRest;
	}
	
	/**
	 * Adds a order to be pending for this session.
	 * @param order to add.
	 */
	public void addPendingOrder(Order order) {
		mOrders.add(order);
		mSubTotal += order.getSubTotalCost();
	}

	/**
	 * Resets the current table ID to the inputed one.
	 * @param newId to set to 
	 */
	public void resetTableID(int newId) {
		if(newId < 0 || newId > 1000) {
			throw new IllegalArgumentException("Invalid tableId");
		}
		mTableID = newId;
	}

	/**
	 * @param userInfo to add to the session
	 */
	public void addUser(UserInfo userInfo) {
		this.mUsers.add(userInfo);
	}

	/**
	 * Removes this user from the current dining session if 
	 * he or she exists.
	 * @param userInfo Information of User to remove
	 */
	public void removeUser(UserInfo userInfo) {
		this.mUsers.remove(userInfo);
	}
	
	/**
	 * Returns if this dining session has this user info.
	 * @param user User to check for
	 * @return true if the user exists in this dining session, false other wise.
	 */
	public boolean hasUser(UserInfo user) {
		return mUsers.contains(user);
	}

	/**
	 * Adds the Customer Request to the this dining session.
	 * 
	 * @param request request to be added
	 */
	public void addRequest(CustomerRequest request) {
		mPendingRequests.add(request);
	}

	@Override
	public void deleteFromCloud() {

		// Delete all the request at this time
		for (CustomerRequest r: mPendingRequests) {
			r.deleteFromCloud();
		}

		// We keep the orders in the cloud for later analytics

		super.deleteFromCloud();
	}
}