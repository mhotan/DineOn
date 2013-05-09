package uw.cse.dineon.library.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class to hold a lot of reusable method and procedures.
 * @author mhotan
 */
public final class Utility {
	
	/**
	 * Prevents construction of a utility class.
	 */
	private Utility() { }
	
	/**
	 * Returns whether the screen can be split horizontally into two panes
	 * for optimal user interaction.  Currently the restriction is 
	 * that if the screen is very large 
	 * <b>Currently: XLarge screen layout</b>
	 * <b>OR if the screen is large and in landscape</b>
	 * @param context Context of the activity
	 * @return true if it is able to split, false otherwise
	 */
	public static boolean isPaneSplitable(Context context) {
		int mask = (context.getResources().getConfiguration().screenLayout 
				& Configuration.SCREENLAYOUT_SIZE_MASK);
		boolean isLargeEnough = mask == Configuration.SCREENLAYOUT_SIZE_LARGE 
				|| mask == Configuration.SCREENLAYOUT_SIZE_XLARGE;
		//		boolean isLayoutOriented = context.getResources().getConfiguration().orientation
		//				== Configuration.ORIENTATION_LANDSCAPE;
		return isLargeEnough;
	}

	/**
	 * Checks whether the given context is connected to 
	 * the network either via wifi or data.
	 * @param context Context that needs network
	 * @return true if connected to the network, false otherwise
	 */
	public static boolean isConnectedToNetwork(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	
	/**
	 * Returns a general Alert Dialog box for notifying the user that
	 * their attempt to login failed.
	 * 
	 * @param message Message of alert dialog box
	 * @param context Context to pop up Alert dialog box
	 * @return AlertDialog Dialog box to show user
	 */
	public static AlertDialog getFailedToCreateAccountDialog(
			String message, Context context) {
		return getGeneralAlertDialog("Failed to Create Account", message, context);
	}
	
	/**
	 * Returns general alert dialog box.
	 * 
	 * @param title title of the alert dailog box
	 * @param message Message of the box
	 * @param context context to appear
	 * @return Alert Dialog box with one option
	 */
	public static AlertDialog getGeneralAlertDialog(
			String title, String message, Context context) {
		AlertDialog.Builder b = new Builder(context);
		b.setTitle(title);
		b.setMessage(message);
		b.setCancelable(true);
		b.setPositiveButton("Dismiss", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return b.create();
	}
	

}
