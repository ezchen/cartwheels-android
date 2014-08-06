package com.cartwheels.tasks;

public interface AsyncTaskCallbacks<Results> {

	public void onTaskFinished(Results results);
}
