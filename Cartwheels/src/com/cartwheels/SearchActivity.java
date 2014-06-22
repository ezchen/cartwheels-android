package com.cartwheels;

import java.util.Arrays;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.savagelook.android.UrlJsonAsyncTask;

public class SearchActivity extends Activity {
	
	SharedPreferences preferences;
	
	private static final String TAGS_DATA = "data";
	private static final String TAGS_ID = "id";
	private static final String TAGS_NAME = "name";
	private static final String TAGS_CITY = "city";
	private static final String TAGS_UPLOAD_ID="upload_id";
	private static final String TAGS_OWNER_SECRET="null";
	private static final String TAGS_PERMIT_NUMBER="permit_number";
	private static final String TAGS_ZIP_CODE="zip_code";
	private static final String TAGS_LAT="lat";
	private static final String TAGS_LON="lon";
	private static final String TAGS_CREATED_AT="created_at";
	private static final String TAGS_UPDATED_AT="updated_at";
	
	DisplayCartsFragment fragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		/* perform the search if the intent has a query */
		Intent intent = getIntent();
		handleIntent(intent);
		fragment = new DisplayCartsFragment();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
		}
		
		preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		
		SearchTask searchTask = new SearchTask(SearchActivity.this);
		searchTask.execute("http://cartwheels.us/carts/data");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		
		/* get the SearchView and set the searchable configuration */
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    
	    int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
	    TextView textView = (TextView) searchView.findViewById(id);
	    textView.setTextColor(Color.WHITE);
	    textView.setHintTextColor(Color.WHITE);
	    
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(true);
	    

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			search(query);
		}
	}
	
	public boolean search(String query) {
		return true;
	}
	
	private class SearchTask extends UrlJsonAsyncTask {
		public SearchTask(Context context) {
			super(context);
		}
		
		protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        
	        String response = null;
	        JSONObject json = new JSONObject();
			try {
				Builder uri = new Builder();
				uri.scheme("http").authority("cartwheels.us").appendPath("carts")
					.appendPath("search");
				
				// add parameters email, auth_token, offset, limit, tq, lq
				uri.appendQueryParameter("auth_token", preferences.getString("AuthToken", ""));
				uri.appendQueryParameter("email", preferences.getString("email", ""));
				
				// hard coded values for testing
				uri.appendQueryParameter("offset", "0");
				uri.appendQueryParameter("limit", "100");
				uri.appendQueryParameter("tq", "halal");
				
				HttpGet get = new HttpGet(uri.toString());
				// default return values
				json.put("success", false);
				json.put("info", "Logout Failed");
				
				get.setHeader("Accept", "application/json");
				get.setHeader("Content-Type", "application/json");
				
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = client.execute(get, responseHandler);
				json = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// Build List View
			try {
				JSONArray carts = json.getJSONArray(TAGS_DATA);
				Log.d("jsonarray", carts.toString());
				
				// test if json array is working
				fragment.buildList(carts);
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			super.onPostExecute(json);
		}
		
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class DisplayCartsFragment extends Fragment {

		private ListView displayCarts;
		
		public DisplayCartsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			displayCarts = (ListView) inflater.inflate(R.layout.fragment_search,
					container, false);
			
			
			return displayCarts;
		}
		
		public void buildList(JSONArray jsonArray) {
			String test;
			try {
				test = jsonArray.getJSONObject(0).getString(TAGS_CITY);
				
				Log.d("length", jsonArray.length() + "");
				ObjectCartListItem[] items = new ObjectCartListItem[10];
				
				for (int i = 0; i < 10; i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					
					String cartName = json.getString(TAGS_NAME);
					String cartZipcode = json.getString(TAGS_ZIP_CODE);
					String cartPermit = json.getString(TAGS_PERMIT_NUMBER);
					
					ObjectCartListItem cartListItem = new ObjectCartListItem(R.drawable.profile, cartName,
															cartZipcode, cartPermit);
					
					Log.d("cart list item", cartListItem.toString());
					items[i] = cartListItem;
				}
				

				Log.d("All cart list items", Arrays.toString(items));
				Log.d("BuildList", "objects added");
				ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(getActivity(), R.layout.listview_cart_row, items);
				
				Log.d("Adapter", "objects added " + adapter.toString());
				
				displayCarts.setAdapter(adapter);
				
				
			} catch (Exception e) {
				test = "nope";
				e.printStackTrace();
			}
			Log.d("carts", test);
		}
	}

}
