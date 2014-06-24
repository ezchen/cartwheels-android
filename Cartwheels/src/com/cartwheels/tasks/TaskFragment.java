package com.cartwheels.tasks;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.R;
import com.google.android.gms.internal.as;

/*
 * Fragment used to store AsyncTask to support orientation change in
 * the activitie's lifecycle.
 */
public class TaskFragment<Params, Progress, Result> extends DialogFragment {
	
	private Fragment fragment;
	private myAsyncTask<Params, Progress, Result> asyncTask;
	ProgressBar progressBar;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// do not include this in the activitie's life cycle
		setRetainInstance(true);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task, container);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressTaskFragment);
		getDialog().setTitle("Progress Dialog");
		
		getDialog().setCanceledOnTouchOutside(false);
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		
		super.onDestroyView();
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (asyncTask != null) {
			asyncTask.cancel(false);
		}
		
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(TASK_FRAGMENT, Activity.RESULT_CANCELED, null);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (asyncTask == null) {
			dismiss();
		}
	}
	
	public void updateProgress(int percent) {
		progressBar.setProgress(percent);
	}
	
	public void taskFinished() {
		if (isResumed())
			dismiss();
		
		asyncTask = null;
		
		if (getTargetFragment() != null)
			getTargetFragment().onActivityResult(TASK_FRAGMENT, Activity.RESULT_OK, null);
	}
	
	
	public void setTargetFragment(Fragment fragment) {
		this.fragment = fragment;
	}
	
	public void setAsyncTask(myAsyncTask<Params, Progress, Result> asyncTask) {
		this.asyncTask = asyncTask;
		
		asyncTask.setFragment(this);
	}
	
	public void execute() {
		asyncTask.execute();
	}
	
	public void update() {
		
	}
	
	public Fragment getTargetFrag() {
		return fragment;
	}
	
	public AsyncTask getAsyncTask() {
		return asyncTask;
	}
	
	
	
	public static interface TaskCallbacks {
		public void onTaskFinished();
		//void onProgressUpdate(int percent);
		//void onCancelled();
		//void onPostExecute();
	}
}
