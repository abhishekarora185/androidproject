package com.clubsatnitk;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		final Button group = (Button) findViewById(R.id.groupintent);
		final Button maintocomment = (Button)findViewById(R.id.maintocomment);
		final Button user = (Button)findViewById(R.id.button1);
		final Button request = (Button)findViewById(R.id.button2);
		final Button member = (Button)findViewById(R.id.member); 
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
	   /* 
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
	    */
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
	   
	    button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				//map.add(new BasicNameValuePair("content", content.getText().toString()));
				//map.add(new BasicNameValuePair("group_id", group_id.getText().toString()));
				//map.add(new BasicNameValuePair("type", type.getText().toString()));
				StringBuilder stringBuilder = new StringBuilder();
				String finalString = "   "; 
				try {
					JSONArray response =  GetViews.posts(map);
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
					System.out.println("Response not proper");
					e.printStackTrace();
				}
				welcome.setText(finalString);
			}
		});
	    
	    group.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,GroupActivity.class);
				startActivity(intent);
			}
		});
	    
	    maintocomment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,CommentActivity.class);
				startActivity(intent);
			}
		});
	    
	    user.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,UserActivity.class);
				startActivity(intent);
			}
		});
	    request.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,RequestActivity.class);
				startActivity(intent);
			}
		});
	    member.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,MemberActivity.class);
				startActivity(intent);
			}
		});
	  }
	

	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }

	}