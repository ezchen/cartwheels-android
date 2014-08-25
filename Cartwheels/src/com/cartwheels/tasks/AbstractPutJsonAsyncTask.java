package com.cartwheels.tasks;

import java.util.HashMap;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public abstract class AbstractPutJsonAsyncTask<Results> extends AbstractJsonAsyncTask<Results> {

protected HashMap<String, String> objectValues;
	
	protected HashMap<String, String> innerObjectValues;

	protected String innerJsonObjKey;

	public AbstractPutJsonAsyncTask(Context context) {
		super(context);
		objectValues = new HashMap<String, String>();
		innerObjectValues = new HashMap<String, String>();
	}
	
	@Override
	public void onPreExecute() {}
	
	@Override
	protected Results doInBackground(String... urls) {
		Results results = null;
		DefaultHttpClient client = new DefaultHttpClient();
		String url = urls[0];
		HttpPut put = setUpPut(url);
		JSONObject holder = new JSONObject();
		JSONObject innerJsonObj = new JSONObject();
		
		String response = null;
		JSONObject json = new JSONObject();
		
		try {
			for (String key : objectValues.keySet()) {
				if (key.length() > 0) {
					String value = objectValues.get(key);
					holder.put(key, value);
					Log.d("doInBackground defaultPostJsonAsyncTask", value);
				}
			}
			
			if (innerJsonObjKey != null) {
				for (String key : innerObjectValues.keySet()) {
					if (key.length() > 0 && innerObjectValues.get(key) != null) {
						String value = innerObjectValues.get(key);
						innerJsonObj.put(key, value);
						Log.d("doInBackground defaultPostJsonAsyncTask", value);
					}
				}
				
				holder.put(innerJsonObjKey, innerJsonObj);
			}
			Log.d("doInBackground", "" + innerJsonObjKey);
			json.put("success", false);
			
			StringEntity se = new StringEntity(holder.toString());
			put.setEntity(se);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(put, responseHandler);
			
			json = new JSONObject(response);
			results = getResult(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public void put(String key, String value) {
		objectValues.put(key, value);
	}
	
	public void putInner(String key, String value) {
		innerObjectValues.put(key, value);
	}
	
	public void setInnerKey(String innerJsonObjKey) {
		this.innerJsonObjKey = innerJsonObjKey;
	}
	
	protected HttpPut setUpPut(String url) {
		HttpPut put = new HttpPut(url);
		put.setHeader("Accept", "application/json");
		put.setHeader("Content-Type", "application/json");
		
		return put;
	}
	
	protected abstract Results getResult(JSONObject json);
}
