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
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CommentActivity extends Activity {

	public String sample_post_id = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);//Needed to make sure that Http requests do not run on the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		final EditText post_id = (EditText) findViewById(R.id.post_id);
		final EditText comment = (EditText)findViewById(R.id.maintocomment);
		final Button submitcomment = (Button)findViewById(R.id.submitcomment);
		final Button back = (Button)findViewById(R.id.backmain);
	
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
	                comment.setText("Hello " + user.getName() + "!");
	              }
	            }
	          });
	        }
	      }
	    });
	    ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
		map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
	    try {
			JSONArray response = GetViews.posts(map);
			
			if(response.length()>1)
			{
				JSONObject childJSONObject = response.getJSONObject(0);
				sample_post_id = childJSONObject.getString("post_id");
				}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sample_post_id!=null)
	    {
	    	post_id.setText(sample_post_id);
	    }
	    submitcomment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> map = new ArrayList<NameValuePair>();
				map.add(new BasicNameValuePair("access_token", Session.getActiveSession().getAccessToken()));
				map.add(new BasicNameValuePair("content", comment.getText().toString()));
				map.add(new BasicNameValuePair("post_id", sample_post_id));
				PostViews.addcomment(map);
			}
		});
	
	    back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CommentActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}

}
