package uw.cse.dineon.user;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.DineOnConstants;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Application for DineOn user side.
 */
public class DineOnUserApplication extends Application {
	public static DineOnUser cachedUser = null;
	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, DineOnConstants.APPLICATION_ID, DineOnConstants.CLIENT_KEY);

		// TODO Initialize Twitter
		// https://www.parse.com/docs/android_guide#twitterusers-setup
		
		// TODO Initialize Facebook
		// https://www.parse.com/docs/android_guide#fbusers-setup		
		ParseFacebookUtils.initialize(DineOnConstants.FACEBOOK_APP_ID);
		
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
	public static void setDineOnUser(DineOnUser user) {
		cachedUser = user;
	}

}
