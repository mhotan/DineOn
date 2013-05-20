package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.profile.MenuItemsFragment;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import uw.cse.dineon.restaurant.profile.RestaurantInfoFragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Test class for testing restaurant profile
 * @author mhotan
 */
@SuppressLint("DefaultLocale")
public class RestaurantProfileActivityTest extends
		ActivityInstrumentationTestCase2<ProfileActivity> {

	private ProfileActivity mActivity;
	private Restaurant r;

	private ParseUser mUser;

	private MenuItem mMenuItem;

	private Menu testMenu;
	
	View mView;

	private static final String TEST_ADDR = "5513";
	private static final String TEST_PHONE = "555-1234";

	private static final String TEST_MENU_TITLE = "Test Menu 17";
	private static final String TEST_ITEM_TITLE = "Test Food 64";
	private static final String TEST_ITEM_DESC = "Test Description 5";
	private static final double TEST_ITEM_PRICE = 9.99;
	private static final int TEST_ITEM_ID = 1;

	private final static String fakeUserName = "vinceRestProfileActTest";
	private final static String fakePassword = "password";
	
	public RestaurantProfileActivityTest() {
		super(ProfileActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(getInstrumentation().getTargetContext(),
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul",
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		mUser = new ParseUser();
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);
		
		try {
			mUser = ParseUser.logIn(fakeUserName, fakeUserName);
		} catch (ParseException e) {
			mUser.signUp();
		}
		
		
		// construct fake restaurant for intent
		r = createFakeRestaurant(mUser);
		r.getInfo().setAddr(TEST_ADDR);
		r.getInfo().setPhone(TEST_PHONE);

		testMenu = new Menu(TEST_MENU_TITLE);
		mMenuItem = new MenuItem(TEST_ITEM_ID, TEST_ITEM_PRICE, TEST_ITEM_TITLE, TEST_ITEM_DESC);
		testMenu.addNewItem(mMenuItem);
		r.getInfo().addMenu(testMenu);

		Intent intent = new Intent(getInstrumentation().getTargetContext(), ProfileActivity.class);
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, r);
		setActivityIntent(intent);

		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		mUser.delete();
		testMenu.deleteFromCloud();
		r.deleteFromCloud();
		mMenuItem.deleteFromCloud();
		mActivity.finish();
		for (Menu m : r.getInfo().getMenuList()) {
			m.deleteFromCloud();
		}
		super.tearDown();
	}

	/**
	 * Tests that actionbar tabs properly switch between tabs.
	 */
	public void testActionBarTabs() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() { // TODO set all these 0/1 to constants.
				mActivity.getActionBar().setSelectedNavigationItem(0);
			}
		});
		getInstrumentation().waitForIdleSync();
		Fragment frag = mActivity.getSupportFragmentManager().findFragmentByTag(
				ProfileActivity.LAST_FRAG_TAG);
		assertEquals(RestaurantInfoFragment.class, frag.getClass());
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		frag = mActivity.getSupportFragmentManager().findFragmentByTag(
				ProfileActivity.LAST_FRAG_TAG);
		assertEquals(MenuItemsFragment.class, frag.getClass());
	}

	/**
	 * Test that the menu_add_menu_item button displays the appropriate action
	 * window and that it can receive text.
	 */
	public void testAddMenuItem() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		boolean result = getInstrumentation().invokeMenuActionSync(mActivity,
				uw.cse.dineon.restaurant.R.id.menu_add_menu_item, 0);

		assertTrue(result); // true if button selected success
		getInstrumentation().waitForIdleSync();
		this.sendKeys("T E S T SPACE I T E M");
		MenuItemsFragment frag = (MenuItemsFragment) mActivity.getSupportFragmentManager()
				.findFragmentByTag(ProfileActivity.LAST_FRAG_TAG);
		View input = frag.newItemAlert
				.findViewById(uw.cse.dineon.restaurant.R.id.input_menuitem_title);
		assertNotNull(input);
		String text = ((TextView) input).getText().toString().toLowerCase();
		assertEquals("test item", text);
		assertTrue(frag.newItemAlert.isShowing());
	}

	/**
	 * Test that activity properly extracts phone/address from the RestInfo
	 * inside the intent.
	 */
	public void testGetInfoFromIntent() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(0);
			}
		});
		getInstrumentation().waitForIdleSync();
		String address = ((TextView) mActivity
				.findViewById(uw.cse.dineon.restaurant.R.id.edittext_restaurant_address)).getText()
				.toString();
		String phone = ((TextView) mActivity
				.findViewById(uw.cse.dineon.restaurant.R.id.edittext_restaurant_phone)).getText()
				.toString();
		assertEquals(TEST_ADDR, address);
		assertEquals(TEST_PHONE, phone);
	}

	/**
	 * Create a menu item through the dialogue and make sure it's added to the list.
	 */
	public void testMenuListPopulate() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		final MenuItemsFragment frag = (MenuItemsFragment) mActivity.getSupportFragmentManager()
				.findFragmentByTag(ProfileActivity.LAST_FRAG_TAG);
		int init_count = frag.getListAdapter().getCount();
		boolean result = getInstrumentation().invokeMenuActionSync(mActivity,
				uw.cse.dineon.restaurant.R.id.menu_add_menu_item, 0);
		assertTrue(result); // true if button selected success
		getInstrumentation().waitForIdleSync();
		this.sendKeys("T E S T SPACE I T E M");
		this.sendKeys("ENTER");
		this.sendKeys("T E S T SPACE D E S C R I P T I O N");
		this.sendKeys("ENTER");
		this.sendKeys("2 PERIOD 9 9");

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// XXX this is how you test-imatically dismiss alert boxes
				frag.newItemAlert.getButton(Dialog.BUTTON_POSITIVE).performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		int new_count = frag.getListAdapter().getCount();
		assertEquals(init_count + 1, new_count);
		MenuItem mi = (MenuItem) frag.getListAdapter().getItem(new_count - 1);
		assertEquals("test item", mi.getTitle());

	}

	/**
	 * Test that the menuitem from the intent is displayed.
	 */
	public void testMenuItemFromIntent() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		final MenuItemsFragment frag = (MenuItemsFragment) mActivity.getSupportFragmentManager()
				.findFragmentByTag(ProfileActivity.LAST_FRAG_TAG);
		MenuItem mi = (MenuItem) frag.getListAdapter().getItem(0);
		assertEquals(TEST_ITEM_TITLE, mi.getTitle());
		assertEquals(TEST_ITEM_DESC, mi.getDescription());
		assertEquals(TEST_ITEM_PRICE, mi.getPrice());
	}

	/**
	 * Test that the Menu Selection Spinner shows the correct title
	 * for the menu included in the intent.
	 */
	public void testMenuSelectionSpinner() {
		// only one menu should exist, so it should be displayed in the spinner
		// by default
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		boolean result = getInstrumentation().invokeMenuActionSync(mActivity,
				uw.cse.dineon.restaurant.R.id.menu_switch_menu, 0);
		assertTrue(result);
		getInstrumentation().waitForIdleSync();
		final MenuItemsFragment frag = (MenuItemsFragment) mActivity.getSupportFragmentManager()
				.findFragmentByTag(ProfileActivity.LAST_FRAG_TAG);
		AlertDialog ad = frag.newMenuAlert;
		Spinner sp = (Spinner) ad.findViewById(uw.cse.dineon.restaurant.R.id.spinner_select_menu);
		boolean vis = ad.findViewById(uw.cse.dineon.restaurant.R.id.container_new_menu).isShown();
		assertFalse(vis);
		// this container should be hidden by default. Test showing in next test
		String menuname = (String) sp.getItemAtPosition(0);
		// This should be the test menu added in the intent
		assertEquals(TEST_MENU_TITLE, menuname);
	}

	/**
	 * Test that the add menu dialogue shows up and properly adds the new menu to
	 * the spinner and switches to it.
	 */
	public void testAddMenu() {
		// copy paste everything from prev test.
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getActionBar().setSelectedNavigationItem(1);
			}
		});
		getInstrumentation().waitForIdleSync();
		boolean result = getInstrumentation().invokeMenuActionSync(mActivity,
				uw.cse.dineon.restaurant.R.id.menu_switch_menu, 0);
		assertTrue(result);
		getInstrumentation().waitForIdleSync();
		final MenuItemsFragment frag = (MenuItemsFragment) mActivity.getSupportFragmentManager()
				.findFragmentByTag(ProfileActivity.LAST_FRAG_TAG);
		final AlertDialog ad = frag.newMenuAlert;
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((Button) ad.findViewById(uw.cse.dineon.restaurant.R.id.button_new_menu))
						.performClick();
				ad.findViewById(uw.cse.dineon.restaurant.R.id.input_new_menu_title).requestFocus();
			}
		});
		getInstrumentation().waitForIdleSync();
		boolean vis = ad.findViewById(uw.cse.dineon.restaurant.R.id.container_new_menu).isShown();
		assertTrue(vis); // Should be visible now
		
		//send keys for new menu title
		this.sendKeys("S A M P L E S"); //samples
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//Every time you try to cast an ImageButton to a Button god kills a kitten
				((ImageButton) ad.findViewById(uw.cse.dineon.restaurant.R.id.button_save_menu))
						.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		Spinner sp = (Spinner) ad.findViewById(uw.cse.dineon.restaurant.R.id.spinner_select_menu);
		String menuname = (String) sp.getItemAtPosition(1);
		assertEquals("samples", menuname);

	}

	private Restaurant createFakeRestaurant(ParseUser user) throws ParseException {
		Restaurant r = new Restaurant(user);
		return r;
	}

}
