package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
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

	private static final String SESSIONS = TAG + "_sessions";

	private DiningSessionListAdapter mAdapter;

	/**
	 * Creates a new customer list fragment.
	 * @param sessions List of DiningSessions
	 * @return New CustomerListFragment
	 */
	public static DiningSessionListFragment newInstance(List<DiningSession> sessions) {
		DiningSessionListFragment frag = new DiningSessionListFragment();
		Bundle args = new Bundle();

		DiningSession[] dsArray;
		if (sessions == null) {
			dsArray = new DiningSession[0];
		} else {
			dsArray = new DiningSession[sessions.size()];
			for (int i = 0; i < sessions.size(); ++i) {
				dsArray[i] = sessions.get(i);
			}
		}

		args.putParcelableArray(SESSIONS, dsArray);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		DiningSession[] dsArray = null;
		if (savedInstanceState != null // From saved instance
				&& savedInstanceState.containsKey(SESSIONS)) {
			dsArray = (DiningSession[])savedInstanceState.getParcelableArray(SESSIONS);
		} else if (getArguments() != null && getArguments().containsKey(SESSIONS)) {
			// Ugh have to convert to array for type reasons.
			// List are not contravariant in java... :-(
			dsArray = (DiningSession[])getArguments().getParcelableArray(SESSIONS);	
		}

		// Error check
		if (dsArray == null) {
			Log.e(TAG, "Unable to extract list of dining sessions");
			return;
		}

		// Must convert to array because this allows dynamic additions
		// Our Adapter needs a dynamic list.
		List<DiningSession> sessions = new ArrayList<DiningSession>(dsArray.length);
		for (DiningSession session : dsArray) {
			sessions.add(session);
		}

		mAdapter = new DiningSessionListAdapter(getActivity(), sessions);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ArrayList<DiningSession> sessions = mAdapter.getCurrentSessions();
		DiningSession[] dsArray = new DiningSession[sessions.size()];
		for (int i = 0; i < dsArray.length; ++i) {
			dsArray[i] = sessions.get(i);
		}
		outState.putParcelableArray(SESSIONS, dsArray);
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
		 * User request detail about a particular dining session.
		 * @param ds Dining session of focus
		 */
		void onDiningSessionDetailRequested(DiningSession ds);
		
	}

	/**
	 * List adapter for holding dining sessions.
	 * @author mhotan
	 */
	private class DiningSessionListAdapter extends BaseAdapter {

		private List<DiningSession> mDiningSessions;
		private int expanded = -1;
		private Context mContext;

		/**
		 * Constructs a new UserList Adapter.
		 * 
		 * @param context The current context
		 * @param sessionList The list of users to display
		 */
		public DiningSessionListAdapter(Context context, List<DiningSession> sessionList) {
			mContext = context;
			mDiningSessions = sessionList;
		}
		
		/**
		 * @return Current list of Dining Sessions.
		 */
		public ArrayList<DiningSession> getCurrentSessions() {
			return new ArrayList<DiningSession>(mDiningSessions);
		}

		@Override
		public int getCount() {
			return mDiningSessions.size();
		}

		@Override
		public Object getItem(int position) {
			return mDiningSessions.get(position);
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
		 * Sets the arrow based on whether or not the list item
		 * is expanded or not.
		 * @param position The position of the list item to set
		 * @param arrowButton The specified arrow button to set
		 */
		private void setArrow(int position, ImageButton arrowButton) {
			if(position == expanded) {
				arrowButton.setImageResource(R.drawable.navigation_next_item);
			} else {
				arrowButton.setImageResource(R.drawable.navigation_expand);
			}
		}

		/**
		 * 
		 * @param customer DiningSession
		 */
		public void add(DiningSession customer) {
			mDiningSessions.add(customer);
			Log.v(TAG, "Added customer " + customer);
			notifyDataSetChanged();
		}

		/**
		 * 
		 * @param customer DiningSession
		 */
		public void remove(DiningSession customer) {
			mDiningSessions.remove(customer);
			Log.v(TAG, "Removed customer " + customer);
			notifyDataSetChanged();
		}



		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LinearLayout vw;
			//use vw.findViewById(R.id.whatever) to get children views

			DiningSession mDiningSession = mDiningSessions.get(position);

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
			ImageButton arrowButton = (ImageButton) vwTop.findViewById(R.id.button_expand_user);
			setArrow(position, arrowButton);
			arrowButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					expand(position);
					if(expanded != position) {
						mListener.onDiningSessionDetailRequested(mDiningSessions.get(position));
						return;
					}
					
					ImageButton arrowButton = 
							(ImageButton) v.findViewById(R.id.button_expand_user);
					setArrow(position, arrowButton);
					//Right arrow case -- goes to details fragment
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

			List<UserInfo> infolist = mDiningSessions.get(position).getUsers();
			if(infolist != null && infolist.size() > 0 && infolist.get(0) != null) {
				UserInfo ui = infolist.get(0);
				name = ui.getName();
			} else {
				Log.w(TAG, "Could not retrieve name for position: " + position);
				name = "No Customer!";
			}

			custName.setText(name);

			//Displays Order information as a string
			StringBuffer buf = new StringBuffer();
			
			for (Order o : mDiningSession.getOrders()) {

				int tableID = o.getTableID();
				if(tableID != -1) {
					buf.append("Table ");
					buf.append(tableID);
				}

				buf.append("\n");
				buf.append(o.getOriginatingTime().toString());
				buf.append("\n\n");
			}
			TextView orderText = (TextView) vwBot.findViewById(R.id.label_user_order_content);
			orderText.setText(buf.toString());

			return vw;
		}
	}
}