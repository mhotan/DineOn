/**
 * 
 */
package uw.cse.dineon.library.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import uw.cse.dineon.library.Storable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

//TODO Test the ParseUtil functions to ensure correctness.

/**
 * @author mtrathjen08
 * 
 * Class will wrap all functions that abstract interaction with
 * the Parse framework
 */
public class ParseUtil {
	private static final String TAG = "ParseUtil";
	
	
	/**
	 * Create a user for the user side of the DineOn application.
	 *  
	 * @param uname - user name to associate with the new account. 
	 * Must not be already used by another user. 
	 * @param passwd - The password to use with the new user will login
	 * @param callback - the static method to call once the response returns from Parse Cloud
	 * @throws IllegalArgumentException if any param is null.
	 */
	public static void createDineOnUser(String uname, String passwd, Method callback){
		//TODO handle exception cases.
		if(uname == null || passwd == null || callback == null)
			throw new IllegalArgumentException();
		ParseUser pu = new ParseUser();
		pu.setUsername(uname);
		pu.setPassword(passwd);
		final Method m = callback;
		pu.signUpInBackground(new SignUpCallback(){

			@Override
			public void done(ParseException e) {
				if(e == null){
					Boolean params = true;
					try {
						m.invoke(null, params);
					}catch(NullPointerException e1){
						Log.d(TAG, "Error: " + e1.getMessage());
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						Log.d(TAG, "Error: " + e1.getMessage());
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						Log.d(TAG, "Error: " + e1.getMessage());
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						Log.d(TAG, "Error: " + e1.getMessage());
						e1.printStackTrace();
					}
				} // TODO
					// Invoke our method with null notifying 
					// User create account failed
			}
			
		});
	}
	
	/**
	 * 
	 * @param uname - user name associated with the account
	 * @param passwd - password associated with the account
	 * @param callback - static method with param of type Storable 
	 * to call once the response is heard from the Parse Cloud.
 	 * @throws IllegalArgumentException if any param is null.
	 */
	public static void logInDineOnCreds(String uname, String passwd, Method callback){
		if(uname == null || passwd == null || callback == null)
			throw new IllegalArgumentException();
		
		final Method m = callback;
		ParseUser.logInInBackground(uname, passwd, new LogInCallback(){

			@Override
			public void done(ParseUser user, ParseException err) {
				if(err == null && user != null){
					//TODO setup default user and call callback with 
					//Storable User object.
					Log.d(TAG, "Log in success, user returned with no error");
				}
				else{
					Log.d(TAG, "Log in error");
				} 
				// TODO
				// Invoke our method with null notifying 
				// User create account failed
				
			}
			
		});
	}
	/**
	 * Save obj into the cloud and store the acquired objID into obj
	 * 
	 * @param obj the java object that will be saved to the cloud
	 */
	public static void saveDataToCloud(Storable obj, Method handler) {
		final Method h = handler;
		
		final ParseObject pObj = obj.packObject();
		final Storable s = obj;
		pObj.saveInBackground( new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// save was successful so send push
					Log.d(TAG, "Successfully saved object.");
					s.setObjId(pObj.getObjectId());
				} else {
					// Error occured
					Log.d(TAG, "Error: " + e.getMessage());
				}
				
				try {
					if (h != null)
						h.invoke(null, (e == null) ? Boolean.TRUE : Boolean.FALSE, pObj.getObjectId(), s);
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		// TODO
		// Handle failure
	}
	
	/**
	 * Query for object in the cloud given a list of attributes. On return the
	 * call-back provided will be invoked with the list of results.
	 * 
	 * @param c Static reference to the type of java class to query on 
	 * @param handle Call back for the query operation (Method must be static)
	 * @param attr The attributes used for the query. Every object in the returned
	 * list will satisfy the attributes passed.
	 * 
	 * Note: Returns a List<Storable> to the handler.
	 */
	public static void getDataFromCloud(Class<? extends Storable> c, Method handle, Map<String, String> attr) {
		
		ParseQuery query = new ParseQuery(c.getSimpleName());
		Set<String> kSet = attr.keySet();
		for (String k : kSet) {
			String val = attr.get(k);
			query.whereEqualTo(k, val);
		}
		final Method h = handle;
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> list, ParseException e) {
		        if (e == null) {
		        	if (list.size() > 0) {
		        		String className = list.get(0).getClassName();
		        		List<Storable> classList = new LinkedList<Storable>();
		        		
		        		try {
		        			Storable s;
		        			for (ParseObject p : list) {
		        				s = (Storable) Class.forName(className).newInstance();
		        				s.unpackObject(p);
		        				classList.add(s);
		        			}		   
		        			h.invoke(null, classList);
		        		} catch (Exception ex) {
		        			Log.d(TAG, "Error: " + ex.getMessage());
		        		}
		        	}
		        } else {
		            Log.d(TAG, "Error: " + e.getMessage());
		        }// TODO
				// Invoke our method with null notifying 
				// User create account failed
		    }
		});
	}
	
	/**
	 * Notify a recipient that an action has occured or state has changed
	 * via a push notification configured with the specified properties.
	 * There must be a custom broadcast receiver on the receiving end which
	 * is subscribed to the sending channel.
	 * 
	 * @param action A string to differenciate b/t different actions causing
	 * a push
	 * @param attr The key-value attributes that describe the information to
	 * send in the form of JSON obj
	 * @param channel The channel which the push is sent over
	 * 
	 * Note: There must must coordination b/t the sender and receiver on the
	 * format of attributes.
	 */
	public static void notifyApplication(String action, Map<String, String> attr, String channel) {
		try{
			JSONObject data = new JSONObject();
	        data.put("action", action);
	        Set<String> kSet = attr.keySet();
	        for (String k : kSet) {
	        	data.put(k, attr.get(k));
	        }
			ParsePush push = new ParsePush();
			push.setChannel(channel);
			push.setData(data);
			push.sendInBackground();
		}
		catch(Exception e){
			Log.d(TAG, e.getMessage());
		}
	}
	
	/**
	 * Pack a Storable List into an array of ParseObjects
	 * 
	 * @param list
	 * @return
	 */
	public static ParseObject packListOfStorables(List<? extends Storable> list) {
		ParseObject container = new ParseObject("Container");
		
		for (int i = 0; i < list.size(); i++) {
			container.put("c" + i, list.get(i).packObject());
		}
		
		return container;
	}
	
	public static List<Storable> unpackListOfStorables(ParseObject container) {
		if (!container.getClassName().equals("Container"))
			throw new IllegalArgumentException();
		
		List<Storable> list = new LinkedList<Storable>();
		try {
			for (String k : container.keySet()) {
				ParseObject p = container.getParseObject(k);
				Storable s = (Storable) Class.forName(p.getClassName()).newInstance();
				s.unpackObject(p);
				list.add(s);
			}
		} catch(Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
		
		return list;
	}
	
}
