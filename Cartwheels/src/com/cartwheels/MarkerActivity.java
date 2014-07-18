package com.cartwheels;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerActivity extends Activity {

    private GoogleMap mMap;
    private ArrayList<ObjectCartListItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        ArrayList<ObjectCartListItem> parcelableItems = intent.getParcelableArrayListExtra("ObjectCartListItems");
        items = new ArrayList<ObjectCartListItem>(parcelableItems);
        setContentView(R.layout.map_marker);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        addMarkersToMap(items);
        
        mMap.setMyLocationEnabled(true);
        
        final View mapView = getFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    
                    for (ObjectCartListItem item : items) {
                    	LatLng position = new LatLng(item.getLat(), item.getLon());
                    	builder.include(position);
                    }
                    
                    LatLngBounds bounds = builder.build();
                      mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }
    
    private void addMarkersToMap(ArrayList<ObjectCartListItem> items) {
    	
    	if (items != null && items.size() != 0) {
	    	for (int i = 0; i < items.size(); i++) {
	    		ObjectCartListItem item = items.get(i);
	    		
	    		LatLng position = new LatLng(item.getLat(), item.getLon());
	    		
	    		mMap.addMarker(new MarkerOptions().position(position).title(item.cartName));
	    	}
    	}
    }
}
