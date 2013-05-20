package uw.cse.dineon.user.restaurant.home;

import java.util.ArrayList;
import java.util.List;

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
	
	private static final String INFORMATION = "Information";

	private final String TAG = this.getClass().getSimpleName();

	private ReferenceDataListener mListener;
	
	private RestaurantInfo mRestaurantInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu_and_info_selection,
				container, false);

		ViewPager pager = (ViewPager)view.findViewById(R.id.pager_menu_info);

		// Use activity's Fragment Manager
		RestaurantMenuCategoryAdapter adapter = 
				new RestaurantMenuCategoryAdapter(getFragmentManager(), this.mRestaurantInfo);

		pager.setAdapter(adapter);
		
		if (this.mListener != null) {
			this.mRestaurantInfo = this.mListener.getCurrentRestaurant();
		}

		// Set initial page to the menu page
		pager.setCurrentItem(0);
		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ReferenceDataListener) {
			mListener = (ReferenceDataListener) activity;
			this.mRestaurantInfo = this.mListener.getCurrentRestaurant();
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
	public class RestaurantMenuCategoryAdapter extends FragmentPagerAdapter {
		
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
			Bundle data = new Bundle();
			// make sure position is within bounds
			position = Math.min(Math.max(position, 0), this.mRestaurantInfo.getMenuList().size());
			switch (position) {
			case 0: // Show restaurant info
				f = new RestaurantInfoFragment();
				data.putParcelable(RestaurantInfoActivity.EXTRA_RESTAURANT, 
						mListener.getCurrentRestaurant());
				f.setArguments(data);
				break;
			default:
				f = new SubMenuFragment();
				data.putParcelable(SubMenuFragment.EXTRA_MENU, 
						this.mRestaurantInfo.getMenuList().get(position - 1));
				f.setArguments(data);
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
