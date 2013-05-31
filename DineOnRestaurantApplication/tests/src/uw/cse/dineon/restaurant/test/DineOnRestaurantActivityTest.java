package uw.cse.dineon.restaurant.test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Test class to cover basic cases of DineOnRestaurantActivity 
 * @author mhotan
 */
public class DineOnRestaurantActivityTest extends
ActivityInstrumentationTestCase2<RestauarantMainActivity> {

	private RestauarantMainActivity mActivity;
	private ParseUser mUser;

	private DineOnUser mDineOnUser;
	private Restaurant mRestaurant;
	private DiningSession testSession;

	int orderNum = 100;

	private static final String fakeUserName = "fakeRestaurantParentClassName";
	private static final String fakePassword = "fakeRestaurantParentClassPassword";

	public DineOnRestaurantActivityTest() {
		super(RestauarantMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Parse.initialize(getInstrumentation().getTargetContext(), 
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", 
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");

		setActivityInitialTouchMode(false);

		mUser = new ParseUser();
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);

		

		mDineOnUser = new DineOnUser(mUser);
		mDineOnUser.setObjId("dou");
		UserInfo mUserInfo = mDineOnUser.getUserInfo();
		mUserInfo.setObjId("dui");
		mRestaurant = new Restaurant(mUser);

		testSession = new DiningSession(1, mUserInfo, mRestaurant.getInfo());
		mRestaurant.addDiningSession(testSession);

		DineOnRestaurantApplication.logIn(mRestaurant);

		setActivityInitialTouchMode(false);

		mActivity = getActivity();

	}

	@Override
	protected void tearDown() throws Exception {

		mActivity.finish();
		super.tearDown();
	}

	/**
	 * Test that when updating a user that doesn't exist
	 * in a dining session nothing happens.
	 * 
	 * Whitebox test
	 */
	public void testOnUserChangedNotExist() {
		mActivity.onUserChanged(mDineOnUser.getUserInfo());
		// TODO No current way to access userinfo data to confirm this
	}

	/**
	 * Tests add customer request to the restaurant
	 * 
	 * Whitebox test
	 * @throws ParseException
	 */
	public void testCustomerRequest() {
		CustomerRequest request = null;

		try {
			request = new CustomerRequest("Android testing is a joke", mDineOnUser.getUserInfo());
			request.saveOnCurrentThread();
			mActivity.onCustomerRequest(request, testSession.getObjId());
			request.deleteFromCloud();
		} catch (Exception e) {
			if (request != null) {
				request.deleteFromCloud();
			}
		}
		assertTrue(mRestaurant.getSessions().get(0).getRequests().contains(request));
	}

	/**
	 * Tests whether order requested does activity.
	 * 
	 * Whitebox test
	 */
	public void testOrderRequested() { 
		Order order = null;
		try {
			List<CurrentOrderItem> list = new ArrayList<CurrentOrderItem>();
			order = new Order(10, mDineOnUser.getUserInfo(), list);
			order.saveOnCurrentThread();
			mActivity.onOrderRequest(order, testSession.getObjId());
			order.deleteFromCloud();
		} catch (Exception e) {
			if (order != null) {
				order.deleteFromCloud();
			}
		}
		assertTrue(mRestaurant.getSessions().get(0).getOrders().contains(order));
	}
	
	/**
	 * Test restaurant can confirm reservation
	 * White box testing
	 */
	public void testOnReservationRequested() {
		Reservation res = null;
		try {
			res = new Reservation(
					mDineOnUser.getUserInfo(), mRestaurant.getInfo(), new GregorianCalendar().getTime());
			res.saveOnCurrentThread();
			mActivity.onReservationRequest(res);
			res.deleteFromCloud();
		} catch (Exception e) {
			if (res != null) {
				res.deleteFromCloud();
			}
		}
		assertTrue(mRestaurant.getReservationList().contains(res));
	}
	
	/**
	 * Tests that the app displays the correct menu when user is not logged in
	 * 
	 * White box test
	 */
	public void testUserNotLoggedIn() {
		DineOnRestaurantApplication.logOut(mActivity);
		mActivity.finish();
		mActivity = getActivity();
		
		//TODO: DoubleCheck that this should be null, it may be looking in the wrong spot and this
		//should exist, but not be visible
		assertNull(mActivity.findViewById(uw.cse.dineon.restaurant.R.id.item_restaurant_profile));
	}
	
	/**
	 * 
	 */
	public void testStartProfileActivity() {
		
	}

}
