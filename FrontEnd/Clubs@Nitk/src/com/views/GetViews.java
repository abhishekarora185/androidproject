package com.views;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class GetViews {
	
	//This class defines all functions that perform GET requests at the Django server
	
	public static JSONArray posts(List<NameValuePair> params) throws JSONException{
		
		InputStream is=null;
		String result = null;
		StringBuilder sb=null; 
		try{	//this block establishes the connection to the server
		     HttpClient httpclient = new DefaultHttpClient();
		     
		     String url = "http://10.0.2.2:8000/posts?";
			 url += URLEncodedUtils.format(params, "utf-8"); 
		     
		     HttpGet http = new HttpGet(url);
		     
		     HttpResponse response = httpclient.execute(http);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     }catch(Exception e){
		         Log.e("log_tag", "Error in http connection"+e.toString());
		         return null;
		    }
		Log.d("Connection","success");

		//this block converts the response to string
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        result=sb.toString();
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		              return null;
		        }
		
		return new JSONArray(result);
		
	}

}
