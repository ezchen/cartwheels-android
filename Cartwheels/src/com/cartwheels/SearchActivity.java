package com.cartwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cartwheels.custom_views.SearchView;
import com.cartwheels.custom_views.SearchView.SearchListener;
import com.cartwheels.tasks.SearchTask;

public class SearchActivity extends Activity
								implements SearchListener{
	
	SharedPreferences preferences;
	
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
		SearchTask searchTask = new SearchTask(this, this);
		
		// put in tq, lq, email, auth_token,
		searchTask.put("tq", textQueryData);
		searchTask.put("lq", locationQueryData);
		
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		searchTask.put("email", email);
		searchTask.put("auth_token", auth_token);
		
		Log.d("searchActivity", "search called: " + textQueryData + " " + locationQueryData);
		
		if (textQueryData.length() == 0 && locationQueryData.length() == 0) {
			Toast.makeText(this, "Please complete one of the search fields", Toast.LENGTH_SHORT).show();
		} else {
			searchTask.execute();
		}
	}
	
	public DisplayCartsFragment getFragment() {
		return this.fragment;
	}


}
