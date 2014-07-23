package com.cartwheels.tasks;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.util.Log;

public class WriteReviewTask extends AsyncTask<String, Void, Boolean> {

	private HashMap<String, String> objectValues;
	private String id;
	private WriteReviewTaskFragment fragment;
	
	public WriteReviewTask() {
		objectValues = new HashMap<String, String>();
	}
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		DefaultHttpClient client = new DefaultHttpClient();
		boolean success = false;
		String response = null;
		JSONObject json = new JSONObject();
		
		try {
			Builder uri = new Builder();
			uri.scheme("https").authority("cartwheels.us").appendPath("carts")
				.appendPath(id).appendPath("reviews");
			
			for (String key : objectValues.keySet()) {
				if (objectValues.get(key).length() > 0) {
					uri.appendQueryParameter(key, objectValues.get(key));
				}
			}
			
			HttpPost post = new HttpPost(uri.toString());
			
			json.put("success", false);
			
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(post, responseHandler);
			
			json = new JSONObject(response);
			
			success = json.getBoolean("success");
			Log.d("jsonObject", json + "");
			Log.d("success", success + "");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
		fragment.onTaskFinished(success);
		super.onPostExecute(success);
	}

	public void setFragment(WriteReviewTaskFragment fragment) {
		this.fragment = fragment;
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public void setCartId(String id) {
		this.id = id;
	}

}
