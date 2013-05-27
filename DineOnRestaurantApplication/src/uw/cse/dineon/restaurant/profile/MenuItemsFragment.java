package uw.cse.dineon.restaurant.profile;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Fragment that presents a editable list of menu items of this restaurant.
 * 
 * @author mhotan
 */
@SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public class MenuItemsFragment extends ListFragment {

	/**
	 * Adapter for restaurant menu Item adapter. Use this to add new Menuitems
	 */
	private RestaurantMenuItemAdapter mAdapter;

	private static final String TAG = "MenuItemsFragment";

	private List<String> menuTitles;

	/**
	 * Activity listener.
	 */
	private MenuItemListener mListener;

	private Menu currentMenu;

	public AlertDialog newItemAlert; // for testing. Otherwise can't access
	public AlertDialog newMenuAlert; // for testing. Otherwise can't access

	/**
	 * Creates a MenuItemsFragment that is ready to build and view.
	 * 
	 * @param info
	 *            Restaurant that contains a group of menus that each contain
	 *            menu items
	 * @return A MenuItemsFragment that is ready to display all the items of the
	 *         restaurant
	 */
	public static MenuItemsFragment newInstance(RestaurantInfo info) {
		// Prepare a Bundle argument
		// for starting an activity with
		MenuItemsFragment frag = new MenuItemsFragment();
		Bundle args = new Bundle();
		// args.putParcelable(DineOnConstants.KEY_RESTAURANTINFO, info);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// IF there are arguments
		// then check if there is a restaurant info instance
		// info can be null
		/*
		 * RestaurantInfo info = getArguments() != null ? (RestaurantInfo)
		 * getArguments() .getParcelable(DineOnConstants.KEY_RESTAURANTINFO) :
		 * null;
		 */
		final RestaurantInfo INFO = mListener.getInfo();

		// If arguments existed and it included a Restaurant Info
		// Proceed
		if (isValid(INFO)) {

			// TODO Handle multiple menus
			if (INFO.getMenuList().size() < 1) {
				Menu defaultMenu = new Menu("Default");
				INFO.getMenuList().add(defaultMenu);
				defaultMenu.saveInBackGround(null);
				Log.d(TAG, "No menu exists, created a default menu!");
			}
			currentMenu = INFO.getMenuList().get(0);

			// Make list of menu titles for future reference
			menuTitles = new ArrayList<String>();
			for (Menu m : INFO.getMenuList()) {
				menuTitles.add(m.getName());
			}

			List<MenuItem> menuitems = currentMenu.getItems();
			mAdapter = new RestaurantMenuItemAdapter(getActivity(), menuitems);
			setListAdapter(mAdapter);
		} else {
			List<String> defList = new ArrayList<String>();
			defList.add("Illegal Restaurant Info State");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_list_item_1, defList);
			setListAdapter(adapter);
		}
		updateTitle();
	}

	/**
	 * Updates the title to reflect the current menu.
	 */
	private void updateTitle() {
		getActivity().getActionBar().setTitle("Menu: " + currentMenu.getName());
	}

	@Override
	public void onCreateOptionsMenu(android.view.Menu menu,
			MenuInflater inflater) {
		// TODO Add your menu entries here
		inflater.inflate(R.menu.menu_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.menu_add_menu_item) {
			makeNewMenuItem();
			return true;
		} else if (item.getItemId() == R.id.menu_switch_menu) {
			switchMenus();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Helper function to prompt the user for new menu item details and handle
	 * saving/consistency.
	 */
	private void makeNewMenuItem() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add New Menu Item");
		alert.setMessage("Input Menu Item Details");
		final View AV = getLayoutInflater(getArguments()).inflate(
				R.layout.alert_new_menuitem, null);

		alert.setView(AV);
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int arg1) {
				String title = ((EditText) AV
						.findViewById(R.id.input_menuitem_title)).getText()
						.toString();
				String desc = ((EditText) AV
						.findViewById(R.id.input_menuitem_desc)).getText()
						.toString();
				String priceString = ((EditText) AV
						.findViewById(R.id.input_menuitem_price)).getText()
						.toString();
				if (title.trim().equals("") || priceString.equals("")) {
					Toast.makeText(getActivity(), "Please input Title and Price",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				double price = Double.parseDouble(priceString);

				MenuItem mi = new MenuItem(mAdapter.getCount() + 1, price,
						title, desc);

				currentMenu.addNewItem(mi);
				mAdapter.add(mi);
				mAdapter.notifyDataSetChanged();

				mListener.onMenuItemAdded(mi);
				Log.v(TAG, "Adding new menu item");
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Do nothing
					}
				});
		this.newItemAlert = alert.show();
	}

	/**
	 * Helper function to prompt user to pick a menu (or create new one).
	 */
	private void switchMenus() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add New Menu Item");
		alert.setMessage("Input Menu Item Details");
		final View AV = getLayoutInflater(getArguments()).inflate(
				R.layout.alert_select_menu, null);

		alert.setView(AV);
		final Spinner SPINNER = (Spinner) AV
				.findViewById(R.id.spinner_select_menu);
		final ArrayAdapter<String> ADAPTER = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, menuTitles);
		SPINNER.setAdapter(ADAPTER);

		SPINNER.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> a, View v,
					int pos, long id) {
				if(pos > mListener.getInfo().getMenuList().size()) {
					Log.e(TAG, "Invalid menu index selected!");
				} else {
					currentMenu = mListener.getInfo().getMenuList().get(pos);
					mAdapter.notifyDataSetInvalidated();
					mAdapter = new RestaurantMenuItemAdapter(getActivity(),
							currentMenu.getItems());
					setListAdapter(mAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "Nothing selected");
			}
		});

		// Handle button to unhide new menu stuff
		Button showAddMenuButton = (Button) AV
				.findViewById(R.id.button_new_menu);
		showAddMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AV.findViewById(R.id.container_new_menu).setVisibility(
						View.VISIBLE);
			}
		});

		ImageButton addMenu = (ImageButton) AV
				.findViewById(R.id.button_save_menu);
		addMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newTitle = ((TextView) AV
						.findViewById(R.id.input_new_menu_title)).getText()
						.toString();
				((TextView) AV.findViewById(R.id.input_new_menu_title))
						.setText("");
				if (newTitle.trim().equals("")) {
					Toast.makeText(getActivity(), "Please input title",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Menu newMenu = new Menu(newTitle);
				menuTitles.add(newTitle);
				mListener.getInfo().addMenu(newMenu);
				SPINNER.setSelection(SPINNER.getCount() - 1);
				// Switch spinner to last item added
			}
		});

		alert.setPositiveButton("Select",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d, int x) {
						updateTitle();
					}
				});

		this.newMenuAlert = alert.show();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuItemListener) {
			mListener = (MenuItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MenuItemsFragment.MenuItemListener");
		}
	}

	/**
	 * Checks whether the current restaurant info is a valid. restaurant info
	 * instance
	 * 
	 * @param info
	 *            info to be analyzed
	 * @return true if it is valid
	 */
	private boolean isValid(RestaurantInfo info) {
		if (info == null) {
			return false;
		}
		return true;
	}

	// ////////////////////////////////////////////////////
	// // Following is the interface in which activities
	// // that wish to attach this Fragment must implement
	// // Intended to use for user input
	// ////////////////////////////////////////////////////

	/**
	 * Interface for all owning activities to implement.
	 * 
	 * @author mhotan
	 */
	public interface MenuItemListener {

		/**
		 * Notifies that the user chooses to delete the current menu item.
		 * 
		 * @param item
		 *            menu item to delete
		 */
		void onMenuItemDeleted(MenuItem item);

		/**
		 * Notifies that the user chose to add the current menu item.
		 * 
		 * @param item
		 *            menu item to add
		 */
		void onMenuItemAdded(MenuItem item);

		// XXX Seems like this might be redundant right now

		/**
		 * Notifies that the user chose to modify the current item.
		 * 
		 * @param item
		 *            menu item to modify
		 */
		void onMenuItemModified(MenuItem item);

		/**
		 * @return RestaurantInfo
		 */
		RestaurantInfo getInfo();

		/**
		 * 
		 * @param view
		 * @param item
		 */
		void onTakePicture(final ImageView view, MenuItem item);
			
		/**
		 * Fragment asks get a picture for this menu item from gallery.
		 * @param view View to place image in when 
		 * @param item
		 */
		void onChoosePicture(final ImageView view, MenuItem item);
	}

	/**
	 * Adapter to handle the modification of menu items.
	 * 
	 * @author mhotan
	 */
	private class RestaurantMenuItemAdapter extends ArrayAdapter<MenuItem> {

		/**
		 * Context to use this adapter.
		 */
		private final Context mContext;
		
		private final List<MenuItem> mItems;
		
		private final NumberFormat mCurrencyFormatter;

		/**
		 * Creates a menu item adapter for displaying, modifying, and deleting
		 * menu items.
		 * 
		 * @param ctx
		 *            Context to use adapter
		 * @param items
		 *            items to add to this adapter
		 */
		public RestaurantMenuItemAdapter(Context ctx, List<MenuItem> items) {
			super(ctx, R.layout.listitem_menuitem_editable, items);
			mContext = ctx;
			// Update the time
			mItems = items;
			mCurrencyFormatter = NumberFormat.getCurrencyInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= this.getCount()) {
				super.getView(position, convertView, parent);
			}
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view;
			if (convertView != null) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.listitem_menuitem_editable,
						parent, false);
			}

			// Obtain the view used for this menu item
			ImageView image = (ImageView) view
					.findViewById(R.id.image_thumbnail_menuitem);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			TextView title = (TextView) view
					.findViewById(R.id.label_menuitem_title);
//			ImageButton delete = (ImageButton) view
//					.findViewById(R.id.button_menuitem_delete);
			TextView description = (TextView) view
					.findViewById(R.id.label_menuitem_desc);
			TextView price = (TextView) view
					.findViewById(R.id.label_menuitem_price);

			MenuItem item = super.getItem(position);
			title.setText(item.getTitle());
			description.setText(item.getDescription());
			price.setText(mCurrencyFormatter.format(item.getPrice()));
			return view;
		}
		
		/**
		 * 
		 * @param view View to place image
		 * @param item Item to ask for image
		 * @return Get a dailog that will handle getting images for a menu item
		 */
		private AlertDialog getRequestImageDialog(final ImageView view, final MenuItem item) {
			AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
			builder.setTitle(R.string.dialog_title_getimage);
			builder.setMessage(R.string.dialog_message_getimage_for_menuitem);
			builder.setPositiveButton(R.string.dialog_option_take_picture, 
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onTakePicture(view, item);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_option_choose_picture, 
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onChoosePicture(view, item);
					dialog.dismiss();
				}
			});
			builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			return builder.create();
		}
		
	}
}
