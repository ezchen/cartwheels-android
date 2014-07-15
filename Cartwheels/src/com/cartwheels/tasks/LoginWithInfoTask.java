package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.util.Log;

public class LoginWithInfoTask extends AbstractPostJsonAsyncTask<HashMap<String, String>> {

	@Override
	public HashMap<String, String> doInBackground(String... urls) {
		HashMap<String, String> info = super.doInBackground(urls);
		
		// Separate http request to get the user info as well
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = null;
		JSONObject json = new JSONObject();
		
		Builder uri = new Builder();
		uri.scheme("http").authority("cartwheels.us").appendPath("users")
			.appendPath("data");
		uri.appendQueryParameter("email", objectValues.get("email"));
		uri.appendQueryParameter("auth_token", info.get("auth_token"));
		uri.appendQueryParameter("offset", "0");
		uri.appendQueryParameter("limit", "1");
		uri.appendQueryParameter("user[email]", objectValues.get("email"));
		
		HttpGet get = new HttpGet(uri.build().toString());
		
		get.setHeader("Accept", "application/json");
		get.setHeader("Content-Type", "application/json");
		
		try {
			json.put("success", false);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			
			Log.d("doInBackground ReviewTask", json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return info;
	}
	
	@Override
	protected HashMap<String, String> getResult(JSONObject json) {
		HashMap<String, String> info = new HashMap<String, String>();
		
		try {
			info.put("auth_token", json.getJSONObject("data").getString("auth_token"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return info;
	}

}
