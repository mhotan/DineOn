package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;
import com.parse.ParseException;

/**
 * White box robotium test to the addition of image features
 * from phase 4.
 * @author mhotan
 *
 */
public class RobotiumProfileMenuTest extends
ActivityInstrumentationTestCase2<ProfileActivity> {

	private Solo solo;
	private FragmentActivity mActivity;

	public RobotiumProfileMenuTest() {
		super(ProfileActivity.class);
	}

	@Override
	public void setUp() throws ParseException {
		Restaurant rest = TestUtility.createFakeRestaurant();
		DineOnRestaurantApplication.logIn(rest);
		solo = new Solo(getInstrumentation(), getActivity());
		mActivity = getActivity();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		DineOnRestaurantApplication.logOut(null);
	}

	/**
	 * Method tests if there was a an empty restaurant and
	 * we change the name to something useful.
	 */
	public void testEmptyRestaurantMenuOK() { 
		addMenuWithName();
	}

	/**
	 * Test whether restaurant add a new menu produces 
	 */
	public void testEmptyRestaurantMenuCancel() {
		// Attempt to switch to menu screen
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();

		getInstrumentation().waitForIdleSync();
		final EditText TEXT = solo.getEditText(0);
		assertNotNull(TEXT);
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TEXT.setText("Penis wines");
			}
		});
		solo.clickOnText("Cancel");

		getInstrumentation().waitForIdleSync();
	}

	private void addMenuWithName() {
		// Attempt to switch to menu screen
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();

		getInstrumentation().waitForIdleSync();
		final EditText TEXT = solo.getEditText(0);
		assertNotNull(TEXT);
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TEXT.setText("Penis wines");
			}
		});
		solo.clickOnText("Ok");

		getInstrumentation().waitForIdleSync();
	}

	/**
	 * 
	 */
	public void testAddNewMenuItemWithPicture() {

		addMenuWithName();

		boolean result = getInstrumentation().invokeMenuActionSync(mActivity,
				uw.cse.dineon.restaurant.R.id.menu_add_menu_item, 0);

		assertTrue(result); // true if button selected success
		getInstrumentation().waitForIdleSync();

		final EditText name = solo.getEditText(0);
		final EditText price = solo.getEditText(2);

		assertNotNull(name);
		assertNotNull(price);

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				name.setText("3 Penis Wine");
				price.setText("5.99");
			}
		});

		solo.clickOnText("Save");
		
		getInstrumentation().waitForIdleSync();
		
		View v = mActivity.findViewById(uw.cse.dineon.restaurant.R.id.image_thumbnail_menuitem);
		solo.clickOnView(v);
		
		getInstrumentation().waitForIdleSync();
		
		solo.clickOnText("Cancel");
	}

	public void testTrivial() {
		assertTrue(true);
	}

}
