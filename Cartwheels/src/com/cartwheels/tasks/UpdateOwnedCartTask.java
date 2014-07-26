package com.cartwheels.tasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class UpdateOwnedCartTask extends AbstractGetJsonAsyncTask<ArrayList<String>> {

	public UpdateOwnedCartTask(String scheme, String authority, String[] path, Context context) {
		super(scheme, authority, path, context);
	}

	@Override
	protected ArrayList<String> getResult(JSONObject json) {
		ArrayList<String> cartIdList = new ArrayList<String>();
		
		try {
			
			JSONArray jsonInfo = json.getJSONArray("data");
			Log.d("jsonarray", jsonInfo + "");
			JSONObject innerJson = jsonInfo.getJSONObject(0);
			
			// take two carts to show as a preview on the home page later
			JSONArray jsonCarts = innerJson.getJSONArray("carts");
			
			for (int i = 0; i < jsonCarts.length(); i++) {
				String cartId = jsonCarts.getJSONObject(i).getString("id");
				cartIdList.add(cartId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cartIdList;
	}
}
