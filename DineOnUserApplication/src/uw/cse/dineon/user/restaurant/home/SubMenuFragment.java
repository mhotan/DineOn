package uw.cse.dineon.user.restaurant.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

/**
 * A list of sub menu items.
 * TODO 
 * @author mhotan
 */
public class SubMenuFragment extends ListFragment {

	private final String TAG = this.getClass().getSimpleName();

	// Key for bundling
	public static final String EXTRA_MENU_TYPE = "MENU_TYPE";
	
	private String mMenuType;
	private final String UNDEFINED_MENU = "Undefined Menu";

	private MenuItemListListener mListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuType = getArguments() != null ? getArguments().getString(EXTRA_MENU_TYPE) 
				: UNDEFINED_MENU;

		// TODO make Parse queries
		// Or load from local data base
		// TODO Change String to a new data type (Restaurant)
		List<String> tediousList = new ArrayList<String>();
		for (int i = 0; i < 5; ++i) {
			tediousList.add(mMenuType + " Item " + (i + 1));
		}

		ArrayAdapter<String> adapter = new MenuItemListAdapter(
				this.getActivity(), tediousList);
		setListAdapter(adapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuItemListListener) {
			mListener = (MenuItemListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RestaurantListFragment.RestaurantListListener");
		}
	}
	
	///

	/**
	 * TODO Adapter for MenuItems.
	 * @author mhotan
	 */
	private class MenuItemListAdapter extends ArrayAdapter<String> {

		private final Context mContext;
		private final List<String> mMenuItems;

		/**
		 * This is a runtime mapping between "Increment and decrement button"
		 * and there respective menu item.
		 * TODO Change String to menuitem;
		 * NOTE (MH): Not exactly sure if this is the best solution
		 */
		private final HashMap<NumberPicker, String> mMapping;
		
		// Listeners for all row items
		private final NumberPicker.OnValueChangeListener mNumPickerListener;
		private final View.OnClickListener mFocusListener;

		/**
		 * 
		 * @param context Context
		 * @param items List of strings
		 */
		public MenuItemListAdapter(Context context, List<String> items) {
			super(context, R.layout.listitem_menuitem, items);
			mContext = context;
			mMenuItems = new ArrayList<String>(items);
			mMapping = new HashMap<NumberPicker, String>();
			
			// Inialize listener for number count
			mNumPickerListener = new OnValueChangeListener() {
				
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					if (mMapping.get(picker) == null) {
						Log.e(TAG, 
								"Failed to initialize Number picker and associate with menu item");
						return;
					}
					
					String menuItem = mMapping.get(picker);
					
					// increment the count with the difference with the new and old value
					int incrementAmount = newVal - oldVal;
					for (int i = 0; i < incrementAmount; ++i) {
						mListener.onMenuItemIncremented(menuItem);
					}
					// decrement the amount
					int decrementAmount = incrementAmount * -1;
					for (int i = 0; i < decrementAmount; ++i) {
						mListener.onMenuItemDecremented(menuItem);
					}
					
				}
			};
			
			mFocusListener  = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView tView = (TextView) v.findViewById(R.id.label_menu_item);
					//TODO Fix this to be a mapping
					String menuItem = tView.getText().toString();
					mListener.onMenuItemFocusedOn(menuItem);
				}
			};

		}
		
		@Override
		public View getView(int position, View covnertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_menuitem, parent, false);
			
			// TODO Here is where we adjust the contents of the list row
			// with attributes determined by the menu item
			// Now we are just setting the text to be the name of the menu item
			
			TextView menuLabel = (TextView) rowView.findViewById(R.id.label_menu_item);
			menuLabel.setOnClickListener(mFocusListener);
			
			// Get the right name from the list
			String item = mMenuItems.get(position);
			menuLabel.setText(item);
			
			NumberPicker np = (NumberPicker) rowView.findViewById(R.id.numberpicker_menuitem_qty);
			mMapping.put(np, item);
			np.setOnValueChangedListener(mNumPickerListener);
			
			return rowView;
		}
	}

	/**
	 * TODO implement.
	 * @author mhotan
	 */
	public interface MenuItemListListener {


		// TODO Place parameter preferable a "Restaurant" Instance


		/**
		 * TODO implement.
		 * @param menuItem String
		 */
		public void onMenuItemFocusedOn(/*Replace with menuitem object*/String menuItem);

		/**
		 * TODO implement.
		 * @param menuItem String
		 */
		public void onMenuItemIncremented(/*Replace with menuitem object*/String menuItem);

		/**
		 * TODO implement.
		 * @param menuItem String
		 */
		public void onMenuItemDecremented(/*Replace with menuitem object*/String menuItem);

		/**
		 * TODO implement.
		 */
		public void onRestaurantInfoRequested();
	
		/**
		 * TODO implement.
		 */
		public void onViewCurrentBill();
		
		/**
		 * TODO change to Restaurant datatype.
		 * @return String
		 */
		public RestaurantInfo getCurrentRestaurant();
	}
}
