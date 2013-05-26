package uw.cse.dineon.user.restaurantselection.test;

import java.util.Date;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class RestaurantSelectionActivityTest extends
		ActivityInstrumentationTestCase2<RestaurantSelectionActivity> {

	private ParseUser testUser;
	private DineOnUser dineOnUser;
	private RestaurantSelectionActivity mActivity;
	private RestaurantInfo testRInfo;
	private Instrumentation mInstrumentation;

	public RestaurantSelectionActivityTest() {
		super(RestaurantSelectionActivity.class);
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
		
		Restaurant rest = new Restaurant(restUser);
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
//		super.tearDown();
	}

	public void testAddRestaurantInfo() throws ParseException {
//		List<RestaurantInfo> tempList = new ArrayList<RestaurantInfo>();
//		tempList.add(testRInfo);
//		mActivity.addRestaurantInfos(tempList);
	}

	public void testOnRestaurantFocusedOn() {
//		mActivity.onRestaurantFocusedOn(testRInfo);
	}

	
	public void testGetRestaurants() {
//		assertNotNull(mActivity.getRestaurants());
	}
	
	public void testDeleteResume() {
//		getInstrumentation().waitForIdleSync();
//		mActivity.finish();
//
//		mActivity = getActivity();
	}

}
