package com.cartwheels.tasks;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class DefaultDeleteAsyncTask extends
		AbstractDeleteJsonAsyncTask<Boolean> {

	public DefaultDeleteAsyncTask(String scheme, String authority, String[] path, Context context) {
		super(scheme, authority, path, context);
	}

	@Override
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("getResult", e.toString());
			return false;
		}
	}

}
