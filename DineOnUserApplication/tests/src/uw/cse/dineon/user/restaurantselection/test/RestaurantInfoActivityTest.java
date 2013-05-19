package uw.cse.dineon.user.restaurantselection.test;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.restaurantselection.RestaurantInfoActivity;

import com.parse.Parse;
import com.parse.ParseUser;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class RestaurantInfoActivityTest extends ActivityInstrumentationTestCase2<RestaurantInfoActivity> {

	private ParseUser testUser;
	private DineOnUser dineOnUser;
	private RestaurantInfoActivity mActivity;
	private RestaurantInfo rInf;

	public RestaurantInfoActivityTest() {
		super(RestaurantInfoActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		testUser = ParseUser.logIn("zach", "zach");
		dineOnUser = new DineOnUser(testUser);
		DineOnUserApplication.setDineOnUser(dineOnUser);
		
		ParseUser restUser = ParseUser.logIn("r", "r");
		rInf = new RestaurantInfo(restUser);
		
	    Intent addEvent = new Intent();
	    addEvent.putExtra("RESTAURANT", rInf);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetCurrentRestaurant() {
//		assertEquals(rInf, mActivity.getCurrentRestaurant());
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}

}
