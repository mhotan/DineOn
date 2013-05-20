package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.active.OrderDetailActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class OrderDetailActivityTest extends
ActivityInstrumentationTestCase2<OrderDetailActivity> {

	private static final String fakeUserName = "createRestaurantFakeUserName";
	private static final String fakePassword = "createRestaurantFakePassword";
	private static final String fakeEmail = "fakeemail@yourmomhouse.com";
	private OrderDetailActivity mActivity;
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private UserInfo mUI;

	public OrderDetailActivityTest() {
		super(OrderDetailActivity.class);
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
		mRestaurant.addOrder(TestUtility.createFakeOrder(1, mUI));
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				OrderDetailActivity.class);
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
	
	public void testEditMessageText() {
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
				EditText message = (EditText) mActivity.findViewById(
						uw.cse.dineon.restaurant.R.id.edittext_message_block);
				message.requestFocus();
				message.setText("Your order is on its way.");
		    }
		});
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();
		mActivity = getActivity();
		assertNotNull(mActivity);
	}

	public void testSendShoutOut(){
		mActivity.sendShoutOut(mUI, "Your order is on its way.");
	}
	
}
