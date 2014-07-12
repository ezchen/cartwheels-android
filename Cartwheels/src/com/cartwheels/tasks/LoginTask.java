package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask extends AbstractPostJsonAsyncTask<String> {

	@Override
	protected String getResult(JSONObject json) {
		try {
			return json.getJSONObject("data").getString("auth_token");
		} catch (JSONException e) {
			return null;
		}
	}

}
