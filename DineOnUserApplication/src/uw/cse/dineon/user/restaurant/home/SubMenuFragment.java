package uw.cse.dineon.user.restaurant.home;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.animation.ExpandAnimation;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This fragment shows a list of menu items.  The list of menu items is determined by the
 * index of the menu in the current restaurant list of menus.
 * @author mhotan
 */
public class SubMenuFragment extends ListFragment {

	private static final String TAG = SubMenuFragment.class.getSimpleName();

	private static final String ZERO = "0";
	
	// Key for bundling and data storage.
	public static final String EXTRA_WHICH_MENU = "MENU";

	// book keeping variable for menus
	private int mMenuIndex;
	private Menu mMenu;

	private MenuItemListListener mListener;
	private MenuItemListAdapter mAdapter;

	/**
	 * Index of sub menu to show.
	 * @param index Index of sub menu
	 * @return A SubMenu instance
	 */
	public static SubMenuFragment getInstance(int index) {
		SubMenuFragment frag = new SubMenuFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_WHICH_MENU, index);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		RestaurantInfo restaurant = mListener.getCurrentRestaurant();
		List<Menu> menus = restaurant.getMenuList();

		// Everything below is to maintain the state of 
		// menu before and after creation and deletion.
		mMenuIndex = -1;
		Bundle args = getArguments();
		if (args != null) {
			mMenuIndex = args.getInt(EXTRA_WHICH_MENU, -1);
		}
		if (mMenuIndex == -1) { // Not in the initial arguments 
			// Check if in the saved instance.
			mMenuIndex = savedInstanceState.getInt(EXTRA_WHICH_MENU, -1);
		} 
		if (mMenuIndex < 0) {
			Log.e(TAG, "Unable to obatin menu index to construct menu fragment");
			return;
		}
		mMenuIndex = Math.max(0, Math.min(menus.size() - 1, mMenuIndex));
		mMenu = menus.get(mMenuIndex);

		mAdapter = new MenuItemListAdapter(
				this.getActivity(), this.mMenu.getItems());
		setListAdapter(mAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_WHICH_MENU, mMenuIndex);
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

	/**
	 * Adds a menu item to the fragment view.
	 * @param item Menu Item to add.
	 */
	public void addMenuItem(MenuItem item) {
		if (item == null) {
			return;
		}
		mAdapter.add(item);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Attempts to remove menu item from the fragment view.
	 * @param item Item to be removed
	 */
	public void removeMenuItem(MenuItem item) {
		mAdapter.remove(item);
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Clears all the quantities of the menu items in the view.
	 * Every menu item will have a zero next to their quantity amount.
	 */
	public void clearQuantities() {
		mAdapter.clearQuantities();
	}
	
	/**
	 * Adapter for MenuItems.
	 * @author mhotan
	 */
	private class MenuItemListAdapter extends ArrayAdapter<MenuItem> {

		private final Context mContext;
		
		private final Map<MenuItem, MenuItemHandler> mHandlers;

		/**
		 * Create an adapter to handle the presentation and interaction 
		 * with menu items.
		 * 
		 * @param context Context
		 * @param items List of strings
		 */
		public MenuItemListAdapter(Context context, List<MenuItem> items) {
			super(context, R.layout.listitem_menuitem, items);
			mContext = context;
			mHandlers = new HashMap<MenuItem, MenuItemHandler>();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Establish a reference to the top and bottom of ex
			View vwTop;
			View vwBot;

			LinearLayout layoutView = null; 
			if (convertView == null) { // Created for the first time.
				layoutView = new LinearLayout(mContext);
				layoutView.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_menuitem_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_menuitem_bottom, null, true);
				layoutView.addView(vwTop);
				layoutView.addView(vwBot);
				convertView = layoutView;
			} else { // We already have a view for this layout
				vwTop = convertView.findViewById(R.id.menuitem_top);
				vwBot = convertView.findViewById(R.id.menuitem_bottom);
			}

			// Get a reference to the menu item to show
			MenuItem toShow = getItem(position);
			
			// For every menut item we only need one menu item for instance
			// of the view.
			if (layoutView != null) { // Brand new view
				MenuItemHandler handler = new MenuItemHandler(toShow, vwTop, vwBot);
				mHandlers.put(toShow, handler);
			}
			
			return convertView;
		}
		
		/**
		 * Clears all the quantities of the current menu items.
		 */
		void clearQuantities() {
			for (MenuItemHandler handler: mHandlers.values()) {
				handler.clearQuantity();
			}
		}
		
		@Override
		public void remove(MenuItem item) {
			mHandlers.remove(item);
			super.remove(item);
		}

		/**
		 * Handler specifically made to handle input from views.
		 * @author mhotan
		 */
		private class MenuItemHandler implements OnClickListener, TextWatcher {

			/**
			 * Menu Item to handle.
			 */
			private final MenuItem mItem;

			private final View mBottom, mTop;

			/**
			 * MenuItem image. 
			 */
			private final ImageView mImage;

			/**
			 * Present the name of the menu item.
			 */
			private final TextView mMenuItemName, mInputQty, mHint;

			/**
			 * Buttons that handle the order adjustment.
			 */
			private final ImageButton mIncrementButton, mDecrementButton, 
			mMoreInfoButton, mAddToPendingOrderButton;

			/**
			 * For manual input of orde quantity.
			 */
			private final EditText mSpecialInstructions;

			/**
			 * Creates a handler for this particular menu item portrayal in a list.
			 * 
			 * @param item Menu item to assign to
			 * @param top Top portion of expandable list view
			 * @param bottom Bottom portion of expandable list view
			 */
			MenuItemHandler(MenuItem item, View top, View bottom) {
				// Assign the menu item
				mItem = item;
				// Set the animation for all transitions
				mBottom = bottom;
				mTop = top;

				// Assign the top portions 
				mImage = (ImageView) top.findViewById(R.id.image_menuitem);
				mMenuItemName = (TextView) top.findViewById(R.id.label_menuitem_name);
				mIncrementButton = (ImageButton) top.findViewById(R.id.button_increment_order);
				mDecrementButton = (ImageButton) top.findViewById(R.id.button_decrement_order);
				mInputQty = (TextView) top.findViewById(R.id.input_order_qty);
				mHint = (TextView) top.findViewById(R.id.label_more_info_hint);
				mAddToPendingOrderButton = (ImageButton) top.findViewById(R.id.button_add_to_order);

				// Assign bottom portions of the view
				mSpecialInstructions = (EditText) 
						bottom.findViewById(R.id.input_special_instructions);
				mMoreInfoButton = (ImageButton) bottom.findViewById(R.id.button_about_menuitem);

				// Set the values that will never change
				mMenuItemName.setText(mItem.getTitle());
				mInputQty.setText(ZERO);

				// Set the initial image to be not visible until we download it.
				mImage.setVisibility(View.GONE);
				DineOnImage image = mItem.getImage();			
				if (image != null) {
					mListener.onGetImage(image, new ImageGetCallback() {

						@Override
						public void onImageReceived(Exception e, Bitmap b) {
							if (e == null) {
								// We got an image to show.
								mImage.setVisibility(View.VISIBLE);
								mImage.setImageBitmap(b);
							}
						}
					});
				} else {
					mImage.setVisibility(View.VISIBLE);
				}

				mBottom.setVisibility(View.GONE);
				
				// Now add the onclick listeners to handle all the event driven from the user.
				mMenuItemName.setOnClickListener(this);
				mHint.setOnClickListener(this);

				// Set the listener for handling order quantity changes
				mIncrementButton.setOnClickListener(this);
				mDecrementButton.setOnClickListener(this);
				mAddToPendingOrderButton.setOnClickListener(this);

				// handle the user request for more information about a menu item
				mMoreInfoButton.setOnClickListener(this);

				mSpecialInstructions.addTextChangedListener(this);
				mTop.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {
				// If the user clicks on the name or the hint
				// Expand the view exposing more instructions.
				if (v == mIncrementButton) {
					incrementQty();
				} else if (v == mDecrementButton) {
					decrementQty();
				} else if (v == mHint || v == mMenuItemName || v == mTop) {
					mBottom.startAnimation(new ExpandAnimation(mBottom, 400));
				} else if (v == mMoreInfoButton) {
					mListener.onMenuItemFocusedOn(mItem);
				} else if (v == mAddToPendingOrderButton) {
					// Extract the current value
					int currentValue = Integer.parseInt("" + mInputQty.getText());
					// If its positive notify listener.
					// Reset the value to 0
					clearQuantity();
					if (currentValue <= 0) {
						return;
					}
					
					// Notify listener
					mListener.onMenuItemAddToOrder(mItem, currentValue);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Save this instruction with this particular menu item
				Log.i(TAG, "User wanted special instructions " + s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Do nothing
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Do nothing
			}

			/**
			 * Increments the quantity of menu items wanted.
			 */
			private void incrementQty() {
				int currentValue = Integer.parseInt("" + mInputQty.getText());
				mInputQty.setText("" + (currentValue + 1));
			}

			/**
			 * Decrements the quantity of menu items wanted.
			 */
			private void decrementQty() {
				int currentValue = Integer.parseInt("" + mInputQty.getText());
				// Handle the edge case
				if (currentValue == 0) {
					return;
				}

				mInputQty.setText("" + (currentValue - 1));
			}
			
			/**
			 * Clear the quantity of the menu item.
			 */
			void clearQuantity() {
				mInputQty.setText(ZERO);
			}
		}
	}

	/**
	 * This is a listener that is specific to this fragment.
	 * Any activity that wants to host this fragment must implement 
	 * the required methods.
	 * @author mhotan
	 */
	public interface MenuItemListListener extends ImageObtainable, RestaurantRetrievable {

		/**
		 * User request more detail about this menu item.
		 * @param menuItem String
		 */
		public void onMenuItemFocusedOn(MenuItem menuItem);

		/**
		 * User commits to adding this menu item to his/her pending order.
		 * User does commit to the order but instead commits to the 
		 * @param item Item to to add
		 * @param quantity Quantity to give to the item.
		 */
		public void onMenuItemAddToOrder(MenuItem item, int quantity);
	}
}
