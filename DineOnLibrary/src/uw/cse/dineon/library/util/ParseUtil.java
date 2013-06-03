/**
 * 
 */
package uw.cse.dineon.library.util;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.UserInfo;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParsePush;

//TODO Test the ParseUtil functions to ensure correctness.

/**
 * @author mtrathjen08
 * 
 * Class will wrap all functions that abstract interaction with
 * the Parse framework
 */
public final class ParseUtil {
	private static final String TAG = "ParseUtil";

	/**
	 * Hidden constructor.
	 */
	private ParseUtil() { }

	//
	//	/**
	//	 * Create a user for the user side of the DineOn application.
	//	 *  
	//	 * @param uname - user name to associate with the new account. 
	//	 * Must not be already used by another user. 
	//	 * @param passwd - The password to use with the new user will login
	//	 * @param callback - the static method to call once the response returns from Parse Cloud
	//	 */
	//	public static void createDineOnUser(String uname, String passwd, Method callback) {
	//		//TODO handle exception cases.
	//		if(uname == null || passwd == null || callback == null) {
	//			throw new IllegalArgumentException();
	//		}
	//		ParseUser pu = new ParseUser();
	//		pu.setUsername(uname);
	//		pu.setPassword(passwd);
	//		final Method M = callback;
	//		pu.signUpInBackground(new SignUpCallback() {
	//
	//			@Override
	//			public void done(ParseException e) {
	//				if(e == null) {
	//					Boolean params = true;
	//					try {
	//						if (M != null) {
	//							M.invoke(null, params); 
	//						}
	//					} catch (NullPointerException e1) {
	//						Log.d(TAG, "Error: " + e1.getMessage());
	//					} catch (IllegalArgumentException e1) {
	//						Log.d(TAG, "Error: " + e1.getMessage());
	//						e1.printStackTrace();
	//					} catch (IllegalAccessException e1) {
	//						Log.d(TAG, "Error: " + e1.getMessage());
	//						e1.printStackTrace();
	//					} catch (InvocationTargetException e1) {
	//						// TODO Auto-generated catch block
	//						Log.d(TAG, "Error: " + e1.getMessage());
	//						e1.printStackTrace();
	//					}
	//				} // TODO
	//				// Invoke our method with null notifying 
	//				// User create account failed
	//			}
	//
	//		});
	//	}
	//
	//	/**
	//	 * 
	//	 * @param uname - user name associated with the account
	//	 * @param passwd - password associated with the account
	//	 * @param callback - static method with param of type Storable 
	//	 * to call once the response is heard from the Parse Cloud.
	//	 * @throws IllegalArgumentException if any param is null.
	//	 */
	////	public static void logInDineOnCreds(String uname, String passwd, Method callback) {
	////		if(uname == null || passwd == null || callback == null) {
	////			throw new IllegalArgumentException();
	////		}
	////
	////		final Method M = callback;
	////		ParseUser.logInInBackground(uname, passwd, new LogInCallback() {
	////
	////			@Override
	////			public void done(ParseUser user, ParseException err) {
	////				if(err == null && user != null) {
	////					//TODO setup default user and call callback with 
	////					//Storable User object.
	////					Log.d(TAG, "Log in success, user returned with no error");
	////				} 
	////				else {
	////					Log.d(TAG, "Log in error");
	////				} 
	////				// TODO
	////				// Invoke our method with null notifying 
	////				// User create account failed
	////
	////			}
	////		});
	////	}
	//	
	//	/**
	//	 * Save obj into the cloud and store the acquired objID into obj.
	//	 * 
	//	 * @param obj the java object that will be saved to the cloud
	//	 * @param handler The static callback to execute on completion of the save.
	//	 */
	//	public static void saveDataToCloud(Storable obj, Method handler) {
	//		final Method H = handler;
	//
	//		final ParseObject P_OBJ = obj.packObject();
	//		final Storable S = obj;
	//		P_OBJ.saveInBackground(new SaveCallback() {
	//			@Override
	//			public void done(ParseException e) {
	//				if (e == null) {
	//					// save was successful so send push
	//					Log.d(TAG, "Successfully saved object.");
	//					// TODO
	////					s.setObjId(pObj.getObjectId());
	//					// Notifify Success
	//				} else {
	//					// Error occured
	//					Log.d(TAG, "Error: " + e.getMessage());
	//				}
	//
	//				try {
	//					if (H != null) {
	//						H.invoke(null, (e == null) ? Boolean.TRUE : Boolean.FALSE, 
	//								P_OBJ.getObjectId(), S);
	//					}
	//				} catch (IllegalArgumentException e1) {
	//					e1.printStackTrace();
	//				} catch (IllegalAccessException e1) {
	//					e1.printStackTrace();
	//				} catch (InvocationTargetException e1) {
	//					// TODO Auto-generated catch block (same as above)
	//					e1.printStackTrace();
	//				}
	//			}
	//		});
	//		// TODO
	//		// Handle failure
	//	}
	//
	//	/**
	//	 * Save obj into the cloud and store the acquired objID into obj.
	//	 *   -For example, a call to this function may looks like this:
	//	 * 		
	//	 * 		Method m = MyActivity.class.getMethod("callback", 
	//	 *                                            Boolean.class,
	//	 *                                            String.class,
	//	 *                                            Storable.class);
	//	 * 		saveDataToCloud(this, DiningSession, m);
	//	 * 
	//	 * @param activity the activity this call originated from and
	//	 * associated with handler.
	//	 * @param obj the java object that will be saved to the cloud
	//	 * @param handler an instance method of activity for callback.
	//	 */
	//	public static void saveDataToCloud(Activity activity,
	//									   Storable obj,
	//									   Method handler) {
	//		final Method H = handler;
	//		final Activity ACT = activity;
	//		final ParseObject P_OBJ = obj.packObject();
	//		final Storable S = obj;
	//		P_OBJ.saveInBackground(new SaveCallback() {
	//			@Override
	//			public void done(ParseException e) {
	//				if (e == null) {
	//					// save was successful so send push
	//					Log.d(TAG, "Successfully saved object.");
	////					s.setObjId(pObj.getObjectId());
	//				} else {
	//					// Error occured
	//					Log.d(TAG, "Error: " + e.getMessage());
	//				}
	//
	//				try {
	//					if (H != null && ACT != null) {
	//						H.invoke(ACT, (e == null) ? Boolean.TRUE : Boolean.FALSE, 
	//								P_OBJ.getObjectId(), S);
	//					}
	//				} catch (IllegalArgumentException e1) {
	//					e1.printStackTrace();
	//				} catch (IllegalAccessException e1) {
	//					e1.printStackTrace();
	//				} catch (InvocationTargetException e1) {
	//					// TODO Auto-generated catch block (same as above)
	//					e1.printStackTrace();
	//				}
	//			}
	//		});
	//		// TODO
	//		// Handle failure
	//	}
	//
	//	/**
	//	 * Query for object in the cloud given a list of attributes. On return the
	//	 * call-back provided will be invoked with the list of results.
	//	 * 
	//	 * @param c Static reference to the type of java class to query on 
	//	 * @param handle Call back for the query operation (Method must be static)
	//	 * @param attr The attributes used for the query. Every object in the returned
	//	 * list will satisfy the attributes passed.
	//	 * 
	//	 * Note: Returns a List<Storable> to the handler.
	//	 */
	////	public static void getDataFromCloud(Class<? extends Storable> c, Method handle, 
	////			Map<String, String> attr) {
	////
	////		ParseQuery query = new ParseQuery(c.getSimpleName());
	////		if(attr != null) {
	////			Set<String> kSet = attr.keySet();
	////			for (String k : kSet) {
	////				String val = attr.get(k);
	////				query.whereEqualTo(k, val);
	////			}
	////		}
	////		final Method H = handle;
	////		query.findInBackground(new FindCallback() {
	////			public void done(List<ParseObject> list, ParseException e) {
	////				if (e == null) {
	////					if (list.size() > 0) {
	////						String className = list.get(0).getClassName();
	////						List<Storable> classList = new LinkedList<Storable>();
	////
	////						try {
	////							Storable s;
	////							for (ParseObject p : list) {
	////								Class<?> clazz = 
	////										Class.forName("uw.cse.dineon.library." + className);
	////								Constructor<?> ctor = 
	//											clazz.getConstructor(ParseObject.class);
	////								Object object = ctor.newInstance(new Object[] {p});
	////								classList.add((Storable) object);
	////								
	//////								s = (Storable) Class.forName("uw.cse.dineon.library."
	//////									+ className).newInstance();
	//////								s.unpackObject(p);
	//////								classList.add(s);
	////							}		   
	////							H.invoke(null, classList);
	////						} catch (Exception ex) {
	////							Log.d(TAG, "Error: " + ex.getMessage());
	////						}
	////					}
	////				} else {
	////					Log.d(TAG, "Error: " + e.getMessage());
	////				} // TODO
	////				// Invoke our method with null notifying 
	////				// User create account failed
	////			}
	////		});
	////	}
	//
	//	/**
	//	 * Retrieve data from the cloud based on the class name of the storable
	//	 * and a set of attributes. Attributes can be empty or null if you do not 
	//	 * need them. The attributes are a relation that must be true for the item/s
	//	 * that you receive. In SQL terms, this would the WHERE table.attribute = value
	//	 * clause.
	//	 * 	-getting a call back before te call example:
	//	 * 		Method m = MyActivity.class.getMethod("callback", List.class)
	//	 * 
	//	 * where callback is the name of the method, and List<Storable>	is the parameter
	//	 * 
	//	 * @param activity The instance the handle belongs to
	//	 * @param c class of storable you expect to get back
	//	 * @param handle An instance method with List<Storable> parameter
	//	 * @param attr Relation attributes that will be true for all data you
	//	 * receive.
	//	 */
	////	public static void getDataFromCloud(Activity activity, 
	////										Class<? extends Storable> c, 
	////										Method handle, 
	////										Map<String, String> attr) {
	////
	////		ParseQuery query = new ParseQuery(c.getSimpleName());
	////		if(attr != null) {
	////			Set<String> kSet = attr.keySet();
	////			for (String k : kSet) {
	////				String val = attr.get(k);
	////				query.whereEqualTo(k, val);
	////			}
	////		}
	////		final Activity ACT = activity;
	////		final Method H = handle;
	////		query.findInBackground(new FindCallback() {
	////			public void done(List<ParseObject> list, ParseException e) {
	////				if (e == null) {
	////					if (list.size() > 0) {
	////						String className = list.get(0).getClassName();
	////						List<Storable> classList = new LinkedList<Storable>();
	////
	////						try {
	////							Storable s;
	////							for (ParseObject p : list) {
	////								Class<?> clazz = 
	////										Class.forName("uw.cse.dineon.library." + className);
	////								Constructor<?> ctor = 
	//											clazz.getConstructor(ParseObject.class);
	////								Object object = ctor.newInstance(new Object[] {p});
	////								classList.add((Storable) object);
	////							}
	////							if (H != null && ACT != null) {
	////								H.invoke(ACT, classList);
	////							}
	////						} catch (Exception ex) {
	////							Log.d(TAG, "Error: " + ex.getMessage());
	////						}
	////					}
	////				} else {
	////					Log.d(TAG, "Error: " + e.getMessage());
	////				} // TODO
	////
	////				// Invoke our method with null notifying 
	////				// User create account failed
	////			}
	////		});
	////	}

	/**
	 * Notify a recipient that an action has occured or state has changed
	 * via a push notification configured with the specified properties.
	 * There must be a custom broadcast receiver on the receiving end which
	 * is subscribed to the sending channel.
	 * 
	 * @param jobj JSONObject to send
	 * @param channel The channel which the push is sent over
	 * 
	 * Note: There must must coordination b/t the sender and receiver on the
	 * format of attributes.
	 */
	public static void notifyApplication(JSONObject jobj, String channel) {
		try {
			ParsePush push = new ParsePush();
			push.setChannel(channel);
			push.setData(jobj);
			push.sendInBackground();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	/**
	 * Pack a Storable List into an array of ParseObjects.
	 * 
	 * @param list of Storable objects
	 * @return ParseObject
	 */
	public static ParseObject packListOfStorables(List<? extends Storable> list) {
		ParseObject container = new ParseObject("Container");

		for (int i = 0; i < list.size(); i++) {
			container.put("c" + i, list.get(i).packObject());
		}

		return container;
	}

	/**
	 * 
	 * @param container ParseObject
	 * @return List<Storable>
	 */
	public static List<Storable> unpackListOfStorables(ParseObject container) {
		if (!container.getClassName().equals("Container")) {
			throw new IllegalArgumentException();
		}

		List<Storable> list = new LinkedList<Storable>();
		try {
			for (String k : container.keySet()) {
				ParseObject p = container.getParseObject(k);
				Storable s = (Storable) Class.forName(p.getClassName()).newInstance();
				//				s.unpackObject(p);
				list.add(s);
			}
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}

		return list;
	}

	/**
	 * Given a storable class definition produce an instance of the Class based
	 * off the ParseObject.
	 * 
	 * @param <T> object extending Storable
	 * @param clazz Class to instantiate
	 * @param po Parse object representation of the Class
	 * @return null on failure, instance of T otherwise
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Storable> T parseObjectToClass(Class<T> clazz, ParseObject po) {
		Constructor<T> ctor = null;
		Object object = null;
		try {
			ctor = clazz.getConstructor(ParseObject.class);
			object = ctor.newInstance(new Object[] {po});	
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception occured while creating ParseObject: " + e.getMessage());
		}
		//		} catch (NoSuchMethodException e) {
		//			throw new RuntimeException("Constructor for: " + clazz.getSimpleName() 
		//					+ " that takes one ParseObject not found!");
		//		} catch (IllegalArgumentException e) {
		//			throw e;
		//		} catch (InstantiationException e) {
		//			throw new RuntimeException(
		//"Instantiation Exception for: " + clazz.getSimpleName() 
		//					+ " constructor that takes one ParseObject not found!");
		//		} catch (IllegalAccessException e) {
		//			throw new RuntimeException(
		//"Illegal Access Exception for: " + clazz.getSimpleName() 
		//					+ " constructor that takes one ParseObject not found!");
		//		} catch (InvocationTargetException e) {
		//			throw new RuntimeException("Invocation Exception for: " + clazz.getSimpleName() 
		//					+ " constructor that takes one 
		//ParseObject not found!\n" + e.getMessage());
		//		}
		if (object != null) {
			return (T)object;
		}
		return null;
	}

	/**
	 * Retrieves a list of parse objects that represent storable list.
	 * 
	 * @requires storables not equal null
	 * @param storables list of storable objects to obtain parse objects from
	 * @return List of ParseObject that represents storables
	 */
	public static List<ParseObject> toListOfParseObjects(List<? extends Storable> storables) {
		List<ParseObject> pObjects = new ArrayList<ParseObject>(storables.size());
		for (Storable s: storables) {
			pObjects.add(s.packObject());
		}
		return pObjects;
	}

	/**
	 * Retrieves a list of Class instances from a associated relation of the parseObjects.
	 * NOTE: All items in objects must have dynamic types of ParseObject or a class cast exception
	 * will be thrown
	 * 
	 * @param <T> object extending Storable
	 * @param clazz Class definition of the particular instance
	 * @param objects List of Objects that must have dynamic tyoes if ParseObjects
	 * @return List of storables from ParesObjects
	 */
	public static <T extends Storable> List<T> toListOfStorables(
			Class<T> clazz, List<Object> objects) {
		List<T> storables = new ArrayList<T>(objects.size());
		for (Object o: objects) {
			ParseObject p = (ParseObject) o;
			T storable = (T) parseObjectToClass(clazz, p);
			storables.add(storable);
		}
		return storables;
	}

	/**
	 * Returns the channel identifier for this Restaurant.
	 * @param rest Restaurant to find channel identifier for
	 * @return Channel as string
	 */
	public static String getChannel(RestaurantInfo rest) {
		return DineOnConstants.CHANNEL_PREFIX + rest.getName();
	}

	/**
	 * Returns the channel identifier for this Restaurant.
	 * @param name Restaurant to find channel identifier for
	 * @return Channel as string
	 */
	public static String getChannel(String name) {
		return DineOnConstants.CHANNEL_PREFIX + name;
	}


	/**
	 * Returns the channel identifier for this Restaurant.
	 * @param user User to extract channel from
	 * @return Channel as string
	 */
	public static String getChannel(UserInfo user) {
		return DineOnConstants.CHANNEL_PREFIX + user.getName();
	}

}
