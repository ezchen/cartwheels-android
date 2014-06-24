package com.cartwheels.tasks;

import android.os.AsyncTask;

public abstract class myAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private TaskFragment fragment;
	
	public void setFragment(TaskFragment fragment) {
		this.fragment = fragment;
	}
}
