package com.cartwheels.tasks;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.R;

public abstract class TaskFragment extends DialogFragment {
	
	protected AsyncTask asyncTask;
	protected ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task, container);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressTaskFragment);
		
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
		
		/*
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(1, Activity.RESULT_CANCELED, null);
		} */
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
		if (isResumed()) {
			Log.d("taskFinished", "dismissed");
			dismiss();
		}
		
		asyncTask = null;
	}
	
	public void setTask(AsyncTask asyncTask) {
		this.asyncTask = asyncTask;
	}
	
	public void execute() {
		asyncTask.execute();
	}
	
	public AsyncTask getTask() {
		return asyncTask;
	}
}
