package uw.cse.dineon.user.restaurant.home.test;

import com.parse.ParseUser;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.test.ActivityInstrumentationTestCase2;

public class RestaurantHomeActivityTest extends
		ActivityInstrumentationTestCase2<RestaurantHomeActivity> {

	private ParseUser testUser;
	private DiningSession testSession;
	private RestaurantHomeActivity mActivity;
	private ParseUser testUser1;
	private RestaurantInfo testRInfo;

	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
//		setActivityInitialTouchMode(false);
//		
//		testUser = ParseUser.logIn("zach", "zach");
//		testUser1 = ParseUser.logIn("r", "r");
//		DineOnUser testDUser = new DineOnUser(testUser1);
//		ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
//		inner.whereEqualTo(RestaurantInfo.PARSEUSER, testUser1);
//		ParseObject tempObj = inner.getFirst();
//		testRInfo = new RestaurantInfo(tempObj);
//		
//		
//		testSession = new DiningSession(1, new Date(243), new UserInfo(testUser), testRInfo);
//		
//	    Intent addEvent = new Intent();
//	    ArrayList<Parcelable> addIntent = new ArrayList<Parcelable>();
//	    addIntent.add(testSession);
//	    addEvent.putParcelableArrayListExtra(DineOnConstants.KEY_DININGSESSION, addIntent);
//	    DineOnUserApplication.setDineOnUser(testDUser);
//	    setActivityIntent(addEvent);
//		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnMenuItemFocusedOn() {
//		MenuItem m = new MenuItem(1, 1, "Fries", "Yum");
//		mActivity.onMenuItemFocusedOn(m);
	}
	
	public void testDeleteResume() {
//		getInstrumentation().waitForIdleSync();
//		mActivity.finish();
//
//		mActivity = getActivity();
	}


}
