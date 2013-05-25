package uw.cse.dineon.user.general;

import com.parse.ParseUser;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author espeo196
 *
 */
public class ProfileEditFragment extends Fragment {

	private ImageButton mProfileImage;
	private InfoChangeListener mListener;
	
	/**
	 * Returns a fragment that will present the information present in the
	 * User Info object.
	 * 
	 * @param info
	 *            User Info to be prepared to present
	 * @return New image fragment.
	 */
	public static ProfileEditFragment newInstance(UserInfo info) {
		// Prepare a Bundle argument
		// for starting an activity with
		ProfileEditFragment frag = new ProfileEditFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final UserInfo INFO = mListener.getInfo();
		View view = inflater.inflate(R.layout.fragment_profile_edit,
				container, false);
		
		if (INFO != null) {
	
			mProfileImage = (ImageButton) view.findViewById(R.id.image_profile_picture);
			
			
			// Grab all of the editable text fields so that you can grab their values
			final TextView USERNAME = (TextView) view.findViewById(R.id.user_name);
			final TextView PHONENUMBER = (TextView) view.findViewById(R.id.user_phone);
			final TextView OLD_PASS = (TextView) view.findViewById(R.id.user_old_pass);
			final TextView NEW_PASS = (TextView) view.findViewById(R.id.user_new_pass);
			
			Button mSaveButton = (Button) view.findViewById(R.id.button_save_changes);
			mSaveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {	
					INFO.setPhone(PHONENUMBER.getText().toString());
					// TODO find way to set username and validate it
					// TODO check if OLD_PASS is same as their old pass word
					// if(OLD_PASS.getText().toString().equals(current user's password)) {
						ParseUser.getCurrentUser().setPassword(NEW_PASS.getText().toString());
					// }
					mListener.onUserInfoUpdate(INFO);
				}
			});
		}
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof InfoChangeListener) {
			mListener = (InfoChangeListener) activity;
		} else {
			throw new ClassCastException("Failed to cast Activity to InfoChangeListener.");
		}
	}
	
	/**
	 * 
	 * @param b bitmap to set Profile image as
	 */
	public void setProfileImage(Bitmap b) {
		mProfileImage.setImageBitmap(b);
	}
	
	/**
	 * Listener for this fragment to communicate back to its attached activity.
	 * 
	 * @author mhotan
	 */
	public interface InfoChangeListener {

		// process is completely replace the restaurant

		/**
		 * Notifies the Activity that the restaurant info requested to be
		 * updated.
		 * 
		 * @param user updated User Info
		 */
		void onUserInfoUpdate(UserInfo user);

		/**
		 * @return The RestaurantInfo object of this listener
		 */
		UserInfo getInfo();

	}

	/**
	 * This provides ONE method of decoding an image to that is.
	 * 
	 * @param path
	 *            path of image file to download
	 * @return View
	 */
	View insertPhoto(String path) {
		// Decode the image as a 220 x 220 image
		// Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

		// Add a border around the image
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		// Place the image in the center of the frame
		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

}
