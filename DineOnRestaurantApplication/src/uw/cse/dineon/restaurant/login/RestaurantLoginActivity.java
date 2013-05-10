package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Login Activity for Restaurant users. Usually sets up the initial
 * 
 * @author mhotan
 */
public class RestaurantLoginActivity extends FragmentActivity implements
LoginFragment.OnLoginListener {

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * This will hold the object ID to a Restaurant object associated to the
	 * User that logs in to this application.
	 */
	private String mRestaurantID;
	
	/**
	 * Reference to this Activity instance for anonymous inner classes.
	 */
	private Context thisCxt;


	// //////////////////////////////////////////////////////////////////////
	// /// Activity specific
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store created activity.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		thisCxt = this;
		// TODO Attempt to get the last ParseUser
	}

	/**
	 * This automates the addition of the User Intent. Should never be called
	 * when mUser is null.
	 * @param intent Intent
	 */
	@Override
	public void startActivity(Intent intent) {
		if (DineOnConstants.DEBUG && mRestaurantID == null) {
			Toast.makeText(this, "Need to create or download a User",
					Toast.LENGTH_SHORT).show();
			return;
		}
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurantID);
		super.startActivity(intent);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actually
		// UI Menu is updated this is done manually
		// See basic_menu under res/menu for ids
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.option_create_new_account) {
			createNewAccount();
		} else if (id == R.id.option_forgot_password) {
			// TODO Implement - Beta Phase?
			DevelopTools.getUnimplementedDialog(this, null).show();
		}
		return true;
	}

	@Override
	public void onLogin(String username, String password) {
		createProgressDialog();

		Resolution unResolution = CredentialValidator.isValidUserName(username);
		Resolution pwResolution = CredentialValidator.isValidPassword(password);

		StringBuffer buf = new StringBuffer();
		if (!unResolution.isValid()) {
			buf.append(unResolution.getMessage() + "\n");
		}
		if (!pwResolution.isValid()) {
			buf.append(pwResolution.getMessage() + "\n");
		}

		if (buf.length() > 0) {
			Utility.getGeneralAlertDialog("Log in failure", buf.toString(), this).show();
			return;
		}
	
		// Log in using asyncronous callback
		ParseUser.logInInBackground(username, password, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {
				
				if (e == null && user != null) {
					// Login successful 
					// because create Restaurant activity always handles first time
					// users we know that this user is returning
					// Therefore there has to be a Restaurant instance in the cloud
					RestaurantFinder callback = new RestaurantFinder(user);
					callback.findRestaurant();
					
				} else if (user == null) {
					destroyProgressDialog();
					Utility.getFailedToCreateAccountDialog("Invalid Credentials", thisCxt).show();
				} else {
					destroyProgressDialog();
					Utility.getGeneralAlertDialog("Server Error", e.getMessage(), thisCxt).show();
				}
			}
		});
	}

	/**
	 * Starts the Main activity for this restaurant.
	 */
	private void startMainActivity() {
		Intent i = new Intent(this, RestauarantMainActivity.class);
		startActivity(i);
	}

	/**
	 * Attempts to create new account. Uses another activity to create this
	 * account.
	 */
	private void createNewAccount() {
		Intent i = new Intent(this, CreateNewRestaurantAccountActivity.class);
		startActivity(i);
	}

	// //////////////////////////////////////////////////////////////////////
	// //// Call
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Handles the Getting of Restaurant instances.
	 * Once they are found the next activity is started
	 * 
	 * @author mhotan
	 */
	private class RestaurantFinder extends GetCallback {

		private final ParseUser user;
		
		/**
		 * Creates a callback associated to this user.
		 * @param user User that is contained in the Restaurant to Find
		 */
		public RestaurantFinder(ParseUser user) {
			this.user = user;
		}
		
		/**
		 * Finds a restaurant associated with this ParseUser.
		 */
		public void findRestaurant() {
			// TODO we need to test these queries
			ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
			inner.whereEqualTo(RestaurantInfo.PARSEUSER, user);
			ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
			query.whereMatchesQuery(Restaurant.INFO, inner);
			query.getFirstInBackground(this);
		}
		
		@Override
		public void done(ParseObject object, ParseException e) {
			// Stop the progress dialog
			destroyProgressDialog();
			
			if (e == null) {
				// We have found the correct object
				mRestaurantID = object.getObjectId();
				startMainActivity();
			} else {
				Utility.getGeneralAlertDialog("Server Failure", 
						"Failed to get your information", thisCxt).show();
			}
		}
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
		mProgressDialog.setTitle("Getting you");
		mProgressDialog.setMessage("Logging in...");
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
