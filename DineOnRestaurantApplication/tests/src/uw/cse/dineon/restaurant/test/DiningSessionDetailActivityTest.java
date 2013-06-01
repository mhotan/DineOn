package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.active.DiningSessionDetailActivity;
import android.test.ActivityInstrumentationTestCase2;
import com.parse.Parse;
import com.parse.ParseUser;

/**
 * 
 * Tests the Activity which displays dining session details
 *
 */
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

		mRI = new RestaurantInfo(mUser);
		mUI = new UserInfo(mUser);
		mDiningSession = TestUtility.createFakeDiningSession(mUI, mRI);
		mRestaurant = TestUtility.createFakeRestaurant();
		mRestaurant.addDiningSession(mDiningSession);


		DineOnRestaurantApplication.logIn(mRestaurant);

		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {

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
		//TODO: Only way to check toast is adding a boolean or something in toast creation
	}
	
	//TODO: More Tests
	
}
