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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Tests library class Restaurant.
 * 
 * White box tests
 * @author Zach
 *
 */
public class RestaurantTest extends AndroidTestCase {
	
	Activity activity;
	Context mContext = null;
	
	DiningSession testSession;
	ParseUser mUser;
	UserInfo testUInfo;
	List<MenuItem> testItems;
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
	}
	
	@Override
	protected void setUp() throws Exception {
		mContext = this.getContext();
		
		mUser = new ParseUser();
		mUser.setUsername("hello");
		mUser.setPassword("rtest");
		
		testSession = new DiningSession(32, new Date(3254645), testUInfo, testRestaurantInfo);
		testSession.setObjId("ts");
		
		testUInfo = new UserInfo(mUser);
		testUInfo.setObjId("tui");
		testItems = new ArrayList<MenuItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItem.setObjId("toi");
		testItems.add(testItem);
		testOrder = new Order(32, testUInfo, testItems);
		testOrder.setObjId("to");
		orders = new ArrayList<Order>();
		orders.add(testOrder);
		
		testUInfos = new ArrayList<UserInfo>();
		
		testUInfos.add(testUInfo);
		
		testRestaurantInfo = new RestaurantInfo(mUser);
		testRestaurant = new Restaurant(mUser);
		
		testRequest = new CustomerRequest("Order", testUInfo);
		testRequest.setObjId("trq");
		testReservation = new Reservation(testUInfo, testRestaurantInfo, new Date(32));
		testReservation.setObjId("tr");
		
		testRequests = new ArrayList<CustomerRequest>();
		testRequests.add(testRequest);
		testReservations = new ArrayList<Reservation>();
		testReservations.add(testReservation);
		testSessions = new ArrayList<DiningSession>();
		testSessions.add(testSession);
	}

	/**
	 * Asserts that the restaurant correctly stores the expected data.
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
	 */
	public void testAddCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		assertEquals(testRequests, testRestaurant.getCustomerRequests());
	}

	/**
	 * Asserts that the restaurant correctly adds a reservation.
	 */
	public void testAddReservation() {
		testRestaurant.addReservation(testReservation);
		assertEquals(testReservations, testRestaurant.getReservationList());
	}

	/**
	 * Asserts that the restaurant correctly completes an order.
	 */
	public void testCompleteOrder() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		assertEquals(new ArrayList<Order>(), testRestaurant.getPendingOrders());
		assertEquals(orders, testRestaurant.getPastOrders());
	}

	/**
	 * Asserts that the restaurant correctly adds an order.
	 */
	public void testAddOrder() {
		testRestaurant.addOrder(testOrder);
		assertEquals(orders, testRestaurant.getPendingOrders());
	}

	/**
	 * Asserts that the restaurant correctly adds a dining session.
	 */
	public void testAddDiningSession() {
		testRestaurant.addDiningSession(testSession);
		assertEquals(testSessions, testRestaurant.getSessions());
	}

	/**
	 * Asserts that the restaurant correctly removes a dining session.
	 */
	public void testRemoveDiningSession() {
		testRestaurant.addDiningSession(testSession);
		testRestaurant.removeDiningSession(testSession);
		assertEquals(new ArrayList<DiningSession>(), testRestaurant.getSessions());
	}

	/**
	 * Asserts that the restaurant correctly removes a request.
	 */
	public void testRemoveCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		testRestaurant.removeCustomerRequest(testRequest);
		assertEquals(new ArrayList<CustomerRequest>(), testRestaurant.getCustomerRequests());
	}

	/**
	 * Asserts that the restaurant correctly removes a reservation.
	 */
	public void testRemoveReservation() {
		testRestaurant.addReservation(testReservation);
		testRestaurant.removeReservation(testReservation);
		assertEquals(new ArrayList<Reservation>(), testRestaurant.getReservationList());
	}

	/**
	 * Asserts that the restaurant correctly clears past orders.
	 */
	public void testClearPastOrders() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		testRestaurant.clearPastOrders();
		assertEquals(new ArrayList<Order>(), testRestaurant.getPastOrders());
	}
	
	/**
	 * Asserts that the Restaurant stays the same when packed and
	 * unpacked.
	 */
	public void testPackAndUnpack() throws ParseException {
		
		ParseObject pObj = testRestaurant.packObject();
		Restaurant unPacked = new Restaurant(pObj);
		
		assertEquals(testRestaurant.getObjId(), unPacked.getObjId());
		assertEquals(testRestaurant.getName(), unPacked.getName());
		assertEquals(testRestaurant.getClass(), unPacked.getClass());
		assertEquals(testRestaurant.getCustomerRequests(), unPacked.getCustomerRequests());
		assertEquals(testRestaurant.getPastOrders(), unPacked.getPastOrders());
		assertEquals(testRestaurant.getPendingOrders(), unPacked.getPendingOrders());
		assertEquals(testRestaurant.getReservationList(), unPacked.getReservationList());
		assertEquals(testRestaurant.getSessions(), unPacked.getSessions());
		assertEquals(testRestaurant.getTempCustomerRequest(), unPacked.getTempCustomerRequest());
		assertEquals(testRestaurant.getTempDiningSession(), unPacked.getTempDiningSession());

	}

}
