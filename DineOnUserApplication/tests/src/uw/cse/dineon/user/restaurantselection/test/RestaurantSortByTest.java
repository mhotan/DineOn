package uw.cse.dineon.user.restaurantselection.test;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Test driven development
 * Test class for Restaurant sorting, which hasn't
 * been implemented yet.
 */

public class RestaurantSortByTest extends
ActivityInstrumentationTestCase2<RestaurantSelectionActivity> {

	private static final String fakeUserName = "userSortByFakeUserName";
	private static final String fakePassword = "userSortByFakePassword";
	private static final String fakeEmail = "userSortBy@yourmomhouse.com";
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private DineOnUser dineOnUser;
	
	private RestaurantSelectionActivity mActivity;
	
	public RestaurantSortByTest() {
		super(RestaurantSelectionActivity.class);
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
		mRestaurant = new Restaurant(mUser);
		
		dineOnUser = new DineOnUser(mUser);
		DineOnUserApplication.setDineOnUser(dineOnUser);
		
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				RestaurantSelectionActivity.class);
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant);
		
		mActivity = getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception {
		mUser.delete();
		mRestaurant.deleteFromCloud();
		super.tearDown();
		
	}
	
	/**
	 * Asserts that clicking the sort by distance button
	 * sorts the restaurants by distance.
	 */
	public void testSortByDistance() {
		assertTrue("Not implemented yet", true);
	}
	
	/**
	 * Asserts that clicking the sort by distance button
	 * sorts the restaurants by user favorites only.
	 */
	public void testSortByFavorites() {
		assertTrue("Not implemented yet", true);
	}
	
	/**
	 * Asserts that clicking the sort by distance button
	 * sorts the restaurants by friends favorites only.
	 */
	public void testSortByFriendsFavorites() {
		assertTrue("Not implemented yet", true);
	}
	
}
