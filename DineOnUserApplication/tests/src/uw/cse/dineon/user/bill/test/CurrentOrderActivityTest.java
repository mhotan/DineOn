package uw.cse.dineon.user.bill.test;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CurrentOrderActivityTest extends
		ActivityInstrumentationTestCase2<CurrentOrderActivity> {

	private CurrentOrderActivity mActivity;

	public CurrentOrderActivityTest() {
		super(CurrentOrderActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		ParseUser testUser = ParseUser.logIn("zach", "zach");
		DineOnUser dineOnUser = new DineOnUser(testUser);
		DineOnUserApplication.setDineOnUser(dineOnUser);
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnPlaceOrder() {
		mActivity.onPlaceOrder("rs");
	}

	public void testOnRequestMade() {
		mActivity.onRequestMade("MORE WATER!!!!");
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}

}
