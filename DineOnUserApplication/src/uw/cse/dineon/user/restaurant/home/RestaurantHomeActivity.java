package uw.cse.dineon.user.restaurant.home;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.MenuItemDetailFragment.MenuItemDetailListener;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;


/**
 * The is the main activity that all other activities branch off from or contain 
 * relevant fragments.  This activity manages the viewing of the information fragment
 * along with all the menus of the restaurant.  This Activity also handles all the call backs for those activities
 * @author mhotan
 */
public class RestaurantHomeActivity extends DineOnUserActivity
// Implement all the fragments callbacks
implements RestaurantRetrievable, SubMenuFragment.MenuItemListListener, 
RestaurantInfoFragment.RestaurantInfoListener,
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
		DineOnUser dou = DineOnUserApplication.getDineOnUser();
		DiningSession currentSession = dou.getDiningSession();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean temp = super.onCreateOptionsMenu(menu);
		this.disableMenuItem(menu, R.id.option_check_in);
		this.disableMenuItem(menu, R.id.option_search);
		return temp;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean temp = super.onPrepareOptionsMenu(menu);
		this.disableMenuItem(menu, R.id.option_check_in);
		this.disableMenuItem(menu, R.id.option_search);
		return temp;
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
	
	@Override
	public void onMakeRequest(String request) {
		UserInfo ui = DineOnUserApplication.getUserInfo();
		
		final CustomerRequest C_REQ = new CustomerRequest(request, ui);
		
		final RestaurantHomeActivity COACT = this;
		C_REQ.saveInBackGround(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null) {
					COACT.placeRequest(C_REQ);
				} else {
					Log.e(TAG, "Request did not save: " + e.getMessage());
				}
			}
		} );
				
	}

	@Override
	public void onMenuItemAddToOrder(MenuItem item, int quantity) {
		DineOnUserApplication.getDineOnUser().setMenuItemToOrder(item, quantity);
	}
}
