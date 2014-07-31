package com.cartwheels;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cartwheels.custom_views.ReviewItemAdapter;
import com.cartwheels.tasks.ReviewTask;
import com.cartwheels.tasks.ReviewTaskFragment;

public class ViewReviewFragment extends ListFragment
										implements OnItemClickListener, OnRefreshListener {

	private ReviewItem[] items;
	private ObjectCartListItem item;
	
	private FragmentManager fragmentManager;
	
	private int offset;
	private int limit;
	private SwipeRefreshLayout swipeLayout;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		offset = 0;
		limit = 20;
		
		fragmentManager = getFragmentManager();
		Resources resources = getResources();
		String fragmentTag = resources.getString(R.string.review_task_fragment_string);
		int fragmentId = resources.getInteger(R.integer.review_task_fragment);
		ReviewTaskFragment fragment = (ReviewTaskFragment) fragmentManager.findFragmentByTag(fragmentTag);
		
		if (fragment != null) {
			fragment.setTargetFragment(this, fragmentId);
		} else {
			Log.d("fragment", "null");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		item = getArguments().getParcelable("ObjectCartListItem");
		
		load();
		
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

	public static ViewReviewFragment newInstance(Bundle arguments) {
		ViewReviewFragment fragment = new ViewReviewFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Resources resources = getResources();
		//int fragmentId = resources.getInteger(R.integer.review_task_fragment);
		swipeLayout.setRefreshing(false);
		if (data != null && data.hasExtra("ReviewItems") && resultCode == Activity.RESULT_OK) {
			items = (ReviewItem[]) data.getParcelableArrayExtra("ReviewItems");
			buildList(items);
			Log.d("onActivityResult ViewReviewFragment", "" + items);
		}
	}
	
	private void buildList(ReviewItem[] items) {
		
		if (items != null) {
			ReviewItemAdapter adapter = new ReviewItemAdapter(getActivity(), R.layout.listview_review_row, items);
			setListAdapter(adapter);
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
	
	private void load() {
		String cartId = item.cartId;
		ReviewTask reviewTask = new ReviewTask(getActivity().getApplicationContext());
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		reviewTask.put("email", email);
		reviewTask.put("AuthToken", auth_token);
		reviewTask.put("offset", "" + offset);
		reviewTask.put("limit", "" + limit);
		
		reviewTask.setCartId(cartId);
		
		ReviewTaskFragment fragment = new ReviewTaskFragment();
		fragment.setTask(reviewTask);
		reviewTask.setFragment(fragment);
		fragment.setShowsDialog(false);
		
		Resources resources = getResources();
		int fragmentId = resources.getInteger(R.integer.review_task_fragment);
		String fragmentTag = resources.getString(R.string.review_task_fragment_string);
		fragment.setTargetFragment(this, fragmentId);
		
		fragment.show(getFragmentManager(), fragmentTag);
		fragment.execute();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh() {
		load();
	}
}
