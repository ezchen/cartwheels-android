package com.cartwheels;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.cartwheels.adapters.CartListItemAdapter;
import com.cartwheels.custom_views.SearchView;
import com.cartwheels.custom_views.SearchView.SearchListener;
import com.cartwheels.tasks.SearchTask;
import com.cartwheels.tasks.SearchTaskFragment;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public class DisplayCartsFragment extends Fragment
											implements OnItemClickListener,
														SearchListener, OnScrollListener {

	private ListView displayCarts;
	//private ObjectCartListItem[] items;
	
	private ArrayList<ObjectCartListItem> arrayListItems;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private CartListItemAdapter adapter;
	
	private FragmentManager fragmentManager;
	
	boolean task;
	boolean moreResults;
	
	private String lastTextQuery;
	private String lastLocationQuery;
	
	private int offset;
	private int limit;
	
	// code up to onDetach() used to maintain callbacks to the activity
	private TaskCallbacks taskCallbacks = dummyCallBacks;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("onAttach DisplayCartsFragment", "fragment is attached " + activity);
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException("Activity must implement TaskCallbacks");
		}
		
		if (!(activity instanceof LocationActivity)) {
			throw new IllegalStateException("Activity must implement LocationActivity");
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
		Resources resources = getResources();
		String fragmentTag = resources.getString(R.string.search_task_fragment_string);
		int fragmentId = resources.getInteger(R.integer.search_task_fragment);
		SearchTaskFragment fragment = (SearchTaskFragment) fragmentManager.findFragmentByTag(fragmentTag);
		
		if (fragment != null) {
			fragment.setTargetFragment(this, fragmentId);
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
		
		if (savedInstanceState == null) {
			arrayListItems = new ArrayList<ObjectCartListItem>();
		} else {
			arrayListItems = savedInstanceState.getParcelableArrayList("ObjectCartArrayListItems");
		}
		adapter = new CartListItemAdapter(R.layout.listview_cart_row, getActivity(), arrayListItems);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(displayCarts);
		displayCarts.setAdapter(swingBottomInAnimationAdapter);
		
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
			Parcelable[] parcelableItems = savedInstanceState.getParcelableArray("ObjectCartListItems");
			
			arrayListItems = savedInstanceState.getParcelableArrayList("ObjectCartArrayListItems");
			
			if (arrayListItems == null)
				return;
			// recreate the list
			adapter = new CartListItemAdapter(R.layout.listview_cart_row, getActivity(), arrayListItems);
			swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
			swingBottomInAnimationAdapter.setInitialDelayMillis(300);
			swingBottomInAnimationAdapter.setAbsListView(displayCarts);
			displayCarts.setAdapter(swingBottomInAnimationAdapter);
			
			// set the offset and limit again
			offset = savedInstanceState.getInt("offset");
			limit = savedInstanceState.getInt("limit");
			moreResults = savedInstanceState.getBoolean("moreResults");
			
			lastTextQuery = savedInstanceState.getString("lastTextQuery", "");
			lastLocationQuery = savedInstanceState.getString("lastLocationQuery", "");
		} else {
			arrayListItems = new ArrayList<ObjectCartListItem>();
			adapter = new CartListItemAdapter(R.layout.listview_cart_row, getActivity(), arrayListItems);
			swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
			swingBottomInAnimationAdapter.setInitialDelayMillis(300);
			swingBottomInAnimationAdapter.setAbsListView(displayCarts);
			displayCarts.setAdapter(swingBottomInAnimationAdapter);
			offset = 0;
			lastTextQuery = "";
			lastLocationQuery = "";
			limit = 20;
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("offset", offset);
		outState.putInt("limit", limit);
		outState.putString("lastTextQuery", lastTextQuery);
		outState.putString("lastLocationQuery", lastLocationQuery);
		outState.putParcelableArrayList("ObjectCartArrayListItems", arrayListItems);
		outState.putBoolean("moreResults", moreResults);

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == R.integer.search_task_fragment && resultCode == Activity.RESULT_OK) {
			taskCallbacks.onTaskFinished();
		}
	}
	
	public void buildList(ObjectCartListItem[] items) {
		
		if (items == null) {
			Toast.makeText(getActivity(), "no results", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// no more results
		if (items.length < limit) {
			moreResults = false;
		}
		
		if (displayCarts != null) {
			task = false;
			arrayListItems.addAll(Arrays.asList(items));
			swingBottomInAnimationAdapter.notifyDataSetChanged();
			adapter.notifyDataSetChanged();
			displayCarts.setOnScrollListener(this);
			displayCarts.invalidate();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), ViewCartActivity.class);
		intent.putExtra("ObjectCartListItem", arrayListItems.get(position));
		
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_viewMap && getActivity() != null) {
			Intent intent = new Intent(getActivity(), MarkerActivity.class);
			intent.putExtra("ObjectCartListItems", arrayListItems);
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
		SearchTask searchTask = new SearchTask(getActivity().getApplicationContext());
		
		searchTask.put("tq", lastTextQuery);
		searchTask.put("lq", lastLocationQuery);
		
		searchTask.put("offset", "" + offset);
		searchTask.put("limit", "" + limit);
		
		getData(searchTask);
	}
	
	@Override
	public void search(String textQueryData, String locationQueryData) {
		if (getActivity() != null) {
			SearchTask searchTask = new SearchTask(getActivity().getApplicationContext());
			// put in tq, lq, email, auth_token,
			
			boolean useLocation = locationQueryData.equals("Current Location");
			if (!useLocation && lastTextQuery.equals(textQueryData) && lastLocationQuery.equals(locationQueryData)) {
				Toast.makeText(getActivity(), "Same Search Input", Toast.LENGTH_LONG).show();;
				hide();
				return;
			}
			
			lastTextQuery = textQueryData;
			lastLocationQuery = locationQueryData;


			if (useLocation) {
				Location location = ((LocationActivity) getActivity()).getLastLocation();
				
				if (location == null) {
					Toast.makeText(getActivity(), "Location Services Are Off. Please Turn Them On.", Toast.LENGTH_SHORT).show();
					return;
				} else {
					String lat = location.getLatitude() + "";
					String lon = location.getLongitude() + "";
					locationQueryData = lat + "," + lon;
					lastLocationQuery = lat + "," + lon;
				}
			}
			offset = 0;
			moreResults = true;
			arrayListItems.clear();
			adapter.notifyDataSetChanged();
			swingBottomInAnimationAdapter.notifyDataSetChanged();
			
			
			searchTask.put("tq", textQueryData);
			searchTask.put("lq", locationQueryData);
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
		
		Resources resources = getResources();
		String fragmentTag = resources.getString(R.string.search_task_fragment_string);
		int fragmentId = resources.getInteger(R.integer.search_task_fragment);
		taskFragment.setTargetFragment(this, fragmentId);
		taskFragment.show(getFragmentManager(), fragmentTag);
		taskFragment.execute();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
        loadMore = loadMore && arrayListItems != null && arrayListItems.size() >= 20;
        if(loadMore && !task && moreResults) {
        	task = true;
        	offset += 20;
            getMoreCarts();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}


	public void hide() {
		SearchView view = (SearchView) getActivity().findViewById(R.id.searchView);
		view.hide();
	}

	public void show() {
		SearchView view = (SearchView) getActivity().findViewById(R.id.searchView);
		view.show();
	}
}