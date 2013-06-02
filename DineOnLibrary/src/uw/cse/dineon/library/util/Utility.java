package uw.cse.dineon.library.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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

//	/**
//	 * Creates an alert dialog that pushes user back to login page.
//	 * @param context Context to show Alert Dialog
//	 * @param loginClass Class of the Login to launch. 
//	 * @return The alert dialog.
//	 */
//	public static AlertDialog getBackToLoginAlertDialog(
//			final Context context, final Class<?> loginClass) {
//		return getBackToLoginAlertDialog(context, 
//				"Looks like we have misplaced your Information.  " 
//						+ "Please log back in to try again." , loginClass);
//	}

	/**
	 * Creates an alert dialog that pushes user back to login page.
	 * @param context Context to show Alert Dialog
	 * @param message custom message to show User
	 * @param loginClass Class of the Login to launch. 
	 * @return The alert dialog.
	 */
	public static AlertDialog getBackToLoginAlertDialog(
			final Context context, final String message, final Class<?> loginClass) {
		AlertDialog.Builder b = new Builder(context);
		b.setTitle("Failed to get you information");
		b.setMessage(message);
		b.setCancelable(true);
		b.setPositiveButton("Login", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(context, loginClass);
				context.startActivity(i);
			}
		});
		b.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return b.create();
	}

	/**
	 * 
	 * @param orders Orders to sort
	 * @return The sorted list of orders
	 */
	public List<Order> sortOrdersMostRecent(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {

			@Override
			public int compare(Order lhs, Order rhs) {
				return lhs.getOriginatingTime().compareTo(rhs.getOriginatingTime());
			}
		});
		return orders;
	}
	
	/**
	 * For a given list of menu items, where some have the same product ID, 
	 * this function creates a mapping of menu items to the quantity that exists
	 * in the list.
	 * 
	 * If the inputted list is null or is empty an empty map is returned
	 * 
	 * @param items MenuItems to organize in a map
	 * @return Mapping of MenuItems to number of occurences in the list.
	 */
	public static Map<MenuItem, Integer> toQuantityMap(List<MenuItem> items) {
		HashMap<MenuItem, Integer> occurences = new HashMap<MenuItem, Integer>();
		if (items == null || items.isEmpty()) {
			return occurences;
		}
		for (MenuItem item : items) {
			if (!occurences.containsKey(item)) {
				occurences.put(item, 1);
			} else {
				int update = occurences.get(item) + 1;
				occurences.put(item, update);
			}
		}
		return occurences;
	}
}
