package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A List fragment that contains pending Request.
 * @author mhotan
 */
public class RequestListFragment extends ListFragment {

	private static final String TAG = RequestListFragment.class.getSimpleName();
	private static final String REQUESTS = TAG + "_requests";

	private RequestItemListener mListener;

	private RequestListAdapter mAdapter;

	/**
	 * Creates a new customer list fragment.
	 * @param requests list of requests
	 * @return new fragment
	 */
	public static RequestListFragment newInstance(List<CustomerRequest> requests) {
		RequestListFragment frag = new RequestListFragment();
		Bundle args = new Bundle();

		CustomerRequest[] requestsArray;
		if (requests == null) {
			requestsArray = new CustomerRequest[0];
		} else {
			requestsArray = new CustomerRequest[requests.size()];
			for (int i = 0; i < requests.size(); ++i) {
				requestsArray[i] = requests.get(i);
			}
		}

		args.putParcelableArray(REQUESTS, requestsArray);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CustomerRequest[] requestArray = null;
		if (savedInstanceState != null // From saved instance
				&& savedInstanceState.containsKey(REQUESTS)) {
			requestArray = (CustomerRequest[])savedInstanceState.getParcelableArray(REQUESTS);
		} else if (getArguments() != null && getArguments().containsKey(REQUESTS)) {
			// Ugh have to convert to array for type reasons.
			// List are not contravariant in java... :-(
			requestArray = (CustomerRequest[])getArguments().getParcelableArray(REQUESTS);	
		}

		// Error check
		if (requestArray == null) {
			Log.e(TAG, "Unable to extract list of orders");
			return;
		}

		// Obtain the current Requests
		List<CustomerRequest> requests = new ArrayList<CustomerRequest>(requestArray.length);
		for (CustomerRequest request : requestArray) {
			requests.add(request);
		}

		mAdapter = new RequestListAdapter(this.getActivity(), requests);
		setListAdapter(mAdapter);	
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		ArrayList<CustomerRequest> requests = mAdapter.getCurrentRequests();
		CustomerRequest[] requestArray = new CustomerRequest[requests.size()];
		for (int i = 0; i < requestArray.length; ++i) {
			requestArray[i] = requests.get(i);
		}
		outState.putParcelableArray(REQUESTS, requestArray);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RequestItemListener) {
			mListener = (RequestItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RequestListFragment.RequestItemListener");
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities can use
	//// to set the values of what is showed to the user for this 
	//// fragment
	//////////////////////////////////////////////////////

	/**
	 * Adds request to this view.
	 * @param request String
	 */
	public void addRequest(CustomerRequest request) {
		mAdapter.add(request);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Adds all the requests to this view.
	 * @param request Collection of Strings
	 */
	public void addAll(Collection<CustomerRequest> request) {
		for (CustomerRequest o: request) {
			mAdapter.add(o);
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Deletes this request if it finds it.
	 * @param request String
	 */
	public void deleteRequest(CustomerRequest request) {
		mAdapter.remove(request);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Clears all requests.
	 */
	public void clearRequest() {
		mAdapter.clear();
		mAdapter.notifyDataSetChanged();
	}

	//////////////////////////////////////////////////////
	//// Following is the interface in which activities
	//// that wish to attach this Fragment must implement
	//// Intended to use for user input
	//////////////////////////////////////////////////////

	/**
	 * Mandatory Listener for this Fragment class.
	 * @author mhotan
	 */
	public interface RequestItemListener {

		/**
		 * Request detail information to be presented
		 * about the specific request.
		 * @param request request to get detail 
		 */
		public void onRequestRequestDetail(CustomerRequest request);

		/**
		 * Assign the staffmember to handle the request.
		 * @param request request to handle 
		 * @param staff staff member to assign to request
		 */
		public void onAssignStaffToRequest(CustomerRequest request, String staff);

		/**
		 * Removes a request. request is removed completely from this 
		 * list.  This is a notification method 
		 * @param request String
		 */
		public void onRemoveRequest(CustomerRequest request);

		/**
		 * Used to get the most recent up to date list of items to show.
		 * Cannot return null
		 * @return List of requests to show
		 */
		public List<CustomerRequest> getCurrentRequests();

	}

	//////////////////////////////////////////////////////
	//// Adapter to handle using list items specific to 
	//// Showing request for restaurants
	//////////////////////////////////////////////////////

	/**
	 * Adpater to handle request management and layout.
	 * @author mhotan
	 */
	private class RequestListAdapter extends ArrayAdapter<CustomerRequest> {

		private final Context mContext;
		private final List<CustomerRequest> mRequests;
		private final Map<View, CustomerRequest> mViewToCustomerRequest;
		private final ArrayList<String> mStaff;
		private final Spinner mSpinner;
		int expanded = -1;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders.
		 * @param ctx Context
		 * @param orders List of orders
		 */
		public RequestListAdapter(Context ctx, List<CustomerRequest> orders) {
			super(ctx, R.layout.listitem_restaurant_request_bot, orders);
			this.mContext = ctx;
			this.mRequests = orders;
			this.mViewToCustomerRequest = new HashMap<View, CustomerRequest>();
			// For debug purposes we will add fake staff members
			mStaff = new ArrayList<String>();
			mSpinner = new Spinner(ctx);
			mStaff.add("Bert");
			mStaff.add("Ernie");
			mStaff.add("Big Bird");
			mStaff.add("Elmo");
		}

		/**
		 * @return Returns current list of requests.
		 */
		public ArrayList<CustomerRequest> getCurrentRequests(){
			return new ArrayList<CustomerRequest>(mRequests);
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


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View vwTop;
			View vwBot;

			final LinearLayout VIEW;
			final CustomerRequest REQUEST = mRequests.get(position);

			if(convertView == null) {
				VIEW = new LinearLayout(mContext);
				VIEW.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_request_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_request_bot, null, true);
				VIEW.addView(vwTop);
				VIEW.addView(vwBot);
			} else {
				//Everything already created, just find them
				VIEW = (LinearLayout) convertView;
				vwTop = VIEW.findViewById(R.id.listitem_request_top);
				vwBot = VIEW.findViewById(R.id.listitem_request_bot);
			}

			TextView title = (TextView) VIEW.findViewById(R.id.label_request_title);
			title.setText(REQUEST.getDescription() + " - " + REQUEST.getUserInfo().getName());

			TextView time = (TextView) VIEW.findViewById(R.id.label_request_time);
			time.setText(REQUEST.getOriginatingTime().toString());

			ImageButton arrowButton = (ImageButton) vwTop.findViewById(R.id.button_expand_request);
			setArrow(position, arrowButton);

			ImageButton assignStaffButton = (ImageButton) vwBot.findViewById(R.id.button_assign);

			// Add the onclick listener that listens 
			arrowButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					expand(position);

					ImageButton arrowButton = 
							(ImageButton) VIEW.findViewById(R.id.button_expand_request);
					setArrow(position, arrowButton);
					//Right arrow case -- goes to details fragment
					if(expanded != position) {
						mListener.onRequestRequestDetail(mRequests.get(position));
					}				
				}
			});

			if(expanded != position) {
				vwBot.setVisibility(View.GONE);
			} else {
				vwBot.setVisibility(View.VISIBLE);
			}


			ImageButton remove = (ImageButton) VIEW.findViewById(R.id.button_remove);

			Spinner spinner = (Spinner) VIEW.findViewById(
					R.id.spinner_staff_to_assign);

			//TODO: Load assigned staff into spinner
			
			spinner.setAdapter(new ArrayAdapter<String>(getActivity(), 
					android.R.layout.simple_list_item_1, mStaff));

			// Add to mapping to reference later
			mViewToCustomerRequest.put(remove, REQUEST);
			mViewToCustomerRequest.put(arrowButton, REQUEST);
			mViewToCustomerRequest.put(spinner, REQUEST);
			remove.setOnClickListener(new AllAroundListener(spinner));
			assignStaffButton.setOnClickListener(new AllAroundListener(spinner));

			return VIEW;
		}

		/**
		 * Listener to handle the use of buttons on request items.
		 * @author mhotan
		 */
		private class AllAroundListener implements 
		View.OnClickListener {

			private final Spinner mSpinner;

			/**
			 * 
			 * @param spinner Spinner
			 */
			public AllAroundListener(Spinner spinner) {

				mSpinner = spinner;

				// Default selection is first on the list
				mSpinner.setSelection(0);
			} 

			@Override
			public void onClick(View v) {

				CustomerRequest request = mViewToCustomerRequest.get(v);

				switch (v.getId()) {
				case R.id.button_remove:
					mAdapter.remove(request);
					mListener.onRemoveRequest(request);
					mAdapter.notifyDataSetChanged();
					break;
				case R.id.button_assign:
					String staffMem = mSpinner.getSelectedItem().toString();
					mListener.onAssignStaffToRequest(request, staffMem);
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}

			}

		}

	}


}
