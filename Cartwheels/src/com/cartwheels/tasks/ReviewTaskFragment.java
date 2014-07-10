package com.cartwheels.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.R;
import com.cartwheels.ReviewItem;
import com.cartwheels.ViewReviewFragment;

public class ReviewTaskFragment extends TaskFragment {

	
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
			intent.putExtra("ReviewItems", items);
			
			//Resources resources = getResources();
			//int fragmentId = resources.getInteger(R.integer.review_task_fragment);
			fragment.onActivityResult(2, Activity.RESULT_OK, intent);
		}
	}
	
	public void execute(String url) {
		((ReviewTask)asyncTask).execute(url);
	}
	
	public void execute() {
		((ReviewTask)asyncTask).execute();
	}
}
