package com.example.populatedatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageIO;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.Menu;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

	private int itemCnt = 100;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Add your initialization code here
		Parse.initialize(this, DineOnConstants.APPLICATION_ID, DineOnConstants.CLIENT_KEY);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				ParseUser rUser = new ParseUser();
				rUser.setUsername("Martys");
				rUser.setPassword("12345");
				rUser.setEmail("martys@booyah.com");
				
				try {
					rUser.signUp();

					// set restaurant & restaurant info
					Restaurant rest = new Restaurant(rUser);
					rest.getInfo().setHours("Mon-Fri: 8am-11pm\n" +
									"Sat-Sun: All Day!!");
					rest.getInfo().setPhone("4031541423");
					Locale l = Locale.US;
					Address addr = new Address(l);
					addr.setAddressLine(0, "1110 NE SW Pkwy");
					rest.getInfo().setAddr(addr);
					rest.addImage(createImage(R.raw.martys));
					rest.saveOnCurrentThread();
					
					// get menus and populate them
					List<Menu> m = new ArrayList<Menu>();
					getFakeMenus(m);
					for(Menu menu : m) {
						menu.saveOnCurrentThread();
						rest.getInfo().addMenu(menu);
					}
					rest.saveOnCurrentThread();
										
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return null;
			}		
		};
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Returns a list of fake entree menu items.
	 * 
	 * @return a list of fake menu items
	 * @throws ParseException 
	 */
	private List<MenuItem> getFakeEntrees() throws ParseException {
		List<MenuItem> mi = new ArrayList<MenuItem>();
		MenuItem food_1 = new MenuItem(itemCnt++, 5.99, "Hamburger", 
				"A basic burger without cheese. " +
				"Crisp lettuce, fresh tomatoes & mayo. Can't go wrong! Served with fries.");
		MenuItem food_2 = new MenuItem(itemCnt++, 8.99, "Bacon Cheeseburger", 
				"The best burger ever! " 
				+ "Topped with Cheddar cheese and applewood-smoked bacon on " +
				"a bed of crips lettuce and fresh tomatoes. Served with fries.");
		MenuItem food_3 = new MenuItem(itemCnt++, 12.99, "The Monster", 
				"The classic Bacon Cheeseburger with twice the meat and cheese! " +
				"Two charbroiled patties, slices of cheese and double bacon. Served with fries.");
		MenuItem food_4 = new MenuItem(itemCnt++, 13.99, "Riblets & Mac 'n' Cheese",
				"Pair tender pork riblets with creamy mac 'n' cheese for the perfect combo.");
		MenuItem food_5 = new MenuItem(itemCnt++, 12.99, "Halibut & Chips", 
				"Beer battered halibut and steak fries.");
		MenuItem food_6 = new MenuItem(itemCnt++, 10.49, "BLT Croissant", 
				"A buttery croissant with sliced turkey, hardwood-smoked bacon, crisp " +
				"lettuce, tomatoes, & mayo. Served with fries.");
		
		food_1.setImage(createImage(R.raw.food_1));
		food_2.setImage(createImage(R.raw.food_2));
		food_3.setImage(createImage(R.raw.food_3));
		food_4.setImage(createImage(R.raw.food_4));
		food_5.setImage(createImage(R.raw.food_5));
		food_6.setImage(createImage(R.raw.food_6));
		
		food_1.saveOnCurrentThread();
		food_2.saveOnCurrentThread();
		food_3.saveOnCurrentThread();
		food_4.saveOnCurrentThread();
		food_5.saveOnCurrentThread();
		food_6.saveOnCurrentThread();
		
		mi.add(food_1);
		mi.add(food_2);
		mi.add(food_3);
		mi.add(food_4);
		mi.add(food_5);
		mi.add(food_6);
		
		return new ArrayList<MenuItem>(mi);
	}
	
	private List<MenuItem> getFakeAppetizers() throws ParseException {
		List<MenuItem> mi = new ArrayList<MenuItem>();
		
		MenuItem app_1 = new MenuItem(itemCnt++, 7.79, "Onion Rings", 
				"Crisp and crunchy fried onion rings.");
		MenuItem app_2 = new MenuItem(itemCnt++, 8.99, "Chicken Wings", 
				"Classic spicy chicken wings.");
		MenuItem app_3 = new MenuItem(itemCnt++, 9.49, "Boneless Wings", 
				"Just like normal wings, but without those pesky bones.");
		MenuItem app_4 = new MenuItem(itemCnt++, 7.49, "Mozzarella Sticks", 
				"Golden fried and served with marinara sauce.");
		
		app_1.setImage(createImage(R.raw.app_1));
		app_2.setImage(createImage(R.raw.app_2));
		app_3.setImage(createImage(R.raw.app_3));
		app_4.setImage(createImage(R.raw.app_4));
		
		app_1.saveOnCurrentThread();
		app_2.saveOnCurrentThread();
		app_3.saveOnCurrentThread();
		app_4.saveOnCurrentThread();
		
		mi.add(app_1);
		mi.add(app_2);
		mi.add(app_3);
		mi.add(app_4);
		
		return new ArrayList<MenuItem>(mi);
	}
	
	private List<MenuItem> getFakeDrinks() throws ParseException {
		List<MenuItem> mi = new ArrayList<MenuItem>();
		
		MenuItem drink_1 = new MenuItem(itemCnt++, 1.99, "Purple Drank!", 
				"Ingredients: water, purple, sugar");
		MenuItem drink_2 = new MenuItem(itemCnt++, 0.00, "Water", 
				"All you can drink. Not all you want to drink.");
		MenuItem drink_3 = new MenuItem(itemCnt++, 10.99, "PowerThirst",
					"Do you want to feel SO ENERGETIC!?!");
		MenuItem drink_4 = new MenuItem(itemCnt++, 2.99, "Cola",
				"Cola soft drink.");
		MenuItem drink_5 = new MenuItem(itemCnt++, 2.99, "Diet Cola",
				"Diet cola soft drink.");
		
		drink_1.setImage(createImage(R.raw.drink_1));
		drink_2.setImage(createImage(R.raw.drink_2));
		drink_3.setImage(createImage(R.raw.drink_3));
		drink_4.setImage(createImage(R.raw.drink_4));
		drink_5.setImage(createImage(R.raw.drink_4));
		
		drink_1.saveOnCurrentThread();
		drink_2.saveOnCurrentThread();
		drink_3.saveOnCurrentThread();
		drink_4.saveOnCurrentThread();
		drink_5.saveOnCurrentThread();
		
		mi.add(drink_1);
		mi.add(drink_2);
		mi.add(drink_3);
		mi.add(drink_4);
		mi.add(drink_5);
		
		return new ArrayList<MenuItem>(mi);

	}
	
	private List<MenuItem> getFakeDesserts() throws ParseException {
		List<MenuItem> mi = new ArrayList<MenuItem>();

		MenuItem dessert_1 = new MenuItem(itemCnt++, 4.99, "Triple Chocolate Lava Cake",
				"Moist chocalate cake with a fudge-filled center. Served with vanilla " +
				"ice cream and hot fudge.");
		MenuItem dessert_2 = new MenuItem(itemCnt++, 5.49, "Dark Chocolate Brownie",
				"Mega moist with dark chocolate hunks, nuts, and hot fudge. Served with vanilla ice cream.");
		MenuItem dessert_3 = new MenuItem(itemCnt++, 5.99, "High Mudd Pie",
				"An avalanche of chocolate and vanilla ice cream layered with Oreo cookies, fudge, and caramel.");
		
		dessert_1.setImage(createImage(R.raw.dessert_1));
		dessert_2.setImage(createImage(R.raw.dessert_2));
		dessert_3.setImage(createImage(R.raw.dessert_3));
		
		dessert_1.saveOnCurrentThread();
		dessert_2.saveOnCurrentThread();
		dessert_3.saveOnCurrentThread();
		
		mi.add(dessert_1);
		mi.add(dessert_2);
		mi.add(dessert_3);
		
		return new ArrayList<MenuItem>(mi);
	}
	
	/**
	 * @param List<Menu> empty list to be populated with 
	 * 		entree, appetizer, drink, and dessert menus
	 * @throws ParseException 
	 */
	private void getFakeMenus(List<Menu> mList) throws ParseException {
		mList.add(new Menu("Entrees"));
		mList.add(new Menu("Appetizers"));
		mList.add(new Menu("Drinks"));
		mList.add(new Menu("Desserts"));
		
		List<List<MenuItem>> menuItems = new ArrayList<List<MenuItem>>();
		menuItems.add(getFakeEntrees());
		menuItems.add(getFakeAppetizers());
		menuItems.add(getFakeDrinks());
		menuItems.add(getFakeDesserts());
		
		int i = 0;
		for(List<MenuItem> list : menuItems) {
			
			for(MenuItem item : list) {
				mList.get(i).addNewItem(item);
			}
			i++;
		}
		for(Menu m : mList) {
			m.saveOnCurrentThread();
		}		
	}
	
	private DineOnImage createImage(int image) throws ParseException {
		DineOnImage dImage = new DineOnImage(ImageIO.loadBitmapFromResource(getResources(), image));
		dImage.saveOnCurrentThread();

		return dImage;
	}
	
}
