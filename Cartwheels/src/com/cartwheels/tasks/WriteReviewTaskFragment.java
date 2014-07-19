package com.cartwheels.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class WriteReviewTaskFragment extends TaskFragment {

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(4, Activity.RESULT_CANCELED, null);
		}
	}
	
	public void onTaskFinished(boolean success) {
		taskFinished();
		
		// send data to the target fragment
		FragmentManager manager = getFragmentManager();
		Log.d("onTaskFinished GetPhotosTaskFragment", "" + manager);
		
		if (getTargetFragment() != null) {
			Log.d("taskFinished", "fragment is not null");
			Fragment fragment = getTargetFragment();

			Intent intent = new Intent();
			intent.putExtra("success", success);
			//Resources resources = getResources();
			//int fragmentId = resources.getInteger(R.integer.review_task_fragment);
			fragment.onActivityResult(4, Activity.RESULT_OK, intent);
		}
	}
	
	public void execute(String url) {
		((WriteReviewTask)asyncTask).execute(url);
	}
	
	public void execute() {
		((WriteReviewTask)asyncTask).execute();
	}
}
