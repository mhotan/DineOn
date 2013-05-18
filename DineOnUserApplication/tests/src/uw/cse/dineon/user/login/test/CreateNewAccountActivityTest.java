package uw.cse.dineon.user.login.test;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.login.CreateNewAccountActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CreateNewAccountActivityTest extends ActivityInstrumentationTestCase2<CreateNewAccountActivity> {

	private ParseUser testUser;
	private DineOnUser dineOnUser;
	private CreateNewAccountActivity mActivity;
	private Intent addEvent;
	private ParseUser tempUser;

	public CreateNewAccountActivityTest() {
		super(CreateNewAccountActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		testUser = ParseUser.logIn("zach", "zach");
		dineOnUser = new DineOnUser(testUser);
		
	    addEvent = new Intent();
	    addEvent.putExtra(DineOnConstants.KEY_USER, dineOnUser);
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}
	
	public void testStartActivity() {
		mActivity.startActivity(addEvent);
	}
	
	public void testOnCreateNewAccount()  {
		mActivity.onCreateNewAccount("test", "test@gmail.com", "test", "test");
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			tempUser = ParseUser.logIn("test", "test");
			tempUser.delete();
		} catch (ParseException e) {
			
		}
	}

}
