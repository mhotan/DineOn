package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RequestDetailActivity;
import android.app.Fragment;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseUser;

public class RequestDetailActivityTest extends
ActivityInstrumentationTestCase2<RequestDetailActivity> {

	private static final String fakeEmail = "fakeemail@yourmomhouse.com";
	private RequestDetailActivity mActivity;
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private UserInfo mUI;
	private RestaurantInfo mRI;
	private CustomerRequest mRequest;
	private DiningSession mDiningSession;

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
	
		mRestaurant = TestUtility.createFakeRestaurant();
		mUI = new UserInfo(mUser);
		mRequest = TestUtility.createFakeRequest(mUI);
		mDiningSession = TestUtility.createFakeDiningSession(mUI, mRI);
		mRestaurant.addDiningSession(mDiningSession);
		mRestaurant.addCustomerRequest(mRequest);
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				RequestDetailActivity.class);
		setActivityIntent(intent);
		
		mRestaurant.setTempRequest(mRequest);
		
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
	 * Tests sending a request to a staff.
	 * 
	 * White box
	 */
	public void testOnSendRequestToStaff(){
		mActivity.onSendTaskToStaff(mRequest, "Marty", "");
		assertEquals("Marty", mRequest.getWaiter());
	}
	
	/**
	 * Tests sending a message to the user.
	 * 
	 * White box
	 */
	public void testSendShoutOut(){
		mActivity.sendShoutOut(mUI, "Your order is on its way.");
		//TODO Need to modify toast creation to assert here
	}
	
	/**
	 * Tests clicking the button to send a request
	 */
	public void testClickRequest() {
		//Fragment mFrag = mActivity.getFragmentManager().findFragmentById(R.layout.fragment_request_detail);
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
		    	mActivity.findViewById(R.id.button_send_to_staff).performClick();
		    }
		});
		getInstrumentation().waitForIdleSync();
		
		//TODO Assert
	}	
}
