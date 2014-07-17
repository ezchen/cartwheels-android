package com.cartwheels.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;

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
			
			ArrayList<CharSequence> cartId = new ArrayList<CharSequence>();
			
			for (String key : items.keySet()) {
				if (key.contains("cartId")) {
					cartId.add(items.get(key));
				} else {
					intent.putExtra(key, items.get(key));
				}
			}
			
			intent.putCharSequenceArrayListExtra("cartId", cartId);
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
}
