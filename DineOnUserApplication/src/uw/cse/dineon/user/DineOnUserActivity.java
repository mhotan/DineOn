package uw.cse.dineon.user;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.user.UserSatellite.SatelliteListener;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import uw.cse.dineon.user.checkin.QRCheckin;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.general.UserPreferencesActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * General Fragment Activity class that pertains to a specific user.
 * Once the User is logged in then they are allowed specific access to
 * different regions of the application.
 *
 * In Particular their user specific preferences
 * @author mhotan
 */
public class DineOnUserActivity extends FragmentActivity implements SatelliteListener {

	private static final String TAG = DineOnUserActivity.class.getSimpleName();

	/**
	 * The associated user .
	 */
	protected DineOnUser mUser;	
	private DiningSessionResponseReceiver mDSResponseReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDSResponseReceiver = new DiningSessionResponseReceiver(ParseUser.getCurrentUser());

		// Check two cases
		// 1. This activity is being created for the first time
		// 2. This activity is being restored

		// 1. 
		Bundle extras = getIntent() == null ? null : getIntent().getExtras();
		String userId;
		if (extras != null) {
			userId = extras.getString(DineOnConstants.KEY_USER);
		} // 2.  
		else if (savedInstanceState.containsKey(DineOnConstants.KEY_USER)) {
			userId = savedInstanceState.getString(DineOnConstants.KEY_USER);
		} else {
			Log.e(TAG, "Unable to retrieve user instance");
			return;
		}

		// Get the latest copy of this user instance
		ParseQuery query = new ParseQuery(DineOnUser.class.getSimpleName());
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.getInBackground(userId, new InitializeCallback(this));
	}

	/**
	 * This automates the addition of the User Intent.
	 * Should never be called when mUser is null.
	 * @param intent Intent
	 */
	@Override
	public void startActivity(Intent intent) {
		if (DineOnConstants.DEBUG && mUser == null) {
			// TODO change to Dialog box
			Toast.makeText(this, "Need to create or download a User", Toast.LENGTH_SHORT).show();
			return;
		}
		// Adds the USer object id
		if (mUser != null) {
			intent.putExtra(DineOnConstants.KEY_USER, mUser.getObjId());
		}
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
		mDSResponseReceiver.register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mDSResponseReceiver.unRegister(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		QRCheckin.QRResult(requestCode, resultCode, intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		//Hides the 
		final MenuItem ITEM = menu.findItem(R.id.option_bill);
		ITEM.setEnabled(false);
		ITEM.setVisible(false);

		final Menu M = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		List<MenuItem> customActionBarButtons = new ArrayList<MenuItem>();
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

		// Remove this activity from the back stack
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Making this null makes sure there is no 
		// data leakage to the login page
		mUser = null;
		startActivity(i);
	}

	/**
	 * Creates the onClick listeners for the specified menu items.
	 * 
	 * @param m the parent menu
	 * @param items the list of MenuItems to create listeners for
	 */
	private void setOnClick(final Menu m, List<MenuItem> items) {
		for (final MenuItem ITEM : items) {
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
	public boolean onPrepareOptionsMenu(Menu menu) {

		// This is for the case where nothing is updated yet
		// There is no User class
		if (mUser == null) {
			disableMenuItem(menu, R.id.option_check_in);
			disableMenuItem(menu, R.id.option_bill);
			return true;
		}

		if(mUser.getDiningSession() != null) {
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
	private void disableMenuItem(Menu menu, int rID) {
		MenuItem item = menu.findItem(rID);
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
	private void enableMenuItem(Menu menu, int rID) {
		MenuItem item = menu.findItem(rID);
		if(item == null) {
			menu.add(rID);
		}
		item.setEnabled(true);
		item.setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		}
		if (i != null) {
			startActivity(i);
		}
		return true;
	}

	/**
	 * Callback for retrieving the User object from the cache
	 * or the network.  After this succeeds we will then continue to 
	 * Intialize to UI
	 * @author mhotan
	 */
	private class InitializeCallback extends GetCallback {

		private final Context mContext;

		/**
		 * Creates a callback to handle queries.
		 * Specically handle downloading a UserObject
		 * @param ctx Context to set
		 */
		public InitializeCallback(Context ctx) {
			mContext = ctx;
		}

		@Override
		public void done(ParseObject object, ParseException e) {
			if (e == null) {
				// We have found the correct object
				try {
					mUser = new DineOnUser(object);
				} catch (Exception e1) {
					Log.e(TAG, "Exception on Fetch:" + e1);
				}
				Toast.makeText(mContext, "User found time to initialize", 
						Toast.LENGTH_SHORT).show();

				// User was found update the UI
				intializeUI();
				
			} else {
				Toast.makeText(mContext, "FAIL: Unable to find user", 
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * This is the defined call back method for when the
	 * User attempts to check in to a restaurant.
	 * @param jobj JSONOBJECT used to check
	 * 	DineOnConstants.OBJ_ID => Parse Object ID for Dining Session
	 */
	public void onCheckInCallback(JSONObject jobj) {
		try {
			Log.d("CONFIRM_DINING_SESSION_FROM_REST", "");

			// Use Utility to call Parse and get the Dining Session instance
			if (jobj == null || !jobj.has(DineOnConstants.OBJ_ID)) {
				Log.d(TAG, "The receiver did not return a valid response for checkin.");
				// TODO Update the UI
				Toast.makeText(this, "No DiningSession returned", Toast.LENGTH_SHORT).show();
			}
			String objId = jobj.getString(DineOnConstants.OBJ_ID);
			Map<String, String> attr = new HashMap<String, String>();
			attr.put(DineOnConstants.OBJ_ID, objId);

			// Then Bundle the Dining Session Instance into		
			Method m = DineOnUserActivity.class.getMethod("onDiningSessionRecievedCallback",
					List.class);

			ParseUtil.getDataFromCloud(this, DiningSession.class, m, attr);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "Failed to invocate method onDiningSessionRecievedCallback()");
		} catch (JSONException e) {
			Log.e(TAG, "JSON error in checkin callback");
		}

	}

	/**
	 * This is a callback for when the Dining Session is recieved via local
	 * caching or.
	 * @param list List<Storable>
	 */
	public void onDiningSessionRecievedCallback(List<Storable> list) {
		// Assert that the first item in the list is
		// is a DiningSession
		if (list == null || list.size() != 1) {
			throw new IllegalArgumentException("List returned is not valid: " + list);
		}
		DiningSession mDiningSession = (DiningSession) list.get(0);

		Toast.makeText(this, "Dining Session Started", Toast.LENGTH_SHORT).show();

		// DEBUG:
		Log.d("GOT_DINING_SESSION_FROM_CLOUD", mDiningSession.getTableID() + "");

		mUser.setDiningSession(mDiningSession);

		invalidateOptionsMenu();

		// TODO Extract channel for push
		// Shouldn't we already have the channel and be registered at this point 
		// via onCreate/onResume?
		// TODO Register for the channel and start listening for updates
		// TODO Extract object id for restaurant

		// Bundle up dining session
		// Start RestaurantMainActivity with bundle
		Intent i = new Intent(this, RestaurantHomeActivity.class);
		ArrayList<Storable> mDSList = new ArrayList<Storable>();
		mDSList.add(mDiningSession);
		//		i.putParcelableArrayListExtra(DineOnConstants.DINING_SESSION, mDSList);
		startActivity(i);
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
		if (mUser != null) {
			savedInstanceState.putString(DineOnConstants.KEY_USER, mUser.getObjId());
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

	private static final String ACTION = "uw.cse.dineon.user.CONFIRM_DINING_SESSION";

	/**
	 * Handles the result of requesting a Dining session.
	 * @author mhotan
	 *
	 */
	private class DiningSessionResponseReceiver extends BroadcastReceiver {

		private final ParseUser mParseUser;
		private final IntentFilter mIF;
		private final String mUserChannel;
		private DineOnUserActivity mCurrentActivity;

		private String mRestaurantSessionChannel;

		/**
		 * Constructs a receiver from information stored in a ParseUser.
		 * @param user ParseUser to construct from
		 */
		public DiningSessionResponseReceiver(ParseUser user) {
			mIF = new IntentFilter(ACTION);
			// TODO Add dining sessions
			mParseUser = user;
			mUserChannel = "uw_cse_dineon_" + mParseUser.getUsername();
			mRestaurantSessionChannel = null;

		}

		/**
		 * Validates this receiver.
		 * @param dineOnUserActivity to register.
		 */
		public void register(DineOnUserActivity dineOnUserActivity) {
			mCurrentActivity = dineOnUserActivity;
			dineOnUserActivity.registerReceiver(this, mIF);
			PushService.subscribe(dineOnUserActivity, mUserChannel, dineOnUserActivity.getClass());
		}

		/**
		 * Invalidates this receiver.
		 * @param dineOnUserActivity to unregister.
		 */
		public void unRegister(DineOnUserActivity dineOnUserActivity) {
			dineOnUserActivity.unregisterReceiver(this);
			PushService.unsubscribe(dineOnUserActivity, mUserChannel);
			mCurrentActivity = null;
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			String theirChannel = intent.getExtras() == null ? null 
					: intent.getExtras().getString("com.parse.Channel");

			if (theirChannel == null || mCurrentActivity == null) {
				return;
			}

			if (theirChannel.equals(mUserChannel)) {
				try {
					JSONObject jobj = 
							new JSONObject(intent.getExtras().getString("com.parse.Data"));
					mCurrentActivity.onCheckInCallback(jobj);

				} 
				catch (JSONException e) {
					Log.d(TAG, "JSONException: " + e.getMessage());
				} 

			} 
			else if (mRestaurantSessionChannel != null 
					&& mRestaurantSessionChannel.equals(theirChannel)) {
				// TODO Do something here that updates the state of the current Dining Session 

			}
		}

	}

	@Override
	public void onFail(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitialDiningSessionReceived(DiningSession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestaurantInfoChanged(RestaurantInfo restaurant) {
		// TODO Auto-generated method stub
		
	}
}