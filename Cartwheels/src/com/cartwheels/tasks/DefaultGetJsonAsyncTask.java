package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.os.AsyncTask;

public abstract class DefaultGetJsonAsyncTask<Params, Results> extends AsyncTask<Params, Void, Results> {
	
	private HashMap<String, String> objectValues;
	private String scheme;
	private String authority;
	private String[] path;
	@SuppressWarnings("rawtypes")
	private DefaultTaskFragment fragment;

	public DefaultGetJsonAsyncTask(String scheme, String authority, String[] path) {
		objectValues = new HashMap<String, String>();
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
	}
	
	@Override
	public void onPreExecute() {}
	
	@Override
	public Results doInBackground(Params... params) {
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onPostExecute(Results items) {
		fragment.onTaskFinished(items);
		super.onPostExecute(items);
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
	
	public void setFragment(DefaultTaskFragment fragment) {
		this.fragment = fragment;
	}
	protected abstract Results getResult(JSONObject json);
}
