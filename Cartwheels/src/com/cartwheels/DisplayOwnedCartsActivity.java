package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.cartwheels.tasks.DefaultPutJsonAsyncTask;
import com.cartwheels.tasks.DefaultTaskFragment;

public class DisplayOwnedCartsActivity extends LocationActivity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	handleIntent(intent);
        }
    };
    
    private void handleIntent(Intent intent) {

    	Fragment fragment = getFragmentManager().findFragmentById(R.id.displayOwnedCartsFragment);
    	
    	// update location
    	if (fragment != null) {
    		fragment.onActivityResult(20, RESULT_OK, intent);
    	}
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_owned_carts);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("start.fragment.action");
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
}
