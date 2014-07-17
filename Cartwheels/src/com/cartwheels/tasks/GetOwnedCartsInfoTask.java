package com.cartwheels.tasks;

import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.util.Log;

import com.cartwheels.ObjectCartListItem;

public class GetOwnedCartsInfoTask extends AbstractGetJsonAsyncTask<ArrayList<ObjectCartListItem>> {

	private ArrayList<String> cartId;
	
	public GetOwnedCartsInfoTask(String scheme, String authority, String[] path, ArrayList<String> cartId) {
		super(scheme, authority, path);
		this.cartId = cartId;
	}

	@Override
	public ArrayList<ObjectCartListItem> doInBackground(String... urls) {
		ArrayList<ObjectCartListItem> items = new ArrayList<ObjectCartListItem>();
		
		for (String id : cartId) {
			DefaultHttpClient client = new DefaultHttpClient();
			
			String response = null;
			JSONObject json = new JSONObject();
			
			Builder uri = appendParameters();
			uri.appendQueryParameter("cart[id]", id);
			HttpGet get = setUpGet(uri);
			
			try {
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = client.execute(get, responseHandler);
				
				json = new JSONObject(response);
				
				// create the ObjectCartListItem to add to the arraylist
				JSONArray jsonArray = json.getJSONArray("data");
				JSONObject innerJsonObj = jsonArray.getJSONObject(0);
				
				String name = innerJsonObj.getString("name");
				int rating = innerJsonObj.getInt("rating");
				String lat = innerJsonObj.getString("lat");
				String lon = innerJsonObj.getString("lon");
				String city = innerJsonObj.getString("city");
				String description = innerJsonObj.getString("description");
				String cartId = innerJsonObj.getString("id");
				String permit_number = innerJsonObj.getString("permit_number");
				String zip_code = innerJsonObj.getString("zip_code");
				
				JSONArray photoJsonArray = innerJsonObj.getJSONArray("photos");
				JSONObject defaultPhotoJson = photoJsonArray.getJSONObject(0);
				
				ObjectCartListItem item = new ObjectCartListItem();
				item.cartName = name;
				item.rating = rating;
				item.cartId = cartId;
				item.lat = lat;
				item.lon = lon;
				item.description = description;
				item.permit = permit_number;
				item.zipcode = zip_code;
				
				String defaultPhotoUrl = defaultPhotoJson.getString("image_url");
				
				item.bitmapUrl = "http://cartwheels.us" + defaultPhotoUrl;
				
				items.add(item);
				Log.d("doInBackground GetOwnedCartsInfoTask", json + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return items;
	}
	
	@Override
	protected ArrayList<ObjectCartListItem> getResult(JSONObject json) {
		return null;
	}

}
