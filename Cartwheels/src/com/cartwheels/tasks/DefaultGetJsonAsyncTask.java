package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultGetJsonAsyncTask extends AbstractGetJsonAsyncTask<Boolean> {

	public DefaultGetJsonAsyncTask(String scheme, String authority,
			String[] path) {
		super(scheme, authority, path);
	}

	@Override
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

}
