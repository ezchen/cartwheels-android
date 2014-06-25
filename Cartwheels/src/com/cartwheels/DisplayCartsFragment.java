package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cartwheels.custom_views.SearchView.SearchListener;
import com.cartwheels.tasks.SearchTask;
import com.cartwheels.tasks.TaskFragment;


public class DisplayCartsFragment extends Fragment
											implements OnItemClickListener,
														SearchListener {

	private ListView displayCarts;
	private ObjectCartListItem[] items;
	
	private FragmentManager fragmentManager;
	public static final int TASK_FRAGMENT = 0;
	public static final String TASK_FRAGMENT_TAG = "displayCarts";
	
	// code up to onDetach() used to maintain callbacks to the activity
	private LruCache<String, Bitmap> bitmapCache;
	private TaskCallbacks taskCallbacks = dummyCallBacks;
	private Activity activity;
	
	public DisplayCartsFragment() {
	}


	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("onAttach DisplayCartsFragment", "fragment is attached");
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException("Activity must implement TaskCallbacks");
		}
		this.activity = activity;
		taskCallbacks = (TaskCallbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		taskCallbacks = dummyCallBacks;
		activity = null;
	}
	
	/* Other stuff not related to callbacks really */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// target fragment may have been redestroyed and recreated. Find it!
		fragmentManager = getFragmentManager();
		TaskFragment fragment = (TaskFragment) fragmentManager.findFragmentByTag("display");
		
		if (fragment != null) {
			fragment.setTargetFragment(this, TASK_FRAGMENT);
			this.bitmapCache = fragment.getBitmapCache();
			Log.d("fragment", "is not null");
		} else {
			Log.d("fragment", "null");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		displayCarts = (ListView) inflater.inflate(R.layout.fragment_search,
				container, false);
		
		displayCarts.setOnItemClickListener(this);
		
		
		return displayCarts;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		// Restore the list
		if (savedInstanceState != null) {
			items = (ObjectCartListItem[]) savedInstanceState.getParcelableArray("ObjectCartListItems");
			
			
						
			RetainFragment storage = RetainFragment.findOrCreateRetainFragment(getFragmentManager());
			bitmapCache = storage.retainedCache;
			
			if (items == null || bitmapCache == null)
				return;
			// recreate the list
			ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(activity,
																R.layout.listview_cart_row, items, bitmapCache);
			displayCarts.setAdapter(adapter);
			Log.d("onActivityCreated", "bitmapCache: " + bitmapCache);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArray("ObjectCartListItems", (Parcelable[]) items);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TASK_FRAGMENT && resultCode == Activity.RESULT_OK) {
			taskCallbacks.onTaskFinished();
		}
	}
	
	public void buildList(LruCache<String, Bitmap> cache, ObjectCartListItem[] items) {
		this.items = items;
		ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(activity,
															R.layout.listview_cart_row, items, cache);
		displayCarts.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(activity, ViewCartActivity.class);
		intent.putExtra("ObjectCartListItem", items[position]);
		startActivity(intent);
	}
	
	
	public static interface TaskCallbacks {
		public void onTaskFinished();
		public void onProgressUpdate(int percent);
		public void onCancelled();
		public void onPostExecute();
	}
	
	private static TaskCallbacks dummyCallBacks = new TaskCallbacks() {
		public void onTaskFinished() {
			
		}
		
		public void onProgressUpdate(int percent) {
			
		}
		
		public void onCancelled() {
			
		}
		
		public void onPostExecute() {
			
		}
	};

	@Override
	public void search(String textQueryData, String locationQueryData) {
		if (activity != null) {
			SharedPreferences preferences = activity.getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
			
			SearchTask searchTask = new SearchTask();
			
			// put in tq, lq, email, auth_token,
			searchTask.put("tq", textQueryData);
			searchTask.put("lq", locationQueryData);
			
			String email = preferences.getString("email", "");
			String auth_token = preferences.getString("AuthToken", "");
			searchTask.put("email", email);
			searchTask.put("auth_token", auth_token);
			
			Log.d("searchActivity", "search called: " + textQueryData + " " + locationQueryData);
			Log.d("searchActivity search", "searchTask created");
			
			TaskFragment taskFragment = new TaskFragment();
			taskFragment.setTask(searchTask);
			
			taskFragment.setTargetFragment(this, R.integer.search_task_fragment);
			taskFragment.show(getFragmentManager(), "display");
			taskFragment.execute();
			
			Log.d("searchActivity search", "TaskFragment SuccessfullyCreated");
			if (textQueryData.length() == 0 && locationQueryData.length() == 0) {
				Toast.makeText(getActivity(), "Please complete one of the search fields", Toast.LENGTH_SHORT).show();
			} else {
				
			}
		}
	}
}