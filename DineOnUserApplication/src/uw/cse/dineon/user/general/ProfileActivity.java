package uw.cse.dineon.user.general;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Activity that manages the profile and settings fragments.
 * 
 * @author mhotan, espeo196
 */
public class ProfileActivity extends DineOnUserActivity implements 
		ProfileEditFragment.InfoChangeListener {
	
	/**
	 * Enums for keeping track of state.
	 */
	public enum State { DEFAULT, EDIT, BACK };
	private State state;
	
	private static final String TAG = ProfileActivity.class.getSimpleName();
	private final int CONTAINER_ID = 10101010;	// ID of dynamically added frame layout
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frame = new FrameLayout(this);
		frame.setId(CONTAINER_ID);
		setContentView(frame, 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		if(savedInstanceState == null) {
			Fragment imageFrag = ProfileImageFragment.newInstance(getInfo());
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			ft.add(CONTAINER_ID, imageFrag, "imageFrag");
		//	ft.addToBackStack(null);
			ft.commit();	
		}
		state = State.DEFAULT;
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actually
		//  UI Menu is updated this is done manually
		inflater.inflate(R.menu.profile_menu, menu);
	
		final android.view.Menu M = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		final android.view.MenuItem ITEM = menu.findItem(R.id.option_bill);
		ITEM.setEnabled(true);
		ITEM.setVisible(true);
		List<android.view.MenuItem> customActionBarButtons = new ArrayList<android.view.MenuItem>();
		customActionBarButtons.add(menu.findItem(R.id.option_bill));

		setOnClick(M, customActionBarButtons);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		MenuItem m = menu.findItem(R.id.option_edit_profile);
		if(state == State.EDIT) {
			m.setEnabled(false);
			m.setVisible(false);
		} else if(state == State.BACK) {
			state = State.DEFAULT;
			m.setEnabled(true);
			m.setVisible(true);
		}		

		// If checked in
		if(DineOnUserApplication.getCurrentDiningSession() != null) {
			disableMenuItem(menu, R.id.option_check_in);
			enableMenuItem(menu, R.id.option_bill);
		} else { // If not checked in
			enableMenuItem(menu, R.id.option_check_in);
			disableMenuItem(menu, R.id.option_bill);
		}
		return true;		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		state = State.BACK;
	}
	
	/**
	 * Creates the onClick listeners for the specified menu items.
	 * 
	 * @param m the parent menu
	 * @param items the list of MenuItems to create listeners for
	 */
	private void setOnClick(final android.view.Menu m, List<android.view.MenuItem> items) {
		for (final android.view.MenuItem ITEM : items) {
			ITEM.getActionView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {   
					m.performIdentifierAction(ITEM.getItemId(), 0);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_edit_profile:
			
			// swap current fragments with editable text fields
			Fragment frag = 
				ProfileEditFragment.newInstance(DineOnUserApplication.getUserInfo());
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			
			ft.replace(CONTAINER_ID, frag);
			ft.addToBackStack(null);
			ft.commit();
			
			state = State.EDIT;
			break;
		case R.id.option_logout:
			ParseUser.logOut();
			startLoginActivity();
			break;
		default:
			//Unknown
			Log.e(TAG, "None of the specified action items were selected.");
		}
		return true;
	}

	@Override
	public void onUserInfoUpdate(UserInfo user) {
		user.saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(getApplicationContext(),
							"Profile Updated!", Toast.LENGTH_LONG)
							.show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
				}
			}
		});
	}

	@Override
	public UserInfo getInfo() {
		return DineOnUserApplication.getUserInfo();
	}
}
