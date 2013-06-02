package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.image.ImageIO;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import uw.cse.dineon.restaurant.profile.RestaurantInfoFragment;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jayway.android.robotium.solo.Solo;
import com.parse.ParseException;

/**
 * White box testing to test manipulation of restaurant info.
 * @author mhotan
 *
 */
public class RobotiumProfileInfoTest extends
ActivityInstrumentationTestCase2<ProfileActivity> {

	private Solo solo;
	private FragmentActivity mActivity;

	public RobotiumProfileInfoTest() {
		super(ProfileActivity.class);
	}

	@Override
	public void setUp() throws ParseException {
		Restaurant rest = TestUtility.createFakeRestaurant();
		DineOnRestaurantApplication.logIn(rest);

		mActivity = getActivity();
		solo = new Solo(getInstrumentation(), mActivity);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		DineOnRestaurantApplication.logOut(null);
	}

	/**
	 * Test adding a single image to the gallery populates exactlys by one
	 */
	public void testAddImage() {

		addImage();

		// Grab the gallery
		LinearLayout gallery = (LinearLayout) 
				mActivity.findViewById(uw.cse.dineon.restaurant.R.id.gallery_restaurant_images);

		assertNotNull(gallery);
		assertEquals(1, gallery.getChildCount());
	}

	/**
	 * Adds a single image
	 */
	private void addImage() {
		// Make sure we are the profile activity
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(0);
			}
		});
		getInstrumentation().waitForIdleSync();

		// Make sure we can grab the fragment
		Fragment frag = mActivity.getSupportFragmentManager().findFragmentByTag(
				ProfileActivity.LAST_FRAG_TAG);
		assertEquals(frag.getClass(), RestaurantInfoFragment.class);

		// Make appropiate cast
		final RestaurantInfoFragment infoFrag = (RestaurantInfoFragment) frag;

		// Load random image and add to view
		final Bitmap b = ImageIO.loadBitmapFromResource(
				getInstrumentation().getContext().getResources(), R.raw.martys);

		// Make sure we are the profile activity
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				infoFrag.addImage(b);
			}
		});

		getInstrumentation().waitForIdleSync();
	}

	/**
	 * Test the deletion of an image when cancel image is not actually deleted.
	 */
	public void testDeleteImage() {
		addImage();

		// Find the image button and press delete
		ImageButton delete = (ImageButton) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.button_delete_image);
		assertNotNull(delete);

		// Make sure we are the profile activity
		solo.clickOnView(delete);
		getInstrumentation().waitForIdleSync();
		
		// agree to delete
		solo.clickOnText("Cancel");
		getInstrumentation().waitForIdleSync();
		
		// Grab the gallery
		LinearLayout gallery = (LinearLayout) 
				mActivity.findViewById(uw.cse.dineon.restaurant.R.id.gallery_restaurant_images);

		assertNotNull(gallery);
		assertEquals(1, gallery.getChildCount());
	}

	public void testUpdateAddress() {



	}

}
