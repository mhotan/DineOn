package uw.cse.dineon.library.util;

import uw.cse.dineon.library.Restaurant;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * General Class that will help up build the project.
 * <b>Add helper methods to help notify the developer and 
 * user that a certain feature is not implemented</b>
 * @author mhotan
 */
public final class DevelopTools {

	private static String[] MENUITEMS_V1 = {
		"Candy", "Cookies", "Monkey Brains", "Sugar Cubes", "Dough Balls"};

	/**
	 * No Instantiation.
	 */
	private DevelopTools() { }

	/**
	 * Return an alert dialog alerting the user and developer a certain feature is not implemented.
	 * if null listener is implemented a listener is created that just cancels the dialog 
	 * in its place
	 * @param ctx Context
	 * @param listener listener to react when user clicks button
	 * @return AlertDialg notifying user this feature is not yet implemented
	 */
	public static AlertDialog getUnimplementedDialog(Context ctx, 
			DialogInterface.OnClickListener listener) {
		if (listener == null) {
			listener = new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			};
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

		// set title
		alertDialogBuilder.setTitle("DARN! Not Implemented");
		alertDialogBuilder.setMessage("This feature is still being built. "
				+ "Sorry, but we are working on it!");
		alertDialogBuilder.setPositiveButton("OK", listener);
		return alertDialogBuilder.create();
	}

	/**
	 * Returns a defaulted Restaurant instance.
	 * 
	 * @param me ParseUser to get own restaurant for
	 * @return me to use for testing
	 */
	public static Restaurant getYourOwnRestaurant(ParseUser me) {
		Restaurant rest = null;
		try {
			rest = new Restaurant(me);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rest;
	}

}
