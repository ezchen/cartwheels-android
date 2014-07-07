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
import android.os.AsyncTask;
import android.util.Log;

import com.cartwheels.ObjectCartListItem;

public class ReviewTask extends AsyncTask<String, Void, ReviewItem[]> {

	private HashMap<String, String> objectValues;
	private int progress;
	private ReviewTaskFragment fragment;
	private String TAGS_DATA;
	private String TAGS_NAME;
	private String TAGS_RATING;
	private String TAGS_TEXT;
	
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected ReviewItem[] doInBackground(String... urls) {
        DefaultHttpClient client = new DefaultHttpClient();
        
        String response = null;
        JSONObject json = new JSONObject();
        
        ReviewItem[] items = null;
        
		try {
			Builder uri = new Builder();
			uri.scheme("http").authority("cartwheels.us").appendPath("carts")
				.appendPath("search");
			
			for (String key : objectValues.keySet()) {
				// if value is 0, do not append to query parameter
				if (objectValues.get(key).length() > 0) {
					Log.d(key, objectValues.get(key));
					uri.appendQueryParameter(key, objectValues.get(key));
				}
			}
			
			HttpGet get = new HttpGet(uri.toString());
			// default return values
			json.put("success", false);
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			items = buildList(json);
			
			Log.d("SearchTask", "jsonObject recieved");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	@Override
	protected void onPostExecute(ReviewItem[] items) {
		// Build List View
			
		Log.d("on post execute", "executed");
		fragment.onTaskFinished(items);
		
		super.onPostExecute(items);
	}
	
	@Override
	protected void onProgressUpdate(Void... unused) {
		if (fragment != null)
			fragment.updateProgress(progress);
	}

	public void setFragment(ReviewTaskFragment fragment) {
		this.fragment = fragment;
	}

	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public ReviewItem[] buildList(JSONObject json) {
		ReviewItem[] items = null;
		
		try {
			JSONArray reviews = json.getJSONArray(TAGS_DATA);
			Log.d("jsonarray", reviews.toString());
			
			items = new ReviewItem[reviews.length()];
			
			for (int i = 0; i < reviews.length(); i++) {
				JSONObject innerJson = reviews.getJSONObject(i);
				
				String user = innerJson.getString(TAGS_NAME);
				String text = innerJson.getString(TAGS_TEXT);
				int rating = innerJson.getInt(TAGS_RATING);
				
				ReviewItem reviewItem = new ReviewItem(user, text, rating);
				
				Log.d("cart list item", reviewItem.toString());
				items[i] = reviewItem;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			Log.e("NullPointerException", e.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.toString());
		}
		return items;
	}

}
