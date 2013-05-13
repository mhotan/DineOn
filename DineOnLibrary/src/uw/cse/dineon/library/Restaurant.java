package uw.cse.dineon.library;


import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * This class represents a Restaurant.  Where internally 
 * the class can tracks its profile representation and List of current 
 * and old restaurant transactions.
 * @author zachr81, mhotan
 */
public class Restaurant extends LocatableStorable {

	/*
	 * Abstract Representation:
	 * 
	 * 
	 */
	
	// Parse used Keys
	public static final String RESERVATION_LIST = "reservationList";
	public static final String INFO = "restaurantInfo";
	public static final String PAST_ORDERS = "pastOrders";
	public static final String PENDING_ORDERS = "pendingOrders";
	public static final String PAST_USERS = "pastUsers";
	public static final String SESSIONS = "restaurantDiningSessions";
	public static final String CUSTOMER_REQUESTS = "customerRequests";

	/**
	 * Restaurant information.
	 */
	private final RestaurantInfo mRestInfo;
	
	/**
	 * Past Orders placed at this restaurant.
	 * Also includes any recently placed orders.
	 */
	private final List<Order> mPastOrders;
	
	/**
	 * Past Orders placed at this restaurant.
	 * Also includes any recently placed orders.
	 */
	private final List<Order> mPendingOrders;
	
	/**
	 * Past users that have visited the restaurant.
	 */
	private final List<UserInfo> mPastUsers;
	
	/**
	 * Currently pending reservations.
	 */
	private final List<Reservation> mReservations;
	
	/**
	 *  Currently Active Dining sessions.
	 */
	private final List<DiningSession> mSessions;
	
	/**
	 * Customer request that is not associated with the restaurant.
	 */
	private final List<CustomerRequest> mCustomerRequests;

	/**
	 * Create a bare bones Restaurant with just a name.
	 * TODO Add basic constraints to creating a restaurant
	 * @param user of Restaurant
	 * @throws ParseException 
	 */
	public Restaurant(ParseUser user) throws ParseException {
		super(Restaurant.class);
		mRestInfo = new RestaurantInfo(user);
		
		mPastOrders = new ArrayList<Order>();
		mPastUsers = new ArrayList<UserInfo>();
		
		mPendingOrders = new ArrayList<Order>();
		mReservations = new ArrayList<Reservation>();
		mSessions = new ArrayList<DiningSession>();
		mCustomerRequests = new ArrayList<CustomerRequest>();
	}

	/**
	 * Creates a Restaurant object from the given ParseObject.
	 * 
	 * @param po Parse object to build from
	 * @throws ParseException 
	 */
	public Restaurant(ParseObject po) throws ParseException {
		super(po);
		mRestInfo = new RestaurantInfo(po.getParseObject(INFO));
		
		mPastOrders = ParseUtil.toListOfStorables(Order.class, po.getList(PAST_ORDERS));
		mPastUsers = ParseUtil.toListOfStorables(UserInfo.class, po.getList(PAST_USERS));
		
		mPendingOrders = ParseUtil.toListOfStorables(
				Order.class, po.getList(PENDING_ORDERS)); 
		mReservations = ParseUtil.toListOfStorables(
				Reservation.class, po.getList(RESERVATION_LIST)); 
		mSessions = ParseUtil.toListOfStorables(DiningSession.class, po.getList(SESSIONS));
		mCustomerRequests = ParseUtil.toListOfStorables(
				CustomerRequest.class, po.getList(CUSTOMER_REQUESTS));
	}

	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(INFO, mRestInfo.packObject());
		
		// Pack up old stuff
		po.put(PAST_ORDERS, ParseUtil.toListOfParseObjects(mPastOrders));
		po.put(PAST_USERS, ParseUtil.toListOfParseObjects(mPastUsers));
		
		// Pack current stuff
		po.put(PENDING_ORDERS, ParseUtil.toListOfParseObjects(mPendingOrders));
		po.put(RESERVATION_LIST, ParseUtil.toListOfParseObjects(mReservations));
		po.put(SESSIONS, ParseUtil.toListOfParseObjects(mSessions));
		po.put(CUSTOMER_REQUESTS, ParseUtil.toListOfParseObjects(mCustomerRequests));	
		return po;
	}

	/////////////////////////////////////////////////////
	////  Setter methods
	/////////////////////////////////////////////////////

	/**
	 * Returns the name of this restaurant.
	 * @return Name
	 */
	public String getName() {
		return mRestInfo.getName();
	}

	/**
	 * Returns reference to Restaurant Info Object.
	 * NOTE: Permissions to change Information is not protected 
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getInfo() {
		return mRestInfo;
	}

	/**
	 * Returns a list copy of Customer Requests.
	 * @return the customerRequests that this restaurant is aware of
	 */
	public List<CustomerRequest> getCustomerRequests() {
		return new ArrayList<CustomerRequest>(mCustomerRequests);
	}

	/**
	 * Returns a list of all current reservations that the Restaurant has tracked.
	 * 
	 * @return List<Reservation>
	 */
	public List<Reservation> getReservationList() {
		return new ArrayList<Reservation>(mReservations);
	}

	/**
	 * Returns the current list of past orders placed at the restaurant.
	 * @return List<Order> of past orders placed at the restaurant.
	 */
	public List<Order> getPastOrders() {
		return new ArrayList<Order>(mPastOrders);
	}

	/**
	 * List of current running dining sessions.
	 * @return List<DiningSession>
	 */
	public List<DiningSession> getSessions() {
		return new ArrayList<DiningSession>(mSessions);
	}
	
	/**
	 * Each one of the restaurants have pending dining sessions.
	 * Returns all the orders of all the pending dining sessions.
	 * @return a list of all pending orders 
	 */
	public List<Order> getPendingOrders() {
		return new ArrayList<Order>(mPendingOrders);
	}

	/////////////////////////////////////////////////////
	////  Setter methods
	/////////////////////////////////////////////////////

	/**
	 * Add a new customer request.
	 * @param newReq request to add
	 */
	public void addCustomerRequest(CustomerRequest newReq) {
		mCustomerRequests.add(newReq);
	}
	
	/**
	 * Adds the given reservation to the reservation list.
	 * @param newReservation to add
	 */
	public void addReservation(Reservation newReservation) {
		mReservations.add(newReservation);
	}
	
	/**
	 * Marks this Order as complete. 
	 * @param order Order that has been completed.
	 */
	public void completeOrder(Order order) {
		if (order == null) {
			return;
		}
		
		// Move from list of pending orders to
		// list of past orders
		if (mPendingOrders.remove(order)) {
			mPastOrders.add(order);
		}
	}
	
	/**
	 * Adds a pending order to a restaurant.
	 * @param order Order to be added. cannot be null.
	 */
	public void addOrder(Order order) {
		if (order == null) {
			throw new NullPointerException("Order being added to restaurant is null");
		}
		mPendingOrders.add(order);
	}
	
	/**
	 * Adds given DiningSession to sessions.
	 * @param session to add
	 */
	public void addDiningSession(DiningSession session) {
		if (session == null) { // Invalid input
			return;
		}
		if (mSessions.contains(session)) { // Already tracking this session
			return;
		}
		
		mSessions.add(session);
	}

	/**
	 * Deletes this dining session from the cloud and the restaurant.
	 * @param session 
	 */
	public void removeDiningSession(DiningSession session) {
		if (session == null) {
			return;
		}
		
		// If we found the session.
		if (mSessions.remove(session)) {
			mPastUsers.addAll(session.getUsers());
			session.deleteFromCloud();
		}
	}
	
	/**
	 * Remove the specified CustomerRequest.
	 * @param oldReq request to remove
	 */
	public void removeCustomerRequest(CustomerRequest oldReq) {
		mCustomerRequests.remove(oldReq);
		// TODO Delete the customer request if it existed
	}

	/**
	 * Remove the specified reservation.
	 * @param removeReservation from restaurant
	 */
	public void removeReservation(Reservation removeReservation) {
		mReservations.remove(removeReservation);
		// TODO Delete the reservation from parse
	}

	/**
	 * Clears all the past orders from this restaurant.
	 */
	public void clearPastOrders() {
		// TODO For each of the Orders delete from parse
		mPastOrders.clear();
	}
	
	/**
	 * Updates the current user if it exists in the restaurant.
	 * @param user User to update
	 */
	public void updateUser(UserInfo user) {
		if (user == null) {
			return;
		}
		// NOTE (MH) Because we have to replace the old version
		// of this user with a current version.
		// We have to check for object ID equality to find the object to replace 
		
		// Find the UserInfo to remove in past users
		UserInfo toRemove = null;
		for (UserInfo info : mPastUsers) {
			if (info.getObjId().equals(user.getObjId())) {
				toRemove = info;
			}
		}
		// Effectively replaces.
		if (toRemove != null) {
			mPastUsers.remove(toRemove);
			mPastUsers.add(user);
		}
		
		// Find the user if they exist in the past .
		for (DiningSession session: mSessions) {
			toRemove = null;
			for (UserInfo sessionUser: session.getUsers()) {
				
				// Find the user to remove.
				if (sessionUser.getObjId().equals(user.getObjId())) {
					toRemove = sessionUser;
					break;
				}
				
				// Remove and replace new user
				if (toRemove != null) {
					session.removeUser(toRemove);
					session.addUser(user);
				}
			}
			
		}
		
		
		
	}

	/**
	 * Permanently deletes the dining session of this.
	 * @param session to remove
	 */
	public void delete(DiningSession session) {
		mSessions.remove(session);
		session.deleteFromCloud();
	}
	

	//	@Override
	//	public void unpackObject(ParseObject pobj) {
	//		this.setObjId(pobj.getObjectId());
	//		
	//		List<Storable> storable = 
	//				ParseUtil.unpackListOfStorables(pobj.getParseObject(Restaurant.MENUS));
	//		List<Menu> menus = new ArrayList<Menu>(storable.size());
	//		for (Storable menu : storable) {
	//			menus.add((Menu) menu);
	//		}
	//		setMenus(menus);
	//		
	//		RestaurantInfo info = new RestaurantInfo();
	//		info.unpackObject((ParseObject) pobj.get(Restaurant.INFO));
	//		this.setInfo(info);
	//		
	//		storable = 
	//			ParseUtil.unpackListOfStorables(pobj.getParseObject(Restaurant.RESERVATION_LIST));
	//		List<Reservation> reserves = new ArrayList<Reservation>(storable.size());
	//		for (Storable res : storable) {
	//			reserves.add((Reservation) res);
	//		}
	//		setReservationList(reserves);
	//		
	//		storable = ParseUtil.unpackListOfStorables(pobj.getParseObject(Restaurant.ORDERS));
	//		for (Storable order : storable) {
	//			addOrder((Order) order);
	//		}
	//		
	//		storable = 
	//				ParseUtil.unpackListOfStorables(pobj.getParseObject(Restaurant.SESSIONS));
	//		List<DiningSession> sessions = new ArrayList<DiningSession>(storable.size());
	//		for (Storable sess : storable) {
	//			sessions.add((DiningSession) sess);
	//		}
	//		setSessions(sessions);
	//		
	//		storable = 
	//			ParseUtil.unpackListOfStorables(pobj.getParseObject(Restaurant.CUSTOMER_REQUESTS));
	//		List<CustomerRequest> requests = new ArrayList<CustomerRequest>(storable.size());
	//		for (Storable request : storable) {
	//			requests.add((CustomerRequest) request);
	//		}
	//		setCustomerRequests(requests);
	//	}
	//
	//	/**
	//	 * A Parcel method to describe the contents of the object.
	//	 * @return an int describing contents
	//	 */
	//	@Override
	//	public int describeContents() {
	//		return 0;
	//	}
	//
	//	/**
	//	 * Write the object to a parcel object.
	//	 * @param dest the Parcel to write to
	//	 * @param flags to set
	//	 */
	//	@Override
	//	public void writeToParcel(Parcel dest, int flags) {
	//		dest.writeTypedList(menus);
	//		dest.writeTypedList(mReservations);
	//		dest.writeParcelable(mRestInfo, flags);
	//		dest.writeTypedList(mOrders);
	//		dest.writeTypedList(mSessions);
	//		dest.writeTypedList(mCustomerRequests);
	//		dest.writeString(this.getObjId());		
	//	}
	//	
	//	/**
	//	 * Parcelable creator object of a Restaurant.
	//	 * Can create a Restaurant from a Parcel.
	//	 */
	//	public static final Parcelable.Creator<Restaurant> CREATOR = 
	//			new Parcelable.Creator<Restaurant>() {
	//
	//				@Override
	//				public Restaurant createFromParcel(Parcel source) {
	//					return new Restaurant(source);
	//				}
	//
	//				@Override
	//				public Restaurant[] newArray(int size) {
	//					return new Restaurant[size];
	//				}
	//	};
	//			
	//	/**
	//	 * Helper method for updating Restaurant with the data from a Parcel.
	//	 * @param source Parcel containing data in the order:
	//	 * 		Menu, Reservation, RestaurantInfo, List<Order>, List<DiningSession, 
	//	 * 		List<CustomerRequests>, String
	//	 */
	//	protected void readFromParcel(Parcel source) {
	//		source.readTypedList(menus, Menu.CREATOR);
	//		source.readTypedList(mReservations, Reservation.CREATOR);
	//		this.setInfo((RestaurantInfo)source.readParcelable(
	//				RestaurantInfo.class.getClassLoader()));
	//		source.readTypedList(mOrders, Order.CREATOR);
	//		source.readTypedList(mSessions, DiningSession.CREATOR);
	//		source.readTypedList(mCustomerRequests, CustomerRequest.CREATOR);
	//		this.setObjId(source.readString());
	//	}
}
