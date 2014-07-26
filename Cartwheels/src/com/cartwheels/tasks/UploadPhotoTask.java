package com.cartwheels.tasks;

import org.json.JSONObject;

import android.content.Context;

public class UploadPhotoTask extends AbstractPostJsonAsyncTask<Boolean> {

	public UploadPhotoTask(Context context) {
		super(context);
	}
	
	@Override
	protected Boolean getResult(JSONObject json) {
		Boolean value = false;
		try {
			return json.getBoolean("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
}
