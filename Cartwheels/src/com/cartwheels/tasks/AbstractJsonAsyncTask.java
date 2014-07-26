package com.cartwheels.tasks;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public abstract class AbstractJsonAsyncTask<Results> extends AsyncTask<String, Void, Results> {

	protected DefaultTaskFragment fragment;
	protected final Context context;
	
	public AbstractJsonAsyncTask(Context context) {
		this.context = context;
	}
	@SuppressWarnings("rawtypes")
	public void setFragment(DefaultTaskFragment fragment) {
		this.fragment = fragment;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onPostExecute(Results items) {
		fragment.onTaskFinished(items);
		super.onPostExecute(items);
	}
	
	protected abstract Results getResult(JSONObject json);
	
	
}
