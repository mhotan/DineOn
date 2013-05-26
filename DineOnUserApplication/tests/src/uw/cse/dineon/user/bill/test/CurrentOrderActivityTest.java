package uw.cse.dineon.user.bill.test;

import java.util.Date;
import java.util.List;

import com.parse.ParseUser;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CurrentOrderActivityTest extends
		ActivityInstrumentationTestCase2<CurrentOrderActivity> {

	private CurrentOrderActivity mActivity;
	private DineOnUser dineOnUser;
	private Instrumentation mInstrumentation;
	private Order one;

	public CurrentOrderActivityTest() {
		super(CurrentOrderActivity.class);
	}

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
		one = new Order(1, dineOnUser.getUserInfo(), mi);
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

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnPlaceOrder() {
		//mActivity.onPlaceOrder(one);
	}

	public void testOnRequestMade() {
		//mActivity.onRequestMade();
	}
}
