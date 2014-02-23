package com.clubsatnitk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.model.*;
import com.views.GetViews;
import com.views.PostViews;

public class MainActivity extends Activity {
	
	//This class represents the homepage of the application.
	//It uses a ListView to display data received from the application server.

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Needed to make sure that Http requests do not run on the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		final TextView welcome = (TextView) findViewById(R.id.tv);
		final EditText content = (EditText) findViewById(R.id.content);
		final EditText group_id = (EditText) findViewById(R.id.group_id);
		final EditText type = (EditText) findViewById(R.id.type);
		final Button button = (Button) findViewById(R.id.fetch);
		final Button submit = (Button) findViewById(R.id.submit);
		
		welcome.setText("...");
		
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
	                welcome.setText("Hello " + user.getName() + "!");
	              }
	            	
	            	//Shift the code under "button"'s onClickListener here once you have no more use for the button
	            	//Needless to say, you will have to extract the relevant JSON data and put it in a custom list view,
	            	//using JSONArray and JSONObject
	            }
	          });
	        }
	      }
	    });
	    
	    button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<NameValuePair> params = new LinkedList<NameValuePair>();
		        params.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
		        params.add(new BasicNameValuePair("type", "public"));
		        //currently configured to fetch only public posts,
		        //will need to update this to fetch private posts once user and group views are working
		        JSONArray remoteData = null;
		        try{
		      	  remoteData = GetViews.posts(params);
		        }
		        catch(JSONException e){
		      	  welcome.setText("Could not connect to server.");
		        }
		        if(remoteData != null){
		      	  welcome.setText(remoteData.toString());
		        }
			}
		});
	    
	    submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				map.add(new BasicNameValuePair("content", content.getText().toString()));
				map.add(new BasicNameValuePair("group_id", group_id.getText().toString()));
				map.add(new BasicNameValuePair("type", type.getText().toString()));
				
				PostViews.posts(map);
			}
		});
	    
	  }

	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }

	}