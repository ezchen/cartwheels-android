package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RegisterTask extends AbstractPostJsonAsyncTask<String> {

	public RegisterTask(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

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
