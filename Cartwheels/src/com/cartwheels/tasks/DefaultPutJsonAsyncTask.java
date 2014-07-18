package com.cartwheels.tasks;

import org.json.JSONObject;

public class DefaultPutJsonAsyncTask extends AbstractPutJsonAsyncTask<Boolean> {

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
