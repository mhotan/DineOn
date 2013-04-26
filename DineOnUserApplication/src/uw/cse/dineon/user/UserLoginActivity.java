package uw.cse.dineon.user;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.PushService;
import com.parse.SaveCallback;

public class UserLoginActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//ParseAnalytics.trackAppOpened(this.getIntent());
		CustomerReceiver rec = new CustomerReceiver((TextView)this.findViewById(R.id.taskList));
		IntentFilter iff = new IntentFilter("com.parse.starter.UPDATE_STATUS");
		PushService.subscribe(this, "push", UserLoginActivity.class);
		this.registerReceiver(rec, iff);
		this.getOrders(null);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
	/** Called when the user clicks the add task button */
	public void addTask(View view) {
		EditText taskText = (EditText) findViewById(R.id.newTaskText);
		final String message = taskText.getText().toString();
		
		String user = "customer";//ParseUser.getCurrentUser().getUsername();
		
		ParseObject testObject = new ParseObject("º");
		testObject.put("item", message);
		testObject.put("uname", user);
		
		taskText.setText("");
		
		testObject.saveInBackground( new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// save was successful so send push
					share(message);
				} else {
					// Error occured
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});
	}
	
	/** Called when the user clicks the add task button */
	public void getOrders(View view) {
		final TextView list = (TextView) findViewById(R.id.taskList);
		
		ParseQuery query = new ParseQuery("Order");
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> taskList, ParseException e) {
		        if (e == null) {
		        	String newList = "";
		        	for (int i = 0; i < taskList.size(); i++) {
		        		newList += taskList.get(i).getString("uname") + " - " + taskList.get(i).getString("item") + "\n";
		        	}
		        	if (!newList.equals("")) list.setText(newList);
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }
		});
	}
	
	public void share(String msg){
		
		try{
			String user = "customer";//ParseUser.getCurrentUser().getUsername();

			JSONObject data = new JSONObject();
	        data.put("action", "com.parse.starter.UPDATE_STATUS");
	        data.put("uname", user);
	        data.put("item", msg);
	
			Log.d(TAG, data.toString());
			ParsePush push = new ParsePush();
			push.setChannel("push");
			push.setData(data);
			push.sendInBackground();
		}
		catch(Exception e){
			Log.d(TAG, e.getMessage());
		}
	}
}
