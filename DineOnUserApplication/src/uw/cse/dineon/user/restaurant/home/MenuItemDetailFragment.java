package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.user.R;
import android.app.Activity;
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
	
	private MenuItemDetailListener mListener;
	
	private MenuItem mMenuItem;
	
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menuitem_detail,
				container, false);
		
		this.mView = view;
		
		return view;
	}
	
	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mListener != null) {
			setMenuItem(mListener.getMenuItem());
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuItemDetailListener) {
			mListener = (MenuItemDetailListener) activity;
			
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MenuItemDetailFragment.MenuItemDetailListener");
		}
	}

	/**
	 * @param item MenuItem to set
	 */
	public void setMenuItem(MenuItem item) {
		this.mMenuItem = item;
		
		// set the title
		TextView title = (TextView) this.mView.findViewById(R.id.label_menu_item_name);
		title.setText(item.getTitle());
		
		// set the description
		TextView description = (TextView) this.mView.findViewById(R.id.label_menu_item_description);
		description.setText(item.getDescription());
		
		// TODO set the image
		
		// TODO set the allergens
	}
	
	/**
	 * TODO implement.
	 * @author mrathjen
	 */
	public interface MenuItemDetailListener {

		/**
		 * TODO get the current menu item.
		 * @return MenuItem
		 */
		public MenuItem getMenuItem();

	}

}
