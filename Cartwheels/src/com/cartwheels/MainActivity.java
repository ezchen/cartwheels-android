package com.cartwheels;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cartwheels.DisplayCartsFragment.TaskCallbacks;
import com.savagelook.android.UrlJsonAsyncTask;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        				TaskCallbacks {

	private static final String TASKS_URL = "http://cartwheels.us/mobile/sessions.json";
	private SharedPreferences preferences;
	
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

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
        		Intent intent = new Intent(this, SearchActivity.class);
        		String message = getString(R.string.title_section1);
        		intent.putExtra("example", message);
        		startActivity(intent);
        		break;
        	case 2:
        		startActivity(new Intent(this, PlaceHolderActivity.class));
        		break;
        	case 3:
        		startActivity(new Intent(this, PlaceHolderActivity.class));
        		break;
        	case 4:
        		startActivity(new Intent(this, PlaceHolderActivity.class));
        		break;
        	case 5:
        		startActivity(new Intent(this, PlaceHolderActivity.class));
        		break;
        	case 6:
        		startActivity(new Intent(this, PlaceHolderActivity.class));
        		break;
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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	
    	Log.d("onOptionsItemSelected", "yes");
        int id = item.getItemId();
        Log.d("menuItemNumber", "" + id);
        Log.d("action_logout", "" + R.id.action_logout);
        switch (id) {
        	case R.id.action_logout: 
        		logout();
        		Log.d("onOptionsItemSelected", "logout");
        		return true;
        	case R.id.action_settings:
        		return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {

    	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    	intent.putExtra("logout", true);
    	startActivity(intent);
    	
    }

    private class GetTasksTask extends UrlJsonAsyncTask {
        public GetTasksTask(Context context) {
            super(context);
        }

        /*
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonTasks = json.getJSONObject("data").getJSONArray("tasks");
                int length = jsonTasks.length();
                List<String> tasksTitles = new ArrayList<String>(length);

                for (int i = 0; i < length; i++) {
                    tasksTitles.add(jsonTasks.getJSONObject(i).getString("title"));
                }

                ListView tasksListView = (ListView) findViewById (R.id.tasks_list_view);
                if (tasksListView != null) {
                    tasksListView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                      android.R.layout.simple_list_item_1, tasksTitles));
                }
            } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                Toast.LENGTH_LONG).show();
            } finally {
            	super.onPostExecute(json);
            }
        }
        */
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
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
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
