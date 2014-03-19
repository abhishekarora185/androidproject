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
import com.views.PostViews;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RequestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		final Button fetch = (Button)findViewById(R.id.fetch);
		final TextView display = (TextView)findViewById(R.id.textView1);
		final EditText group_name = (EditText)findViewById(R.id.editText1);
		final Button join = (Button)findViewById(R.id.button1);
		
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
			                display.setText("Hello " + user.getName() + "!");
			              }
			            }
			          });
			        }
			      }
			    });
	    
	    fetch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				//map.add(new BasicNameValuePair("group_id", group_name.getText().toString()));
				StringBuilder stringBuilder = new StringBuilder();
				String finalString = "   "; 
				try {
					JSONArray response =  GetViews.requests(map);
					for (int i = 0; i < response.length() && i<5; i++) {  // **line 2**
					     JSONObject childJSONObject = response.getJSONObject(i);
					     
					     stringBuilder.append(childJSONObject.getString("group_id"));
					     stringBuilder.append("\n");
					     stringBuilder.append(childJSONObject.getString("requests"));
					     stringBuilder.append("\n");
					}
					finalString = stringBuilder.toString(); 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				display.setText(finalString);
				
			}
		});
	    
	    join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				map.add(new BasicNameValuePair("group_id", group_name.getText().toString()));
				PostViews.addrequest(map);	
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.request, menu);
		return true;
	}

}
