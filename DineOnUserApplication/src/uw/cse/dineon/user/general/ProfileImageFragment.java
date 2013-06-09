package uw.cse.dineon.user.general;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.general.ProfileEditFragment.InfoChangeListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author mhotan
 */
public class ProfileImageFragment extends Fragment {

	
	private ImageView mProfileImage;
	private TextView mProfileName;
	private TextView mProfileEmail;
	private TextView mProfilePhone;
	private InfoChangeListener mListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile,
				container, false);
		mProfileImage = (ImageView) view.findViewById(R.id.image_profile_picture);
		
		DineOnImage image = DineOnUserApplication.getUserInfo().getImage();
		if (image != null) {
			mListener.onGetImage(image, new InitialGetImageCallback(
					mProfileImage));
		}
		
		mProfileName = (TextView) view.findViewById(R.id.label_profile_name);
		mProfileName.setText(DineOnUserApplication.getDineOnUser().getName());
		
		mProfileEmail = (TextView) view.findViewById(R.id.user_email_display);
		mProfileEmail.setText(DineOnUserApplication.getUserInfo().getEmail());
		
		mProfilePhone = (TextView) view.findViewById(R.id.user_phone_display);
		mProfilePhone.setText(DineOnUserApplication.getUserInfo().getPhone());
		
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
	 * Get the pre set image for this user.
	 * 
	 * @author mhotan
	 */
	private class InitialGetImageCallback implements ImageGetCallback {

		private ImageView mView;

		/**
		 * prepares callback for placing an image in the view.
		 * 
		 * @param view View to place image.
		 */
		public InitialGetImageCallback(ImageView view) {
			mView = view;
		}

		@Override
		public void onImageReceived(Exception e, Bitmap b) {
			if (e == null && mView != null) {
				mView.setImageBitmap(b);
			}
		}
	}
	
	/**
	 * Creates a new instance of this fragment.
	 * @param info UserInfo of current user
	 * @return ProfileImageFragment instance
	 */
	public static ProfileImageFragment newInstance(UserInfo info) {
		// Prepare a Bundle argument
		// for starting an activity with
		ProfileImageFragment frag = new ProfileImageFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}
	
	/**
	 * 
	 * @param b bitmap to set Profile image as
	 */
	public void setProfileImage(Bitmap b) {
		// TODO Narrow down size
		mProfileImage.setImageBitmap(b);
	}

	/**
	 * Sets profile name.
	 * @param n String to set
	 */
	public void mProfileName(String n) {
		mProfileName.setText(n);
	}
	
	
}
