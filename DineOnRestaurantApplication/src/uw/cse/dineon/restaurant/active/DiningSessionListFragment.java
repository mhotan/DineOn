package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * For displaying current restaurant customers.
 * TODO Improve and complete
 * @author mhotan
 * @author blasv
 */
public class DiningSessionListFragment extends ListFragment {

	/**
	 * For Use with Log functions.
	 */
	private static final String TAG = DiningSessionListFragment.class.getSimpleName();

	//An activity that implements the required listener functions
	private DiningSessionListListener mListener;

	//String for storing list in arguments
//	private static final String KEY_LIST = "MY LIST";

	private DiningSessionListAdapter mAdapter;

//	/**
//	 * Creates a new customer list fragment.
//	 * @param sessions List of DiningSessions
//	 * @return New CustomerListFragment
//	 */
//	public static DiningSessionListFragment newInstance(List<DiningSession> sessions) {
//		DiningSessionListFragment frag = new DiningSessionListFragment();
//		ArrayList<DiningSession> mList = new ArrayList<DiningSession>();
//		if (sessions != null) {
//			mList.addAll(sessions);
//		}
//
//		//Store list in arguments for future retrieval
//		Bundle args = new Bundle();
////		args.putParcelableArrayList(KEY_LIST, mList);
//		frag.setArguments(args);
//		return frag;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<DiningSession> sessions = mListener.getCurrentSessions();
		if (sessions == null) {
			sessions = new ArrayList<DiningSession>();
		}

		mAdapter = new DiningSessionListAdapter(getActivity(), sessions);
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DiningSessionListListener) {
			mListener = (DiningSessionListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CurrentCustomersFragment.CustomerListener");
		}
	}

	/**
	 * @param session DiningSession
	 */
	public void addDiningSession(DiningSession session) {
		if (mAdapter != null) {
			mAdapter.add(session);
		} else {
			Log.d(TAG, "Attempted to add customer to nonexistent list!");
		}
	}

	/**
	 * 
	 * @param session DiningSession
	 */
	public void removeDiningSession(DiningSession session) {
		if (mAdapter != null) {
			mAdapter.remove(session);
		} else {
			Log.d(TAG, "Attempted to remove customer from nonexistent list!");
		}
	}

	/**
	 * Mandatory interface for this fragment.
	 * @author mhotan
	 */
	public interface DiningSessionListListener {

		/**
		 * Retrieves the current diningsessions.
		 * 
		 * @return list of DiningSessions, or null if no restaurant is available
		 */
		public List<DiningSession> getCurrentSessions();

		/**
		 * TODO Add more methods here as needed
		 * To complete the full functionality of this fragment
		 * These methods will probably be methods that signify the 
		 * user wants do conduct a certain action for this User
		 * 
		 * IE: Send the user a message
		 */
	}
	
	/**
	 * List adapter for holding dining sessions.
	 * @author mhotan
	 */
	private class DiningSessionListAdapter extends BaseAdapter {
		
		private List<DiningSession> users;
		private int expanded = -1;
		private Context mContext;
		
		/**
		 * Constructs a new UserList Adapter.
		 * 
		 * @param context The current context
		 * @param userlist The list of users to display
		 */
		public DiningSessionListAdapter(Context context, List<DiningSession> userlist) {
			mContext = context;
			users = userlist;
		}

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return users.get(position);
		}

		@Override
		//Get unique "ID" associated with the element at the given position
		//For now we just use the position as the ID
		public long getItemId(int position) {
			return position;
			// XXX This will likely break if we start reordering the list
			// (which isn't a big deal if we never use the method)
		}
		
		/**
		 * Toggles expansion of the given list item.
		 * Currently only one item can be expanded at a time
		 * @param position The position of the list item to toggle
		 */
		public void expand(int position) {
			if(expanded == position) { //Already expanded, collapse it
				expanded = -1;
			} else {
				expanded = position;
			}
			notifyDataSetChanged();
		}
		
		/**
		 * 
		 * @param customer DiningSession
		 */
		public void add(DiningSession customer) {
			users.add(customer);
			Log.v(TAG, "Added customer " + customer);
			notifyDataSetChanged();
		}
		
		/**
		 * 
		 * @param customer DiningSession
		 */
		public void remove(DiningSession customer) {
			users.remove(customer);
			Log.v(TAG, "Removed customer " + customer);
			notifyDataSetChanged();
		}
		
		

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LinearLayout vw;
			//use vw.findViewById(R.id.whatever) to get children views
			
			View vwTop;
			View vwBot;
			if (convertView == null) {
				//Initialize and verticalize parent container viewgroup
				vw = new LinearLayout(mContext);
				vw.setOrientation(LinearLayout.VERTICAL);
				
				//inflate views from xml and add to parent view
				LayoutInflater inflater = 
						(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_user_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_user_bot, null, true);
				vw.addView(vwTop);
				vw.addView(vwBot);
			} else {
				//Everything already created, just find them
				vw = (LinearLayout) convertView;
				vwTop = vw.findViewById(R.id.listitem_user_top);
				vwBot = vw.findViewById(R.id.listitem_user_bot);
			}
			//Set up expand button
			ImageButton expand = (ImageButton) vwTop.findViewById(R.id.button_expand_user);
			
			expand.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 expand(position);
	            	 Log.v(TAG, "Toggled position " + position);
	            	 //Stick *that* in your closure and smoke it
	             }
	         });
			
			if(expanded != position) {
				vwBot.setVisibility(View.GONE);
			} else {
				vwBot.setVisibility(View.VISIBLE);
			}
			
			//TODO Pull actual info from a UserInfo object
			TextView custName = (TextView) vwTop.findViewById(R.id.label_user_name);
			
			String name;
			String phone;
			
			List<UserInfo> infolist = users.get(position).getUsers();
			if(infolist != null && infolist.size() > 0 && infolist.get(0) != null) {
				UserInfo ui = infolist.get(0);
				name = ui.getName();
				phone = ui.getPhone();
			} else {
				Log.w(TAG, "Could not retrieve name for position: " + position);
				name = "No Customer!";
				phone = "No phone?";
			}
	
			custName.setText(name);
			
			TextView infotext = (TextView) vwBot.findViewById(R.id.label_user_info);
			infotext.setText(phone);
			
			
			return vw;
		}
		
		
		
	}
	
}
