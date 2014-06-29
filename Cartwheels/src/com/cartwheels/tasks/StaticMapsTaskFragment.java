package com.cartwheels.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.cartwheels.ViewCartFragment;

public class StaticMapsTaskFragment extends TaskFragment {
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (getTargetFragment() != null) {
			getTargetFragment().onActivityResult(1, Activity.RESULT_CANCELED, null);
		}
	}
	
	public void onTaskFinished(Bitmap mapBitmap) {
		taskFinished();
		
		// send data to the target fragment
		FragmentManager manager = getFragmentManager();
		Log.d("manager", "" + manager);
		
		if (getTargetFragment() != null) {
			Log.d("taskFinished", "fragment is not null");
			ViewCartFragment fragment = (ViewCartFragment) getTargetFragment();
			
			Intent intent = new Intent();
			intent.putExtra("mapBitmap", mapBitmap);
			fragment.onActivityResult(1, Activity.RESULT_OK, intent);
		}
	}
	
	public void execute(String url) {
		((ImageDownloaderTask)asyncTask).execute(url);
	}
	
}
