package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantInfoActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantInfoFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This Fragment shows a swipable pager that can flip through
 * The restaurant Information page, And all other sub parts of the
 * menu.
 * 
 * It passes user interaction back to the user via its containing
 * interface 
 * 
 * @author mhotan
 */
public class RestaurantHomeMainFragment extends Fragment {

	private final String TAG = this.getClass().getSimpleName();

	private ReferenceDataListener mListener;

	private static final String[] CONTENT = new String[] { 
		"Information", "Entrees", "Appetizers", "Drink Menu", "Happy Hour" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu_and_info_selection,
				container, false);

		ViewPager pager = (ViewPager)view.findViewById(R.id.pager_menu_info);

		// Use activity's Fragment Manager
		RestaurantMenuCategoryAdapter adapter = 
				new RestaurantMenuCategoryAdapter(getFragmentManager());

		pager.setAdapter(adapter);

		// Set initial page to the menu page
		pager.setCurrentItem(1);
		
		// TODO Need to figure a process of passing the restaurant we chose
		// in the previous process and send to this containing context
		// However we cant assume the previous activity is RestaurantSelectionActivity
		// That wont always be true

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ReferenceDataListener) {
			mListener = (ReferenceDataListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RestaurantHomeMainFragment.ReferenceDataListener");
		}
	}

	/**
	 * Adapter class that manages the view.
	 * @author mhotan
	 *
	 */
	class RestaurantMenuCategoryAdapter extends FragmentPagerAdapter {

		/**
		 * 
		 * @param fm FragmentManager
		 */
		public RestaurantMenuCategoryAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			Bundle data = new Bundle();
			// make sure position is within bounds
			position = Math.min(Math.max(position, 0), CONTENT.length - 1);
			switch (position) {
			case 0: // Show restaurant info
				String restaurant = "FIXME: Restaurant should be here!";
				f = new RestaurantInfoFragment();
				data.putString(RestaurantInfoActivity.EXTRA_RESTAURANT, 
						mListener.getCurrentRestaurant().getName());
				f.setArguments(data);
				break;
			default:
				String menuType = CONTENT[Math.min(position, CONTENT.length - 1)];
				f = new SubMenuFragment();
				data.putString(SubMenuFragment.EXTRA_MENU_TYPE, menuType);
				f.setArguments(data);
			}
			
			return f;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			position = Math.max(Math.min(position, CONTENT.length - 1), 0);
			return CONTENT[position];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	/**
	 * TODO implement.
	 * @author mhotan
	 */
	public interface ReferenceDataListener {

		/**
		 * TODO change to Restaurant datatype.
		 * @return String
		 */
		public RestaurantInfo getCurrentRestaurant();

	}

}
