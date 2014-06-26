package com.cartwheels;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

public class RetainFragment extends Fragment {

	private static final String TAG = "RetainFragment";
	public LruCache<String, Bitmap> retainedCache;
	
	public RetainFragment() {}
	
	public static final RetainFragment findOrCreateRetainFragment(FragmentManager fragmentManager,
												String tag) {
		RetainFragment fragment = (RetainFragment) fragmentManager.findFragmentByTag(tag);
		
		if (fragment == null) {
			fragment = new RetainFragment();
			fragmentManager.beginTransaction().add(fragment, tag).commit();
		}
		
		return fragment;
	}
	public static final RetainFragment findOrCreateRetainFragment(FragmentManager fragmentManager) {
		RetainFragment fragment = (RetainFragment) fragmentManager.findFragmentByTag(TAG);
		
		if (fragment == null) {
			fragment = new RetainFragment();
			fragmentManager.beginTransaction().add(fragment, TAG).commit();
		}
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
}
