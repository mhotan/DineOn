package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.user.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class MenuItemDetailActivity extends FragmentActivity {

	public static final String EXTRA_MENUITEM = "menuitem";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		setContentView(R.layout.activity_menuitem_detail);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String s = extras.getString(EXTRA_MENUITEM);
			TextView view = (TextView) findViewById(R.id.label_menuitem_details);
			view.setText(s);
		}
	}

}