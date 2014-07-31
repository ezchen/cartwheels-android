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

import android.content.Context;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.util.Log;

import com.cartwheels.TrustedHttpClient;

public class PhotoUrlTask extends AsyncTask<String, Void, String[]> {
	
	private final static String TAGS_DATA = "data";
	private final static String TAGS_PHOTOS = "photos";
	private final static String TAGS_IMAGE_URL = "image_url";
	private final static String TAGS_ID = "id";
	private final static String TAGS_PERMIT_NUMBER = "permit_number";
	private final static String TAGS_LON = "lon";
	private final static String TAGS_LAT = "lat";
	private final static String TAGS_ZIPCODE="zip_code";
	private final static String TAGS_DESCRIPTION="description";
	private final static String TAGS_NAME="name";
	private final static String TAGS_RATING="rating";
	private final static String TAGS_CITY="city";
	private HashMap<String, String> objectValues;
	
	private String cartId;
	
	private GetPhotosTaskFragment fragment;
	
	protected final Context context;
	public PhotoUrlTask(Context context) {
		this.context = context;
		objectValues = new HashMap<String, String>();
	}

	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected String[] doInBackground(String... urls) {
        DefaultHttpClient client = new TrustedHttpClient(context);
        
        String response = null;
        JSONObject json = new JSONObject();
        
        String[] items = null;
        Log.d("SearchTask doInBackground", "method entered");
		try {
			Builder uri = new Builder();
			uri.scheme("https").authority("cartwheels.us").appendPath("carts")
				.appendPath("data");
			
			for (String key : objectValues.keySet()) {
				// if value is 0, do not append to query parameter
				if (objectValues.get(key).length() > 0) {
					Log.d(key, objectValues.get(key));
					uri.appendQueryParameter(key, objectValues.get(key));
				}
			}
			
			uri.appendQueryParameter("cart[id]", cartId);
			
			HttpGet get = new HttpGet(uri.toString());
			// default return values
			json.put("success", false);
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(get, responseHandler);
			
			json = new JSONObject(response);
			
			Log.d("doInBackground PhotoUrlTask", json.toString());
			items = buildList(json);
			
			Log.d("doInBackground PhotoUrlTask", Arrays.toString(items));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	private String[] buildList(JSONObject json) {
		String[] items = null;
		
		try {
			JSONArray jsonCartData = json.getJSONArray(TAGS_DATA);
			Log.d("jsonarray", jsonCartData.toString());
			
			JSONObject jsonObject = jsonCartData.getJSONObject(0);
			
			JSONArray jsonArrayPhotoUrls = jsonObject.getJSONArray(TAGS_PHOTOS);
			
			Log.d("jsonArrayPhotoUrls", jsonArrayPhotoUrls.toString());
			items = new String[jsonArrayPhotoUrls.length()];
			for (int i = 0; i < jsonArrayPhotoUrls.length(); i++) {
				JSONObject jsonPhotoObject = jsonArrayPhotoUrls.getJSONObject(i);
				
				Log.d("jsonPhotoObject", jsonPhotoObject.toString());
				String photoUrl = "https://cartwheels.us" + jsonPhotoObject.getString(TAGS_IMAGE_URL);
				
				Log.d("cart list item", photoUrl);
				items[i] = photoUrl;
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
	
	@Override
	protected void onPostExecute(String[] items) {
		// Build List View
			
		Log.d("on post execute", "executed");
		fragment.onTaskFinished(items);
		
		super.onPostExecute(items);
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public void setFragment(GetPhotosTaskFragment fragment) {
		this.fragment = fragment;
	}
	
	public void setCartId(String id) {
		this.cartId = id;
	}
}
