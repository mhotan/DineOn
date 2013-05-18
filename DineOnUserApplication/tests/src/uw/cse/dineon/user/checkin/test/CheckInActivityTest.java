package uw.cse.dineon.user.checkin.test;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.checkin.CheckInActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CheckInActivityTest extends
		ActivityInstrumentationTestCase2<CheckInActivity> {

	private CheckInActivity mActivity;
	private Intent addEvent;

	public CheckInActivityTest() {
		super(CheckInActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		ParseUser testUser = ParseUser.logIn("zach", "zach");
		DineOnUser dineOnUser = new DineOnUser(testUser);
		
	    addEvent = new Intent();
	    addEvent.putExtra(DineOnConstants.KEY_USER, dineOnUser);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnActivityResultIntIntIntent() {
		mActivity.onActivityResult(0, 0, addEvent);
	}

	public void testOnCheckInSuccess() {
		mActivity.onCheckInSuccess();
	}

	public void testOnCheckInFail() {
		mActivity.onCheckInFail();
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}

}
