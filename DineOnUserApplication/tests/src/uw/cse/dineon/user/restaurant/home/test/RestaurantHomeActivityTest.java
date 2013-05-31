package uw.cse.dineon.user.restaurant.home.test;

import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.restaurant.home.MenuItemDetailActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;

public class RestaurantHomeActivityTest extends
		ActivityInstrumentationTestCase2<RestaurantHomeActivity> {

	private ParseUser testUser;
	private DiningSession testSession;
	private RestaurantHomeActivity mActivity;
	private ParseUser testUser1;
	private DineOnUser dineOnUser;
	private RestaurantInfo testRInfo;
	private Restaurant rest;
	private Instrumentation mInstrumentation;
	private long time = 1000;
	
	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// create a user
		dineOnUser = TestUtility.createFakeUser();
		
		// create a restaurant
		rest = TestUtility.createFakeRestaurant();
		
		// create a dining session simulation
		DiningSession ds = TestUtility.createFakeDiningSession(
				dineOnUser.getUserInfo(), rest.getInfo());

		Order one = TestUtility.createFakeOrder(5, dineOnUser.getUserInfo());
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		
		Menu m = TestUtility.createFakeMenu();
		rest.getInfo().addMenu(m);
		
		// Initialize activity testing parameters
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    // initilize static data
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    DineOnUserApplication.setRestaurantOfInterest(rest.getInfo());
	    
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test that the UI is going to MenuItemDetailActivity when a
	 * menuitem is focused on
	 */
	public void testOnMenuItemFocusedOn() {
		final MenuItem m = new MenuItem(1, 1, "Fries", "Yum");
		
		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
				MenuItemDetailActivity.class.getName(), null, false);
		
		final RestaurantHomeActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.onMenuItemFocusedOn(m);
	          }
	      });
		mInstrumentation.waitForIdleSync();
		
		MenuItemDetailActivity itemSelect = (MenuItemDetailActivity) monRsa
				.waitForActivityWithTimeout(time);
		assertNotNull(itemSelect);
		itemSelect.finish();
	}
	
	/** 
	 * Test that I can get the current restaurant of focus
	 */
	public void testGetCurrentRestaurant() {
		assertNotNull(this.mActivity.getCurrentRestaurant());
		this.mActivity.finish();
	}
	
	/**
	 * Test onBackPressed to make sure we're directed to the 
	 * RestaurantSelectionActivity
	 */
	public void testOnBackPressed() {
		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
				RestaurantSelectionActivity.class.getName(), null, false);
		
		final RestaurantHomeActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.onBackPressed();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		
		RestaurantSelectionActivity resSelect = (RestaurantSelectionActivity) monRsa
				.waitForActivityWithTimeout(time);
		assertNotNull(resSelect);
		resSelect.finish();
	}
	
	/**
	 * Test the swiping of tabs in the restaurant home screen.
	 */
	public void testInfoMenuTabs() {
		android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				this.mActivity.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
		assertNotNull(pager);
		
		PagerAdapter adapter = pager.getAdapter();   
		assertNotNull(adapter);

		final android.support.v4.view.ViewPager PAGER = pager;
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(1);
			}
		});	      
		mInstrumentation.waitForIdleSync(); 
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(0);
			}
		});	      
		mInstrumentation.waitForIdleSync();
		this.mActivity.finish();
	}

	
}
