package uw.cse.dineon.user.login.test;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.login.CreateNewAccountActivity;
import uw.cse.dineon.user.login.CreateNewAccountFragment;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Tests CreateNewAccountActivity and its corresponding fragment.
 * Ensures that they can be accessed and function properly.
 * @author espeo196
 */
public class CreateNewAccountActivityTest extends ActivityInstrumentationTestCase2<CreateNewAccountActivity> {

	private static final int WAIT_TIME = 10000;
	
	private static final String fakeUserName = "createUserFakeUser";
	private static final String fakePassword = "createUserFakePass";
	private static final String fakeEmail = "fake@fake.com";
	private CreateNewAccountActivity mActivity;

	public CreateNewAccountActivityTest() {
		super(CreateNewAccountActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		
		setActivityInitialTouchMode(false);

		mActivity = getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFragmentExistence() {
		Fragment f = getFragment();
		assertNotNull(f);
		assertNotNull(f.getView());
	}
	
	public void testCreateNewAccount() throws ParseException {
		
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestaurantSelectionActivity.class.getName(), null, false);
		
		CreateNewAccountFragment frag = getFragment();
		View current = frag.getView();
		final EditText username = (EditText) 
				current.findViewById(
						uw.cse.dineon.user.R.id.intput_createnewaccount_username);
		final EditText password = (EditText) 
				current.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_password);
		final EditText passwordrepeat = (EditText) 
				current.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_repeat_password);
		final EditText email = (EditText) 
				current.findViewById(
						uw.cse.dineon.user.R.id.input_createnewaccount_email);
		final Button submit = (Button) current.findViewById(
				uw.cse.dineon.user.R.id.button_create_account);
		
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				username.setText(fakeUserName);
				password.setText(fakePassword);
				passwordrepeat.setText(fakePassword);
				email.setText(fakeEmail);
				submit.performClick();
			}
		});
		
		RestaurantSelectionActivity mainAct = (RestaurantSelectionActivity) 
				monitor.waitForActivityWithTimeout(WAIT_TIME);
		assertNotNull(mainAct);

		ParseUser curUser = ParseUser.getCurrentUser();
		curUser.fetch();
		assertNotNull(curUser);
		assertEquals(curUser.getUsername(), fakeUserName);
		assertEquals(curUser.getEmail(), fakeEmail);
		
		ParseQuery inner = new ParseQuery(UserInfo.class.getSimpleName());
		inner.whereEqualTo(UserInfo.PARSEUSER, curUser);
		ParseQuery query = new ParseQuery(DineOnUser.class.getSimpleName());
		query.whereMatchesQuery(DineOnUser.USER_INFO, inner);
		ParseObject object = query.getFirst();
		
		assertNotNull(object);
		
		DineOnUser justMade = new DineOnUser(object);
		
		assertNotNull(justMade);
		assertNotNull(justMade.getUserInfo());
		
		assertEquals(justMade.getName(), fakeUserName);
		justMade.getUserInfo().deleteFromCloud();
		justMade.deleteFromCloud();
		curUser.deleteInBackground();
		
		mainAct.finish();
	}

	private CreateNewAccountFragment getFragment(){
		Fragment f = mActivity.getSupportFragmentManager().findFragmentById(
				uw.cse.dineon.user.R.id.createNewAccount);
		CreateNewAccountFragment frag = (CreateNewAccountFragment) f;
		return frag;
	}

}
