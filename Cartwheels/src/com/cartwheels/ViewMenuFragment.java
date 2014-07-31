package com.cartwheels;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cartwheels.adapters.MenuItemAdapter;
import com.cartwheels.tasks.GetMenuItemsAsyncTask;
import com.cartwheels.tasks.MenuItemTaskFragment;

public class ViewMenuFragment extends ListFragment implements OnRefreshListener {

	private ObjectCartListItem item;
	private ArrayList<FoodMenuItem> items;
	
	private MenuItemAdapter adapter;
	private boolean loading;
	
	private SwipeRefreshLayout swipeLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		load();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		swipeLayout = new SwipeRefreshLayout(getActivity());
		swipeLayout.setLayoutParams(
				new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, 
						ViewGroup.LayoutParams.MATCH_PARENT));
		swipeLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
		return swipeLayout;
	}
	
	public static ViewMenuFragment newInstance(Bundle arguments) {
		ViewMenuFragment fragment = new ViewMenuFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	private void load() {
		loading = true;
		
		item = getArguments().getParcelable("ObjectCartListItem");
		String cartId = item.cartId;
		
		String scheme = "https";
		String authority = "cartwheels.us";
		String[] path = new String[2];
		path[0] = "menu_items";
		path[1] = "data";
		
		GetMenuItemsAsyncTask getMenuTask = new GetMenuItemsAsyncTask(scheme, authority, path, getActivity().getApplicationContext());
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		getMenuTask.put("email", email);
		getMenuTask.put("AuthToken", auth_token);
		
		getMenuTask.put("menu_item[cart_id]", cartId);
		
		MenuItemTaskFragment fragment = new MenuItemTaskFragment(40);
		fragment.setTask(getMenuTask);
		getMenuTask.setFragment(fragment);
		fragment.setTargetFragment(this, 40);
		
		fragment.show(getFragmentManager(), "ViewMenuFragment");
		fragment.execute();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		swipeLayout.setRefreshing(false);
		if (requestCode == 40 && resultCode == Activity.RESULT_OK) {
			ArrayList<FoodMenuItem> items = data.getParcelableArrayListExtra("result");
			this.items = items;
			
			adapter = new MenuItemAdapter(getActivity(), R.layout.listview_menu_items, items);
			setListAdapter(adapter);
			loading = false;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	    menu.clear();
	    inflater.inflate(R.menu.view_cart, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            load();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onRefresh() {
		load();
	}
}
