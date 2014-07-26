package com.cartwheels.tasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.cartwheels.FoodMenuItem;

public class GetMenuItemsAsyncTask extends AbstractGetJsonAsyncTask<ArrayList<FoodMenuItem>> {

	// returns jsonarray
	private final static String TAGS_DATA = "data";
	
	// returns int
	private final static String TAGS_ID = "id";
	
	// returns int
	private final static String TAGS_MENU_ID = "menu_id";
	
	// returns double/float
	private final static String TAGS_PRICE = "price";
	
	// returns String array
	private final static String TAGS_NOTES = "notes";
	
	// returns JSONObject
	private final static String TAGS_PHOTO = "photo";
	// returns string 
	private final static String TAGS_IMAGE_URL = "image_url";
	private final static String TAGS_IMAGE_URL_LARGE = "image_url_large";
	private final static String TAGS_DESCRIPTION = "description";
	private final static String TAGS_NAME="name";
	
	public GetMenuItemsAsyncTask(String scheme, String authority, String[] path, Context context) {
		super(scheme, authority, path, context);
	}

	@Override
	protected ArrayList<FoodMenuItem> getResult(JSONObject json) {
		ArrayList<FoodMenuItem> result = new ArrayList<FoodMenuItem>();
		try {
			JSONArray innerJsonArray = json.getJSONArray("data");
			
			for (int i = 0; i < innerJsonArray.length(); i++) {
				JSONObject innerJson = innerJsonArray.getJSONObject(i);
				
				int id = 0;
				int menu_id = 0;
				double price = 0;
				JSONArray notes = null;

				String image_url = "https://cartwheels.us/systems/images/default.png";
				String image_url_large = "https://cartwheels.us/systems/images/default.png";
				
				String description = null;
				String name = null;
				
				if (innerJson.has(TAGS_ID))
					id = innerJson.getInt(TAGS_ID);
				
				if (innerJson.has(TAGS_MENU_ID))
					menu_id = innerJson.getInt(TAGS_MENU_ID);
				
				if (innerJson.has(TAGS_PRICE))
					price = innerJson.getDouble(TAGS_PRICE);
				
				if (innerJson.has(TAGS_NOTES))
					notes = innerJson.getJSONArray(TAGS_NOTES);
				
				if (innerJson.has(TAGS_PHOTO)) {
					JSONObject photo = innerJson.getJSONObject(TAGS_PHOTO);
					
					if (photo.has(TAGS_IMAGE_URL))
						image_url = "https://cartwheels.us" + photo.getString(TAGS_IMAGE_URL);
					if (photo.has(TAGS_IMAGE_URL_LARGE))
						image_url_large = "https://cartwheels.us" + photo.getString(TAGS_IMAGE_URL_LARGE);
				}
				
				if (innerJson.has(TAGS_DESCRIPTION))
					description = innerJson.getString(TAGS_DESCRIPTION);
				
				if (innerJson.has(TAGS_NAME))
					name = innerJson.getString(TAGS_NAME);
				
				FoodMenuItem item = new FoodMenuItem();
				item.setId(id);
				item.setMenu_id(menu_id);
				item.setPrice(price);
				item.setImage_url(image_url);
				item.setImage_url_large(image_url_large);
				item.setDescription(description);
				item.setName(name);
				
				result.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
