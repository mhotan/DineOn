package uw.cse.dineon.user.checkin.test;

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
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.checkin.CheckInActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CheckInActivityTest extends
		ActivityInstrumentationTestCase2<CheckInActivity> {

	private ParseUser testUser;
	private DineOnUser dineOnUser;
	private MenuItem steak;
	private CheckInActivity mActivity;

	public CheckInActivityTest() {
		super(CheckInActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		super.setUp();
		setActivityInitialTouchMode(false);
//		
		testUser = new ParseUser();
		testUser.setUsername("testUser");
		testUser.setPassword("12345");
//		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
//		
		dineOnUser = new DineOnUser(testUser);
//		
		Restaurant rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
//		
		List<CurrentOrderItem> mi = new ArrayList<CurrentOrderItem>();
		int itemCnt = 100;
		steak = new MenuItem(itemCnt++, 12.99, "testSteak", 
		"A juicy (test) hunk of meat.");
		mi.add(new CurrentOrderItem(steak));
		
		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
		ds.addPendingOrder(one);
		
		Menu m = new Menu("testEntrees");
		m.addNewItem(steak);
		
		rest.getInfo().addMenu(m);
			
	    Intent addEvent = new Intent();
	    
	    addEvent.putExtra(DineOnConstants.KEY_DININGSESSION, ds);
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testBasic() {
		
	}

}
