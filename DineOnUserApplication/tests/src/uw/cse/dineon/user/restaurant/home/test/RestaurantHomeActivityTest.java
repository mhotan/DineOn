package uw.cse.dineon.user.restaurant.home.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.ParseUser;

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

	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
//		super.setUp();
//		setActivityInitialTouchMode(false);
//		
//		testUser = new ParseUser();
//		testUser.setUsername("testUser");
//		testUser.setPassword("12345");
//		
//		ParseUser restUser = new ParseUser();
//		restUser.setUsername("testRestUser");
//		restUser.setPassword("12345");
//		
//		dineOnUser = new DineOnUser(testUser);
//		
//		Restaurant rest = new Restaurant(restUser);
//		DiningSession ds = 
//				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
//		
//		List<MenuItem> mi = TestUtility.getFakeMenuItems();
//		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
//		ds.addPendingOrder(one);
//		
//		Menu m = TestUtility.getFakeMenu();
//		m.addNewItem(mi.get(0));
//		rest.getInfo().addMenu(m);
//			
//	    Intent addEvent = new Intent();
//	    ArrayList<Parcelable> addIntent = new ArrayList<Parcelable>();
//	    addIntent.add(testSession);
//	    addEvent.putParcelableArrayListExtra(DineOnConstants.KEY_DININGSESSION, addIntent);
//	    DineOnUserApplication.setDineOnUser(dineOnUser);
//	    setActivityIntent(addEvent);
//		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
//		mActivity.finish();
//		super.tearDown();
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
