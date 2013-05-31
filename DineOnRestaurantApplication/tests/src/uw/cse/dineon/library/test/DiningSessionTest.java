package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import android.app.Activity;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.parse.ParseUser;

/**
 * A set of tests for the DiningSession library class
 * @author zach
 *
 */
public class DiningSessionTest extends AndroidTestCase {

	Activity activity;
	Context mContext = null;

	DiningSession testSession;
	ParseUser mUser;
	ParseUser testUser1;
	UserInfo testUInfo;
	UserInfo testUInfo1;
	List<CurrentOrderItem> testItems;
	MenuItem testItem;
	Order testOrder;
	List<Order> orders;
	List<UserInfo> testUInfos;
	RestaurantInfo testRInfo;


	/**
	 * Default constructor. 
	 */
	public DiningSessionTest() {
		super();
	}

	//Setup for the tests
	@Override
	protected void setUp() throws Exception {
		Log.i("progress", "start setup");

		Log.i("progress", "init parse");

		mUser = new ParseUser();
		mUser.setEmail("dst@a.com");
		mUser.setPassword("dst");
		mUser.setObjectId("245");

		testUInfo1 = new UserInfo(mUser);
		testUInfo1.setObjId("tui1");
		testRInfo = new RestaurantInfo(mUser);
		testSession = new DiningSession(32, new Date(3254645), testUInfo1, testRInfo);

		testUInfo = new UserInfo(mUser);
		testUInfo.setObjId("tui");
		testItems = new ArrayList<CurrentOrderItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItems.add(new CurrentOrderItem(testItem));
		testOrder = new Order(32, testUInfo, testItems);
		testOrder.setObjId("to");
		orders = new ArrayList<Order>();
		orders.add(testOrder);

		testUInfos = new ArrayList<UserInfo>();

		testUInfos.add(testUInfo1);
		testUInfos.add(testUInfo);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Asserts that the session has the correct list of orders
	 * 
	 * White box
	 */
	public void testGetOrders() {
		testSession.addPendingOrder(testOrder);
		assertEquals(orders, testSession.getOrders());
	}

	/**
	 * Asserts that the session has the correct table id stored
	 * 
	 * White box
	 */
	public void testGetTableID() {
		assertEquals(32, testSession.getTableID());
	}

	/**
	 * Asserts that the session has the correct changed table id stored
	 * 
	 * White box
	 */
	public void testResetTableID() {
		testSession.resetTableID(42);
		assertEquals(42, testSession.getTableID());
	}

	/**
	 * Asserts that the session has the correct list of users stored
	 * 
	 * White box
	 */
	public void testGetUsers() {
		List<UserInfo> expectedUser = new ArrayList<UserInfo>();
		expectedUser.add(testUInfo1);
		assertEquals(expectedUser, testSession.getUsers());
	}

	/**
	 * Asserts that the correct user is added
	 * 
	 * White box
	 */
	public void testAddUser() {
		testSession.addUser(testUInfo);

		assertEquals(testUInfos, testSession.getUsers());
	}

}
