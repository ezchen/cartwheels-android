package com.cartwheels;

import org.json.JSONException;
import org.json.JSONObject;

import com.cartwheels.tasks.DefaultPostJsonAsyncTask;

public class CheckinTask extends DefaultPostJsonAsyncTask<Boolean> {
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
