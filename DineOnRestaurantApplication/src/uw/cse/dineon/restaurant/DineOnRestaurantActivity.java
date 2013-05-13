package uw.cse.dineon.restaurant;

import java.util.Date;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.RestaurantSatellite.SateliteListener;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

/**
 * General Fragment Activity class that pertains to a specific Restaurant
 * client.  Once the Restaurant logged in then they are allowed specific
 * information related to the restaurant
 * @author mhotan
 */
public class DineOnRestaurantActivity extends FragmentActivity 
implements SateliteListener {

	/**
	 * member that defines this restaurant user
	 * This encapsulated this user with this restaurant instance only
	 * Another Activity (like LoginActivity) does the work of 
	 * authenticating and creating an account.
	 * 
	 * Abstract Function
	 * 		mRestaurant != null if and only if this user is logged in 
	 * 		with a proper Restaurant account
	 */
	protected static final String TAG = DineOnRestaurantActivity.class.getSimpleName();

	private RestaurantSatellite mSatellite;	

	/**
	 * The underlying restaurant instance.
	 */
	protected Restaurant mRestaurant;

	/**
	 * Just a variable for creation.  This helps us track the user
	 * 
	 */
	private String mRestaurantId;

	private DineOnRestaurantActivity thisResActivity;


	/**
	 * Updates the UI based on the state of this activity.
	 */
	protected void updateUI() {

		// Lets invalidate the options menu so it shows the correct 
		// buttons
		invalidateOptionsMenu();

		// TODO  Initialize the UI based on the state of the application
		// ...
	}

	/*
	 * FragmentActivity specific methods
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSatellite = new RestaurantSatellite();

		thisResActivity = this;

		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		// Lets first check if the activity is being recreated after being
		// destroyed but there was an already existing restuarant
		if (savedInstanceState != null && savedInstanceState.getString(
				DineOnConstants.KEY_RESTAURANT) != null) { 
			// Activity recreated
			mRestaurantId = savedInstanceState.getString(
					DineOnConstants.KEY_RESTAURANT);
		} 
		else if (extras != null && extras.getString(
				DineOnConstants.KEY_RESTAURANT) != null) {
			// Activity started and created for the first time
			// Valid extras were passed into this
			mRestaurantId = extras.getString(
					DineOnConstants.KEY_RESTAURANT);
		}

		if (mRestaurantId == null) {
			Utility.getGeneralAlertDialog("Uh OH!", "Doesn't look like your logged in"
					, this).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// We need to download the restaurant before registering the receiver
		// Hopefully its fast
		ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.getInBackground(mRestaurantId, new GetCallback() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					try {
						mRestaurant = new Restaurant(object);
					} catch (Exception e1) {
						return;
					}
					mSatellite.register(mRestaurant, thisResActivity);
				} else {
					Utility.getBackToLoginAlertDialog(
							thisResActivity, RestaurantLoginActivity.class).show();
				}
			}
		});
		updateUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSatellite.unRegister();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy 
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_restaurant_profile:
			startProfileActivity();
			break;
		case R.id.item_logout:
			if (mRestaurant != null) {
				mRestaurant.saveEventually(null);
			}
			// TODO Notify Users that Restaurant is closing
			ParseUser.logOut();
			startLoginActivity();
			break;
		default:
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Depending on the state of the current application
		// Adjust what is presented to the user		
		if (!isLoggedIn()) {
			setMenuToNonUser(menu);
		}
		return true;
	}

	/**
	 * Given a menu set set this menu to show.
	 * that the user is not logged in
	 * @param menu to display
	 */
	private void setMenuToNonUser(Menu menu) {
		MenuItem itemProfile = menu.findItem(R.id.item_restaurant_profile);
		if (itemProfile != null) {
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
		}
		MenuItem itemLogout = menu.findItem(R.id.item_logout);
		if (itemLogout != null) {
			itemLogout.setEnabled(false);
			itemLogout.setVisible(false);
		}

		// Add a ability to log in
		MenuItem item = menu.add("Log in");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startLoginActivity();
				return false;
			}
		});
	}

	/**
	 * Start log in activity. 
	 */
	public void startLoginActivity() {
		Intent i = new Intent(this, RestaurantLoginActivity.class);
		startActivity(i);
	}
	
	/**
	 * Starts the activity that lets the user look at the restaurant profile.
	 */
	public void startProfileActivity() {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivity(i);
	}
	
	@Override
	public void startActivity(Intent intent) {
		if (mRestaurant != null) {
			intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant.getObjId());
		}
		super.startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Place the correct Key for the restaurant
		if (mRestaurant != null) {
			savedInstanceState.putString(DineOnConstants.KEY_RESTAURANT, mRestaurant.getObjId());
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Returns the reference to the current Restaurant object associated with this user.
	 * 
	 * @return restaurant associated with this
	 */
	protected Restaurant getRestaurant() {
		return mRestaurant;
	}

	/**
	 * Returns whether the user is logged in.
	 * This function can be used to determine the state
	 * of the application.
	 * @return whether a user is logged in
	 */
	protected boolean isLoggedIn() {
		// TODO Sync with Parse User to ensure
		// That the user is logged in via Parse
		// Then check if we have a associated restaurant
		if (mRestaurant != null) {
			return true;
		}
		Log.w(TAG, "Restaurant instance associated with this user is null");
		return false;
	}
	
	////////////////////////////////////////////////
	/////  Satellite Calls 
	/////////////////////////////////////////////////

	/**
	 * Notifies all the current Customers that 
	 * a change in the state of this restaurant 
	 * has changed.  
	 */
	protected void notifyAllUsersOfRestaurantChange() {
		if (mRestaurant == null) {
			Log.e(TAG, "Restaurant is null while attempting to use the satellite");
			return;
		}
		// For every User that is currently in the restaurant
		//  That is get all the Users for all the active dining sessions
		
	}
	
	////////////////////////////////////////////////
	/////  Satelite Listener Callbacks
	/////////////////////////////////////////////////
	
	@Override
	public void onFail(String message) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onFail" + message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUserCheckedIn(UserInfo user, int tableID) {
		final DiningSession DS = new DiningSession(tableID, user);
		DS.saveInBackGround(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					mSatellite.confirmDiningSession(DS);
				} else {
					Log.e(TAG, "unable to confirm dining session: " + e.getMessage());
				}
			}
		});
		Toast.makeText(this, "onUserCheckedIn", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUserChanged(UserInfo user) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onUserChanged", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onOrderRequest(Order order, String sessionID) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onOrderRequest", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCustomerRequest(CustomerRequest request, String sessionID) {
		Context context = getApplicationContext();
		CharSequence text = "Request recieved!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onCheckedOut(DiningSession session) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onCheckedOut", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onReservationRequest(Date date) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onReservationRequest", Toast.LENGTH_SHORT).show();
	}
}
