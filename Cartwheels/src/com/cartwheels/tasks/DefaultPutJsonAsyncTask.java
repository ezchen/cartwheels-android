package com.cartwheels.tasks;

import org.json.JSONObject;

import android.content.Context;

public class DefaultPutJsonAsyncTask extends AbstractPutJsonAsyncTask<Boolean> {

	public DefaultPutJsonAsyncTask(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Boolean getResult(JSONObject json) {
		try {
			return json.getBoolean("success");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
