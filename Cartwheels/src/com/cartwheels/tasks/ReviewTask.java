package com.cartwheels.tasks;

import java.util.Arrays;
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

import com.cartwheels.ReviewItem;

public class ReviewTask extends AsyncTask<String, Void, ReviewItem[]> {

	private HashMap<String, String> objectValues;
	private String cartId;
	
	private int progress;
	private ReviewTaskFragment fragment;
	
	public static String TAGS_DATA = "data";
	public static String TAGS_NAME = "user_id";
	public static String TAGS_RATING = "rating";
	public static String TAGS_TEXT = "text";
	public static final String TAGS_REVIEW_ID = "id";
	private static final String TAGS_UPDATED_AT = "updated_at";
	private static final String TAGS_CREATED_AT = "created_at";
	
	public ReviewTask() {
		objectValues = new HashMap<String, String>();
	}
	
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
			uri.scheme("http").authority("cartwheels.us").appendPath("reviews")
				.appendPath("data");
			
			for (String key : objectValues.keySet()) {
				// if value is 0, do not append to query parameter
				if (objectValues.get(key).length() > 0) {
					Log.d(key, objectValues.get(key));
					uri.appendQueryParameter(key, objectValues.get(key));
				}
			}
			
			uri.appendQueryParameter("review[cart_id]", "73");
			
			HttpGet get = new HttpGet(uri.toString());
			// default return values
			json.put("success", false);
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			
			Log.d("doInBackground ReviewTask", json.toString());
			items = buildList(json, objectValues);
			
			if (items != null)
				Log.d("ReviewTask items:", Arrays.toString(items));
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
	
	public static JSONObject getUserJSON(String userId, HashMap<String, String> objectValues) {
		DefaultHttpClient client = new DefaultHttpClient();
        
        String response = null;
        JSONObject json = new JSONObject();
        
		try {
			Builder uri = new Builder();
			uri.scheme("http").authority("cartwheels.us").appendPath("users")
				.appendPath("data");
			
			for (String key : objectValues.keySet()) {
				// if value is 0, do not append to query parameter
				if (objectValues.get(key).length() > 0) {
					Log.d(key, objectValues.get(key));
					uri.appendQueryParameter(key, objectValues.get(key));
				}
			}
			
			uri.appendQueryParameter("user[id]", userId + "");
			
			HttpGet get = new HttpGet(uri.toString());
			// default return values
			json.put("success", false);
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			
			Log.d("doInBackground ReviewTask", json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public String getUserName(JSONObject json) {
		String userName = null;
		try {
			JSONArray info = json.getJSONArray(TAGS_DATA);
			JSONObject innerJson = info.getJSONObject(0);
			
			userName = innerJson.getString("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}
	
	public ReviewItem[] buildList(JSONObject json, HashMap<String, String> objectValues) {
		ReviewItem[] items = null;
		
		try {
			JSONArray reviews = json.getJSONArray(TAGS_DATA);
			Log.d("jsonarray", reviews.toString());
			
			items = new ReviewItem[reviews.length()];
			
			for (int i = 0; i < reviews.length(); i++) {
				JSONObject innerJson = reviews.getJSONObject(i);
				
				String userId = innerJson.getString(TAGS_NAME);
				String user = getUserName(getUserJSON(userId, objectValues));
				String text = innerJson.getString(TAGS_TEXT);
				int rating = innerJson.getInt(TAGS_RATING);
				String reviewId = innerJson.getString(TAGS_REVIEW_ID);
				String createdAt = innerJson.getString(TAGS_CREATED_AT);
				String updatedAt = innerJson.getString(TAGS_UPDATED_AT);
				
				ReviewItem reviewItem = new ReviewItem(user, text, rating);
				reviewItem.reviewId = reviewId;
				reviewItem.createdAt = createdAt;
				reviewItem.updatedAt = updatedAt;
				
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

	public void setCartId(String id) {
		this.cartId = id;
	}
}
