package com.cartwheels.tasks;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.DisplayCartsFragment;
import com.cartwheels.ObjectCartListItem;
import com.cartwheels.R;

/*
 * Fragment used to store AsyncTask to support orientation change in
 * the activity's lifecycle.
 */
public class TaskFragment extends DialogFragment {
	
	private SearchTask asyncTask;
	private LruCache<String, Bitmap> bitmapCache;
	ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//do not include this in the activitie's life cycle
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
			getTargetFragment().onActivityResult(getResources().getInteger(R.integer.search_task_fragment), Activity.RESULT_CANCELED, null);
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
	
	public void taskFinished(LruCache<String, Bitmap> cache, ObjectCartListItem[] items) {
		if (isResumed()) {
			Log.d("taskFinished", "dismissed");
			dismiss();
		}
		asyncTask = null;
		this.bitmapCache = cache;
		
		if (getTargetFragment() != null) {
			Log.d("taskFinished", "fragment is not null");
			DisplayCartsFragment fragment = (DisplayCartsFragment) getTargetFragment();
			fragment.buildList(bitmapCache, items);
			getTargetFragment().onActivityResult(getResources().getInteger(R.integer.search_task_fragment), Activity.RESULT_OK, null);
		}
	}
	
	public LruCache<String, Bitmap> getBitmapCache() {
		return bitmapCache;
	}
	
	public void setTask(SearchTask asyncTask) {
		this.asyncTask = asyncTask;
		
		asyncTask.setFragment(this);
	}
	
	public void execute() {
		asyncTask.execute();
	}
	
	public void update() {
		
	}
	
	public SearchTask getAsyncTask() {
		return asyncTask;
	}
	
	

}
