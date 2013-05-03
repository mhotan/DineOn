package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

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
 * For displaying current restaurant customers
 * TODO Improve and complete
 * @author mhotan
 * @author blasv
 */
@SuppressWarnings("unused")
public class CustomerListFragment extends ListFragment {

	/**
	 * For Use with Log functions
	 */
	private final String TAG = this.getClass().getSimpleName();

	//An activity that implements the required listener functions
	private CustomerListener mListener;

	//String for storing list in arguments
	private static final String KEY_LIST = "MY LIST";

	//TODO change string to User
	private UserListAdapter mAdapter;

	/**
	 * Creates a new customer list fragment.
	 * @param customers TODO Change to user class
	 * @return New CustomerListFragment
	 */
	public static CustomerListFragment newInstance(List<String> customers) {
		CustomerListFragment frag = new CustomerListFragment();
		ArrayList<String> mList = new ArrayList<String>();
		if (customers != null) {
			mList.addAll(customers);
		}

		//Store list in arguments for future retrieval
		Bundle args = new Bundle();
		args.putStringArrayList(KEY_LIST, mList);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Retrieve customer list from stored arguments if available
		List<String> mCustomers = getArguments() != null ? getArguments().getStringArrayList(KEY_LIST) : null;
		if (mCustomers == null){
			if (mListener != null)
				mCustomers = mListener.getCurrentUsers();
			else
				mCustomers = new ArrayList<String>(); // Empty
		}

		mAdapter = new UserListAdapter(getActivity(), mCustomers);
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof CustomerListener) {
			mListener = (CustomerListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CurrentCustomersFragment.CustomerListener");
		}
	}

	/**
	 * @param customer
	 */
	public void addCustomer(String customer) {
		if (mAdapter != null) {
			mAdapter.add(customer);
		} 
		else
			Log.d(TAG, "Attempted to add customer to nonexistant list!");
		
	}

	public void removeCustomer(String customer){
		if (mAdapter != null) {
			mAdapter.remove(customer);
		} else
			Log.d(TAG, "Attempted to remove customer from nonexistant list!");
	}

	/**
	 * Mandatory interface for this fragment
	 * @author mhotan
	 */
	public interface CustomerListener {

		/**
		 * Retrieves the current user 
		 * TODO Change to type User
		 * @return
		 */
		public List<String> getCurrentUsers();

		/**
		 * TODO Add more methods here as needed
		 * To complete the full functionality of this fragment
		 * These methods will probably be methods that signify the 
		 * user wants do conduct a certain action for this User
		 * 
		 * IE: Send the user a message
		 */
	}
	
	private class UserListAdapter extends BaseAdapter {
		
		private List<String> users;
		private int expanded = -1;
		private Context mContext;
		
		/**
		 * Constructs a new UserList Adapter
		 * 
		 * @param context The current context
		 * @param userlist The list of users to display
		 */
		public UserListAdapter(Context context, List<String> userlist){
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
		public void expand(int position){
			if(expanded == position){//Already expanded, collapse it
				expanded = -1;
			} else {
				expanded = position;
			}
			notifyDataSetChanged();
		}
		
		public void add(String customer) {
			users.add(customer);
			Log.v(TAG, "Added customer " + customer);
			notifyDataSetChanged();
		}
		
		public void remove(String customer) {
			users.remove(customer);
			Log.v(TAG, "Removed customer " + customer);
			notifyDataSetChanged();
		}
		
		

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LinearLayout vw;
			//use vw.findViewById(R.id.whatever) to get children views
			
			View vw_top;
			View vw_bot;
			if (convertView == null){
				//Initialize and verticalize parent container viewgroup
				vw = new LinearLayout(mContext);
				vw.setOrientation(LinearLayout.VERTICAL);
				
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
				vw_top = inflater.inflate(R.layout.listitem_restaurant_user_top, null, true);
				vw_bot = inflater.inflate(R.layout.listitem_restaurant_user_bot, null, true);
				vw.addView(vw_top);
				vw.addView(vw_bot);
			} else {
				//Everything already created, just find them
				vw = (LinearLayout) convertView;
				vw_top = vw.findViewById(R.id.listitem_user_top);
				vw_bot = vw.findViewById(R.id.listitem_user_bot);
			}
			//Set up expand button
			ImageButton expand = (ImageButton) vw_top.findViewById(R.id.button_expand_user);
			
			expand.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 expand(position);
	            	 Log.v(TAG, "Toggled position " + position);
	            	 //Stick *that* in your closure and smoke it
	             }
	         });
			
			if(expanded != position)
				vw_bot.setVisibility(View.GONE);
			else
				vw_bot.setVisibility(View.VISIBLE);
			
			//TODO Pull actual info from a User object
			TextView cust_name = (TextView) vw_top.findViewById(R.id.label_user_name);
			cust_name.setText(users.get(position));
			
			TextView infotext = (TextView) vw_bot.findViewById(R.id.label_user_info);
			infotext.setText("This text was modified!");
			
			
			return vw;
		}
		
		
		
	}
	
}
