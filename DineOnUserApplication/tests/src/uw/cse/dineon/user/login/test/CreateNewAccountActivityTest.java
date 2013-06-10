package uw.cse.dineon.user.login.test;

import java.util.Date;
import java.util.List;

import com.parse.ParseUser;
import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.login.CreateNewAccountActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

/**
 * Tests CreateNewAccountActivity and its corresponding fragment.
 * Ensures that they can be accessed and function properly.
 * @author espeo196
 */
public class CreateNewAccountActivityTest extends 
		ActivityInstrumentationTestCase2<CreateNewAccountActivity> {

	private static final int WAIT_TIME = 10000;
	
	private DineOnUser dineOnUser;
	private Restaurant rest;
	private Instrumentation mInstrumentation;
	private CreateNewAccountActivity mActivity;

	public CreateNewAccountActivityTest() {
		super(CreateNewAccountActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ParseUser user = new ParseUser();
		user.setUsername("testUser");
		user.setPassword("12345");
		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		
		dineOnUser = new DineOnUser(user);
		
		rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
		
		List<MenuItem> mi = TestUtility.createFakeMenuItems(3);
		Order one = new Order(1, dineOnUser.getUserInfo());
		for (MenuItem item: mi) {
			one.setItemQuantity(item, 1);
		}
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		Menu m = TestUtility.createFakeMenu();
		m.addNewItem(mi.get(0));
		rest.getInfo().addMenu(m);
		
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    DineOnUserApplication.setDineOnUser(this.dineOnUser, null);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    
		mActivity = getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.setActivity(null);

	}

	/**
	 * Test to make sure that creating an invalid account will not
	 * advance the user to the restaurant selection activity. Once
	 * that is ensured, it makes certain that the transition to 
	 * restaurant selection activity is successful  if the creation
	 * of the account were to succeed.
	 */
	public void testCreateNewAccount() {
		this.dineOnUser.setDiningSession(null);
		this.dineOnUser.getUserInfo().setEmail("fakeEmai@fake.com");
			
		ActivityMonitor monRsa = getInstrumentation().addMonitor(
				RestaurantSelectionActivity.class.getName(), null, false);
	

		final EditText U_ET = (EditText) 
				this.mActivity.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_username);
		assertNotNull(U_ET);
		final EditText P_ET = (EditText) 
				this.mActivity.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_password);
		assertNotNull(P_ET);
		final EditText PR_ET = (EditText)
				this.mActivity.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_repeat_password);
		assertNotNull(PR_ET);
		final EditText E_ET = (EditText) 
				this.mActivity.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_email);
		assertNotNull(E_ET);
		final Button S_B = (Button) this.mActivity.findViewById(
				uw.cse.dineon.user.R.id.button_create_account);
		assertNotNull(S_B);
		
		mActivity.runOnUiThread(new Runnable() {
		
			@Override
			public void run() {
				U_ET.setText(dineOnUser.getName());
				P_ET.setText("12345");
				P_ET.setText("123456");
				E_ET.setText(dineOnUser.getUserInfo().getEmail());
				S_B.performClick();
			}
		});
		this.mInstrumentation.waitForIdleSync();
		
		assertTrue(this.mActivity instanceof CreateNewAccountActivity);
		this.mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mActivity.destroyFailDialog();
			}
		});
		
		this.mActivity.onSuccess(this.dineOnUser);
		RestaurantSelectionActivity rsa = (RestaurantSelectionActivity) 
				monRsa.waitForActivityWithTimeout(WAIT_TIME);

		assertNotNull(rsa);
	}

}
