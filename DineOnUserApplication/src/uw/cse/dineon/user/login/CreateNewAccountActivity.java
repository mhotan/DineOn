package uw.cse.dineon.user.login;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/**
 * Create account activity that allows the user to create an account or call
 * decide to do a third party login.
 * @author mhotan
 */
public class CreateNewAccountActivity extends FragmentActivity 
implements CreateNewAccountFragment.OnCreateNewAccountListener {

	public static final String TAG = CreateNewAccountActivity.class.getSimpleName();

	private AlertDialog failDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
	}

	/**
	 * This automates the addition of the User Intent.
	 * Should never be called when mUser is null.

	 * @param intent Intent to start activity with
	 */
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		finish();
	}

	@Override
	public void onCreateNewAccount(String username, String email,
			String password, String passwordRepeat) {
		// Handle the validation
		Resolution completeRes = CredentialValidator.validateAll(username, 
				email, password, passwordRepeat);

		if (completeRes.isValid()) {
			final ParseUser P_USER = new ParseUser();
			P_USER.setUsername(username);
			P_USER.setPassword(password);
			P_USER.setEmail(email);
			P_USER.signUpInBackground(getSignUpCallback());
		}
		else {
			showFailAlertDialog(completeRes.getMessage());
		}
	}

	/**
	 * Just shows general failure dialog with this message.
	 * @param error String to display
	 */
	private void showFailAlertDialog(String error) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Failed to create account");
		builder.setMessage(error);
		builder.setCancelable(true);
		builder.setPositiveButton("Try Again", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		failDialog = builder.create();
		failDialog.show();
	}
	
	/**
	 * signup callback method.
	 * @return the callback object.
	 */
	public SignUpCallback getSignUpCallback() {
		return new SignUpCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// Hooray! Let them use the app now.
					// TODO Create a new User object and save it to the
					// cloud and associate it with the actual user
					// possibly by user name
					final DineOnUser USER = new DineOnUser(ParseUser.getCurrentUser());
					USER.saveInBackGround(getSaveCallback(USER));
				} else {
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					showFailAlertDialog(e.getMessage());
				}
			}
	
		};
	}
	
	/**
	 * Gets the savecallback for the given user.
	 * @param user being saved
	 * @return the callback object
	 */
	public SaveCallback getSaveCallback(final DineOnUser user) {
		return new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// Success
					onSuccess(user);
				} else {
					showFailAlertDialog(e.getMessage());
				}
			}
		};
	}
	
	/**
	 * Gets rid of failure to create account dialog.
	 */
	public void destroyFailDialog() {
		if(this.failDialog != null) {
			this.failDialog.cancel();
		}
		
	}
	
	/**
	 * 
	 * @param dou DineOnUser that's created
	 */
	public void onSuccess(DineOnUser dou) {
		if(dou != null) {
			DineOnUserApplication.setDineOnUser(dou);
			Intent intent = 
					new Intent(this, RestaurantSelectionActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onLoginWithFacebook() {
		// TODO Auto-generated method stub
		
	}

}
