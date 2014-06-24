package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri.Builder;
import android.util.Log;

import com.cartwheels.SearchActivity;
import com.savagelook.android.UrlJsonAsyncTask;

public class SearchTask extends UrlJsonAsyncTask implements myAsyncTask {

	private static final String TAGS_DATA = "data";
	private static final String TAGS_ID = "id";
	private static final String TAGS_NAME = "name";
	private static final String TAGS_CITY = "city";
	private static final String TAGS_UPLOAD_ID="upload_id";
	private static final String TAGS_OWNER_SECRET="null";
	private static final String TAGS_PERMIT_NUMBER="permit_number";
	private static final String TAGS_ZIP_CODE="zip_code";
	private static final String TAGS_LAT="lat";
	private static final String TAGS_LON="lon";
	private static final String TAGS_CREATED_AT="created_at";
	private static final String TAGS_UPDATED_AT="updated_at";
	private static final String TAGS_PHOTOS="photos";
	
	private final SearchActivity searchActivity;
	private int progress;
	
	TaskFragment fragment;

	private HashMap<String, String> objectValues;
	
	public SearchTask(SearchActivity searchActivity, Context context) {
		super(context);
		this.searchActivity = searchActivity;
		objectValues = new HashMap<String, String>();
	}
	
	@Override
	protected JSONObject doInBackground(String... urls) {
        DefaultHttpClient client = new DefaultHttpClient();
        
        String response = null;
        JSONObject json = new JSONObject();
        
		try {
			Builder uri = new Builder();
			uri.scheme("http").authority("cartwheels.us").appendPath("carts")
				.appendPath("search");
			
			for (String key : objectValues.keySet()) {
				// if value is 0, do not append to query parameter
				
				if (objectValues.get(key).length() > 0)
					uri.appendQueryParameter(key, objectValues.get(key));
			}
			
			// hard coded values for testing
			uri.appendQueryParameter("offset", "0");
			uri.appendQueryParameter("limit", "100");
			
			HttpGet get = new HttpGet(uri.toString());
			// default return values
			json.put("success", false);
			json.put("info", "Logout Failed");
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			json = new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	@Override
	protected void onPostExecute(JSONObject json) {
		// Build List View
		try {
			JSONArray carts = json.getJSONArray(TAGS_DATA);
			Log.d("jsonarray", carts.toString());
			
			// test if json array is working
			// searchActivity.getFragment().buildList(carts);
			if 	(fragment == null)
				return;
			fragment.taskFinished();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		super.onPostExecute(json);
	}

	@Override
	protected void onProgressUpdate(Void... unused) {
		if (fragment != null)
			fragment.updateProgress(progress);
	}
	@Override
	public void setFragment(TaskFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	
}