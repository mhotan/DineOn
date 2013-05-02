package uw.cse.dineon.library.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.parse.PushService;

public class DineOnReceiver extends BroadcastReceiver {

	private static final String TAG = "DineOnReceiver";
	
	private Method handler;
	
	private DineOnReceiver(Method h){
		this.handler = h;
	}
	
	/**
	 * Factory method that builds and returns a new DineOnRecevier for receiving
	 * push notifications specific to the context and call-back function provided.
	 * 
	 * @param h Call-back function for push notifications received
	 * @param ctx Context of the activity that this receiver is connected to
	 * @param act The activity associated with the receiver
	 * @param action The action specifying the type of push notifications received 
	 * by the receiver
	 * @param channel The channel the receiver subscribes to for intercepting 
	 * notifications
	 * @return Constructed DineOnReceiver given arguments
	 */
	public static DineOnReceiver createDineOnRecevier(
			Method h, Context ctx, Class<? extends FragmentActivity> act, String action, String channel) {
		
		DineOnReceiver rec = new DineOnReceiver(h);
		IntentFilter iff = new IntentFilter(action);
		PushService.subscribe(ctx, channel, act);
		ctx.registerReceiver(rec, iff);
		
		rec.handler = h;
		
		return rec;
	}
	
	/**
	 * Call-back for a push notification matching the attributes of the
	 * broadcast receiver. Calls the user specified callback set on creation.
	 * 
	 * @param context 
	 * @param intent Android Intent containing the data associated with the
	 * push notification.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
 
			Iterator<String> it = json.keys();
			Map<String, String> retMap = new HashMap<String, String>();
			while (it.hasNext()) {
				String next = it.next();
				retMap.put(next, json.getString(next));
			}

			handler.invoke(null, retMap);
		
		} catch (IllegalAccessException e) {
			Log.d(TAG, "ReflectionException: " + e.getMessage());
		} catch (InvocationTargetException e) {
			Log.d(TAG, "ReflectionException: " + e.getMessage());
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
	
}
