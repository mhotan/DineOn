package uw.cse.dineon.user.checkin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseUser;

import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.ParseUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class QRCheckin {
	
	private static JSONObject userInfo = new JSONObject();
	private static String TAG = "QRCheckin";
	/**
	 * Processes the information gathered from a QR scan and sends the checkin
	 * request to the restaurant expecting a DiningSession as a result
	 * 
	 * @param requestCode QR format information
	 * @param resultCode -1 if successful and failed otherwise
	 * @param intent Information returned from the QR scan activity
	 */
	public static void QRResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode != Activity.RESULT_OK) {
			return; // TODO Handle failure
		}
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
			  // handle scan result
			  String contents = scanResult.getContents();
			  // TODO Handle Correct log in
			  try {
				  Log.d(TAG, contents);
				  userInfo.put("objID", ParseUser.getCurrentUser().getObjectId());
//				  userInfo.put(UserInfo.NAME, ParseUser.getCurrentUser().getUsername());
//				  userInfo.put(UserInfo.PHONE, 1234567890);
//				  userInfo.put(UserInfo.EMAIL, ParseUser.getCurrentUser().getEmail());
				  JSONObject data = new JSONObject(contents);
				  //Log.d("ZXing", data.toString());
				  Map<String, String> attr = new HashMap<String, String>();
				  attr.put("userInfo", userInfo.toString()); 
				  attr.put("tableID", data.getInt("TABLE_NUM") + "");
				  ParseUtil.notifyApplication(
						  "uw.cse.dineon.user.REQUEST_DINING_SESSION", 
						  attr, 
						  "uw_cse_dineon_" + data.getString("RESTAURANT"));
			  } catch (JSONException e) {
				  Log.d("ZXing", "Error: " + e.getMessage());
			  }
			  
		  } else {
			  // else continue with any other code you need in the method
			  Log.d("ZXing", "Error getting the result");
		  }
	}
}
