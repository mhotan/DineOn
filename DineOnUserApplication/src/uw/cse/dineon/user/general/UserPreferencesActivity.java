package uw.cse.dineon.user.general;

import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 * @author mhotan
 */
public class UserPreferencesActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
