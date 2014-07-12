package com.cartwheels.tasks;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.R;

public class DefaultTaskFragment<
						Task extends AbstractPostJsonAsyncTask< Results>,
						TargetFragment extends Fragment,
						Results>
						extends DialogFragment {

	protected int fragmentId;
	protected Task asyncTask;
	protected ProgressBar progressBar;
	
	public DefaultTaskFragment(int id) {
		this.fragmentId = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		if (savedInstanceState != null) {
			savedInstanceState.getInt("fragmentId", 0);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_task, container);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressTaskFragment);
		progressBar.setVisibility(View.GONE);
		getDialog().hide();
		//progressBar.setVisibility(View.INVISIBLE);
		view.setVisibility(View.GONE);
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
			dismiss();
		}
		
		asyncTask = null;
	}
	
	public void onTaskFinished(Results items) {
		if (items instanceof Boolean) {
			onTaskFinished((Boolean) items);
			return;
		} else if (items instanceof String) {
			onTaskFinished((String) items);
			return;
		}
		taskFinished();
		
		if (getTargetFragment() != null) {
			@SuppressWarnings("unchecked")
			TargetFragment fragment = (TargetFragment) getTargetFragment();
			
			Intent intent = new Intent();
			intent.putExtra("result", (Parcelable) items);
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
	
	public void onTaskFinished(Boolean items) {
		taskFinished();
		
		if (getTargetFragment() != null) {
			@SuppressWarnings("unchecked")
			TargetFragment fragment = (TargetFragment) getTargetFragment();
			
			Intent intent = new Intent();
			intent.putExtra("result", items.booleanValue());
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
	
	public void onTaskFinished(String items) {
		taskFinished();
		
		if (getTargetFragment() != null) {
			@SuppressWarnings("unchecked")
			TargetFragment fragment = (TargetFragment) getTargetFragment();
			
			Intent intent = new Intent();
			intent.putExtra("result", items);
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
	
	public void setTask(Task asyncTask) {
		this.asyncTask = asyncTask;
	}
	

	@SuppressWarnings("unchecked")
	public void execute() {
		asyncTask.execute();
	}
	
	public void execute(String... urls) {
		asyncTask.execute(urls);
	}
	
	public Task getTask() {
		return asyncTask;
	}
}
