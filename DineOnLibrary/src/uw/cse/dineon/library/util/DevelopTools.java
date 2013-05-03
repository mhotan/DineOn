package uw.cse.dineon.library.util;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * General Class that will help up build the project
 * <b>Add helper methods to help notify the developer and 
 * user that a certain feature is not implemented
 * @author mhotan
 */
public class DevelopTools {

	private static String[] MENUITEMS_V1 = {
		"Candy", "Cookies", "Monkey Brains", "Sugar Cubes", "Dough Balls"};

	/**
	 * Disallows 
	 */
	private DevelopTools() {

	}

	/**
	 * Return an alert dialog alerting the user and developer a certain feature is not implemented.
	 * if null listener is implemented a listener is created that just cancels the dialog 
	 * in its place
	 * @param ctx
	 * @param listener listener to react when user clicks button
	 * @return AlertDialg notifying user this feature is not yet implemented
	 */
	public static AlertDialog getUnimplementedDialog(Context ctx, DialogInterface.OnClickListener listener) {
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
		alertDialogBuilder.setMessage("This feature is still being built. " +
				"Sorry, but we are working on it!");
		alertDialogBuilder.setPositiveButton("OK", listener);
		return alertDialogBuilder.create();
	}

	/**
	 * Returns a defaulted Restaurant instance
	 * 
	 * @return Restaurant to use for testing
	 */
	public static Restaurant getDefaultRestaurant(){
		RestaurantInfo rInfo = new RestaurantInfo();
		List<MenuItem> items = new ArrayList<MenuItem>();
		for (int i = 0; i < MENUITEMS_V1.length; i++) {
			items.add(new MenuItem(i + 1, (double)i + 1, MENUITEMS_V1[i]));
		}
		Menu m = new Menu("Marty's Main Menu",items);
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(m);
		rInfo.setAddr("1234 Happy St.");
		rInfo.setMenu(m);
		rInfo.setPhone(1234567890);
		rInfo.setName("Marty's");

		return new Restaurant(menus, new ArrayList<Reservation>(), rInfo, new ArrayList<Order>(),
				new ArrayList<DiningSession>(), new ArrayList<CustomerRequest>());
	}

}
