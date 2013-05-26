package uw.cse.dineon.user.restaurant.home.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.ParseUser;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Parcelable;
import android.test.ActivityInstrumentationTestCase2;

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

	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ParseUser user = new ParseUser();
		user.setUsername("testUser");
		user.setPassword("12345");
		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		
		dineOnUser = new DineOnUser(user);
		
		rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
		
		List<CurrentOrderItem> mi = TestUtility.createFakeOrderItems(3);
		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		Menu m = TestUtility.createFakeMenu();
		m.addNewItem(mi.get(0).getMenuItem());
		rest.getInfo().addMenu(m);
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		mActivity.finish();
		super.tearDown();
	}

	public void testOnMenuItemFocusedOn() {
//		MenuItem m = new MenuItem(1, 1, "Fries", "Yum");
//		mActivity.onMenuItemFocusedOn(m);
	}
	
	public void testOnMenuItemIncremented() {
		
	}
	
	public void testOnMenuItemDecremented() {
		
	}


}
