package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Detial Fragment showing the detail page of a particular menu item.
 * @author mhotan
 */
public class MenuItemDetailFragment extends Fragment {

	private static final String TAG = MenuItemDetailFragment.class.getSimpleName();
	
	public static final String KEY_MENU = "menu";
	
	private MenuItemDetailListener mListener;
	
	private MenuItem mMenuItem;
	
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menuitem_detail,
				container, false);
		
		this.mView = view;
		
		return view;
	}
	
	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setMenuItem(mListener.getMenuItem());
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuItemDetailListener) {
			mListener = (MenuItemDetailListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MenuItemDetailFragment.MenuItemDetailListener");
		}
	}

	/**
	 * @param item MenuItem to set
	 */
	public void setMenuItem(MenuItem item) {
		if (item == null) {
			Log.w(TAG, "Null item attempted to be used");
			return;
		}
		
		this.mMenuItem = item;
		
		// set the title
		TextView title = (TextView) this.mView.findViewById(R.id.label_menu_item_name);
		title.setText(mMenuItem.getTitle());
		
		// set the description
		TextView description = (TextView) this.mView.findViewById(R.id.label_menu_item_description);
		description.setText(mMenuItem.getDescription());
		
		final ImageView IMAGEVIEW = (ImageView) mView.findViewById(R.id.image_menu_item);
		DineOnImage image = item.getImage();
		mListener.onGetImage(image, new ImageGetCallback() {
			
			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e == null) {
					IMAGEVIEW.setImageBitmap(b);
				} else {
					IMAGEVIEW.setVisibility(View.GONE);
				}
			}
		});
		
		// TODO set the allergens
	}
	
	/**
	 * TODO implement.
	 * @author mrathjen
	 */
	public interface MenuItemDetailListener extends ImageObtainable {

		/**
		 * TODO get the current menu item.
		 * @return MenuItem
		 */
		public MenuItem getMenuItem();

	}

}
