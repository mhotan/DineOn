package uw.cse.dineon.restaurant;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnStandardActivity;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.RestaurantSatellite.SateliteListener;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * General Fragment Activity class that pertains to a specific Restaurant
 * client.  Once the Restaurant logged in then they are allowed specific
 * information related to the restaurant
 * @author mhotan
 */
public class DineOnRestaurantActivity extends DineOnStandardActivity 
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
	 * Reference to this activity for inner class listeners.
	 */
	private DineOnRestaurantActivity thisResActivity;

	/**
	 * Location Listener for location based services.
	 */
	private RestaurantLocationListener mLocationListener;


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

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setProgressBarIndeterminateVisibility(true); 

		// Initialize the satellite 
		mSatellite = new RestaurantSatellite();

		// retrieve necessary references.
		thisResActivity = this;
		mRestaurant = DineOnRestaurantApplication.getRestaurant();

		if (mRestaurant == null) {
			Utility.getGeneralAlertDialog("Uh OH!", "Doesn't look like your logged in"
					, this).show();
		}

		this.mLocationListener = new RestaurantLocationListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSatellite.register(mRestaurant, thisResActivity);
		updateUI(); // This is the call that should trigger a lot of UI changes.
	}

	@Override
	protected void onPause() {
		destroyProgressDialog();
		mSatellite.unRegister();
		super.onPause();
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
	 * Adds a customer request to the restaurant.
	 * @param request Customer Request to add
	 */
	protected void addCustomerRequest(CustomerRequest request) {
		// reference our mRestaurant object
		mRestaurant.addCustomerRequest(request);
		mRestaurant.saveInBackGround(null);
	}

	/**
	 * Removes the request from the restaurant pending request record.
	 * @param request Request to delete.
	 */
	protected void removeCustomerRequest(CustomerRequest request) {
		// Remove the customer request from the 
		// restaurant permanently.
		mRestaurant.removeCustomerRequest(request);
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
		// That the user is logged in via Parse
		// Then check if we have a associated restaurant
		return mRestaurant != null;
	}

	////////////////////////////////////////////////
	/////  Satelite Listener Callbacks
	/////////////////////////////////////////////////

	@Override
	public void onFail(String message) {
		Toast.makeText(this, "Failed to do something: " + message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUserCheckedIn(UserInfo user, int tableID) {
		final DiningSession DS = new DiningSession(tableID, user, mRestaurant.getInfo());
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
							// Tell the customer that we have received their order
							mSatellite.confirmOrder(SESSION, order);
							// Add the order to our restaurant
							addOrder(order);
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
							// Tell the customer we have received their request
							mSatellite.confirmCustomerRequest(SESSION, request);
							// Update our state as well
							addCustomerRequest(request);
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

		// We are not updating the dining session
		// because there is no dining session with this reservation.
		addReservation(reservation);
	}

	@Override
	public void onCheckedOut(DiningSession session) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onCheckedOut", Toast.LENGTH_SHORT).show();

		// All we do is call the 
		removeDiningSession(session);
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
			return true;
		case R.id.item_logout:
			if (mRestaurant != null) {
				createProgressDialog(true, "Saving...", "Cleaning up and logging out");
				mRestaurant.saveInBackGround(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						destroyProgressDialog();
						DineOnRestaurantApplication.logOut(thisAct);
						startLoginActivity();
					}
				});
			}
			return true;
		case R.id.item_restaurant_menu:
			startProfileActivity();
			return true;
		default:
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Depending on the state of the current application
		// Adjust what is presented to the user		
		if (!isLoggedIn()) {
			setMenuToNonUser(menu);
		}

		MenuItem progressbar = menu.findItem(R.id.item_progress);
		if (progressbar != null) {
			progressbar.setEnabled(false);
			progressbar.setVisible(false);
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
	 * Clears the back stack so user can't push back to go to their last page.
	 */
	public void startLoginActivity() {
		Intent i = new Intent(this, RestaurantLoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	/**
	 * Starts the activity that lets the user look at the restaurant profile.
	 */
	public void startProfileActivity() {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivity(i);
	}

	// //////////////////////////////////////////////////////////////////////
	// /// UI Specific methods
	// //////////////////////////////////////////////////////////////////////



	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 * @param cancelable Allows the progress dialog to be cancelable.
	 * @param title Title to show in dialog
	 * @param message Message to show in box
	 */
	protected void createProgressDialog(boolean cancelable, String title, String message) {
		if (mProgressDialog != null) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(cancelable);
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

	/**
	 * Listener for getting restaurant location at creation time.
	 * @author mtrathjen08
	 *
	 */
	private class RestaurantLocationListener implements android.location.LocationListener {

		/**
		 * Location Manager for location services.
		 */
		private LocationManager mLocationManager;

		/**
		 * Last received location from mananger. Initially null.
		 */
		private Location mLocation;

		/**
		 * Constructor for the location listener.
		 */
		public RestaurantLocationListener() {
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			this.mLocation = null;
		}

		/**
		 * Return the last recorder location of the user. Null if no update.
		 * @return last recorder location.
		 */
		private Location getLastLocation() {
			return this.mLocation;
			// TODO add support for gps
		}

		/**
		 * Request a location reading from the Location Manager.
		 */
		private void requestLocationUpdate() {
			this.mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
			// TODO add support for gps
		}

		@Override
		public void onLocationChanged(Location loc) {
			this.mLocation = loc;
		}

		@Override
		public void onProviderDisabled(String arg0) { 
			// Do nothing
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// Do nothing
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// Do nothing
		}
	}

}
