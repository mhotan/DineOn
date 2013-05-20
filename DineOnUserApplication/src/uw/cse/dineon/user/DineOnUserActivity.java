package uw.cse.dineon.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.user.UserSatellite.SatelliteListener;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.bill.CurrentOrderFragment.OrderUpdateListener;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import uw.cse.dineon.user.checkin.IntentResult;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.general.UserPreferencesActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurant.home.SubMenuFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
public class DineOnUserActivity extends FragmentActivity implements 
SatelliteListener,
SubMenuFragment.MenuItemListListener, /* manipulation of order from sub menu */
OrderUpdateListener /* manipulation of list from the current order activity */{ 

	private static final String TAG = DineOnUserActivity.class.getSimpleName();

	/**
	 * Satellite for communication.
	 */
	private UserSatellite mSat;

	/**
	 * A self reference.
	 */
	private DineOnUserActivity thisActivity;

	private HashMap<MenuItem, CurrentOrderItem> mMenuItemMappings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisActivity = this;

		mSat = new UserSatellite();

		if (DineOnUserApplication.cachedUser == null) {
			Utility.getBackToLoginAlertDialog(this, 
					"Unable to find your information", UserLoginActivity.class).show();
		}
		
		this.mMenuItemMappings = new HashMap<MenuItem, CurrentOrderItem>();
	}

	/**
	 * This automates the addition of the User Intent.
	 * Should never be called when mUser is null.
	 * @param intent Intent
	 */
	@Override
	public void startActivity(Intent intent) {
		// Adds the User object id
		super.startActivity(intent);
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
		mSat.register(DineOnUserApplication.cachedUser, thisActivity);
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

					mSat.requestCheckIn(DineOnUserApplication.cachedUser.getUserInfo(),
							data.getInt(DineOnConstants.TABLE_NUM), 
							data.getString(DineOnConstants.KEY_RESTAURANT));
				}

			} catch (JSONException e) {
				Log.e(TAG, "JSONException: " + e.getMessage());
			}
			//Log.d("ZXing", data.toString());
		}
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		//Hides the 
		final android.view.MenuItem ITEM = menu.findItem(R.id.option_bill);
		ITEM.setEnabled(false);
		ITEM.setVisible(false);

		final android.view.Menu M = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		List<android.view.MenuItem> customActionBarButtons = new ArrayList<android.view.MenuItem>();
		customActionBarButtons.add(menu.findItem(R.id.option_bill));
		customActionBarButtons.add(menu.findItem(R.id.option_check_in));

		setOnClick(M, customActionBarButtons);

		return true;
	}

	/**
	 * Sends the user back to the login page.
	 */
	public void startLoginActivity() {
		Intent i = new Intent(this, UserLoginActivity.class);

		// Making this null makes sure there is no 
		// data leakage to the login page
		DineOnUserApplication.cachedUser = null;
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

		// This is for the case where nothing is updated yet
		// There is no User class
		if (DineOnUserApplication.cachedUser == null) {
			disableMenuItem(menu, R.id.option_check_in);
			disableMenuItem(menu, R.id.option_bill);
			return true;
		}

		if(DineOnUserApplication.cachedUser.getDiningSession() != null) {
			disableMenuItem(menu, R.id.option_check_in);
			enableMenuItem(menu, R.id.option_bill);
		} 
		else {
			enableMenuItem(menu, R.id.option_check_in);
			disableMenuItem(menu, R.id.option_bill);
		}

		return true;
	}

	/**
	 * Disables and hides the specified menu item.
	 * 
	 * @param menu The specified menu
	 * @param rID The id of the specified menu item
	 */
	private void disableMenuItem(android.view.Menu menu, int rID) {
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
	private void enableMenuItem(android.view.Menu menu, int rID) {
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
			break;
		case R.id.option_settings:
			i = new Intent(getApplicationContext(), UserPreferencesActivity.class);
			break;
		case R.id.option_check_in:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			break;
		case R.id.option_bill:
			i = new Intent(getApplicationContext(), CurrentOrderActivity.class);
			// Count all the elements that the user has currently selected
			startActivityForResult(i, DineOnConstants.REQUEST_VIEW_CURRENT_ORDER);
			break;
		case R.id.option_logout:
			ParseUser.logOut();
			startLoginActivity();
			break;
		default:
			//Unknown
			Log.e(TAG, "None of the specified action items were selected.");
		}
		if (i != null) {
			startActivity(i);
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
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the ID if the user is not null
		if (DineOnUserApplication.cachedUser != null) {
			savedInstanceState
				.putString(DineOnConstants.KEY_USER, DineOnUserApplication.cachedUser.getObjId());
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Restores an instance of a DiningSession from the given Bundle
	 * parameter.
	 *
	 * @param savedInstanceState Bundle that holds session information
	 * 		to be restored.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//		mDiningSession.unbundle(savedInstanceState.getBundle("diningSession"));
	}

	@Override
	public void onFail(String message) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onFail: " + message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInitialDiningSessionReceived(DiningSession session) {
		// TODO Auto-generated method stub

		// DEBUG:
		Log.d("GOT_DINING_SESSION_FROM_CLOUD", session.getTableID() + "");

		final DiningSession M_SESSION = session;
		DineOnUserApplication.cachedUser.setDiningSession(session);
		DineOnUserApplication.cachedUser.saveInBackGround(new SaveCallback() {
			
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

	public void diningSessionChangeActivity(DiningSession dsession){
		Intent i = new Intent(thisActivity, RestaurantHomeActivity.class);
		i.putExtra(DineOnConstants.KEY_DININGSESSION, dsession);
		startActivity(i);
	}
	@Override
	public void onRestaurantInfoChanged(RestaurantInfo restaurant) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onRestaurantInfoChanged", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConfirmOrder(DiningSession ds, String orderId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onConfirmOrder", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConfirmCustomerRequest(DiningSession ds, String requestID) {
		// TODO implement
		Toast.makeText(this, "onConfirmCustomerRequest", Toast.LENGTH_SHORT).show();
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
		mSat.requestCustomerRequest(DineOnUserApplication.cachedUser.getDiningSession(), cr, 
				DineOnUserApplication.cachedUser.getDiningSession().getRestaurantInfo());
		Toast.makeText(this, "Made Request", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMenuItemFocusedOn(uw.cse.dineon.library.MenuItem menuItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestaurantInfoRequested() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewCurrentBill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPlaceOrder(Order order) {
		// TODO Auto-generated method stub
		mSat.requestOrder(DineOnUserApplication.cachedUser.getDiningSession(), 
				order, 
				DineOnUserApplication.cachedUser.getDiningSession().getRestaurantInfo());
	}

	@Override
	public void onIncrementItemOrder(MenuItem item) {
		// TODO Auto-generated method stub
		if (DineOnUserApplication.cachedOrderMapping.containsKey(item)) {
			CurrentOrderItem orderItem = DineOnUserApplication.cachedOrderMapping.get(item);
			orderItem.incrementQuantity();
		} else {
			DineOnUserApplication.cachedOrderMapping.put(item, new CurrentOrderItem(item));
		}
	}

	@Override
	public void onDecrementItemOrder(MenuItem item) {
		// TODO Auto-generated method stub
		if (DineOnUserApplication.cachedOrderMapping.containsKey(item)) {
			CurrentOrderItem orderItem = DineOnUserApplication.cachedOrderMapping.get(item);
			orderItem.decrementQuantity();
		}
	}

	@Override
	public void onRemoveItemFromOrder(MenuItem item) {
		// TODO Auto-generated method stub
		if (DineOnUserApplication.cachedOrderMapping.containsKey(item)) {
			CurrentOrderItem orderItem = DineOnUserApplication.cachedOrderMapping.get(item);
			orderItem.setQuantity(0);
		}
	}
	
	@Override
	public void onMenuItemIncremented(MenuItem item) {
		// TODO Auto-generated method stub
		if (DineOnUserApplication.cachedOrderMapping.containsKey(item)) {
			CurrentOrderItem orderItem = DineOnUserApplication.cachedOrderMapping.get(item);
			orderItem.incrementQuantity();
		} else {
			DineOnUserApplication.cachedOrderMapping.put(item, new CurrentOrderItem(item));
		}
	}

	@Override
	public void onMenuItemDecremented(MenuItem item) {
		// TODO Auto-generated method stub
		if (DineOnUserApplication.cachedOrderMapping.containsKey(item)) {
			CurrentOrderItem orderItem = DineOnUserApplication.cachedOrderMapping.get(item);
			orderItem.decrementQuantity();
		}
	}

	@Override
	public HashMap<MenuItem, CurrentOrderItem> getOrder() {
		// TODO Auto-generated method stub
		return DineOnUserApplication.cachedOrderMapping;
	}

	@Override
	public void resetCurrentOrder() {
		// TODO Auto-generated method stub
		DineOnUserApplication.cachedOrderMapping.clear();
	}
}
