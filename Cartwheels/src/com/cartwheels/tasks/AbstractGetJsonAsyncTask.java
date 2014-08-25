package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri.Builder;

public abstract class AbstractGetJsonAsyncTask<Results> extends AbstractJsonAsyncTask<Results> {
	
	protected HashMap<String, String> objectValues;
	protected String scheme;
	protected String authority;
	protected String[] path;

	public AbstractGetJsonAsyncTask(String scheme, String authority, String[] path, Context context) {
		super(context);
		objectValues = new HashMap<String, String>();
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
	}
	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected Results doInBackground(String... urls) {
		Results results = null;
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = null;
		JSONObject json = new JSONObject();
		
		try {
			Builder uri = appendParameters();
			
			HttpGet get = setUpGet(uri);
			
			json.put("success", false);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			results = getResult(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	protected Builder appendParameters() {
		Builder uri = new Builder();
		uri.scheme(scheme).authority(authority);
		
		for (String pathArg : path) {
			uri.appendPath(pathArg);
		}
		
		for (String key : objectValues.keySet()) {
			if (objectValues.get(key).length() > 0) {
				uri.appendQueryParameter(key, objectValues.get(key));
			}
		}
		
		return uri;
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	protected HttpGet setUpGet(Builder uri) {
		HttpGet get = new HttpGet(uri.toString());
		get.setHeader("Accept", "application/json");
		get.setHeader("Content-Type", "application/json");
		
		return get;
	}
	
	protected abstract Results getResult(JSONObject json);
}
