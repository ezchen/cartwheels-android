package com.cartwheels;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cartwheels.tasks.TaskFragment;


public class DisplayCartsFragment extends Fragment
											implements OnItemClickListener {

	private ListView displayCarts;
	private ObjectCartListItem[] items;
	
	private FragmentManager fragmentManager;
	public static final int TASK_FRAGMENT = 0;
	public static final String TASK_FRAGMENT_TAG = "displayCarts";
	
	// code up to onDetach() used to maintain callbacks to the activity
	private TaskCallbacks taskCallbacks = dummyCallBacks;
	
	public DisplayCartsFragment() {
	}


	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
		TaskFragment fragment = (TaskFragment) fragmentManager.findFragmentByTag(TASK_FRAGMENT_TAG);
		
		if (fragment != null) {
			fragment.setTargetFragment(this, TASK_FRAGMENT);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		displayCarts = (ListView) inflater.inflate(R.layout.fragment_search,
				container, false);
		
		displayCarts.setOnItemClickListener(this);
		
		Log.d("DisplayCartsFragment onCreateView", "fragment created");
		
		
		return displayCarts;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TASK_FRAGMENT && resultCode == Activity.RESULT_OK) {
			taskCallbacks.onTaskFinished();
		}
	}
	
	public void buildList(JSONArray jsonArray) {
		try {
			Log.d("length", jsonArray.length() + "");
			items = new ObjectCartListItem[jsonArray.length()];
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				
				String cartName = json.getString(getResources().getString(R.string.TAGS_NAME));
				String cartZipcode = json.getString(getResources().getString(R.string.TAGS_ZIP_CODE));
				String cartPermit = json.getString(getResources().getString(R.string.TAGS_PERMIT_NUMBER));
				
				JSONArray arrayBitmapUrl = json.getJSONArray(getResources().getString(R.string.TAGS_PHOTOS));
				
				String bitmapUrl = null;
				if (arrayBitmapUrl.length() > 0) {
					JSONObject jsonBitmapUrl = arrayBitmapUrl.getJSONObject(0);
					bitmapUrl = jsonBitmapUrl.getString(getResources().getString(R.string.TAGS_URL_THUMB));
				}
				
				ObjectCartListItem cartListItem = new ObjectCartListItem(bitmapUrl, cartName,
														cartZipcode, cartPermit);
				
				Log.d("cart list item", cartListItem.toString());
				items[i] = cartListItem;
			}
			
			Log.d("All cart list items", Arrays.toString(items));
			Log.d("BuildList", "objects added");
			
			if (getActivity() == null) {
				Log.d("getActivity", "null");
			}
			ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter((Activity) taskCallbacks, R.layout.listview_cart_row, items);
			
			displayCarts.setAdapter(adapter);
			
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			Log.e("NullPointerException", e.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.toString());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), ViewCartActivity.class);
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
}