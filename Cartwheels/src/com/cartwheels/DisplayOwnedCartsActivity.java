package com.cartwheels;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

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
        actionBar.setTitle("Home");
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
}
