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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cartwheels.custom_views.SearchView;
import com.cartwheels.custom_views.SearchView.SearchListener;
import com.savagelook.android.UrlJsonAsyncTask;

public class SearchActivity extends Activity
								implements SearchListener{
	
	private SharedPreferences preferences;
	
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
	private static final String TAGS_PHOTOS="photos";
	
	private DisplayCartsFragment fragment;
	
	private SearchView searchView;
	
	
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
		
		// access the view to set this as the searchListener
		searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setSearchListener(this);
		
		/*
		SearchTask searchTask = new SearchTask(SearchActivity.this);
		searchTask.execute("http://cartwheels.us/carts/data"); */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

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

	public void handleIntent(Intent intent) {
		if (intent.hasExtra("tq")) {
			String tq = intent.getStringExtra("tq");
			String lq = intent.getStringExtra("lq");

			search(tq, lq);
		}
	}
	
	@Override
	public void search(String textQueryData, String locationQueryData) {
		SearchTask searchTask = new SearchTask(this);
		searchTask.setTextQuery(textQueryData);
		searchTask.setLocationQuery(locationQueryData);
		
		Log.d("searchActivity", "search called: " + textQueryData + " " + locationQueryData);
		
		if (textQueryData.length() == 0 && locationQueryData.length() == 0) {
			Toast.makeText(this, "Please complete one of the search fields", Toast.LENGTH_SHORT);
		} else {
			searchTask.execute();
		}
	}
	
	private class SearchTask extends UrlJsonAsyncTask {
		
		private String textQuery;
		private String locationQuery;
		
		public SearchTask(Context context) {
			super(context);
		}
		
		public void setTextQuery(String textQuery) {
			this.textQuery = textQuery;
		}
		
		public void setLocationQuery(String locationQuery) {
			this.locationQuery = locationQuery;
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
				
				if (!(textQuery.length() == 0))
					uri.appendQueryParameter("tq", textQuery);
				if (!(locationQuery.length() == 0))
					uri.appendQueryParameter("lq", locationQuery);
				
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
	public static class DisplayCartsFragment extends Fragment
												implements OnItemClickListener {

		private ListView displayCarts;
		private ObjectCartListItem[] items;
		//ObjectCartListItem items[];
		
		public DisplayCartsFragment() {
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
		
		public void buildList(JSONArray jsonArray) {
			try {
				Log.d("length", jsonArray.length() + "");
				items = new ObjectCartListItem[jsonArray.length()];
				
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					
					String cartName = json.getString(TAGS_NAME);
					String cartZipcode = json.getString(TAGS_ZIP_CODE);
					String cartPermit = json.getString(TAGS_PERMIT_NUMBER);
					
					JSONArray arrayBitmapUrl = json.getJSONArray(TAGS_PHOTOS);
					
					String bitmapUrl = null;
					if (arrayBitmapUrl.length() > 0) {
						JSONObject jsonBitmapUrl = arrayBitmapUrl.getJSONObject(0);
						bitmapUrl = jsonBitmapUrl.getString("image_url_thumb");
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
				ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(getActivity(), R.layout.listview_cart_row, items);
				
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
	}


}
