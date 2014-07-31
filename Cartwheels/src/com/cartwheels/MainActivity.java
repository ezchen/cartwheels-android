package com.cartwheels;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cartwheels.DisplayCartsFragment.TaskCallbacks;
import com.cartwheels.custom_views.SearchView;
import com.cartwheels.tasks.SearchTask;
import com.cartwheels.tasks.SearchTaskFragment;

public class MainActivity extends LocationActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        				TaskCallbacks {

	private static final String TASKS_URL = "https://cartwheels.us/mobile/sessions.json";
	private SharedPreferences preferences;
	
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private SearchView searchView;
	
	DisplayCartsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        
        Intent intent = getIntent();
		handleIntent(intent);
		
		Resources resources = getResources();
		String fragmentTag = resources.getString(R.string.search_task_fragment_string);
		if (savedInstanceState != null) {
			fragment = (DisplayCartsFragment) getFragmentManager().getFragment(savedInstanceState, fragmentTag);
		} else {
			fragment = (DisplayCartsFragment) getFragmentManager().findFragmentById(R.id.display_carts_fragment);
		}
		
		preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		
		// access the view to set this as the searchListener
		searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setSearchListener(fragment);
		
		/*
		SearchTask searchTask = new SearchTask(SearchActivity.this);
		searchTask.execute("https://cartwheels.us/carts/data"); */
	}
    
    private void loadTasksFromAPI(String url) {
        //GetTasksTask getTasksTask = new GetTasksTask(MainActivity.this);
        //getTasksTask.setMessageLoading("Loading tasks...");
        //getTasksTask.execute(url);
    }
    
    @Override
    public void onResume() {
    	// make a super class with this method maybe?
    	super.onResume();
    	if (preferences.contains("AuthToken")) {
    		loadTasksFromAPI(TASKS_URL);
    	} else {
    		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    		startActivityForResult(intent, 0);
    	}
    	
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        /* Create the new activity */
        switch (position) {
        	case 1:
        		Toast.makeText(this, "Search Page Already Selected", Toast.LENGTH_SHORT).show();
        		break;
        	case 2:
        		if ("user".equals(preferences.getString("LoginType", "user"))) {
        			Toast.makeText(this, "Only owners can access this page", Toast.LENGTH_SHORT).show();
        			return;
        		}
        		startActivity(new Intent(this, DisplayOwnedCartsActivity.class));
        		break;
        	case 3:
        		logout();
        }
    }

    /*
    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                break;
            case 5:
            	mTitle = getString(R.string.title_section4);
            	break;
            case 6:
            	mTitle = getString(R.string.title_section5);
            	break;
            case 7:
            	mTitle = getString(R.string.title_section6);
            	break;
        }
    }
    */

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Home");
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
		} else if (id == R.id.action_viewMap) {
			// start the map activity w/ all cart data
			
			if (fragment != null) {
				Log.d("onOptionsItemSelected", "fragment handling selection");
				fragment.onOptionsItemSelected(item);
			} else {
				Intent intent = new Intent(MainActivity.this, MarkerActivity.class);
				startActivity(intent);
			}
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		Resources resources = getResources();
		String fragmentTag = resources.getString(R.string.search_task_fragment_string);
		getFragmentManager().putFragment(outState, fragmentTag, fragment);
	}
	
	//@Override
	public void search(String textQueryData, String locationQueryData) {
		SearchTask searchTask = new SearchTask(getApplicationContext());
		
		// put in tq, lq, email, auth_token,
		searchTask.put("tq", textQueryData);
		searchTask.put("lq", locationQueryData);
		
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		searchTask.put("email", email);
		searchTask.put("auth_token", auth_token);
		
		Log.d("searchActivity", "search called: " + textQueryData + " " + locationQueryData);
		Log.d("searchActivity search", "searchTask created");
		
		SearchTaskFragment taskFragment = new SearchTaskFragment();
		taskFragment.setTask(searchTask);
		
		Resources resources = getResources();
		int fragmentId = resources.getInteger(R.integer.search_task_fragment);
		taskFragment.setTargetFragment(fragment, fragmentId);
		taskFragment.show(getFragmentManager(), "displayCarts");
		taskFragment.execute();
		
		Log.d("searchActivity search", "TaskFragment SuccessfullyCreated");
		if (textQueryData.length() == 0 && locationQueryData.length() == 0) {
			Toast.makeText(this, "Please complete one of the search fields", Toast.LENGTH_SHORT).show();
		} else {
			
		}
	}
	
    private void logout() {

    	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    	intent.putExtra("logout", true);
    	startActivity(intent);
    	
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            /*
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));*/
        }
    }
	@Override
	public void onTaskFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgressUpdate(int percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostExecute() {
		// TODO Auto-generated method stub
		
	}

}
