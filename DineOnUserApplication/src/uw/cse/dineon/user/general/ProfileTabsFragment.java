package uw.cse.dineon.user.general;

import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author jpmcneal
 *
 */
public class ProfileTabsFragment extends Fragment {
	private FragmentTabHost mTabHost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTabHost = new FragmentTabHost(this.getActivity());	
		mTabHost.setup(this.getActivity(), this.getFragmentManager());
		Bundle arg = new Bundle();
		arg.putInt("Arg for Frag1", 1);
		mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"), 
				ProfileTabsFragment.NestedFrag1.class, arg);
		arg = new Bundle();
		arg.putInt("Arg for Frag2", 2);
		mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), 
				ProfileTabsFragment.NestedFrag2.class, arg);
		arg = new Bundle();
		arg.putInt("Arg for Frag1", 3);
		mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Tab 3"), 
				ProfileTabsFragment.NestedFrag3.class, arg);
		return mTabHost;
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * 
	 * @author jpmcneal
	 *
	 */
	public static class NestedFrag1 extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			return v;
		}

		@Override
		public void onPause() {
			super.onPause();
		}
		
		
	}
	
	/**
	 * 
	 * @author jpmcneal
	 *
	 */
	public static class NestedFrag2 extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			return v;
		}

		@Override
		public void onPause() {
			super.onPause();
		}		
	}
	
	/**
	 * 
	 * @author jpmcneal
	 *
	 */
	public static class NestedFrag3 extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			return v;
		}

		@Override
		public void onPause() {
			super.onPause();
		}		
		
	}

}
