package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.CreateNewAccountFragment;
import uw.cse.dineon.restaurant.login.CreateNewRestaurantAccountActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class CreateRestaurantAccountTest extends
ActivityInstrumentationTestCase2<CreateNewRestaurantAccountActivity> {

	private static final int WAIT_TIME = 10000;
	
	private static final String fakeUserName = "createRestAcctFakeUserName";
	private static final String fakePassword = "createRestAcctFakePassword";
	private static final String fakeEmail = "createRestAcct@yourmomhouse.com";
	private CreateNewRestaurantAccountActivity mActivity;

	private EditText username;
	private EditText password;
	private EditText passwordrepeat;
	private EditText email;
	private Button submit;
	
	public CreateRestaurantAccountTest() throws ParseException {
		super(CreateNewRestaurantAccountActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// initialize Parse
		Parse.initialize(getInstrumentation().getTargetContext(),
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul",
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		
		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		
		CreateNewAccountFragment frag = getFragment();
		View current = frag.getView();
		username = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_createnewaccount_username);
		
		password = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_createnewaccount_password);
		
		passwordrepeat = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_createnewaccount_repeat_password);
		
		email = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_createnewaccount_email);
		
		submit = (Button) current.findViewById(
				uw.cse.dineon.restaurant.R.id.button_create_account);
	}

	@Override
	protected void tearDown() throws Exception {
//		mActivity.finish();
		super.tearDown();
	}
	
	/**
	 * Test the existence of the CreateNewAccount Fragment
	 * 
	 * White-box
	 */
	public void testFragmentExistence() {
		Fragment f = getFragment();
		assertNotNull(f);
		assertNotNull(f.getView());
	}
	
	/**
	 * Asserts that a user correctly creates an account
	 * with valid credentials.
	 * 
	 * White-box
	 */
	public void testCreateNewAccount() throws ParseException {
		
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				username.setText(fakeUserName);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				email.setText(fakeEmail);
				submit.performClick();
			}
		});
		
		RestauarantMainActivity mainAct = (RestauarantMainActivity) 
				monitor.waitForActivityWithTimeout(WAIT_TIME);
		assertNotNull(mainAct);
		
		ParseUser curUser = ParseUser.getCurrentUser();
		curUser.fetch();
		assertNotNull(curUser);
		assertEquals(curUser.getUsername(), fakeUserName);
		assertEquals(curUser.getEmail(), fakeEmail);
		
		ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
		inner.whereEqualTo(RestaurantInfo.PARSEUSER, curUser);
		ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
		query.whereMatchesQuery(Restaurant.INFO, inner);
		ParseObject object = query.getFirst();
		
		assertNotNull(object);
		
		Restaurant justMade = new Restaurant(object);
		
		assertNotNull(justMade);
		assertNotNull(justMade.getInfo());
		
		assertEquals(justMade.getName(), fakeUserName);
		
		justMade.deleteFromCloud();
		curUser.delete();
		
		mainAct.finish();
	}
	
	/**
	 * Asserts that a user can't create an account without a username.
	 * 
	 * White-box
	 */
	public void testNoUsernameFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				email.setText(fakeEmail);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a password.
	 * 
	 * White-box
	 */
	public void testNoPasswordFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with different passwords.
	 * 
	 * White-box
	 */
	public void testNoPasswordMatchFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText("lolz");
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without an email.
	 * 
	 * White-box
	 */
	public void testNoEmailFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(2000);
		assertNull(startedActivity);
	}
	
	/**
	 * Returns the fragment
	 * 
	 * @return frag CreateNewAccountFragment
	 */
	private CreateNewAccountFragment getFragment(){
		Fragment f = mActivity.getSupportFragmentManager().findFragmentById(
				uw.cse.dineon.restaurant.R.id.fragment1);
		CreateNewAccountFragment frag = (CreateNewAccountFragment) f;
		return frag;
	}
}
