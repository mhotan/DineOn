package uw.cse.dineon.library.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.UserInfo;

/**
 * 
 * @author Espeo196, Jordan
 *
 */
public class TestUtility {
	public static DineOnUser dineOnUser;
	
	
	/**
	 * Returns a list of fake menu items.
	 * 
	 * @return a list of fake menu items
	 */
	public static List<MenuItem> getFakeMenuItems() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		int itemCnt = 100;
		MenuItem steak = new MenuItem(itemCnt++, 12.99, "testSteak", 
				"A juicy (test) hunk of meat.");

		menuItems.add(steak);
	
		return new ArrayList<MenuItem>(menuItems);
	}
	
	/**
	 * @return A Menu representing an Entree menu
	 */
	public static Menu getFakeMenu() {
		Menu entreeMenu = new Menu("testEntrees");
		for (MenuItem item: getFakeMenuItems()) {
			entreeMenu.addNewItem(item);
		}
		return entreeMenu;
	}
	
}
