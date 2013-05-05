package uw.cse.dineon.restaurant;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.DineOnReceiver;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
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
import android.view.MenuItem.OnMenuItemClickListener;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * General Fragment Activity class that pertains to a specific Restaurant
 * client.  Once the Restaurant logged in then they are allowed specific
 * information related to the restaurant
 * @author mhotan
 */
public class DineOnRestaurantActivity extends FragmentActivity {

	/**
	 * member that defines this restaurant user
	 * This encapsulated this user with this restaurant instance only
	 * Another Activity (like LoginActivity) does the work of 
	 * authenticating and creating an account.
	 * 
	 * Abstract Function
	 * 		mRestaurant != null if and only if this user is logged in with a proper Restaurant account
	 */
	protected static final String TAG = DineOnRestaurantActivity.class.getSimpleName();
	
	private DiningSessionRequestReceiver mDSRequestReceiver;	
	private Restaurant mRestaurant;

	/*
	 * FragmentActivity specific methods
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDSRequestReceiver = new DiningSessionRequestReceiver(ParseUser.getCurrentUser());
		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		Restaurant restaurant;

		if (DineOnConstants.DEBUG) {
			restaurant = DevelopTools.getDefaultRestaurant();
			setRestaurant(restaurant);
		} 
		else { // Regular Mode
			// Lets first check if the activity is being recreated after being
			// destroyed but there was an already existing restuarant
			if (savedInstanceState != null && (restaurant = 
					savedInstanceState.getParcelable(DineOnConstants.KEY_RESTAURANT)) != null) { 
				// Activity recreated
				setRestaurant(restaurant);
			} 
			else if (extras != null && (restaurant = extras.getParcelable(
					DineOnConstants.KEY_RESTAURANT)) != null) {
				// Activity started and created for the first time
				// Valid extras were passed into this
				setRestaurant(restaurant);
			} 
			// else Illegal state
			// No restaurant associated with this
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDSRequestReceiver.register(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDSRequestReceiver.unRegister(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * @param menu to display
	 * @return true
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Adjust dynamic attributes of the menu
		// Depending on the state of the current application
		// Adjust what is presented to the user		
		if (DineOnConstants.DEBUG) {
			return true;
		}else{
			if (!isLoggedIn()){
				setMenuToNonUser(menu);
			}
			return true;
		}
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
			Bundle b = new Bundle();
			b.putParcelable(DineOnConstants.KEY_RESTAURANT, mRestaurant);
			startProfileActivity(null);
			break;
		case R.id.item_logout:
			saveRestaurant();
			ParseUser.logOut();
			break;
		default:

		}
		return true;
	}
	
	/**
	 * Starts profile activity.
	 * 
	 * @param rest current Restaurant for the activity
	 */
	public void startProfileActivity(Restaurant rest) {
		Intent i = new Intent(this, ProfileActivity.class);
		if (rest != null) {
			i.putExtra(DineOnConstants.KEY_RESTAURANT, rest);
		}
		startActivity(i);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Save state of all the fields of this activity
		// mRestaurant
		// Save to Parse then reference later
		savedInstanceState.putParcelable(DineOnConstants.KEY_RESTAURANT, mRestaurant);
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Sets the current state of this restaurant to the input.
	 * @param rest updated restaurant to set as this
	 */
	protected void setRestaurant(Restaurant rest) {
		if (rest == null) {
			String message =  "Illegal Restaurant attempted to be set";
			Log.w(TAG, message);
			throw new IllegalArgumentException(message);
		}	
		mRestaurant = rest;

		// Update the Cloud with the updated restaurant instance
		saveRestaurant();
		// Perform asyncronous save to cloud of the updated state 
		// not very precedent
	}

	/**
	 * Returns the reference to the current Restaurant object associated with this user.
	 * 
	 * @return restaurant associated with this
	 */
	protected Restaurant getRestaurant(){
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

	/**
	 * A new dining sessin has been acquired.
	 * The restaurant must then update its state. 
	 * @param dSess Dining session to update our state with
	 */
	protected static void diningSessionAcquired(DiningSession dSess) {
		// TODO How to 
		// TODO Null check, Log error if null.
		// TODO Update our state
		// TODO Update UI
		
	}

	/*
	 * Brodcast receiver callback specific methods 
	 ************************************************************************/

	// jobj :
	//		- userInfo : UserInfo.JSON
	//		- tableNum : int
	public  void onDiningSessionRequest(JSONObject jobj) {
		Log.d("GOT_DINING_SESSION_REQUEST", "");
		try {
			// create a dining session
			JSONObject juserInfo = new JSONObject(jobj.getString("userInfo"));
			ParseObject user = new ParseObject(UserInfo.class.getSimpleName());
			user.setObjectId(juserInfo.getString("objID"));
			user.put(UserInfo.NAME,juserInfo.getString(UserInfo.NAME));
			user.put(UserInfo.EMAIL, juserInfo.getString(UserInfo.EMAIL));
			user.put(UserInfo.PHONE, juserInfo.getString(UserInfo.PHONE));
	
			DiningSession newDS = new DiningSession(
					new LinkedList<Order>(), 
					0, 
					jobj.getInt("tableID"));
			UserInfo info = new UserInfo();
			info.unpackObject(user);
			newDS.addUser(info);
			// save to cloud
			Method m = DineOnRestaurantActivity.class.getMethod("onSavedDiningSession", Boolean.class, String.class, Storable.class); 
			ParseUtil.saveDataToCloud(this, newDS, m);
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}

	/**
	 * Save DiningSession.
	 * 
	 * @param success Boolean
	 * @param objID String ID of obj
	 * @param obj Storable object to be saved
	 */
	public static void onSavedDiningSession(Boolean success, String objID, Storable obj) {
		Log.d("SAVED_NEW_DINING_SESSION_REST", "");
		try {
		if (success) {
			// push notification for user
			DiningSession session = (DiningSession) obj;
			Map<String, String> attr = new HashMap<String, String>();
			attr.put(DineOnConstants.OBJ_ID, objID);
			if(session.getUsers() != null && 
					!session.getUsers().isEmpty()){
				ParseUtil.notifyApplication(
						"uw.cse.dineon.user.CONFIRM_DINING_SESSION", 
						attr, 
						"uw_cse_dineon_" + session.getUsers().get(0).getName());
			
				diningSessionAcquired(session);
			}
			else{
				Log.d(TAG, "Error[invalid state]: " + 
			"A dining session saved, but no user associated.");
			}
		} else {
			Log.d(TAG, "Error: A dining session couldn't be saved.");
		}
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}
	
	/**
	 * Handles the result of requesting a Dining session
	 * @author mhotan
	 *
	 */
	private class DiningSessionRequestReceiver extends BroadcastReceiver {
		private final String ACTION = "uw.cse.dineon.user.REQUEST_DINING_SESSION";
		private final String CHANNEL_PREFIX = "uw_cse_dineon_";
		private final ParseUser mParseRestaurant;
		private final IntentFilter mIF;
		private final String mRestaurantChannel;
		private DineOnRestaurantActivity mCurrentActivity;
		
		private String mRestaurantSessionChannel;
		
		public DiningSessionRequestReceiver(ParseUser restaurant) {
			mIF = new IntentFilter(ACTION);
			mParseRestaurant = restaurant;
			mRestaurantChannel = CHANNEL_PREFIX 
					+ mParseRestaurant.getUsername();
			mRestaurantSessionChannel = null;
			
		}
		
		public void register(DineOnRestaurantActivity dineOnRestaurantActivity){
			mCurrentActivity = dineOnRestaurantActivity;
			dineOnRestaurantActivity.registerReceiver(this, mIF);
			PushService.subscribe(dineOnRestaurantActivity, 
					mRestaurantChannel, 
					dineOnRestaurantActivity.getClass());
		}
		
		/**
		 * Invalidates this receiver.
		 */
		public void unRegister(DineOnRestaurantActivity dineOnRestaurantActivity) {
			dineOnRestaurantActivity.unregisterReceiver(this);
			PushService.unsubscribe(dineOnRestaurantActivity, 
					mRestaurantChannel);
			mCurrentActivity = null;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String theirChannel = intent.getExtras() == null ? null :  
				intent.getExtras().getString("com.parse.Channel");
			
			if (theirChannel == null || mCurrentActivity == null) {
				return;
			}
			
			if (theirChannel.equals(mRestaurantChannel)) {
				try {
				
					JSONObject jobj = new JSONObject(intent.getExtras().getString("com.parse.Data"));
					mCurrentActivity.onDiningSessionRequest(jobj);
				} 
				catch (JSONException e) {
				      Log.d(TAG, "JSONException: " + e.getMessage());
			    } 
			} 
			else if (mRestaurantSessionChannel != null && 
					mRestaurantSessionChannel.equals(theirChannel)) {
				// TODO Do something here that updates the state of the current Dining Session 
				
			}
		}
		
		
	}

	/**
	 * Helper to save/update Restaurant associated with this activity.
	 */
	private void saveRestaurant() {
		try {
			ParseUtil.saveDataToCloud(mRestaurant, 
					Restaurant.class.getMethod("onSaveInstanceState", 
							new Class[]{Bundle.class}));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
