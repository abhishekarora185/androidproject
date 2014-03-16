//Returns the posts belonging to the group name entered.
package com.clubsatnitk;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.views.GetViews;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GroupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		//Needed to make sure that Http requests do not run on the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		final TextView posts = (TextView) findViewById(R.id.group_posts);
		final EditText name = (EditText)findViewById(R.id.group_name);
		final Button submit = (Button)findViewById(R.id.group_submit);
		final Button back = (Button)findViewById(R.id.mainintent);
		
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	            	
	            	if (user != null) {
	                posts.setText("Hello " + user.getName() + "!");
	              }
	            }
	          });
	        }
	      }
	    });
	    submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				map.add(new BasicNameValuePair("group_id", name.getText().toString()));
				StringBuilder stringBuilder = new StringBuilder();
				String finalString = "   "; 
				try {
					JSONArray response =  GetViews.groups(map);
					for (int i = 0; i < response.length() && i<5; i++) {  // **line 2**
					     JSONObject childJSONObject = response.getJSONObject(i);
					     
					     stringBuilder.append(childJSONObject.getString("author"));
					     stringBuilder.append("\n");
					     stringBuilder.append(childJSONObject.getString("content"));
					     stringBuilder.append("\n");
					     stringBuilder.append(childJSONObject.getString("comments"));
					}
					finalString = stringBuilder.toString(); 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				posts.setText(finalString);
			}
		});
	    
	    back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GroupActivity.this,MainActivity.class);
				startActivity(intent);
				
			}
		});
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

}
