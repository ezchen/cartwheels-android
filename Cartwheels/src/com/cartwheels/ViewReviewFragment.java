package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cartwheels.DisplayCartsFragment.TaskCallbacks;
import com.cartwheels.tasks.ReviewItem;
import com.cartwheels.tasks.ReviewTaskFragment;

public class ViewReviewFragment extends Fragment
										implements OnItemClickListener {

	private ListView reviews;
	private ReviewItem items;
	
	private FragmentManager fragmentManager;
	
	private int offset;
	private int limit;
	
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
		String fragmentTag = null;
		int fragmentId = 2;
		
		ReviewTaskFragment fragment = null;
		
		if (fragment != null) {
			fragment.setTargetFragment(this, fragmentId);
		} else {
			Log.d("fragment", "null");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		reviews = (ListView) inflater.inflate(R.layout.fragment_search, container, false);
		
		reviews.setOnItemClickListener(this);
		
		return reviews;
	}

	public static ViewReviewFragment newInstance(Bundle arguments) {
		ViewReviewFragment fragment = new ViewReviewFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
