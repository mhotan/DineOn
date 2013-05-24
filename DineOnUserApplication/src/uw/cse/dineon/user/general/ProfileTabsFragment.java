package uw.cse.dineon.user.general;

import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileTabsFragment extends Fragment{
	private FragmentTabHost mTabHost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTabHost = new FragmentTabHost(this.getActivity());	
		mTabHost.setup(this.getActivity(), this.getFragmentManager());
		Bundle arg = new Bundle();
		arg.putInt("Arg for Frag1", 1);
		mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"), ProfileTabsFragment.nestedFrag1.class,arg);
		arg = new Bundle();
		arg.putInt("Arg for Frag2", 2);
		mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), ProfileTabsFragment.nestedFrag2.class,arg);
		arg = new Bundle();
		arg.putInt("Arg for Frag1", 3);
		mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Tab 3"), ProfileTabsFragment.nestedFrag3.class,arg);
		// TODO Auto-generated method stub
		return mTabHost;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public static class nestedFrag1 extends Fragment{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			// TODO Auto-generated method stub
			return v;
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}
		
		
	}
	
	public static class nestedFrag2 extends Fragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			// TODO Auto-generated method stub
			return v;
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}		
	}
	
	public static class nestedFrag3 extends Fragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_empty, container, false);
			// TODO Auto-generated method stub
			return v;
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}		
		
	}

}
