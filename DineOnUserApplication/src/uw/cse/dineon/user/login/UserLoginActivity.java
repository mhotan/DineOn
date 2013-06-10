package uw.cse.dineon.user.login;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
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

	public static final String MIME_TEXT_PLAIN = "text/plain";

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

	@Override
	protected void onNewIntent(Intent intent) {
		/**
		 * This method gets called, when a new Intent gets associated
		 *  with the current activity instance.
		 * Instead of creating a new activity, 
		 * onNewIntent will be called. For more information have a look
		 * at the documentation.
		 *
		 * In our case this method gets called, when the user attaches a Tag to the device.
		 */
		handleIntent(intent);
	}

	/**
	 * Handles NFC Intent.
	 * @param intent intent containing NFC data.
	 */
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}

	/**
	 * Background task for reading the data. Do not block the UI thread while reading.
	 *
	 * @author Ralf Wondratschek
	 */
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}
			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN 
						&& Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}
			return null;
		}
		
		/**
		 * Read text on NFC.
		 * @param record Record NDef record containing data.
		 * @return String encoding of restaurant name
		 * @throws UnsupportedEncodingException Character encoding is not supported
		 */
		private String readText(NdefRecord record) 
				throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at 3.2.1
			 *
			 * http://www.nfc-forum.org/specs/
			 *
			 * bit_7 defines encoding
			 * bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */
			byte[] payload = record.getPayload();
			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;
			// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			// e.g. "en"
			// Get the Text
			return new String(payload, languageCodeLength + 1, 
					payload.length - languageCodeLength - 1, textEncoding);
		}
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				// TODO Start activity if logged in.
				if (DineOnUserApplication.getDineOnUser() != null 
						&& DineOnUserApplication.getCurrentDiningSession() == null) {
					
					JSONObject obj;
					try {
						obj = new JSONObject(result);
						String restName = obj.getString(DineOnConstants.KEY_RESTAURANT);
						int tableNum = obj.getInt(DineOnConstants.TABLE_NUM);
						Intent i = new Intent(getApplicationContext(), 
								RestaurantSelectionActivity.class);
						i.putExtra(DineOnConstants.KEY_RESTAURANT, restName);
						i.putExtra(DineOnConstants.TABLE_NUM, tableNum);
						startActivity(i);
					} catch (JSONException e) {
						Log.e(TAG, "Reading NFC failed!");
					}
					
					
					
				} else {
					Log.w(TAG, "Illegal state for starting activity from NFC");
				}
			}
		}
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
