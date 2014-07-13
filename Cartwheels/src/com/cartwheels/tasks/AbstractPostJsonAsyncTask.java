package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractPostJsonAsyncTask<Results> extends AbstractJsonAsyncTask<Results> {
	
	private HashMap<String, String> objectValues;
	
	private HashMap<String, String> innerObjectValues;

	private String innerJsonObjKey;

	public AbstractPostJsonAsyncTask() {
		objectValues = new HashMap<String, String>();
		innerObjectValues = new HashMap<String, String>();
	}
	
	@Override
	public void onPreExecute() {}
	
	@Override
	public Results doInBackground(String... urls) {
		Results results = null;
		DefaultHttpClient client = new DefaultHttpClient();
		String url = urls[0];
		HttpPost post = setUpPost(url);
		JSONObject holder = new JSONObject();
		JSONObject innerJsonObj = new JSONObject();
		
		String response = null;
		JSONObject json = new JSONObject();
		
		try {
			for (String key : objectValues.keySet()) {
				if (key.length() > 0) {
					String value = objectValues.get(key);
					holder.put(key, value);
					Log.d("doInBackground defaultPostJsonAsyncTask", value);
				}
			}
			
			if (innerJsonObjKey != null) {
				for (String key : innerObjectValues.keySet()) {
					if (key.length() > 0 && innerObjectValues.get(key) != null) {
						String value = innerObjectValues.get(key);
						innerJsonObj.put(key, value);
						Log.d("doInBackground defaultPostJsonAsyncTask", value);
					}
				}
				
				holder.put(innerJsonObjKey, innerJsonObj);
			}
			Log.d("doInBackground", "" + innerJsonObjKey);
			json.put("success", false);
			
			StringEntity se = new StringEntity(holder.toString());
			post.setEntity(se);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(post, responseHandler);
			
			json = new JSONObject(response);
			results = getResult(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public void putInner(String key, String value) {
		innerObjectValues.put(key, value);
	}
	
	public void setInnerKey(String innerJsonObjKey) {
		this.innerJsonObjKey = innerJsonObjKey;
	}
	
	protected HttpPost setUpPost(String url) {
		HttpPost post = new HttpPost(url);
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-Type", "application/json");
		
		return post;
	}
	
	protected abstract Results getResult(JSONObject json);
}
