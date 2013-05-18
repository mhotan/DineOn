package uw.cse.dineon.user.test;

import uw.cse.dineon.library.DineOnUser;

import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseUser;


public class DineOnUserActivityTest extends ActivityInstrumentationTestCase2<DineOnUserActivity> {
	private DineOnUserActivity mActivity;
	private ParseUser testUser;
	private DineOnUser dineOnUser;

//TODO No asserts in the class, it just runs the code
	
	public DineOnUserActivityTest() {
		super(DineOnUserActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		testUser = ParseUser.logIn("zach", "zach");
		dineOnUser = new DineOnUser(testUser);
		
	    Intent addEvent = new Intent();
	    addEvent.putExtra(DineOnConstants.KEY_USER, dineOnUser);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
		

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnActivityResultIntIntIntent() {
		Intent addEvent = new Intent();
	    addEvent.putExtra(DineOnConstants.KEY_USER, "zach");
	    setActivityIntent(addEvent);
		mActivity.onActivityResult(1, 1, addEvent);
		//fail("Not yet implemented");
	}
//
//	public void testOnCreateOptionsMenuMenu() {
//		//fail("Not yet implemented");
//	}

	public void testStartLoginActivity() {
		mActivity.startLoginActivity();
		//fail("Not yet implemented");
	}

//	public void testOnPrepareOptionsMenuMenu() {
//		//fail("Not yet implemented");
//	}

//	public void testOnOptionsItemSelectedMenuItem() {
//		assertTrue(true);
//		//fail("Not yet implemented");
//	}

	
//	public void testOnDiningSessionRecievedCallback() throws ParseException {
//		
//		mActivity.onInitialDiningSessionReceived(new DiningSession(2, new UserInfo(ParseUser.logIn("zach", "zach"))));
//		//fail("Not yet implemented");
//	}

	public void testOnSaveInstanceStateBundle() {
		assertTrue(true);
		//fail("Not yet implemented");
	}

	public void testOnRestoreInstanceStateBundle() {
		assertTrue(true);
		//fail("Not yet implemented");
	}

	public void testOnFail() {
		assertTrue(true);
		//fail("Not yet implemented");
	}

	public void testOnInitialDiningSessionReceived() {
		assertTrue(true);
		//fail("Not yet implemented");
	}

	public void testOnRestaurantInfoChanged() {
		assertTrue(true);
		//fail("Not yet implemented");
	} 
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}

}
