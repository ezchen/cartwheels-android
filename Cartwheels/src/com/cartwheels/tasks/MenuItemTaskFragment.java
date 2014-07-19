package com.cartwheels.tasks;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.cartwheels.FoodMenuItem;


public class MenuItemTaskFragment extends
		DefaultTaskFragment<GetMenuItemsAsyncTask, Fragment, ArrayList<FoodMenuItem>> {

	public MenuItemTaskFragment(int id) {
		super(id);
	}

	@Override
	public void onTaskFinished(ArrayList<FoodMenuItem> items) {
		taskFinished();
		
		if (getTargetFragment() != null) {
			@SuppressWarnings("unchecked")
			Fragment fragment = getTargetFragment();
			
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("result", items);
			
			fragment.onActivityResult(fragmentId, Activity.RESULT_OK, intent);
		}
	}
}
