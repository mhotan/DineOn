package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.active.RequestDetailActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class RequestDetailActivityTest extends
ActivityInstrumentationTestCase2<RequestDetailActivity> {

	private static final String fakeUserName = "createRestaurantFakeUserName";
	private static final String fakePassword = "createRestaurantFakePassword";
	private static final String fakeEmail = "fakeemail@yourmomhouse.com";
	private RequestDetailActivity mActivity;
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private UserInfo mUI;
	private CustomerRequest mRequest;

	public RequestDetailActivityTest() {
		super(RequestDetailActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(getInstrumentation().getTargetContext(), 
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", 
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");

		mUser = new ParseUser();
		mUser.setEmail(fakeEmail);
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);
		try {
			mUser = ParseUser.logIn(fakeUserName, fakePassword);
		} catch (ParseException e) {
			mUser.signUp();
		}
		
		mRestaurant = TestUtility.createFakeRestaurant(mUser);
		mUI = new UserInfo(mUser);
		mRequest = TestUtility.createFakeRequest(mUI);
		mRestaurant.addCustomerRequest(mRequest);
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				RequestDetailActivity.class);
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant);
		setActivityIntent(intent);

		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		mUser.delete();
		mRestaurant.deleteFromCloud();
		mUI.deleteFromCloud();
		
		mActivity.finish();
		super.tearDown();
	}
	
	/**
	 * Asserts that the activity correctly restarts.
	 * 
	 * White box
	 */
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();
		mActivity = getActivity();
		assertNotNull(mActivity);
	}

	/**
	 * Tests sending a request to a staff.
	 * 
	 * White box
	 */
	public void testOnSendRequestToStaff(){
		mActivity.onSendTaskToStaff(mRequest, "Marty", "");
	}
	
	/**
	 * Tests sending a message to the user.
	 * 
	 * White box
	 */
	public void testSendShoutOut(){
		mActivity.sendShoutOut(mUI, "Your order is on its way.");
	}
	
}
