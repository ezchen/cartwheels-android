package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.cartwheels.TrustedHttpClient;

import android.content.Context;
import android.net.Uri.Builder;

public abstract class AbstractDeleteJsonAsyncTask<Results> extends AbstractJsonAsyncTask<Results> {

	protected String scheme;
	protected String authority;
	protected String[] path;
	
	protected HashMap<String, String> objectValues;
	
	public AbstractDeleteJsonAsyncTask(String scheme, String authority, String[] path, Context context) {
		super(context);
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
		objectValues = new HashMap<String, String>();
	}

	@Override
	protected Results doInBackground(String... arg0) {
		DefaultHttpClient client = new TrustedHttpClient(context);
		String response = null;
		JSONObject json = new JSONObject();
		
		Results result = null;
		
		try {
			Builder uri = appendParameters();
			
			HttpDelete delete = new HttpDelete(uri.build().toString());
			
			json.put("success", false);
			json.put("info", "Logout Failed");
			
			delete.setHeader("Accept", "application/json");
			delete.setHeader("Content-Type", "application.json");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(delete, responseHandler);
			json = new JSONObject(response);
			
			result = getResult(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected Builder appendParameters() {
		Builder uri = new Builder();
		uri.scheme(scheme).authority(authority);
		
		for (String pathArg : path) {
			if (pathArg != null)
				uri.appendPath(pathArg);
		}
		
		for (String key : objectValues.keySet()) {
			uri.appendQueryParameter(key, objectValues.get(key));
		}
		
		return uri;
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
}
