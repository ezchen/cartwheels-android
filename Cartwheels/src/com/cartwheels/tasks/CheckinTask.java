package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckinTask extends AbstractPostJsonAsyncTask<Boolean> {
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
