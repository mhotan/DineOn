package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import android.app.Activity;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

public class RestaurantTest extends AndroidTestCase {
	
	Activity activity;
	Context mContext = null;
	
	DiningSession testSession;
	ParseUser mUser;
	UserInfo testUInfo;
	List<CurrentOrderItem> testItems;
	MenuItem testItem;
	Order testOrder;
	List<Order> orders;
	List<UserInfo> testUInfos;
	Restaurant testRestaurant;
	RestaurantInfo testRestaurantInfo;
	
	CustomerRequest testRequest;
	Reservation testReservation;
	
	List<CustomerRequest> testRequests;
	List<Reservation> testReservations;
	List<DiningSession> testSessions;
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mUser.delete();
		testRestaurant.deleteFromCloud();
	}
	@Override
	protected void setUp() throws Exception {
		Log.i("progress", "start setup");
		mContext = this.getContext();
		Log.i("progress", "got context");
		Parse.initialize(mContext, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		Log.i("progress", "init parse");
		
		mUser = new ParseUser();
		mUser.setUsername("restaurantTest");
		mUser.setPassword("rtest");
		mUser.signUp();
		
		testSession = new DiningSession(32, new Date(3254645), testUInfo, testRestaurantInfo);
		
		testUInfo = new UserInfo(mUser);
		testItems = new ArrayList<CurrentOrderItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItems.add(new CurrentOrderItem(testItem));
		testOrder = new Order(32, testUInfo, testItems);
		orders = new ArrayList<Order>();
		orders.add(testOrder);
		
		testUInfos = new ArrayList<UserInfo>();
		
		testUInfos.add(testUInfo);
		
		testRestaurantInfo = new RestaurantInfo(mUser);
		testRestaurant = new Restaurant(mUser);
		
		testRequest = new CustomerRequest("Order", testUInfo);
		testReservation = new Reservation(testUInfo, testRestaurantInfo, new Date(32));
		
		testRequests = new ArrayList<CustomerRequest>();
		testRequests.add(testRequest);
		testReservations = new ArrayList<Reservation>();
		testReservations.add(testReservation);
		testSessions = new ArrayList<DiningSession>();
		testSessions.add(testSession);
		Log.i("progress", "end setup");
	}

	/**
	 * Asserts that the restaurant correctly stores the expected data.
	 * 
	 * White box
	 */
	public void testRestaurantParseUser() {
		assertEquals(testRestaurantInfo.getName(), testRestaurant.getName());
		assertEquals(testRestaurantInfo.getName(), testRestaurant.getInfo().getName());
		assertEquals(new ArrayList<CustomerRequest>(), testRestaurant.getCustomerRequests());
		assertEquals(new ArrayList<Reservation>(), testRestaurant.getReservationList());
		assertEquals(new ArrayList<Order>(), testRestaurant.getPastOrders());
		assertEquals(new ArrayList<DiningSession>(), testRestaurant.getSessions());
		assertEquals(new ArrayList<Order>(), testRestaurant.getPendingOrders());
		
	}

	/**
	 * Asserts that the restaurant correctly adds a request.
	 * 
	 * White box
	 */
	public void testAddCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		assertEquals(testRequests, testRestaurant.getCustomerRequests());
	}

	/**
	 * Asserts that the restaurant correctly adds a reservation.
	 * 
	 * White box
	 */
	public void testAddReservation() {
		testRestaurant.addReservation(testReservation);
		assertEquals(testReservations, testRestaurant.getReservationList());
	}

	/**
	 * Asserts that the restaurant correctly completes an order.
	 * 
	 * White box
	 */
	public void testCompleteOrder() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		assertEquals(new ArrayList<Order>(), testRestaurant.getPendingOrders());
		assertEquals(orders, testRestaurant.getPastOrders());
	}

	/**
	 * Asserts that the restaurant correctly adds an order.
	 * 
	 * White box
	 */
	public void testAddOrder() {
		testRestaurant.addOrder(testOrder);
		assertEquals(orders, testRestaurant.getPendingOrders());
	}

	/**
	 * Asserts that the restaurant correctly adds a dining session.
	 * 
	 * White box
	 */
	public void testAddDiningSession() {
		testRestaurant.addDiningSession(testSession);
		assertEquals(testSessions, testRestaurant.getSessions());
	}

	/**
	 * Asserts that the restaurant correctly removes a dining session.
	 * 
	 * White box
	 */
	public void testRemoveDiningSession() {
		testRestaurant.addDiningSession(testSession);
		testRestaurant.removeDiningSession(testSession);
		assertEquals(new ArrayList<DiningSession>(), testRestaurant.getSessions());
	}

	/**
	 * Asserts that the restaurant correctly removes a request.
	 * 
	 * White box
	 */
	public void testRemoveCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		testRestaurant.removeCustomerRequest(testRequest);
		assertEquals(new ArrayList<CustomerRequest>(), testRestaurant.getCustomerRequests());
	}

	/**
	 * Asserts that the restaurant correctly removes a reservation.
	 * 
	 * White box
	 */
	public void testRemoveReservation() {
		testRestaurant.addReservation(testReservation);
		testRestaurant.removeReservation(testReservation);
		assertEquals(new ArrayList<Reservation>(), testRestaurant.getReservationList());
	}

	/**
	 * Asserts that the restaurant correctly clears past orders.
	 * 
	 * White box
	 */
	public void testClearPastOrders() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		testRestaurant.clearPastOrders();
		assertEquals(new ArrayList<Order>(), testRestaurant.getPastOrders());
	}

}
