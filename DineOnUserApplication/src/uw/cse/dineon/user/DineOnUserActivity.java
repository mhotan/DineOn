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
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.user.UserSatellite.SatelliteListener;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import uw.cse.dineon.user.checkin.IntentResult;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.general.UserPreferencesActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.content.Context;
import android.content.Intent;
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
import com.parse.SaveCallback;

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
	
	private String mUserId;
	private UserSatellite mSat;
	
	private DineOnUserActivity This;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		This = this;
		
		mSat = new UserSatellite();

		// Check two cases
		// 1. This activity is being created for the first time
		// 2. This activity is being restored

		// 1. 
		Bundle extras = getIntent() == null ? null : getIntent().getExtras();
		
		if (extras != null) {
			mUserId = extras.getString(DineOnConstants.KEY_USER);
		} // 2.  
		else if (savedInstanceState.containsKey(DineOnConstants.KEY_USER)) {
			mUserId = savedInstanceState.getString(DineOnConstants.KEY_USER);
		} else {
			Log.e(TAG, "Unable to retrieve user instance");
			return;
		}

		// Get the latest copy of this user instance
		
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
		
		ParseQuery query = new ParseQuery(DineOnUser.class.getSimpleName());
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.getInBackground(mUserId, new GetCallback() {
			
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					try{
						// Success
						mUser = new DineOnUser(object);
						mSat.register(mUser, This);
					}catch(Exception e1){
						Log.d(TAG, e1.getMessage());
					}
				} else { 
					Utility.getBackToLoginAlertDialog(This, UserLoginActivity.class).show();
				}
				intializeUI();
			}
		});
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
		if (intent == null) return;
		IntentResult scanResult = 
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
			  // handle scan result
			  try {
				  String contents = scanResult.getContents();
				  JSONObject data;
				
				  data = new JSONObject(contents);
				  if(data.has(DineOnConstants.KEY_RESTAURANT) && data.has(DineOnConstants.TABLE_NUM)){
					  mSat.requestCheckIn(mUser.getUserInfo(), data.getInt(DineOnConstants.TABLE_NUM), 
							  data.getString(DineOnConstants.KEY_RESTAURANT));
				  }
				  
			  } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			  //Log.d("ZXing", data.toString());
			  
				  
		  }
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
	
	@Override
	public void onFail(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitialDiningSessionReceived(DiningSession session) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dining Session Started", Toast.LENGTH_SHORT).show();

		// DEBUG:
		Log.d("GOT_DINING_SESSION_FROM_CLOUD", session.getTableID() + "");

		mUser.setDiningSession(session);
		mUser.saveInBackGround(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if (e == null)
					intializeUI();
				else
					Log.e(TAG, "unable to save the updated dineon user " + 
					      "after new dining session received.");
			}
		});

	}

	@Override
	public void onRestaurantInfoChanged(RestaurantInfo restaurant) {
		// TODO Auto-generated method stub
		
	}
}