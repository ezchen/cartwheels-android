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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cartwheels.custom_views.SearchView.SearchListener;
import com.cartwheels.tasks.SearchTask;
import com.cartwheels.tasks.SearchTaskFragment;


public class DisplayCartsFragment extends Fragment
											implements OnItemClickListener,
														SearchListener {

	private ListView displayCarts;
	private ObjectCartListItem[] items;
	
	private FragmentManager fragmentManager;
	public static final int TASK_FRAGMENT = 0;
	public static final String TASK_FRAGMENT_TAG = "displayCarts";
	
	private String lastTextQuery;
	private String lastLocationQuery;
	
	private int offset;
	private int limit;
	
	// code up to onDetach() used to maintain callbacks to the activity
	private LruCache<String, Bitmap> bitmapCache;
	private TaskCallbacks taskCallbacks = dummyCallBacks;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("onAttach DisplayCartsFragment", "fragment is attached " + activity);
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException("Activity must implement TaskCallbacks");
		}
		taskCallbacks = (TaskCallbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		taskCallbacks = dummyCallBacks;
	}
	
	/* Other stuff not related to callbacks really */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// target fragment may have been redestroyed and recreated. Find it!
		fragmentManager = getFragmentManager();
		SearchTaskFragment fragment = (SearchTaskFragment) fragmentManager.findFragmentByTag("display");
		
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
		
		View view = inflater.inflate(R.layout.view_more_carts, container);
		View previousCarts = inflater.inflate(R.layout.load_previous_carts, container);
		displayCarts.addFooterView(previousCarts);
		displayCarts.addFooterView(view);
		
		
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
			ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(getActivity(),
																R.layout.listview_cart_row, items, bitmapCache);
			displayCarts.setAdapter(adapter);
			Log.d("onActivityCreated", "bitmapCache: " + bitmapCache);
			
			// set the offset and limit again
			offset = savedInstanceState.getInt("offset");
			limit = savedInstanceState.getInt("limit");
			
			lastTextQuery = savedInstanceState.getString("lastTextQuery", "");
			lastLocationQuery = savedInstanceState.getString("lastLocationQuery", "");
		} else {
			offset = 0;
			lastTextQuery = "";
			lastLocationQuery = "";
			limit = 20;
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArray("ObjectCartListItems", (Parcelable[]) items);
		outState.putInt("offset", offset);
		outState.putInt("limit", limit);
		outState.putString("lastTextQuery", lastTextQuery);
		outState.putString("lastLocationQuery", lastLocationQuery);
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
		this.bitmapCache = cache;
		
		if (items == null) {
			Toast.makeText(getActivity(), "no results", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(getActivity(),
															R.layout.listview_cart_row, items, cache);
		displayCarts.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		int length = parent.getAdapter().getCount();
		
		Log.d("position", "" + position);
		if (position == length - 1) {
			offset += 20;
			getMoreCarts();
			return;
		}
		
		if (position == length - 2) {
			offset -= 20;
			getMoreCarts();
			return;
		}
		
		Intent intent = new Intent(getActivity(), ViewCartActivity.class);
		intent.putExtra("ObjectCartListItem", items[position]);
		
		ObjectCartListItem item = items[position];
		
		Bitmap bitmap = null;
		if (item != null) {
			if (item.bitmapUrl != null)
				bitmap = bitmapCache.get(item.bitmapUrl);
		}
		
		intent.putExtra("bitmap", bitmap);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_viewMap && getActivity() != null) {
			Intent intent = new Intent(getActivity(), MarkerActivity.class);
			intent.putExtra("ObjectCartListItems", items);
			Log.d("onOptionsItemSelected", "item: " + items);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
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

	public void getMoreCarts() {
		SearchTask searchTask = new SearchTask();
		
		searchTask.put("tq", lastTextQuery);
		searchTask.put("lq", lastLocationQuery);
		
		searchTask.put("offset", "" + offset);
		searchTask.put("limit", "" + limit);
		
		getData(searchTask);
	}
	
	@Override
	public void search(String textQueryData, String locationQueryData) {
		if (getActivity() != null) {
			SearchTask searchTask = new SearchTask();
			// put in tq, lq, email, auth_token,
			searchTask.put("tq", textQueryData);
			searchTask.put("lq", locationQueryData);
			
			lastTextQuery = textQueryData;
			lastLocationQuery = locationQueryData;

			offset = 0;
			
			// offset always starts at 0
			searchTask.put("offset", 0 + "");
			searchTask.put("limit", "" + limit);
			
			
			if (textQueryData.length() == 0 && locationQueryData.length() == 0) {
				Toast.makeText(getActivity(), "Please complete one of the search fields", Toast.LENGTH_SHORT).show();
			} else {
				getData(searchTask);
			}
		}
	}
	
	public void getData(SearchTask searchTask) {
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		searchTask.put("email", email);
		searchTask.put("auth_token", auth_token);
		
		SearchTaskFragment taskFragment = new SearchTaskFragment();
		taskFragment.setTask(searchTask);
		
		taskFragment.setTargetFragment(this, R.integer.search_task_fragment);
		taskFragment.show(getFragmentManager(), "display");
		taskFragment.execute();
	}
}