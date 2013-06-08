package uw.cse.dineon.user.restaurant.home;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.R;
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

	private static final String INFORMATION = "Information";

	private static final String TAG = RestaurantHomeMainFragment.class.getSimpleName();

	private RestaurantRetrievable mListener;

	private RestaurantInfo mRestaurantInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu_and_info_selection,
				container, false);

		ViewPager pager = (ViewPager)view.findViewById(R.id.pager_menu_info);

		// Here we know the activity has been created therefore there
		// must be a restaurant to focus on
		mRestaurantInfo = mListener.getCurrentRestaurant();

		// Use activity's Fragment Manager
		RestaurantMenuCategoryAdapter adapter = 
				new RestaurantMenuCategoryAdapter(getFragmentManager(), this.mRestaurantInfo);
		pager.setAdapter(adapter);

		// Set initial page to the menu page
		pager.setCurrentItem(0);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantRetrievable) {
			mListener = (RestaurantRetrievable) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RestaurantRetrievable");
		}
	}

	/**
	 * The adapter is in charge for managing the transitions between
	 * fragments of a particular view.
	 * @author mhotan
	 */
	public static class RestaurantMenuCategoryAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragments;
		private RestaurantInfo mRestaurantInfo;

		/**
		 * 
		 * @param fm FragmentManager
		 * @param r RestaurantInfo
		 */
		public RestaurantMenuCategoryAdapter(FragmentManager fm, RestaurantInfo r) {
			super(fm);
			this.mRestaurantInfo = r;
			this.mFragments = new ArrayList<Fragment>();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			// make sure position is within bounds
			position = Math.min(Math.max(position, 0), this.mRestaurantInfo.getMenuList().size());
			switch (position) {
			case 0: // Show restaurant info
				f = new RestaurantInfoFragment();
				break;
			default:
				f = SubMenuFragment.getInstance(position - 1);
			}

			this.mFragments.add(position, f);
			return f;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0: 
				return INFORMATION;
			default:
				position = Math.max(Math.min(position - 1, 
						this.mRestaurantInfo.getMenuList().size() - 1), 0);
				return this.mRestaurantInfo.getMenuList().get(position).getName();
			}
		}

		@Override
		public int getCount() {
			return this.mRestaurantInfo.getMenuList().size() + 1;
		}
	}
}
