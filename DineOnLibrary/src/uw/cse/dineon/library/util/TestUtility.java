package uw.cse.dineon.library.util;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;

/**
 * 
 * @author Espeo196, Jordan
 *
 */
public final class TestUtility {
	public static DineOnUser dineOnUser;
	public static Restaurant rest;
	public static ParseUser user;
	public static ParseUser restUser;
	
	/**
	 * Hidden constructor.
	 */
	private TestUtility() {	}
	

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
	 * @return A Menu representing a menu
	 */
	public static Menu getFakeMenu() {
		Menu entreeMenu = new Menu("testEntrees");
		for (MenuItem item: getFakeMenuItems()) {
			entreeMenu.addNewItem(item);
		}
		return entreeMenu;
	}
	
	/**
	 * @return DineOnUser
	 */
	public static DineOnUser getDineOnUser() {
		return dineOnUser;
	}

	/**
	 * @return Restaurant
	 */
	public static Restaurant getRest() {
		return rest;
	}

	/**
	 * @return ParseUser
	 */
	public static ParseUser getUser() {
		return user;
	}

	/**
	 * @return restaurant ParseUser
	 */
	public static ParseUser getRestUser() {
		return restUser;
	}
	
}
