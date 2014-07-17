package com.cartwheels;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.GetOwnedCartsInfoTask;
import com.cartwheels.tasks.UpdateOwnedCartTask;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class DisplayOwnedCartsFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_display_owned_carts, container, false);
		updateUserInfo();
		return view;
	}
	
	private void updateUserInfo() {
		String[] path = new String[2];
		path[0] = "owners";
		path[1] = "data";
		
		UpdateOwnedCartTask asyncTask = new UpdateOwnedCartTask("http", "cartwheels.us", path);
		DefaultTaskFragment<UpdateOwnedCartTask, DisplayOwnedCartsFragment, ArrayList<String>> fragment = 
				new DefaultTaskFragment<UpdateOwnedCartTask, DisplayOwnedCartsFragment, ArrayList<String>>(12) {
			@Override
			protected Intent getIntent(ArrayList<String> items) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("cartId", items);
				return intent;
			}
		};
		
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		asyncTask.put("email", email);
		
		String auth_token = preferences.getString("AuthToken","");
		asyncTask.put("auth_token", auth_token);
		
		String offset = "0";
		asyncTask.put("offset", offset);
		
		String limit = "1";
		asyncTask.put("limit", limit);
		
		asyncTask.put("owner[email]", email);
		
		asyncTask.setFragment(fragment);
		fragment.setTask(asyncTask);
		
		fragment.setTargetFragment(this, 12);
		fragment.show(getFragmentManager(), "getOwnedCarts");
		fragment.execute();
	}
	
	@Override
	public void onActivityResult(int resultCode, int requestCode, Intent data) {
		Log.d("onActivityResult", resultCode + "");
		if (resultCode == 12 && requestCode == Activity.RESULT_OK) {
			ArrayList<String> cartId = data.getStringArrayListExtra("cartId");
			
			// start another async task that retrieves more info about each cart
			String[] path = new String[2];
			path[0] = "carts";
			path[1] = "data";
			
			GetOwnedCartsInfoTask asyncTask = new GetOwnedCartsInfoTask("http", "cartwheels.us", path, cartId);
			
			
			SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
			String email = preferences.getString("email", "");
			String auth_token = preferences.getString("AuthToken", "");
			asyncTask.put("email", email);
			asyncTask.put("auth_token", auth_token);
			asyncTask.put("offset", "0");
			asyncTask.put("limit", "1");
			
			DefaultTaskFragment<GetOwnedCartsInfoTask, DisplayOwnedCartsFragment, ArrayList<ObjectCartListItem>> fragment =
					new DefaultTaskFragment<GetOwnedCartsInfoTask, DisplayOwnedCartsFragment, ArrayList<ObjectCartListItem>>(13) {
				@Override
				protected Intent getIntent(ArrayList<ObjectCartListItem> items) {
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra("ObjectCartListItems", items);
					return intent;
				}
			};
			
			fragment.setTask(asyncTask);
			asyncTask.setFragment(fragment);
			fragment.setTargetFragment(this, 13);
			
			fragment.show(getFragmentManager(), "getOwnedCartsInfo");
			fragment.execute();
		} else if (resultCode == 13 && requestCode == Activity.RESULT_OK) {
			if (data != null) {
				ArrayList<ObjectCartListItem> items = data.getParcelableArrayListExtra("ObjectCartListItems");
				
				buildList(items);
			}
		}
	}

	private void buildList(ArrayList<ObjectCartListItem> items) {
		DisplayOwnedCartsAdapter adapter = new DisplayOwnedCartsAdapter(R.layout.listview_owned_carts, getActivity(), items);
		SwingBottomInAnimationAdapter swingBottomInAnimation = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimation.setInitialDelayMillis(300);
		
		ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
		swingBottomInAnimation.setAbsListView(listView);
		setListAdapter(swingBottomInAnimation);
	}
}
