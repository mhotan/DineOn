package uw.cse.dineon.user.restaurant.home.test;

import java.util.ArrayList;
import java.util.Date;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
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
	private RestaurantInfo testRInfo;

	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
//		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
//		setActivityInitialTouchMode(false);
//		
//		testUser = ParseUser.logIn("zach", "zach");
//		testUser1 = ParseUser.logIn("r", "r");
//		DineOnUser testDUser = new DineOnUser(testUser1);
//		ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
//		inner.whereEqualTo(RestaurantInfo.PARSEUSER, testUser1);
//		ParseObject tempObj = inner.getFirst();
//		testRInfo = new RestaurantInfo(tempObj);
//		
//		
//		testSession = new DiningSession(1, new Date(243), new UserInfo(testUser), testRInfo);
//		
//	    Intent addEvent = new Intent();
//	    ArrayList<Parcelable> addIntent = new ArrayList<Parcelable>();
//	    addIntent.add(testSession);
//	    addEvent.putParcelableArrayListExtra(DineOnConstants.KEY_DININGSESSION, addIntent);
//	    DineOnUserApplication.setDineOnUser(testDUser);
//	    setActivityIntent(addEvent);
//		mActivity = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnMenuItemFocusedOn() {
//		MenuItem m = new MenuItem(2, 2.3, "fries", "potato");
//		mActivity.onMenuItemFocusedOn(m);
	}
	
	public void testDeleteResume() {
//		getInstrumentation().waitForIdleSync();
//		mActivity.finish();
//
//		mActivity = getActivity();
	}


}
