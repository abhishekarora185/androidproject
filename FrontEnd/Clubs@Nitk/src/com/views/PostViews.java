package com.views;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class PostViews {
	
	//This class defines all functions that perform POST requests at the Django server

	public static void posts(ArrayList<NameValuePair> map) {
		
		InputStream is = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://10.0.2.2:8000/posts/");
			post.setEntity(new UrlEncodedFormEntity(map));
			
			HttpResponse response = client.execute(post);
			is = response.getEntity().getContent();	
		}
		catch(Exception e) {
			Log.e("log_tag","Error in http connection : " + e.toString());
		}
		
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       StringBuilder sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        Log.d("result",sb.toString());
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
	}
	
	public static void addcomment(ArrayList<NameValuePair> map) {
		
		InputStream is = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://10.0.2.2:8000/addcom/");
			post.setEntity(new UrlEncodedFormEntity(map));
			
			HttpResponse response = client.execute(post);
			is = response.getEntity().getContent();	
		}
		catch(Exception e) {
			Log.e("log_tag","Error in http connection : " + e.toString());
		}
		
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       StringBuilder sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        Log.d("result",sb.toString());
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
	}
	
	public static void addrequest(ArrayList<NameValuePair> map) {
		
		InputStream is = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://10.0.2.2:8000/requests/");
			post.setEntity(new UrlEncodedFormEntity(map));
			
			HttpResponse response = client.execute(post);
			is = response.getEntity().getContent();	
		}
		catch(Exception e) {
			Log.e("log_tag","Error in http connection : " + e.toString());
		}
		
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       StringBuilder sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        Log.d("result",sb.toString());
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
	}
	
public static void addmember(ArrayList<NameValuePair> map) {
		
		InputStream is = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://10.0.2.2:8000/members/");
			post.setEntity(new UrlEncodedFormEntity(map));
			
			HttpResponse response = client.execute(post);
			is = response.getEntity().getContent();	
		}
		catch(Exception e) {
			Log.e("log_tag","Error in http connection : " + e.toString());
		}
		
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       StringBuilder sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        Log.d("result",sb.toString());
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
	}
}
