package uw.cse.dineon.restaurant.test;

import java.util.Random;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.R;
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
import com.parse.ParseUser;

/**
 * Tests the Activity for creating a new restaurant account
 * @author Zach
 *
 */
public class CreateRestaurantAccountTest extends
ActivityInstrumentationTestCase2<CreateNewRestaurantAccountActivity> {

	private static final int WAIT_LOGIN_TIME = 500;
	
	private static String fakeUserName = "createRestAcctUN";
	private static final String fakePassword = "createRestAcctFakePassword";
	private static final String fakeEmail = "createRestAcct@yourmomhouse.com";
	private static final String fakeCreditCardNum = "4111111111111111";
	private static final String fakeSecurityCode = "411";
	private static final String expMonth = "12";
	private static final String expYear = "2049";
	private static final String zipCode = "98095";
	private CreateNewRestaurantAccountActivity mActivity;

	private EditText username;
	private EditText password;
	private EditText passwordrepeat;
	private EditText email;
	private EditText mCreditCard, mSecurityCode, mExpMo, mExpYr, mZip; 
	private Button submit;
	
	public CreateRestaurantAccountTest() throws ParseException {
		super(CreateNewRestaurantAccountActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DineOnConstants.DEBUG = false;
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
		
		mCreditCard = (EditText) current.findViewById(R.id.input_credit_card_number);
		mSecurityCode = (EditText) current.findViewById(R.id.input_security_code);
		mExpMo = (EditText) current.findViewById(R.id.input_expiration_month);
		mExpYr = (EditText) current.findViewById(R.id.input_expiration_year);
		mZip = (EditText) current.findViewById(R.id.input_zip_code);
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
	 *TODO Commented out because it requires a network call
	 * 
	 * Black-box
	 */
	public void testCreateNewAccount() throws ParseException {
		Random r = new Random();
		int randomNum = r.nextInt(1000000);
		fakeUserName = "" + randomNum;
		
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				username.setText(fakeUserName);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				email.setText(fakeEmail);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.performClick();
			}
		});
		
		RestauarantMainActivity mainAct = (RestauarantMainActivity) 
				monitor.waitForActivityWithTimeout(10000);
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
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
		//TODO test that correct dialog appears 
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
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
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
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
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
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a credit card
	 * number.
	 */
	public void testNoCreditCardFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(null);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a credit card
	 * security code.
	 */
	public void testNoSecurityCodeFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(null);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with a 4 digit security code
	 * number that expects .
	 */
	public void testFourDigitSecurityCodeWhenExpectedThreeFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText("1111");
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a credit card
	 * expiration month.
	 */
	public void testNoExpMonthFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(null);
				mExpYr.setText(expYear);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with an expired
	 * credit card (month).
	 */
	public void testExpiredMonthFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText("01");
				mExpYr.setText("2013");
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a credit card
	 * expiration year.
	 */
	public void testNoExpYrFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(null);
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with an expired credit
	 * card (year).
	 */
	public void testExpiredYrFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText("2000");
				mZip.setText(zipCode);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account without a zip code.
	 */
	public void testNoZipCodeFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText(null);
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with a too short zip code.
	 */
	public void testTooShortZipCodeFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText("9999");
				submit.requestFocus();
				submit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't create an account with a too long zip code.
	 */
	public void testTooLongZipCodeFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				username.setText(fakeUserName);
				email.setText(fakeEmail);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				mCreditCard.setText(fakeCreditCardNum);
				mSecurityCode.setText(fakeSecurityCode);
				mExpMo.setText(expMonth);
				mExpYr.setText(expYear);
				mZip.setText("999999");
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
