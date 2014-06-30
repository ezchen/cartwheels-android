package com.cartwheels.tasks;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.cartwheels.ObjectCartListItem;

public class SearchTask extends AsyncTask<String, Void, ObjectCartListItem[]> 
									implements myAsyncTask {

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
	private static final String TAGS_URL_THUMB="image_url_thumb";
	
	private int progress;
	
	private LruCache<String, Bitmap> bitmapCache;
	
	SearchTaskFragment fragment;

	private HashMap<String, String> objectValues;
	
	public SearchTask() {
		objectValues = new HashMap<String, String>();
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		bitmapCache = new LruCache<String, Bitmap>(maxMemory / 8);
	}
	
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected ObjectCartListItem[] doInBackground(String... urls) {
        DefaultHttpClient client = new DefaultHttpClient();
        
        String response = null;
        JSONObject json = new JSONObject();
        
        ObjectCartListItem[] items = null;
        Log.d("SearchTask doInBackground", "method entered");
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
			json.put("info", "Logout Failed");
			
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
	protected void onPostExecute(ObjectCartListItem[] items) {
		// Build List View
			
		Log.d("on post execute", "executed");
		fragment.taskFinished(bitmapCache, items);
		
		super.onPostExecute(items);
	}

	@Override
	protected void onProgressUpdate(Void... unused) {
		if (fragment != null)
			fragment.updateProgress(progress);
	}
	@Override
	public void setFragment(SearchTaskFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public ObjectCartListItem[] buildList(JSONObject json) {
		ObjectCartListItem[] items = null;
		
		try {
			JSONArray carts = json.getJSONArray(TAGS_DATA);
			Log.d("jsonarray", carts.toString());
			
			items = new ObjectCartListItem[carts.length()];
			
			for (int i = 0; i < carts.length(); i++) {
				JSONObject innerJson = carts.getJSONObject(i);
				
				String cartName = innerJson.getString(TAGS_NAME);
				String cartZipcode = innerJson.getString(TAGS_ZIP_CODE);
				String cartPermit = innerJson.getString(TAGS_PERMIT_NUMBER);
				String lat = innerJson.getString(TAGS_LAT);
				String lon = innerJson.getString(TAGS_LON);
				
				JSONArray arrayBitmapUrl = innerJson.getJSONArray(TAGS_PHOTOS);
				
				String bitmapUrl = null;
				if (arrayBitmapUrl.length() > 0) {
					JSONObject jsonBitmapUrl = arrayBitmapUrl.getJSONObject(0);
					bitmapUrl = jsonBitmapUrl.getString(TAGS_URL_THUMB);
				}
				if (bitmapUrl != null)
					cacheBitmap("http://cartwheels.us" + bitmapUrl);
				ObjectCartListItem cartListItem = new ObjectCartListItem("http://cartwheels.us" + bitmapUrl, cartName,
														cartZipcode, cartPermit);
				cartListItem.lat = lat;
				cartListItem.lon = lon;
				
				Log.d("cart list item", cartListItem.toString());
				items[i] = cartListItem;
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
	
	private void cacheBitmap(String url) {
		Bitmap bitmap = downloadBitmap(url);
		
		if (bitmap != null) {
			addBitmapToCache(bitmapCache, url, bitmap);
		}
	}
	
	private static void addBitmapToCache(LruCache<String, Bitmap> cache, 
												String key, Bitmap bitmap) {
		cache.put(key, bitmap);
	}
	
	private static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }
 
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
}