package uw.cse.dineon.user.test;

import uw.cse.dineon.library.DineOnUser;

import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseUser;


public class DineOnUserActivityTest extends ActivityInstrumentationTestCase2<DineOnUserActivity> {
	
	private DineOnUser dineOnUser;
	private DineOnUserActivity mActivity;

	public DineOnUserActivityTest() {
		super(DineOnUserActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		
		ParseUser testUser = new ParseUser();
		testUser.setUsername("User");
		testUser.setPassword("pass");
		dineOnUser = new DineOnUser(testUser);
		
	    Intent addEvent = new Intent();
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
		

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnActivityResultIntIntIntent() {
		Intent addEvent = new Intent();
	    addEvent.putExtra(DineOnConstants.KEY_USER, "User");
	    setActivityIntent(addEvent);
		mActivity.onActivityResult(1, 1, addEvent);
	}

	public void testStartLoginActivity() {
		mActivity.startLoginActivity();
	}

//	public void testOnPrepareOptionsMenuMenu() {
//		//fail("Not yet implemented");
//	}

//	public void testOnOptionsItemSelectedMenuItem() {40
//		assertTrue(true);
//	}


}
