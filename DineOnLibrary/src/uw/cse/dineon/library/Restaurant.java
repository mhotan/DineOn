package uw.cse.dineon.library;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * 
 * @author zachr81, mhotan
 */
public class Restaurant extends Storable {

	public static final String RESERVATION_LIST = "reservationList";
	public static final String INFO = "info";
	public static final String ORDERS = "orders";
	public static final String SESSIONS = "sessions";
	public static final String CUSTOMER_REQUESTS = "customerRequests";

	private final RestaurantInfo mRestInfo;
	private final List<Reservation> mReservations;
	private final List<Order> mOrders;
	private final List<DiningSession> mSessions;
	private final List<CustomerRequest> mCustomerRequests;

	/**
	 * Create a bare bones Restaurant with just a name.
	 * TODO Add basic constraints to creating a restaurant
	 * @param user of Restaurant
	 */
	public Restaurant(ParseUser user) {
		super(Restaurant.class);
		mRestInfo = new RestaurantInfo(user);
		mReservations = new ArrayList<Reservation>();
		mOrders = new ArrayList<Order>();
		mSessions = new ArrayList<DiningSession>();
		mCustomerRequests = new ArrayList<CustomerRequest>();
	}

	/**
	 * Creates a Restaurant object from the given ParseObject.
	 * 
	 * @param po Parse object to build froms
	 * @throws ParseException 
	 */
	public Restaurant(ParseObject po) {
		super(po);
		mRestInfo = new RestaurantInfo(po.getParseObject(INFO));
		mReservations = ParseUtil.toListOfStorables(
				Reservation.class, po.getList(RESERVATION_LIST)); 
		mOrders = ParseUtil.toListOfStorables(Order.class, po.getList(ORDERS));
		mSessions = ParseUtil.toListOfStorables(DiningSession.class, po.getList(SESSIONS));
		mCustomerRequests = ParseUtil.toListOfStorables(
				CustomerRequest.class, po.getList(CUSTOMER_REQUESTS));
	}

	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(INFO, mRestInfo.packObject());
		po.put(RESERVATION_LIST, ParseUtil.toListOfParseObjects(mReservations));
		po.put(ORDERS, ParseUtil.toListOfParseObjects(mOrders));
		po.put(SESSIONS, ParseUtil.toListOfParseObjects(mSessions));
		po.put(CUSTOMER_REQUESTS, ParseUtil.toListOfParseObjects(mCustomerRequests));	
		return po;
	}

	/**
	 * Returns the name of this restaurant.
	 * @return Name
	 */
	public String getName() {
		return mRestInfo.getName();
	}
	
	/**
	 * @return the customerRequests
	 */
	public List<CustomerRequest> getCustomerRequests() {
		return mCustomerRequests;
	}

	//	/**
	//	 * @param customerRequests the customerRequests to set
	//	 */
	//	public void setCustomerRequests(List<CustomerRequest> customerRequests) {
	//		this.mCustomerRequests = customerRequests;
	//	}

	/**
	 * Add a new customer request.
	 * @param newReq request to add
	 */
	public void addCustomerRequest(CustomerRequest newReq) {
		mCustomerRequests.add(newReq);
	}

	/**
	 * Remove the specified CustomerRequest.
	 * @param oldReq request to remove
	 */
	public void removeCustomerRequest(CustomerRequest oldReq) {
		mCustomerRequests.remove(oldReq);
	}

	//	/**
	//	 * @return a copy of the Menu list
	//	 */
	//	public List<Menu> getMenus() {
	//		List<Menu> copy = new ArrayList<Menu>(menus.size());
	//		Collections.copy(copy, menus);
	//		return copy;
	//	}
	//	
	//	/**
	//	 * @param menus A Menu object to set as the new menu
	//	 */
	//	public void setMenus(List<Menu> menus) {
	//		this.menus = menus;
	//	}

	/**
	 * @return List<Reservation>
	 */
	public List<Reservation> getReservationList() {
		List<Reservation> copy = new ArrayList<Reservation>(mReservations.size());
		Collections.copy(copy, mReservations);
		return copy;
	}

	//	/**
	//	 * @param newReservationList A List of Reservations to set as the new reservation list
	//	 */
	//	public void setReservationList(List<Reservation> newReservationList) {
	//		this.mReservations = newReservationList;
	//	}
	//	
	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getInfo() {
		return mRestInfo;
	}

	//	/**
	//	 * Sets info to the param value.
	//	 * @param newInfo to set
	//	 */
	//	public void setInfo(RestaurantInfo newInfo) {
	//		this.mRestInfo = newInfo;
	//	}

	/**
	 * @return List<Order>
	 */
	public List<Order> getOrders() {
		List<Order> copy = new ArrayList<Order>(mOrders.size());
		Collections.copy(copy, mOrders);
		return copy;
	}
	//	
	//	/**
	//	 * Sets orders to the parameter value.
	//	 * @param newOrders to set
	//	 */
	//	public void setOrders(List<Order> newOrders) {
	//		mOrders = newOrders;
	//	}
	//	
	/**
	 * @return List<DiningSession>
	 */
	public List<DiningSession> getSessions() {
		List<DiningSession> copy = new ArrayList<DiningSession>(mSessions.size());
		Collections.copy(copy, mSessions);
		return copy;
	}

	//	/**
	//	 * Sets sessions to the parameter value.
	//	 * @param newSessions to set
	//	 */
	//	public void setSessions(List<DiningSession> newSessions) {
	//		mSessions = newSessions;
	//	}
	//	
	//	/**
	//	 * Adds the given menu to the menu list.
	//	 * @param newMenu to add
	//	 */
	//	public void addMenu(Menu newMenu) {
	//		menus.add(newMenu);
	//	}
	//	
	//	/**
	//	 * Remove the specified menu.
	//	 * @param menu to remove from the restaurant
	//	 */
	//	public void removeMenu(Menu menu) {
	//		menus.remove(menu);
	//	}


	/**
	 * Adds the given reservation to the reservation list.
	 * @param newReservation to add
	 */
	public void addReservation(Reservation newReservation) {
		mReservations.add(newReservation);
	}

	/**
	 * Remove the specified reservation.
	 * @param removeReservation from restaurant
	 */
	public void removeReservation(Reservation removeReservation) {
		mReservations.remove(removeReservation);
	}

	/**
	 * Adds given order to orders.
	 * @param order to add
	 */
	public void addOrder(Order order) {
		mOrders.add(order);
	}

	/**
	 * Remove given order from orders.
	 * @param order to remove
	 */
	public void removeOrder(Order order) {
		mOrders.remove(order);
	}

	/**
	 * Adds given DiningSession to sessions.
	 * @param session to add
	 */
	public void addDiningSession(DiningSession session) {
		mSessions.add(session);
	}

	/**
	 * Removes given DiningSession from sessions.
	 * @param session to remove
	 */
	public void removeDiningSession(DiningSession session) {
		mSessions.remove(session);
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
