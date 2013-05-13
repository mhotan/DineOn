package uw.cse.dineon.restaurant;

import java.util.Date;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.RestaurantDownloader.RestaurantDownLoaderCallback;
import uw.cse.dineon.restaurant.RestaurantSatellite.SateliteListener;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.app.ProgressDialog;
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

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Satellite to communicate through.
	 */
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
	 * This is a very important call that serves as a notification 
	 * that the state of the Restaurant has changed.
	 * Updates the UI based on the state of this activity.
	 */
	protected void updateUI() {
		// Lets invalidate the options menu so it shows the correct buttons
		// Destroy any progress dailog if it exists
		destroyProgressDialog();
		invalidateOptionsMenu();

		// TODO  Initialize the UI based on the state of the application
		// ...
	}

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
		RestaurantDownloader downloader = new CustomRestaurantDownloader(
				mRestaurantId, new RestaurantDownLoaderCallback() {

					@Override
					public void onFailToDownLoadRestaurant(String message) {
						Utility.getBackToLoginAlertDialog(
								thisResActivity,
								message,
								RestaurantLoginActivity.class).show();
					}

					@Override
					public void onDownloadedRestaurant(Restaurant rest) {
						if (rest != null) {
							mRestaurant = rest;
							mSatellite.register(mRestaurant, thisResActivity);
							updateUI(); // This is the call that should trigger a lot of UI changes.
						}
					}
				});
		downloader.execute(CachePolicy.CACHE_ELSE_NETWORK);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSatellite.unRegister();
	}

	/**
	 * Returns the reference to the current Restaurant object associated with this user.
	 * 
	 * @return restaurant associated with this, or null if restaurant has not uploaded yet.
	 */
	protected Restaurant getRestaurant() {
		return mRestaurant;
	}

	/**
	 * Notifies all the users that a Change in this restaurant has changed.
	 */
	protected void notifyAllRestaurantChange() {
		for (DiningSession session: mRestaurant.getSessions()) {
			notifyGroupRestaurantChange(session);
		}
	}
	
	/**
	 * Notifies all the groups of the Dining Session that 
	 * a change has occured.
	 * @param session Dining Session that includes users to notify.
	 */
	protected void notifyGroupRestaurantChange(DiningSession session) {
		for (UserInfo user: session.getUsers()) {
			mSatellite.notifyChangeRestaurantInfo(mRestaurant.getInfo(), user);
		}
	}
	
	/**
	 * Adds a dining session to the restaurant.
	 * @param session Dining Session to add
	 */
	protected void addDiningSession(DiningSession session) {
		mRestaurant.addDiningSession(session);
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Adds a dining session to the restaurant.
	 * @param session Dining Session to add
	 */
	protected void removeDiningSession(DiningSession session) {
		mRestaurant.removeDiningSession(session);
		mRestaurant.saveInBackGround(null);
	}

	/**
	 * Adds an Order to the state of this restaurant.
	 * @param order Order that is being added to the restaurant.
	 */
	protected void addOrder(Order order) {
		// Add the order to this restaurant.
		mRestaurant.addOrder(order);
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Adds a dining session to the restaurant.
	 * @param order Order that was completed
	 */
	protected void completeOrder(Order order) {
		mRestaurant.completeOrder(order);
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Adds a reservation to the state of this restaurant.
	 * @param reservation reservation that is being added to the restaurant.
	 */
	protected void addReservation(Reservation reservation) {
		// Add the order to this restaurant.
		mRestaurant.addReservation(reservation);
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Removes the reservation from this restaurant.
	 * @param reservation resrvation to remove.
	 */
	protected void removeReservation(Reservation reservation) {
		mRestaurant.removeReservation(reservation);
		mRestaurant.saveInBackGround(null);
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
					// Notify the user that the we have satisfied there request
					mSatellite.confirmDiningSession(DS);
					// Adds the dining session to the restaurant
					addDiningSession(DS);
				} else {
					Log.e(TAG, "unable to confirm dining session: " + e.getMessage());
				}
			}
		});
		Toast.makeText(this, "onUserCheckedIn", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUserChanged(UserInfo user) {
		Toast.makeText(this, "onUserChanged", Toast.LENGTH_SHORT).show();

		if (mRestaurant == null) {
			Log.e(TAG, "Null Restaurant when accepting user change");
			// TODO What do we do in this case queue the request ???
			return;
		}

		// TODO Update the current restaurant
		mRestaurant.updateUser(user);

		// Save the changes and notify user
		mRestaurant.saveInBackGround(null);
	}

	@Override
	public void onOrderRequest(final Order order, String sessionID) {
		Toast.makeText(this, "onOrderRequest", Toast.LENGTH_SHORT).show();

		if (mRestaurant == null) {
			Log.e(TAG, "Null Restaurant when accepting order");
			// TODO What do we do in this case queue the request ???
			return;
		}

		// TODO Validate Order

		for (final DiningSession SESSION: mRestaurant.getSessions()) {
			if (SESSION.getObjId().equals(sessionID)) {
				// Found the correct session.
				// Add the Order to the session
				SESSION.addPendingOrder(order);
				SESSION.saveInBackGround(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							mSatellite.confirmOrder(SESSION, order);
						} else {
							Log.e(TAG, "Error saving dining session" + e.getMessage());
						}
					}
				});
				// We are done there can be no duplicate
				break;
			}
		}
	}

	@Override
	public void onCustomerRequest(final CustomerRequest request, String sessionID) {
		Toast.makeText(this, "onOrderRequest", Toast.LENGTH_SHORT).show();

		if (mRestaurant == null) {
			Log.e(TAG, "Null Restaurant when accepting customer request.");
			return;
		}

		// TODO Validate Request

		for (final DiningSession SESSION: mRestaurant.getSessions()) {
			if (SESSION.getObjId().equals(sessionID)) {
				// Found the correct session.
				// Add the Order to the session
				SESSION.addRequest(request);
				SESSION.saveInBackGround(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							mSatellite.confirmCustomerRequest(SESSION, request);
						} else {
							Log.e(TAG, "Error saving dining session" + e.getMessage());
						}
					}
				});
				// We are done there can be no duplicate
				break;
			}
		}
	}
	
	@Override
	public void onReservationRequest(Reservation reservation) {
		Toast.makeText(this, "onReservationRequest", Toast.LENGTH_SHORT).show();
		
		if (mRestaurant == null) {
			Log.e(TAG, "Null Restaurant when accepting customer request.");
			return;
		}

		// TODO Validate Reservation
		
		mSatellite.confirmReservation(reservation.getUserInfo(), reservation);
	
		mRestaurant.addReservation(reservation);
	}

	@Override
	public void onCheckedOut(DiningSession session) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onCheckedOut", Toast.LENGTH_SHORT).show();
		
		// All we do is call the 
		removeDiningSession(session);
	}

	////////////////////////////////////////////////
	/////  Restaurant Downloader
	/////////////////////////////////////////////////

	/**
	 * Downloads restaurants and then sets the class member to currect reference.
	 * @author mhotan
	 */
	private class CustomRestaurantDownloader extends RestaurantDownloader {

		/**
		 * Downloads restaurant.
		 * @param id Parse object ID of restaurant to download.
		 * @param callback Callback to use 
		 */
		public CustomRestaurantDownloader(String id, 
				RestaurantDownLoaderCallback callback) {
			super(id, callback);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			createProgressDialog();
		}

		@Override
		protected void onPostExecute(Restaurant result) {
			destroyProgressDialog();
			super.onPostExecute(result);
		}

	}

	////////////////////////////////////////////////
	/////  Establish Menu
	/////////////////////////////////////////////////	

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

	// //////////////////////////////////////////////////////////////////////
	// /// UI Specific methods
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	protected void createProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(R.string.dialog_title_loggin_in);
		mProgressDialog.setMessage("Getting you your own restaurant");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	protected void destroyProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}
