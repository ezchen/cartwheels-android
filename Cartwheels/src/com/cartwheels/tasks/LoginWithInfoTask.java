package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
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
		uri.scheme("http").authority("cartwheels.us").appendPath(innerJsonObjKey + "s")
			.appendPath("data");
		
		if (info != null) {
		String auth_token = info.get("auth_token");
		
		if (auth_token != null && auth_token.length() > 0) {
		info.put("userType", innerJsonObjKey);
		
		String email = innerObjectValues.get("email");
		Log.d("email LoginWIthInfoTask", email);
		
		uri.appendQueryParameter("email", innerObjectValues.get("email"));
		uri.appendQueryParameter("auth_token", info.get("auth_token"));
		uri.appendQueryParameter("offset", "0");
		uri.appendQueryParameter("limit", "20");
		uri.appendQueryParameter("owner[email]", innerObjectValues.get("email"));
		
		HttpGet get = new HttpGet(uri.build().toString());
		
		get.setHeader("Accept", "application/json");
		get.setHeader("Content-Type", "application/json");
		
		try {
			json.put("success", false);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			
			JSONArray jsonInfo = json.getJSONArray("data");
			JSONObject innerJson = jsonInfo.getJSONObject(0);
			
			// take user name and time created
			String name = innerJson.getString("name");
			String createdAt = innerJson.getString("created_at");
			
			info.put("name", name);
			info.put("createdAt", createdAt);
			
			// take two carts to show as a preview on the home page later
			//JSONArray jsonCarts = innerJson.getJSONArray("carts");
			
			/*
			for (int i = 0; i < jsonCarts.length(); i++) {
				String cartId = jsonCarts.getJSONObject(i).getString("id");
				info.put("cartId" + i, cartId);
			}*/
			
			//Log.d("doInBackground LoginWithInfoTask", jsonCarts.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
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
