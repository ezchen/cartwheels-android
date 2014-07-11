package com.cartwheels.tasks;

import org.json.JSONObject;

import android.graphics.Bitmap;

public class UploadPhotoTask extends AbstractPostJsonAsyncTask<Boolean> {

	public UploadPhotoTask() {
		super();
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
