package uw.cse.dineon.restaurant;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.RestaurantSatellite.SateliteListener;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
	private Restaurant mRestaurant;

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

	}

	@Override
	public void onUserCheckedIn(UserInfo user, int tableID) {
		// Parameter check if user is null then 
		// Something is wrong if user == null
		// TODO Create a Dining Session
		// 
	}

	@Override
	public void onUserChanged(UserInfo user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOrderPlaced(DiningSession session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCustomerRequest(DiningSession session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedOut(DiningSession session) {
		// TODO Auto-generated method stub

	}

	//	/**
	//	 * Handles the communication to Customers.
	//	 * @author mhotan
	//	 */
	//	private class RestaurantSatelite extends BroadcastReceiver {
	//
	//		/**
	//		 * Restaurant this station is associated to.
	//		 */
	//		private Restaurant mParseRestaurant;
	//
	//		/**
	//		 * Intent filter to control which actions to hold.
	//		 */
	//		private final IntentFilter mIF;
	//
	//		/**
	//		 * The Channel associated with THIS Satelite.
	//		 */
	//		private String mThisChannel;
	//
	//		/**
	//		 * The activity that the broadcast receiver is registers with.
	//		 */
	//		private DineOnRestaurantActivity mCurrentActivity;
	//
	//		/**
	//		 * TODO ???
	//		 */
	//		private String mRestaurantSessionChannel;
	//
	//		public RestaurantSatelite() {
	//			mIF = new IntentFilter();
	//			mIF.addAction(DineOnConstants.ACTION_REQUEST_DINING_SESSION);
	//			mIF.addAction(DineOnConstants.ACTION_ORDER_PLACED);
	//			mIF.addAction(DineOnConstants.ACTION_CHECK_OUT);
	//			mIF.addAction(DineOnConstants.ACTION_CHANGE_USER_INFO);
	//			mRestaurantSessionChannel = null;
	//		}
	//
	//		public void register(Restaurant restaurant, 
	//				DineOnRestaurantActivity dineOnRestaurantActivity){
	//
	//			// Check for null values
	//			if (restaurant == null) {
	//				throw new IllegalArgumentException(
	//						"Null restaurant when registering broadcast receiver");
	//			}
	//			if (dineOnRestaurantActivity == null) {
	//				Log.w(TAG, "RestaurantSatelite attempted to register null activity");
	//				return;
	//			}
	//
	//			// Establish the activity 
	//			mCurrentActivity = dineOnRestaurantActivity;
	//
	//			// Establish a reference to the restaurant
	//			mParseRestaurant = restaurant;
	//
	//			// Establish the channel to make 
	//			mThisChannel = ParseUtil.getChannel(mParseRestaurant);
	//
	//			// Registers this activity to this receiver
	//			mCurrentActivity.registerReceiver(this, mIF);
	//
	//			// Subscribe to my channel so I can hear incoming messages
	//			PushService.subscribe(dineOnRestaurantActivity, 
	//					mThisChannel, 
	//					dineOnRestaurantActivity.getClass());
	//		}
	//
	//		/**
	//		 * Invalidates this receiver.
	//		 */
	//		public void unRegister() {
	//			if (mCurrentActivity == null) {
	//				return;
	//			}
	//
	//			mCurrentActivity.unregisterReceiver(this);
	//			PushService.unsubscribe(mCurrentActivity, mThisChannel);
	//			mCurrentActivity = null;
	//		}
	//
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//
	//			// Extract the channel they were sending to
	//			String theirChannel = intent.getExtras() == null ? null :  
	//				intent.getExtras().getString(DineOnConstants.PARSE_CHANNEL);
	//
	//			// IF they don't have a channel are our activity died
	//			// Then exit this method
	//			if (theirChannel == null || mCurrentActivity == null) {
	//				Log.w(TAG, "[onReceive] Their channel: " + theirChannel 
	//						+ " Our activity: " + mCurrentActivity);
	//				return;
	//			}
	//
	//			// As an assurance we assert their channel is equal to ours
	//			if (theirChannel.equals(mThisChannel)) {
	//				// Extract the 
	//				String id = null;
	//				try {
	//					JSONObject jobj = new JSONObject(
	//							intent.getExtras().getString(DineOnConstants.PARSE_DATA));
	//					id = jobj.getString(DineOnConstants.OBJ_ID);
	//				} 
	//				catch (JSONException e) {
	//					Log.d(TAG, "JSONException: " + e.getMessage());
	//				} 
	//				String action = intent.getAction();
	//				if (DineOnConstants.ACTION_REQUEST_DINING_SESSION.equals(action)) {
	//
	//				} else if (DineOnConstants.ACTION_ORDER_PLACED.equals(action)) {
	//
	//				} else if (DineOnConstants.ACTION_CUSTOMER_REQUEST.equals(action)) {
	//
	//				} else if (DineOnConstants.ACTION_CHECK_OUT.equals(action)) {
	//
	//				} else if (DineOnConstants.ACTION_CHANGE_USER_INFO.equals(action)) {
	//
	//				} else {
	//					Log.w(TAG, "Unknown action received: " + action);
	//				}
	//
	//			} 
	//			else if (mRestaurantSessionChannel != null && 
	//					mRestaurantSessionChannel.equals(theirChannel)) {
	//				// TODO Do something here that updates the state of the current Dining Session 
	//
	//			}
	//		}
	//
	//
	//	}
	//
	//	/**
	//	 * Helper to save/update Restaurant associated with this activity.
	//	 */
	//	private void saveRestaurant() {
	//		try {
	//			ParseUtil.saveDataToCloud(mRestaurant, 
	//					Restaurant.class.getMethod("onSaveInstanceState", 
	//							new Class[]{Bundle.class}));
	//		} catch (NoSuchMethodException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}

	/*
//	 * Brodcast receiver callback specific methods 
//	 ************************************************************************/
	//
	//	// jobj :
	//	//		- userInfo : UserInfo.JSON
	//	//		- tableNum : int
	//	public void onDiningSessionRequest(JSONObject jobj) {
	//		Log.d("GOT_DINING_SESSION_REQUEST", "");
	//		try {
	//			// create a dining session
	//			JSONObject juserInfo = new JSONObject(jobj.getString("userInfo"));
	//			ParseObject user = new ParseObject(UserInfo.class.getSimpleName());
	//			user.setObjectId(juserInfo.getString("objID"));
	//			user.put(UserInfo.NAME,juserInfo.getString(UserInfo.NAME));
	//			user.put(UserInfo.EMAIL, juserInfo.getString(UserInfo.EMAIL));
	//			user.put(UserInfo.PHONE, juserInfo.getString(UserInfo.PHONE));
	//
	//			DiningSession newDS = new DiningSession(
	//					new LinkedList<Order>(), 
	//					0, 
	//					jobj.getInt("tableID"));
	//			UserInfo info = new UserInfo();
	//			info.unpackObject(user);
	//			newDS.addUser(info);
	//			// save to cloud
	//			Method m = DineOnRestaurantActivity.class.getMethod("onSavedDiningSession", 
	//					Boolean.class, String.class, Storable.class); 
	//			ParseUtil.saveDataToCloud(this, newDS, m);
	//
	//		} catch (NoSuchMethodException e) {
	//			// TODO Auto-generated catch block
	//			Log.d(TAG, "Error: " + e.getMessage());
	//			e.printStackTrace();
	//		} catch (Exception e) {
	//			Log.d(TAG, "Error: " + e.getMessage());
	//		}
	//	}

	//	/**
	//	 * Save DiningSession.
	//	 * 
	//	 * @param success Boolean
	//	 * @param objID String ID of obj
	//	 * @param obj Storable object to be saved
	//	 */
	//	public void onSavedDiningSession(Boolean success, String objID, Storable obj) {
	//		Log.d("SAVED_NEW_DINING_SESSION_REST", "");
	//		try {
	//			if (success) {
	//				// push notification for user
	//				DiningSession session = (DiningSession) obj;
	//				Map<String, String> attr = new HashMap<String, String>();
	//				attr.put(DineOnConstants.OBJ_ID, objID);
	//				if(session.getUsers() != null 
	//						&& !session.getUsers().isEmpty()) {
	//					ParseUtil.notifyApplication(
	//							"uw.cse.dineon.user.CONFIRM_DINING_SESSION", 
	//							attr, 
	//							"uw_cse_dineon_" + session.getUsers().get(0).getName());
	//
	//					diningSessionAcquired(session);
	//				} else {
	//					Log.d(TAG, "Error[invalid state]: " 
	//							+ "A dining session saved, but no user associated.");
	//				}
	//			} else {
	//				Log.d(TAG, "Error: A dining session couldn't be saved.");
	//			}
	//		} catch (Exception e) {
	//			Log.d(TAG, "Error: " + e.getMessage());
	//		}
	//	}

}
