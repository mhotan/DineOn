package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.active.DiningSessionDetailActivity;
import uw.cse.dineon.restaurant.active.OrderDetailActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class DiningSessionDetailActivityTest extends
ActivityInstrumentationTestCase2<DiningSessionDetailActivity> {

	private static final String fakeUserName = "createRestaurantFakeUserName";
	private static final String fakePassword = "createRestaurantFakePassword";
	private static final String fakeEmail = "fakeemail@yourmomhouse.com";
	private DiningSessionDetailActivity mActivity;
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private RestaurantInfo mRI;
	private UserInfo mUI;
	private DiningSession mDiningSession;

	public DiningSessionDetailActivityTest() {
		super(DiningSessionDetailActivity.class);
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
		mRI = new RestaurantInfo(mUser);
		mUI = new UserInfo(mUser);
		mDiningSession = TestUtility.createFakeDiningSession(mUI, mRI);
		mRestaurant = TestUtility.createFakeRestaurant(mUser);
		mRestaurant.addDiningSession(mDiningSession);
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				DiningSessionDetailActivity.class);
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
	 * Tests sending a message to the user.
	 * 
	 * White box
	 */
	public void testSetDiningSession(){
		mActivity.sendShoutOut(mUI, "Wazzup home slice???");
	}
	
}
