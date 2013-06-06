package uw.cse.dineon.restaurant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.android.DineOnStandardActivity;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.RestaurantSatellite.SateliteListener;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
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
//	private RestaurantLocationListener mLocationListener;


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
			Utility.getGeneralAlertDialog(getString(R.string.uh_oh), 
					getString(R.string.not_logged_in)
					, this).show();
		}

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
	 * Removes dining session from restaurant.
	 * @param session Dining Session to add
	 */
	protected void removeDiningSession(DiningSession session) {
		
		// We have to iterate through all the orders pertaining to
		// this dining session and remove it from the pending list
		removeCustomerRequests(session.getRequests());
		
		// Cancel any pending orders for the restaurant.
		for (Order pendingOrder: session.getOrders()) {
			mRestaurant.cancelPendingOrder(pendingOrder);
		}		
		
		// Finally remove the diningsession
		mRestaurant.removeDiningSession(session);
		session.deleteFromCloud();
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Returns a list of sessions.
	 * @return a list of sessions
	 */
	public List<DiningSession> getCurrentSessions() {
		if (mRestaurant == null) {
			mRestaurant = DineOnRestaurantApplication.getRestaurant();
		}
		
		return mRestaurant.getSessions();
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
	 * Returns a list of pending orders.
	 * @return a list of pending orders
	 */
	protected List<Order> getPendingOrders() {
		if (mRestaurant == null) {
			mRestaurant = DineOnRestaurantApplication.getRestaurant();
		}
		
		return mRestaurant.getPendingOrders();
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
	 * Attempts to remove the requests from the restaurant.
	 * @param requests Requests to remove from Restaurant
	 */
	private void removeCustomerRequests(Collection<CustomerRequest> requests) {
		for (CustomerRequest request: requests) {
			mRestaurant.removeCustomerRequest(request);
		}
		mRestaurant.saveInBackGround(null);
	}

	/**
	 * Removes the request from the restaurant pending request record.
	 * 
	 * Any class that overrides this method should call super.removeCustomerRequest
	 * at the end of the function block.
	 * 
	 * @param request Request to delete.
	 */
	protected void removeCustomerRequest(CustomerRequest request) {
		// Remove the customer request from the 
		// restaurant permanently.
		mRestaurant.removeCustomerRequest(request);
		mRestaurant.saveInBackGround(null);
	}
	
	/**
	 * Returns a list of customer requests.
	 * @return a list of customer requests
	 */
	protected List<CustomerRequest> getCurrentRequests() {
		if (mRestaurant == null) {
			mRestaurant = DineOnRestaurantApplication.getRestaurant();
		}
		
		return mRestaurant.getCustomerRequests();
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
		Toast.makeText(this, getString(R.string.failed) + message, Toast.LENGTH_LONG).show();
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
					Log.e(TAG, getString(R.string.unable_to_confirm_dining_session)
							+ e.getMessage());
				}
			}
		});
		Toast.makeText(this, getString(R.string.checked_in), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUserChanged(UserInfo user) {
		Toast.makeText(this, getString(R.string.user_changed), Toast.LENGTH_SHORT).show();

		if (mRestaurant == null) {
			Log.e(TAG, getString(R.string.null_restaurant));
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
		Toast.makeText(this, getString(R.string.order_request), Toast.LENGTH_SHORT).show();

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
							Log.e(TAG, getString(R.string.error_saving_dining_session) 
									+ e.getMessage());
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
		Toast.makeText(this, getString(R.string.order_request), Toast.LENGTH_SHORT).show();

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
							Log.e(TAG, getString(R.string.error_saving_dining_session) 
									+ e.getMessage());
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
		Toast.makeText(this, getString(R.string.reservation_request), Toast.LENGTH_SHORT).show();

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
		Toast.makeText(this, getString(R.string.checked_out), Toast.LENGTH_SHORT).show();

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
		final android.view.Menu M = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		List<android.view.MenuItem> customActionBarButtons = new ArrayList<android.view.MenuItem>();
		customActionBarButtons.add(menu.findItem(R.id.item_restaurant_menu));
		setOnClick(M, customActionBarButtons);
		return true;
	}

	/**
	 * Creates the onClick listeners for the specified menu items.
	 * 
	 * @param m the parent menu
	 * @param items the list of MenuItems to create listeners for
	 */
	private void setOnClick(final android.view.Menu m, List<android.view.MenuItem> items) {
		for (final android.view.MenuItem ITEM : items) {
			ITEM.getActionView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {   
					m.performIdentifierAction(ITEM.getItemId(), 0);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_restaurant_profile:
			startProfileActivity();
			return true;
		case R.id.item_logout:
			if (mRestaurant != null) {
				createProgressDialog(true, getString(R.string.saving), 
						getString(R.string.logging_out));
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
		MenuItem item = menu.add(getString(R.string.login));
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
	
//	/**
//	 * Listener for getting restaurant location at creation time.
//	 * @author mtrathjen08
//	 *
//	 */
//	private class RestaurantLocationListener implements android.location.LocationListener {
//
//		/**
//		 * Location Manager for location services.
//		 */
//		private LocationManager mLocationManager;
//
//		/**
//		 * Last received location from mananger. Initially null.
//		 */
//		private Location mLocation;
//
//		/**
//		 * Constructor for the location listener.
//		 */
//		public RestaurantLocationListener() {
//			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			this.mLocation = null;
//		}
//
//		/**
//		 * Return the last recorder location of the user. Null if no update.
//		 * @return last recorder location.
//		 */
//		Location getLastLocation() {
//			return this.mLocation;
//			// TODO add support for gps
//		}
//
//		/**
//		 * Request a location reading from the Location Manager.
//		 */
//		private void requestLocationUpdate() {
//			this.mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
//			// TODO add support for gps
//		}
//
//		@Override
//		public void onLocationChanged(Location loc) {
//			this.mLocation = loc;
//		}
//
//		@Override
//		public void onProviderDisabled(String arg0) { 
//			// Do nothing
//		}
//
//		@Override
//		public void onProviderEnabled(String arg0) {
//			// Do nothing
//		}
//
//		@Override
//		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//			// Do nothing
//		}
//	}

}
