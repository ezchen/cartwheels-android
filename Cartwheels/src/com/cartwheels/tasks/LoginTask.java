package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LoginTask extends AbstractPostJsonAsyncTask<String> {

	@Override
	protected String getResult(JSONObject json) {
		
		Log.d("getResult logintask", json + "");
		try {
			return json.getJSONObject("data").getString("auth_token");
		} catch (JSONException e) {
			return null;
		}
	}

}
