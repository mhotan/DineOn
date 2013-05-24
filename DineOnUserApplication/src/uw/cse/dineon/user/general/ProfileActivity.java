package uw.cse.dineon.user.general;

import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

/**
 * Activity that manages the profile and settings fragments.
 * 
 * @author mhotan
 */
public class ProfileActivity extends DineOnUserActivity {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_and_settings);
//		this.getSupportFragmentManager()
//				.findFragmentById(R.id.fragmentProfileImage)
//				.getFragmentManager();
//		this.getSupportFragmentManager()
//				.findFragmentById(R.id.fragmentTabView)
//				.getFragmentManager();

	}

}
