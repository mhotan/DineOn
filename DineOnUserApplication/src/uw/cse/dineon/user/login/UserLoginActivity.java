package uw.cse.dineon.user.login;

import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Initial activity that user is brought to in order to gain admittance.  
 * Activity that allows users to login via Email and Password, Facebook, or Twitter
 * <b>Application has the ability of connecting to facebook and twitter
 * <b>Once login is validated users will be taken to the restaurant selection activity
 * <b>User also have the ability to create a new account  
 * 
 * @author mhotan
 */
public class UserLoginActivity extends FragmentActivity implements 
LoginFragment.OnLoginListener {

	private static final String TAG = UserLoginActivity.class.getSimpleName();

	// Request code to create a new account
	private static final int REQUEST_CREATE_NEW_ACCOUNT = 0x1;

	/**
	 * Return code for  
	 */
	public static final String RETURN_CODE_LOGIN_CREDENTIALS = TAG + ":LOGIN_NEW_CREDENTIALS";
	
	/**
	 * 
	 */
	public static final String RETURN_CODE_LOGIN_3RDPARTY = TAG + ":LOGIN_3RD_PARTY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// TODO Complete Initialization
		// check internet connection
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		
		ParseUser me = ParseUser.getCurrentUser();
		if (me != null) {
			startRestSelectionAct();
		}
	}
	
	private void startRestSelectionAct(){
		Intent i = new Intent(this, RestaurantSelectionActivity.class);
//		i.putExtra(RestaurantSelectionActivity.EXTRA_USER, loginCredentials);
		startActivity(i);
	}
	
	// TODO Static Callback
	@Override
	public void onLogin(String username, String password) {
		// TODO Auto-generated method stub
		
	} 
	
	@Override
	public void onLogin(String loginCredentials) {
		// TODO Auto-generated method stub
		// Check credentials

		startRestSelectionAct();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy 
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_forgot_password: 
			// TODO Implement
			// TODO Implement
			DevelopTools.getUnimplementedDialog(this, null).show();
		case R.id.option_create_new_account:
			Intent i = new Intent(this.getApplicationContext(), CreateNewAccountActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}

	@Override
	public void onCreateNewAccount() {
		// TODO Auto-generated method stub
		Intent creatAccountIntent = new Intent(this, CreateNewAccountActivity.class);
		startActivityForResult(creatAccountIntent, REQUEST_CREATE_NEW_ACCOUNT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CREATE_NEW_ACCOUNT) {
			if (data.hasExtra(RETURN_CODE_LOGIN_CREDENTIALS)) {

				// TODO Remove this and place actual implementation
				// Verify account actually exist
				// automatically proceed once account is verified
				Toast.makeText(this, "User " + data.getExtras().getString(RETURN_CODE_LOGIN_CREDENTIALS)
						+ " created!",
						Toast.LENGTH_SHORT).show();
			} else if (data.hasExtra(RETURN_CODE_LOGIN_3RDPARTY)) {

				Toast.makeText(this, "Login using " + data.getExtras().getString(RETURN_CODE_LOGIN_3RDPARTY)
						+ " requested",
						Toast.LENGTH_SHORT).show();
			}
		}
	}


}
