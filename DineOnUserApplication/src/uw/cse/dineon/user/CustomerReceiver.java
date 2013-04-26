package uw.cse.dineon.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class CustomerReceiver extends BroadcastReceiver {

	private static final String TAG = "CustomerReceiver";
	private TextView tv;
	public CustomerReceiver(TextView tv) {
		this.tv = tv;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
 
			Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
			if(json.has("item") && json.has("uname")){
				this.tv.append(json.getString("uname") + " - " + json.getString("item") + "\n");
			}
			else{
				Log.d(TAG, "Push notifcation not for task list");
			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
	
}
