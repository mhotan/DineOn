package uw.cse.dineon.library;

import java.util.*;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * 
 * @author zachr81
 *
 */
public class Restaurant extends Storable implements Parcelable {

	public static final String MENUS = "menus";
	public static final String RESERVATION_LIST = "reservationList";
	public static final String INFO = "info";
	public static final String ORDERS = "orders";
	public static final String SESSIONS = "sessions";
	public static final String CUSTOMER_REQUESTS = "customerRequests";
	
	private List<Menu> menus;
	private List<Reservation> reservationList;
	private RestaurantInfo info;
	private List<Order> orders;
	private List<DiningSession> sessions;
	private List<CustomerRequest> customerRequests;
	
	/**
	 * @param Menu
	 * @param reservationList
	 * @param info
	 * @param orders
	 * @Param sessions
	 */
	public Restaurant(List<Menu> menus, List<Reservation> reservationList,
			RestaurantInfo info, List<Order> orders, List<DiningSession> sessions,
			List<CustomerRequest> customerRequests) {
		super();
		this.menus = menus;
		this.reservationList = reservationList;
		this.info = info;
		this.orders = orders;
		this.sessions = sessions;
		this.customerRequests = customerRequests;
	}
	
	/**
	 * Creates a Restaurant object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		Menus, ReservationList, RestaurantInfo, Orders, Sessions,
	 * 		CustomerRequests, String.
	 */
	public Restaurant(Parcel source) {
		readFromParcel(source);
	}
	
	/**
	 * @return the customerRequests
	 */
	public List<CustomerRequest> getCustomerRequests() {
		return customerRequests;
	}

	/**
	 * @param customerRequests the customerRequests to set
	 */
	public void setCustomerRequests(List<CustomerRequest> customerRequests) {
		this.customerRequests = customerRequests;
	}
	
	/**
	 * Add a new customer request.
	 * @param newReq request to add
	 */
	public void addCustomerRequest(CustomerRequest newReq) {
		customerRequests.add(newReq);
	}
	
	/**
	 * Remove the specified CustomerRequest.
	 * @param oldReq request to remove
	 */
	public void removeCustomerRequest(CustomerRequest oldReq) {
		customerRequests.remove(oldReq);
	}

	/**
	 * @return a copy of the Menu list
	 */
	public List<Menu> getMenus() {
		List<Menu> copy = new ArrayList<Menu>(menus.size());
		Collections.copy(copy, menus);
		return copy;
	}
	
	/**
	 * @param menu: A Menu object to set as the new menu
	 * 
	 */
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	
	/**
	 * @return List<Reservation>
	 */
	public List<Reservation> getReservationList() {
		List<Reservation> copy = new ArrayList<Reservation>(reservationList.size());
		Collections.copy(copy, reservationList);
		return copy;
	}
	
	/**
	 * @param reservationList: A List of Reservations to set as the new reservation list
	 * 
	 */
	public void setReservationList(List<Reservation> newReservationList) {
		this.reservationList = newReservationList;
	}
	
	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getInfo() {
		return info;
	}
	
	/**
	 * Sets info to the param value.
	 * @param RestaurantInfo
	 */
	public void setInfo(RestaurantInfo newInfo) {
		this.info = newInfo;
	}
	
	/**
	 * @return List<Order>
	 */
	public List<Order> getOrders() {
		List<Order> copy = new ArrayList<Order>(orders.size());
		Collections.copy(copy, orders);
		return copy;
	}
	
	/**
	 * Sets orders to the parameter value.
	 * @param List<Order>
	 */
	public void setOrders(List<Order> newOrders) {
		orders = newOrders;
	}
	
	/**
	 * @return List<DiningSession>
	 */
	public List<DiningSession> getSessions() {
		List<DiningSession> copy = new ArrayList<DiningSession>(sessions.size());
		Collections.copy(copy, sessions);
		return copy;
	}
	
	/**
	 * Sets sessions to the parameter value.
	 * @param List<DiningSession>
	 */
	public void setSessions(List<DiningSession> newSessions) {
		sessions = newSessions;
	}
	
	/**
	 * Adds the given menu to the menu list
	 * @param Menu to add
	 */
	public void addMenu(Menu newMenu) {
		menus.add(newMenu);
	}
	
	/**
	 * Remove the specified menu.
	 * @param menu
	 */
	public void removeMenu(Menu menu) {
		menus.remove(menu);
	}
	
	
	/**
	 * Adds the given reservation to the reservation list.
	 * @param Reservation
	 */
	public void addReservation(Reservation newReservation) {
		reservationList.add(newReservation);
	}
	
	/**
	 * Remove the specified reservation.
	 * @param Reservation
	 */
	public void removeReservation(Reservation removeReservation) {
		reservationList.remove(removeReservation);
	}
	
	/**
	 * Adds given order to orders.
	 * @param Order
	 */
	public void addOrder(Order order) {
		orders.add(order);
	}
	
	/**
	 * Remove given order from orders.
	 * @param Order
	 */
	public void removeOrder(Order order) {
		orders.remove(order);
	}
	
	/**
	 * Adds given DiningSession to sessions.
	 * @param DiningSession
	 */
	public void addDiningSession(DiningSession session) {
		sessions.add(session);
	}
	
	/**
	 * Removes given DiningSession from sessions.
	 * @param DiningSession
	 */
	public void removeDiningSession(DiningSession session) {
		sessions.remove(session);
	}


	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(Restaurant.MENUS, this.menus);
		pobj.add(Restaurant.INFO, this.info);
		pobj.add(Restaurant.RESERVATION_LIST, this.reservationList);
		pobj.add(Restaurant.ORDERS, this.orders);
		pobj.add(Restaurant.SESSIONS, this.sessions);
		pobj.add(Restaurant.CUSTOMER_REQUESTS, this.customerRequests);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
				
		return pobj;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setMenus((List<Menu>) pobj.get(Restaurant.MENUS));
		this.setInfo((RestaurantInfo)pobj.get(Restaurant.INFO));
		this.setReservationList((List<Reservation>) pobj.get(Restaurant.RESERVATION_LIST));
		this.setOrders((List<Order>) pobj.get(Restaurant.ORDERS));
		this.setSessions((List<DiningSession>) pobj.get(Restaurant.SESSIONS));
		this.setCustomerRequests((List<CustomerRequest>) pobj.get(Restaurant.CUSTOMER_REQUESTS));
		
	}

	/**
	 * A Parcel method to describe the contents of the object.
	 * @return an int describing contents
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Write the object to a parcel object.
	 * @param the Parcel to write to and any set flags
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(menus);
		dest.writeTypedList(reservationList);
		dest.writeParcelable(info, flags);
		dest.writeTypedList(orders);
		dest.writeTypedList(sessions);
		dest.writeTypedList(customerRequests);
		dest.writeString(this.getObjId());
						
	}
	
	/**
	 * Parcelable creator object of a Restaurant.
	 * Can create a Restaurant from a Parcel.
	 */
	public static final Parcelable.Creator<Restaurant> CREATOR = 
			new Parcelable.Creator<Restaurant>() {

				@Override
				public Restaurant createFromParcel(Parcel source) {
					return new Restaurant(source);
				}

				@Override
				public Restaurant[] newArray(int size) {
					return new Restaurant[size];
				}
	};
			
	//read an object back out of parcel
	private void readFromParcel(Parcel source) {
		source.readTypedList(menus, Menu.CREATOR);
		source.readTypedList(reservationList, Reservation.CREATOR);
		this.setInfo((RestaurantInfo)source.readParcelable(
				RestaurantInfo.class.getClassLoader()));
		source.readTypedList(orders, Order.CREATOR);
		source.readTypedList(sessions, DiningSession.CREATOR);
		source.readTypedList(customerRequests, CustomerRequest.CREATOR);
		this.setObjId(source.readString());
	}
}
