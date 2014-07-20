package com.cartwheels;

import com.cartwheels.tasks.DefaultGetJsonAsyncTask;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class DisplayOwnedCartsActivity extends LocationActivity
											implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	handleIntent(intent);
        }
    };
    
    private SharedPreferences preferences;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    
    private void handleIntent(Intent intent) {

    	Fragment fragment = getFragmentManager().findFragmentById(R.id.displayOwnedCartsFragment);
    	// update location
    	if (fragment != null) {
    		
    		if (intent.getAction().equals("start.fragment.action")) {
    			fragment.onActivityResult(20, RESULT_OK, intent);
    		} else if (intent.getAction().equals("start.fragment.editCart")) {
    			fragment.onActivityResult(30, RESULT_OK, intent);
    		} else if (intent.getAction().equals("start.fragment.updateMenu")) {
    			fragment.onActivityResult(50, RESULT_OK, intent);
    		} else if (intent.getAction().equals("start.fragment.viewCart")) {
    			ObjectCartListItem item = intent.getParcelableExtra("ObjectCartListItem");
    			Intent data = new Intent(DisplayOwnedCartsActivity.this, ViewCartActivity.class);
    			data.putExtra("ObjectCartListItem", item);
    			startActivity(data);
    		}
    	}
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_owned_carts);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("start.fragment.action");
		filter.addAction("start.fragment.editCart");
		filter.addAction("start.fragment.updateMenu");
		filter.addAction("start.fragment.viewCart");
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
    	case 1:
    		Intent intent = new Intent(DisplayOwnedCartsActivity.this, MainActivity.class);
    		startActivity(intent);
    		break;
    	case 2:
    		Toast.makeText(this, "My Carts Page Already Selected", Toast.LENGTH_SHORT).show();
    		break;
    	case 3:
    		logout();
        }
	}
	
    private void logout() {

    	Intent intent = new Intent(DisplayOwnedCartsActivity.this, LoginActivity.class);
    	intent.putExtra("logout", true);
    	startActivity(intent);
    	
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		menu.clear();
		getMenuInflater().inflate(R.menu.owned_carts_fragment, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_refresh:
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
}
