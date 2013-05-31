package uw.cse.dineon.restaurant.test;

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

public class CreateRestaurantAccountTest extends
ActivityInstrumentationTestCase2<CreateNewRestaurantAccountActivity> {

	private static final int WAIT_TIME = 10000;
	private static final int WAIT_LOGIN_TIME = 500;
	
	private String validCreditCard = "4222222222222";
	private String validSecurityCode = "411";
	private String month = "12";
	private String year = "2099";
	private String zip = "98105";
	private static String fakeUserName = "createRestAcctUN";
	private static final String fakePassword = "createRestAcctFakePassword";
	private static final String fakeEmail = "createRestAcct@yourmomhouse.com";
	private CreateNewRestaurantAccountActivity mActivity;

	private EditText username;
	private EditText password;
	private EditText passwordrepeat;
	private EditText email;
	private Button submit;
	private EditText creditCard;
	private EditText securityCode;
	private EditText expMonth;
	private EditText expYear;
	private EditText zipCode;
	
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
		
		creditCard = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_credit_card_number);
		
		securityCode = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_security_code);
		
		expMonth = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_expiration_month);
		
		expYear = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_expiration_year);
		
		zipCode = (EditText) 
				current.findViewById(
						uw.cse.dineon.restaurant.R.id.input_zip_code);
		
		submit = (Button) current.findViewById(
				uw.cse.dineon.restaurant.R.id.button_create_account);
	}

	@Override
	protected void tearDown() throws Exception {
		mActivity.finish();
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
	 * Commented out because it requires a network call
	 * 
	 * Black-box
	 */
	public void testCreateNewAccount() throws ParseException {
//		Random r = new Random();
//		int randomNum = r.nextInt(1000000);
//		fakeUserName = "" + randomNum;
//		
//		ActivityMonitor monitor = getInstrumentation().addMonitor(
//				RestauarantMainActivity.class.getName(), null, false);
//		
//		mActivity.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				username.setText(fakeUserName);
//				password.setText(fakePassword);
//				passwordrepeat.setText(fakePassword);
//				email.setText(fakeEmail);
//				submit.performClick();
//			}
//		});
//		
//		RestauarantMainActivity mainAct = (RestauarantMainActivity) 
//				monitor.waitForActivityWithTimeout(WAIT_TIME);
//		assertNotNull(mainAct);
//		
//		ParseUser curUser = ParseUser.getCurrentUser();
//		curUser.fetch();
//		assertNotNull(curUser);
//		assertEquals(curUser.getUsername(), fakeUserName);
//		assertEquals(curUser.getEmail(), fakeEmail);
//		
//		ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
//		inner.whereEqualTo(RestaurantInfo.PARSEUSER, curUser);
//		ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
//		query.whereMatchesQuery(Restaurant.INFO, inner);
//		ParseObject object = query.getFirst();
//		
//		assertNotNull(object);
//		
//		Restaurant justMade = new Restaurant(object);
//		
//		assertNotNull(justMade);
//		assertNotNull(justMade.getInfo());
//		
//		assertEquals(justMade.getName(), fakeUserName);
//		
//		justMade.deleteFromCloud();
//		curUser.delete();
//		
//		mainAct.finish();
	}
	
	/**
	 * Asserts that a user can't create an account without a username.
	 * 
	 * Black-box
	 */
	public void testNoUsernameFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				email.setText(fakeEmail);
				creditCard.setText(validCreditCard);
				securityCode.setText(validSecurityCode);
				expMonth.setText(month);
				expYear.setText(year);
				zipCode.setText(zip);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without an email.
	 * 
	 * Black-box
	 */
	public void testNoEmailFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				creditCard.setText(validCreditCard);
				securityCode.setText(validSecurityCode);
				expMonth.setText(month);
				expYear.setText(year);
				zipCode.setText(zip);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a password.
	 * 
	 * Black-box
	 */
	public void testNoPasswordFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(null);
				creditCard.setText(validCreditCard);
				securityCode.setText(validSecurityCode);
				expMonth.setText(month);
				expYear.setText(year);
				zipCode.setText(zip);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with different passwords.
	 * 
	 * Black-box
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
				creditCard.setText(validCreditCard);
				securityCode.setText(validSecurityCode);
				expMonth.setText(month);
				expYear.setText(year);
				zipCode.setText(zip);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
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
