package uw.cse.dineon.user;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.util.DineOnConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author mhotan
 */
public class DineOnUserReceiver extends BroadcastReceiver {

	private static final String TAG = DineOnUserReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString(DineOnConstants.PARSE_CHANNEL);
			String data = intent.getExtras().getString(DineOnConstants.PARSE_DATA);
			JSONObject json = new JSONObject(data);

			Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
			Iterator itr = json.keys();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				Log.d(TAG, "..." + key + " => " + json.getString(key));
			}
			
			// Relay the information forward
			Intent i = new Intent(context, DineOnUserService.class);
			i.putExtra(DineOnConstants.PARSE_DATA, data);
			i.putExtra(DineOnConstants.PARSE_CHANNEL, channel);
			i.setAction(intent.getAction());
			context.startService(i);
			
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}	
	}

}
