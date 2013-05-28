package uw.cse.dineon.restaurant;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Application class for the entire restaurant application.
 * 
 * @author mhotan, mrathjen, jmcneal
 */
public class DineOnRestaurantApplication extends Application {
	
	/**
	 * Reference to the current restaurant.
	 */
	private static Restaurant mRestaurant;
	
	/**
	 * Logs out of this current restaurant.
	 * Context is required to correctly stop listening for request.
	 * 
	 * @param ctx to stop listening to channel.
	 */
	public static void logOut(Context ctx) {
		if (ctx != null) {
			PushService.unsubscribe(ctx, ParseUtil.getChannel(mRestaurant.getInfo()));
		}
		mRestaurant = null;
		ParseUser.logOut();
	}
	
	/**
	 * Logs into the application with the inputted restaurant.
	 * @param restaurant restaurant to log in with.
	 */
	public static void logIn(Restaurant restaurant) {
		if (restaurant == null) {
			throw new IllegalArgumentException("Restaurant can't be null on log in");
		}
		mRestaurant = restaurant;
		
	}
	
	/**
	 * Returns a reference to the current restaurant.
	 * 
	 * (MH) I hate this.  I don't like exposing the internal representation of the restaurant.
	 * This gives clients a reference to the current restaurant which they could accidently or
	 * purposely null out. 
	 * @return A reference  to the Current Restaurant instance
	 */
	public static Restaurant getRestaurant() {
		return mRestaurant;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, DineOnConstants.APPLICATION_ID, DineOnConstants.CLIENT_KEY);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

}
