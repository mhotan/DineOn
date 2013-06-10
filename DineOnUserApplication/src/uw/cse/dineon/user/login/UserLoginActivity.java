package uw.cse.dineon.user.login;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.login.DineOnUserDownloader.DineOnUserDownloaderCallback;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Initial activity that user is brought to in order to gain admittance.  
 * Activity that allows users to login via Email and Password, Facebook, or Twitter
 * <b>Application has the ability of connecting to facebook and twitter. </b>
 * <b>Once login is validated users will be taken to the restaurant selection activity. </b>
 * <b>User also have the ability to create a new account.  </b>
 * 
 * @author mhotan
 */
public class UserLoginActivity extends FragmentActivity implements 
LoginFragment.OnLoginListener,
DineOnUserDownloaderCallback {

	private static final String TAG = UserLoginActivity.class.getSimpleName();

	// Request code to create a new account
	private static final int REQUEST_LOGIN_FACEBOOK = 0x1;

	public static final String EXTRA_FACEBOOK = "Login with facebook";

	private Context thisCxt;

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Login to handle user attempts to login.
	 */
	private DineOnLoginCallback mLoginCallback;

	////////////////////////////////////////////////////////////////////////
	/////  Activity specific 
	////////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		mLoginCallback = new DineOnLoginCallback();
		thisCxt = this;
		Log.d(TAG, "Createing UserLoginActivity after logout");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Automatic login and entry if user has an active account already.
		DineOnUser user = DineOnUserApplication.getDineOnUser();
		if (user != null) {
			startRestSelectionAct(user);
		}
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	/**
	 * This automates the addition of the User Intent.
	 * Should never be called when mUser is null.
	 * @param intent Intent
	 */
	@Override
	public void startActivity(Intent intent) {
		if (DineOnConstants.DEBUG && DineOnUserApplication.getDineOnUser() == null) {
			Toast.makeText(this, "Need to create or download a User", Toast.LENGTH_SHORT).show();
			return;
		}
		super.startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	////////////////////////////////////////////////////////////////////////
	/////  Private Helper methods for starting new activities
	////////////////////////////////////////////////////////////////////////

	/**
	 * Starts Restaurant selection activity with current.
	 * @param user User to send of the
	 */
	public void startRestSelectionAct(DineOnUser user) {
		// Destroy any running progress dialog
		if(user != null) {
			DineOnUserApplication.setDineOnUser(user, this);
			destroyProgressDialog();
			Intent i;
			if (DineOnUserApplication.getDineOnUser().getDiningSession() != null) {
				i = new Intent(this, RestaurantHomeActivity.class);
			} else {
				i = new Intent(this, RestaurantSelectionActivity.class);
			}
			startActivity(i);
			this.finish();
		}
	}

	////////////////////////////////////////////////////////////////////////
	/////  Callbacks for the Fragment interface
	////////////////////////////////////////////////////////////////////////

	// Logging in Via email
	@Override
	public void onLogin(String username, String password) {
		createProgressDialog();
		Resolution unResolution = CredentialValidator.isValidUserName(username);
		Resolution pwResolution = CredentialValidator.isValidPassword(password);

		StringBuffer buf = new StringBuffer();
		if (!unResolution.isValid()) {
			destroyProgressDialog();
			buf.append(unResolution.getMessage() + "\n");
		}
		if (!pwResolution.isValid()) {
			destroyProgressDialog();
			buf.append(pwResolution.getMessage() + "\n");
		}

		if (buf.length() > 0) {
			destroyProgressDialog();
			showInvalidCredentialDialog(buf.toString());
			return;
		}

		ParseUser.logInInBackground(username, password, mLoginCallback);
	} 

	/**
	 * User login via Facebook.
	 */
	@Override
	public void onLoginWithFacebook() {
		// TODO Disable all the buttons so user does not
		// Monkey it
		createProgressDialog();
		// Replace actionbar with menu

		// Process the face book application
		ParseFacebookUtils.logIn(this, REQUEST_LOGIN_FACEBOOK, mLoginCallback);
	}

	////////////////////////////////////////////////////////////////////////
	/////  UI Specific methods
	////////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	protected void createProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Getting you ready to DineOn!");
		mProgressDialog.setMessage("Logging in...");       
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	protected void destroyProgressDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * Show bad input alert message for logging in.
	 * @param message message to show
	 */
	protected void showInvalidCredentialDialog(String message) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Failed to login");
		b.setMessage(message);
		b.setCancelable(true);
		b.setPositiveButton("Try Again", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	////////////////////////////////////////////////////////////////////////
	/////  Callback for logging in, saving, and dowloading
	////////////////////////////////////////////////////////////////////////

	/**
	 * Custom callback to handle login results 
	 * If a Parse user instance was successfully created or found
	 * then depending if they are new create or download a user reference
	 * 
	 * This will prioritize cache.
	 * 
	 * @author mhotan
	 */
	private class DineOnLoginCallback extends com.parse.LogInCallback {

		@Override
		public void done(ParseUser user, ParseException e) {

			// Unable to login
			if (user == null) {
				destroyProgressDialog();
				Utility.getGeneralAlertDialog("Login Failed", 
						"Invalid Login Credentials", thisCxt).show();
				return;
			} 
			if (e != null) {
				destroyProgressDialog();
				Utility.getGeneralAlertDialog("Login Failed", e.getMessage(), thisCxt).show();
				return;
			}

			// This method at this point needs to produce a User Instance 
			if (user.isNew()) {
				final DineOnUser M_USER = new DineOnUser(user);
				createProgressDialog();
				M_USER.saveInBackGround(new SaveCallback() {

					/**
					 * Start an activity Restaurant selection
					 * activity once we know there is a User object created for us
					 */
					@Override
					public void done(ParseException e) {
						destroyProgressDialog();
						if (e == null) { // Success
							startRestSelectionAct(M_USER);
						} else {
							Utility.getGeneralAlertDialog("Login Failed", 
									e.getMessage(), thisCxt).show();
						}
					}
				});
			} else {
				downloadDineOnUser(user);
			}

		}
	}

	/**
	 * Download the DineOnUser using parse user provided.
	 * @param user parse user to use finding the DineOn User
	 */
	public void downloadDineOnUser(ParseUser user) {
		DineOnUserDownloader userDownloader = new DineOnUserDownloader(user, this);
		userDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
	}

	@Override
	public void onFailToDownLoadDineOnUser(String message) {
		destroyProgressDialog();
		Utility.getGeneralAlertDialog("Server Failure", 
				"Failed to get your information", thisCxt).show();
	}

	@Override
	public void onDownloadedDineOnUser(DineOnUser user) {
		destroyProgressDialog();
		startRestSelectionAct(user);
	} 
}
