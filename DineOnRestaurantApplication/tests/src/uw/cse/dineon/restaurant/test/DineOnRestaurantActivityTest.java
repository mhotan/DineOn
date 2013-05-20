package uw.cse.dineon.restaurant.test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import android.content.Intent;
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
		mUser.signUp();

		mDineOnUser = new DineOnUser(mUser);
		mRestaurant = new Restaurant(mUser);

		testSession = new DiningSession(1, mDineOnUser.getUserInfo(), mRestaurant.getInfo());
		testSession.saveOnCurrentThread();
		mRestaurant.addDiningSession(testSession);

		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				RestauarantMainActivity.class);
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant);
		setActivityIntent(intent);

		setActivityInitialTouchMode(false);

		mActivity = getActivity();

	}

	@Override
	protected void tearDown() throws Exception {
		mUser.delete();
		mDineOnUser.deleteFromCloud();
		mRestaurant.deleteFromCloud();
		testSession.deleteFromCloud();
		mActivity.finish();
		super.tearDown();
	}

	/**
	 * Test if the Activity is actually able to check in and this actually does something
	 * 
	 * Whitebox test
	 */
	public void testUserCheckedIn() {
		mActivity.onUserCheckedIn(mDineOnUser.getUserInfo(), 1);
	}

	/**
	 * Test that when updating a user that doesn't exist
	 * in a dining session nothing happens.
	 * 
	 * Whitebox test
	 */
	public void testOnUserChangedNotExist() {
		mActivity.onUserChanged(mDineOnUser.getUserInfo());
		// TODO
	}

	/**
	 * Tests add customer request to the restaurant
	 * 
	 * Whitebox test
	 * @throws ParseException
	 */
	public void onTestCustomerRequest() {
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
	}

	/**
	 * Tests whether order requested does activity.
	 * 
	 * Whitebox test
	 */
	public void onTestOrderRequested() { 
		Order order = null;
		try {
			order = new Order(10, mDineOnUser.getUserInfo(), new ArrayList<MenuItem>());
			order.saveOnCurrentThread();
			mActivity.onOrderRequest(order, testSession.getObjId());
			order.deleteFromCloud();
		} catch (Exception e) {
			if (order != null) {
				order.deleteFromCloud();
			}
		}
	}
	
	/**
	 * Test restaurant can confirm reservation
	 * White box testing
	 */
	public void onReservationRequested() {
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
	}
	
	/**
	 * 
	 */
	public void testStartProfileActivity() {
		
	}

}
