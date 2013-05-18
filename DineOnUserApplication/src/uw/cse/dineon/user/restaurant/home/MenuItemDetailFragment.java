package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author 
 *
 */
public class MenuItemDetailFragment extends Fragment {

	public static final String KEY_MENU = "menu";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menuitem_detail,
				container, false);
		
		
		return view;
	}

	/**
	 * TODO finish.
	 * @param item String
	 */
	public void setMenuItem(MenuItem item) {
		TextView view = (TextView) getView().findViewById(R.id.label_menu_item_name);
		view.setText(item.getTitle());
	}

}
