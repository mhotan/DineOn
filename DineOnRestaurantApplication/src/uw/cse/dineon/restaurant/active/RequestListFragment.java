package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.animation.ExpandAnimation;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;


/**
 * A List fragment that contains pending Request.
 * @author mhotan
 */
public class RequestListFragment extends ListFragment {

	private static final String TAG = RequestListFragment.class.getSimpleName();

	/**
	 * Listener that creates a reference back to the activity.
	 */
	private RequestItemListener mListener;

	/**
	 * Adapter that controls the presentation and change in request quantity.
	 */
	private RequestListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<CustomerRequest> requests = mListener.getCurrentRequests();
		if (requests == null) {
			Log.e(TAG, "List of Customer requests retrieved is null");
			requests = new ArrayList<CustomerRequest>();
		}
		
		//We can assert that the listener has a non null list of dining sessions
		mAdapter = new RequestListAdapter(getActivity(), requests);
		setListAdapter(mAdapter);
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
	 * @author glee23
	 */
	public interface RequestItemListener {

		/**
		 * Request detail information to be presented
		 * about the specific request.
		 * @param request request to get detail 
		 */
		public void onRequestSelected(CustomerRequest request);

		/**
		 * Assign the staffmember to handle the request.
		 * @param request request to handle 
		 * @param staff staff member to assign to request
		 */
		public void onAssignStaffToRequest(CustomerRequest request, String staff);

		/**
		 * Removes a request. Request is removed completely from this 
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
	 * @author glee23
	 */
	private class RequestListAdapter extends ArrayAdapter<CustomerRequest> {

		private final Context mContext;
		private List<String> mStaff;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Requests.
		 * @param ctx Context
		 * @param requests List of CustomerRequests
		 */
		public RequestListAdapter(Context ctx, List<CustomerRequest> requests) {
			super(ctx, R.layout.listitem_restaurant_request_top, requests);
			this.mContext = ctx;
			
			// For debug purposes we will add fake staff members
			String[] employees = getActivity().getResources().
					getStringArray(R.array.default_employees);
			mStaff = new ArrayList<String>();
			for (String e: employees) {
				mStaff.add(e);
			}
		}

		@Override
		public void add(CustomerRequest r) {
			super.add(r);
			this.notifyDataSetChanged();
		}

		@Override
		public void addAll(Collection<? extends CustomerRequest> collection) {
			super.addAll(collection);
			notifyDataSetChanged();
		}

		@Override
		public void clear() {
			super.clear();
			this.notifyDataSetChanged();
		}

		@SuppressWarnings("BC_UNCONFIRMED_CAST")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View vwTop;
			View vwBot;

			LinearLayout layoutView = null;

			if(convertView == null) {
				layoutView = new LinearLayout(mContext);
				layoutView.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_request_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_request_bot, null, true);
				layoutView.addView(vwTop);
				layoutView.addView(vwBot);
				convertView = layoutView;
			} else {
				//Everything already created, just find them
				vwTop = convertView.findViewById(R.id.listitem_request_top);
				vwBot = convertView.findViewById(R.id.listitem_request_bot);
			}

			CustomerRequest requestToShow = getItem(position);

			// For every restaurant to present create a handler for the request;
			// We are creating this view for the very first time
			if (layoutView != null) {
				// Create a handler just for the request.
				new CustomerRequestHandler(requestToShow, vwTop, vwBot);
			}
			return convertView;
		}

		/**
		 * Listener for certain item of a customer request view.
		 * @author glee23
		 */
		private class CustomerRequestHandler implements OnClickListener {

			private final CustomerRequest mRequest;
			private final ImageView mExpandDown;
			private final Button mPickRequest;
			private final Spinner mSpinner;
			private final View mTop, mBottom;

			/**
			 * Build this handler from the request and its corresponding views.
			 * 
			 * @param request CustomerRequest to associate to.
			 * @param top Top view for the request.
			 * @param bottom bottom view for the request.
			 */
			public CustomerRequestHandler(CustomerRequest request, View top, View bottom) {
				mRequest = request;

				mTop = top;
				mBottom = bottom;

				// Get a reference to all the top pieces 
//				final ImageView REQUESTIMAGE = (ImageView) 
//						mTop.findViewById(R.id.image_order_thumbnail);
				TextView title = (TextView) mTop.findViewById(R.id.label_request_title);

				mExpandDown = (ImageView) 
						mTop.findViewById(R.id.button_expand_request);
				TextView time = (TextView) mTop.findViewById(R.id.label_request_time);
				mPickRequest = (Button) mBottom.findViewById(R.id.button_proceed_request);	

				// Get a reference to all the bottom pieces
				ImageButton assignStaffButton = (ImageButton) 
						mBottom.findViewById(R.id.button_assign);

				ImageButton remove = (ImageButton) mBottom.findViewById(R.id.button_remove);

				mSpinner = (Spinner) mBottom.findViewById(
						R.id.spinner_staff_to_assign);

				//Populate

				title.setText(mRequest.getDescription() 
						+ getString(R.string.dash) + mRequest.getUserInfo().getName());


				time.setText(mRequest.getOriginatingTime().toString());

				// Add listeners for reaction purposes
				remove.setOnClickListener(this);
				mSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, mStaff));
				assignStaffButton.setOnClickListener(this);

				mTop.setOnClickListener(this);
				mPickRequest.setOnClickListener(this);

				// Set the image of this request
				//				DineOnImage image = order.getMainImage();
				//				if (image != null) {
				//					mListener.onGetImage(image, new ImageGetCallback() {
				//
				//						@Override
				//						public void onImageReceived(Exception e, Bitmap b) {
				//							if (e == null) {
				//								// We got the image so set the bitmap
				//								ORDERIMAGE.setImageBitmap(b);
				//							}
				//						}
				//					});
				//				}

				// Set the bottom view to initial to be invisible
				mBottom.setVisibility(View.GONE);
			}

			@Override
			public void onClick(View v) {

				if (v == mTop || v == mPickRequest) { 
					int bottomVisibility = mBottom.getVisibility();
					// Expand the bottom view if it is not shown
					// Hide the expand down button.
					if (bottomVisibility == View.GONE) {
						mExpandDown.setVisibility(View.GONE);
					} else if (bottomVisibility == View.VISIBLE) {
						mExpandDown.setVisibility(View.VISIBLE);
					}

					// Expand the animation
					ExpandAnimation expandAni = new ExpandAnimation(mBottom, 500);
					mBottom.startAnimation(expandAni);

				} else if (v.getId() == R.id.button_assign) {
					mListener.onAssignStaffToRequest(mRequest, (String) mSpinner.getSelectedItem());
					mAdapter.notifyDataSetChanged();
				} else if (v.getId() == R.id.button_remove) {
					mAdapter.remove(mRequest);
					mListener.onRemoveRequest(mRequest);
					mAdapter.notifyDataSetChanged();
				}

				if (v == mPickRequest) {
					mListener.onRequestSelected(mRequest);
				}
			}



		}
	}

}
