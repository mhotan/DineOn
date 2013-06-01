package uw.cse.dineon.user.restaurantselection.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class RestaurantSelectionActivityTest extends
		ActivityInstrumentationTestCase2<RestaurantSelectionActivity> {

	private DineOnUser dineOnUser;
	private RestaurantSelectionActivity mActivity;
	private RestaurantInfo testRInfo;
	private Instrumentation mInstrumentation;
	private long time = 5000;

	public RestaurantSelectionActivityTest() {
		super(RestaurantSelectionActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// create a user
		dineOnUser = TestUtility.createFakeUser();
		
		// create a restaurant
		Restaurant rest = TestUtility.createFakeRestaurant();
		
		// create a dining session simulation
		DiningSession ds = TestUtility.createFakeDiningSession(
				dineOnUser.getUserInfo(), rest.getInfo());

		Order one = TestUtility.createFakeOrder(5, dineOnUser.getUserInfo());
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		
		Menu m = TestUtility.createFakeMenu();
		rest.getInfo().addMenu(m);
			    
	    // initilize static data
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    DineOnUserApplication.setRestaurantOfInterest(rest.getInfo());
		// Initialize activity testing parameters
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test that we can add restaurant infos to the selection list
	 * @throws ParseException
	 */
	public void testAddRestaurantInfo() throws ParseException {
		testRInfo = new RestaurantInfo(new ParseUser());
		List<RestaurantInfo> tempList = new ArrayList<RestaurantInfo>();
		tempList.add(testRInfo);
		final List<RestaurantInfo> list = tempList;
		final RestaurantSelectionActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.addRestaurantInfos(list);
	          }
	      });
		mInstrumentation.waitForIdleSync();
		
		// Notify the list of change
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.notifyFragment();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}
	
	/**
	 * Test that the Restaurant Home activity is started when 
	 * a restaurant is selected.
	 */
	public void testOnRestaurantSelected() {
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		RestaurantInfo rest = null;
		try {
			rest = new RestaurantInfo(restUser);
		} catch (ParseException e) {
			fail("Parse Exception Thrown");
		}
		
		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
				RestaurantHomeActivity.class.getName(), null, false);
		
		this.mActivity.onRestaurantSelected(rest);
		
		mInstrumentation.waitForIdleSync();
		
		RestaurantHomeActivity resSelect = (RestaurantHomeActivity) monRsa
				.waitForActivityWithTimeout(time );
		assertNotNull(resSelect);
		resSelect.finish();
	}
	
	/**
	 * Test that we get a list of restaurants in the activity
	 */
	public void testGetRestaurants() {
		assertNotNull(mActivity.getRestaurants());
		this.mActivity.finish();
	}
	
	/**
	 * Test that the dialog gets create and later deleted
	 */
	public void testProgressDialog() {
		final RestaurantSelectionActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.createProgressDialog();
	        	  
	          }
	      });
//		mInstrumentation.waitForIdleSync();
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.destroyProgressDialog();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}
	
	/**
	 * Test that a toast is made when no restaurants are found
	 */
	public void testToast() {
		final RestaurantSelectionActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
		      public void run() {
		  		RSA.showNoRestaurantsDialog("No restaurants");
		      }
		  });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}
	
	/**
	 * Test that the parse query callback update fields.
	 */
	public void testFindCallback() {
		ParseUser user = new ParseUser();
		user.setUsername("bill");
		user.setPassword("lovefred");
		FindCallback fCall = mActivity.getFindCallback("TestMessage");
		RestaurantInfo testRI = null;
		try {
			testRI = new RestaurantInfo(user);
		} catch (ParseException e) {
			fail("Couldn't build a restaurantInfo");
		}
		
		List<ParseObject> testL = new ArrayList<ParseObject>();
		testL.add(testRI.packObject());
		final List<ParseObject> list = testL;
		final FindCallback callback = fCall;
		final RestaurantSelectionActivity RSA = this.mActivity;
		RSA.runOnUiThread(new Runnable() {
		      public void run() {
		    	  callback.done(list, null);
		      }
		  });
		mInstrumentation.waitForIdleSync();
		RSA.runOnUiThread(new Runnable() {
		      public void run() {
		    	  callback.done(list, new ParseException(4, "Test error"));
		      }
		  });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}

}
