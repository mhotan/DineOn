package uw.cse.dineon.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.android.DineOnStandardActivity;
import uw.cse.dineon.library.checkin.IntentIntegrator;
import uw.cse.dineon.library.checkin.IntentResult;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.user.UserSatellite.SatelliteListener;
import uw.cse.dineon.user.bill.CurrentBillActivity;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * General Fragment Activity class that pertains to a specific user.
 * Once the User is logged in then they are allowed specific access to
 * different regions of the application.
 *
 * In Particular their user specific preferences
 * @author mhotan
 */
public class DineOnUserActivity extends DineOnStandardActivity implements 
SatelliteListener { 
//OrderUpdateListener /* manipulation of list from the current order activity */ {
//SubMenuFragment.MenuItemListListener /* manipulation of order from sub menu */  

	private static final String TAG = DineOnUserActivity.class.getSimpleName();
	
	/**
	 * Satellite for communication.
	 */
	protected UserSatellite mSat;

	/**
	 * A self reference.
	 */
	private DineOnUserActivity This;
	
	private ProgressDialog mProgressDialog;
	
	protected DineOnUser mUser;

	/**
	 * Set this value to the current dining user.
	 * @param savedInstanceState bundle to create activity from
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Creating DineOnUserActivity");
		This = this;

		mSat = new UserSatellite();
		mUser = DineOnUserApplication.getDineOnUser();
		if (mUser == null) {
			Utility.getBackToLoginAlertDialog(this, 
					"Unable to find your information", UserLoginActivity.class).show();
		}
		
		this.mProgressDialog = null;

	}

	/**
	 * @return UserSatellite for this activity
	 */
	public UserSatellite getSat() {
		return this.mSat;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "DineOnUserActivity destroyed");
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleSearchIntent(intent);
	}

	/**
	 * Given an intent where the user request to search something, 
	 * process the query and react accordingly.
	 * 
	 * @param intent intent
	 */
	private void handleSearchIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			this.onSearch(intent.getStringExtra(SearchManager.QUERY));
		}
	}

	/**
	 * Method where sub activities can override to receive specific search request for example.
	 * 
	 * IE the activity can expose a search view and to the user and just react to user request
	 * with this method.
	 * 
	 * @param query Query user is requesting
	 */
	protected void onSearch(String query) {
		// TODO Implement Parse Query
		Log.d(TAG, "User requested a search for " + query);
	}

	/**
	 * A valid user found this allows the ability for the Userinterface to initialize.
	 * Any subclasses of this activity can use this as a sign that the user has been identified
	 */
	protected void intializeUI() {

		// Lets invalidate the options menu so it shows the correct 
		// buttons
		invalidateOptionsMenu();

		// TODO  Initialize the UI based on the state of the application
		// ...
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming DineOnUserActivity");
		mSat.register(DineOnUserApplication.getDineOnUser(), This);
		intializeUI();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSat.unRegister();
		
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) { 
			return;
		}
		IntentResult scanResult = 
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			// handle scan result
			try {
				String contents = scanResult.getContents();
				JSONObject data;

				data = new JSONObject(contents);
				if(data.has(DineOnConstants.KEY_RESTAURANT) 
						&& data.has(DineOnConstants.TABLE_NUM)) {
					
					// block the user until dining session is returned
					createProgressDialog("Checking In...", "Contacting " 
							+ data.getString(DineOnConstants.KEY_RESTAURANT));

					mSat.requestCheckIn(DineOnUserApplication.getUserInfo(),
							data.getInt(DineOnConstants.TABLE_NUM), 
							data.getString(DineOnConstants.KEY_RESTAURANT));
					
					// Add a timeout to dialog
					timerDelayRemoveDialog(DineOnConstants.MAX_RESPONSE_TIME);
				}

			} catch (JSONException e) {
				Log.e(TAG, "JSONException: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Create a progress dialog to notify the user.
	 * @param title title of the dialog
	 * @param message message in the dialog
	 */
	public void createProgressDialog(String title, String message) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);       
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	public void destroyProgressDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	/**
	 * If an error occurs while checking in show error dialog to user.
	 * @param message message to display to the user
	 */
	public void showErrorCheckingInDialog(String message) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Failed to Check In!");
		b.setMessage(message);
		b.setCancelable(true);
		b.show();
	}
	
	/**
	 * Postpones an action.
	 * @param time millisecond time until runner is called
	 * @param runner Runnable instance to execute with allotted time left
	 */
	public void timerDelayRemoveDialog(long time, Runnable runner) {
		new Handler().postDelayed(runner, time);
	}
	
	/**
	 * Create a timeout for the progress dialog.
	 * @param time timeout for progress dialog
	 */
	public void timerDelayRemoveDialog(long time) {
	    new Handler().postDelayed(new Runnable() {
	        public void run() {        
	        	if (mProgressDialog != null && mProgressDialog.isShowing()) {
	        		destroyProgressDialog();
		        	showErrorCheckingInDialog("No response from restaurant. Try again.");
	        	}
	        }
	    }, time); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		//Hides the 
		final android.view.MenuItem ITEM1 = menu.findItem(R.id.option_bill);
		ITEM1.setEnabled(false);
		ITEM1.setVisible(false);

		final android.view.MenuItem ITEM2 = menu.findItem(R.id.option_view_order);
		ITEM2.setEnabled(false);
		ITEM2.setVisible(false);

		final android.view.Menu M = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		List<android.view.MenuItem> customActionBarButtons = new ArrayList<android.view.MenuItem>();
		customActionBarButtons.add(menu.findItem(R.id.option_bill));
		customActionBarButtons.add(menu.findItem(R.id.option_check_in));
		customActionBarButtons.add(menu.findItem(R.id.option_view_order));
		setOnClick(M, customActionBarButtons);

		// Set up search view.
		final SearchView SEARCHVIEW = (SearchView) 
				menu.findItem(R.id.option_search).getActionView();

		// Enable the search widget in the action bar
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			SEARCHVIEW.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		SEARCHVIEW.setIconified(true);
		SEARCHVIEW.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// Make the call to search for a particular restaurant
				onSearch(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) { // Do nothing
				return false;
			}
		});

		return true;
	}

	/**
	 * Sends the user back to the login page.
	 */
	public void startLoginActivity() {
		Intent i = new Intent(this, UserLoginActivity.class);
		this.finish();
		
		// clean up user data
		DineOnUserApplication.setCurrentDiningSession(null);
//		DineOnUserApplication.clearCurrentOrder();
		DineOnUserApplication.clearResaurantList();
		DineOnUserApplication.setRestaurantOfInterest(null);
		DineOnUserApplication.setDineOnUser(null);
				
		Log.d(TAG, "Finishing DineOnUserActivity before logout");
		startActivity(i);
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

	/**
	 * Dynamically prepares the options menu.
	 * @param menu the specified menu to prepare
	 * @return true if the options menu is successfully prepared
	 */
	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {

		SearchView searchView = (SearchView) menu.findItem(R.id.option_search).getActionView();

		// If checked in
		if(DineOnUserApplication.getCurrentDiningSession() != null) {
			
			// Disable the check in button because we are already checked in.
			disableMenuItem(menu, R.id.option_check_in);
			
			// Should be able to view any pending orders.
			if (mUser.hasPendingOrder()) {
				enableMenuItem(menu, R.id.option_view_order);
			} else {
				disableMenuItem(menu, R.id.option_view_order);
			}
				
			// If there is an order to bill
			if (mUser.hasPendingOrder() && mUser.getDiningSession().hasOrders()) {
				enableMenuItem(menu, R.id.option_bill);
			} else {
				disableMenuItem(menu, R.id.option_bill);
			}
			
			// There is a dining session therefore 
			if (searchView != null) {
				searchView.setEnabled(false);
				searchView.setVisibility(View.GONE);
			}
		} 
		else { // If not checked in
			enableMenuItem(menu, R.id.option_check_in);
			enableMenuItem(menu, R.id.option_search);
			disableMenuItem(menu, R.id.option_bill);
			disableMenuItem(menu, R.id.option_view_order);
			if (searchView != null) {
				searchView.setEnabled(true);
				searchView.setVisibility(View.VISIBLE);
			}
		}

		return true;
	}

	/**
	 * Disables and hides the specified menu item.
	 * 
	 * @param menu The specified menu
	 * @param rID The id of the specified menu item
	 */
	protected void disableMenuItem(android.view.Menu menu, int rID) {
		android.view.MenuItem item = menu.findItem(rID);
		if(item != null) {
			item.setEnabled(false);
			item.setVisible(false);
		}
	}

	/**
	 * Enables and shows the specified menu item.
	 * @precondition the rID is an actual valid rID for the menu
	 * item to enable.
	 * @param menu The specified menu
	 * @param rID The id of the specified menu item
	 */
	protected void enableMenuItem(android.view.Menu menu, int rID) {
		android.view.MenuItem item = menu.findItem(rID);
		if(item == null) {
			menu.add(rID);
		}
		item = menu.findItem(rID);
		item.setEnabled(true);
		item.setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.option_profile:
			i = new Intent(getApplicationContext(), ProfileActivity.class);
			startActivity(i);
			break;
		case R.id.option_check_in:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			break;
		case R.id.option_view_order:
			i = new Intent(getApplicationContext(), CurrentOrderActivity.class);
			// Count all the elements that the user has currently selected
			startActivity(i);
			break;
		case R.id.option_bill:
			i = new Intent(getApplicationContext(), CurrentBillActivity.class);
			// Count all the elements that the user has currently selected
			startActivity(i);
			break;
		case R.id.option_logout:
			ParseUser.logOut();
			startLoginActivity();
			break;
		default:
			//Unknown
			Log.e(TAG, "None of the specified action items were selected.");
		}
		return true;
	}

	/**
	 * Saves the instance of the current DiningSession. Information can
	 * be recovered by using onRestoreInstanceState.
	 *
	 * @param savedInstanceState Bundle to store the current
	 * 		activity's data to.
	 */
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the ID if the user is not null
//		if (DineOnUserApplication.getDineOnUser() != null) {
//			savedInstanceState.putString(DineOnConstants.KEY_USER, 
//					DineOnUserApplication.getDineOnUser().getObjId());
//		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onFail(String message) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onFail: " + message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInitialDiningSessionReceived(DiningSession session) {

		// DEBUG:
		Log.d("GOT_DINING_SESSION_FROM_CLOUD", session.getTableID() + "");
		
		// kill the checkin progress dialog
		destroyProgressDialog();

		final DiningSession M_SESSION = session;
		DineOnUserApplication.setCurrentDiningSession(session);
		DineOnUserApplication.getDineOnUser().saveInBackGround(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					//intializeUI();
					// start the restaurant home activity for selected restaurant
					diningSessionChangeActivity(M_SESSION);
				} else {
					Log.e(TAG, "unable to save the updated dineon user " 
							+ "after new dining session received.");
				}
			}
		});

	}

	/**
	 * Got the confirmation for a dining session, so open restaurant's home.
	 * @param dsession new dining session
	 */
	public void diningSessionChangeActivity(DiningSession dsession) {
		Intent i = new Intent(This, RestaurantHomeActivity.class);
		DineOnUserApplication.setCurrentDiningSession(dsession);
		startActivity(i);
	}

	@Override
	public void onRestaurantInfoChanged(RestaurantInfo restaurant) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Restaurant Info Changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConfirmOrder(DiningSession ds, String orderId) {
		Toast.makeText(this, "Order Confirmed", Toast.LENGTH_SHORT).show();
		
		// We have to chech that the order id of the order
		// we are currently possession of as our pending order 
		// is the same as the order returned
		if (mUser.getPendingOrder() == null) {
			Log.e(TAG, "Local pending order is null but still got an order confirmation");
			return;
		} 
		if (!mUser.getPendingOrder().getObjId().equals(orderId)) {
			Log.e(TAG, "Local pending order object ID does not match confirmed id.");
			return;
		} 
		
		// Successfully retrieved
		DineOnUserApplication.setCurrentDiningSession(ds);
	}

	@Override
	public void onConfirmCustomerRequest(DiningSession ds, String requestID) {
		// TODO implement
		Toast.makeText(this, "Your Request was received", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConfirmReservation(Reservation res) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onConfirmReservation", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 * @param cr CustomerRequest to place
	 */
	public void placeRequest(CustomerRequest cr) {
		mSat.requestCustomerRequest(DineOnUserApplication.getCurrentDiningSession(), cr, 
				DineOnUserApplication.getCurrentDiningSession().getRestaurantInfo());
		Toast.makeText(this, "Made Request!", Toast.LENGTH_LONG).show();
	}

	/**
	 * Pay bill for current order.
	 */
	public void payBill() {
		mSat.requestCheckOut(DineOnUserApplication.getCurrentDiningSession(), 
				DineOnUserApplication.getCurrentDiningSession().getRestaurantInfo());

		Toast.makeText(this, "Payment Sent!", Toast.LENGTH_SHORT).show();

		// TODO Need to add a confirmation from restaurant that the user
		// has successfully paid
		DineOnUserApplication.setCurrentDiningSession(null);
	}

//	@Override
//	public void onMenuItemFocusedOn(uw.cse.dineon.library.MenuItem menuItem) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onViewCurrentBill() {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public RestaurantInfo getCurrentRestaurant() {
//		return DineOnUserApplication.getCurrentDiningSession().getRestaurantInfo();
//	}

//	@Override
//	public void onPlaceOrder(Order order) {
//		mSat.requestOrder(DineOnUserApplication.getCurrentDiningSession(), 
//				order, 
//				DineOnUserApplication.getCurrentDiningSession().getRestaurantInfo());
//		DineOnUserApplication.clearCurrentOrder();
//		Toast.makeText(this, "Order Sent!", Toast.LENGTH_SHORT).show();
//	}

//	@Override
//	public void onIncrementItemOrder(MenuItem item) {
//		DineOnUserApplication.incrementItemInCurrentOrder(item);
//	}
//
//	@Override
//	public void onDecrementItemOrder(MenuItem item) {
//		DineOnUserApplication.decrementItemInCurrentOrder(item);
//	}
//
//	@Override
//	public void onRemoveItemFromOrder(MenuItem item) {
//		DineOnUserApplication.removeItemInCurrentOrder(item);
//	}
//
//	@Override
//	public void onMenuItemIncremented(MenuItem item) {
//		DineOnUserApplication.incrementItemInCurrentOrder(item);
//	}
//
//	@Override
//	public void onMenuItemDecremented(MenuItem item) {
//		DineOnUserApplication.decrementItemInCurrentOrder(item);
//	}
//
//	@Override
//	public HashMap<MenuItem, CurrentOrderItem> getOrder() {
//		return DineOnUserApplication.getCurrentOrder();
//	}
//
//	@Override
//	public void resetCurrentOrder() {
//		DineOnUserApplication.clearCurrentOrder();
//	}
//
//	@Override
//	public void doneWithOrder() {
//		// TODO Auto-generated method stub
//	}
}
