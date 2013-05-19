package uw.cse.dineon.user.login.test;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class UserLoginActivityTest extends
		ActivityInstrumentationTestCase2<UserLoginActivity> {

	private UserLoginActivity mActivity;
	private EditText mNameText;
	private ParseUser testUser;
	private DineOnUser dineOnUser;

	public UserLoginActivityTest() {
		super(UserLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		testUser = ParseUser.logIn("zach", "zach");
		dineOnUser = new DineOnUser(testUser);
		DineOnUserApplication.setDineOnUser(dineOnUser);
		
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		mActivity = getActivity();
		
		mNameText = (EditText) mActivity.findViewById(uw.cse.dineon.user.R.id.input_login_email);
		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testOnLogin() {
		mActivity.onLogin("jordan", "12345");
		getInstrumentation().waitForIdleSync();
	}
	
}
