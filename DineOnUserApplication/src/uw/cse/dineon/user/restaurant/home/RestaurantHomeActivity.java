package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.MenuItemDetailFragment.MenuItemDetailListener;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;


/**
 * The main activity where the restaurant.
 * @author mhotan
 */
public class RestaurantHomeActivity extends DineOnUserActivity
// Implement all the fragments callbacks
implements SubMenuFragment.MenuItemListListener, 
RestaurantInfoFragment.RestaurantInfoListener,
RestaurantHomeMainFragment.ReferenceDataListener,
MenuItemDetailListener {

	private final String TAG = this.getClass().getSimpleName();

	public static final String EXTRA_RESTAURANT = "restaurant"; 
	public static final String EXTRA_MENUITEM_NAME = "menuitemname";
	
	/**
	 * A reference to the restaurant where we want to center all our request.
	 */
	private RestaurantInfo mRestaurant;

	/**
	 * Reference to UI components that contain relevant views.
	 * mMainContainer can never be null
	 * While support container can be null pending some available layouts.
	 */
	private FrameLayout mSupportContainer;

	private MenuItem mItemOfFocus;

	//////////////////////////////////////////////////////////////////////
	////  Android specific 
	//////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Make sure we have the current restaurant
		DiningSession currentSession = mUser.getDiningSession();
		if (currentSession != null) { // We have a current session with a restaurant info
			mRestaurant = currentSession.getRestaurantInfo();
		} else if (DineOnUserApplication.getRestaurantOfInterest() != null) {
			mRestaurant = DineOnUserApplication.getRestaurantOfInterest();
		}
		
		setContentView(R.layout.activity_restaurant_home);	
		
		// Container to hold support details.
		mSupportContainer = (FrameLayout) findViewById(R.id.container_support);
		if (mSupportContainer != null) {
			mSupportContainer.setVisibility(View.GONE);
		}
	}

	//////////////////////////////////////////////////////////////////////
	////   Call backs for Fragment methods
	//////////////////////////////////////////////////////////////////////


	@Override
	public void onMakeReservation(String reservation) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMenuItemFocusedOn(MenuItem menuItem) {
		// Attempt to find if the fragment exists
		// If it does update its contents
		// TODO Implement the ability to switch inbetween fragments
		mItemOfFocus = menuItem;
		// Attempt to replace the container
		if (!replaceFragmentInContainer(new MenuItemDetailFragment())) {
			// If it doesn't start a new activity 
			Intent i = new Intent(getApplicationContext(), MenuItemDetailActivity.class);
			i.putExtra(MenuItemDetailActivity.EXTRA_MENUITEM_NAME, menuItem.getTitle());
			startActivity(i);
		}
	}

	/**
	 * Replaces any fragment in the container with the inputed fragment.
	 * If no container exists then nothing is added.
	 * @param frag Fragment to add.
	 * @return true if something was added or replaced, false other wise
	 */
	private boolean replaceFragmentInContainer(Fragment frag) {
		View view = findViewById(R.id.container_support);
		if (view != null) { // We are in a 7in or above tablet
			FrameLayout container = (FrameLayout) view;
			container.setVisibility(View.VISIBLE);

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			ft.replace(R.id.container_support, frag);
			ft.commit();
			return true;
		}
		return false;
	}

	@Override
	public void onViewCurrentBill() {
		// TODO Take to Current Bill screen

	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		return this.mRestaurant;
	}

	@Override
	public MenuItem getMenuItem() {
		return mItemOfFocus;
	}
	
	@Override
	public void onBackPressed() {
		// We want on back to always go to restaurant selection.
		Intent i = new Intent(this, RestaurantSelectionActivity.class);
		startActivity(i);
	}
}
