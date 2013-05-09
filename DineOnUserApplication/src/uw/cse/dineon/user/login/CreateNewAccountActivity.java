package uw.cse.dineon.user.login;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Create account activity that allows the user to create an account or call
 * decide to do a third party login
 * @author mhotan
 */
public class CreateNewAccountActivity extends FragmentActivity 
implements CreateNewAccountFragment.onCreateNewAccountListener {

	public static final String TAG = CreateNewAccountActivity.class.getSimpleName();

	private boolean mLoginWithFacebook = false;

	private DineOnUser mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
		
	}

	@Override
	public void onCreateNewAccount(String username, String email,
			String password, String passwordRepeat) {
		// Handle the validation
		Resolution completeRes = CredentialValidator.validateAll(username, 
				email, password, passwordRepeat);

		if (completeRes.isValid()) {
			final ParseUser user = new ParseUser();
			user.setUsername(username);
			user.setPassword(password);
			user.setEmail(email);
			user.signUpInBackground(new SignUpCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						// Hooray! Let them use the app now.
						// TODO Create a new User object and save it to the
						// cloud and associate it with the actual user
						// possibly by user name
						mUser = new DineOnUser(ParseUser.getCurrentUser());
					} 
					else {
						// Sign up didn't succeed. Look at the ParseException
						// to figure out what went wrong
						showFailAlertDialog(e.getMessage());
					}
				}
			});
		}
		else {
			showFailAlertDialog(completeRes.getMessage());
		}
	}

	@Override
	public void finish(){
		Intent retIntent = new Intent();
		// Specify the user of facebook
		retIntent.putExtra(UserLoginActivity.EXTRA_FACEBOOK, mLoginWithFacebook);

		// Send restaurant instance back
		if (mUser != null) {
			retIntent.putExtra(DineOnConstants.KEY_USER, mUser.getObjId());
		}
		setResult(RESULT_OK, retIntent);
		super.finish();
	}

	@Override
	public void onLoginWithFacebook() {
		// TODO Later phase
		mLoginWithFacebook = true;
		finish();
	}

	@Override
	public void onLoginWithTwitter() {
		// TODO Later phase
		DevelopTools.getUnimplementedDialog(this, null);		
	}

	/**
	 * Just shows general failure dialog with this message
	 * @param error
	 */
	private void showFailAlertDialog(String error){
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
		builder.create().show();
	}

}
