package com.example.populatedatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uw.cse.dineon.library.DineOnUser;
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
					Locale l = new Locale("en");
					Address addr = new Address(l);
					addr.setAddressLine(0, "1110 NE SW Pkwy");
					rest.getInfo().setAddr(addr);
					rest.addImage(createImage(R.raw.martys));
					rest.saveOnCurrentThread();
					
					// get menus and populate them
					List<Menu> m = getFakeMenu();
					m.get(0).saveOnCurrentThread();
					m.get(1).saveOnCurrentThread();
					rest.getInfo().addMenu(m.get(0));
					rest.getInfo().addMenu(m.get(1));
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
	 * Returns a list of fake menu items.
	 * 
	 * @return a list of fake menu items
	 * @throws ParseException 
	 */
	private List<MenuItem> getFakeMenuItems() throws ParseException {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		int itemCnt = 100;
		MenuItem food = new MenuItem(itemCnt++, 2.99, "Food", 
				"You eat it.");
		MenuItem gfood = new MenuItem(itemCnt++, 8.99, "Good Food", 
				"You definitely eat it.");
		MenuItem bfood = new MenuItem(itemCnt++, 12.99, "...Food?", 
				"You can eat it...");
		MenuItem drink1 = new MenuItem(itemCnt++, 1.99, "Purple Drank!", 
				"Ingredients: water, purple, sugar");
		MenuItem drink2 = new MenuItem(itemCnt++, 0.09, "Water", 
				"It's water. What do you want to know?");
		MenuItem drink3 = new MenuItem(itemCnt++, 10.99, "PowerThirst",
					"Do you want to feel SO ENERGETIC!?!");
		food.setImage(createImage(R.raw.food));
		gfood.setImage(createImage(R.raw.goodfood));
		bfood.setImage(createImage(R.raw.badfood));
		drink1.setImage(createImage(R.raw.purpledrank));
		drink2.setImage(createImage(R.raw.water));
		drink3.setImage(createImage(R.raw.powerthirst));
		
		food.saveOnCurrentThread();
		gfood.saveOnCurrentThread();
		bfood.saveOnCurrentThread();
		drink1.saveOnCurrentThread();
		drink2.saveOnCurrentThread();
		drink3.saveOnCurrentThread();
		
		menuItems.add(food);
		menuItems.add(gfood);
		menuItems.add(bfood);
		menuItems.add(drink1);
		menuItems.add(drink2);
		menuItems.add(drink3);
		
		return new ArrayList<MenuItem>(menuItems);
	}
	
	/**
	 * @return A Menu representing a menu
	 * @throws ParseException 
	 */
	private List<Menu> getFakeMenu() throws ParseException {
		List<Menu> mList = new ArrayList<Menu>();
		List<MenuItem> mi = getFakeMenuItems();
		Menu entreeMenu = new Menu("Entrees");
		entreeMenu.addNewItem(mi.get(0));
		entreeMenu.addNewItem(mi.get(1));
		entreeMenu.addNewItem(mi.get(2));
		
		Menu drinkMenu = new Menu("Drinks");
		drinkMenu.addNewItem(mi.get(3));
		drinkMenu.addNewItem(mi.get(4));
		drinkMenu.addNewItem(mi.get(5));
		entreeMenu.saveOnCurrentThread();
		drinkMenu.saveOnCurrentThread();
		mList.add(entreeMenu);
		mList.add(drinkMenu);
		
		return mList;
	}
	
	private DineOnImage createImage(int image) throws ParseException {
		DineOnImage dImage = new DineOnImage(ImageIO.loadBitmapFromResource(getResources(), image));
		dImage.saveOnCurrentThread();

		return dImage;
	}
	
}
