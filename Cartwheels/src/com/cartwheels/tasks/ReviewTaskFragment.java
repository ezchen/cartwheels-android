package com.cartwheels.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.cartwheels.ViewReviewFragment;

public class ReviewTaskFragment extends TaskFragment {

	private ReviewTask asyncTask;
	ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(1, Activity.RESULT_CANCELED, null);
		}
	}
	
	public void onTaskFinished(ReviewItem[] items) {
		taskFinished();
		
		// send data to the target fragment
		FragmentManager manager = getFragmentManager();
		Log.d("manager", "" + manager);
		
		if (getTargetFragment() != null) {
			Log.d("taskFinished", "fragment is not null");
			ViewReviewFragment fragment = (ViewReviewFragment) getTargetFragment();
			
			Intent intent = new Intent();
			intent.putExtra("mapBitmap", items);
			fragment.onActivityResult(1, Activity.RESULT_OK, intent);
		}
	}
	
	public void execute(String url) {
		((ReviewTask)asyncTask).execute(url);
	}
}
