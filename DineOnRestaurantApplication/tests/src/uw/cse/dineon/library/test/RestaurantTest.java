package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.test.AndroidTestCase;

public class RestaurantTest extends AndroidTestCase {
	
	Activity activity;
	Context mContext = null;
	
	DiningSession testSession;
	ParseUser testUser;
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
	

	
	protected void tearDown() throws Exception {
		testUser.delete();
	}

	protected void setUp() throws Exception {
		Parse.initialize(this.getContext(), "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");

		
		testUser = new ParseUser();
		testUser.setUsername("tester");
		testUser.setPassword("pass");
		testUser.signUp();
		testUser.save();
		testSession = new DiningSession(32, new Date(3254645), testUInfo);
		
		testUInfo = new UserInfo(testUser);
		testItems = new ArrayList<MenuItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItems.add(testItem);
		testOrder = new Order(32, testUInfo, testItems);
		orders = new ArrayList<Order>();
		orders.add(testOrder);
		
		testUInfos = new ArrayList<UserInfo>();
		
		testUInfos.add(testUInfo);
		
		testRestaurantInfo = new RestaurantInfo(testUser);
		testRestaurant = new Restaurant(testUser);
		
		testRequest = new CustomerRequest("Order", testUInfo);
		testReservation = new Reservation(testUInfo, testRestaurantInfo, new Date(32));
		
		testRequests = new ArrayList<CustomerRequest>();
		testRequests.add(testRequest);
		testReservations = new ArrayList<Reservation>();
		testReservations.add(testReservation);
		testSessions = new ArrayList<DiningSession>();
		testSessions.add(testSession);
	}

	protected void tearDownAfterClass() throws Exception {
		super.tearDown();
		
	}

	public void testRestaurantParseUser() {
		assertEquals(testRestaurantInfo.getName(), testRestaurant.getName());
		assertEquals(testRestaurantInfo.getName(), testRestaurant.getInfo().getName());
		assertEquals(new ArrayList<CustomerRequest>(), testRestaurant.getCustomerRequests());
		assertEquals(new ArrayList<Reservation>(), testRestaurant.getReservationList());
		assertEquals(new ArrayList<Order>(), testRestaurant.getPastOrders());
		assertEquals(new ArrayList<DiningSession>(), testRestaurant.getSessions());
		assertEquals(new ArrayList<Order>(), testRestaurant.getPendingOrders());
		
	}

	public void testAddCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		assertEquals(testRequests, testRestaurant.getCustomerRequests());
	}

	public void testAddReservation() {
		testRestaurant.addReservation(testReservation);
		assertEquals(testReservations, testRestaurant.getReservationList());
	}

	public void testCompleteOrder() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		assertEquals(new ArrayList<Order>(), testRestaurant.getPendingOrders());
		assertEquals(orders, testRestaurant.getPastOrders());
	}

	public void testAddOrder() {
		testRestaurant.addOrder(testOrder);
		assertEquals(orders, testRestaurant.getPendingOrders());
	}

	public void testAddDiningSession() {
		testRestaurant.addDiningSession(testSession);
		assertEquals(testSessions, testRestaurant.getSessions());
	}

	public void testRemoveDiningSession() {
		testRestaurant.addDiningSession(testSession);
		testRestaurant.removeDiningSession(testSession);
		assertEquals(new ArrayList<DiningSession>(), testRestaurant.getSessions());
	}

	public void testRemoveCustomerRequest() {
		testRestaurant.addCustomerRequest(testRequest);
		testRestaurant.removeCustomerRequest(testRequest);
		assertEquals(new ArrayList<CustomerRequest>(), testRestaurant.getCustomerRequests());
	}

	public void testRemoveReservation() {
		testRestaurant.addReservation(testReservation);
		testRestaurant.removeReservation(testReservation);
		assertEquals(new ArrayList<Reservation>(), testRestaurant.getReservationList());
	}

	public void testClearPastOrders() {
		testRestaurant.addOrder(testOrder);
		testRestaurant.completeOrder(testOrder);
		testRestaurant.clearPastOrders();
		assertEquals(new ArrayList<Order>(), testRestaurant.getPastOrders());
	}

}
