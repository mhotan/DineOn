package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.active.OrderDetailActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import com.parse.ParseUser;

/**
 * Test for 
 * @author mhotan
 */
public class OrderDetailActivityTest extends
ActivityInstrumentationTestCase2<OrderDetailActivity> {

	private static final String fakeUserName = "createRestaurantFakeUserName";
	private static final String fakePassword = "createRestaurantFakePassword";
	private static final String fakeEmail = "fakeemail@yourmomhouse.com";
	private OrderDetailActivity mActivity;
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private UserInfo mUI;
	private DiningSession mDiningSession;

	public OrderDetailActivityTest() {
		super(OrderDetailActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();


		mUser = new ParseUser();
		mUser.setEmail(fakeEmail);
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);
		
		mRestaurant = TestUtility.createFakeRestaurant();
		mUI = new UserInfo(mUser);
		mRestaurant.addOrder(TestUtility.createFakeOrder(1, mUI));
		mRestaurant.addDiningSession(mDiningSession);
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				OrderDetailActivity.class);
		setActivityIntent(intent);
		
		DineOnRestaurantApplication.logIn(mRestaurant);

		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		mActivity.finish();
		super.tearDown();
	}
	
	/**
	 * Tests editing the message text.
	 * 
	 * White box
	 */
	public void testEditMessageText() {
		final EditText message = (EditText) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.edittext_message_block);
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
				 
				message.requestFocus();
				message.setText("Your order is on its way.");
		    }
		});
		assertEquals("Your order is on its way.", message.getText());
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
	public void testSendShoutOut(){
		mActivity.sendShoutOut(mUI, "Your order is on its way.");
		//TODO No way to find without modifying toast creation
	}
	
	//TODO: More tests
	
}
