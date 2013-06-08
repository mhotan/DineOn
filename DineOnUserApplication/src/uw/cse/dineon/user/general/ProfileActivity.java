package uw.cse.dineon.user.general;

import java.util.List;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity that manages the profile and settings fragments.
 * 
 * @author mhotan, espeo196
 */
public class ProfileActivity extends DineOnUserActivity implements 
		ProfileEditFragment.InfoChangeListener {
	
	/**
	 * Enums for keeping track of state.
	 */
	public enum State { DEFAULT, EDIT, BACK };
	private State state;
	private Context This;
	private ProfileEditFragment mProfileEditFragment;
	private Fragment mFragment;
	
	private static final String TAG = ProfileActivity.class.getSimpleName();
	private final int CONTAINER_ID = 10101010;	// ID of dynamically added frame layout
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frame = new FrameLayout(this);
		This = this;
		frame.setId(CONTAINER_ID);
		setContentView(frame, 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		if(savedInstanceState == null) {
			mFragment = ProfileImageFragment.newInstance(getInfo());
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			ft.add(CONTAINER_ID, mFragment, "imageFrag");
		//	ft.addToBackStack(null);
			ft.commit();	
		}
		state = State.DEFAULT;
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actually
		//  UI Menu is updated this is done manually
		inflater.inflate(R.menu.profile_menu, menu);
	
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		MenuItem m = menu.findItem(R.id.option_edit_profile);
		if(state == State.EDIT) {
			m.setEnabled(false);
			m.setVisible(false);
		} else if(state == State.BACK) {
			state = State.DEFAULT;
			m.setEnabled(true);
			m.setVisible(true);
		}		

		// If checked in
		if(DineOnUserApplication.getCurrentDiningSession() != null) {
			disableMenuItem(menu, R.id.option_check_in);
		} else { // If not checked in
			enableMenuItem(menu, R.id.option_check_in);
		}
		return true;		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		state = State.BACK;
	}
	
	/**
	 * Creates the onClick listeners for the specified menu items.
	 * 
	 * @param m the parent menu
	 * @param items the list of MenuItems to create listeners for
	 */
	private void setOnClick(final android.view.Menu m, List<android.view.MenuItem> items) {
		for (final android.view.MenuItem ITEM : items) {
			ITEM.getActionView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {   
					m.performIdentifierAction(ITEM.getItemId(), 0);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_edit_profile:
			
			// swap current fragments with editable text fields
			Fragment frag = 
				ProfileEditFragment.newInstance(DineOnUserApplication.getUserInfo());
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			
			ft.replace(CONTAINER_ID, frag);
			ft.addToBackStack(null);
			ft.commit();
			
			state = State.EDIT;
			break;
		case R.id.option_logout:
			ParseUser.logOut();
			startLoginActivity();
			break;
		default:
			//Unknown
			Log.e(TAG, "None of the specified action items were selected.");
		}
		return true;
	}

	@Override
	public void onImageAddedToUserInfo(UserInfo info, Bitmap b) {
		UserInfoImageCreator creator = new UserInfoImageCreator(info, b);
		creator.execute();
	}
	
	@Override
	public void onUserInfoUpdate(UserInfo user) {
		user.saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(getApplicationContext(),
							"Profile Updated!", Toast.LENGTH_LONG)
							.show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
				}
			}
		});
	}

	@Override
	public UserInfo getInfo() {
		return DineOnUserApplication.getUserInfo();
	}
	
	/**
	 * @return returns a progress dialog to show while the image is saving.
	 */
	private ProgressDialog getSavingImageDialog() {
		return getSaveDialog(R.string.saving_new_image);
	}

	/**
	 * Returns a progress dialog for showing that a certain item is saving.
	 * @param messageResId Resource message id.
	 * @return Progress dialog 
	 */
	private ProgressDialog getSaveDialog(int messageResId) {
		ProgressDialog d = new ProgressDialog(this);
		d.setIndeterminate(true);
		d.setCancelable(false);
		d.setTitle(R.string.saving);
		d.setMessage(getResources().getString(messageResId));
		return d;
	}
	
	/**
	 * Creates a DineOnImage for UserInfo.
	 * 
	 * @author glee23
	 */
	private class UserInfoImageCreator extends
	AsyncTask<Void, Void, DineOnImage> {

		private final Bitmap mBitmap;
		private final UserInfo mInfo;
		private final ProgressDialog mDialog;

		/**
		 * Prepares the saving process.
		 * 
		 * @param info
		 *            UserInfo to save
		 * @param b
		 *            Bitmap to use.
		 */
		public UserInfoImageCreator(UserInfo info, Bitmap b) {
			mBitmap = b;
			mInfo = info;
			mDialog = getSavingImageDialog();
		}

		@Override
		protected void onPreExecute() {
			invalidateOptionsMenu();
			mDialog.show();
		}

		@Override
		protected DineOnImage doInBackground(Void... arg0) {
			try {
				DineOnImage image = new DineOnImage(mBitmap);
				image.saveOnCurrentThread();
				mInfo.setImage(image);
				mInfo.saveOnCurrentThread();
				return image;
			} catch (ParseException e) {
				Log.e(TAG,
						"Unable to save image for menu item exception: "
								+ e.getMessage());
				return null; // Fail case
			}
		}

		@Override
		protected void onPostExecute(DineOnImage result) {
			if (result != null) {
				addImageToCache(result, mBitmap);
			} else {
				String message = getResources().getString(
						R.string.message_unable_get_image);
				Toast.makeText(This, message, Toast.LENGTH_SHORT).show();
			}
			
			// Stop the progress spinner
			invalidateOptionsMenu();
			mDialog.dismiss();
		}
	}

}

