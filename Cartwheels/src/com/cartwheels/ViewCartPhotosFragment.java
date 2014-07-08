package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.GridView;

public class ViewCartPhotosFragment extends Fragment {

	private GridView cartPhotos;
	private String[] imageUrls;
	
	private FragmentManager fragmentManager;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fragmentManager = getFragmentManager();
		
		Resources resources = getResources();
		String fragmentTag;
		int fragmentId;
		
		
	}
}
