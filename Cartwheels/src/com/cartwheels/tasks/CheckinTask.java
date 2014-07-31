package com.cartwheels.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class CheckinTask extends AbstractPostJsonAsyncTask<Boolean> {
	public CheckinTask(Context context) {
		super(context);
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
