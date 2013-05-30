package uw.cse.dineon.restaurant.login;

import java.util.Locale;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.CreateNewAccountFragment.CreateNewAccountListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * This class is in charge on instantiating a Restaurant object.
 * Then associate the restaurant object with this user.
 * @author mhotan
 */
public class CreateNewRestaurantAccountActivity extends FragmentActivity 
implements CreateNewAccountListener {

	private static final String TAG = CreateNewRestaurantAccountActivity.class.getSimpleName();

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Instance to myself.
	 */
	private CreateNewRestaurantAccountActivity This;

	/**
	 * Location Listener for location based services.
	 */
	private RestaurantLocationListener mLocationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);

		this.mLocationListener = new RestaurantLocationListener();
		try {
			this.mLocationListener.requestLocationUpdate();
		} catch (IllegalArgumentException ex) {
			// provider is not available because using emulator
			Toast.makeText(this, "Stop using an emulator idiot!", 
					Toast.LENGTH_SHORT).show();
		}

		This = this;
	}

	@Override
	public void submitNewAccount(String username, String email, String pw,
			String pwRepeat) {
		
		username = username.trim();
		username = username.toLowerCase(Locale.getDefault());
		email = email.trim();
		pw = pw.trim();
		pwRepeat = pwRepeat.trim();
		
		// Handle the validation
		Resolution completeRes = CredentialValidator.validateAll(username, email, pw, pwRepeat);

		if (completeRes.isValid() && !username.equals("")) { // Valid passwords
			AsyncRestaurantCreator creator =  new AsyncRestaurantCreator(username, pw, email);
			creator.execute();
		} else {
			Utility.getFailedToCreateAccountDialog(completeRes.getMessage(), This).show();
		}
	}

	@Override
	protected void onPause() {
		destroyProgressDialog();
		super.onPause();
	}

	/**
	 * Listener for getting restaurant location at creation time.
	 * @author mtrathjen08
	 */
	private class RestaurantLocationListener implements android.location.LocationListener {

		/**
		 * Location Manager for location services.
		 */
		private LocationManager mLocationManager;

		private boolean waitingForLocation;

		private Location mLocation;

		/**
		 * Constructor for the location listener.
		 */
		public RestaurantLocationListener() {
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			this.waitingForLocation = false;
			this.mLocation = null;
		}

		/**
		 * Return the last recorder location of the user.
		 * @return last recorder location.
		 */
		private Location getLastLocation() {
			return this.mLocation;
			// TODO add support for gps
		}

		/**
		 * Request a location reading from the Location Manager.
		 */
		private void requestLocationUpdate() {
			this.mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
			// TODO add support for gps
		}

		/**
		 * The location was not returned before login, wait for return.
		 */
		public void waitForLocation() {
			this.waitingForLocation = true;
		}

		@Override
		public void onLocationChanged(Location loc) {
			this.mLocation = loc;

			if (this.waitingForLocation) {
				destroyProgressDialog();
				DineOnRestaurantApplication.getRestaurant().getInfo().
				updateLocation(loc.getLongitude(), loc.getLatitude());
				DineOnRestaurantApplication.getRestaurant().getInfo().saveInBackGround(null);
				startMainActivity();
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Creates a restaurant in the back ground.
	 * @author mhotan
	 */
	private class AsyncRestaurantCreator extends AsyncTask<Void, Exception, Restaurant> {

		private final ParseUser mUser;

		private Exception mError;

		/**
		 * Starts the framework for creating a restaurant from credentials. 
		 * @param name Name of the user
		 * @param password password chosen
		 * @param email email of the user.
		 */
		public AsyncRestaurantCreator(String name, String password, String email) {
			mUser = new ParseUser();
			mUser.setUsername(name);
			mUser.setPassword(password);
			mUser.setEmail(email);
		}

		@Override
		protected void onPreExecute() {
			createProgressDialog();
		}
		
		@Override
		protected Restaurant doInBackground(Void... params) {
			try {
				mUser.signUp();
				Restaurant newRest = new Restaurant(mUser);
				newRest.saveOnCurrentThread();
				return newRest;
			} catch (Exception e) {
				onProgressUpdate(e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Exception... exception) {
			mError = exception[0];
		}

		@Override
		protected void onPostExecute(Restaurant rest) {
			if (mError != null) {
				destroyProgressDialog();
				Utility.getFailedToCreateAccountDialog(mError.getMessage(), This).show();
				return;
			} else {
				// Obtain the location of the current restaurant
				Location loc = mLocationListener.getLastLocation();
				if (loc != null) {
					destroyProgressDialog();
					rest.getInfo().updateLocation(loc.getLongitude(), 
							loc.getLatitude());
					rest.getInfo().saveInBackGround(null);
					DineOnRestaurantApplication.logIn(rest);
					startMainActivity();
				} else {
					DineOnRestaurantApplication.logIn(rest);
					if (mLocationListener.mLocationManager.
							getProvider(LocationManager.NETWORK_PROVIDER) == null) {
						destroyProgressDialog();
						// we're in an emulator so don't wait
						rest.getInfo().updateLocation(0.0, 0.0);
						rest.getInfo().saveInBackGround(null);
						startMainActivity();
					} else {
						mLocationListener.waitForLocation();
					}
				}
			}
		}

	}

	/**
	 * Starts the Main activity for this restaurant.
	 */
	private void startMainActivity() {
		Intent i = new Intent(this, RestauarantMainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	// //////////////////////////////////////////////////////////////////////
	// /// UI Specific methods
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	protected void createProgressDialog() {
		if (mProgressDialog != null) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Logging in...");
		mProgressDialog.setMessage("Getting you your own restaurant");
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
