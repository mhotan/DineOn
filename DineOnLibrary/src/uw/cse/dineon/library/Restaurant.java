package uw.cse.dineon.library;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * This class represents a Restaurant.  Where internally 
 * the class can tracks its profile representation and List of current 
 * and old restaurant transactions.
 * @author zachr81, mhotan
 */
public class Restaurant extends LocatableStorable {

	// Parse used Keys
	public static final String RESERVATION_LIST = "reservationList";
	public static final String INFO = "restaurantInfo";
	public static final String PAST_ORDERS = "pastOrders";
	public static final String PAST_USERS = "pastUsers";
	public static final String SESSIONS = "restaurantDiningSessions";
	public static final String CUSTOMER_REQUESTS = "customerRequests";

	/**
	 * Restaurant information.
	 */
	private final RestaurantInfo mRestInfo;
	
	/**
	 * Past Orders placed at this restaurant.
	 */
	private final List<Order> mPastOrders;
	
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
	 */
	public Restaurant(ParseUser user) {
		super(Restaurant.class);
		mRestInfo = new RestaurantInfo(user);
		
		mPastOrders = new ArrayList<Order>();
		mPastUsers = new ArrayList<UserInfo>();
		
		mReservations = new ArrayList<Reservation>();
		mSessions = new ArrayList<DiningSession>();
		mCustomerRequests = new ArrayList<CustomerRequest>();
	}

	/**
	 * Creates a Restaurant object from the given ParseObject.
	 * 
	 * @param po Parse object to build from
	 */
	public Restaurant(ParseObject po) {
		super(po);
		mRestInfo = new RestaurantInfo(po.getParseObject(INFO));
		
		mPastOrders = ParseUtil.toListOfStorables(Order.class, po.getList(PAST_ORDERS));
		mPastUsers = ParseUtil.toListOfStorables(UserInfo.class, po.getList(PAST_USERS));
		
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
		List<Order> copy = new ArrayList<Order>(mPastOrders.size());
		Collections.copy(copy, mPastOrders);
		return copy;
	}

	/**
	 * List of current running dining sessions.
	 * @return List<DiningSession>
	 */
	public List<DiningSession> getSessions() {
		List<DiningSession> copy = new ArrayList<DiningSession>(mSessions.size());
		Collections.copy(copy, mSessions);
		return copy;
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
	 * Adds all the orders in to past orders. 
	 * @param orders Completed orders
	 */
	public void addToPastOrders(Collection<Order> orders) {
		mPastOrders.addAll(orders);
	}
	
	/**
	 * Adds given DiningSession to sessions.
	 * @param session to add
	 */
	public void addDiningSession(DiningSession session) {
		mSessions.add(session);
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
