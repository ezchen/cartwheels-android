package com.cartwheels.tasks;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.cartwheels.LoginFragment;


public class LoginWithInfoTaskFragment extends
		DefaultTaskFragment<LoginWithInfoTask, LoginFragment, HashMap<String, String>> {

	public LoginWithInfoTaskFragment(int id) {
		super(id);
	}

	@Override
	public void onTaskFinished(HashMap<String, String> items) {
		taskFinished();
		
		if (getTargetFragment() != null) {
			@SuppressWarnings("unchecked")
			LoginFragment fragment = (LoginFragment) getTargetFragment();
			
			
			Intent intent = new Intent();
			
			for (String key : items.keySet()) {
				intent.putExtra(key, items.get(key));
			}
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
}
