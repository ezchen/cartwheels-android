package com.cartwheels.tasks;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultPostJsonAsyncTask extends
		AbstractPostJsonAsyncTask<Boolean> {

	@Override
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
}
