package uw.cse.dineon.user.general;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Fragment with editable text fields. Used for changing profile information.
 * @author espeo196
 *
 */
public class ProfileEditFragment extends Fragment {

	private ImageButton mProfileImage;
	private InfoChangeListener mListener;
	private Context mContext;
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
		final View EDIT_VIEW = inflater.inflate(R.layout.fragment_profile_edit,
				container, false);
		mContext = getActivity();
		if (INFO != null) {
	
			mProfileImage = (ImageButton) EDIT_VIEW.findViewById(R.id.image_profile_picture);
			
			
			// Grab all of the editable text fields so that you can grab their values
			final TextView EMAIL = (TextView) EDIT_VIEW.findViewById(R.id.user_email);
			final TextView PHONENUMBER = (TextView) EDIT_VIEW.findViewById(R.id.user_phone);
			final TextView OLD_PASS = (TextView) EDIT_VIEW.findViewById(R.id.user_old_pass);
			final TextView NEW_PASS = (TextView) EDIT_VIEW.findViewById(R.id.user_new_pass);
			
			((TextView)EDIT_VIEW.findViewById(R.id.label_profile_name_edit))
					.setText(DineOnUserApplication.getUserInfo().getName());
			
			EMAIL.setText(DineOnUserApplication.getUserInfo().getEmail());
			if(DineOnUserApplication.getUserInfo().getPhone() != null) {
				PHONENUMBER.setText(DineOnUserApplication.getUserInfo().getPhone());
			} else {
				PHONENUMBER.setText("");				
			}
			
			DineOnImage image = INFO.getImage();
			if (image != null) {
				mListener.onGetImage(image, new InitialGetImageCallback(
						mProfileImage));
			}

			// Set an onlick listener to handle the changing of images.
			mProfileImage.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("BC_UNCONFIRMED_CAST")
				@Override
				public void onClick(View v) {
					ImageButton imageView = (ImageButton) v;
					AlertDialog getImageDialog = getRequestImageDialog(new UserImageGetCallback(
							INFO, imageView));
					getImageDialog.show();
				}
			});
			
			Button mSaveButton = (Button) EDIT_VIEW.findViewById(R.id.button_save_changes);
			mSaveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {							
					// if nothing was changed, don't make network calls
					if(infoChanged(EDIT_VIEW)) {
						INFO.setEmail(EMAIL.getText().toString());
						INFO.setPhone(PhoneNumberUtils.formatNumber(
								PHONENUMBER.getText().toString()));
						
						// if password fields are empty don't attempt to login/change passwords
						if(!OLD_PASS.getText().toString().equals("")
							&& !NEW_PASS.getText().toString().equals("")) {
							
							try {
								// login to see if old pass is valid
								ParseUser.logIn(DineOnUserApplication.getUserInfo().getName(), 
										OLD_PASS.getText().toString());
								// old pass is valid, so set NEW_PASS
								DineOnUserApplication.getUserInfo()
										.setPassword(NEW_PASS.getText().toString());
							} catch (ParseException e) {
								Toast.makeText(getActivity(), 
										"Old Password doesn't match.", Toast.LENGTH_LONG).show();
								return;
							}
						}
						mListener.onUserInfoUpdate(INFO);
						
						// reset password fields to empty
						// (avoids problems with consecutive button presses)
						OLD_PASS.setText("");
						NEW_PASS.setText("");
					}
				}
			});
		}
		return EDIT_VIEW;
	}
	
	/**
	 * Get an alert dialog to present the user with the option to take
	 * pictures.
	 * 
	 * @param callback
	 *            Callback to accept pictures
	 * @return Get a dialog that will handle getting images for a menu item
	 */
	private AlertDialog getRequestImageDialog(
			final ImageGetCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.dialog_title_getimage);
		builder.setMessage(R.string.dialog_message_getimage_for_menuitem);
		builder.setPositiveButton(R.string.dialog_option_take_picture,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onRequestTakePicture(callback);
				dialog.dismiss();
			}
		});
		builder.setNeutralButton(R.string.dialog_option_choose_picture,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onRequestGetPictureFromGallery(callback);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		return builder.create();
	}
	
	/**
	 * 
	 * @param view inflater.inflate(R.layout.fragment_profile_edit,
					container, false)
	 * @return true if any of the text fields were changed
	 */
	public boolean infoChanged(View view) {
		// Grab all of the editable text fields so that you can grab their values
		final TextView EMAIL = (TextView) view.findViewById(R.id.user_email);
		final TextView PHONENUMBER = (TextView) view.findViewById(R.id.user_phone);
		final TextView OLD_PASS = (TextView) view.findViewById(R.id.user_old_pass);
		final TextView NEW_PASS = (TextView) view.findViewById(R.id.user_new_pass);
		
		
		
		return !EMAIL.getText().toString().equals(DineOnUserApplication.getUserInfo().getEmail())
				|| !PHONENUMBER.getText().toString().
					equals(DineOnUserApplication.getUserInfo().getPhone())
				|| !OLD_PASS.getText().toString().equals("") 
				|| !NEW_PASS.getText().toString().equals("");
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
	 * @param b bitmap to set Profile image as
	 */
	public void setProfileImage(Bitmap b) {
		mProfileImage.setImageBitmap(b);
	}
	
	/**
	 * Listener for this fragment to communicate back to its attached activity.
	 * 
	 * @author glee23
	 */
	public interface InfoChangeListener extends ImageObtainable {

		// process is completely replace the restaurant

		/**
		 * Notifies the Activity that the user requested to be
		 * updated.
		 * 
		 * @param user updated User Info
		 */
		void onUserInfoUpdate(UserInfo user);

		/**
		 * @return The UserInfo object of this listener
		 */
		UserInfo getInfo();
		
		/**
		 * The user has just added an image to their profile.
		 * 
		 * @param info
		 *            UserInfo to change
		 * @param b
		 *            Bitmap to use.
		 */
		void onImageAddedToUserInfo(UserInfo info, Bitmap b);

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

	/**
	 * Get the pre set image for this userinfo.
	 * 
	 * @author glee23
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
	 * An image get callback to to populate user profile view.
	 * 
	 * @author glee23
	 */
	private class UserImageGetCallback implements ImageGetCallback {

		private final UserInfo mInfo;
		private final ImageButton mButton;

		/**
		 * A callback to handle the retrieving of images.
		 * 
		 * @param info
		 *            UserInfo to get image for.
		 * @param button
		 *            ImageButton to hold image.
		 */
		public UserImageGetCallback(UserInfo info, ImageButton button) {
			mInfo = info;
			mButton = button;
		}

		@Override
		public void onImageReceived(Exception e, Bitmap b) {
			if (e == null) {
				mButton.setImageBitmap(b);
				mListener.onImageAddedToUserInfo(mInfo, b);
			} else {
				String message = getActivity().getResources().getString(
						R.string.message_unable_get_image);
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
				.show();
			}
		}
	}
	
}
