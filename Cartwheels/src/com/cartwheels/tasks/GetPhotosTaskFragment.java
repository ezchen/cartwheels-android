package com.cartwheels.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.ViewCartPhotosFragment;

public class GetPhotosTaskFragment extends TaskFragment {
	ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		getDialog().hide();
		return null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(3, Activity.RESULT_CANCELED, null);
		}
	}
	
	public void onTaskFinished(String[] items) {
		taskFinished();
		
		// send data to the target fragment
		FragmentManager manager = getFragmentManager();
		Log.d("onTaskFinished GetPhotosTaskFragment", "" + manager);
		
		if (getTargetFragment() != null) {
			Log.d("taskFinished", "fragment is not null");
			ViewCartPhotosFragment fragment = (ViewCartPhotosFragment) getTargetFragment();

			Intent intent = new Intent();
			intent.putExtra("ImageUrls", items);
			
			//Resources resources = getResources();
			//int fragmentId = resources.getInteger(R.integer.review_task_fragment);
			fragment.onActivityResult(3, Activity.RESULT_OK, intent);
		}
	}
	
	public void execute(String url) {
		((PhotoUrlTask)asyncTask).execute(url);
	}
	
	public void execute() {
		((PhotoUrlTask)asyncTask).execute();
	}
}
