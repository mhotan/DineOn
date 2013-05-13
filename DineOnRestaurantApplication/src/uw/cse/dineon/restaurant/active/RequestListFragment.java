package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A List fragment that contains pending Request.
 * @author mhotan
 */
public class RequestListFragment extends ListFragment {

	private static final String TAG = RequestListFragment.class.getSimpleName();

	private RequestItemListener mListener;
	
	private ArrayAdapter<CustomerRequest> mAdapter;
	
//	private static List<CustomerRequest> mRequests;
	
//	/**
//	 * Creates a new customer list fragment.
//	 * @param requests list of requests
//	 * @return new fragment
//	 */
//	public static RequestListFragment newInstance(List<CustomerRequest> requests) {
//		RequestListFragment frag = new RequestListFragment();
//		ArrayList<CustomerRequest> mList = new ArrayList<CustomerRequest>();
//		if (requests != null) {
//			mList.addAll(requests);
//		}
//		
//		Bundle b = new Bundle();
//		
//		if(requests != null) {
//			mRequests.addAll(requests);			
//		} else {
//			mRequests = null;
//		}
//		
//		frag.addAll(requests);
//		frag.setArguments(b);
//		return frag;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Obtain the current Requests
		List<CustomerRequest> requests = mListener.getCurrentRequests();
		if (requests == null) {
			requests = new ArrayList<CustomerRequest>();
		}

		mAdapter = new RequestListAdapter(this.getActivity(), requests);
		setListAdapter(mAdapter);	
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RequestItemListener) {
			mListener = (RequestItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RequestListFragment.RequestItemListener");
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
		 * Dismisses the request.  Doesn't mean issue was tended to
		 * @param request request to dismiss
		 */
		public void onDismissRequest(CustomerRequest request);

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
		private final ArrayList<String> mStaff;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders.
		 * @param ctx Context
		 * @param orders List of orders
		 */
		public RequestListAdapter(Context ctx, List<CustomerRequest> orders) {
			super(ctx, R.layout.listitem_restaurant_request, orders);
			this.mContext = ctx;
			this.mRequests = orders;
			
			// For debug purposes we will add fake staff members
			mStaff = new ArrayList<String>();
			mStaff.add("Bert");
			mStaff.add("Ernie");
			mStaff.add("Big Bird");
			mStaff.add("Elmo");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_restaurant_request, parent, false);
			
			CustomerRequest request = mRequests.get(position);
			
			TextView title = (TextView) view.findViewById(R.id.label_request_title);
			title.setText(request.getDescription());
			
			ImageButton reqDetail = (ImageButton) view.findViewById(R.id.button_request_detail);
			ImageButton sendToStaff = (ImageButton) view.findViewById(R.id.button_send_to_staff);
			CheckBox dismissBox = (CheckBox) view.findViewById(R.id.checkBox_dismiss_request);
			Spinner staff = (Spinner) view.findViewById(R.id.spinner_staff_to_assign);
			staff.setAdapter(new ArrayAdapter<String>(getActivity(), 
					android.R.layout.simple_list_item_1, mStaff));
			
			AllAroundListener listener = new AllAroundListener(request,
					staff,
					sendToStaff,
					reqDetail,
					dismissBox);
			
			reqDetail.setOnClickListener(listener);
			sendToStaff.setOnClickListener(listener);
			dismissBox.setOnCheckedChangeListener(listener);
			
			return view;
		}

		/**
		 * Listener to handle the use of buttons on request items.
		 * @author mhotan
		 */
		private class AllAroundListener implements 
		View.OnClickListener, CompoundButton.OnCheckedChangeListener {

			private final CustomerRequest mRequest;
			private final Spinner mStaff;
			private final ImageButton mAssignToStaff, mDetailButton;
			private final CheckBox mDismiss;
			private boolean mDelete;

			/**
			 * 
			 * @param request String
			 * @param staff Spinner
			 * @param assignToStaff ImageButton
			 * @param detailButton ImageButton
			 * @param dismiss CheckBox
			 */
			public AllAroundListener(
					CustomerRequest request,
					Spinner staff,
					ImageButton assignToStaff,
					ImageButton detailButton,
					CheckBox dismiss) {

				mRequest = request;
				mStaff = staff;
				mAssignToStaff = assignToStaff;
				mDetailButton = detailButton;
				mDismiss = dismiss;
				mDelete = false;

				mAssignToStaff.setOnClickListener(this);
				mDetailButton.setOnClickListener(this);
				mDismiss.setOnCheckedChangeListener(this);
				
				// Default selection is first on the list
				mStaff.setSelection(0);
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// Is checked == show delete button
				mDelete = isChecked;
				if (mDelete) {
					mAssignToStaff.setImageResource(R.drawable.discard_content);
					mListener.onDismissRequest(mRequest);
				} else {
					mAssignToStaff.setImageResource(R.drawable.add_staffmember);
				}
			}

			@Override
			public void onClick(View v) {
				if (v == mAssignToStaff) {
					if (mDelete) { // User wants to delete this request
						mAdapter.remove(mRequest);
						mListener.onRemoveRequest(mRequest);
						mAdapter.notifyDataSetChanged();
					} else { // User wants to assign this request to a staff member
						String staffMem = mStaff.getSelectedItem().toString();
						mListener.onAssignStaffToRequest(mRequest, staffMem);
					}

				} else if (v == mDetailButton) {
					// User just wants details about this request
					mListener.onRequestRequestDetail(mRequest);
				}
			}

		}

	}


}
